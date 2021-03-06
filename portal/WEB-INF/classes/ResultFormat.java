/*
@author Tatenda M. Chipeperekwa
@date 25/06/09
@description This class is used to format the results that have been returned
	      from the browsing request.	
*/


   import java.io.IOException;
   import java.util.ArrayList;
   import java.util.Iterator;
   import java.util.List;
   import java.net.URL;
   
   import javax.servlet.*;
   import javax.servlet.http.*;
   
   import javax.xml.parsers.DocumentBuilder;
   import javax.xml.parsers.DocumentBuilderFactory;
   import javax.xml.parsers.ParserConfigurationException;

   import org.w3c.dom.Document;
   import org.w3c.dom.Element;
   import org.w3c.dom.NodeList;
   import org.xml.sax.SAXException;

   
   import java.io.*;
   import javax.xml.transform.Source;
   import javax.xml.transform.stream.StreamSource;
   import javax.xml.validation.*;
   import org.xml.sax.SAXException;
   import org.xml.sax.*;

   import javax.xml.parsers.*;
   import javax.xml.transform.*;
   import javax.xml.transform.dom.*;
   import javax.xml.transform.stream.*;

public class ResultFormat 
{
    
      private String html_document;//stores the html document to the returned by the query
      private String servletUrl;//Stores the URL of the servlet that handles the browse requests

    
       public ResultFormat()
      {
             html_document ="<p>No results to display yet...</p>"+"\n";

      }
    
