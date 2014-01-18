import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.regex.*;

/**
 * Date: 9 April 2009
 * @author Lawrence Webley
 * Connects to the sql database and performs various queries.
 */
public class DatabaseConnection 
{    
    /* Connection to the sql server used by all methods in this class*/
    private Connection con;
    /* An array of the format specifications this database supports. We search here
     * for schema and namespace information if its needed in a request*/
    private MetadataFormat [] formatSpecList;

    private String url, username, password;
    
    /*Constructor class. Establishes a connection to the sql server and receives the formatList*/
    public DatabaseConnection(String address, String username, String password, MetadataFormat [] formatList, String databaseDriver ) 
     throws ClassNotFoundException, SQLException
    {
        formatSpecList = formatList;
        Class.forName (databaseDriver);
        url = address;
        this.username = username;
        this.password = password;
        con = DriverManager.getConnection (url, username, password);
    }

    /*
     * A connect method to reconnect to the db when a new request is made
     */
    public void connect() throws SQLException
    {
        con = DriverManager.getConnection (url, username, password);  
    }
    
   public String quote ( String data )
   {
      String s = data.replace ("\\", "\\\\");
      s = s.replace ("'", "\\'");
      return "'"+s+"'";
   }   

    public StringBuffer getRecord(String ID, String metaType) throws SQLException
    {
        connect();
        //create statement and query the database
        Statement stm = con.createStatement();
        ResultSet rs = stm.executeQuery("SELECT * FROM Archive WHERE ID = "+ quote (ID)+";");
        
        //boolean variables to keep track of whether the record with the ID exists,
        // or has been found but doesnt match the dissemination type specified
        boolean recFound = false;
        boolean recExists = false;
        
        //create our string buffer to store the response
        StringBuffer sqlResponse = new StringBuffer();
        
        //create our date formatting tools and set the timezone to UTC time.
        SimpleDateFormat UTCDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        UTCDateFormatter.setTimeZone(tz);
        
        //for all records with matching ID
        while(rs.next())
        {
           recExists = true;
           //if it matches required metadata type
           if(metaType.equals(rs.getString("MetaType")))
           {
               recFound = true;
               sqlResponse.append("<GetRecord>\n");
               sqlResponse.append("<record>\n");
               sqlResponse.append("<header");
               //check to see if its a deleted record
               if(rs.getBoolean("Deleted"))
               {
                   sqlResponse.append(" status=\"deleted\">\n");
                   sqlResponse.append("<identifier>"+rs.getString("ID"));
                   sqlResponse.append("</identifier>\n");
                   sqlResponse.append("<datestamp>");
                   sqlResponse.append(UTCDateFormatter.format(rs.getTimestamp("Date")));
                   sqlResponse.append("</datestamp>\n");
                    //sets
                    sqlResponse.append("<setSpec>");
                    sqlResponse.append(rs.getString("SetSpec"));
                    sqlResponse.append("</setSpec>\n");
                   sqlResponse.append("</header>\n");
               }
               else//if its not deleted print out metadata
               {
                   sqlResponse.append(">\n");
                   sqlResponse.append("<identifier>");
                   sqlResponse.append(rs.getString("ID"));
                   sqlResponse.append("</identifier>\n");
                   sqlResponse.append("<datestamp>");
                   sqlResponse.append(UTCDateFormatter.format(rs.getTimestamp("Date")));
                   sqlResponse.append("</datestamp>\n");
                    //sets
                    sqlResponse.append("<setSpec>");
                    sqlResponse.append(rs.getString("SetSpec"));
                    sqlResponse.append("</setSpec>\n");
                   sqlResponse.append("</header>\n");
                   sqlResponse.append("<metadata>\n");
                   //sqlResponse.append(rs.getString("MetaData"));
                   try {
                      sqlResponse.append(new String (rs.getBytes("MetaData"), "UTF-8"));
                   } catch ( Exception e ) {
                      e.printStackTrace();
                   }   
                   sqlResponse.append("\n");
                   sqlResponse.append("</metadata>\n");
                   //check for about section.
                   if(rs.getString("About")!= null && rs.getString("About").length() != 0)
                   {
                       sqlResponse.append("<about>\n");
                       try {
                          sqlResponse.append(new String (rs.getBytes ("About"), "UTF-8"));
                       } catch ( Exception e ) {
                          e.printStackTrace();
                       }    
                       sqlResponse.append("\n");
                       sqlResponse.append("</about>");
                   }
               }
                       
               sqlResponse.append("</record>\n");
               sqlResponse.append("</GetRecord>\n");
           }           
        }   
        if(!recExists)//if the record doesnt exist print this
        {
            sqlResponse.append("<error code=\"idDoesNotExist\">No matching identifier in database</error>\n");
        }
        else if (!recFound)//if it DOES exist, but doesnt match required metadata format:
        {
            sqlResponse.append("  <error code=\"cannotDisseminateFormat\"/>\n");
                       
        }
        rs.close();
        return sqlResponse;
    }
    
