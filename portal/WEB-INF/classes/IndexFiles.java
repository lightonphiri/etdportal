/*
@author Tatenda M. Chipeperekwa
@date 2010-07-25
@description This class is used to index all the records
that are within the upper level database so that they can
be searched easily.
*/
   import org.apache.lucene.*;
   import org.apache.lucene.analysis.standard.StandardAnalyzer;
   import org.apache.lucene.analysis.StopAnalyzer;
   import org.apache.lucene.index.IndexWriter;
   import org.apache.lucene.index.Term;
   import java.io.File;
   import java.io.FileNotFoundException;
   import java.io.IOException;
   import java.util.Date;

   import java.util.ArrayList;
   import java.util.Iterator;
   import java.util.List;
   import java.net.URL;
   import java.sql.*;

    public class IndexFiles
   {
   
       public IndexFiles() {}
   
   
       public void createIndex(ConfigurationManager applicationSettings)
      {	
//         String configPath = applicationSettings.getIndexDirectory ();
//         File indexDirectory = new File(configPath+"/index");
         
         String configPath = "/etc/etdportal/portal/";
         File indexDirectory = new File(applicationSettings.getIndexDirectory ()+"/index");
         
      
         DatabaseBrowser allRecords = new DatabaseBrowser();                   //new database object to retrieve all the records
         int total_records=0;																	//store the total number of records
      
      //this section is used to determine the total
      //number of records in the database so that
      //they can all be indexed for the search.
         try
         {	 
            Statement stm = applicationSettings.getDatabaseConnection().createStatement();
         
            ResultSet total_data = stm.executeQuery ("Select count(*) from RecordDate");
            total_data.next();								//retrieving the total
            total_records=Integer.parseInt(total_data.getString("count(*)"));
         
         }
             catch(SQLException sqle)
            {
               sqle.printStackTrace();
            }
      
         List<Record> recordsToIndex = browse(applicationSettings.getDatabaseConnection(),applicationSettings.getLastHarvest());//calls the method that gets all the records
      
      
	      //check if the index has been made already
         if (indexDirectory.exists()) {
         //System.out.println("Cannot save index to directory, please delete it first");
         //System.exit(1);
         }
      
         try {
         
            IndexWriter writer = new IndexWriter(indexDirectory, new StandardAnalyzer());
            indexDocs(writer, recordsToIndex,configPath);				//writing records to index
            writer.optimize();													//optimizing the index
            writer.close();
         
         } 
             catch (IOException e) {
               System.out.println(" caught a " + e.getClass() +
                  "\n with message: " + e.getMessage());
            }	
      
      } 

       public void createIndexIncrement ( ConfigurationManager applicationSettings, List<Record> recordsToIndex )
      {	
         String configPath = "/etc/etdportal/portal/";
         File indexDirectory = new File(applicationSettings.getIndexDirectory ()+"/index");
               
         try {         
            IndexWriter writer = new IndexWriter(indexDirectory, new StandardAnalyzer());
            indexDocs(writer, recordsToIndex,configPath);				//writing records to index
            //writer.optimize();								//optimizing the index
            writer.close();
         } catch (IOException e) {
            System.out.println(" caught a " + e.getClass() +
            "\n with message: " + e.getMessage());
         }	
      }

       public void createIndexIncrementOptimal ( ConfigurationManager applicationSettings, List<Record> recordsToIndex )
      {	
         String configPath = "/etc/etdportal/portal/";
         File indexDirectory = new File(applicationSettings.getIndexDirectory ()+"/index");
               
         try {         
            IndexWriter writer = new IndexWriter(indexDirectory, new StandardAnalyzer());
            indexDocs(writer, recordsToIndex,configPath);				//writing records to index
            writer.optimize();								//optimizing the index
            writer.close();
         } catch (IOException e) {
            System.out.println(" caught a " + e.getClass() +
            "\n with message: " + e.getMessage());
         }	
      }
      
      
   
   /**
   *Writes all the Lucene Documents,that are created using the getDocument()
   *method, to the index. 	
   *@param writer IndexWriter that updates the Lucene Index.
   *@param myrecords the List of records to be written to the Luncene Index.
   *@param configPath application path used to locate the directory containing
   *			the XSLT that transforms the xml into indexable fields.
   */
       static void indexDocs(IndexWriter writer,List<Record> myrecords,String configPath)
      {
         try{
            FileDocument doc_Objects = new FileDocument();
            for(int i=0;i<myrecords.size();i++)
            {	
               Term older = new Term("repository_identifier_updater",myrecords.get(i).getRepositoryIdentifier());
            //the updating of the index occurs here, the old document is replaced in the index 
            //System.out.println (myrecords.get(i));
               writer.updateDocument(older,doc_Objects.getDocument(myrecords.get(i),configPath));
            
            }
         }
             catch(Exception e){}
      }
   
   
   
   
   /**
   *harvests the portal database for records that need to be updated in the search index.
   */
       private List<Record> browse(Connection con, String lastHarvestDate)
      {
      
      
         List<Record> allRecords = new ArrayList<Record>();
         Statement stm = null;
         ResultSet query_etd = null;
         try
         {			
            stm = con.createStatement();
            
            query_etd = stm.executeQuery ("Select distinct identifier_etd from RecordLastHarvestDate where harvest_etd between '"+lastHarvestDate.replace("T"," ").replace("Z","")+"' and CURRENT_TIMESTAMP");
         
            Record record_found = null;			        //tempory record store
         
            while(query_etd.next())					//accessing the results returned
            {	   record_found = new Record();	
               record_found.setRepositoryIdentifier(query_etd.getString ("identifier_etd"));//setting the identifier
            
               stm = con.createStatement();
             //Searching for the date of the document
               ResultSet xml_etd = stm.executeQuery ("Select description_etd from RecordXML where identifier_etd=\""+record_found.getRepositoryIdentifier()+"\"");
               xml_etd.next();//accessing the description returned
               record_found.setPortalXML(xml_etd.getString ("description_etd"));
            
             //retrieving the affiliation
               ResultSet affiliation_etd = stm.executeQuery ("Select affiliation_etd from RecordAffiliation where identifier_etd=\""+record_found.getRepositoryIdentifier()+"\"");	
               affiliation_etd.next();//accessing the description returned
               record_found.setAffiliation(affiliation_etd.getString ("affiliation_etd"));
             
              //sanitizing record
               record_found.setPortalXML(record_found.getPortalXML().replaceAll("&lt;.*?&gt;",""));
               allRecords.add(record_found);				//adding the record
            }
            
         }
             catch(SQLException sqle)
            {
               sqle.printStackTrace();
            }
      
         return allRecords;
      
      }
   }

