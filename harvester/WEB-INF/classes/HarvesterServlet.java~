

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 * Responsible for assembling the control panel from its constituent parts.
 * <p>
 * This class receives the Http requests sent to the servlet and then extracts
 * the <code>loc</code> parameter from it. Depending on this paramter, one of several different
 * web pages will be constructed, and one of several different actions will be performed.
 * <p>
 * The default page for an unknown location will be the home page. If no <tt>loc</tt>
 * paramter exists in the Http request, then an error message is returned. The
 * available unique locations that are recognised are <code>(list, repoStatus,
 * add, Edit Repository, Delete Repository, Harvest now, Reset Last Harvest Date,
 * Save Repository)</code>
 * <p>
 * Some locations also require other parameters, such as a <code>fileName</code>
 * parameter.
 *
 * @author Lawrence Webley
 */
public class HarvesterServlet extends HttpServlet
{

    /**
     * Using the given request, this method looks for the loc paramter in the
     * request string and then uses this to determine which part of the control
     * panel to run. Once a particular location has been determined, other paramters
     * such as file names will be extracted from the request.
     * @param request The incoming http request from a remote user
     * @param response Our response to the remote user
     * @throws java.io.IOException Inherited from {@link javax.servlet.http.HttpServlet}
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType ("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String location = request.getParameter("loc");

        if(location != null)
        {
            if(location.equals("list"))
            {
                StringBuffer page = new StringBuffer();
                //header
                page.append("   <div id=\"header\"><h1>Harvester Control Panel Home</h1></div>\n");
                //nav bar
                NavBar nav = new NavBar();
                page.append(nav.getNav());
                //status window
                StatusPane stats = new StatusPane();
                page.append(stats.getStatusPane());
                //sidebar
                //RepoList sidebar = new RepoList();
                //page.append(sidebar.getRepoList());
                //footer
                Calendar date = Calendar.getInstance();
                SimpleDateFormat UTCDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
                page.append("   <div id=\"footer\"><p>Page generated "+(UTCDateFormatter.format(date.getTime()))+"</p></div>");

                //generate html
                HTMLWriter writer = new HTMLWriter();
                out.println(writer.format(page));
            }
            /* This was part of the sidebar system. Showed the currently selected repo in the status area
            else if(location.equals("repoStatus"))
            {
                String fileName = request.getParameter("fileName");
                if(fileName == null)
                {
                    //tell them file not found
                    out.println("<P>File could not be found</P>");
                }
                else
                {
                    //show them the status
                    StringBuffer page = new StringBuffer();

                    //start with header
                    page.append("   <div id=\"header\"><h1>Status of "+fileName+"</h1></div>\n");
                    //nav bar
                    NavBar nav = new NavBar();
                    page.append(nav.getNav());
                    //status window
                    StatusPane stats = new StatusPane();
                    page.append(stats.getFileStatusPane(fileName));
                    //sidebar
                    RepoList sidebar = new RepoList();
                    page.append(sidebar.getRepoList());
                    //footer
                    Calendar date = Calendar.getInstance();
                    SimpleDateFormat UTCDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
                    page.append("   <div id=\"footer\"><p>Page generated "+(UTCDateFormatter.format(date.getTime()))+"</p></div>");

                    //generate html
                    HTMLWriter writer = new HTMLWriter();
                    out.println(writer.format(page));
                }
            }
            */
            else if(location.equals("add"))
            {
                //show them the status
                StringBuffer page = new StringBuffer();

                //start with header
                page.append("   <div id=\"header\"><h1>Add a new Repository</h1></div>\n");
                //nav bar
                NavBar nav = new NavBar();
                page.append(nav.getNav());
                //status window
                RepositoryEditor repo = new RepositoryEditor();
                page.append(repo.getRepoEditor());
                //sidebar
                //RepoList sidebar = new RepoList();
                //page.append(sidebar.getRepoList());
                //footer
                Calendar date = Calendar.getInstance();
                SimpleDateFormat UTCDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
                page.append("   <div id=\"footer\"><p>Page generated "+(UTCDateFormatter.format(date.getTime()))+"</p></div>");

                //generate html
                HTMLWriter writer = new HTMLWriter();
                out.println(writer.format(page));
            }