    //returns all the suppoted metadata formats on this server
    public StringBuffer getAllMetadataFormats() throws SQLException
    {
        connect();
        //create statement and query the database
        Statement stm = con.createStatement();
        ResultSet rs = stm.executeQuery("SELECT DISTINCT MetaType FROM Archive;");
        
        StringBuffer sqlResponse = new StringBuffer();
        //sqlResponse.append(" <ListMetadataFormats>");
        while(rs.next())
        {
            sqlResponse.append("<metadataFormat>");
            sqlResponse.append("<metadataPrefix>");
            sqlResponse.append(rs.getString("MetaType"));
            sqlResponse.append("</metadataPrefix>");
            sqlResponse.append("<schema>");
            
            //we use a variable to store the namespace so we only need to search the 
            //formatSpec list once in order to retrieve all the needed info
            String namespace = "";
            for(int i = 0; i < formatSpecList.length; i++)
            {
                if(rs.getString("MetaType").equals(formatSpecList[i].getPrefix()))
                {
                    sqlResponse.append(formatSpecList[i].getSchema());
                    namespace = formatSpecList[i].getNamespace();
                }
            }
            
            sqlResponse.append("</schema>");
            sqlResponse.append("<metadataNamespace>");
            sqlResponse.append(namespace);
            sqlResponse.append("</metadataNamespace>");
            sqlResponse.append("</metadataFormat>");
        }
        //sqlResponse.append(" </ListMetadataFormats>");
        rs.close();
        return sqlResponse;
    }
    
    //returns the supported metadata formats for a particular ID
    public StringBuffer getMetadataFormats(String ID) throws SQLException
    {
        connect();
        //create statement and execute sql query            
        Statement stm = con.createStatement();
        ResultSet rs = stm.executeQuery("SELECT MetaType FROM Archive WHERE ID="+quote (ID)+";");
        
        //check if any records with this MetaType exists
        boolean found = false;
        
        //create our respone string buffer
        StringBuffer sqlResponse = new StringBuffer();
        
        //start loop
        sqlResponse.append("<ListMetadataFormats>");
        while(rs.next())
        {           
            found = true;
            sqlResponse.append("<metadataFormat>");
            sqlResponse.append("<metadataPrefix>");
            sqlResponse.append(rs.getString("MetaType"));
            sqlResponse.append("</metadataPrefix>");
            sqlResponse.append("<schema>");
            
            //we use a variable to store the namespace so we only need to search the 
            //formatSpec list once in order to retrieve all the needed info
            String namespace = "";
            for(int i = 0; i < formatSpecList.length; i++)
            {
                if(rs.getString("MetaType").equals(formatSpecList[i].getPrefix()))
                {
                    sqlResponse.append(formatSpecList[i].getSchema());
                    namespace = formatSpecList[i].getNamespace();
                }
            }
            
            sqlResponse.append("</schema>");
            sqlResponse.append("<metadataNamespace>");
            sqlResponse.append(namespace);
            sqlResponse.append("</metadataNamespace>");
            sqlResponse.append("</metadataFormat>");
        }   
        sqlResponse.append("</ListMetadataFormats>");
        if(!found)
        {
            sqlResponse = new StringBuffer();
            sqlResponse.append("<error code=\"idDoesNotExist\" />");
        }
        rs.close();
        return sqlResponse;
    }
    
