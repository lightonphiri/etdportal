

/**
 * Date: 9 - 12 April 2009
 * @author Lawrence Webley
 * Provides OAI-PMH repository support to a sql database.
 */
import java.io.*;
import java.sql.SQLException;
import javax.servlet.http.*;
import javax.servlet.*;
import javax.xml.parsers.ParserConfigurationException;

public class OAI_servlet extends HttpServlet
{    
    //doServe is a variable that tracks whether the servlet has sufficient data
    //to provide a OAI-PMH style response.
    private boolean doServe;

    //establish a configuration object that will be passed around the rest
    //of the program.
    private Config settings;
    
    //constructor - code here is only run once, at the startup of the webserver
    @Override
    public void init ( ServletConfig config )
     throws ServletException
    {
        //make sure the parent constructor is run
        super.init (config);
        
        System.setProperty ("file.encoding", "UTF-8");

        doServe = true;
        //if the config object fails to instantiate, then we know that we 
        //have insufficient data to continue, so rather than terminating the program
        //we will change the servlet responses' to requests to reflect this.
        try
        {
            settings = new Config(this);
        }catch(IOException e)
        {
            settings.log.add(e.toString());
            doServe = false;
        }catch(ParserConfigurationException e)
        {
            settings.log.add(e.toString());
            doServe = false;
        }catch(ClassNotFoundException e)
        {
            settings.log.add(e.toString());
            doServe = false;
        }catch(SQLException e)
        {
            settings.log.add(e.toString());
            doServe = false;
        }
        catch(Exception e)
        {
            settings.log.add(e.toString());
            doServe = false;
        }
        
    }
    
    //provide the get response, this method is run for every web request received
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        //declare our response class, which we will instantiate as a particular
        //subclass of response depending on what our verb is.
        Response verbResponse;
        //get the printWriter object with which we will print out data
        response.setContentType ("text/xml");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();       
        if(doServe)
            {
            
            //============ProcessRequest==================

            //Find out which of the 6 basic operations we are serving
            String operation = request.getParameter("verb");
            //if no verb is specified set it to some invalid string so we dont get
            //nullpointer exceptions
            if(operation == null || operation.length() == 0)
            {
                operation = "badVerb";
            }

            //==========OPERATION 1 - GetRecord===========
            if(operation.equals("GetRecord"))
            {
                //process get record request                     
                verbResponse = new GetRecord(request, settings);
                StringBuffer metaData = verbResponse.getFormattedResponse();
                out.println(metaData);
            }
            //==========OPERATION 2 - Identify=========== 
            else if(operation.equals("Identify"))
            {
                //process identify request
                verbResponse = new Identify(request, settings);
                StringBuffer metaData = verbResponse.getFormattedResponse();
                out.println(metaData);
            }

            //==========OPERATION 3 - ListIdentifiers=========== 
            else if(operation.equals("ListIdentifiers"))
            {
                //list all identifiers                
                verbResponse = new ListIdentifiers(request, settings);
                StringBuffer metaData = verbResponse.getFormattedResponse();
                out.println(metaData);
            }

            //==========OPERATION 4 - ListMetadataFormats=========== 
            else if(operation.equals("ListMetadataFormats"))
            {
                //list metadata formats available .....
                verbResponse = new ListMetadataFormats(request, settings);
                StringBuffer metaData = verbResponse.getFormattedResponse();
                out.println(metaData);
            }

            //==========OPERATION 5 - ListRecords=========== 
            else if(operation.equals("ListRecords"))
            {
                //Lists all matching records.....
                verbResponse = new ListRecords(request, settings);
                StringBuffer outputXml = verbResponse.getFormattedResponse();
                out.println(outputXml);
            }

            //==========OPERATION 6 - ListSets=========== 
            else if(operation.equals("ListSets"))
            {
                //Lists available sets .....
                verbResponse = new ListSets(request, settings);
                StringBuffer outputXml = verbResponse.getFormattedResponse();
                out.println(outputXml);
            }
            
            //===Condition if verb is not matched
            else
            {
                ResponseFormatter formatter = new ResponseFormatter();
                StringBuffer error = new StringBuffer();
                String baseURL = request.getRequestURL().toString();
                error.append("\t<request>");
                error.append(baseURL);
                error.append("</request>\n");
                error.append("\t<error code=\"badVerb\">Illegal OAI verb</error>\n");
                StringBuffer outputXml = formatter.formatString(error);
                out.println(outputXml);
            }
        }else
        {
            settings.log.add("Incorrect or missing Config file contents! Please fix this" +
                    " before trying to run this servlet again!");
        }
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        //throw the params accross to the doGet method and let it deal with it.
        doGet(request, response);
    }
}
