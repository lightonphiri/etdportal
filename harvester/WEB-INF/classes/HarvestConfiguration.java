


/* import declarations */
import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.sql.Timestamp;

/**
 * Class to load and store configuration details about a harvest. 
 * <p>
 * This represents the state of any harvest and will update the configuration
 * files as the harvest progresses. Uses the <code>Properties</code> object to
 * store harvest properties.
 * @author Alexander van Olst
 * @author Lawrence Webley
 * @version 1.9.6.27
 */
public class HarvestConfiguration{

  /** The name of the harvester settings file (different from the repository ones) */
  //public final static String settingsFile = "/var/lib/tomcat5.5/webapps/harvester/WEB-INF/config/config.xml";
    //DEBUG
  //public final static String settingsFile = "C:/Users/Lawrence/Documents/OAI WORK/July3Revisions/webapps/harvester/WEB-INF/config/config.xml";
  //public final static String settingsFile = "configuration.xml";

  /* Instance variables */
  /** location of settings for class */
  private String settingsFile;
  /** the name of the configuration file that is being used*/
  private String harvestFileName;
  /**  status of the harvest this configuration represents*/
  private String harvestStatus;
  /** Set name of harvest */
  private String harvestName;
  /**  url to harvest from */
  private String baseURL;
  /** the metadata format to harvest */
  private String metadataFormat;
  /** last date harvesed */
  private String dateFrom;
  /** the URL of the database to harvest to */
  private String databaseURL;
  /** the username to connect to the database with  */
  private String databaseUser;
  /** the plaintext password for the database */
  private String databasePassword;
  /** the set to harvest (this will be an empty string if no set is to be specified) */
  private String harvestSetSpec;
  /** the time between harvests (milliseconds)*/
  private long harvestInterval;
  /** whether the harvest is currently running */
  private boolean isRunning; 
  /** The current position in the harvest */
  public long cursor;
  /** The total size of the harvest (if not supported by the target repository this will be -1)*/
  public long completeListSize;
  /** A boolean to check if this config file has correctly started up.
   * True if all is good and you may use it. False if there is some erro and you should abort*/
  public boolean isLoaded;

  /**
   * Constructor to set up the instance variables and load up the harverst and database
   * properties
   * @param target the file name of the repository configuration that is to be harvested
   */
  public HarvestConfiguration(String target){ // create an instance of this object that will store the configuration of the harvest specified in target

        isLoaded = true;
	harvestFileName = target; // set the filename instance variable
        cursor = 0;
        completeListSize = -1;

        // find relative position of settings file
        settingsFile = new Settings ().getConfigDir () + "config.xml";

	loadHarvestProperties(); // load the harvest properties
	loadDatabaseProperties(); // load the database properties
  }

  /**
   * Loads the <code>xml</code> config file representing the repository settings
   * using a <code>Properties</code> object. Stores them in local private variables.
   */
  private void loadHarvestProperties(){
     Properties harvestSettings = new Properties(); // Properties object that will be used to load the harvest settings (source, metadataformats)

     FileInputStream harvestSettingsFileStream = null;   
     
     try {
       harvestSettingsFileStream = new FileInputStream(harvestFileName); // create inputstream for settingsFile
     } catch (Exception e){ // if there is an error loading the settingsFile, exit and print a message
	System.out.println("Could not load " + harvestFileName + "."); // error message
	System.out.println("Please make sure that the file exists and you are allowed access to it.");
	// we should now cancel this specific harvest
        isLoaded = false;
     }

     try {
	harvestSettings.loadFromXML(harvestSettingsFileStream); // try load the database settings from the file input stream

	/* Load all the values from the file */
    harvestName = harvestSettings.getProperty("Name");
	baseURL = harvestSettings.getProperty("baseURL");
	metadataFormat = harvestSettings.getProperty("metadataFormat");
	dateFrom = harvestSettings.getProperty("dateFrom");
	harvestStatus = harvestSettings.getProperty("harvestStatus");
	harvestInterval = Long.decode( harvestSettings.getProperty("harvestInterval") );
	isRunning = (new Boolean( harvestSettings.getProperty("isRunning") ) ).booleanValue(); // get the boolean value of the string
        harvestSetSpec = harvestSettings.getProperty("SetSpec");
	 
	harvestSettingsFileStream.close(); // close the file stream
     } catch (Exception e){ // if there is an error
	System.out.println("Could not load harvest configuration from " + harvestFileName + "."); //error message
	System.out.println("Please make sure that the file is correctly formatted.");
	// we should now cancel this specific harvest
        isLoaded = false;
     }

	

  }

