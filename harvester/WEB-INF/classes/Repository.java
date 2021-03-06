import java.util.StringTokenizer;
import java.util.Date;
import java.sql.Timestamp;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * Class to load and store configuration details about a repository/harvest. 
 * <p>
 * This represents the state of any harvest and will update the configuration
 * files as the harvest progresses. Uses the <code>Properties</code> object to
 * store harvest properties.
 * @author Alexander van Olst
 * @author Lawrence Webley
 * @author Hussein Suleman
 * @version 1.9.6.27
 */
public class Repository
{
  /** settings for class */
  private Config conf;
  /**  status of the harvest this configuration represents*/
  private String harvestStatus;
  /**  Id of repository */
  private String ID;
  /**  name of repository */
  private String name;
  /**  url to harvest from */
  private String baseURL;
  /** the metadata format to harvest */
  private String metadataFormat;
  /** last date harvested */
  private String dateFrom;
  /** the set to harvest (this will be an empty string if no set is to be specified) */
  private String setSpec;
  /** the time between harvests (milliseconds)*/
  private long harvestInterval;
  /** the timeout for requests  (milliseconds)*/
  private long timeout;
  /** whether the harvest is currently running */
  private int running;
  /** The current position in the harvest */
  public long cursor;
  /** The total size of the harvest (if not supported by the target repository this will be -1)*/
  public long completeListSize;
  /** A boolean to check if this config file has correctly started up.
   * True if all is good and you may use it. False if there is some erro and you should abort*/
  public boolean isLoaded;
  private int numberOfRecords;
  private Timestamp startDate;
  private String resumptionToken;
  

  /**
   * Constructor to set up the instance variables and load up the harvest and database
   * properties
   * @param target the file name of the repository configuration that is to be harvested
   */
   public Repository ( Config aConf, String targetID )
   {
      ID = targetID;
      isLoaded = true;
      cursor = 0;
      completeListSize = -1;

      name = "";
      baseURL = "";
      metadataFormat = "";
      setSpec = "";
      dateFrom = "";
      harvestInterval = 3000;
      timeout = 180000;
      running = 0;
      harvestStatus = "";
      resumptionToken = "";

      conf = aConf;
      
      startDate = new Timestamp( ( new Date() ).getTime() ); // get the current timestamp
      
      load (); // load the repository properties
   }
   
   // constructor for a new repository to be added
   public Repository ( Config aConf )
   {
      ID = "";
      name = "";
      baseURL = "";
      metadataFormat = "";
      setSpec = "";
      dateFrom = "";
      harvestInterval = 3000;
      timeout = 180000;
      running = 0;
      harvestStatus = "";
      resumptionToken = "";
      
      startDate = new Timestamp( ( new Date() ).getTime() ); // get the current timestamp

      conf = aConf;
   }

   // copy constructor
   public Repository ( Repository rep )
   {
      ID = rep.ID;
      name = rep.name;
      baseURL = rep.baseURL;
      metadataFormat = rep.metadataFormat;
      setSpec = rep.setSpec;
      dateFrom = rep.dateFrom;
      harvestInterval = rep.harvestInterval;
      timeout = rep.timeout;
      running = rep.running;
      harvestStatus = rep.harvestStatus;      
      startDate = rep.startDate;
      resumptionToken = rep.resumptionToken;
      
      conf = rep.conf;
   }
   
   // Constructor for new harvest entry
   public Repository ( Config aConf, String anID, String aName, String aBaseURL, String aMetadataFormat, 
                       String aSetSpec, String aDateFrom, long aHarvestInterval, long aTimeout, int aRunning, 
                       String aHarvestStatus, String aResumptionToken )
   {
      ID = anID;
      name = aName;
      baseURL = aBaseURL;
      metadataFormat = aMetadataFormat;
      setSpec = aSetSpec;
      dateFrom = aDateFrom;
      harvestInterval = aHarvestInterval;
      timeout = aTimeout;
      running = aRunning;
      harvestStatus = aHarvestStatus;
      resumptionToken = aResumptionToken;
      
      startDate = new Timestamp( ( new Date() ).getTime() ); // get the current timestamp

      cursor = 0;
      completeListSize = -1;
      conf = aConf;
   }

  /**
   * Loads the <code>xml</code> config file representing the repository settings
   * using a <code>Properties</code> object. Stores them in local private variables.
   */
   private void load ()
   {
      Database db = new Database (conf);
      if (db.connect ())
      {
         if (! db.loadRepository (this))
         {
            isLoaded = false;
            conf.log.add("Cannot load repository details", "Cannot load repository details");
         }   
      }
      else
      {
         conf.log.add("Cannot load database", "Cannot load database");
         isLoaded = false;
      }
   }
      
  /**
   * Save the repository configuration back to the original file
   */
   public void save () 
   {
      Database db = new Database (conf);
      if (db.connect ())
      {
         if (! db.saveRepository (this))
            conf.log.add("Cannot save repository details", "Cannot save repository details");
      }
      else
         conf.log.add("Cannot load database","Cannot load database");
   }
   
   /*
    * Delete the repository and its data
    */
   public void delete () 
   {
      Database db = new Database (conf);
      if (db.connect ())
      {
         if (! db.deleteRepository (this))
            conf.log.add("Cannot delete repository", "Cannot delete repository");
      }
      else
         conf.log.add("Cannot load database","Cannot load database");
   }

   /**
    * Gets the name of the harvest (used as set spec).
    * @return the name of the harvest.
    */
   public String getID ()
   {
      return ID;
   }
   /**
    * Sets the name of the harvest (used as set spec).
    * @return the name of the harvest.
    */
   public void setID ( String anID )
   {
      ID = anID;
   }

