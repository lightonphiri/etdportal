
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author Lawrence Webley
 */
public class HarvesterServlet extends HttpServlet
{

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    { 
        response.setContentType ("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String operation = request.getParameter("loc");
        
        StringBuffer page = new StringBuffer();
        //header
        page.append("   <div id=\"header\"><h1>Harvester Control Panel Home</h1></div>\n");
        //nav bar
        NavBar nav = new NavBar();
        page.append(nav.getNav());
        //sidebar
        RepoList sidebar = new RepoList();
        page.append(sidebar.getRepoList());

        if(operation != null)
        {
            if(operation.equals("list"))
            {
              //status window
              StatusPane stats = new StatusPane();
              page.append(stats.getStatusPane());
            }
            else if(operation.equals("repoStatus"))
            {
                String fileName = request.getParameter("fileName");
                if(fileName == null)
                {
                    //tell them file not found
                    page.append("<p>File could not be found</p>");
                }
                else
                {
                    //status window
                    StatusPane stats = new StatusPane();
                    page.append(stats.getFileStatusPane(fileName));
                }
            }
            else if(operation.equals("add"))
            {
                //status window
                RepositoryEditor repo = new RepositoryEditor();
                page.append(repo.getRepoEditor());
            }
            else
            {
                //status window
                StatusPane stats = new StatusPane();
                page.append(stats.getStatusPane());
            }
        }else
        {
            //for save and edit requests
            operation = request.getParameter("name");
            if(operation != null)
            {
                //Save Window
                ConfigWriter conf = new ConfigWriter();
                page.append(conf.saveConfig(request));
            }
            else
            {
                //status window
                StatusPane stats = new StatusPane();
                page.append(stats.getStatusPane());
            }
        }
        
        //footer
        Calendar date = Calendar.getInstance();
        SimpleDateFormat UTCDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        page.append("   <div id=\"footer\"><p>Page generated "+(UTCDateFormatter.format(date.getTime()))+"</p></div>");
        //generate html
        HTMLWriter writer = new HTMLWriter();
        out.println(writer.format(page));
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        doGet(request, response);
    }

}
