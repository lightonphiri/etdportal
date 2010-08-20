


import java.util.Enumeration;
import java.util.Scanner;
import javax.servlet.http.HttpServletRequest;

/**
 * Date: 20th April 2009
 * @author Lawrence
 * This class is called with the set of expected parameters for a particular request. 
 * If the parameters are in the incorrect format or there are additional unwanted parameters, 
 * then it will tell this to the calling class.
 */
public class CheckParams 
{
    private HttpServletRequest request;
    
    public CheckParams(HttpServletRequest req)
    {
        request = req;
    }
    
    //This method scans through the parameters of a particular request and will return false
    //if they are not in the string array paramter or if there are duplicates. If one of the 
    //items in the params array is "identifier" then it will also check for validity of the identifier
    public boolean check(String [] params)
    {
        //get the parameters names
        Enumeration e = request.getParameterNames();
        //initialize counter to 0
        int count = 0;
        //increment counter for each parameter (where a parameter is a key pair)
        while(e.hasMoreElements())
        {
            //get next parameter
            String key = (String)e.nextElement();
            //check to see if this paramter is one of the accepted ones.
            boolean isValid = false;
            for(int i = 0;i<params.length;i++)
            {
                if(params[i].equals(key) || key.equals("verb"))
                {
                    if(key.equals("identifier"))
                    {
                        isValid = checkID(request.getParameter("identifier"));
                    }
                    else
                    {
                        isValid = true;
                    }                    
                }
            }
            if(params.length == 0)
            {
                isValid = true;
            }
             
            if(!isValid)
            {
                return false;
            }
            count++;          
        }
        //if there are too many parameters
        if(count > params.length + 1)
        {
            return false;
        }
        //check for duplicates now       
        //normal getParams method doesnt check for duplicates, only returns one instance even if two are present
        //so we need to get the actual query string.
        String query = request.getQueryString();
        Scanner inLine = new Scanner(query);       
        int realParams = 0;
        //we set the delimter to the & symbol
        inLine.useDelimiter("&");
        //now we count how many args there are
        while(inLine.hasNext())
        {
            inLine.next();
            realParams++;      
        }        
        //reinitialze inLine so we can now extract data
        inLine = new Scanner(query);
        Scanner inWord;
        inLine.useDelimiter("&");
        String [] pNames = new String [realParams];
        int c = 0;
        while(inLine.hasNext())
        {
            inWord = new Scanner(inLine.next());
            inWord.useDelimiter("=");
            pNames[c] = inWord.next();
            c++; //ha ha, very funny.
        }
        
        //now check for duplicate keys        
        for(int i = 0;i<c; i++)
        {
            for(int j = i+1;j<c;j++)
            {
                if(pNames[i].equals(pNames[j]))
                {
                    return false;
                }
            }
        }
        return true;        
    }   
    
    //returns the xml request tag for a given set of parameters.
    public String getRequest(String [] params)
    {
        //get base url
        String baseURL = request.getRequestURL().toString();
        //make sure that the verb is the first variable - dunno if this is important
        String metaData = " <request verb=\""+request.getParameter("verb")+"\"";
         //get the parameters names
        Enumeration e = request.getParameterNames();
        while(e.hasMoreElements())
        {
            //get next key
            String key = (String)e.nextElement();
            //skip the key if its the verb, since we already delt with this
            if(!key.equals("verb"))
            {
                //if the key is a valid parameter, then print it in request
                //so we need to iterate through the list of valid parameters
                //prininting out matches
                for(int i = 0;i<params.length;i++)
                {
                    if(key.equals(params[i]))
                    {
                        //add the next variable
                        metaData = metaData +"\n\t"+ key+"=\""+request.getParameter(key)+"\"";
                    }
                }
                
            }
        }
        //finish off the tags
        metaData = metaData +">"+baseURL+"</request>\n";
        return metaData;
    }
    //fuck knows how to do this
    public boolean checkDateConsitancy(String date1, String date2)
    {        
        //first check that dates are valid.
        if(checkDate(date1) && checkDate(date2))
        {
            //then check that they are the same granularity (same length means the same in this case)
            if(date1.length() == date2.length())
            {
                //return true if dates meet both of these criteria
                return true;
            }
        }//otherwise they are not compatible dates, so return false;
        return false;
    }
    //check to see if the date is valid
    //ideas for this method and some code borrowed and modified 
    //from AbstractCatalog.java by Jeffrey A. Young, OCLC Online Computer Library Center
    public boolean checkDate(String date)
    {   
        //for yyyy-mm-dd granularity
        if (date.length() < 10
                || !Character.isDigit(date.charAt(0)) // YYYY
                || !Character.isDigit(date.charAt(1))
                || !Character.isDigit(date.charAt(2))
                || !Character.isDigit(date.charAt(3))
                || date.charAt(4) != '-'
                    || !Character.isDigit(date.charAt(5)) // MM
                    || !Character.isDigit(date.charAt(6))
                    || date.charAt(7) != '-'
                        || !Character.isDigit(date.charAt(8)) // DD
                        || !Character.isDigit(date.charAt(9))) {
            return false;
        }
        //for yyyy-mm-ddThh:mm:ssZ granularity
        if (date.length() > 10) {
            if (date.charAt(10) != 'T'
                || date.charAt(date.length()-1) != 'Z'
                    || !Character.isDigit(date.charAt(11)) // hh
                    || !Character.isDigit(date.charAt(12))
                    || date.charAt(13) != ':'
                        || !Character.isDigit(date.charAt(14)) // mm
                        || !Character.isDigit(date.charAt(15))
                        || date.charAt(16) != ':'
                            || !Character.isDigit(date.charAt(17)) // ss
                            || !Character.isDigit(date.charAt(18))) {
                return false;
            }
        }        
        //if too many characters.
        if (date.length() > 20)
            return false;
        
        return true;
    }
    //checks to see if an identifier is valid.
    public boolean checkID(String ID)
    {
        for(int i = 0;i < ID.length();i++)
        {
            if(ID.charAt(i) == '"')
            {
                return false;
            }
        }
        return true;
    }
    