  /**
   * Method to load the database properties from the configuration file settings file
   */
  private void loadDatabaseProperties(){ 

     Properties databaseSettings = new Properties(); // Properties object that will be used to load the database settings
     FileInputStream dbSettingsFileStream = null;   
     
     try {
       dbSettingsFileStream = new FileInputStream(settingsFile); // create inputstream for settingsFile
     } catch (Exception e){ // if there is an error loading the settingsFile, exit and print a message
	System.out.println("Could not load database configuration from " + settingsFile + "."); // error message
	System.out.println("Please make sure that the file exists and you are allowed access to it.");
	isLoaded = false;
     }

     try {
	databaseSettings.loadFromXML(dbSettingsFileStream); // try load the database settings from the file input stream

	/* Load all the values from the file */	
	databaseURL = databaseSettings.getProperty("databaseURL");
	databaseUser = databaseSettings.getProperty("databaseUsername");
	databasePassword = databaseSettings.getProperty("databasePassword");

        dbSettingsFileStream.close(); // close the file stream
     } catch (Exception e){ // if there is an error
	System.out.println("Could not load database configuration from " + settingsFile + "."); //error message
	System.out.println("Please make sure that the file is correctly formatted.");
	isLoaded = false;
     }


  }

  /**
   * Save the harvest configuration back to the original file
   */
  private void saveHarvestConfiguration(){
	Properties harvestSettings = new Properties (); // create the properties object that will be saved to file
	
	/* set the properties in the harvestSettings object to the values stored in this configuration object */
    harvestSettings.setProperty("Name", harvestName);
	harvestSettings.setProperty("baseURL",baseURL);
	harvestSettings.setProperty("metadataFormat",metadataFormat);
	if (dateFrom != null) harvestSettings.setProperty("dateFrom",dateFrom); // if there is a dateFrom we add it
	harvestSettings.setProperty("harvestStatus",harvestStatus);
	harvestSettings.setProperty("harvestInterval",String.valueOf(harvestInterval));	
	harvestSettings.setProperty("isRunning",String.valueOf(isRunning) );
	try {
		FileOutputStream out = new FileOutputStream(harvestFileName); // create the file output stream
		harvestSettings.storeToXML(out, "OAI Metadata Harvester Settings File ");
		out.close();
	} catch (Exception e){ // if there is an error 
		System.out.println("Unable to save harvest settings file " + harvestFileName + ".");
		System.out.println("The web interface data may now be out of date.");
	}

  }


  /* Accessor and Mutator methods */

  /**
   * Sets the status of the harvest. After setting the variable, this method will
   * save the changes back to file so that the web front can view changes.
   * @param newStatus the new status of the harvest
   */
  public void setHarvestStatus ( String newStatus ){ // change the status of the harvest 
	harvestStatus = newStatus;
	
	saveHarvestConfiguration(); // saves the configuration to the config file
  }


  /**
   * Updates the <code>dateFrom</code> in the repository config file to the current
   * date and time.
   */
  public void updateDateFrom(){ // update the dateFrom in the properties and save the file
	Timestamp currentDate = new Timestamp( ( new Date() ).getTime() ); // get the current timestamp
	dateFrom = currentDate.toString();	// convert to a string
	saveHarvestConfiguration(); // save this to the file
  }

  /**
   * Sets whether a harvest is currently running on the given config file.
   * @param newValue whether the harvest is currently running.
   */
  public void setRunning(boolean newValue){ // set the value of is running
	isRunning = newValue; // update the value
	saveHarvestConfiguration();
  }
  /**
   * Returns whether or not the harvest file has specified a set to harvest
   * @return true if there is a set spec
   */
  public boolean hasSetSpec()
  {
      if(harvestSetSpec == null || harvestSetSpec.equals("") == true)
      {
          return false;
      }
      else
      {
          return true;
      }
  }

  /**
   * Gets the set spec to harvest
   * @return the set spec
   */
  public String getSetSpec()
  {
      return harvestSetSpec;
  }
  

  /**
   * Accessor method to get the url of the repository to be harvested.
   * @return the base Url of the repository to be harvested.
   */
  public String getBaseURL(){
	return baseURL;
  }

/**
 * Accessor method to get the type of metadata that is to be harvested.
 * @return the metadata format that is to be harvested
 */
  public String getMetadataFormat(){
	return metadataFormat;
  }

  /**
   * Accessor method to get the date of the last harvest.
   * @return the last date harvested
   */
  public String getDateFrom(){
	return dateFrom;
  }

  /**
   * Accessor method to get the status of the current (or last to occur) harvest
   * on this repository.
   * @return the harvest status
   */
  public String getHarvestStatus(){
	return harvestStatus;
  }

  /**
   * Returns whether or not a harvest is currently being run on this repository.
   * @return true if a harvest is currently being run on this repository. False otherwise.
   */
  public boolean isRunning(){
	return isRunning;
  }

  /**
   * Gets the url of the database in which we will store the records.
   * @return the database url.
   */
  public String getDatabaseURL(){
	return databaseURL;
  }

  /**
   * Gets the database username
   * @return the username used to log in to the database
   */
  public String getDatabaseUser(){
	return databaseUser;
  }

  /**
   * Gets the database password
   * @return the password corresponding the username used to log in to the database
   */
  public String getDatabasePassword(){
	return databasePassword;
  }

  /**
   * Gets the interval time between harvests.
   * @return the interval time between harvests.
   */
  public long getHarvestInterval(){
	return harvestInterval;
  }

  /**
   * Gets the name of the harvest (used as set name).
   * @return the name of the harvest.
   */
  public String getName()
  {
      return harvestName;
  }
  
}
