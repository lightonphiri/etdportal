/*
@author Tatenda M. Chipeperekwa
@date 25/06/09
@description The SearchEngine class is used to run a searh query
on the index created by the IndexFiles class using
the Lucene library.
*/


   import java.io.*;
   import org.apache.lucene.analysis.*;
   import org.apache.lucene.analysis.standard.StandardAnalyzer;
   import org.apache.lucene.document.*;
   import org.apache.lucene.index.*;
   import org.apache.lucene.search.*;
   import org.apache.lucene.queryParser.*;
   import java.net.URLEncoder;
   import java.util.ArrayList;
   import java.util.Iterator;
   import java.util.List;


    public class SearchEngine{
       private String results_page;        //stores results
       private String servletUrl;          //Stores the URL of the servlet that handles the browse requests
       private String realApplicationPath; // location of application servlet on server

       public SearchEngine ( String appPath )
       { 
          results_page=""; 
          realApplicationPath = appPath;
       }
     
       public String escapeHTML(String s)
      {
         s = s.replaceAll("&", "&amp;");
         s = s.replaceAll("<", "&lt;");
         s = s.replaceAll(">", "&gt;");
         s = s.replaceAll("\"", "&quot;");
         s = s.replaceAll("'", "&apos;");
         return s;
      }
     
       public String search (String user_query,String startat,String maxresults)
      {
	  ConfigurationManager applicationSettings = new ConfigurationManager();
	  applicationSettings.configureApplication(false);
	  setServletUrl(applicationSettings.getServletUrl());

         int lastDisplayed=0;			     //the value of the last result displayed	
	 boolean error = false;                       //used to control flow for error messages
         IndexSearcher searcher = null;               //the searcher used to open/search the index
         Query query = null;                          //the Query created by the QueryParser
         Hits hits = null;                            //the search results
         int startindex = 0;                          //the first index displayed on this page
         int maxpage    = 10;                         //the maximum items displayed on this page
         String queryString = null;                   //the query entered in the previous page
         String startVal    = null;                   //string version of startindex
         //String maxresults  = null;                 //string version of maxpage
         int thispage = 0;                            //used for the for/next either maxpage or
      					       	     //hits.length() - startindex - whichever is
      					                   //less
      					    
         results_page="";                             //stores results

             try 
         {
            searcher = new IndexSearcher(applicationSettings.getIndexDirectory () + "/index");
              //applicationSettings.getIndexDirectory());      //create an indexSearcher for our page
	  } 
             catch (Exception e) 
            {			                              //any error that happens is probably due
            			                              //to a permission problem or non-existant
            			                              //or otherwise corrupt index
               results_page+=
	  	  "<p>ERROR opening the Index - contact sysadmin!</p>\n"+
                "<p>Error message:"+escapeHTML(e.getMessage())+"</p>\n";   
               error = true;                                  //don't do anything else
            }
      
      
         if (error == false) 
         {                                      
            queryString =user_query;                         //get the search criteria
            startVal    = startat;                           //get the start index
            try 
            {
               maxpage    = Integer.parseInt(maxresults);    //parse the max results first
               startindex = Integer.parseInt(startVal);      //then the start index  
            } 
            catch (Exception e) 
	    { 
		//if an error occurs in converting start index and maximum page values we continue and use default values
	    }
         
         
            if (queryString == null)//no search query specified
            {   error=true; 
	        results_page+=
                "<p>Please specify a search query above.<p>";
     	    }
         
            Analyzer analyzer = new StandardAnalyzer();//construct our analyzer
            try 
            {
               QueryParser qp = new QueryParser("portalXML", analyzer);   //default field for query search is the "portalXML" field;
										  
	       
	        //qp.setPhraseSlop(5);                                    //allow results that are matching but not exact to be displayed
	       
               query = qp.parse(queryString);                           //query and construct the Query object
               				      
            } 
                catch (ParseException e) 
               {		      	                                     // "operator error"
               				                              //send error HTML
               				      
               
                  results_page+=
                     "<p>Error while parsing query:"+ escapeHTML(e.getMessage())+"</p>\n";
               
                  error = true;                
		}
         }
      
         try
         {
			    if (error == false && searcher != null)
			    {					         
					    thispage = maxpage;                                      // default last element to maxpage
					    hits = searcher.search(query);                           // run the query 
							  
					    if (hits.length() == 0) 
					    {                               			     // if we got no results tell the user
					           
						results_page+="<p> No results could match your query.</p>\n";
					    
						error = true;                                        // don't bother with the rest of the page
												     
					    }
			    }
         
            if (error == false && searcher != null) 
            {     
			
		lastDisplayed=startindex + maxpage;
	    	
		if ((startindex + maxpage) > hits.length()) 
		{
		    thispage = hits.length() - startindex;         // set the max index to maxpage or last
        	                                                   // actual search result,whichever is less	
		    lastDisplayed=hits.length();	
		} 
            
	    
	       
	    
	    
		results_page += "<p>Showing Results "+(startindex+1)+" to "+lastDisplayed+" of "+hits.length()+"</p>";
		
	String links = "<div class=\"links\">\n";
		//Page numbers for the search results,used to browse ahead 
	//or skip some results pages while browsing.
	
	int last_page = 0;//top index on the last page
	String full_link = "";//full page link for records
	String pageurl;//part of the page URL
	int current_page= (startindex/maxpage + 1);//the current page being viewed
	int p = 0;
	List<String> pages = new ArrayList<String>();//list to store all the pages
	for(p = 0;p<hits.length();p+=maxpage)
	{   pageurl=getServletUrl()+"?action=search&amp;query="+URLEncoder.encode(queryString)+  "&amp;maxresults="+maxpage+"&amp;start=" + (p);
	  
	    if((p/maxpage + 1)==current_page)
	      full_link+= current_page;//deactivate the link to the current page
	    else
	      full_link+= "<a href="+pageurl+">"+(p/maxpage + 1)+"</a>\n";
	    
	  pages.add(full_link);
	  full_link = "";
	}
	last_page = p-maxpage;

	
	if(current_page!=1)
	  links+= "<span class=\"link_previous\"><a href="+getServletUrl()+"?action=search&amp;query="+URLEncoder.encode(queryString)+  "&amp;maxresults="+maxpage+"&amp;start=" + 0 +">First</a></span>\n";
	else
	  links+="<span class=\"link_previous\">First</span>\n";

	if ( (startindex - maxpage) >= 0) 
	{//used to create the previous results link
	
	    String lessurl=getServletUrl()+"?action=search&amp;query="+URLEncoder.encode(queryString)+  "&amp;maxresults="+maxpage+"&amp;start=" + (startindex - maxpage);
	
	    links += "<span class=\"link_previous\"><a href=\""+lessurl+"\">Previous</a></span>\n";
	}else
	{
	    links += "<span class=\"link_previous\">Previous</span>\n";
	}

	      
	      
	      
	links +="<span class=\"link_pages\">"+"\n";
	
	if(current_page<=maxpage/2)//viewing the first limit/2 pages
	{
	  if(pages.size()<maxpage)
	  {
	    for(int p1 = 0;p1<pages.size();p1++)
	    {			  	
	      links += pages.get(p1)+"\n";
	    }
	  }else
	  {
	    for(int p1 = 0;p1<maxpage;p1++)
	    {			  	
	      links += pages.get(p1)+"\n";
	    }
	  
	  }
	  
	}else if(pages.size()-current_page<10)//toward the end of the results
	{
	  for(int p1 =pages.size()-maxpage;p1<pages.size();p1++)
	  {			  	
	    links += pages.get(p1)+"\n";
	  }
	}else//in the middle of the search results
	{
	  for(int p1 = current_page-maxpage/2;p1<current_page+maxpage/2;p1++)
	  {			  	
	    links += pages.get(p1)+"\n";
	  }
	}
	
		
	links +="</span>"+"\n";	       
	 
	if ( (startindex + maxpage) < hits.length()) 
	{   //if there are more results...display 
	    //the more link
	    
	    //construct the "more" link
	    String moreurl=getServletUrl()+"?action=search&amp;query="+URLEncoder.encode(queryString)+  "&amp;maxresults="+maxpage+"&amp;start=" + (startindex + maxpage);
	
	    links += "<span class=\"link_next\"><a href=\""+moreurl+"\">Next</a></span>\n";
	}else
	{
	    links += "<span class=\"link_next\">Next</span>\n";
	}
	 
	if(current_page!=pages.size())
	  links+= "<span class=\"link_next\"><a href=\""+getServletUrl()+"?action=search&amp;query="+URLEncoder.encode(queryString)+  "&amp;maxresults="+maxpage+"&amp;start=" + last_page +"\">Last</a></span>\n";
	else
	 links+="<span class=\"link_next\">Last</span>\n";

	       links += "</div>";		  

               results_page += links;     	      
            	
	       results_page+=
				"<ol start=\"" + (startindex+1) + "\">"+"\n";
				
            

				for (int i = startindex; i < (thispage + startindex); i++)
				{//for each element
				
				    results_page+=
				    "<p><li>"+"\n";
				
				    Document doc = hits.doc(i);                 //get the next document 
				  
				    String repository_identifier = doc.get("repository_identifier");  //used to form URL for displaying record
				    String portalXML = doc.get("portalXML");  //used to form HTML view for displaying record
				    InputStream record_to_transform = null;
				    try {//converting the portalXML into an inputStream for XSLT trasformtion
				      record_to_transform = new ByteArrayInputStream(portalXML.getBytes("UTF-8"));

  				      results_page += (new ResultFormat()).viewRecord(record_to_transform,realApplicationPath+"viewsnippets.xsl",realApplicationPath);

				    } catch (UnsupportedEncodingException e) {
				      ConfigurationManager.log.add("Error: \n"+e.toString());
				    }
				
				results_page += "</li></p>"+"\n";
				
				}

            
			      results_page += "</ol>" + "\n";
	    
			      results_page += links;
          	
			      //closing the searcher
			      if (searcher != null)
				searcher.close();
	       
	  }     
         }
         catch(Exception e)
         {
	 	//Error provision for any exceptions that might have been missed
		results_page+="Error processing search query.\n"+e.getMessage();
	  }		
        

          
         return results_page;//returning the page
      }

      //Accessor method for servletUrl 
      public String getServletUrl()
      {
		return servletUrl;
      }

      //Mutator method for servletUrl	
      public void setServletUrl(String param)
      {
		servletUrl=param;
      }	
     
       public String toString()
      {
         return results_page;
       
      }
     
     
   }
