/**
*The HarvestingMain class is used to run
*the application that will harvest data
*from the local repositiory and place it
*in a database in tables that will make it
*easier for users to browse the data in the
*online application interface.
*<p> 
*This class also instantiates the indexing of the 
*for searching purposes.
*@author Tatenda M. Chipeperekwa
*@date   21/12/09
*
*/

   import java.util.Iterator;
   import java.util.ArrayList;
   import java.util.List;

   import java.io.*;
   import java.net.HttpURLConnection;
   import java.net.ProtocolException;
   import java.net.URL;
   import java.net.URLConnection;
   import java.util.Scanner;
   import java.sql.*;
   //cut out imports after testing
   import javax.xml.parsers.ParserConfigurationException;
   import org.w3c.dom.Document;
   import org.w3c.dom.Element;
   import org.w3c.dom.NodeList;
   import org.xml.sax.SAXException;
   import org.xml.sax.SAXException;
   import java.io.*;
   import javax.xml.transform.Source;
   import javax.xml.transform.stream.StreamSource;
   import javax.xml.validation.*;
   import org.xml.sax.SAXException;
   import org.xml.sax.*;

   import javax.xml.parsers.*;
   import javax.xml.transform.*;
   import javax.xml.transform.dom.*;
   import javax.xml.transform.stream.*;
   import java.util.Properties;
   
    public class HarvestingMain
   {
	
       public static void main(String[]args)
      {

 
	 (new HarvestingMain()).run(args);	
      	
      
      }

      /**
      *This method is used by the HarvestingMain class to set up
      *the ConfigurationManager class that is used to extract 
      *configuration information, and as well as to carry out the
      *harvesting and updating of the database.
      *	@param args arguments used to run the Harvester.
      */
      public void run(String [] args)
      {
         ConfigurationManager applicationSettings = new ConfigurationManager();
	  	
	  //stores the file name passed to the program		
	  String configFileName="";			

	  if(args.length!=1)
	  {
		System.out.println("Error Condition.\nUsage : HarvestingMain <config file name>\n");
		System.exit(0);
	  }else
	  {
		configFileName=args[0];
		applicationSettings.configureApplication(configFileName,true);
	  }	

	  List<String> identifiers =  new ArrayList<String>();//stores all the identifiers found during the harvesting  
	  ResponseParser oaiParser = new ResponseParser();//is used to parse the responses received from the central repository
	  HarvestRequest oaiRequest = new HarvestRequest();//contains methods that run OAI-PMH requests on the central repositiory
	  DatabaseUpdater recordsUpdater = new DatabaseUpdater();//updates the portal database
	  List<Record> records_to_update;//used to store the records parsed sucessfully
	  
	  //creating the connection to the local database	
	  applicationSettings.createDatabaseConnection();

	  //clearing the database in the case of batch updating of records
	  //recordsUpdater.clearDatabase(applicationSettings.getDatabaseConnection());

	  //creating the verb used to harvest the repositiory
	  String ListIdentifiersVerb = "verb=ListIdentifiers&metadataPrefix=oai_dc&from="+applicationSettings.getLastHarvest();
	  //String ListIdentifiersVerb = "verb=ListIdentifiers&metadataPrefix=oai_dc&from=2009-01-01";
	  //String ListIdentifiersVerb = "verb=ListIdentifiers&metadataPrefix=oai_dc";
	  System.out.println("Request: "+applicationSettings.getRepositoryURL()+"?"+ListIdentifiersVerb);	
    	
	  do{//harvest at least once then check for a resumptionToken	

			//InputStream used to capture the ListIdentifiers response from the central repository
		       InputStream ListIdentifiersResponse = oaiRequest.ListIdentifiers(ListIdentifiersVerb,applicationSettings.getRepositoryURL());
			
			if(ListIdentifiersResponse!=null)//if there was a valid response for the request made	
			{ 
				//extracting the list of identifiers in the repository
				records_to_update = oaiParser.getRecords(ListIdentifiersResponse,"ListIdentifiers");
				
				if(records_to_update.size()>0)//if the response yielded some records
				{
				    //carrying out a ListMetadataFormats for each identifier and extracting the relevant formats
				    for(int i = 0;i<records_to_update.size();i++)
				    {	
					//only records that are not deleted should be harvested
					if(records_to_update.get(i).getStatus()==false)
					{
					    String ListMetadataFormatsVerb = "verb=ListMetadataFormats&identifier="+records_to_update.get(i).getRepositoryIdentifier();
					    
					    //InputStream used to capture the ListMetadataFormats response from the central repository
					    InputStream ListMetadataFormatsResponse = oaiRequest.ListMetadataFormats(ListMetadataFormatsVerb,applicationSettings.getRepositoryURL());
					    
					    //capturing all the metadataFormats that a record appears in
					    records_to_update.get(i).setMetadataFormats(oaiParser.getMetadataFormats(ListMetadataFormatsResponse));
				    
					    //extracting the record in the different metadata formats that it comes in				  
					    for(int y=0;y<records_to_update.get(i).getMetadataFormats().size();y++)
					    {
					      String GetRecordVerb = "verb=GetRecord&identifier="+records_to_update.get(i).getRepositoryIdentifier()+"&metadataPrefix="+records_to_update.get(i).getMetadataFormats().get(y);
					      //InputStream used to capture the GetRecord response from the central repository
					      InputStream GetRecordResponse = oaiRequest.GetRecord(GetRecordVerb,applicationSettings.getRepositoryURL());
					      
					      //capturing all the full xml record in DOM form for metadataFormats that a record appears in
					      //Document d = oaiParser.getMetadataElement(GetRecordResponse);
					      // if (d != null) // check for no errors in parsing [hussein, 1feb2010]
					         //records_to_update.get(i).getMetadataRecords().add(d);

						   try{
							//capturing all the full xml record in DOM form for metadataFormats that a record appears in
						        Document d = oaiParser.getMetadataElement(GetRecordResponse);
							
							if(oaiParser.getParsingStatus()==true){

                        			         Element docEle = d.getDocumentElement();
								  
							 String check_value = "";
							 if(records_to_update.get(i).getMetadataFormats().get(y).equals("oai_dc"))
								check_value=oaiParser.getTagValue(docEle,"dc");
							 if(records_to_update.get(i).getMetadataFormats().get(y).equals("oai_marc"))
								check_value=oaiParser.getTagValue(docEle,"oai_marc");
							 if(records_to_update.get(i).getMetadataFormats().get(y).equals("oai_etdms"))
								check_value=oaiParser.getTagValue(docEle,"oai_etdms");
							  

							 if(!check_value.equals("Not Available")) 
							 {	records_to_update.get(i).getMetadataRecords().add(d);
							        //updating the database with the new record
								recordsUpdater.insertRecord(records_to_update.get(i),applicationSettings.getDatabaseConnection());
							  }
							}
						      }catch(Exception e)
						      {
							  System.out.println("Parsing Error, mal-formed record\n");
							  //e.printStackTrace();
						      }
				    
					    }
					    
					    //updating the database with the new record
					    //recordsUpdater.insertRecord(records_to_update.get(i),applicationSettings.getDatabaseConnection());
					    
					    
					    } //System.out.println(oaiParser.getStringFromDocument(records_to_update.get(i).getMetadataRecords().get(1)));
					    //System.exit(0);
				    }
				} //recordsUpdater.updateDatabase(records_to_update,applicationSettings.getDatabaseConnection());
				//System.out.println(records_to_update.size());
				//System.out.println("Resumption Token: "+oaiParser.getTokenValue());
			}
		 	
			if(!oaiParser.getTokenValue().equals(""))//if there is a valid resumptionToken,set the verb again
		 	{
				ListIdentifiersVerb = "verb=ListIdentifiers"+"&resumptionToken="+oaiParser.getTokenValue();
	                        System.out.println("Request: "+applicationSettings.getRepositoryURL()+"?"+ListIdentifiersVerb);	
		 	}
		 
	    }while(!oaiParser.getTokenValue().equals(""));

	  //updating the configuration file after the harvesting procedure
	  applicationSettings.updateConfigurationFile();
	  
	    try{
		//indexing the database
		IndexFiles newIndex = new IndexFiles();
		newIndex.createIndex(applicationSettings);
	  
	    }catch(Exception e)
	    {
		System.out.println("Indexing Error, mal-formed record\n\n\n");
		e.printStackTrace();
	    }


	    try{
       	     applicationSettings.getDatabaseConnection().close();//closing connection to database

	    }catch(SQLException sqle)
	    {
		sqle.printStackTrace();
	    }	



      }	
      
}