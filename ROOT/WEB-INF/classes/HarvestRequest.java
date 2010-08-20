/*
@author Tatenda M. Chipeperekwa
@date 24/04/2009
This class contains all the OAI-PMH requests for the
metadata harvester.

*/
   import java.io.*;
   import java.net.HttpURLConnection;
   import java.net.ProtocolException;
   import java.net.URL;
   import java.net.URLConnection;
   import java.util.Scanner;


public class HarvestRequest
{
	

   /**
   * Sends a ListRecords request to the servlet controlling access to the database.
   *<p>
   *The ListRecords method accepts two paramters as detailed above.
   *The URL of the servlet(or server) is concatenated to the verb
   *if the verb is well formed, and this completes a request.A 
   *connection is made to the servlet(or server) and a response
   *is captured and returned.
   *
   * @param  serverUrl - The URL of the servlet
   * @param  verb - all the request parameters
   * @return Returns records that have been harvested from the repository
   *
   */
       public InputStream ListRecords(String verb,String serverUrl)
      {
        return performRequest(verb,serverUrl);     
      }
      
   /**
   * Sends a GetRecord request to the servlet controlling access to the database.
   *<p>
   *The GetRecord method accepts two paramters as detailed above.
   *The URL of the servlet(or server) is concatenated to the verb
   *if the verb is well formed, and this completes a request.A 
   *connection is made to the servlet(or server) and a response
   *is captured and returned.
   * @param  serverUrl - The URL of the servlet
   * @param  verb - all the request parameters
   * @return xmldata - Returns an item in the specified metadata format as a record.
   * 
   */
       public InputStream GetRecord(String verb,String serverUrl)
      {
         InputStream i = null;
         while (i == null)
         {
            i = performRequest(verb,serverUrl);
         }   
         return i;
//            return performRequest(verb,serverUrl);
      }
   /**
   * Sends an Identify request to the servlet controlling access to the database.
   *<p>
   *The Identify method accepts two paramters as detailed above.
   *The URL of the servlet(or server) is concatenated to the verb
   *if the verb is well formed, and this completes a request.A 
   *connection is made to the servlet(or server) and a response
   *is captured and returned.
   *
   * @param  serverUrl - The URL of the servlet
   * @param  verb - all the request parameters
   * @return Returns information about the repository containing the metadata items.
   * 
   *
   */
       public InputStream Identify(String verb,String serverUrl)
      {
         return performRequest(verb,serverUrl);     
      }
   	
   /**
   * Sends a ListIdentifiers request to the servlet controlling access to the database.
   *<p>
   *The ListIdentifiers method accepts two paramters as detailed above.
   *The URL of the servlet(or server) is concatenated to the verb
   *if the verb is well formed, and this completes a request.A 
   *connection is made to the servlet(or server) and a response
   *is captured and returned.
   *
   * @param  serverUrl - The URL of the servlet
   * @param  verb - all the request parameters
   * @return Returns only headers of records in the repository
   *
   *
   */
       public  InputStream ListIdentifiers(String verb,String serverUrl)
      {
                 	     
         return performRequest(verb,serverUrl);     
      }
   
   /**
   * Sends a ListMetadataFormats request to the servlet controlling access to the database.
   *<p>
   *The ListMetadataFormats method accepts two paramters as detailed above.
   *The URL of the servlet(or server) is concatenated to the verb
   *if the verb is well formed, and this completes a request.A 
   *connection is made to the servlet(or server) and a response
   *is captured and returned.
   * @param  serverUrl - The URL of the servlet
   * @param  verb - all the request parameters
   * @return Returns the metadata formats in the repository
   *
   *
   */
       public InputStream ListMetadataFormats(String  verb,String serverUrl)
      {
         InputStream i = null;
         while (i == null)
         {
            i = performRequest(verb,serverUrl);
         }   
         return i;
      
      }
   	
   
   /** Sends a ListSets request to the servlet controlling access to the database.
   *<p>
   *The ListSets method accepts two paramters as detailed above.
   *The URL of the servlet(or server) is concatenated to the verb
   *if the verb is well formed, and this completes a request.A 
   *connection is made to the servlet(or server) and a response
   *is captured and returned.
   * @param  serverUrl - The URL of the servlet
   * @param  verb - all the request parameters
   * @return Returns the set structure of the repository
   * 
   *
   */
       public InputStream ListSets(String verb,String serverUrl)
      {

         return performRequest(verb,serverUrl);
      
      }
      
      
      
      /**
      *Generic private method used to carry out requests on the repository specified in the
      *config file.
      * @param  serverUrl - The URL of the servlet
      * @param  verb - all the request parameters
      * @return Returns the response from the Central Repository.
      *
      */
      private InputStream performRequest(String verb , String serverUrl)
      {
          InputStream response=null;
      	  URLConnection conn=null;
         try
         {
            /*
         	*Creating a connection to send data to the servlet
         	*/
            String urlStr =  serverUrl;//stores servlet URL

            if ( verb != null &&  verb.length () > 0)
            {
               urlStr += "?" +  verb;//completes request if verb is well formed
            }
            URL url = new URL(urlStr);
            conn = url.openConnection ();//sending the complete request to the servlet
            
          	/*
         	*Receiving the response from the servlet
         	*/
		response = conn.getInputStream();
         } 
         catch (IOException ioe)
         {
            response = null;
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
         	     
	 return response;
      
      }
      
      
      
   
   }

