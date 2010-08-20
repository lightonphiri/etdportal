/**
*ConfigurationManager class controls access to and sharing
*of configuration settings for the application.
*<p>
*These settings are read in from a configuration file that
*contains information regarding:
*<ul>
*	<li>Portal database username
*	<li>Portal database password
*	<li>Central Java Servelet OAI Repository URL
*	<li>Portal Java Servelet URL
*</ul>
*<p>
*@author Tatenda M. Chipeperekwa
*@date   17/12/09
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
   import java.util.TimeZone;
   import java.text.SimpleDateFormat;
	

public class ConfigurationManager
{

    private String indexDirectory;	       		//stores the specified index directory
    private String repositoryURL;				//URL of the repository to be harvested
    private String lastHarvest;				//stores the date of the last harvest    
    private Connection databaseConnection; 		//connection object for the local database	
    private String databaseUrl;				//url of the database in the server
    private String databaseUsername;			//stores the username used to access the database
    private String databasePassword;			//stores the corresponding password used to access the database
    private String configFileName;				//stores the name of the config file used
    private String servletUrl;				//Stores the URL of the servlet that handles the browse requests
	
/**
*Default Constructor.
*
*/			
    public ConfigurationManager()
    {
	  servletUrl="";	
         repositoryURL="";
	  indexDirectory="";
	  lastHarvest="";
    }
    

/**
*Extracts configuration settings from the configuration file stored on disk,
*namely config.xml or otherwise.
*<p>
*The configuration settings are then stored as instance variables of the 
*ConfigurationMananger class.
*@param configFileName full path and file name of configuration file
*@param indexing <code>true</code> if an indexing operation is to be carried
*		   out,for example during harvesting of new records from the 
*		   central OAI repository,otherwise <code>false</code>.				
*/
   public void configureApplication(String configFileName,Boolean indexing)
   {

	  File configFile = new File(configFileName);	
	  setConfigFileName(configFileName);//saving the name of the configuration file name

	  //stores the configuration information passed to the program
         Document newConfigFile=null;
	   
         //get the factory
         DocumentBuilderFactory configFactory = DocumentBuilderFactory.newInstance();
      
	  //Document builder
         DocumentBuilder configBuilder=null;


	  
	   try
          {
	
		  //Using factory get an instance of document builder
	         configBuilder = configFactory.newDocumentBuilder();
	   }
             catch(ParserConfigurationException pce) {
               pce.printStackTrace();
          }


          try
          {
		 //parse using builder to get DOM representation of the XML file
        	 newConfigFile = configBuilder.parse(configFile.getAbsolutePath());

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
        	 Element configRoot = newConfigFile.getDocumentElement();
      
         	 //get a nodelist of <configFile> elements
         	 NodeList configList = configRoot.getElementsByTagName("configFile");
               

         		if(configList != null && configList.getLength() > 0)
         		{
           		 	for(int i = 0 ; i < configList.getLength();i++) 
            			{
            
               			   //get the configFile element
                           		   Element configElement = (Element)configList.item(i);
            
         	 			  //extracting the fields to set Configuration settings
                 			  String repositoryURL= getTagValue(configElement,"repositoryURL");
					  String indexDirectory=getTagValue(configElement,"indexDirectory");
  					  String lastHarvest=getTagValue(configElement,"lastHarvest");
					  String databaseUrl=getTagValue(configElement,"databaseUrl");  
					  String databasePassword=getTagValue(configElement,"databasePassword");  
					  String databaseUsername=getTagValue(configElement,"databaseUsername");
					  String servletUrl=getTagValue(configElement,"servletUrl");
 	  
	
						
					    if(repositoryURL.equals("Not Available")|| servletUrl.equals("Not Available")||indexDirectory.equals("Not Available"))
					    {
							System.out.println("Error Condition.\n Please provide sufficient configuration data");
							System.out.println();
							System.exit(0);
					    }else
					    {	
							setRepositoryURL(repositoryURL);
							
							
							if(indexing)
							{
							    deleteDir(new File(indexDirectory));	
							    
							    //setIndexDirectory(indexDirectory);
							    System.out.println("Deleted");
							}
							
							//saving the configuration settings as instance variables
							setIndexDirectory(indexDirectory); 
							setLastHarvest(lastHarvest);
							setDatabaseUrl(databaseUrl);
							setDatabaseUsername(databaseUsername);
							setDatabasePassword(databasePassword);
							setServletUrl(servletUrl);
							
					    }	
		    
            			}
        	 	}else
	  			{
					System.out.println("Error Condition.\nNo data sources specified in configuration file.\n");
					System.exit(0);

	 			}
           }  catch(Exception e)//Exception condition that results from undefined elements in the configFile element
	    {
				e.printStackTrace();
			
	    }	

   	}	
	

	
