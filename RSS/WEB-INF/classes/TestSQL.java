/**
 *
 * @author Lawrence Webley
 *  RSS Feed Generator
 *  18 June 2009
 */
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import java.util.TimeZone;
//import javax.servlet.http.*;
//import javax.servlet.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

public class TestSQL
{   
    //database variables
    private String dbAddress, dbUsername, dbPassword, dbDriver;
    //admin email
    private String adminEmail;
    //chanal variables
    private String repositoryName, repositoryDescription;
    //address of the user friendly website containing the records
    private String repositoryURL;
    //boolean variable to keep track of whether the config file has been properly read.
    private boolean configFileError = false;
    //message to display if there is a config error
    private String configErrorMessage;
    
    /**
     * auxiliary method to get an Element from an XPath
     */
    public Element getXMLElement ( Element root, String xpath )
    {
       StringTokenizer st = new StringTokenizer (xpath, "/");
       while (st.hasMoreTokens ())
       {
          NodeList nextLevel = root.getElementsByTagName (st.nextToken ());
          if (nextLevel.getLength () == 0)
             return null;
          else
             root = (Element)nextLevel.item(0);
       }
       return root;
    }

    /**
     * auxiliary method to get a value from an XPath
     */
    public String getXMLValue ( Element root, String xpath, String defaultValue )
    {
       root = getXMLElement (root, xpath);
       if (root == null)
          return defaultValue;
       NodeList values = root.getChildNodes ();
       if (values.getLength () == 0)
          return defaultValue;
       return values.item(0).getNodeValue().trim();   
    }
    
