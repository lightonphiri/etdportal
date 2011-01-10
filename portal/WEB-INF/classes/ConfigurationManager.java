/**
*ConfigurationManager class controls access to and sharing
*of configuration settings for the application.
*@author Tatenda M. Chipeperekwa
*@date   2010-07-25
*
*/

   import javax.xml.parsers.DocumentBuilder;
   import javax.xml.parsers.DocumentBuilderFactory;
   import javax.xml.parsers.ParserConfigurationException;

   import org.w3c.dom.Document;
   import org.w3c.dom.Element;
   import org.w3c.dom.NodeList;
   import org.xml.sax.SAXException;
   
   import javax.xml.transform.Source;
   import javax.xml.transform.stream.StreamSource;
   import javax.xml.validation.*;
   import org.xml.sax.SAXException;
   import java.io.IOException;

   import java.sql.Timestamp;
   import java.util.Date;
   import java.io.BufferedWriter;
   import java.io.FileWriter;
   import java.io.File;
   import java.sql.*;
   import java.util.StringTokenizer;
   import java.util.TimeZone;
   import java.text.SimpleDateFormat;
   import java.util.List;   
   import java.util.ArrayList;
	

    public class ConfigurationManager
   {
   
      private String indexDirectory;	       		//stores the specified index directory
      private String repositoryURL;				   //URL of the repository to be harvested
      private String lastHarvest;			  			//stores the date of the last harvest    
      private Connection databaseConnection; 		//connection object for the local database	
      private String databaseUrl;						//url of the database in the server
      private String databaseUsername;				//stores the username used to access the database
      private String databasePassword;				//stores the corresponding password used to access the database
      private String databaseDriver;				//stores the driver name for the database
      private String configFileName;					//stores the name of the config file used
      private String servletUrl;						//Stores the URL of the servlet that handles the browse requests
      private List<String> stopWords;					//stores a list of stop words that are removed for ordering 
      private List<String> browseCategories;		//stores a list of categories that are enabled for browsing 
   
   
   /**
   *Default Constructor.
   */			
       public ConfigurationManager()
      {
         servletUrl="";	
         repositoryURL="";
         indexDirectory="";
         lastHarvest="";
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
   *Extracts configuration settings from the configuration file stored on disk,
   *namely config.xml or otherwise.
   *<p>
   *The configuration settings are then stored as instance variables of the 
   *ConfigurationMananger class.
   *@param configFileName full path and file name of configuration file
   *@param harvesting <code>true</code> if an harvesting operation is to be carried
   *		   out,for example during harvesting of new records from the 
   *		   central OAI repository,otherwise <code>false</code>.				
   */
     public void configureApplication ( Boolean harvesting )
     {
        try
        {
            //declare the db variables
            String dbAddress, dbUsername, dbPassword, dbDriver;
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
               // get basic values
                String repositoryURL = getXMLValue (root, "portal/repositoryURL", "http://localhost:8080/OAI-PMH/");
                String indexDirectory = getXMLValue (root, "portal/indexDirectory", "index");
//                String lastHarvest = getXMLValue (root, "portal/lastHarvest", "");
                String databaseUrl = getXMLValue (root, "portal/database/URL", "localhost/dbp");  
                String databasePassword = getXMLValue (root, "portal/database/password", "");  
                String databaseUsername = getXMLValue (root, "portal/database/username", "");
                String databaseDriver = getXMLValue (root, "repository/database/driver", "com.mysql.jdbc.Driver");
                String servletUrl = getXMLValue (root, "repository/servletUrl", "http://localhost:8080/");

                // get the browse categories
                browseCategories = new ArrayList<String>();
                Element portal = getXMLElement (root, "portal");
                NodeList listOfBrowsers = portal.getElementsByTagName("browser");
                for ( int i=0; i<listOfBrowsers.getLength (); i++ )
                   browseCategories.add (getXMLValue ((Element)listOfBrowsers.item(i), "", "title"));

                // get the last harvest date from the database
                String lastHarvest = "";
                Class.forName (databaseDriver);
                Connection databaseConnection = DriverManager.getConnection (databaseUrl, databaseUsername, databasePassword);
                Statement stm = databaseConnection.createStatement ();
                ResultSet rs = stm.executeQuery ("select value from Properties where name=\'lastHarvest'");
                if (rs.next ())
                   lastHarvest = rs.getString ("value");
                rs.close ();
                stm.close ();
                databaseConnection.close ();

                // store config if values are sensible
                if("".equals (repositoryURL) || "".equals (servletUrl) || "".equals (indexDirectory))
                {
                   System.out.println("Error Condition.\n Please provide sufficient configuration data");
                   System.out.println();
                   System.exit(0);
                }
                else
                {	
                   setRepositoryURL(repositoryURL);
                  		
                   if(harvesting)
                   {
                      setStopWords(readStopWords());				
                   }
                  	
                   //saving the configuration settings as instance variables
                   setIndexDirectory(indexDirectory); 
                   setLastHarvest(lastHarvest);
                   setDatabaseUrl(databaseUrl);
                   setDatabaseUsername(databaseUsername);
                   setDatabasePassword(databasePassword);
                   setDatabaseDriver(databaseDriver);
                   setServletUrl(servletUrl);
                }	
            }
            else
            {
                throw new IOException("Incorrect config file structure. Base Node has wrong name.");
            }
            
        }//exceptions
        catch(Exception e)
        {
           e.printStackTrace();
        }       
    }	
   
   
   /**
   *Recursively deletes the contents of the specified directory,dir.
   *<p>
   *This operation is carried out when indexing new records for the
   *Lucene search engine. The index directory has to be deleted before
   *the IndexWriter writes a new index for the search interface.
   *@param dir directory containing the Lucene index.
   *@return <code>true</code> if all deletions were successful,otherwise
     <code>false</code> if a deletion fails.
   */
       public boolean deleteDir(File dir) {
         if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
               boolean success = deleteDir(new File(dir, children[i]));
               if (!success) {
                  return false;
               }
            }
         }
      
        // The directory is now empty so delete it
         return dir.delete();
      }
   
        
       //Accessor methods for class instance variables
        
       public String getRepositoryURL()
      {
         return repositoryURL;
       
      }
       
       public String getDatabaseUsername()
      {
         return databaseUsername;
       
      }
       public String getConfigFileName()
      {
         return configFileName;
       
      }
   
       public String getDatabasePassword()
      {
         return databasePassword;
       
      }
   
       public String getDatabaseUrl()
      {
         return databaseUrl;
       
      }

       public String getDatabaseDriver()
      {
         return databaseDriver;
       
      }
       
       public String getIndexDirectory ()
      {
         return indexDirectory;
       
      }
       public String getLastHarvest ()
      {
         return lastHarvest;
       
      }
       public String getServletUrl()
      {
         return servletUrl;
      }
   
       public Connection getDatabaseConnection()
      {
         return databaseConnection ;
      }      
   
       public List<String> getStopWords()
      {
         return stopWords;
      }  
        
       public List<String> getBrowseCategories()
      {
         return	browseCategories;
      }
   
              
     //Mutator methods for class instance variables
       public void setConfigFileName(String param)
      {
         configFileName=param;
       
      } 
       public void setRepositoryURL(String param )
      {
         repositoryURL =param;
       
      }
       
       public void setDatabaseUsername(String param)
      {
         databaseUsername=param;
       
      }
   
       public void setDatabasePassword(String param)
      {
         databasePassword=param;
       
      }
   
       public void setDatabaseUrl(String param)
      {
         databaseUrl=param;
       
      }

       public void setDatabaseDriver(String param)
      {
         databaseDriver=param;
       
      }
   
       public void setLastHarvest (String param)
      {
         lastHarvest=param;
       
      }
       public void setIndexDirectory (String param)
      {
         indexDirectory=param;
       
      }
       public void setServletUrl(String param)
      {
         servletUrl=param;
      }
      
       public void setStopWords(List<String> mstopWords)
      {
         stopWords = mstopWords;
      }
       public void setBrowseCategories(List<String> mbrowseCategories)
      {
         browseCategories = mbrowseCategories;
      }
   
   /**
   *Extracts the <code>String</code> represenation of ConfigurationManager
   *instance.
   */
       public String toString()
      {
        
         return "\nRepository: "+getRepositoryURL()+"\n"+
               "Index Directory: "+getIndexDirectory()+"\n"+
            "Date of last harvest : "+getLastHarvest()+"\n";
      
                
      }
