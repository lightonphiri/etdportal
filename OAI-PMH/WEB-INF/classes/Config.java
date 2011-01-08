import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.StringTokenizer;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Lawrence Webley
 * @author Hussein Suleman
 * This class is used to store the configuration data for the server, and is 
 * passed around as a floating object between classes.
 */
public class Config 
{
    /*Admin email address */
    public String adminEmail;
    /*Name of the repository*/
    public String repoName;
    /*Description of repository*/
    public String repoDescUrl;
    public String repoDescText;
    /*Contains the metadata format information.
     * (name, namespace and schema for each metadata type)*/
    public MetadataFormat [] formatList;
    /*And instance of the DatabaseConnection class, so that the connection need 
     * only be created once and then all statements passed to that. This is a 
     * performance enhancing change*/
    public DatabaseConnection dbCon;
    
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

    /**
     * Constructor class for the config class. The xml config document is read
     * into the program at this point and then partitioned into usefull variables. 
     */
    public Config ( HttpServlet servlet )
     throws ParserConfigurationException, IOException, ClassNotFoundException, SQLException
    {        
        try
        {
            //declare the db variables
            String dbAddress, dbUsername, dbPassword;
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
                repoName = getXMLValue (root, "repository/repositoryName", "Default repository name");
                repoDescUrl = getXMLValue (root, "repository/description/URL", "http://somewhere.org/");
                repoDescText = getXMLValue (root, "repository/description/text", "Default repository description");
                
                // get the database connection details
                dbAddress = getXMLValue (root, "repository/database/URL", "localhost/dba");
                dbUsername = getXMLValue (root, "repository/database/username", "");
                dbPassword = getXMLValue (root, "repository/database/password", "");
            
                // get the metadata formats
                Element metadataFormats = getXMLElement (root, "repository/metadataFormats");
                NodeList listOfFormats = metadataFormats.getElementsByTagName("metadataFormat");
                formatList = new MetadataFormat [listOfFormats.getLength()];
                for ( int i=0; i<listOfFormats.getLength (); i++ )
                   formatList[i] = new MetadataFormat (
                                      getXMLValue ((Element)listOfFormats.item(i), "prefix", ""),
                                      getXMLValue ((Element)listOfFormats.item(i), "schema", ""),
                                      getXMLValue ((Element)listOfFormats.item(i), "namespace", "")                                       
                                   );
                
            }
            else
            {
                throw new IOException("Incorrect config file structure. Base Node has wrong name.");
            }
            
            //instantiate the database connection
            //if this fails then we cannot continue with the response serving
            //hence errors are thrown back to the OAI_servlet
            dbCon = new DatabaseConnection(dbAddress, dbUsername, dbPassword, formatList);
            
        }//exceptions
        catch(SAXException e)
        {
            Exception x = e.getException();
            if(x != null)
            {
                x.printStackTrace();
            }
        }       
        
    }//end constructor
}
