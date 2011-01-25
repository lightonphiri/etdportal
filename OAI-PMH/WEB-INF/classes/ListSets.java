


import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;



/**
 * Date: 12 April 2009
 * @author Lawrence Webley
 * Services the (very brief) ListSets request.
 * At this point in time Sets are not supported, so this will need to be extended to work with sets.
 */
public class ListSets extends Response
{
    public ListSets(HttpServletRequest req, Config settings)
    {
        super(req);
        
        //get params
        String resumptionToken = httpRequest.getParameter("resumptionToken");
        
        //check params
        CheckParams cp = new CheckParams(httpRequest);
        String [] nParams = {};
        String [] params = {"resumptionToken"};
        
        //if a resumption token is found
        if(resumptionToken != null && resumptionToken.length() != 0 && cp.check(params))
        {
            outputResponse.append(" <request verb=\"ListSets\" resumptionToken=\"");
            outputResponse.append(resumptionToken);
            outputResponse.append("\">\n\t");
            outputResponse.append(baseURL);
            outputResponse.append("</request>\n");
            outputResponse.append(" <error code=\"badResumptionToken\">This server does not support flow control</error>\n");
            
        }//if no paramaters aside from verb
        else if (cp.check(nParams))
        {
            outputResponse.append(" <request verb=\"ListSets\">\n\t");
            outputResponse.append(baseURL);
            outputResponse.append("</request>\n");
            try
            {               
                outputResponse.append(settings.dbCon.listSets());                
            }
            catch(SQLException e)
            {                
                outputResponse.append(" <error>A Sql exception occured while trying to access the database! Please contact an admin for assistance.</error>\n");
                settings.log.add("SQL Exception occured while trying to access the database: "+e);
            }
        }//bad arguments
        else
        {
            outputResponse.append(cp.getRequest(nParams));
            outputResponse.append(" <error code=\"badArgument\"/>\n");
        }
    }
}