    //if a resumption token is received as a request, it comes here to continue the 
    //request
    public StringBuffer listRecords ( boolean headersOnly, String ResumptionToken ) throws SQLException
    {
        connect();
        //example of the token below
        //LR!2002-06-01T23:20:00Z!2004-08-03T21:20:00Z!oai_dc!100
        //or
        //LRS!2002-06-01T23:20:00Z!2004-08-03T21:20:00Z!UniversityOfCapeTown!oai_dc!100
        //format is:
        //type!fromDate!untilDate!metadataType!cursor
        //or
        //type!fromDate!untilDate!set!metadataType!cursor
        //delimiter is !     
        
        //Start by creating a scanner on the token and setting its delimeter to !
/*        Scanner resump = new Scanner(ResumptionToken);
        resump.useDelimiter("!");
        
        //check to see what type of request it is 
        //LR for ListRecords
        //LI for ListIdentifiers
        //LRS for ListRecords with set support
        //LIS for ListIdentifiers with set support
        boolean headersOnly;
        boolean setsEnabled;
        String type = resump.next();
        //Check if its identifiers or records
        if(type.equals("LR") || type.equals("LRS"))
        {
            headersOnly = false;
        }
        else
        {
            headersOnly = true;
        }
        //check if we are using sets or not
        if(type.equals("LRS") || type.equals("LIS"))
        {
            setsEnabled = true;
        }
        else
        {
            setsEnabled = false;
        }
        
        //determine to and from dates
        String from = resump.next();
        String until = resump.next();

        //if we are using sets, get which set we are looking for
        String sets = "";
        if(setsEnabled)
        {
             sets = resump.next();
        }

        //get metadata format
        String metadataFormat = resump.next();
        
        //get current cursor position
        int cursor = resump.nextInt();

        return listRecords(from, until, metadataFormat, headersOnly, sets, cursor);
*/                

      
      String from="", until="", metadataPrefix="oai_dc", set="", identifier="";
      int cursor = 0, totalRecords = -1;
      Pattern p = Pattern.compile ("([12][0-9]{3}-[01][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9]Z)!([12][0-9]{3}-[01][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9]Z)!([^!]*)!([^!]+)!([0-9]+)!([0-9]+)!(.*)");
      Matcher m = p.matcher (ResumptionToken);
      if (m.matches ())
      {
         from = m.group (1);
         until = m.group (2);
         set = m.group (3);
         metadataPrefix = m.group (4);
         String cursorStr = m.group (5);
         try {
            cursor = Integer.parseInt (cursorStr);
         } catch (Exception e) {  
         }
         String totalRecordsStr = m.group (6);
         try {
            totalRecords = Integer.parseInt (totalRecordsStr);
         } catch (Exception e) {  
         }
         identifier = m.group (7);         
      }
      
      return listRecords(from, until, metadataPrefix, headersOnly, set, cursor, totalRecords, identifier);        
    }
    
    
    public String makeTimestampDate ( String aDate, int fromUntil )
    {
/*
        String fullDate = aDate;

        if (fullDate.length () == 20)
           fullDate = aDate.substring (0, 10) + " " + aDate.substring (11, 19);
        else
        {
           if (fromUntil == 0)
              fullDate = aDate + " 00:00:00";
           else
              fullDate = aDate + " 23:59:59";   
        }   

        String yearStr = fullDate.substring (0, 4);
        int year = Integer.parseInt (yearStr);
        if (year < 1980)
           fullDate = "1980-01-01 00:00:01";
    
        return fullDate;

*/
        String fullDate = aDate;

        if (fullDate.length () == 10)
        {
           if (fromUntil == 0)
              fullDate = aDate + "T00:00:00Z";
           else
              fullDate = aDate + "T23:59:59Z";
        }

        String yearStr = fullDate.substring (0, 4);
        int year = Integer.parseInt (yearStr);
        if (year < 1980)
           fullDate = "1980-01-01T00:00:01Z";

        //set up date formatters and timezones
        SimpleDateFormat UTCDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        TimeZone tz1 = TimeZone.getTimeZone("UTC");
        UTCDateFormatter.setTimeZone(tz1);
        SimpleDateFormat LocalDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone tz2 = TimeZone.getTimeZone("GMT+2");
        LocalDateFormatter.setTimeZone(tz2);
        
        try {
           return ((LocalDateFormatter.format(UTCDateFormatter.parse (fullDate))));
        } catch (Exception e) {
           return "1980-01-01 00:00:00";
        }
    }
    
    
    /**
     * Returns the response data for ListRecords and ListIdentifiers requests.
     * If headersOnly is true, then it will be ListIdentifiers response data (only print headers)
     * and if false it will print the entire record for all matching entries.
     * @param from The date from which we will search
     * @param until The date untill which we will search
     * @param metaType What metadata type we are searching for
     * @param headersOnly Whether we want just the headers, or the entire records
     * @param set The set to search within (empty string for no sets)
     * @param cursor The position within the results to continue this search from.
     * @return A stringbuffer with the correctly formatted results.
     * @throws SQLException If the method fails to correctly access the database
     */
    public StringBuffer listRecords(String from, String until, String metaType, boolean headersOnly, String set , int cursor, int totalRecords, String identifier) throws SQLException
    {
        int epochSize = 1000;
    
        connect();
        //variable tracking whether a resumption token is needed
        boolean resume = false;
        //convinience bool to check if sets are enabled
        boolean sets = true;

        if(set.equals(""))
        {
            sets = false;
        }
        
        //if our current cursor position is non zero, obviously we are resuming
        //if(cursor != 0)
        //{
        //    resume = true;
        //}
        
        //create statement
        Statement stm = con.createStatement();
        
        //lets get the total number of records we are going to need
        // use CountCache to speed up counting of rows
//        int totalRecords = 0;
        ResultSet rs;

        String fullfrom = makeTimestampDate (from, 0);
        String fulluntil = makeTimestampDate (until, 1);
        
        if (totalRecords == -1)
        {
           if(sets)
              rs=stm.executeQuery("SELECT COUNT(*) FROM Archive use index (MetaTypeSetSpecDateID) WHERE MetaType="+quote (metaType)+" AND SetSpec="+quote (set)+" AND Date BETWEEN "+quote (fullfrom)+" AND "+quote (fulluntil)+";");
           else
              rs=stm.executeQuery("SELECT COUNT(*) FROM Archive use index (MetaTypeDateID) WHERE MetaType="+quote (metaType)+" AND Date BETWEEN "+quote (fullfrom)+" AND "+quote (fulluntil)+";");
           rs.next();
           totalRecords = rs.getInt("COUNT(*)");
        }

/*        rs = stm.executeQuery ("select count from CountCache where MetaType="+quote (metaType)+" AND SetSpec="+quote (set)+" AND fromdate="+quote (fullfrom)+" AND untildate="+quote (fulluntil)+" AND NOW()-Date<3600;");
        if (rs.next())
        {
           totalRecords = rs.getInt ("count");
        }
        else
        {
           if(sets)
           {
              rs= stm.executeQuery("SELECT COUNT(*) FROM Archive use index (MetaTypeSetSpecDateID) WHERE MetaType="+quote (metaType)+" AND SetSpec="+quote (set)+" AND Date BETWEEN "+quote (fullfrom)+" AND "+quote (fulluntil)+";");
           }
           else
           {
              rs= stm.executeQuery("SELECT COUNT(*) FROM Archive use index (MetaTypeDateID) WHERE MetaType="+quote (metaType)+" AND Date BETWEEN "+quote (fullfrom)+" AND "+quote (fulluntil)+";");
           }
           rs.next();
           totalRecords = rs.getInt("COUNT(*)");
           rs = stm.executeQuery ("replace into CountCache values (null, "+quote (metaType)+", "+quote (fullfrom)+", "+quote (fulluntil)+", "+quote (set)+", "+totalRecords+");");
        }
*/        
        //if there are more than 100 records, we are going to use flow control,
        //so we set resume to true
        if(totalRecords > cursor + epochSize)
        {
            resume = true;
        }
        
        //get all unique MetaTypes from our db
        rs = stm.executeQuery("SELECT DISTINCT MetaType FROM Archive;");
        
        //create a list of supported formats
        LinkedList formats = new LinkedList();       
        while(rs.next())
        {
            formats.add(rs.getString("MetaType"));
        }
        
        //create our respone string buffer
        StringBuffer sqlResponse = new StringBuffer();
        
        //check if db supports this format... if it doesnt, we will return an error 
        //message later in the response
        boolean formatAccepted = false;  
                
        for(int i = 0; i < formats.size();i++)
        {
            if(formats.get(i).equals(metaType))
            {
                formatAccepted = true;
                break;
            }
        }
        if(formatAccepted)
        {
            String identifierClause = "";
            if (identifier.length ()>0)
               identifierClause = "(Date="+quote(fullfrom)+" AND ID>"+quote (identifier)+") OR ";
            else   
               identifierClause = "(Date="+quote(fullfrom)+") OR ";
            
            //now make the query for records that match the specified MetaType
            if(sets)
            {
                rs = stm.executeQuery("SELECT * FROM Archive force index (MetaTypeSetSpecDateID) WHERE"+
                                      " MetaType="+quote (metaType)+
                                      " AND SetSpec="+quote (set)+
                                      " AND ("+
                                             identifierClause+
                                            "(Date>"+quote (fullfrom)+" AND Date<="+quote (fulluntil)+")"+
                                           ")"+
                                      " ORDER BY MetaType,SetSpec,Date,ID"+
                                      " LIMIT "+epochSize+";");
            }
            else
            {
                rs = stm.executeQuery("SELECT * FROM Archive force index (MetaTypeDateID) WHERE"+
                                      " MetaType="+quote (metaType)+
                                      " AND ("+
                                             identifierClause+
                                            "(Date>"+quote (fullfrom)+" AND Date<="+quote (fulluntil)+")"+
                                           ")"+
                                      " ORDER BY MetaType,Date,ID"+
                                      " LIMIT "+epochSize+";");
            }
            
            //add correct tag information depending on headersOnly
            if(headersOnly)
            {
                sqlResponse.append(" <ListIdentifiers>\n");
            }else
            {
                sqlResponse.append(" <ListRecords>\n");
            }

            //boolean variable to see if at least one record matches the criteria
            boolean recExists = false;
            
            // get identifier and dateStamp of last record
            String lastIdentifier = "";
            String lastDatestamp = from;

            //set up date formatter and timezone
            SimpleDateFormat UTCDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            TimeZone tz = TimeZone.getTimeZone("UTC");
            UTCDateFormatter.setTimeZone(tz);

            int seenRecords = 0;
            
            //iterate through records
            while( (seenRecords + cursor)<totalRecords && rs.next())
            {
                recExists = true;
                seenRecords++;
                if(!headersOnly)
                {
                    sqlResponse.append("  <record>\n");
                }
                if(rs.getBoolean("Deleted"))
                {
                    sqlResponse.append("<header status=\"deleted\">\n");
                    sqlResponse.append("<identifier>");
                    try {
                       lastIdentifier = new String (rs.getBytes ("ID"), "UTF-8");
                    } catch (Exception e) {
                       e.printStackTrace ();
                    }   
                    sqlResponse.append(lastIdentifier);
                    sqlResponse.append("</identifier>\n");
                    sqlResponse.append("<datestamp>");
                    lastDatestamp = UTCDateFormatter.format(rs.getTimestamp("Date"));  
                    sqlResponse.append(lastDatestamp);
                    sqlResponse.append("</datestamp>\n");
                    //sets
                    sqlResponse.append("<setSpec>");
                    try {
                       sqlResponse.append(new String (rs.getBytes ("SetSpec"), "UTF-8"));
                    } catch (Exception e) {
                       e.printStackTrace ();
                    }   
                    sqlResponse.append("</setSpec>\n");
                    
                    sqlResponse.append("</header>\n");
                }else
                {
                    sqlResponse.append("<header>\n");
                    try {
                       lastIdentifier = new String (rs.getBytes ("ID"), "UTF-8");
                    } catch (Exception e) {
                       e.printStackTrace ();
                    }   
                    sqlResponse.append("<identifier>"+lastIdentifier+"</identifier>\n");
                    sqlResponse.append("<datestamp>");
                    lastDatestamp = UTCDateFormatter.format(rs.getTimestamp("Date"));
                    sqlResponse.append(lastDatestamp);
                    sqlResponse.append("</datestamp>\n");
                    //sets                      
                    sqlResponse.append("<setSpec>");
                    try {
                       sqlResponse.append(new String (rs.getBytes ("SetSpec"), "UTF-8"));
                    } catch (Exception e) {
                       e.printStackTrace ();
                    }
                    sqlResponse.append("</setSpec>\n");
                    
                    sqlResponse.append("</header>\n");
                    if(!headersOnly)
                    {                    
                        String metadata = "";
                        try {
                           metadata = new String (rs.getBytes ("Metadata"), "UTF-8");
                        } catch ( Exception e ) {
                           e.printStackTrace();
                        }
                        if (metadata.matches ("(?sm)\\s*<\\s*metadata\\s*>.*<\\s*\\/\\s*metadata\\s*>\\s*"))
                           sqlResponse.append (metadata);
                        else
                        {   
                           sqlResponse.append ("<metadata>\n");
                           sqlResponse.append(metadata);
                           sqlResponse.append("\n");
                           sqlResponse.append("</metadata>\n");
                        }   

                        //check for an about section
                        if(rs.getString("About")!= null && rs.getString("About").length() != 0)
                       {
                           sqlResponse.append("<about>\n");
                           try {
                              sqlResponse.append(new String (rs.getBytes ("About"), "UTF-8"));
                           } catch ( Exception e ) {
                              e.printStackTrace();
                           }    
                           sqlResponse.append("\n");
                           sqlResponse.append("</about>");
                       }
                    }
                }
                if(!headersOnly)
                {
                    sqlResponse.append("</record>\n");
                }
            }
            if(resume)
            {
                sqlResponse.append("<resumptionToken ");
                sqlResponse.append("completeListSize=\"");
                sqlResponse.append(totalRecords);
                sqlResponse.append("\" cursor=\"");
                sqlResponse.append(cursor);
                sqlResponse.append("\"");
                if((cursor + epochSize) < totalRecords)
                {
                    sqlResponse.append(">");
//                    if(headersOnly)
//                    {
//                        if(sets)
//                        {
//                            sqlResponse.append("LIS!");
//                        }
//                        else
//                            sqlResponse.append("LI!");
//                    }else
//                    {
//                        if(sets)
//                        {
//                            sqlResponse.append("LRS!");
//                        }
//                        else
//                            sqlResponse.append("LR!");
//                    }
                    sqlResponse.append(lastDatestamp);
                    sqlResponse.append("!");
                    sqlResponse.append(until);
                    sqlResponse.append("!");
                    if(sets)
//                    {
                       sqlResponse.append(set);
                    sqlResponse.append("!");
//                    }
                    sqlResponse.append(metaType);
                    sqlResponse.append("!");
                    sqlResponse.append(cursor+epochSize);
                    sqlResponse.append("!");
                    sqlResponse.append(totalRecords);
                    sqlResponse.append("!");
                    // remember that we did not XML-escape lastIdentifier!!!
                    sqlResponse.append(lastIdentifier);
                    sqlResponse.append("</resumptionToken>");
                }
                else
                {
                    sqlResponse.append("/>");
                }
            }
            if(headersOnly){
                sqlResponse.append(" </ListIdentifiers>\n");
            }else
            {
                sqlResponse.append(" </ListRecords>\n");
            }

            //if no matches are found
            if(!recExists)
            {
                //overwrites anything written to xml so far
                sqlResponse = new StringBuffer();
                sqlResponse.append("<error code=\"noRecordsMatch\"/>\n");
            }


            //if no format is found, return a dissemination error
        }else
        {
            sqlResponse.append(" <error code=\"cannotDisseminateFormat\"/>\n");
        }
        rs.close();
        return sqlResponse;
    }
    
