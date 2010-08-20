/**
*The DatabaseBrowser class is used to run MYSQL queries
*on the Portal Database.
*
*@author Tatenda M. Chipeperekwa
*@date 17/12/09
*
*/
   import java.io.IOException;
   import java.util.ArrayList;
   import java.util.Iterator;
   import java.util.List;
   import java.net.URL;
   import java.sql.*;
   import java.io.*;





    public class DatabaseBrowser {
      

      private int total_records;	 //total number of records in the database
      private int offset;	        //base integer for the browse query
      private int lastDisplayed;	 //number of the last record displayed
      
      /**
	*Default Constructor
	*/
       public DatabaseBrowser()
      { 
		total_records=0;	
		offset=0;	       
      		lastDisplayed=0;	

      }

      //Mutator methods for the class instance variables

      public void setTotalRecords(int total)
      {
	total_records=total;
      }

      public void setOffset(int param)
      {
	offset=param;
      }

      public void setLastDisplayed(int param)
      {
	lastDisplayed=param;
      }
  
      //Accessor methods for the class instance variables

      public int getTotalRecords()
      {
	return total_records;
      }
     
      public int getOffset()
      {
	return offset;
      }

      public int getLastDisplayed()
      {
	return lastDisplayed;
      }

/**
*Parses parameters for queries on the Portal Database for the metadata records.
*<p>
*Proceeds to call the appropriate methods that will return the correct set of 
*results per set of parameters.
*@param criteria set to "Title" to query the records based on the title,or
*		   set to "Date" to query the records based on date.
*@param order set to "asc" for ascending,or "desc" for descing order.
*@param offset position of the first record in the query.
*@param limit maximum number of records returned per query.
*@param con connection to the Portal Database as created by the ConfigurationManager.
*@return a list of records that are representative of the parameters sent to this method.
*/
       public List<Record> browse (String criteria,String order,int offset,int limit,Connection con)
      { 
	    			
	Statement stm = null; 
	
	int browse_limit=limit;								         //stores the limit of the browse		
	setOffset(offset);				      				                //set the base index to browse from
	
	    try
	    {	 Connection date_con = con;
//	         if (date_con==null)
//	            return new ArrayList<Record>();
	    	
		 stm = date_con.createStatement();
		 int total_records=0;								  //store the total number of records
		 ResultSet total_data = stm.executeQuery ("Select count(*) from RecordDate");
		 total_data.next();								         //retrieving the total
		 total_records=Integer.parseInt(total_data.getString("count(*)"));
		 setTotalRecords(total_records);						         //setting the total records
		  
	    }catch(SQLException sqle)
	    {
		sqle.printStackTrace();
	    }
	

	//sets the index of the last record displayed
	//depending on the offset and browse_limit
	if(getOffset()+browse_limit>getTotalRecords())
	{
	      setLastDisplayed(getTotalRecords());
	}
	else
	{
	      setLastDisplayed(getOffset()+browse_limit-1);
	}

	
         try 
	 { 	 
		//the query is a browse by "Title"
		if(criteria.equals("Title"))
		{
		      return browseTitle(order,con,limit);//browsing by title

		}
		//the query is a browse by "Date"
		if(criteria.equals("Date"))
		{
		      return browseDate(order,con,limit);//browsing by date
		}
		
          }catch(Exception e)
	    {//exception caught in the private methods,but this is for any other errors
		e.printStackTrace();
	    }
	  return new ArrayList<Record>();
      }

/**
*Queries the database for a record specified by its unique
*repository identifier.
*@param con a Connection object that is used to access the Portal Database.
*@param repositoryID unique repository identifier for record to be viewed.
*@return the record that was requested for viewing.
*
*/
      public Record viewRecord (Connection con,String repositoryID)
      {
	    Statement stm = null;
	    ResultSet query_etd = null;
	    Record record_found =new Record();
	  
	  try
	  {			
		      stm = con.createStatement();
		      
		       query_etd = stm.executeQuery ("Select distinct description_etd,identifier_etd from RecordXML where identifier_etd ='"+repositoryID+"'");

		      if(query_etd.next())					//accessing the results returned
		      {	    record_found.setRepositoryIdentifier(query_etd.getString ("identifier_etd"));//setting the identifier
			    record_found.setPortalXML(query_etd.getString ("description_etd"));//setting the PortalXML;  
		      }

	    }catch(SQLException sqle)
	    {
		sqle.printStackTrace();
	    }
	    
	    return record_found;
	  
      }

/**
*Queries the database for records by "Title",ordered either
*ascending or descending.
*@param order set to "asc" for ascending,or "desc" for descing order.
*@param limit maximum number of records returned per query.
*@param con connection to the Portal Database as created by the ConfigurationManager.
*@return a list of Record objects with the records to be browsed .
*/
      private List<Record> browseTitle(String order,Connection con,int limit)
      {
	   if(limit>getTotalRecords())	//limit must be less than total number of records
	   {
	      limit=getTotalRecords();
	   }else if(limit<0)		//no negative limits are allowed
	   {
	      limit = 10;
	   } 
    
	    List<Record> allRecords = new ArrayList<Record>();
	    Statement stm = null;
	    ResultSet query_etd = null;
	  try
	  {			
		      stm = con.createStatement();
		      
                      if(order.equals("asc")||order.equals("desc"))
		      {
		         query_etd = stm.executeQuery ("Select distinct title_etd,identifier_etd from RecordTitle order by title_etd "+ order +" Limit "+limit+" offset "+(getOffset()-1));
		      }
		      else
		      {  //for an error in the ordering information
		   	 //the default is set to descending order
		         query_etd = stm.executeQuery ("Select distinct title_etd,identifier_etd from RecordTitle order by title_etd asc Limit "+limit+" offset "+(getOffset()-1));
		      }

		      Record record_found = null;			        //tempory record store
	    
		      while(query_etd.next())					//accessing the results returned
		      {	   record_found = new Record();	
			   record_found.setRepositoryIdentifier(query_etd.getString ("identifier_etd"));//setting the identifier

			    stm = con.createStatement();
			    //Searching for the date of the document
			    ResultSet xml_etd = stm.executeQuery ("Select description_etd from RecordXML where identifier_etd=\""+record_found.getRepositoryIdentifier()+"\"");
			    xml_etd.next();//accessing the description returned
			    record_found.setPortalXML(xml_etd.getString ("description_etd"));//setting the format of the record
			    
			     allRecords.add(record_found);				//adding the record
		      }
		      
  	    }catch(SQLException sqle)
	    {
		sqle.printStackTrace();
	    }

	    return allRecords;
	  
      } 

/**
*Queries the database for records by "Date",ordered either
*ascending or descending.
*@param order set to "asc" for ascending,or "desc" for descing order.
*@param limit maximum number of records returned per query.
*@param con connection to the Portal Database as created by the ConfigurationManager.
*@return a list of Record objects with the records to be browsed .
*/
      private List<Record> browseDate(String order,Connection con,int limit)
      {
	   if(limit>getTotalRecords())	//limit must be less than total number of records
	   {
	      limit=getTotalRecords();
	   }else if(limit<0)		//no negative limits are allowed
	   {
	      limit = 10;
	   } 

	    List<Record> allRecords = new ArrayList<Record>();
	    Statement stm = null;
	    ResultSet query_etd = null;
	  try
	  {			
		      stm = con.createStatement();
		      

		      if(order.equals("asc")||order.equals("desc"))
		      {
			  query_etd = stm.executeQuery ("Select distinct date_etd,identifier_etd from RecordDate order by date_etd "+ order + " Limit "+limit+" offset "+(getOffset()-1));
		      }else//for an error in the ordering information
		      {	   //the default is set to ascending order
	  
			  query_etd = stm.executeQuery ("Select distinct date_etd,identifier_etd from RecordDate order by date_etd asc Limit "+limit+" offset "+(getOffset()-1));
		      }
  
		      Record record_found = null;			        //tempory record store
	    
		      while(query_etd.next())					//accessing the results returned
		      {	    record_found = new Record();
			    record_found.setDate(query_etd.getString ("date_etd"));//setting the date of the record
			    record_found.setRepositoryIdentifier(query_etd.getString ("identifier_etd"));//setting the identifier 	

			    stm = con.createStatement();
			    //Searching for the date of the document
			    ResultSet xml_etd = stm.executeQuery ("Select description_etd from RecordXML where identifier_etd=\""+record_found.getRepositoryIdentifier()+"\"");
			    xml_etd.next();//accessing the description returned
			    record_found.setPortalXML(xml_etd.getString ("description_etd"));//setting the format of the record
			    
         		    allRecords.add(record_found);				//adding the record
		      }

	    }catch(SQLException sqle)
	    {
		sqle.printStackTrace();
	    }

	    return allRecords;
	  
      }

      	      
}
