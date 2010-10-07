


/* import declarations */
import java.io.File;
import java.util.Date;
import java.sql.Timestamp;


/**
 * Class to run an OAI harvest on several different sources as described by the files in ./sources
 * @author Alexander van Olst
 * @author Lawrence Webley
 * @version 1.9.6.27
 */
public class OAIScheduledHarvest
{
    /**
     * Default constructor - will attempt to harvest any files in the sources directory
     */
    public OAIScheduledHarvest()
    {
        Settings set = new Settings ();
        File sources = new File(set.getSourceDir ()); // file object for the sources directory

        String[] files = sources.list(); // get a list of all the children of directory file

        if (files != null)
        { // if there are children of the sources directory
            for (int i = 0; i < files.length; i++)
            { // for every item in files
                if(files[i].endsWith(".xml"))
                {
                    doHarvest(set.getSourceDir () + files[i]); // do the harvest specified by the file
                }
            }
        }

    }
  
  /**
   * Runs a harvest on a given source file, as per the instructions contained within.
   * <p>
   * Depending on the last harvest date, harvest interval and harvest status, this
   * method will determine whether or not a harvest needs to be run on a given
   * repository, and if it DOES need to be done, it will do it.
   * @param sourceFile the name of the config file for the repository to be harvested.
   */
    public static void doHarvest(String sourceFile)
    {
        HarvestConfiguration thisHarvestConfig = new HarvestConfiguration(sourceFile); // create a harvest configuration for this harvest

        /* decide whether to run the harvest */
        if(thisHarvestConfig.isLoaded)
        {
            String status = thisHarvestConfig.getHarvestStatus(); // get the status string of the
            String lastDateString = thisHarvestConfig.getDateFrom();
            long harvestInterval = thisHarvestConfig.getHarvestInterval(); // get the required update interval the source
            boolean isRunning = thisHarvestConfig.isRunning(); // check whether the harvest is already running

            Timestamp lastDate = null;
            if (lastDateString != null)
                    lastDate = Timestamp.valueOf(lastDateString); // get the date/time of the last update
            else lastDate = new Timestamp (0);
            Timestamp currentDate = new Timestamp( ( new Date() ).getTime() );

            if ( ((!isRunning) && ( ( currentDate.getTime() - lastDate.getTime() > harvestInterval ) || (status.equals("Update forced") ) ) ))
            { // if the harvest should be done
                /* we now start with a harvest */
                System.out.println("Doing a harvest on " + sourceFile);
                thisHarvestConfig.setHarvestStatus("Starting Harvest...");
                thisHarvestConfig.setRunning(true); // update the isRunning variable

                try
                {
                    /* creating the request to server */
                    OAIRequest request = new OAIRequest(thisHarvestConfig);

                    thisHarvestConfig.updateDateFrom(); // update the dateFrom in the harvest file
                    System.out.println("Harvest completed. \n");
                }
                catch(Exception e)
                {
                    thisHarvestConfig.setHarvestStatus("Failed. Unexpected Error Occured. See console output for details");
                    System.err.println("Error caught in OAIHarvest.java : "+e);
                }
                finally //We never want the system to crash with a config file in the running=true state, as this would break the harvest routine
                {
                    thisHarvestConfig.setRunning(false); // the harvest has finished
                }
            }
            else
            {
                System.out.println("Skipping harvest on "+sourceFile);
            }
        }
        else
        {
              System.out.println("Aborting harvest on "+sourceFile);
        }
    }

}