    /**
     * Used to format the data to be displayed after the query to the database
     * 
     */
    public void create_document(String criteria,List<Record> recordsToFormat,int total,int offset,int limit,int lastDisplayed,String order,String configPath)
    {           
         
         html_document = "<div class=\"content\"><p>Showing results "+offset+" to "+ lastDisplayed+" of "+total+"</p>\n";

         String links = "<div class=\"links2\">\n<div class=\"links\">\n";

	//Page numbers for the search results,used to browse ahead 
	//or skip some results pages while browsing.
	
	int last_page = 0;//top index on the last page
	String full_link = "";//full page link for records
	String pageurl;//part of the page URL
	int current_page= (offset/limit + 1);//the current page being viewed
	int p = 0;
	List<String> pages = new ArrayList<String>();//list to store all the pages
	for(p = 1;p<total;p+=limit)
	{			   						pageurl=getServletUrl()+"?action=browse&category="+criteria+"&amp;maxresults="+limit+"&amp;start="+(p)+"&order="+order;
	  
	    if((p/limit + 1)==current_page)
	      full_link+= current_page;//deactivate the link to the current page
	    else
	      full_link+= "<a href="+pageurl+">"+(p/limit + 1)+"</a>\n";
	    
	  pages.add(full_link);
	  full_link = "";
	}
	last_page = p-limit;


	if(current_page!=1)
	  links+= "<span class=\"link_previous\"><a href="+getServletUrl()+"?action=browse&category="+criteria+"&amp;maxresults="+limit+"&amp;start="+1+"&order="+order+">First</a></span>\n";
	else
	  links+="<span class=\"link_previous\">First</span>\n";

	if ((offset - limit) > 0) 
	{//used to create the previous results link
	
	  String lessurl=getServletUrl()+"?action=browse&category="+criteria+"&amp;maxresults="+limit+"&amp;start=" + (offset - limit)+"&order="+order;               
	  links += "<span class=\"link_previous\"><a href="+lessurl+">Previous</a></span>"+"\n";
	}else
	{
	  links += "<span class=\"link_previous\">Previous</span>"+"\n";
	}
    
	      
	      
	      
	links +="<span class=\"link_pages\">"+"\n";
	
	if(current_page<=limit/2)//viewing the first limit/2 pages
	{
	  if(pages.size()<limit)
	  {
	    for(int p1 = 0;p1<pages.size();p1++)
	    {			  	
	      links += pages.get(p1)+"\n";
	    }
	  }else
	  {
	    for(int p1 = 0;p1<limit;p1++)
	    {			  	
	      links += pages.get(p1)+"\n";
	    }
	  
	  }
	}else if(pages.size()-current_page<10)//toward the end of the results
	{
	  for(int p1 =pages.size()-limit;p1<pages.size();p1++)
	  {			  	
	    links += pages.get(p1)+"\n";
	  }
	}else//in the middle of the search results
	{
	  for(int p1 = current_page-limit/2;p1<current_page+limit/2;p1++)
	  {			  	
	    links += pages.get(p1)+"\n";
	  }
	}
	
		
	links +="</span>"+"\n";	       
	  
	  
	  if ((offset + limit) < total) 
	  {   //if there are more results...display 
	    //the more link
	    
	    //construct the "more" link
	    String moreurl=getServletUrl()+"?action=browse&category="+criteria+"&amp;maxresults="+limit+"&amp;start=" + (offset + limit)+"&order="+order;
	  
	    links += "<span class=\"link_next\"><a href="+moreurl+">Next</a></span>"+"\n";
	  
	  }else
	  {
	    links += "<span class=\"link_next\">Next</span>"+"\n";
	  }
	  
	if(current_page!=pages.size())
	  links+= "<span class=\"link_next\"><a href="+getServletUrl()+"?action=browse&category="+criteria+"&amp;maxresults="+limit+"&amp;start="+last_page+"&order="+order+">Last"+"</a></span>\n";
	else
	 links+="<span class=\"link_next\">Last</span>\n";
	
	  links += "</div></div>\n";

	  html_document += links;     	      
	  
	  html_document += "<ol start=\"" + offset + "\">";

	  //Adding the recordsToFormat to be browsed to the page
	  //displays full records
	  for(int x = 0;x< recordsToFormat.size();x++)
	  {
	    Record recordToDisplay = recordsToFormat.get(x);
       
	    InputStream record_to_transform = null;
	    try {//converting the portalXML into an inputStream for XSLT trasformtion
	      record_to_transform = new ByteArrayInputStream(recordToDisplay.getPortalXML().getBytes("UTF-8"));
	    } catch (UnsupportedEncodingException e) {
	      ConfigurationManager.log.add("Error: \n"+e.toString());
	    }

	    String record = viewRecord(record_to_transform,configPath+"viewsnippets.xsl",configPath);
	    
	    //html_document += "<p><li>" + recordToDisplay.getPortalXML() + "</li></p>";
	    html_document += "<p><li>" + record + "</li></p>";
	  }   	
	
	  html_document += "</ol>";    
	  
	  html_document += links+"</div>";
	  
      	}
	
	
         /* 
   * public String viewXmlFile(InputStream response)
   * 
   * This method is used to parse the xml file, record.xml, that contains all the
   * results from the harvesting. It essentially takes the file and create
   * a DOM object that can be then manipulated to extract relevant information.
   *
   */
       public String viewRecord(InputStream response,String xslFilename, String configPath )
      {
         String test ="";
         try {
            // Create transformer factory
            TransformerFactory factory = TransformerFactory.newInstance();
    
            // Use the factory to create a template containing the xsl file
            Templates template = factory.newTemplates(new StreamSource(
                new FileInputStream(xslFilename)));
    
            // Use the template to create a transformer
            Transformer xformer = template.newTransformer();
    
            // Prepare the input file
            Source source = new StreamSource(response);
            source.setSystemId (configPath);
    
            // Create a new document to hold the results
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.newDocument();
            Result result = new DOMResult(doc);
    
            // Apply the xsl file to the source file and create the DOM tree
            xformer.transform(source, result);
	    
            return getStringFromDocument(doc);
        } catch (ParserConfigurationException e) {
            // An error occurred while creating an empty DOM document
	    test=test+"parseConfigError";
            ConfigurationManager.log.add("Error: \n"+e.toString());
        } catch (FileNotFoundException e) {
	  test=test+"IOError\n"+e.getMessage();
          ConfigurationManager.log.add("Error: \n"+e.toString());
        } catch (TransformerConfigurationException e) {
            // An error occurred in the XSL file
            test=test+"Transformer Configuration Error\n"+e.getMessage();
            ConfigurationManager.log.add("Error: \n"+e.toString());
        } catch (TransformerException e) {
            // An error occurred while applying the XSL file
	    test=test+"Transformer Application Error\n"+e.getMessage();
            ConfigurationManager.log.add("Error: \n"+e.toString());
        }
        return test;

      }
      //method to convert Document to String
      private String getStringFromDocument(Document doc)
      {
	  try
	  {
	    DOMSource domSource = new DOMSource(doc);
	    StringWriter writer = new StringWriter();
	    StreamResult result = new StreamResult(writer);
	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer = tf.newTransformer();
	    transformer.transform(domSource, result);
	    return writer.toString();
	  }
	  catch(TransformerException ex)
	  {
	    ConfigurationManager.log.add("Error: \n"+ex.toString());
	    return null;
	  }
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
         return html_document;        
      }
   
   }
   
