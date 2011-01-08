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
import javax.servlet.http.*;
import javax.servlet.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

public class Summary extends HttpServlet 
{   
    //database variables
    private String dbAddress, dbUsername, dbPassword, dbDriver;
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
    public void init ( ServletConfig config )
     throws ServletException
    {
        super.init (config);
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
/*                // get the descriptive configuration values 
                adminEmail = getXMLValue (root, "repository/adminEmail", "someone@somewhere.org");
                repositoryName = getXMLValue (root, "repository/repositoryName", "Default repository name");
                repositoryURL = getXMLValue (root, "repository/description/URL", "http://somewhere.org/");
                repositoryDescription = getXMLValue (root, "repository/description/text", "Default repository description");
*/                
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
    @Override
     public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
     {
        //get the printWriter object with which we will print out data
        response.setContentType ("text/xml");
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
                ResultSet rs = stm.executeQuery("SELECT source,count(distinct id) FROM Archive group by source");

                //list our most recent 5 records
                while(rs.next())
                {
                    //use xml parsing on the metadata in order to extract neccesary details
                    //NOTE, WE ARE NOT VALIDATING THE XML, JUST READING IT.
                    //We assume everything in the database has been validated by the harvesting program 
                    //that put it there.
                        finalResponse.append ("<source>");
                        finalResponse.append ("<url>"+rs.getString ("source")+"</url>");
                        finalResponse.append ("<count>"+rs.getString ("count(distinct id)")+"</count>");
                        finalResponse.append ("</source>");
                        
                }

            }catch(ClassNotFoundException e)
            {
                finalResponse.append("<error>Failed to connect to database</error>\n");
                e.printStackTrace();
            }catch(SQLException e)
            {
                finalResponse.append("<error>an SQL Error occured</error>\n");
                e.printStackTrace();
            }
            finally
            {
                if(configFileError)
                {   
                    finalResponse = new StringBuffer("");
                }else
                {
                    finalResponse.append("</summary>");   
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
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        //throw the params accross to the doGet method and let it deal with it.
        doGet(request, response);
    }
}
