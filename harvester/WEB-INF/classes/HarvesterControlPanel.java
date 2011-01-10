import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;
import java.util.ArrayList;
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
 # @author Hussein Suleman
 */
public class HarvesterControlPanel extends HttpServlet
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
   public void doGet (HttpServletRequest request, HttpServletResponse response) 
    throws IOException
   {
      response.setContentType ("text/html;charset=UTF-8");
      PrintWriter out = response.getWriter();
      String action = request.getParameter("action");
      
      Config conf = new Config ();
      
      StringBuffer page = new StringBuffer();
      page.append ("<div id=\"header\"><h1>Harvester Control Panel</h1></div>\n");

      // actions that require changes
      if ("save".equals (action))
      {
         String ID = request.getParameter ("id");
         
         Repository rep = new Repository (conf, ID);
         rep.setName (request.getParameter ("name"));
         rep.setBaseURL (request.getParameter ("baseURL"));
         rep.setMetadataFormat (request.getParameter ("metadataFormat"));
         rep.setSetSpec (request.getParameter ("setSpec"));
         rep.setHarvestInterval (Integer.parseInt (request.getParameter ("harvestInterval")));
         rep.setDateFrom (request.getParameter ("dateFrom"));
         
         rep.save ();
      } 
      else if ("delete".equals (action))
      {
         String ID = request.getParameter ("id");
         Repository rep = new Repository (conf, ID);
         rep.delete ();
      }
      
      // actions to navigate and display pages
      if ("edit".equals (action))
      {
         String ID = request.getParameter ("id");
         Repository rep;
         
         if ((ID == null) || ("".equals (ID)))
         {
            page.append ("<div id=\"header\"><h1>Add a new Repository</h1></div>\n");
            rep = new Repository (conf);
         }   
         else
         {   
            page.append ("<div id=\"header\"><h1>Edit a Repository</h1></div>\n");
            rep = new Repository (conf, ID);
         }
                  
         page.append ("<form action=\"?\">"+
                      "<input type=\"hidden\" name=\"action\" value=\"save\"/>");
         if (! "".equals (rep.getID ()))             
            page.append ("<input type=\"hidden\" name=\"id\" value=\""+rep.getID ()+"\"/>" +
                         "<table><tr><th>ID</th><td>"+rep.getID ()+"</td></tr>");
         else        
            page.append ("<table><tr><th>ID</th><td><input type=\"text\" name=\"id\" size=\"40\" value=\"\"/></td></tr>");
         page.append ("<tr><th>name</th><td><input type=\"text\" name=\"name\" size=\"40\" value=\""+rep.getName ()+"\"/></td></tr>" +
                      "<tr><th>baseURL</th><td><input type=\"text\" name=\"baseURL\" size=\"40\" value=\""+rep.getBaseURL ()+"\"/></td></tr>" +
                      "<tr><th>metdataPrefix</th><td><input type=\"text\" name=\"metadataFormat\" size=\"40\" value=\""+rep.getMetadataFormat ()+"\"/></td></tr>" +
                      "<tr><th>setSpec</th><td><input type=\"text\" name=\"setSpec\" size=\"40\" value=\""+rep.getSetSpec ()+"\"/></td></tr>" +
                      "<tr><th>harvest interval</th><td><input type=\"text\" name=\"harvestInterval\" size=\"40\" value=\""+rep.getHarvestInterval ()+"\"/></td></tr>" +
                      "<tr><th>last harvest</th><td><input type=\"text\" name=\"dateFrom\" size=\"40\" value=\""+rep.getDateFrom ()+"\"/></td></tr>" +
                      "</table>" +
                      "<input type=\"submit\" value=\"save\"></form>");

         if (! "".equals (rep.getID ()))
            page.append ("<form action=\"?\">"+
                         "<input type=\"hidden\" name=\"action\" value=\"delete\"/>"+
                         "<input type=\"hidden\" name=\"id\" value=\""+rep.getID ()+"\"/>"+
                         "<input type=\"submit\" value=\"delete repository\">" +
                         "</form>");
      }
      else
      {
         page.append ("<div id=\"header\"><h1>Repository List</h1></div>\n" +
                      "<table>\n" + 
                      "<tr><th>ID</th><th>name</th><th>md</th><th>set</th><th>on?</th><th>status</th><th>last harvest</th></tr>\n");
         
         Database db = new Database (conf);
         if (db.connect ())
         {
            ArrayList<Repository> reps = db.getRepositoryList ();
            for ( int i=0; i<reps.size(); i++ )
            {
               Repository rep = reps.get(i);
               page.append ("<tr>"+
                            "<td><a href=\"?action=edit&id="+rep.getID()+"\">"+rep.getID()+"</a></td>"+
                            "<td>"+rep.getName()+"</td>"+
                            "<td>"+rep.getMetadataFormat()+"</td>"+
                            "<td>"+rep.getSetSpec()+"</td>"+
                            "<td>"+rep.getRunning()+"</td>"+
                            "<td>"+rep.getHarvestStatus()+"</td>"+
                            "<td>"+rep.getDateFrom()+"</td>"+
                            "</tr>");
            }
            
         }
         
         page.append ("</table>\n");
         page.append ("Click on ID to edit/delete || <a href=\"?action=edit\">Add Repository</a> || <a href=\"?action=list\">Refresh List</a>");
      }
      
      HTMLWriter writer = new HTMLWriter();
      out.println(writer.format(page));
      
/*        if (location != null)
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
*/
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
