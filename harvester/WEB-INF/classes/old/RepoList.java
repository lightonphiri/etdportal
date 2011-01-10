
import java.io.File;

/**
 * Generates the html code resposible for displaying the right hand sidebar
 * which contains a list of all available repositories.
 * <p>
 * Each repository is represented by an <code>xml</code> configuration file located in the
 * sources directory. This class reads the sources directory and then populates
 * the sidebar with the names of the <code>xml</code> files located there.
 * @author lawrence Webley
 */
public class RepoList extends HtmlPane
{
    /**
     * Constructor to initialize its parent constructor.
     */
    public RepoList()
    {
        super();
    }

    /**
     * Creates the html code for the sidebar div, calling the <code>getRepositories
     * </code> method to actually get the list of <code>.xml</code> files.
     * @return the html code containing the <code>sidebar</code> div.
     */
    public StringBuffer getRepoList()
    {
        output.append("     <div id=\"sidebar\">\n");
        output.append("     <h2>Repositories</h2>\n");

        output.append("     <ul>\n");
        output.append(getRepositories());        
        output.append("     </ul>\n");
        output.append("     </div>\n");
        return output;
    }

    /**
     * Scans the <code>sources</code> directory for <code>xml</code> files and then
     * adds them to the <code>stringbuffer</code> in the html list item format.
     * It also adds links to them so that if clicked, they will cause the status
     * window to try and open them.
     * @return a list of xml config files for the repositories
     */
    public StringBuffer getRepositories()
    {
        StringBuffer repos = new StringBuffer();
        File sources = new File(new Settings ().getSourceDir ());
        //uncomment this to determine the path the sources should inhabit.
        //System.out.println(sources.getAbsolutePath());
        String[] files = sources.list();
        

        if (files != null)
        { // if there are children of the sources directory
            for (int i = 0; i < files.length; i++)
            { // for every item in files
                if(files[i].endsWith(".xml"))
                {
                    repos.append("          <li><a href=\"controlPanel?loc=repoStatus&fileName="+files[i]+"\">"+files[i]+"</a></li>\n");
                }
            }
        }
        else
        {
            repos.append("          <h3>No repositories found....</h3>");
        }
        return repos;
    }
}
