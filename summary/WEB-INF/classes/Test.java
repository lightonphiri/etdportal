/**
 *
 * @author Lawrence Webley
 *  RSS Feed Generator
 *  18 June 2009
 */
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
//import javax.servlet.http.*;
//import javax.servlet.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

public class Test 
{   
    //database variables
//    private String dbAddress, dbUsername, dbPassword;
    //boolean variable to keep track of whether the config file has been properly read.
//    private boolean configFileError = false;
    //message to display if there is a config error
//    private String configErrorMessage;
    
    //servlet instantiation (which happens on tomcat startup)
    public static void main ( String [] args )
    {
       String dbAddress = "//localhost:3306/dba";
       String dbUsername = "dba";
       String dbPassword = "dba";

/*        super.init (config);
        //This is where we read in our config file and assign variables
        try
        {   
            //read in the xml config file
            DocumentBuilderFactory docBuilderFac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFac.newDocumentBuilder();
            Document doc = docBuilder.parse(getServletContext ().getResourceAsStream ("/WEB-INF/config/config.xml"));
            
            //normalize text representation
            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();
            
            //check to see if the base node has the correct name
            if(root.getNodeName().equals("configuration"))
            {
                //get database details==========================================                
                NodeList dbList = root.getElementsByTagName("database");
                Element dbElement = (Element)dbList.item(0);
                
                //get the address element of the database
                NodeList dbAddressList = dbElement.getElementsByTagName("databaseAddress");
                Element dbAddressElement = (Element)dbAddressList.item(0);
                NodeList dbTextAddressElement = dbAddressElement.getChildNodes();
                dbAddress = ((Node)dbTextAddressElement.item(0)).getNodeValue().trim();
                
                //get the username element of the database
                NodeList dbUserList = dbElement.getElementsByTagName("username");
                Element dbUserElement = (Element)dbUserList.item(0);
                NodeList dbTextUserElement = dbUserElement.getChildNodes();
                dbUsername = ((Node)dbTextUserElement.item(0)).getNodeValue().trim();
                
                //get the username element of the database
                NodeList dbPassList = dbElement.getElementsByTagName("password");
                Element dbPassElement = (Element)dbPassList.item(0);
                NodeList dbTextPassElement = dbPassElement.getChildNodes();
                dbPassword = ((Node)dbTextPassElement.item(0)).getNodeValue().trim();                              
                                
            }
            else
            {
                configFileError = true;
                configErrorMessage = "Config file has incorrect structure. Root element should be named 'configuration'.";
            }
        }catch(IOException e)
        {
            e.printStackTrace();
            configErrorMessage = "An IO Error occured. This is most likely because the servlet cannot find or open the config file.";
            configFileError = true;
        }
        catch(ParserConfigurationException e)
        {
            e.printStackTrace();
            configErrorMessage = "A Parser configuration error has occured. Please contact the systems administrator for assistance.";
            configFileError = true;
        }catch(SAXException e)
        {
            e.printStackTrace();
            configErrorMessage = "A SAX exception has occured. Please contact the systems administrator for assistance.";
            configFileError = true;
        }
        catch(NullPointerException e)
        {
            configErrorMessage = "A null pointer error has occured. This is probably due to one or more empty or incorrect fields" +
                    " in the config file. Please contact the systems administrator for help.";
            configFileError = true;
            e.printStackTrace();
        }

    }
    
    //doGet method -> url requests will cause this to execute
    @Override
     public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
     {
        //get the printWriter object with which we will print out data
        PrintWriter out = response.getWriter();
        
        //if we have no config file errors, then go ahead and service the request
        if(!configFileError)
        {
            //variable declaration and instantiation        
            String baseURL;
            baseURL = request.getRequestURL().toString();            

            //use a string buffer, because strings are immutable and therefore highly inefficient
            //when added many times.
            StringBuffer finalResponse = new StringBuffer();
            //provide Rss response xml

            //specification for xml
            finalResponse.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

            //declare as an rss type
            finalResponse.append("<summary>\n");
*/
            //database transactions
StringBuffer finalResponse = new StringBuffer ();
            try
            {
                //make connection to database
                Class.forName("com.mysql.jdbc.Driver");
                String url = "jdbc:mysql:"+dbAddress;
                Connection con;
                con = DriverManager.getConnection (url, dbUsername, dbPassword);

                //create a statement and execute our query
                Statement stm = con.createStatement();
                ResultSet rs = stm.executeQuery("SELECT source,count(id) FROM Archive group by source");

                //list our most recent 5 records
                while(rs.next())
                {
                    //use xml parsing on the metadata in order to extract neccesary details
                    //NOTE, WE ARE NOT VALIDATING THE XML, JUST READING IT.
                    //We assume everything in the database has been validated by the harvesting program 
                    //that put it there.
//                    try
//                    {
                        finalResponse.append ("<source>");
                        finalResponse.append ("<url>"+rs.getString ("source")+"</url>");
                        finalResponse.append ("<count>"+rs.getString ("count(id)")+"</count>");
                        finalResponse.append ("</source>");
                        
/*                        //set protocal and put it in a string buffer
                        StringBuffer xml = new StringBuffer("<?xml version=\"1.0\"?>");
                        xml.append(rs.getString("source"));
*/
                }

            }catch(ClassNotFoundException e)
            {
                finalResponse.append("       <item>\n");
                finalResponse.append("        <title>Failed to connect to database</title>\n");
                finalResponse.append("        <link>"+"</link>\n");
                finalResponse.append("        <description>The rss feed generator failed to establish a connection" +
                        "to the sql server hosting the metadata archive. Please contact the system administrator for details</description>\n");
                finalResponse.append("       </item>\n");                                 
                e.printStackTrace();
            }catch(SQLException e)
            {
                 finalResponse.append("       <item>\n");
                finalResponse.append("        <title>A SQL Error occured</title>\n");
                finalResponse.append("        <link>"+"</link>\n");
                finalResponse.append("        <description>The rss feed generator encountered a SQL error while " +
                        "attempting to query the metadata archive. Please contact the system administrator for details</description>\n");
                finalResponse.append("       </item>\n");
                e.printStackTrace();
            }
            finally
            {
            }  
            System.out.println(finalResponse);
    }
}
