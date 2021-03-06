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
         
         Repository rep = new Repository (conf);
         rep.setID (ID);
         rep.setName (request.getParameter ("name"));
         rep.setBaseURL (request.getParameter ("baseURL"));
         rep.setMetadataFormat (request.getParameter ("metadataFormat"));
         rep.setSetSpec (request.getParameter ("setSpec"));
         rep.setHarvestInterval (Integer.parseInt (request.getParameter ("harvestInterval")));
         rep.setTimeout (Integer.parseInt (request.getParameter ("timeout")));
         rep.setDateFrom (request.getParameter ("dateFrom"));
         rep.setResumptionToken (request.getParameter ("resumptionToken"));
         
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
                      "<tr><th>timeout</th><td><input type=\"text\" name=\"timeout\" size=\"40\" value=\""+rep.getTimeout ()+"\"/></td></tr>" +
                      "<tr><th>last harvest</th><td><input type=\"text\" name=\"dateFrom\" size=\"40\" value=\""+rep.getDateFrom ()+"\"/></td></tr>" +
                      "<tr><th>resumptionToken</th><td><input type=\"text\" name=\"resumptionToken\" size=\"40\" value=\""+rep.getResumptionToken ()+"\"/></td></tr>" +
                      "</table>" +
                      "<input type=\"submit\" value=\"save\"></form>");

         if (! "".equals (rep.getID ()))
            page.append ("<form action=\"?\">"+
                         "<input type=\"hidden\" name=\"action\" value=\"delete\"/>"+
                         "<input type=\"hidden\" name=\"id\" value=\""+rep.getID ()+"\"/>"+
                         "<input type=\"submit\" value=\"delete repository\">" +
                         "</form>");
      }
      else // display a list of repositories
      {
         page.append ("<div id=\"header\"><h1>Repository List</h1></div>\n" +
                      "<table>\n" + 
                      "<tr><th>ID</th><th>name</th><th>md</th><th>set</th><th>on?</th><th>status</th><th>last harvest</th><th>#rec</th></tr>\n");
         
         Database db = new Database (conf);
         if (db.connect ())
         {
            ArrayList<Repository> reps = db.getRepositoryList ();
            for ( int i=0; i<reps.size(); i++ )
            {
               Repository rep = reps.get(i);
               String setSpec = rep.getSetSpec ();
               if (setSpec.length () > 12)
                  setSpec = setSpec.substring (0, 12)+"...";
               page.append ("<tr>"+
                            "<td><a href=\"?action=edit&id="+rep.getID()+"\">"+rep.getID()+"</a></td>"+
                            "<td>"+rep.getName()+"</td>"+
                            "<td>"+rep.getMetadataFormat()+"</td>"+
                            "<td>"+setSpec+"</td>"+
                            "<td>"+rep.getRunning()+"</td>"+
                            "<td>"+rep.getHarvestStatus()+"</td>"+
                            "<td>"+rep.getDateFrom()+"</td>"+
                            "<td>"+rep.getNumberOfRecords()+"</td>"+
                            "</tr>");
            }
            
         }
         
         page.append ("</table>\n");
         page.append ("Click on ID to edit/delete || <a href=\"?action=edit\">Add Repository</a> || <a href=\"?action=list\">Refresh List</a>");
      }
      
      HTMLWriter writer = new HTMLWriter();
      out.println(writer.format(page));
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
