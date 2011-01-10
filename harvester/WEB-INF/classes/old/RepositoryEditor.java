
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;



/**
 * The class responsible for displaying the webpage content that allows creation and
 * editting of repositories.
 * <p>
 * Fills the <code>main</code> div of the webpage when used. It creates a form
 * that is used to change or create repositories. These form settings are then passed
 * on to the {@link ControlPanel.ConfigWriter} class for actual storage.
 * @author Lawrence Webley
 */
public class RepositoryEditor extends HtmlPane
{
    /**
     * Basic constructor to call the super constructor.
     */
    public RepositoryEditor()
    {
        super();
    }

    /**
     * Generates the html code necessary to create a new repository.
     * <p>
     * Uses forms to create input text areas for setting all the necessary
     * repository settings. Once the submit button on this form is pressed, it will
     * pass on its data to the {@link ControlPanel.ConfigWriter} class for saving.
     * @return the html code for displaying an interface to create a new repository
     */
    public StringBuffer getRepoEditor()
    {
        output.append("     <div id=\"main\">\n");
        output.append("         <h2>Repository set up</h2>\n");
        output.append("         <fieldset>\n");
        output.append("             <legend>Repository Settings</legend>\n");
        //Input
        output.append("             <form name=\"input\" action=\"controlPanel\" method=\"get\">\n");
        output.append("             Repository Name:\n");
        output.append("             <input type=\"text\" name=\"name\" />\n");
        output.append("             <br />");        
        output.append("             URL to servlet:\n");
        output.append("             <input type=\"text\" name=\"url\" />\n");
        output.append("             <br />");
        output.append("             Metadata format to harvest:\n");
        output.append("             <input type=\"text\" name=\"meta\" />\n");
        output.append("             <br />");
        output.append("             Repository set to harvest from (leave empty for no sets):\n");
        output.append("             <input type=\"text\" name=\"set\" />\n");
        output.append("             <br />");
        output.append("             Harvest interval:\n");
        output.append("             <input type=\"text\" name=\"interval\" />\n");
        output.append("             <br />");       
        output.append("             <input type=\"submit\" name=\"loc\" value=\"Save Repository\" />\n");

        output.append("             </form>\n");
        output.append("         </fieldset>\n");
        output.append("     </div>\n");        
        

        return output;
    }

    /**
     * Takes a filename of a config file and then outputs to the web page an
     * interface for editing said config file. Does not allow the filename or
     * "set spec" to be changed. Also uses the {@link ControlPanel.ConfigWriter}
     * to actually save the data.
     * @param fileName The name of the config file to be edited
     * @return the html code that allows editing of a repository config file.
     */
    public StringBuffer getRepoEditor(String fileName)
    {
        output.append("     <div id=\"main\">\n");

        Properties harvestSettings = new Properties(); // Properties object that will be used to load the harvest settings (source, metadataformats)

        FileInputStream harvestSettingsFileStream = null;

        try {           
            harvestSettingsFileStream = new FileInputStream(new Settings ().getSourceDir ()+fileName);
        } catch (Exception e){ // if there is an error loading the settingsFile, exit and print a message
            System.out.println("Could not load database configuration from " + fileName + "."); // error message
            System.out.println("Please make sure that the file exists and you are allowed access to it.");
            // we should now cancel this specific harvest
        }

        try {
            harvestSettings.loadFromXML(harvestSettingsFileStream); // try load the database settings from the file input stream

            //get file settings
            String name = harvestSettings.getProperty("Name");
            String baseURL = harvestSettings.getProperty("baseURL");
            String metadataFormat = harvestSettings.getProperty("metadataFormat");
            String set = harvestSettings.getProperty("SetSpec");
            long harvestInterval = Long.decode( harvestSettings.getProperty("harvestInterval") );
            boolean isRunning = (new Boolean( harvestSettings.getProperty("isRunning") ) ).booleanValue(); // get the boolean value of the string


            output.append("         <h2>Update a Repository</h2>\n");
            output.append("         <fieldset>\n");
            output.append("             <legend>Repository Settings</legend>\n");
            //Input
            output.append("             <form name=\"input\" action=\"controlPanel\" method=\"get\">\n");
            output.append("             Repository Name: "+ name+"\n");
            output.append("             <input type=\"hidden\" name=\"name\" value=\""+name+"\" />\n");
            output.append("             <br />");
            output.append("             URL to servlet:\n");
            output.append("             <input type=\"text\" name=\"url\" value=\""+baseURL+"\" />\n");
            output.append("             <br />");
            output.append("             Metadata format to harvest:\n");
            output.append("             <input type=\"text\" name=\"meta\" value=\""+metadataFormat+"\" />\n");
            output.append("             <br />");
            if(set != null)
            {
                output.append("             Repository set to harvest from (leave empty for no sets):\n");
                output.append("             <input type=\"text\" name=\"set\" value=\""+set+"\" />\n");
                output.append("             <br />");
            }
            else
            {
                output.append("             Repository set to harvest from (leave empty for no sets):\n");
                output.append("             <input type=\"text\" name=\"set\" />\n");
                output.append("             <br />");
            }
            output.append("             Harvest interval:\n");
            output.append("             <input type=\"text\" name=\"interval\" value=\""+harvestInterval+"\" />\n");
            output.append("             <br />");           
            output.append("             <input type=\"submit\" name=\"loc\" value=\"Save Repository\" />\n");

            output.append("             </form>\n");
            output.append("         </fieldset>\n");


        }catch (Exception e){ // if there is an error
            System.out.println("Could not load harvest configuration from " + fileName + "."); //error message
            System.out.println("Please make sure that the file is correctly formatted.");
            // we should now cancel this specific harvest
        }
        output.append("     </div>\n");
        return output;
    }

    /**
     * Given a config files name, this method will delete the given config file.
     * @param fileName the name of the file to be deleted
     * @return a message saying whether the operation was a success or not.
     */
    public StringBuffer deleteRepository(String fileName)
    {
        output.append("     <div id=\"main\">\n");
        try{
            File sources = new File(new Settings ().getSourceDir ()+fileName);
            sources.delete();

            output.append("         <h2>Repository Deleted Successfully</h2>\n");
            output.append("          The repository config file has been deleted.\n");
            output.append("     </div>\n");
        }catch(SecurityException e)
        {
            output.append("         <h2>Failed to delete repository!</h2>\n");
            output.append("          There was an error while trying to delete the repository!\n");
            output.append("          Error was: "+e);
            output.append("     </div>\n");
        }
        return output;

    }
}