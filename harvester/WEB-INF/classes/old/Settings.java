
/**
 * A floating configuration object that stores commonly needed variables that are
 * specific to the current control panel deployment.
 * @author Lawrence
 */
public class Settings
{
    /** The directory that the repository xml files are stored. */
    //DEBUG
    public String classDir;
//    public static final String sourceDir = //"C:/Users/Lawrence/Documents/OAI WORK/July3Revisions/webapps/harvester/WEB-INF/config/sources/";
//    "/var/lib/tomcat6/webapps/harvester/WEB-INF/config/sources/";
    
    /**
     * Default constructor - does nothing special.
     */
    public Settings()
    {
       classDir = getClass().getProtectionDomain().
                  getCodeSource().getLocation().toString().substring (5);
    }
    
    public String getSourceDir ()
    {
       return classDir + "../config/sources/";
    }

    public String getConfigDir ()
    {
       return classDir + "../config/";
    }
}