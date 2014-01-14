import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.sql.Timestamp;

/** OAI Harvest - A driver class to run a harvest of OAI Metadata from different sources
 * @author Alexander van Olst
 * @author Lawrence Webley
 * @author Hussein Suleman
 * @version 1.9.6.25
 */
public class OAIHarvest
{
   Config conf;

   /**
    * Main method that (if the <code>args</code> is empty) will scan the sources directory
    * and check each harvest file to see if it needs harvesting. If it
    * does need harvesting, it will execute a harvest on that file.
    * <p>
    * If the <code>args</code> is not empty, then the program will try to harvest
    * each of the files specified (the arguments are expected to be repository
    * xml config files).
    * @param args the list of repository config files to be harvested.
    */
   public static void main (String[] args)
   {
      Config conf = new Config ();
    
      if (args.length == 0)
      { // checks database for repository list and harvests all
         Database db = new Database (conf);
         if (db.connect ())
         {
            String[] repoList = db.getRepositoryIDList ();
            new OAIHarvest(conf, repoList);
         }
      }
      else
      { // iff there are command line args, try to run harvests on those.
         new OAIHarvest (conf, args);
      }
   }

   /**
    * Constructor - will attempt to harvest all repositories in list
    */
   public OAIHarvest ( Config aConf, String [] args )
   {
      conf = aConf;
      for ( int i=0; i<args.length; i++ )
         doHarvest (args[i]);
   }

   /**
    * Runs a harvest on a given source repository
    * <p>
    * Depending on the last harvest date, harvest interval and harvest status, this
    * method will determine whether or not a harvest needs to be run on a given
    * repository, and if it DOES need to be done, it will do it.
    * @param sourceFile the name of the config file for the repository to be harvested.
    */
   public void doHarvest ( String repositoryName )
   {
      conf.openLogFile ("."+repositoryName);
      conf.log.add("Checking "+repositoryName+" to see if it needs harvesting....");

      Repository rep = new Repository (conf, repositoryName);
   
      /* decide whether to run the harvest */
      if (rep.isLoaded)
      {
         String status = rep.getHarvestStatus(); // get the status string of the
         String lastDateString = rep.getDateFrom();
         long harvestInterval = rep.getHarvestInterval(); // get the required update interval the source
         int isRunning = rep.getRunning(); // check whether the harvest is already running

         Timestamp lastDate = null;
         if (! "".equals (lastDateString))
            lastDate = Timestamp.valueOf(lastDateString); // get the date/time of the last update
         else 
            lastDate = new Timestamp (0);
         Timestamp currentDate = new Timestamp( ( new Date() ).getTime() );

         if ( ((isRunning == 0) && (! rep.getBaseURL ().equals ("")) && ( ( currentDate.getTime() - lastDate.getTime() > (harvestInterval*1000) ) || ("Update forced".equals(status) ) ) ))
         { // if the harvest should be done
            /* we now start with a harvest */
            conf.log.add("Harvesting: " + repositoryName + " @ " + rep.getBaseURL ());
            rep.updateHarvestStatus ("Starting harvest");
            rep.updateRunning(1); // update the isRunning variable

            try
            {
            	/* backup the original repository settings */
            	String metadataFormat = rep.getMetadataFormat();
            	String setInfo = rep.getSetSpec();
            	
               /* creates list of metadataPrefixes */
               ArrayList<String> metadataPrefixes = new ArrayList<String>();
               StringTokenizer m_st = new StringTokenizer (rep.getMetadataFormat (), ", ");
               while (m_st.hasMoreTokens ())
                  metadataPrefixes.add (m_st.nextToken ());
               // no idea why this was here in the first place!
               //if (metadataPrefixes.size () == 0)
               //   metadataPrefixes.add ("");

               /* creates list of sets */
               ArrayList<String> sets = new ArrayList<String>();
               StringTokenizer s_st = new StringTokenizer (rep.getSetSpec (), ", ");
               while (s_st.hasMoreTokens ())
                  sets.add (s_st.nextToken ());
               if (sets.size () == 0)
                  sets.add ("");
               
               /* create one request to the server for each mdp/set pair */
               for ( int m = 0; m<metadataPrefixes.size(); m++ )
                  for ( int s = 0; s<sets.size(); s++ )
                  {
                     rep.setMetadataFormat (metadataPrefixes.get (m));
                     rep.setSetSpec (sets.get (s));
                     OAIRequest request = new OAIRequest (conf, rep);
                  }
                  
               //Now restore the original properties we had
               rep.setMetadataFormat(metadataFormat);
               rep.setSetSpec(setInfo);

               rep.updateDateFrom(); // update the dateFrom in the harvest file
               conf.log.add("Harvest completed on "+repositoryName);
               
               //remove the temp resumptionToken
               if (! ("".equals (rep.getResumptionToken ())))
                  rep.updateResumptionToken ("");
            } catch(Exception e) {
               rep.updateHarvestStatus ("Failed connecting to baseURL");
               conf.log.add("Error caught during harvest on "+repositoryName+": "+e,
                       "Error caught during harvest on "+repositoryName+": "+e);
            } finally {
               rep.updateRunning(0); // the harvest has finished
               rep.updateCounts();
            }
         }
         else
         {
            conf.log.add("Skipping harvest on " + repositoryName);            
         }
      }
      else
      {
         conf.log.add("Harvest configuration not loaded for " + repositoryName);
      }
   }
}
