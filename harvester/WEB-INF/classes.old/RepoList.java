
import java.io.File;

/**
 *
 * @author lawrence Webley
 */
public class RepoList extends HtmlPane
{
    public RepoList()
    {
        super();
    }

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

    private StringBuffer getRepositories()
    {
        StringBuffer repos = new StringBuffer();
        File sources = new File("/var/lib/tomcat5.5/webapps/harvester/WEB-INF/config/sources");
        String[] files = sources.list();
        

        if (files != null)
        { // if there are children of the sources directory
            for (int i = 0; i < files.length; i++)
            { // for every item in files
                repos.append("          <li><a href=\"controlPanel?loc=repoStatus&fileName="+files[i]+"\">"+files[i]+"</a></li>\n");
            }
        }
        else
        {
            repos.append("          <h3>No repositories found....</h3>");
        }
        return repos;
    }
}
