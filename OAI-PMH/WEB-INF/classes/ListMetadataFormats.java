

/**
 * Date: 10 April 2009
 * @author Lawrence Webley
 * Lists the metadata formats currently supported by this archive.
 */
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
public class ListMetadataFormats extends Response
{  
    public ListMetadataFormats(HttpServletRequest req, Config settings)
    {
        super(req);
        
        //get parameters for url
        String identifier = httpRequest.getParameter("identifier");
        
        //check parameters
        CheckParams cp = new CheckParams(httpRequest);
        //paramters if there is an identifier
        String [] params = {"identifier"};
        //no paramters
        String [] nParams = {};
        
        
        //first check if there is no identifier and that there are no other args
        if((identifier == null || identifier.length() == 0) && (cp.check(nParams)))
        {
            outputResponse.append(" <request verb=\"ListMetadataFormats\">\n");
            outputResponse.append("       ");
            outputResponse.append(baseURL);
            outputResponse.append("</request>\n");
            outputResponse.append(" <ListMetadataFormats>\n");
            try{
                outputResponse.append(settings.dbCon.getAllMetadataFormats());
            }   
            catch(SQLException e)
            {
                outputResponse.append("\t<error>Failed to connect to the database here... Perhaps it is under maintainance?\n");
                outputResponse.append(e);
                outputResponse.append("\t</error>\n");
            }
            outputResponse.append(" </ListMetadataFormats>\n");
           
        }//now we check if there is only that one identifier paramter
        else if (cp.check(params))
        {
            outputResponse.append(" <request verb=\"ListMetadataFormats\"\n");
            outputResponse.append("       identifier=\"");
            outputResponse.append(identifier);
            outputResponse.append("\">\n");
            outputResponse.append("       ");
            outputResponse.append(baseURL);
            outputResponse.append("</request>\n");
            try
            {
                //use the floating config objects DatabaseConnection instance
                outputResponse.append(settings.dbCon.getMetadataFormats(identifier));
            }
            catch(Exception e)
            {
                outputResponse.append("\t<error>Failed to connect to the database... Perhaps it is under maintainance?\n");
                outputResponse.append(e);
                outputResponse.append("\t</error>\n");
            }
        }//otherwise we have bad arguments
        else
        {
            outputResponse.append(cp.getRequest(params));
            outputResponse.append(" <error code=\"badArgument\"/>\n");
        }
    }
}
