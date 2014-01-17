import java.io.IOException;
import java.util.StringTokenizer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

/**
 * Configuration information for the database
 * @author Hussein Suleman
 */
public class Config
{
  /** location of settings for class */
  private String settingsFile;
  /** the URL of the database to harvest to */
  private String dbAddress;
  /** the username to connect to the database with  */
  private String dbUsername;
  /** the plaintext password for the database */
  private String dbPassword;
  /** the driver for the database */
  private String dbDriver;
  /** path of log file */
  private String logLocation;
  /** logging class */
  public Log log;  
  public MetadataFormat [] formatList;

   /**
    * Constructor to set up the instance variables and load up the harvest and database
    * properties
    * @param target the file name of the repository configuration that is to be harvested
    */
   public Config ()
   {
      // find relative position of settings file
      settingsFile = "/etc/etdportal/config.xml";
      loadDatabaseProperties (); // load the database properties
      openLogFile ("");
   }
   
   /* for specialised per-repository log files */
   public void openLogFile ( String suffix )
   {
      try{
         log = new Log( logLocation + suffix );
      } catch(IOException e) {
         System.out.println("Could not open log file! Error: "+e);
      }
   }   

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
    * Method to load the database properties from the database
    */
   private void loadDatabaseProperties ()
   {
      try {
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
            // get the database connection details
            dbAddress = getXMLValue (root, "repository/database/URL", "localhost/dba");
            dbUsername = getXMLValue (root, "repository/database/username", "");
            dbPassword = getXMLValue (root, "repository/database/password", "");
            dbDriver = getXMLValue (root, "repository/database/driver", "com.mysql.jdbc.Driver");
            logLocation = getXMLValue (root, "repository/logLocation", "/var/log/etdportal/repository.log");
            
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
            System.out.println ("Incorrect config file structure.  Base Node has wrong name.");
         }
      } catch ( Exception e ) {
         e.printStackTrace();
      }       
   }
   
   /*
    * accessor methods 
    */
   public String getDbAddress () { return dbAddress; }
   public String getDbUsername () { return dbUsername; }
   public String getDbPassword () { return dbPassword; }
   public String getDbDriver () { return dbDriver; }
   
}