    //servlet instantiation (which happens on tomcat startup)
    public void init ()
    {
        System.setProperty ("file.encoding", "UTF-8");
        //This is where we read in our config file and assign variables
        try
        {   
            //read in the xml config file
            DocumentBuilderFactory docBuilderFac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFac.newDocumentBuilder();
            Document doc = docBuilder.parse("/etc/etdportal/config.xml");
            
            //normalize text representation
            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();
            
            // check to see if the base node has the correct name
            if(root.getNodeName().equals("configuration"))
            {
                // get the descriptive configuration values 
                adminEmail = getXMLValue (root, "repository/adminEmail", "someone@somewhere.org");
                repositoryName = getXMLValue (root, "repository/repositoryName", "Default repository name");
                repositoryURL = getXMLValue (root, "repository/description/URL", "http://somewhere.org/");
                repositoryDescription = getXMLValue (root, "repository/description/text", "Default repository description");
                
                // get the database connection details
                dbAddress = getXMLValue (root, "repository/database/URL", "localhost/dba");
                dbUsername = getXMLValue (root, "repository/database/username", "");
                dbPassword = getXMLValue (root, "repository/database/password", "");
                dbDriver = getXMLValue (root, "repository/database/driver", "com.mysql.jdbc.Driver");
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
     public void doGet() throws IOException
     {
        PrintStream out = System.out;
        
//        response.setContentType ("text/xml");
//        response.setCharacterEncoding("UTF-8");
        //get the printWriter object with which we will print out data
//        PrintWriter out = response.getWriter();
        
        //if we have no config file errors, then go ahead and service the request
        if(!configFileError)
        {
            //variable declaration and instantiation        
            String baseURL;
//            baseURL = request.getRequestURL().toString();
            baseURL = "someurl";

            //use a string buffer, because strings are immutable and therefore highly inefficient
            //when added many times.
            StringBuffer finalResponse = new StringBuffer();
            //provide Rss response xml

            //specification for xml
            finalResponse.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

            //declare as an rss type
            finalResponse.append("<rss version=\"2.0\">\n");

            //channel creation
            finalResponse.append("  <channel>\n");
            finalResponse.append("      <title>"+repositoryName+"</title>\n");
            finalResponse.append("      <link>"+repositoryURL+"</link>\n");
            finalResponse.append("      <description>"+repositoryDescription+"</description>\n");
            finalResponse.append("      <language>en-gb</language>\n");
            finalResponse.append("      <docs>http://www.rhttp:/ssboard.org/rss-specification</docs>\n");
            finalResponse.append("      <generator>etdportal RSS Feed generator</generator>\n");        
            finalResponse.append("      <webMaster>"+adminEmail+"</webMaster>\n");

            //set the default values, should the db transactions fail
            String title = "No Title Found";
            String description = "No description Found";

            //database transactions
            try
            {
                //make connection to database
                Class.forName (dbDriver);
                String url = dbAddress;
                Connection con;
                con = DriverManager.getConnection (url, dbUsername, dbPassword);

                //create a statement and execute our query
                Statement stm = con.createStatement();
                ResultSet rs = stm.executeQuery("SELECT DISTINCT * FROM Archive where Deleted=\'0\' ORDER BY Date desc LIMIT 20000,5");

                //list our most recent 5 records
                while(rs.next())
                {
                    //use xml parsing on the metadata in order to extract neccesary details
                    //NOTE, WE ARE NOT VALIDATING THE XML, JUST READING IT.
                    //We assume everything in the database has been validated by the harvesting program 
                    //that put it there.
                    try
                    {
                        //set protocal and put it in a string buffer
                        StringBuffer xml = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                        xml.append(new String (rs.getBytes("MetaData"), "UTF-8"));
                        
                        System.out.println (new String (rs.getBytes("MetaData"), "UTF-8"));
                        //xml.append (new String (rs.getString("Metadata")));

                        //now use a StringReader to convert it into a InputStream
                        //(we need to do this, so that we can use a string instead of a file with the docBuilder)
                        StringReader xmlStream = new StringReader(xml.toString());
                        InputSource inStream = new InputSource();
                        inStream.setCharacterStream(xmlStream);

                        //read into document structure
                        DocumentBuilderFactory docBuilderFac = DocumentBuilderFactory.newInstance();
                        docBuilderFac.setNamespaceAware (true);
                        DocumentBuilder docBuilder = docBuilderFac.newDocumentBuilder();
                        Document doc;
                        doc = docBuilder.parse(inStream);

                        //normalize text representation
                        doc.getDocumentElement().normalize();

                        //get title
                        NodeList titleList = doc.getElementsByTagNameNS("*", "title");
                        if (titleList.getLength() > 0)
                        {
                           Element titleElement = (Element)titleList.item(0);
                           NodeList textTitleList = titleElement.getChildNodes();
                           if (textTitleList.getLength() > 0)
                              title = ((Node)textTitleList.item(0)).getNodeValue().trim();
                        }   

                        //get description
                        NodeList descList = doc.getElementsByTagNameNS("*","description");
                        if (descList.getLength () > 0)
                        {
                           Element descElement = (Element)descList.item(0);
                           NodeList textDescList = descElement.getChildNodes();
                           if (textDescList.getLength () > 0)
                              description = ((Node)textDescList.item(0)).getNodeValue().trim();
                        }   

                        //shorten description if its too long
                        if(description.length() > 384)
                        {
                            description = description.substring(0, 384);
                            //add some ending dots to signify that the description continues
                            description = description + "...";
                        }
                        
                        // some quick encoding fixes [hussein, 4 march 2010]
                        title = title.replaceAll ("&", "&amp;");
                        title = title.replaceAll ("<", "&lt;");
                        title = title.replaceAll (">", "&gt;");
                        description = title.replaceAll ("&", "&amp;");
                        description = title.replaceAll ("<", "&lt;");
                        description = title.replaceAll (">", "&gt;");

                        //catch throwable errors
                    }catch(ParserConfigurationException e)
                    {
                        e.printStackTrace();
                    }catch(SAXException e)
                    {
                        e.printStackTrace();
                    }

                    //set up our date formatting tools
                    SimpleDateFormat UTCDateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss zzz");
                    TimeZone tz = TimeZone.getTimeZone("UTC");
                    UTCDateFormatter.setTimeZone(tz);

                    //and finally we get to writting our RSS item
                    finalResponse.append("       <item>\n");
                    finalResponse.append("        <title>"+title+"</title>\n");
                    finalResponse.append("        <link>"+repositoryURL+"</link>\n");
                    finalResponse.append("        <description>"+description+"</description>\n");
                    finalResponse.append("        <pubDate>"+UTCDateFormatter.format(rs.getTimestamp("Date"))+"</pubDate>\n");
                    finalResponse.append("        <guid>"+rs.getString("ID")+"</guid>\n");
                    finalResponse.append("       </item>\n");
                }

            }catch(ClassNotFoundException e)
            {
                finalResponse.append("       <item>\n");
                finalResponse.append("        <title>Failed to connect to database</title>\n");
                finalResponse.append("        <link>"+baseURL+"</link>\n");
                finalResponse.append("        <description>The rss feed generator failed to establish a connection " +
                        "to the sql server hosting the metadata archive. Please contact the system administrator for details</description>\n");
                finalResponse.append("       </item>\n");                                 
                e.printStackTrace();
            }catch(SQLException e)
            {
                 finalResponse.append("       <item>\n");
                finalResponse.append("        <title>A SQL Error occured</title>\n");
                finalResponse.append("        <link>"+baseURL+"</link>\n");
                finalResponse.append("        <description>The rss feed generator encountered a SQL error while " +
                        "attempting to query the metadata archive. Please contact the system administrator for details</description>\n");
                finalResponse.append("       </item>\n");
                e.printStackTrace();
            }
            finally
            {
                if(configFileError)
                {   
                    finalResponse = new StringBuffer("");
                }else
                {
                    finalResponse.append("  </channel>\n");
                    finalResponse.append("</rss>\n");   
                }
                out.print(finalResponse);
            }  
        }else//this runs if we encountered an error in the configuration file, and aborted 
            //the process of serving a request.
        {
            StringBuffer output = new StringBuffer(configErrorMessage);
            out.print(output);
        }
     }
     
     public static void main ( String [] args )
     {
        try {
        TestSQL ts = new TestSQL ();
        ts.init ();
        ts.doGet ();
        } catch (Exception e) {}
     }
    
}
