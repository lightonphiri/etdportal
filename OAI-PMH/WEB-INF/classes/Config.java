

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
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
            Document doc = docBuilder.parse(servlet.getServletContext().getResourceAsStream ("/WEB-INF/config/config.xml"));
            
            //normalize text representation
            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();
            
            //check to see if the base node has the correct name
            if(root.getNodeName().equals("configuration"))
            {
                //get the admin email===========================================
                NodeList adminEmailList = root.getElementsByTagName("AdminEmail");
                Element adminEmailElement = (Element)adminEmailList.item(0);
                NodeList textAEList = adminEmailElement.getChildNodes();
                adminEmail = ((Node)textAEList.item(0)).getNodeValue().trim();   
                
                //get the repository name=======================================
                NodeList repoList = root.getElementsByTagName("repositoryName");
                Element repoElement = (Element)repoList.item(0);
                NodeList textRepoList = repoElement.getChildNodes();
                repoName = ((Node)textRepoList.item(0)).getNodeValue().trim();
                
                //get the repository description================================
                NodeList repoDescList = root.getElementsByTagName("description");
                Element repoDescElement = (Element)repoDescList.item(0);
                
                //get the URL element of description;
                NodeList urlList = repoDescElement.getElementsByTagName("URL");
                Element urlElement = (Element)urlList.item(0);
                NodeList textUrlElement = urlElement.getChildNodes();
                repoDescUrl = ((Node)textUrlElement.item(0)).getNodeValue().trim();
                
                //get the text element of description
                NodeList textList = repoDescElement.getElementsByTagName("text");
                Element textElement = (Element)textList.item(0);
                NodeList stringTextElement = textElement.getChildNodes();
                repoDescText = ((Node)stringTextElement.item(0)).getNodeValue().trim();
                
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
                throw new IOException("Incorrect config file structure. Base Node has wrong name.");
            }
            //Now we are done with the config file. We now concentrate on the metadataFormats.xml file
            Document docM = docBuilder.parse(servlet.getServletContext().getResourceAsStream ("/WEB-INF/config/metadataFormats.xml"));
            
            //normalize text representation
            docM.getDocumentElement().normalize();
            Element rootM = docM.getDocumentElement();
            
            //check to see if the base node has the correct name
            if(rootM.getNodeName().equals("MetadataFormats"))
            {
                NodeList listOfFormats = rootM.getElementsByTagName("FormatSpec");
                int totalFormats = listOfFormats.getLength();
                formatList = new MetadataFormat [totalFormats];
                System.out.println("Number of formats in metadataFormats file is "+ totalFormats);
                
                for(int i = 0; i < totalFormats;i++)
                {
                    //create temp variables to store the file entries until we can
                    //put them in the formatList array
                    String prefix;
                    String schema;
                    String namespace;
                    Node formatNode = listOfFormats.item(i);
                    if(formatNode.getNodeType() == Node.ELEMENT_NODE)
                    {
                        Element formatElement = (Element)formatNode;
                        
                        //======get format Name=========
                        NodeList prefixList = formatElement.getElementsByTagName("prefix");
                        Element prefixElement = (Element)prefixList.item(0);
                        
                        NodeList textPrefixList = prefixElement.getChildNodes();
                        prefix = ((Node)textPrefixList.item(0)).getNodeValue().trim();
                        
                        //======get format Schema=========
                        NodeList schemaList = formatElement.getElementsByTagName("schema");
                        Element schemaElement = (Element)schemaList.item(0);
                        
                        NodeList textSchemaList = schemaElement.getChildNodes();
                        schema = ((Node)textSchemaList.item(0)).getNodeValue().trim();
                        
                        //======get format Schema=========
                        NodeList namespaceList = formatElement.getElementsByTagName("namespace");
                        Element namespaceElement = (Element)namespaceList.item(0);
                        
                        NodeList textNSList = namespaceElement.getChildNodes();
                        namespace = ((Node)textNSList.item(0)).getNodeValue().trim();
                        
                        //now we add this formatSpec to our formatList array
                        System.out.println("Adding new format at index ["+i+"]: Prefix="+prefix
                                +", Schema="+schema+", namespace="+namespace);
                        formatList[i] = new MetadataFormat(prefix,schema,namespace);
                    }
                }
            }else
            {
                throw new IOException("Incorrect metadata format file structure. Base Node has wrong name.");
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