    //returns the earliet possible datestamp - we have an index on date for fast access
    public StringBuffer getEarliestDateStamp() throws SQLException
    {
        connect();
        StringBuffer sqlResponse = new StringBuffer();
        //create statement and execute sql query
        Statement stm = con.createStatement();
        //get earliest Date stamp
        ResultSet rs = stm.executeQuery("SELECT Date FROM Archive ORDER BY Date LIMIT 1;");
        
        //set up date formatter and timezone
        SimpleDateFormat UTCDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        UTCDateFormatter.setTimeZone(tz);
        
        //move the result set pointer to the first row
        rs.next();
        
        //format the date and add it to our resp onse
        sqlResponse.append((UTCDateFormatter.format(rs.getTimestamp("Date"))));
        rs.close();
        return sqlResponse;
    }

    public StringBuffer listSets() throws SQLException
    {
        connect();
        StringBuffer sqlResponse = new StringBuffer();
        //Create statement so we can execute a query
        Statement stm = con.createStatement();
        
        //Exectute our query and get the result from the database.
        ResultSet rs = stm.executeQuery("SELECT DISTINCT SetSpec FROM Archive;");


        sqlResponse.append("<ListSets>\n");
        //Iterate through all the results, adding them to the response
        while(rs.next())
        {
            sqlResponse.append("<set>\n");
            
            String setSpec = rs.getString("SetSpec");
            sqlResponse.append("<setSpec>"+setSpec+"</setSpec>\n");            
            
            Statement stmName = con.createStatement();
            ResultSet rsName = stmName.executeQuery("SELECT name FROM Repositories WHERE ID="+quote (setSpec)+";");
            String setName = "";
            while(rsName.next())
               setName = rsName.getString("name");
            if (setName.equals (""))
               setName = setSpec;
            sqlResponse.append("<setName>"+setName+"</setName>\n");
                           
            sqlResponse.append("</set>\n");
        }

        sqlResponse.append("</ListSets>\n");

        return sqlResponse;
    }
}


