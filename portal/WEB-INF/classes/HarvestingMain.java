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
*@date   2010-07-25
*/

   import java.util.Iterator;
   import java.util.ArrayList;
   import java.util.List;
   import java.io.*;
   import java.net.HttpURLConnection;
   import java.net.ProtocolException;
   import java.net.URL;
   import java.net.URLConnection;
   import java.net.URLEncoder;
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
         applicationSettings.configureApplication(true);
      
         List<String> identifiers =  new ArrayList<String>();					//stores all the identifiers found during the harvesting  
         ResponseParser oaiParser = new ResponseParser();							//is used to parse the responses received from the central repository
         HarvestRequest oaiRequest = new HarvestRequest();						//contains methods that run OAI-PMH requests on the central repositiory
         DatabaseUpdater recordsUpdater = new DatabaseUpdater();				//updates the portal database
//         List<Record> records_to_update;												//used to store the records parsed sucessfully
      
      //creating the connection to the local database	
         applicationSettings.createDatabaseConnection();
      
      //creating the verb used to harvest the repositiory
         String ListIdentifiersVerb = "verb=ListIdentifiers&metadataPrefix=oai_dc&from="+applicationSettings.getLastHarvest();
         System.out.println("Request: "+applicationSettings.getRepositoryURL()+"?"+ListIdentifiersVerb);	
      
      //getting a list of setspecs and corresponding set names from the central repository
         List<String> setSpecList = new ArrayList<String>();
         List<String> setNameList = new ArrayList<String>();
         String ListSetsVerb = "verb=ListSets";
         System.out.println("Request: "+applicationSettings.getRepositoryURL()+"?"+ListSetsVerb);	
         InputStream ListSetsResponse = oaiRequest.ListSets(ListSetsVerb,applicationSettings.getRepositoryURL());
         if(ListSetsResponse!=null)//if there was a valid response for the request made	
         { 
            Document listSetsDocument = oaiParser.parseXmlFile(ListSetsResponse);
            Element listSetsElement = listSetsDocument.getDocumentElement();
            setSpecList = oaiParser.getList(listSetsElement,"setSpec");
            System.out.println("setSpecs "+setSpecList.size());
            setNameList = oaiParser.getList(listSetsElement,"setName");
            System.out.println("setNames "+setNameList.size());
         
         }
         else{System.out.println("Error obtain sets from central repository.");System.exit(0);}
      
      //creating a timestamp for the current batch of records updated
         String currentHarvestTime = applicationSettings.createLastHarvestDate();
         int harvestBatch = 0;
          
         do
         {//harvest at least once then check for a resumptionToken	
         
             //InputStream used to capture the ListIdentifiers response from the central repository
            InputStream ListIdentifiersResponse = oaiRequest.ListIdentifiers(ListIdentifiersVerb,applicationSettings.getRepositoryURL());
         
            if(ListIdentifiersResponse!=null)//if there was a valid response for the request made	
            { 
            //extracting the list of identifiers in the repository
               List<Record> records_to_update = oaiParser.getRecords(ListIdentifiersResponse,"ListIdentifiers");
            
               if(records_to_update.size()>0)//if the response yielded some records
               {
                //carrying out a ListMetadataFormats for each identifier and extracting the relevant formats
                  for(int i = 0;i<records_to_update.size();i++)
                  {	
                  //only records that are not deleted should be harvested
                     if(records_to_update.get(i).getStatus()==false)
                     {
                        InputStream ListMetadataFormatsResponse = null;
                        try {
                           String ListMetadataFormatsVerb = "verb=ListMetadataFormats&identifier="+URLEncoder.encode (records_to_update.get(i).getRepositoryIdentifier(), "UTF-8");
                           //InputStream used to capture the ListMetadataFormats response from the central repository
                           ListMetadataFormatsResponse = oaiRequest.ListMetadataFormats(ListMetadataFormatsVerb,applicationSettings.getRepositoryURL());
                        } catch (Exception e) {
                           System.out.println("URL encoding Error, mal-formed identifier\n");
                           e.printStackTrace();
                        } 
                     
                     //capturing all the metadataFormats that a record appears in
                        records_to_update.get(i).setMetadataFormats(oaiParser.getMetadataFormats(ListMetadataFormatsResponse));
                     
                     //extracting the record in the different metadata formats that it comes in				  
                        for(int y=0;y<records_to_update.get(i).getMetadataFormats().size();y++)
                        {
                           InputStream GetRecordResponse = null;
                           
                           try {
                              String GetRecordVerb = "verb=GetRecord&identifier="+URLEncoder.encode (records_to_update.get(i).getRepositoryIdentifier(), "UTF-8")+"&metadataPrefix="+records_to_update.get(i).getMetadataFormats().get(y);
                              //InputStream used to capture the GetRecord response from the central repository
                              GetRecordResponse = oaiRequest.GetRecord(GetRecordVerb,applicationSettings.getRepositoryURL());
                           } catch (Exception e) {
                              System.out.println("URL encoding Error, mal-formed identifier\n");
                              e.printStackTrace();
                           }
                        
                        
                        	      
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
                                 {	
                                    records_to_update.get(i).getMetadataRecords().add(d);
                                 }
                              }
                           }
                               catch(Exception e)
                              {
                                 System.out.println("Parsing Error, mal-formed record\n");
                                 e.printStackTrace();
                              }
                        
                        }
                     
                        //setting the affiliation of the record 	
                        if(records_to_update.get(i).getMetadataRecords().size()>0)
                        {
                           Element docEle = records_to_update.get(i).getMetadataRecords().get(0).getDocumentElement();
                           String setSpec = oaiParser.getTagValue(docEle,"setSpec");
                           int setNameIndex = setSpecList.indexOf(setSpec);
                        
                           if(setNameIndex>-1)
                              records_to_update.get(i).setAffiliation(setNameList.get(setNameIndex));
                        }	
                        recordsUpdater.insertRecord(records_to_update.get(i),applicationSettings.getDatabaseConnection(),currentHarvestTime.replace("T"," ").replace("Z",""),applicationSettings.getStopWords());
                     } 
                  }
               }
               
               // indexing the database
               try{
                  System.out.println("Indexing records...");
                  IndexFiles newIndex = new IndexFiles();
                  if (harvestBatch == 20)
                  {
                     newIndex.createIndexIncrementOptimal (applicationSettings, records_to_update);
                     harvestBatch = 1;
                  }   
                  else
                  { 
                     newIndex.createIndexIncrement (applicationSettings, records_to_update);
                     harvestBatch++;
                  }   
                  System.out.println("Indexing records complete.");
               } catch (Exception e) {
                  System.out.println("Indexing Error, mal-formed record\n\n\n");
                  e.printStackTrace();
               }
                
               System.out.println("Resumption Token: "+oaiParser.getTokenValue());
            }
            else{System.out.println("Failed to make connection to central repository");}
         
            if(!oaiParser.getTokenValue().equals(""))//if there is a valid resumptionToken,set the verb again
            {
	       try {
                  ListIdentifiersVerb = "verb=ListIdentifiers"+"&resumptionToken=" + oaiParser.getTokenValue();
                  ListIdentifiersVerb = "verb=ListIdentifiers"+"&resumptionToken=" + URLEncoder.encode (oaiParser.getTokenValue(), "UTF-8");
	       }
	       catch(Exception e) {
                  System.out.println("URL encoding Error, mal-formed identifier\n");
                  e.printStackTrace();
	       }
               System.out.println("Request: "+applicationSettings.getRepositoryURL()+"?"+ListIdentifiersVerb);	
            }
         
         }while(!oaiParser.getTokenValue().equals(""));
      
      
//         try{
         //indexing the database
//            System.out.println("Indexing records...");
//            IndexFiles newIndex = new IndexFiles();
//            newIndex.createIndex(applicationSettings);
//            System.out.println("Indexing records complete.");
//         
//         }
//             catch(Exception e)
//            {
//               System.out.println("Indexing Error, mal-formed record\n\n\n");
//               e.printStackTrace();
//            }
      
      //updating the configuration file after the harvesting procedure
         applicationSettings.setLastHarvest(currentHarvestTime);
         applicationSettings.updateConfigurationFile();
      
         try{
            applicationSettings.getDatabaseConnection().close();//closing connection to database
         
         }
             catch(SQLException sqle)
            {
               sqle.printStackTrace();
            }	
      
      
      
      }	
      
   }
