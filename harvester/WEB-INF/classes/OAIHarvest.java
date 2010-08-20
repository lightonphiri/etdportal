
/** OAI Harvest Runner - A driver class to run a harvest of OAI Metadata from different sources
 * @author Alexander van Olst
 * @author Lawrence Webley
 * @version 1.9.6.25
 */
public class OAIHarvest
{
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
        if(args.length == 0)
        {//checks all config files and determines whether to run them
            System.out.println("OAI Harvest Runner Starting... \n");
            OAIScheduledHarvest currentHarvest = new OAIScheduledHarvest(); // run a new harvest
        }
        else
        {//iff there are command line args, try to run harvests on those.
            for(int i = 0; i < args.length;i++)
            {
                System.out.println("Harvesting "+args[i]);
                OAIScheduledHarvest.doHarvest(args[i]);
            }
        }
    }
	 
}
