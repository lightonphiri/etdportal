


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * Date: 9 April 2009
 * @author Lawrence Webley
 * Writes out the generic response information at the beginning and end  of a 
 * OAI-PMH xml file, inserting the variable content in the correct spot
 */
public class ResponseFormatter
{
    //use a string buffer, because strings are immutable and therefore highly inefficient
    //when added many times.
    StringBuffer finalResponse = new StringBuffer();
    
    public ResponseFormatter()
    {        
        SimpleDateFormat UTCDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        UTCDateFormatter.setTimeZone(tz);
        
        finalResponse.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<OAI-PMH xmlns=\"http://www.openarchives.org/OAI/2.0/\"\n" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"+
                "         xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/\n" +
                "         http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd\">\n" +
                " <responseDate>");
        finalResponse.append((UTCDateFormatter.format(new Date())));
        finalResponse.append("</responseDate>\n");
    }
    
    public StringBuffer formatString(StringBuffer variableContent)
    {        
        finalResponse.append(variableContent);
        finalResponse.append("</OAI-PMH>");
        return finalResponse;
    }
}
