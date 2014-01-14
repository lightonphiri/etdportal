import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;


/**
 * Date 12 April 2009
 * @author Lawrence Webley
 */
public class ListIdentifiers extends Response
{
    public ListIdentifiers(HttpServletRequest req, Config settings)
    {
        super(req);
        
        //get all possible valid args from url
        String metadataPrefix = httpRequest.getParameter("metadataPrefix");        
        String from = httpRequest.getParameter("from");
        String until = httpRequest.getParameter("until");
        String set = httpRequest.getParameter("set");
        String resumptionToken = httpRequest.getParameter("resumptionToken");
        
        //check the parameters
        CheckParams cp = new CheckParams(httpRequest);
        //accepted parameters for standard operation
        String [] params = {"metadataPrefix","from","until","set"};
        //accepted parameters for resumption (only the one)
        String [] tParams= {"resumptionToken"};
        
        //boolean to keep track of whether the dates are valid
        boolean badDates = false;
        
        //now work on creating the response.
        if(metadataPrefix != null && metadataPrefix.length() != 0 && cp.check(params))
        {
            outputResponse.append(" <request verb=\"ListIdentifiers\" ");
              
            //check from date for validity
            if(from != null && from.length() != 0 )
            {
                if((until == null && cp.checkDate(from)) 
                        || (until != null && cp.checkDateConsitancy(from, until)))
                {
                    outputResponse.append("from=\"");
                    outputResponse.append(from);
                    outputResponse.append("\"\n\t");
                }else
                {
                    badDates = true;
                }
            }else
            {            
                //initialize default 'from' date
                try{
                    from = settings.dbCon.getEarliestDateStamp().toString();
                }catch(SQLException e)
                {
                    from = "1970-01-01T00:00:00Z";
                }
            }
            
            if(until != null && until.length() != 0)
            {
                if((from == null && cp.checkDate(until)) 
                        || (from != null && cp.checkDateConsitancy(from, until)))
                {
                    outputResponse.append("until=\"");
                    outputResponse.append(until);
                    outputResponse.append("\"\n\t");
                }else
                {
                    badDates = true;
                }
            }else
            {
                //initialize default 'until' date - a few hundred years in the future!                
                until = "2037-01-01T00:00:00Z";
            }                        
                
            if(!badDates)
            {//actually do the search here. Everything previous to here was just testing to see if args are ok
                //and writting general request information to the response
                outputResponse.append("metadataPrefix=\"");
                outputResponse.append(metadataPrefix);
                outputResponse.append("\">\n\t");
                outputResponse.append(baseURL);
                outputResponse.append(" </request>\n");
                try
                {
                    if(set != null && set.length() != 0) //if we have a set we need to search in
                    {
                         outputResponse.append(settings.dbCon.listRecords(from, until, metadataPrefix, true, set, 0, -1, ""));
                    }
                    else
                    { //otherwise we have no sets to search in
                        //use DatabaseConnection in the floating Config object to get
                        outputResponse.append(settings.dbCon.listRecords(from, until, metadataPrefix, true, "", 0, -1, ""));
                    }
                }
                catch(SQLException e)
                {
                    outputResponse.append("\t<error>Failed to connect to the database... Perhaps it is under maintainance?\n");
                    outputResponse.append("\t</error>\n");
                    settings.log.add("SQL Exception occured while trying to access the database: "+e);
                }
            }                 
        }//else if the resumption token exists and it is the only argument
        else if(resumptionToken != null && resumptionToken.length() != 0 && (cp.check(tParams)))
        {
            outputResponse.append(" <request verb=\"ListIdentifiers\" resumptionToken=\"");
            outputResponse.append(resumptionToken);
            outputResponse.append("\">\n\t");
            outputResponse.append(baseURL);
            outputResponse.append("</request>\n");
            
             //check validity of resumption token
            if(cp.checkResumptionToken(resumptionToken, settings))
            {
                //pass the work of decoding the resumptionToken to the DatabaseConnection
                try{
                    outputResponse.append(settings.dbCon.listRecords(true, resumptionToken));
                }catch(SQLException e)
                {
                    outputResponse.append("\t<error>Failed to connect to the database... Perhaps it is under maintainance?\n");
                    outputResponse.append("\t</error>\n");
                    settings.log.add("SQL Exception occured while trying to access the database: "+e);
                }
            }
            else
            {
                outputResponse.append("\t<error code=\"badResumptionToken\"/>");
            }
        }
        else//if none of the above match, its obvious we have some bad arguments
        {
            outputResponse.append(cp.getRequest(params));
            outputResponse.append(" <error code=\"badArgument\"/>\n");
        }
        if(badDates)
        {
            outputResponse = new StringBuffer();
            outputResponse.append(" <request verb=\"ListRecords\"/>\n");
            outputResponse.append(" <error code=\"badArgument\">Error in date granularity</error>");
        }
    }
}