    //checks to see if a given resumption token conforms to this servers standard
    //if not, returns false
    public boolean checkResumptionToken(String RT, Config settings)
    {
        /* format for a resumption token must match
         * requestType!fromDate!untilDate!metadataPrefix!cursor
         * So an example record would be
         * LR!2002-06-01T23:20:00Z!2004-08-03T21:20:00Z!oai_dc!100
         */
        
         //first check if the request type is correct
        if(!(RT.substring(0, 2).equals("LR")) & !(RT.substring(0, 2).equals("LI")))
        {            
            return false;
        }        
        else
        {//now check the ! delimeter
            if(RT.charAt(2) != '!')
            {                
                return false;
            }else
            {//now check from date - 20 chars long
                if(!checkDate(RT.substring(3, 23)))
                {                    
                    return false;                    
                }
                else
                {//check next ! delimeter
                    if(RT.charAt(23) != '!')
                    {                        
                        return false;
                    }
                    else//check next data - another 20 chars
                    {
                        if(!checkDate(RT.substring(24, 44)))
                        {                            
                            return false;
                        }
                        else//check next ! delimiter
                        {
                            if(RT.charAt(44) != '!')
                            {                                
                                return false;
                            }
                            else//check metadata prefix
                            {
                                try
                                {
                                    int i = 45;
                                    String prefix = "";

                                    //read in the prefix
                                    while(RT.charAt(i)!= '!')
                                    {
                                        prefix = prefix + RT.charAt(i);
                                        i++;

                                        //incase this is broken resumption token and 
                                        //there IS no next '!' delimiter then a 
                                        //StringIndexOutOfBoundsException will be thrown
                                        //sooner or later.
                                       
                                    }
                                    //now we should have the metadata prefix stored,
                                    //so lets compare it to the supported formats on 
                                    //the server. If it matchs, then its legal.
                                    boolean supported = false;

                                    for(int j = 0; j < settings.formatList.length;j++)
                                    {
                                        if(prefix.equals(settings.formatList[j].getPrefix()))
                                        {
                                            supported = true;
                                            break;
                                        }
                                    }
                                    if(!supported)
                                    {                                        
                                        return false;
                                    }
                                    else
                                    {//we have already checked last ! delimeter,
                                        //so now we only need to check cursor
                                        String cursor = RT.substring(i+1);
                                        for(int k = 0; k < cursor.length();k++)
                                        {
                                            if(!Character.isDigit(cursor.charAt(k)))
                                            {                                               
                                                return false;
                                            }
                                        }
                                    }//this is often thrown if a broken resumption token
                                //with no ! after its metadataPrefix is received
                                }catch(StringIndexOutOfBoundsException e)
                                {
                                    return false;
                                    
                                }
                            }
                        }
                    }                    
                }                
            }
        }
        return true;
    }
   
}
