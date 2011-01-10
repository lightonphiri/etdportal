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
  /** whether the harvest is currently running */
  private int running;
  /** The current position in the harvest */
  public long cursor;
  /** The total size of the harvest (if not supported by the target repository this will be -1)*/
  public long completeListSize;
  /** A boolean to check if this config file has correctly started up.
   * True if all is good and you may use it. False if there is some erro and you should abort*/
  public boolean isLoaded;

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

      conf = aConf;
      load (); // load the repository properties
   }
   
   // Constructor for new harvest entry
   public Repository ( Config aConf, String anID, String aName, String aBaseURL, String aMetadataFormat, String aSetSpec, String aDateFrom, long aHarvestInterval )
   {
      ID = anID;
      name = aName;
      baseURL = aBaseURL;
      metadataFormat = aMetadataFormat;
      setSpec = aSetSpec;
      dateFrom = aDateFrom;
      harvestInterval = aHarvestInterval;
      
      cursor = 0;
      completeListSize = -1;
      running = 0;
      harvestStatus = "Not yet harvested";
      
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
            System.out.println ("Cannot load repository details");
         }   
      }
      else
      {
         System.out.println ("Cannot load database");
         isLoaded = false;
      }
   }
      
  /**
   * Save the repository configuration back to the original file
   */
   private void save () 
   {
      Database db = new Database (conf);
      if (db.connect ())
      {
         if (! db.saveRepository (this))
            System.out.println ("Cannot save repository details");
      }
      else
         System.out.println ("Cannot load database");
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
      Timestamp currentDate = new Timestamp( ( new Date() ).getTime() ); // get the current timestamp
      dateFrom = currentDate.toString();	// convert to a string
      save (); // save this to the file
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
      save (); // saves the configuration to the config file
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
      save ();
   }
}
