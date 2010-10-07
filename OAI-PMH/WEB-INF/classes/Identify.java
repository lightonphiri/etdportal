


import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;

/**
 * Date: 10 April 2009
 * @author Lawrence Webley
 * Class to deal with identify requests.
 */
public class Identify extends Response
{    
    
    public Identify(HttpServletRequest req, Config settings)
    {   
        super(req);
        
        CheckParams ch = new CheckParams(httpRequest);        
        String [] params = {};//we have no parameters
        
        if(ch.check(params))
        {            
            outputResponse.append(" <request verb=\"Identify\">");
            outputResponse.append(baseURL);
            outputResponse.append("</request>\n");
            outputResponse.append(" <Identify>\n");
            outputResponse.append("   <repositoryName>");
            outputResponse.append(settings.repoName);
            outputResponse.append("</repositoryName>\n");
            outputResponse.append("   <baseURL>");
            outputResponse.append(baseURL);
            outputResponse.append("</baseURL>\n");
            outputResponse.append("   <protocolVersion>2.0</protocolVersion>\n");
            outputResponse.append("   <adminEmail>");
            outputResponse.append(settings.adminEmail);
            outputResponse.append("</adminEmail>\n");
            outputResponse.append("   <earliestDatestamp>");
            try{//get earliest date
            outputResponse.append(settings.dbCon.getEarliestDateStamp());
            }catch(SQLException e)
            {//if failed, set this to begining
                outputResponse.append("0000-00-00T00:00:00Z");
                e.printStackTrace();
            }
            outputResponse.append("</earliestDatestamp>\n");
            outputResponse.append("   <deletedRecord>persistent</deletedRecord>\n");
            outputResponse.append("   <granularity>YYYY-MM-DDThh:mm:ssZ</granularity>\n");
            outputResponse.append("   <description>\n");
            outputResponse.append("    <eprints\n");
            outputResponse.append("        xmlns=\"http://www.openarchives.org/OAI/1.1/eprints\"\n");
            outputResponse.append("        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
            outputResponse.append("        xsi:schemaLocation=\"http://www.openarchives.org/OAI/1.1/eprints\n");
            outputResponse.append("        http://www.openarchives.org/OAI/1.1/eprints.xsd\">\n");
            outputResponse.append("       <content>\n");
            outputResponse.append("         <URL>");
            outputResponse.append(settings.repoDescUrl);
            outputResponse.append("</URL>\n");
            outputResponse.append("          <text>");
            outputResponse.append(settings.repoDescText);
            outputResponse.append("</text>\n ");
            outputResponse.append("       </content>\n");
            outputResponse.append("       <metadataPolicy/>\n");
            outputResponse.append("       <dataPolicy/>\n");
            outputResponse.append("    </eprints>\n");
            outputResponse.append("   </description>\n");
            outputResponse.append(" </Identify>");
        }
        else
        {
            outputResponse.append(ch.getRequest(params));
            outputResponse.append(" <error code=\"badArgument\"/>\n");
        }
        
    }
    
    
}
