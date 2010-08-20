
import java.io.FileInputStream;
import java.util.Properties;

/**
 *
 * @author Lawrence Webley
 */
public class StatusPane extends HtmlPane
{
    public StatusPane()
    {
        super();
    }

    public StringBuffer getStatusPane()
    {
        output.append("     <div id=\"main\">\n");
        output.append("         <h2>Status</h2>\n");
        output.append("         <p>Click a repository to check its status -></p>\n");
        output.append("     </div>\n");

        return output;
    }

    public StringBuffer getFileStatusPane(String fileName)
    {
        //returns the status of the repo. Reads XML from file
         output.append("     <div id=\"main\">\n");

         Properties harvestSettings = new Properties(); // Properties object that will be used to load the harvest settings (source, metadataformats)

         FileInputStream harvestSettingsFileStream = null;

         try {
           harvestSettingsFileStream = new FileInputStream("/var/lib/tomcat5.5/webapps/harvester/WEB-INF/config/sources/"+fileName); // create inputstream for settingsFile
         } catch (Exception e){ // if there is an error loading the settingsFile, exit and print a message
            System.out.println("Could not load database configuration from " + fileName + "."); // error message
            System.out.println("Please make sure that the file exists and you are allowed access to it.");
            // we should now cancel this specific harvest
         }

         try {
            harvestSettings.loadFromXML(harvestSettingsFileStream); // try load the database settings from the file input stream

            //get file settings
            String baseURL = harvestSettings.getProperty("baseURL");
            String metadataFormat = harvestSettings.getProperty("metadataFormat");
            String dateFrom = harvestSettings.getProperty("dateFrom");
            String harvestStatus = harvestSettings.getProperty("harvestStatus");
            long harvestInterval = Long.decode( harvestSettings.getProperty("harvestInterval") );
            boolean isRunning = (new Boolean( harvestSettings.getProperty("isRunning") ) ).booleanValue(); // get the boolean value of the string

            //Write to html
            output.append("         <h3>Status of "+baseURL+"</h3>\n");
            output.append("         <p>Metadata Format Harvested: "+metadataFormat+"</p>\n");
            output.append("         <p>Last Harvest Date: "+dateFrom+"</p>\n");
            output.append("         <p>Harvest Status: "+harvestStatus+"</p>\n");
            output.append("         <p>Harvest interval: "+harvestInterval+"</p>\n");
            if(isRunning)
            {
                output.append("         <p>A Harvest is currently being run.</p>\n");
            }
            else
            {
                output.append("         <p>This Repository is not currently being harvested.</p>\n");
            }
            harvestSettingsFileStream.close(); // close the file stream
         } catch (Exception e){ // if there is an error
            System.out.println("Could not load harvest configuration from " + fileName + "."); //error message
            System.out.println("Please make sure that the file is correctly formatted.");
            // we should now cancel this specific harvest
         }
         output.append("     </div>\n");

         return output;
    }
}
