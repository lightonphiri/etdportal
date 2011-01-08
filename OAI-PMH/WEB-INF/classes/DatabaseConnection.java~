import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.TimeZone;

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
    public DatabaseConnection(String address, String username, String password, MetadataFormat [] formatList) throws ClassNotFoundException, SQLException
    {
        formatSpecList = formatList;
        Class.forName("com.mysql.jdbc.Driver");
        url = "jdbc:mysql:"+address;
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
    
    public StringBuffer getRecord(String ID, String metaType) throws SQLException
    {
        connect();
        //create statement and query the database
        Statement stm = con.createStatement();
        ResultSet rs = stm.executeQuery("SELECT * FROM Archive WHERE ID = '"+ID+"';");
        
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
                   sqlResponse.append("</header>\n");
                   //sqlResponse.append("<metadata>\n");
                   sqlResponse.append(rs.getString("MetaData"));
                   sqlResponse.append("\n");
                   //sqlResponse.append("</metadata>\n");
                   //check for about section.
                   if(rs.getString("About")!= null && rs.getString("About").length() != 0)
                   {
                       //sqlResponse.append("<about>\n");
                       //sqlResponse.append(rs.getString("About"));
                       //sqlResponse.append("\n");
                       //sqlResponse.append("</about>");
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
        ResultSet rs = stm.executeQuery("SELECT MetaType FROM Archive WHERE ID='"+ID+"';");
        
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
    public StringBuffer listRecords(String ResumptionToken) throws SQLException
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
        Scanner resump = new Scanner(ResumptionToken);
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
    public StringBuffer listRecords(String from, String until, String metaType, boolean headersOnly, String set , int cursor) throws SQLException
    {
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
        if(cursor != 0)
        {
            resume = true;
        }
        
        //create statement
        Statement stm = con.createStatement();
        
        //lets get the total number of records we are going to need
        ResultSet rs;
        if(sets)
        {
            rs= stm.executeQuery("SELECT COUNT(*) FROM Archive WHERE MetaType = '"+metaType+"' " + "AND SetSpec = '"+set+"' AND Date BETWEEN '"+from+"' AND '"+until+"';");
        }
        else
        {
            rs= stm.executeQuery("SELECT COUNT(*) FROM Archive WHERE MetaType = '"+metaType+"' AND Date BETWEEN '"+from+"' AND '"+until+"';");
        }
        rs.next();
        int totalRecords = rs.getInt("COUNT(*)");
        
        //if there are more than 100 records, we are going to use flow control,
        //so we set resume to true
        if(totalRecords > 100)
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
            //now make the query for records that match the specified MetaType
            if(sets)
            {
                rs = stm.executeQuery("SELECT * FROM Archive WHERE MetaType = '"+metaType+"' " +
                        "AND SetSpec = '"+set+"' AND Date BETWEEN '"+from+"' AND '"+until+"' ORDER BY Date LIMIT 100 OFFSET "+cursor+";");
            }
            else
            {
                rs = stm.executeQuery("SELECT * FROM Archive WHERE MetaType = '"+metaType+"' " +
                        "AND Date BETWEEN '"+from+"' AND '"+until+"' ORDER BY Date LIMIT 100 OFFSET "+cursor+";");
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

            //set up date formatter and timezone
            SimpleDateFormat UTCDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
            TimeZone tz = TimeZone.getTimeZone("UTC");
            UTCDateFormatter.setTimeZone(tz);

            //iterate through records
            while(rs.next())
            {
                recExists = true;
                if(!headersOnly)
                {
                    sqlResponse.append("  <record>\n");
                }
                if(rs.getBoolean("Deleted"))
                {
                    sqlResponse.append("<header status=\"deleted\">\n");
                    sqlResponse.append("<identifier>");
                    sqlResponse.append(rs.getString("ID"));
                    sqlResponse.append("</identifier>\n");
                    sqlResponse.append("<datestamp>");
                    sqlResponse.append(UTCDateFormatter.format(rs.getTimestamp("Date")));
                    sqlResponse.append("</datestamp>\n");
                    //sets
                    sqlResponse.append("<setSpec>\n");
                    sqlResponse.append(rs.getString("SetSpec"));
                    sqlResponse.append("</setSpec>\n");
                    
                    sqlResponse.append("</header>\n");
                }else
                {
                    sqlResponse.append("<header>\n");
                    sqlResponse.append("<identifier>"+rs.getString("ID")+"</identifier>\n");
                    sqlResponse.append("<datestamp>");
                    sqlResponse.append(UTCDateFormatter.format(rs.getTimestamp("Date")));
                    sqlResponse.append("</datestamp>\n");
                    //sets                      
                    sqlResponse.append("<setSpec>\n");
                    sqlResponse.append(rs.getString("SetSpec"));
                    sqlResponse.append("</setSpec>\n");
                    
                    sqlResponse.append("</header>\n");
                    if(!headersOnly)
                    {                    
                        //sqlResponse.append("<metadata>\n");
                        sqlResponse.append(rs.getString("MetaData"));
                        sqlResponse.append("\n");
                        //sqlResponse.append("</metadata>\n");

                        //check for an about section
                        if(rs.getString("About")!= null && rs.getString("About").length() != 0)
                       {
                           //sqlResponse.append("<about>\n");
                           //sqlResponse.append(rs.getString("About"));
                           //sqlResponse.append("\n");
                           //sqlResponse.append("</about>");
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
                if((cursor + 100) < totalRecords)
                {
                    sqlResponse.append(">");
                    if(headersOnly)
                    {
                        if(sets)
                        {
                            sqlResponse.append("LIS!");
                        }
                        else
                            sqlResponse.append("LI!");
                    }else
                    {
                        if(sets)
                        {
                            sqlResponse.append("LRS!");
                        }
                        else
                            sqlResponse.append("LR!");
                    }
                    sqlResponse.append(from);
                    sqlResponse.append("!");
                    sqlResponse.append(until);
                    sqlResponse.append("!");
                    if(sets)
                    {
                        sqlResponse.append(set);
                        sqlResponse.append("!");
                    }
                    sqlResponse.append(metaType);
                    sqlResponse.append("!");
                    sqlResponse.append(cursor+100);
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
            sqlResponse.append("<setSpec>"+rs.getString("SetSpec")+"</setSpec>\n");
            sqlResponse.append("<setName>"+rs.getString("SetSpec")+"</setName>\n");
            sqlResponse.append("</set>\n");
        }

        sqlResponse.append("</ListSets>\n");

        return sqlResponse;
    }
}


