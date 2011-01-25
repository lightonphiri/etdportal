


import javax.servlet.http.HttpServletRequest;


/**
 * Date: 9 April 2009
 * @author Lawrence Webley
 * A class to handle the GetRecord request
 */
public class GetRecord extends Response
{    
    public GetRecord(HttpServletRequest req, Config settings)
    {
        super(req);
        
        //get the url request parameters
        String identifier = httpRequest.getParameter("identifier");
        String metadataPrefix = httpRequest.getParameter("metadataPrefix"); 
        
        //instantiate parmeter checking class with our httpRequest
        CheckParams paramsChecker = new CheckParams(httpRequest);
        
        //create list of accepted parameters, which we pass to the parameter 
        //checking class
        String [] params = {"identifier", "metadataPrefix"};
        
        //if the identifier and metadataPrefix are not null and not empty
        //AND they pass the parameter checking, then start construction the response
        if(identifier!=null && identifier.length() != 0
        && metadataPrefix != null && metadataPrefix.length() != 0 && paramsChecker.check(params))
        {
            //create request section of response
            outputResponse.append(" <request verb=\"GetRecord\" identifier=\"");
            outputResponse.append(identifier);
            outputResponse.append("\"\n");
            outputResponse.append("\tmetadataPrefix=\"");
            outputResponse.append(metadataPrefix);
            outputResponse.append("\">");
            outputResponse.append(baseURL);
            outputResponse.append("</request>\n");
            //now we need to search the sql database for the record
            try
            {                
                //use the config objects connection to the database to retrieve our record
                settings.dbCon.connect();
                outputResponse.append(settings.dbCon.getRecord(identifier, metadataPrefix));
            }
            catch(Exception e)
            {
                outputResponse.append("\t<error>Failed to connect to the database... Perhaps it is under maintainance?\n");
                outputResponse.append(e);
                outputResponse.append("\t</error>\n");
                settings.log.add("SQL Exception occured while trying to access the database: "+e);
            }
        }
        else
        {//bad args            
            String [] nullParams = {};
            //empty string buffer
            outputResponse = new StringBuffer();
            outputResponse.append(paramsChecker.getRequest(nullParams));
            outputResponse.append("\t<error code=\"badArgument\">Incorrect or inadequate arguments</error>\n");
        }
    }
    
}
