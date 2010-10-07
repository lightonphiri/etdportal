

/**
 * Date: 10 April 2009
 * @author Lawrence Webley
 */
import javax.servlet.http.HttpServletRequest;
//A generic layout for a servlet response. Extend this in order to add a new 
//verb response.
public abstract class Response 
{
    StringBuffer outputResponse = new StringBuffer();
    HttpServletRequest httpRequest;
    StringBuffer baseURL = new StringBuffer();
    
    public Response(HttpServletRequest req)
    {
        httpRequest = req;
        baseURL.append(httpRequest.getRequestURL().toString());
    }
    public StringBuffer getFormattedResponse()
    {
        ResponseFormatter formatter = new ResponseFormatter();
        outputResponse = formatter.formatString(outputResponse);
        return outputResponse;
    }
}
