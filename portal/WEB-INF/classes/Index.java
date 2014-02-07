/*
@author Hussein Suleman
@author Tatenda Chipeperekwa
@date 2010-07-25
@description Dynamically generated index page
*/


   import java.util.Iterator;
   import java.util.ArrayList;
   import java.util.List;

   import java.io.*;
   import javax.servlet.*;
   import javax.servlet.http.*;
   import org.w3c.dom.Document;

   import java.net.*;
   import org.w3c.dom.*;
   import javax.xml.parsers.*;


    public class Index extends HttpServlet 
   {   
      DatabaseBrowser records_requested;					//object used to run queries on the database
      ResultFormat display_results;							//used to format results for display
      ConfigurationManager applicationSettings;			//used to pass configuration parameters in the program	
      int highlight; 
       protected void processRequest(HttpServletRequest request, HttpServletResponse response)
       throws ServletException, IOException 
      {
         highlight = 1;
         response.setContentType("text/html;charset=UTF-8");
         PrintWriter out = response.getWriter();
      
         String query = "";
         if (request.getParameter ("query") != null)
            query = request.getParameter ("query");
          
         
         String action = request.getParameter ("action");
         if ("browse".equals (action))
         {
            String category = request.getParameter ("category");
            String order = request.getParameter ("order");
            if ("Title".equals (category) && "asc".equals (order))
               highlight = 2;
            if ("Affiliation".equals (category) && "asc".equals (order))
               highlight = 3;
            if ("Date".equals (category) && "desc".equals (order))
               highlight = 4;
            if ("Date".equals (category) && "asc".equals (order))
               highlight = 5;
         }
         else if ("search".equals (action))
            highlight = 6;
         else if ("submit".equals (action))
            highlight = 7;
         else if ("about".equals (action))
            highlight = 8;
         else if ("admin".equals (action))
            highlight = 9;
         else if ("advancedsearchpage".equals (action))
            highlight = 10;
      
         out.println ("<html>"+
                   "<head>"+
                     "<title>South African National ETD Portal</title>"+
                     "<link rel=\"stylesheet\" title=\"style\" type=\"text/css\" href=\"style/style.css\"/>"+
                   "</head>"+
                   "<body>"+
                   "<div id=\"page\">"+
                   "<div id=\"header\">"+
                     "<h1>National ETD Portal</h1>"+
                     "<div class=\"description\">South African theses and dissertations</div>"+
                   "</div>"+
                   "<div id=\"mainarea\">"+
                   "<div id=\"sidebar\">"+
                   "<div id=\"sidebarnav\">"+
                     "<a "+(highlight==1?"class=\"active\" ":"")+"href=\"?\">Home</a>"+
            			 getBrowsingLinks()+
                     "<p class=\"menusep\">Search</p>"+
                         "<form method=\"get\"><input class=\"searchtext\" type=\"text\" name=\"query\" value=\""+query+"\"/><input type=\"hidden\" name=\"action\" value=\"search\"/><input type=\"submit\" value=\"go\"/></form>"+
            			"<a "+(highlight==10?"class=\"active\" ":"")+"href=\"?action=advancedsearchpage\">Advanced Search</a>"+		
                     "<p class=\"menusep\">Information</p>"+
                     "<a "+(highlight==7?"class=\"active\" ":"")+"href=\"?action=submit\">Submit your site</a>"+
                     "<a "+(highlight==8?"class=\"active\" ":"")+"href=\"?action=about\">About</a>"+
                     "<a "+(highlight==9?"class=\"active\" ":"")+"href=\"?action=admin\">Admin</a>"+
                   "</div></div>");
      
         String content = "";
      
         if ("search".equals (request.getParameter ("action")))
         {//basic search
            String start = "0";
            String maxResults = "10";
         
            if (request.getParameter ("start") != null)
               start = request.getParameter ("start");
            if (request.getParameter ("maxresults") != null)
               maxResults = request.getParameter ("maxresults");
            
            SearchEngine engine = new SearchEngine("/etc/etdportal/portal/");
            engine.search (query, start, maxResults);
         
            content = "<div id=\"contentarea\">"+
                       "<h2>Search Results</h2>"+
                       engine+
                   "</div>";
         }
         else if ("advancedsearchpage".equals (request.getParameter ("action")))
         {//creating advanced search page
            content = "<div id=\"contentarea\">"+
               "<h2>Advanced Search</h2>"+
               "<form method='get'>"+
               "<div class = 'labels'>"+
               "<p class='intro'>Modify your query to narrow your search by making use of the AND, OR and NOT. Use AND to match "+
               "documents that contain two or more chosen terms existing anywhere in the text of a single document. "+
               "This is equivalent to an intersection using sets. The symbol && can be used in place of the word AND. "+
               "Use OR to show records that have one or more of the chosen terms.  Use NOT to exclude documents that "+
               "contain the chosen terms - this is equivalent to a difference using sets.</p>"+
               "<select name='option' size='1'>"+
               "<option>Keyword</option>"+
               "<option>Title</option>"+
               "<option>Creator</option>"+
               "<option>Affiliation</option>"+
               "</select>"+
               "<input class='searchtext' type='text' name='query' value=''/>"+
               "</div><p></p>"+
               "<div class='advancedsearch'>"+	
               "<select name='modifier1' size='1'><option>AND</option><option>OR</option><option>NOT</option></select>"+	
               "<select name='option1' size='1'><option>Keyword</option><option>Title</option><option>Creator</option><option>Affiliation</option></select>"+
               "<input class='searchtext1' type='text' name='query1' value=''/>"+
               "<p></p>"+
               "<select name='modifier2' size='1'><option>AND</option><option>OR</option><option>NOT</option></select>"+	
               "<select name='option2' size='1'><option>Keyword</option><option>Title</option><option>Creator</option><option>Affiliation</option></select>"+
               "<input class='searchtext2' type='text' name='query2' value=''/>"+
               "<p></p>"+	
               "<select name='modifier3' size='1'><option>AND</option><option>OR</option><option>NOT</option></select>"+	
               "<select name='option3' size='1'><option>Keyword</option><option>Title</option><option>Creator</option><option>Affiliation</option></select>"+
               "<input class='searchtext3' type='text' name='query3' value=''/>"+
               "<p></p>"+	
               "<input type='hidden' name='action' value='advancedsearch'/>"+
               "<input class = 'searchbutton' type='submit' value='Search'/>"+
               "</div>"+	
               "</form>"+
               "</div>";
         }
         else if ("advancedsearch".equals (request.getParameter ("action")))
         {//advanced search
         
            String advancedQuery = "";
         
         //parsing the options from the advanced search
            String option = request.getParameter ("option").toLowerCase();	
            String option1 = request.getParameter ("option1").toLowerCase();	
            String option2 = request.getParameter ("option2").toLowerCase();
            String option3 = request.getParameter ("option3").toLowerCase();
         
            String query0 = request.getParameter ("query");	
            String query1 = request.getParameter ("query1");	
            String query2 = request.getParameter ("query2");
            String query3 = request.getParameter ("query3");
         
            String modifier1 = request.getParameter ("modifier1");	
            String modifier2 = request.getParameter ("modifier2");
            String modifier3 = request.getParameter ("modifier3");
         
            advancedQuery = createQuery(option,option1,option2,option3,query0,query1,query2,query3,modifier1,modifier2,modifier3);	
         
            String start = "0";
            String maxResults = "10";
         
            if (request.getParameter ("start") != null)
               start = request.getParameter ("start");
            if (request.getParameter ("maxresults") != null)
               maxResults = request.getParameter ("maxresults");
            
            SearchEngine engine = new SearchEngine("/etc/etdportal/portal/");
            engine.search (advancedQuery, start, maxResults);
         
            content = "<div id=\"contentarea\">"+
                       "<h2>Search Results</h2>"+
                       engine+
                   "</div>";
         }
         else if ("view".equals (request.getParameter ("action")))
         {   //instantiating the ConfigurationManager by reading the configuration file
         	//config.xml		
            applicationSettings = new ConfigurationManager();
            applicationSettings.configureApplication(false);
            applicationSettings.createDatabaseConnection();
            
            if(applicationSettings.getDatabaseConnection()==null)
               out.println("no connection");			
         
         
            String identifier = "";
            if (request.getParameter ("identifier") != null)
               identifier = request.getParameter ("identifier");
         
            Record record_to_view = (new DatabaseBrowser()).viewRecord(applicationSettings.getDatabaseConnection(),identifier);
          
         
            InputStream record_to_transform = null;
            try {//converting the portalXML into an inputStream for XSLT trasformtion
               record_to_transform = new ByteArrayInputStream(record_to_view.getPortalXML().getBytes("UTF-8"));
            } 
                catch (UnsupportedEncodingException e) {
                    ConfigurationManager.log.add("Error: \n"+e.toString());
               }
               
            String record=(new ResultFormat()).viewRecord(record_to_transform,"/etc/etdportal/portal/viewfull.xsl","/etc/etdportal/portal/");
         
            content = "<div id=\"contentarea\">"+
                       "<h2>View Record</h2>"+
                       "<p>"+record+"</p>"+
                   "</div>";
         
         }
         else if ("browse".equals (request.getParameter ("action")))
         {
         //instantiating the ConfigurationManager by reading the configuration file
         //config.xml		
            applicationSettings = new ConfigurationManager();
            applicationSettings.configureApplication(false);
            applicationSettings.createDatabaseConnection();
            
            String configPath = "/etc/etdportal/portal/";
         
            if(applicationSettings.getDatabaseConnection()==null)
               out.println("no connection");					
            
            List<Record> browseResults = null;
         //stores the results from the browse query
         
            int start = 1;
            int maxResults = 10;
            String order = "asc";
         
            if (request.getParameter ("start") != null)
               start = Integer.parseInt (request.getParameter ("start"));
            if (request.getParameter ("maxresults") != null)
               maxResults = Integer.parseInt (request.getParameter ("maxresults"));
            if (request.getParameter ("order") != null)
               order = request.getParameter ("order");
         
         //initialising the query object for each new browse category
            records_requested = new DatabaseBrowser();
                    
         //initialising the results object for each new browse category
            display_results = new ResultFormat();
            display_results.setServletUrl(applicationSettings.getServletUrl());
         
            browseResults = records_requested.browse (request.getParameter("category"),order,start,maxResults,applicationSettings.getDatabaseConnection());
                
         //formatting the results
            display_results.create_document(request.getParameter("category"),browseResults,records_requested.getTotalRecords(),records_requested.getOffset(),maxResults,records_requested.getLastDisplayed(),order,configPath);
         
            content = "<div id=\"contentarea\">"+
                       "<h2>Browse Results</h2>"+
                       display_results+
                   "</div>";
         }
         else if ("admin".equals (request.getParameter ("action")))
         {
            content = "<div id=\"contentarea\">"+
                   "<iframe src=\"/harvester/?loc=list\" width=\"600\" height=\"400\" frameborder=\"0\">admin</iframe>"+
                   "</div>";
         }
         else if ("about".equals (request.getParameter ("action")) || "submit".equals (request.getParameter ("action")))
         {
            content = "<div id=\"contentarea\">"+
                       "<h2>About the site</h2>"+
                       "<p>This site is run by the <a href=\"http://www.cs.uct.ac.za/research/dll\">UCT-CS Digital Libraries Laboratory</a> "+
                       "on behalf of the <a href=\"http://www.ndltd.org/\">Networked Digital Library of Theses and Dissertations</a></p>"+
                       "<p>Metadata for all theses and dissertations produced internationally "+
                       "are collected and made accessible, as well as disseminated further, from this site.</p>"+
                       "<p>Software developed and maintained by the <a href=\"http://www.cs.uct.ac.za/research/dll\">"+
                       "Digital Libraries Laboratory</a> at University of Cape Town</p>"+
                       "<h2>How to contribute metadata</h2>"+
                       "<p>To contribute metadata, contact <a href=\"mailto:hussein-at-cs-uct.ac.za\">"+
                       "Hussein Suleman</a>.</p>"+
                   "</div>";
         }
         else
         {
            Document summary = getDocument ("http://localhost:8080/summary/");
         
            String collectionStatistics = "";
            int counter = 0;
            NodeList sources = summary.getDocumentElement ().getElementsByTagName ("source");
            for ( int i=0; i<sources.getLength(); i++ )
            {
               Element source = (Element)(sources.item(i));
               String name = source.getElementsByTagName ("name").item(0).getFirstChild().getNodeValue();
               String count = source.getElementsByTagName ("count").item(0).getFirstChild().getNodeValue();
               collectionStatistics += "<tr><td>"+name+"</td><td>"+count+"</td></tr>";
               counter += Integer.parseInt (count);
            }         
         
            Document rss = getDocument ("http://localhost:8080/RSS/");
         
            String recentEntries = "";
            NodeList items = rss.getDocumentElement ().getElementsByTagName ("item");
            for ( int i=0; i<items.getLength(); i++ )
            {
               Element item = (Element)(items.item(i));
               String title = "no title found";
               if ((item.getElementsByTagName ("title").getLength()>0) &&
                   (item.getElementsByTagName ("title").item(0).hasChildNodes ()))
                  title = item.getElementsByTagName ("title").item(0).getFirstChild().getNodeValue();
               String pubDate = "no pubdate found";
               if ((item.getElementsByTagName ("pubDate").getLength()>0) &&
                   (item.getElementsByTagName ("pubDate").item(0).hasChildNodes ()))
                  pubDate = item.getElementsByTagName ("pubDate").item(0).getFirstChild().getNodeValue();
               String guid = "no guid found";
               if ((item.getElementsByTagName ("guid").getLength()>0) &&
                   (item.getElementsByTagName ("guid").item(0).hasChildNodes ()))
                  guid = item.getElementsByTagName ("guid").item(0).getFirstChild().getNodeValue();
               recentEntries += "<li>"+
                                "<a href=\"?action=view&identifier="+guid+"\">"+
                                "<span class=\"snippet_title\">"+title+"</span>"+
                                "</a>"+
                                "<br/>"+
                             "<span class=\"snippet_citation\">"+pubDate+"</span></li>";
            }
         
            content = "<div id=\"contentarea\">"+
                       "<h2>Recent Submissions</h2>"+
                       "<ol>"+recentEntries+"</ol>"+
                       "<h2>Collection Statistics</h2>"+
                       "<table class=\"stats\">"+
                       "<tr><th>Collection</th><th>Total</th></tr>"+
                       collectionStatistics+
                       "<tr><th>Total</th><th>"+counter+"</th></tr>"+
                       "</table>"+
                   "</div>";   
         }
      
         out.println (content);
      
         out.println ("</div><div id=\"footer\">"+
            "Portal and Archive managed by UCT-CS DLL; CSS courtesy of <a href=\"http://www.free-css-templates.com/\">Free CSS Templates</a>"+
            "<br/>Feeds: |<a href=\"/RSS/\">RSS</a>| "+
            "|<a href=\"/OAI-PMH/\">OAI-PMH</a>|"+
            "</div>");
      
         out.println ("</div></body>"+
                   "</html>");
      
         out.close ();
      } 
   
    /** 
    * Handles the HTTP <code>GET</code> method.
    * @param request servlet request
    * @param response servlet response
    */
       protected void doGet(HttpServletRequest request, HttpServletResponse response)
       throws ServletException, IOException {
         processRequest(request, response);
      } 
   
    /** 
    * Handles the HTTP <code>POST</code> method.
    * @param request servlet request
    * @param response servlet response
    */
       protected void doPost(HttpServletRequest request, HttpServletResponse response)
       throws ServletException, IOException {
         processRequest(request, response);
      }
   
   /** 
    * Creates an advanced search query that is passed to the search engine.
    */
       private String createQuery(String option,String option1,String option2,String option3,String query,String query1,String query2,String query3,String modifier1,String modifier2,String modifier3)
      {
         String advancedQuery = "";
      
         if(query.equals(""))
         {
            advancedQuery = "Please enter a valid query.";
         }
         else{
            query = "("+ option+":"+ query+")";	
         
            if(!query1.equals(""))
               query1 =modifier1+" "+"("+ option1+":"+query1+")";
            else
               query1 = "";
         
            if(!query2.equals(""))
               query2 =modifier2+" "+"("+ option2+":"+query2+")";
            else
               query2 = "";
         
            if(!query3.equals(""))
               query3 =modifier3+" "+"("+ option3+":"+query3+")";
            else
               query3 = "";
         
            advancedQuery = query+" "+query1+" "+query2+" "+query3;
            advancedQuery=advancedQuery.replaceAll("keyword:", "");		
         
         }
         return advancedQuery;
      }
   
   /** 
    * Creates the browsing links based on the categories specified in the configuration file.
    */
       private String getBrowsingLinks()
      {
         applicationSettings = new ConfigurationManager();
         applicationSettings.configureApplication(false);
       	
         String links = "";
      
         if(applicationSettings.getBrowseCategories()!=null && applicationSettings.getBrowseCategories().size()>0)	
         {
            links = "<p class=\"menusep\">Browse</p>";
         
            if(applicationSettings.getBrowseCategories().contains("title"))	
            { 	  
               links+="<a "+(highlight==2?"class=\"active\" ":"")+
                  "href=\"?action=browse&category=Title&order=asc\">Title (A-Z)</a>";
            }
         
            if(applicationSettings.getBrowseCategories().contains("affiliation"))	
            {
               links+="<a "+(highlight==3?"class=\"active\" ":"")+
                  "href=\"?action=browse&category=Affiliation&order=asc\">Institution (A-Z)</a>";
            }
         
            if(applicationSettings.getBrowseCategories().contains("date"))	
            {	
               links+="<a "+(highlight==5?"class=\"active\" ":"")+
                  "href=\"?action=browse&category=Date&order=asc\">By year, ascending</a>";
               links+="<a "+(highlight==4?"class=\"active\" ":"")+
                  "href=\"?action=browse&category=Date&order=desc\">By year, descending</a>";
            
            }	
         
         }
         return links;
      }
       private Document getDocument ( String url )
      {
         Document d = null;
         try {
            URL u = new URL (url);
            URLConnection conn = u.openConnection ();
            InputStream response2 = conn.getInputStream ();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance ();
            DocumentBuilder db = dbf.newDocumentBuilder();
            d = db.parse (response2);
         } 
             catch (Exception e) {
            }
         return d;
      }
   }
