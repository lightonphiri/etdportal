


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;


/**
 * A Class to save and/or edit the repository config files.
 * <p>
 * Uses the <code>Properties</code> class to store and read the individual
 * repository settings in xml files.
 * @author Lawrence Webley
 */
public class ConfigWriter
{
    /**
     * Saves data from a <code>HttpServletRequest</code> to an <code>Xml</code> file.
     * <p>
     * Given a <code>HttpServletRequest</code> containing <code>name, url, meta
     * </code> and <code>interval</code> parameters, this method will create a
     * <code>Properties</code> object and then save this to an xml file named after
     * the <code>name</code> parameter.
     * @param request the http request that contains the save info
     * @return a boolean specifing whether the save operation was a success or not.
     */
    public static boolean saveConfig(HttpServletRequest request)
    {
        Properties harvestSettings = new Properties (); // create the properties object that will be saved to file

        boolean success;

        //now try to get all the properties
        String name = request.getParameter("name");
        String baseURL = request.getParameter("url");
        String metadataFormat = request.getParameter("meta");
        String comment = "Created by Harvester Control Panel";
        String interval = request.getParameter("interval");


        harvestSettings.setProperty("Name", name);
        harvestSettings.setProperty("baseURL",baseURL);
        harvestSettings.setProperty("metadataFormat",metadataFormat);
        harvestSettings.setProperty("dateFrom","1970-01-01 00:00:00.000");
        harvestSettings.setProperty("harvestStatus","No harvest has yet been run.");
        harvestSettings.setProperty("harvestInterval",interval);
        harvestSettings.setProperty("isRunning","false" );

        try{
            
            FileOutputStream out = new FileOutputStream(Settings.sourceDir+name+".xml");
            harvestSettings.storeToXML(out, comment);
            out.close();
            success = true;
        }catch(IOException e)
        {
            System.err.println("Error Saving config file!");
            success = false;
        }
        return success;
    }

    /**
     * Resets the date variable of a repository config file.
     * <p>
     * Given a file name for a <code>xml</code> file containing a repositories
     * configuration, this method will open it using a <code>Properties</code>
     * object and then reset its last update date to 1970-01-01 00:00:00.000. It
     * will then also set the harvest status to "Update forced", meaning the next
     * time the harvester has a look at it, it will harvest it regardless of other
     * considerations. This <code>Properties</code> object will then be saved to
     * <code>Xml</code> over the original file.
     * @param fileName the name of the config file to be edited
     * @return a boolean specifying whether or not the operation was a success.
     */
    public static boolean resetDate(String fileName)
    {
        boolean success;
        Properties harvestSettings = new Properties ();

        try
        {
            success = true;
            //read config file            
            FileInputStream harvestSettingsFileStream = new FileInputStream(Settings.sourceDir+fileName);
            harvestSettings.loadFromXML(harvestSettingsFileStream);
            //reset date
            harvestSettings.setProperty("dateFrom","1970-01-01 00:00:00.000");
            harvestSettings.setProperty("harvestStatus", "Update forced");
            //save
            
            FileOutputStream out = new FileOutputStream(Settings.sourceDir+fileName); // create the file output stream
            harvestSettings.storeToXML(out, "Updated by Harvester Control Panel");
            out.close();
        }
        catch(IOException e)
        {
            success = false;
        }


        return success;
    }
}
