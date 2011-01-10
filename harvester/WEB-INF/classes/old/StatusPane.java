


import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * The main status pane of the webpage.
 * <p>
 * Uses the <code>Properties</code> object to read config data from the xml files
 * selected. Dispalys pertinant data to the user. If the repository is not currently
 * being harvested, offers the user options to delete, edit, reset the harvest date
 * of and harvest now. If the repository is currently being harvested, these options
 * are unavailable.
 * @author Lawrence Webley
 */
public class StatusPane extends HtmlPane
{
    /**
     * Basic constructore to call the parent constructor.
     */
    public StatusPane()
    {
        super();
    }

    /**
     * Since no file is specified, this basic filler window simply tells the user
     * to select a repository to check its status.
     * @return generated code for the <code>main</code> div
     */
    public StringBuffer getStatusPane()
    {
        //make it list sequentially here
        output.append("     <div id=\"main\">\n");

        //search sources directory for repos to list.
        File sources = new File(new Settings ().getSourceDir ());
        String[] files = sources.list();
        output.append("         <h2>Repository Statuses</h2>\n");
        
        if (files != null)
        { // if there are children of the sources directory
            for (int i = 0; i < files.length; i++)
            { // for every item in files
                if(files[i].endsWith(".xml"))
                {
                    output.append(getFileStatusPane(files[i]));
                }
            }
        }
        else
        {
            output.append("         No repositories found....");
        }
        output.append("     </div>\n");

        return output;
    }

    /**
     * Generates html code to describe the status of the repository in question,
     * using the configuration file given. Uses the <code>Properties</code> object
     * to extract date from the <code>xml</code> file given. Also includes a form
     * which allows the deleting of the repository config file (with a confirmation
     * dialog) and forms to allow editing of the config file, reseting of the last
     * harvest date and an option to harvest now. The harvest now operation will
     * create a new thread (see the {@link Harvester.HarvesterThread} class) of
     * the harvester from within the control panel servlet.
     * @param fileName the name of the file who's status we are checking
     * @return generated code depicting the status of the given file. Returns the
     * code using a <code>main</code> div.
     */
    public StringBuffer getFileStatusPane(String fileName)
    {
        //returns the status of the repo. Reads XML from file        

         Properties harvestSettings = new Properties(); // Properties object that will be used to load the harvest settings (source, metadataformats)

         FileInputStream harvestSettingsFileStream = null;
         StringBuffer statusOutput = new StringBuffer();

         try {          
             harvestSettingsFileStream = new FileInputStream(new Settings ().getSourceDir ()+fileName);
         } catch (Exception e){ // if there is an error loading the settingsFile, exit and print a message
            System.out.println("Could not load database configuration from " + fileName + "."); // error message
            System.out.println("Please make sure that the file exists and you are allowed access to it.");
            statusOutput.append("Please make sure that the file exists and you are allowed access to it.");
            return statusOutput;
            // we should now cancel this specific harvest
         }



         try {
            harvestSettings.loadFromXML(harvestSettingsFileStream); // try load the database settings from the file input stream

            //get file settings
            String name = harvestSettings.getProperty("Name");
            String baseURL = harvestSettings.getProperty("baseURL");
            String metadataFormat = harvestSettings.getProperty("metadataFormat");
            String dateFrom = harvestSettings.getProperty("dateFrom");
            String harvestStatus = harvestSettings.getProperty("harvestStatus");
            long harvestInterval = Long.decode( harvestSettings.getProperty("harvestInterval") );
            boolean isRunning = (new Boolean( harvestSettings.getProperty("isRunning") ) ).booleanValue(); // get the boolean value of the string

            

            //Write to html
            //Start by making a bordered area (check how to center, group)
            statusOutput.append("         <fieldset>\n");
            statusOutput.append("         <legend>"+name+"</legend>\n");
            statusOutput.append("         Base URL: "+baseURL+"\n");
            statusOutput.append("         <br>Metadata Format Harvested: "+metadataFormat+"\n");
            statusOutput.append("         <br>Last Harvest Date: "+dateFrom+"\n");
            statusOutput.append("         <br>Harvest Status: "+harvestStatus+"\n");
            statusOutput.append("         <br>Harvest interval: "+harvestInterval+"\n");
            statusOutput.append("         <br>Harvest Currently Running: ");
            if(isRunning)
            {
                statusOutput.append("<b><u>Yes</u></b></p>\n");
            }
            else
            {
                statusOutput.append("No</p>\n");
            }
            harvestSettingsFileStream.close(); // close the file stream

            //add the buttons for the repository
            statusOutput.append("             <form name=\"input\" action=\"controlPanel\" method=\"get\">\n");
            statusOutput.append("             <input type=\"hidden\" name=\"fileName\" value=\""+fileName+"\"\n");
            if(isRunning)
            {
                statusOutput.append("             <input disabled type=\"submit\" name=\"loc\" value=\"Edit Repository\" />\n");
                statusOutput.append("             <input disabled type=\"submit\" name=\"loc\" value=\"Harvest Now\" />\n");
                statusOutput.append("             <input disabled type=\"submit\" name=\"loc\" value=\"Reset Last Harvest Date\" />\n");
            }else
            {
                statusOutput.append("             <input type=\"submit\" name=\"loc\" value=\"Edit Repository\" />\n");
                statusOutput.append("             <input type=\"submit\" name=\"loc\" value=\"Harvest Now\" />\n");
                statusOutput.append("             <input type=\"submit\" name=\"loc\" value=\"Reset Last Harvest Date\" />\n");
            }
            statusOutput.append("             </form>\n");

            statusOutput.append("             <form name=\"input\" action=\"controlPanel\" method=\"get\" onsubmit=\"return executeOnDelete();\">\n");
            statusOutput.append("             <input type=\"hidden\" name=\"fileName\" value=\""+fileName+"\"\n");
            if(isRunning)
            {
                statusOutput.append("             <input disabled type=\"submit\" name=\"loc\" value=\"Delete Repository\" />\n");
            }else
            {
                statusOutput.append("             <input type=\"submit\" name=\"loc\" value=\"Delete Repository\" />\n");
            }
            statusOutput.append("             </form>\n");

            statusOutput.append("             </fieldset>\n");

         } catch (Exception e){ // if there is an error
            System.out.println("Could not load harvest configuration from " + fileName + "."); //error message
            System.out.println("Error: Please make sure that the file is correctly formatted.");
            statusOutput = new StringBuffer("Error: Please make sure that the file is correctly formatted.");
            // we should now cancel this specific harvest
         }
         

         return statusOutput;
    }
}