   /**
    * Gets the name of the harvest (used as set name).
    * @return the name of the harvest.
    */
   public String getName ()
   {
      return name;
   }
   /**
    * Sets the name of the harvest (used as set name).
    * @return the name of the harvest.
    */
   public void setName ( String aName )
   {
      name = aName;
   }

   /**
    * Get the url of the repository to be harvested.
    * @return the base Url of the repository to be harvested.
    */
   public String getBaseURL ()
   {
      return baseURL;
   }
   /**
    * Set the url of the repository to be harvested.
    * @return the base Url of the repository to be harvested.
    */
   public void setBaseURL ( String aBaseURL )
   {
      baseURL = aBaseURL;
   }

   /**
    * Get the type of metadata that is to be harvested.
    * @return the metadata format that is to be harvested
    */
   public String getMetadataFormat ()
   {
      return metadataFormat;
   }
   /**
    * Set the type of metadata that is to be harvested.
    * @return the metadata format that is to be harvested
    */
   public void setMetadataFormat ( String aMetadataFormat )
   {
      metadataFormat = aMetadataFormat;
   }
   
   public void getNameSpace()
   {
   		
   }

   /**
    * Gets the set spec to harvest
    * @return the set spec
    */
   public String getSetSpec ()
   {
      return setSpec;
   }
   /**
    * Sets the set spec to harvest
    * @return the set spec
    */
   public void setSetSpec ( String aSetSpec )
   {
      setSpec = aSetSpec;
   }

   /**
    * Get the date of the last harvest.
    * @return the last date harvested
    */
   public String getDateFrom ()
   {
      return dateFrom;
   }
   /**
    * Set the date of the last harvest.
    * @return the last date harvested
    */
   public void setDateFrom ( String aDateFrom )
   {
      dateFrom = aDateFrom;
   }
   /**
    * Updates the dateFrom in the repository config file to the current
    * date and time.
    */
   public void updateDateFrom ()
   {
      if (cursor == 0)  // do not update date if there were no records
         return;
//      Timestamp currentDate = new Timestamp( ( new Date() ).getTime() ); // get the current timestamp
      dateFrom = startDate.toString();	// convert to a string
//      save (); // save this to the file
      Database db = new Database (conf);
      if (db.connect ())
      {
         db.updateDateFrom (this);
      }
      else
         conf.log.add("Cannot load database","Cannot load database");
   }

   /**
    * Gets the interval time between harvests.
    * @return the interval time between harvests.
    */
   public long getHarvestInterval ()
   {
      return harvestInterval;
   }
   /**
    * Sets the interval time between harvests.
    * @return the interval time between harvests.
    */
   public void setHarvestInterval ( long aHarvestInterval )
   {
      harvestInterval = aHarvestInterval;
   }

   /**
    * Gets the timeout value.
    * @return the timeout value.
    */
   public long getTimeout ()
   {
      return timeout;
   }
   /**
    * Sets the timeout value.
    * @return the timeout value.
    */
   public void setTimeout ( long aTimeout )
   {
      timeout = aTimeout;
   }
   
   /**
    * Get the status of the current (or last to occur) harvest
    * on this repository.
    * @return the harvest status
    */
   public String getHarvestStatus ()
   {
      return harvestStatus;
   }
   /**
    * Set the status of the current (or last to occur) harvest
    * on this repository.
    * @return the harvest status
    */
   public void setHarvestStatus ( String aHarvestStatus )
   {
      harvestStatus = aHarvestStatus;
   }
   /**
    * Sets the status of the harvest. After setting the variable, this method will
    * save the changes back to file so that the web front can view changes.
    * @param newStatus the new status of the harvest
    */
   public void updateHarvestStatus ( String aHarvestStatus )
   {
      setHarvestStatus (aHarvestStatus);
//      save (); // saves the configuration to the config file
      Database db = new Database (conf);
      if (db.connect ())
      {
         db.updateHarvestStatus (this);
      }
      else
         conf.log.add("Cannot load database","Cannot load database");
   }

   /**
    * Returns whether or not a harvest is currently being run on this repository.
    * @return true if a harvest is currently being run on this repository. False otherwise.
    */
   public int getRunning ()
   {
      return running;
   }
   /**
    * Sets whether or not a harvest is currently being run on this repository.
    * @return true if a harvest is currently being run on this repository. False otherwise.
    */
   public void setRunning ( int aRunning )
   {
      running = aRunning;
   }
   /**
    * Sets whether a harvest is currently running on the given repository.
    * @param newValue whether the harvest is currently running.
    */
   public void updateRunning ( int aRunning )
   {
      setRunning (aRunning);
//      save ();
      Database db = new Database (conf);
      if (db.connect ())
      {
         db.updateRunning (this);
      }
      else
         conf.log.add("Cannot load database","Cannot load database");
   }

   /**
    * Update pre-calculated row count
    */
   public void updateCounts ()
   {
      Database db = new Database (conf);
      if (db.connect ())
      {
         db.updateCounts (this);
      }
      else
         conf.log.add("Cannot load database","Cannot load database");
   }
   
   public int getNumberOfRecords ()
   {
      return numberOfRecords;
   }
   public void setNumberOfRecords ( int numOfRecords )
   {
      numberOfRecords = numOfRecords;
   }
   
   public String getResumptionToken ()
   {
      return resumptionToken;
   }
   public void setResumptionToken ( String res )
   {
      resumptionToken = res;
   }
   public void updateResumptionToken ( String res )
   {
      setResumptionToken (res);
      Database db = new Database (conf);
      if (db.connect ())
      {
         db.updateResumptionToken (this);
      }
      else
         conf.log.add("Cannot load database","Cannot load database");
   }
}

