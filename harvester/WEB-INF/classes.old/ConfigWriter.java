
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;


/**
 *
 * @author Lawrence Webley
 */
public class ConfigWriter extends HTMLWriter
{
    public ConfigWriter()
    {
        super();
    }

    public StringBuffer saveConfig(HttpServletRequest request)
    {
        Properties harvestSettings = new Properties (); // create the properties object that will be saved to file

        output.append("     <div id=\"main\">\n");

        //now try to get all the properties
        String name = request.getParameter("name");
        String baseURL = request.getParameter("url");
        String metadataFormat = request.getParameter("meta");
        String comment = request.getParameter("comment");
        String interval = request.getParameter("interval");


        harvestSettings.setProperty("Name", name);
        harvestSettings.setProperty("baseURL",baseURL);
	harvestSettings.setProperty("metadataFormat",metadataFormat);
	harvestSettings.setProperty("dateFrom","1970-1-1 00:00:00.000");
	harvestSettings.setProperty("harvestStatus","No harvest has yet been run.");
	harvestSettings.setProperty("harvestInterval",interval);
	harvestSettings.setProperty("isRunning","false" );

        try{
            FileOutputStream out = new FileOutputStream("/var/lib/tomcat5.5/webapps/harvester/WEB-INF/config/sources/"+name+".xml"); // create the file output stream
            harvestSettings.storeToXML(out, comment);
            out.close();

            output.append("         <h2>Repository Saved</h2>\n");
            output.append("          The repository has been saved successfully\n");
            output.append("     </div>\n");
        }catch(IOException e)
        {
            output.append("         <h2>Failed to save repository!</h2>\n");
            output.append("          There was an error while trying to save the repository!\n");
            output.append("Error was: "+e);
            output.append("     </div>\n");
        }
        return output;
    }
}