/**
*Extracts the tag value for elements in a <code>Document</code> tree.
*@param recordElement <code>Element</code> representing the the tag in 
*			the XML file that is to be extracted.
*@param tagName the textual name of the tag to be extracted.
*@return tagValue <code>String</code> representation of tag.
*/
	private String getTagValue(Element recordElement, String tagName)
	{
		String tagValue="";//temporary variable for tag field
		try{
				  NodeList tagList = recordElement.getElementsByTagName(tagName);
				 
         			  if(tagList != null && tagList.getLength() > 0) 
				  {
             				  Element tag = (Element)tagList.item(0);
			                	if(tag.getFirstChild().getNodeValue() != null)
						{
							tagValue=tag.getFirstChild().getNodeValue();
						       return tagValue;
						}
						else{return "";}
         			  }else
				  {return "";}	
		}catch(Exception e)
		{	//some config files might have a tag but the tag might not contain any value
			//Uncomment the next line to view tag without value
			//System.out.println("Cannot extract value for: " + tagName);
			return "Not Available.";	
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

/**
*Updates the date of the last harvest by adding a datestamp of the
*current date and time.
*
*/
	public void updateConfigurationFile()
	{	
		// create an instance of Date with the current date
        	Date currentDate = new Date(); 
	
		// create an instance of Timestamp using the current date from currentDate
       	        Timestamp currentTimeStamp = new Timestamp(currentDate.getTime());
		
		//create our date formatting tools and set the timezone to UTC time.
		SimpleDateFormat UTCDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
		TimeZone tz = TimeZone.getTimeZone("UTC");
		UTCDateFormatter.setTimeZone(tz);
		
		//updating the time of the last harvest to the current harvest
		setLastHarvest(UTCDateFormatter.format(currentTimeStamp));
		
		String newConfigFile=
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<configData>\n"+
    		"	<configFile>\n"+
               "		<repositoryURL>"+getRepositoryURL()+"</repositoryURL>\n"+
 	 	"		<indexDirectory>"+getIndexDirectory()+"</indexDirectory>\n"+
	 	"		<lastHarvest>"+getLastHarvest()+"</lastHarvest>\n"+
		"		<databaseUrl>"+getDatabaseUrl()+"</databaseUrl>\n"+
		"		<databaseUsername>"+getDatabaseUsername()+"</databaseUsername>\n"+
              "		<databasePassword>"+getDatabasePassword()+"</databasePassword>\n"+
              "		<servletUrl>"+getServletUrl()+"</servletUrl>\n"+	
    		"	</configFile>\n"+
		"</configData>\n";

		//stores a copy of the contents of the config file for use with the browsing interface
		String userIntefaceConfig=newConfigFile;

		    try {
				//deleteIndexDirectory(new File("../config/confg.xml"));
        			BufferedWriter configOutput = new BufferedWriter(new FileWriter("../config/config.xml"));
        			configOutput.write(newConfigFile);
        			configOutput.close();
   		         } catch (IOException e) {
				System.out.println("Error Condition.\n Error writing out to config file");		
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
	         				
         	Class.forName ("com.mysql.jdbc.Driver");
         	
		//creating connection using settings saved when configuration file was read.	
	      	databaseConnection = DriverManager.getConnection (getDatabaseUrl(),getDatabaseUsername() , getDatabasePassword());

 	     }catch(SQLException sqle)
	    {
		sqle.printStackTrace();
	    }catch(ClassNotFoundException cnf)
	    {
		cnf.printStackTrace();
	    }	
	
       }
        


}