            else if(location.equals("Edit Repository"))
            {
                 String fileName = request.getParameter("fileName");
                if(fileName == null)
                {
                    //tell them file not found
                    out.println("File could not be found");
                }
                else
                {
                    //show them the status
                    StringBuffer page = new StringBuffer();

                    //start with header
                    page.append("   <div id=\"header\"><h1>Editing a Repository</h1></div>\n");
                    //nav bar
                    NavBar nav = new NavBar();
                    page.append(nav.getNav());
                    //status window
                    RepositoryEditor repo = new RepositoryEditor();
                    page.append(repo.getRepoEditor(fileName));
                    //sidebar
                    //RepoList sidebar = new RepoList();
                    //page.append(sidebar.getRepoList());
                    //footer
                    Calendar date = Calendar.getInstance();
                    SimpleDateFormat UTCDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
                    page.append("   <div id=\"footer\"><p>Page generated "+(UTCDateFormatter.format(date.getTime()))+"</p></div>");

                    //generate html
                    HTMLWriter writer = new HTMLWriter();
                    out.println(writer.format(page));
                }
            }
            else if(location.equals("Delete Repository"))
            {
                String fileName = request.getParameter("fileName");
                if(fileName == null)
                {
                    //tell them file not found
                    out.println("File could not be found");
                }
                else
                {
                    //show them the status
                    StringBuffer page = new StringBuffer();

                    //start with header
                    page.append("   <div id=\"header\"><h1>Editing a Repository</h1></div>\n");
                    //nav bar
                    NavBar nav = new NavBar();
                    page.append(nav.getNav());
                    //status window
                    RepositoryEditor repo = new RepositoryEditor();
                    page.append(repo.deleteRepository(fileName));
                    //sidebar
                    //RepoList sidebar = new RepoList();
                    //page.append(sidebar.getRepoList());
                    //footer
                    Calendar date = Calendar.getInstance();
                    SimpleDateFormat UTCDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
                    page.append("   <div id=\"footer\"><p>Page generated "+(UTCDateFormatter.format(date.getTime()))+"</p></div>");

                    //generate html
                    HTMLWriter writer = new HTMLWriter();
                    out.println(writer.format(page));
                }
            }
            else if (location.equals("Harvest Now"))
            {
                String fileName = request.getParameter("fileName");
                if(fileName == null)
                {
                    //tell them file not found
                    out.println("File could not be found");
                }
                else
                {
                    //Start the harvest
                    String[] args = new String [1];
                    args[0] = "sources/"+fileName;
                    HarvesterThread harvest = new HarvesterThread(args);
                    harvest.start();                    

                    //show them the status
                    StringBuffer page = new StringBuffer();

                    //start with header
                    page.append("   <div id=\"header\"><h1>Status of "+fileName+"</h1></div>\n");
                    //nav bar
                    NavBar nav = new NavBar();
                    page.append(nav.getNav());
                    //status window
                    StatusPane stats = new StatusPane();
                    page.append(stats.getFileStatusPane(fileName));
                    //sidebar
                    //RepoList sidebar = new RepoList();
                    //page.append(sidebar.getRepoList());
                    //footer
                    Calendar date = Calendar.getInstance();
                    SimpleDateFormat UTCDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
                    page.append("   <div id=\"footer\"><p>Page generated "+(UTCDateFormatter.format(date.getTime()))+"</p></div>");

                    //generate html
                    HTMLWriter writer = new HTMLWriter();
                    out.println(writer.format(page));
                }
            }
            else if (location.equals("Reset Last Harvest Date"))
            {
                String fileName = request.getParameter("fileName");
                if(fileName == null)
                {
                    //tell them file not found
                    out.println("File could not be found");
                }
                else
                {
                    //Reset the date                    
                    ConfigWriter.resetDate(fileName);

                    //show them the status
                    StringBuffer page = new StringBuffer();

                    //start with header
                    page.append("   <div id=\"header\"><h1>Status of "+fileName+"</h1></div>\n");
                    //nav bar
                    NavBar nav = new NavBar();
                    page.append(nav.getNav());
                    //status window
                    StatusPane stats = new StatusPane();
                    page.append(stats.getFileStatusPane(fileName));
                    //sidebar
                    //RepoList sidebar = new RepoList();
                    //page.append(sidebar.getRepoList());
                    //footer
                    Calendar date = Calendar.getInstance();
                    SimpleDateFormat UTCDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
                    page.append("   <div id=\"footer\"><p>Page generated "+(UTCDateFormatter.format(date.getTime()))+"</p></div>");

                    //generate html
                    HTMLWriter writer = new HTMLWriter();
                    out.println(writer.format(page));
                }
            }
            else if (location.equals("Save Repository"))
            {
                //Save the actual data                
                boolean confSaved = ConfigWriter.saveConfig(request);

                //use a default page
                StringBuffer page = new StringBuffer();
                //header
                page.append("   <div id=\"header\"><h1>Harvester Control Panel Home</h1></div>\n");
                //nav bar
                NavBar nav = new NavBar();
                page.append(nav.getNav());
                if(confSaved)
                {
                    //status of new repo
                    StatusPane stats = new StatusPane();
                    page.append(stats.getFileStatusPane(request.getParameter("name")+".xml"));
                }
                else
                {
                    page.append("   <div id=\"main\">");
                    page.append("<h3>File could not be saved!</h3>");
                    page.append("Check the tomcat log file for more details...");
                    page.append("</div>\n");
                }
                //sidebar
                //RepoList sidebar = new RepoList();
                //page.append(sidebar.getRepoList());
                //footer
                Calendar date = Calendar.getInstance();
                SimpleDateFormat UTCDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
                page.append("   <div id=\"footer\"><p>Page generated "+(UTCDateFormatter.format(date.getTime()))+"</p></div>");

                //generate html
                HTMLWriter writer = new HTMLWriter();
                out.println(writer.format(page));
            }
            //this can be used to get an href to the stylesheet if urls to stylesheets arent working properly
            else if(location.equals("styleSheet"))
            {
                File styleSheet = new File(new Settings ().getSourceDir()+ "StyleSheet.css");
                if(styleSheet.exists())
                {
                    Scanner reader = new Scanner(styleSheet);
                    while(reader.hasNextLine())
                    {
                        out.println(reader.nextLine());
                    }
                }else
                {
                    out.println("No StyleSheet found.... Please create a stylesheet called StyleSheet.css and put it in the WEB-INF directory.");
                }
            }
            else//unknown location
            {
                //use a default page
                StringBuffer page = new StringBuffer();
                //header
                page.append("   <div id=\"header\"><h1>Harvester Control Panel Home</h1></div>\n");
                //nav bar
                NavBar nav = new NavBar();
                page.append(nav.getNav());
                //status window
                StatusPane stats = new StatusPane();
                page.append(stats.getStatusPane());
                //sidebar
                //RepoList sidebar = new RepoList();
                //page.append(sidebar.getRepoList());
                //footer
                Calendar date = Calendar.getInstance();
                SimpleDateFormat UTCDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
                page.append("   <div id=\"footer\"><p>Page generated "+(UTCDateFormatter.format(date.getTime()))+"</p></div>");

                //generate html
                HTMLWriter writer = new HTMLWriter();
                out.println(writer.format(page));
            }
        }else
        {
            out.println("Invalid URL");
        }

    }
    /**
     * Handles requests that come in using the post request type. We only support
     * get requests, so this simply calls the <tt>doGet</tt> method, forwarding the
     * parameters.
     * @param request The incoming http request from a remote user
     * @param response Our response to the remote user
     * @throws java.io.IOException Inherited from {@link javax.servlet.http.HttpServlet}
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        doGet(request, response);
    }

}
