/*
@author Tatenda M. Chipeperekwa
@date 25/06/09
@description This class is used to index all the records
that are within the upper level database so that they can
be searched easily.
*/
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.index.IndexWriter;
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
	String configPath = "../config/";
	File indexDirectory = new File(configPath+"index");
  	
	DatabaseBrowser allRecords = new DatabaseBrowser();                   //new database object to retrieve all the records
        int total_records=0;								//store the total number of records

	//this section is used to determine the total
	//number of records in the database so that
	//they can all be indexed for the search.
	try
	    {	 
		 Statement stm = applicationSettings.getDatabaseConnection().createStatement();
		 
		 ResultSet total_data = stm.executeQuery ("Select count(*) from RecordDate");
		 total_data.next();								//retrieving the total
		 total_records=Integer.parseInt(total_data.getString("count(*)"));

	    }catch(SQLException sqle)
	    {
		sqle.printStackTrace();
	    }

	List<Record> recordsToIndex = allRecords.browse("Title","asc",1,total_records,applicationSettings.getDatabaseConnection());//calls the method that gets all the records
	System.out.println(recordsToIndex.size());


      //check if the index has been made already
      if (indexDirectory.exists()) {
      System.out.println("Cannot save index to directory, please delete it first");
      System.exit(1);
      }

    try {
   	
	IndexWriter writer = new IndexWriter(indexDirectory, new StandardAnalyzer(), true);
	indexDocs(writer, recordsToIndex,configPath);				//writing records to index
	writer.optimize();						//optimizing the index
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
         	writer.addDocument(doc_Objects.getDocument(myrecords.get(i),configPath));
        }
	}catch(Exception e){}
  }


}

