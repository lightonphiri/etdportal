


import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;


/**
 * Date: 12 April 2009
 * @author Lawrence Webley
 * Deals with the ListRecords requests
 */
public class ListRecords extends Response 
{
    public ListRecords(HttpServletRequest req, Config settings)
    {
        super(req);
        
        //get components of the request
        String metadataPrefix = httpRequest.getParameter("metadataPrefix");        
        String from = httpRequest.getParameter("from");
        String until = httpRequest.getParameter("until");
        String set = httpRequest.getParameter("set");
        String resumptionToken = httpRequest.getParameter("resumptionToken");
        
        //set up the accepted parameters lists
        CheckParams cp = new CheckParams(httpRequest);
        String [] params = {"metadataPrefix","from","until","set"};
        String [] tParams = {"resumptionToken"};
        boolean badDates = false;
        
        //now work on creating the response.
        if(metadataPrefix != null && metadataPrefix.length() != 0 && cp.check(params))
        {            
            outputResponse.append(" <request verb=\"ListRecords\" ");              
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
                    from = "1970-00-00T00:00:00Z";
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
                until = "2500-00-00T00:00:00Z";
            }
            //support of sets is currently not available.
            if(set != null && set.length() != 0)
            {
                outputResponse.append("set=\"");
                outputResponse.append(set);
                outputResponse.append("\" \n\t");
                outputResponse.append("metadataPrefix=\"");
                outputResponse.append(metadataPrefix);
                outputResponse.append("\">\n\t");
                outputResponse.append(baseURL);
                outputResponse.append(" </request>\n");
                outputResponse.append(" <error code=\"noSetHierarchy\"/>\n");
                
            }else if(!badDates)
            {//actually do the search etc here. Everything up till here was just testing to see if args are ok
                outputResponse.append("metadataPrefix=\"");
                outputResponse.append(metadataPrefix);
                outputResponse.append("\">\n\t");
                outputResponse.append(baseURL);
                outputResponse.append(" </request>\n");;
                try
                {
                    //use floating config objects database connection
                    outputResponse.append(settings.dbCon.listRecords(from, until, metadataPrefix, false, 0));
                }
                catch(SQLException e)
                {
                    outputResponse.append("\t<error>Failed to connect to the database here... Perhaps it is under maintainance?\n");
                    outputResponse.append(e);
                    outputResponse.append("\t</error>\n");
                }
            }                 
        }//else if the resumption token exists and it is the only argument
        else if(resumptionToken != null && resumptionToken.length() != 0 && cp.check(tParams))
        {
            outputResponse.append(" <request verb=\"ListRecords\" resumptionToken=\"");
            outputResponse.append(resumptionToken);
            outputResponse.append("\">\n\t");
            outputResponse.append(baseURL);
            outputResponse.append("</request>\n");
            
            //check validity of resumption token
            if(cp.checkResumptionToken(resumptionToken, settings))
            {
                //pass the work of decoding the resumptionToken to the DatabaseConnection
                try{
                    outputResponse.append(settings.dbCon.listRecords(resumptionToken));
                }catch(SQLException e)
                {
                    outputResponse.append("\t<error>Failed to connect to the database... Perhaps it is under maintainance?\n");
                    outputResponse.append("\t</error>\n");
                }
            }
            else
            {
                outputResponse.append("\t<error code=\"badResumptionToken\"/>");
            }
        }
        else//bad arguments
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