/*
*Creates a timestamp using the current system time.
*/   
       public String createLastHarvestDate()
      {
         // create an instance of Date with the current date
         Date currentDate = new Date(); 
      
         // create an instance of Timestamp using the current date from currentDate
         Timestamp currentTimeStamp = new Timestamp(currentDate.getTime());
      
         //create our date formatting tools and set the timezone to UTC time.
         SimpleDateFormat UTCDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
         TimeZone tz = TimeZone.getTimeZone("UTC");
         UTCDateFormatter.setTimeZone(tz);
      
         return UTCDateFormatter.format(currentTimeStamp);
      }
   
   /**
   *Updates the configuration data in the database after every harvest.
   */
       public void updateConfigurationFile()
      {
         try {
                Class.forName (databaseDriver);
                Connection databaseConnection = DriverManager.getConnection (databaseUrl, databaseUsername, databasePassword);
                Statement stm = databaseConnection.createStatement ();
                stm.executeUpdate ("replace into Properties values (\'lastHarvest\', \'"+getLastHarvest ()+"\')");
                stm.close ();
                databaseConnection.close ();
          } catch (Exception e) {
             
          }       
      }         
   
   /**
   *Creates a connection to the portal database when the harvesting is initiated,
   *and during searching and browsing.
   *
   */
       public void createDatabaseConnection()
      {
         try{
            				
            Class.forName (getDatabaseDriver ());
         	
         //creating connection using settings saved when configuration file was read.	
            databaseConnection = DriverManager.getConnection (getDatabaseUrl(),getDatabaseUsername() , getDatabasePassword());
         
         }
             catch(SQLException sqle)
            {
               sqle.printStackTrace();
            }
             catch(ClassNotFoundException cnf)
            {
               cnf.printStackTrace();
            }	
      
      }
   /**
   *Reads in a list of stop-words to be removed from the record title
   *when updating the portal database.
   *
   */
       private List<String> readStopWords()
      {
         List<String> stopWordsTemp = new ArrayList<String>();
         File stopWordsFile = new File("/etc/etdportal/portal/stopwords.xml");	
        
		   Document newStopWordsFile=null;
      
         //get the factory
         DocumentBuilderFactory stopWordsFactory = DocumentBuilderFactory.newInstance();
      
         //Document builder
         DocumentBuilder stopWordsBuilder=null;
      
         try
         {
         
         //Using factory get an instance of document builder
            stopWordsBuilder = stopWordsFactory.newDocumentBuilder();
         }
             catch(ParserConfigurationException pce) {
               pce.printStackTrace();
            }
      
      
         try
         {
            //parse using builder to get DOM representation of the XML file
            newStopWordsFile = stopWordsBuilder.parse(stopWordsFile.getAbsolutePath());
         
         }
             catch(SAXException se) {
               se.printStackTrace();
            }    
             catch(IOException ioe){
               ioe.printStackTrace();
            }
      
      
      
         try
         {
         //get the root elememt
            Element stopWordsRoot = newStopWordsFile.getDocumentElement();
         
         	 //get a nodelist of <word> elements
            NodeList stopWordsList = stopWordsRoot.getElementsByTagName("word");
               
         
            if(stopWordsList != null && stopWordsList.getLength() > 0)
            {
               for(int i = 0 ; i < stopWordsList.getLength();i++) 
               {
                  Element word = (Element)stopWordsList.item(i);
                  if(word.getFirstChild().getNodeValue() != null)
                  {
                     stopWordsTemp.add(word.getFirstChild().getNodeValue()+" ");
                  }
               }
            
               return stopWordsTemp;
            
            }
         } 
             catch(Exception e)//Exception condition that results from undefined elements in the "word" element
            {
               e.printStackTrace();
            
            }
         return stopWordsTemp;
      
      }
   }
