import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.validation.SchemaFactory;

/**
 * Class to parse and store the response to an OAI Request
 * @author Alexander van Olst
 * @author Lawrence Webley
 # @author Hussein Suleman
 * @version 1.9.6.24
 */
public class OAIResponseHandler
{
    /** The response from the server */
    String response;
    /** repository configuration */
    Repository rep;
    /** database configuration */
    Config conf;

    /**
     * Constructor that takes a input stream from a response from a repository server
     * and then creates a document instance out of it.
     * @param is the input stream response from a repository server
     * @param config the harvester configuration object containing the settigns
     * pertinent to this harvest.
     */
//   public OAIResponseHandler ( Config aConf, InputStream is, Repository r ) 
   public OAIResponseHandler ( Config aConf, String res, Repository r ) 
    throws Exception
   {
      rep = r;
      conf = aConf;
      response = res;

/*      BufferedReader input = new BufferedReader(new InputStreamReader(is, "UTF-8")); 
      StringBuilder strB = new StringBuilder();
      String str;
      while (null != (str = input.readLine())) 
         strB.append(str).append("\n"); 
      input.close();
      String response = strB;
*/
/*      Scanner in = new Scanner(is);
      response = new StringBuffer("");
      while(in.hasNext())
      {
         response.append(in.nextLine() + "\n");
      }
*/
      conf.log.add(rep.getID() + ": Downloaded document with size: "+response.length ());
      if (response.indexOf ("<error") >= 0)
         conf.log.add(rep.getID() + ": OAI-PMH error in response: "+response);

/*      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);
      String line = null;
System.out.println ("3a");
      while ( (line = br.readLine()) != null)
      {
System.out.println ("3b "+line);
         response.append(line + "\n");
      }                                                                                  
System.out.println ("3c");
*/
   }

   /**
    * Uses regex to seperate out records and then store them in the database.
    * Uses the <code>getRecords</code> method to retrieve the
    * seperated out records.
    */
   public void store () 
    throws Exception
   {
      ArrayList<OAIRecord> results = getRecords(); // get a list of the records in the document
      Database db = new Database (conf);
      if(! db.connect())
         throw new Exception("Error: Failed to store records in local SQL database!");

      //If available, get the complete size of the repo.
      setCompleteListSize();

      //factory for the W3C XML Schema language
      SchemaFactory factory =  SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

      if (results != null)
      {
         for (int i = 0; i < results.size(); i++)
         {
            OAIRecord rec = results.get(i);
            if(rec.selfValidate(factory, conf))
            {
//               db.addToBatch (rec, rep);
               db.saveRecord (rec, rep);
               rep.cursor++;
            }
            else
            {
               conf.log.add("Record with ID: ("+rec.getID()+") failed validation and was not added!",
                       "Record with ID: ("+rec.getID()+") failed validation and was not added!");
            }
         }
         //execute the batch run to add the records to the database
//         db.executeBatch();
      }
      db.disconnect ();      
   }

    /**
     * Decomposes the xml received from the server into its constituent records
     * and then returns them as an <code>ArrayList</code>.
     * @return an array list of {@link Harvester.OAIRecord}s.
     */
    public ArrayList<OAIRecord> getRecords() 
     throws Exception
    {
        ArrayList<OAIRecord> result = new ArrayList<OAIRecord>();
        Pattern p = Pattern.compile("< *record[^>]*>.*?< */ *record *>", Pattern.CANON_EQ | Pattern.DOTALL);
        Matcher recordMatch = p.matcher(response);
        //Iterate through records
        while(recordMatch.find())
        {            
            String record = response.substring(recordMatch.start(), recordMatch.end());

            //Find identifier
            Pattern identPattern = Pattern.compile("< *identifier[^>]*>[^<]*< */ *identifier *>", Pattern.CANON_EQ | Pattern.DOTALL);
            Matcher identMatch = identPattern.matcher(record);
            if(identMatch.find())
            {
                String identifier = extractTagValue(record.substring(identMatch.start(), identMatch.end()));
                result.add(toRecord(record, identifier));                
            }
            else
            {
                conf.log.add("Malformed Record encountered - No Identifier! Ignoring and continuing",
                        "Malformed Record encountered - No Identifier! Ignoring and continuing");
            }            
        }

        Pattern nonXml = Pattern.compile("<\\?xml .*?>");
        Matcher nonXmlMatch = nonXml.matcher(response);
        if(!nonXmlMatch.find())
        {
            conf.log.add("A non-xml reply was received from the target host:\n"+response,
                    "A non-xml reply was received from the target host, check log for details");            
            throw new Exception("Non-XML response from target host.");
        }

        //Match error case as well
        return result;
    }

    /**
     * Takes a tag like <resmptionToken>blahblah</resumptionToken>
     * and returns the blahblah part between tags.
     * @param tag the tag we remove the data from
     */
    public String extractTagValue(String tag)
    {
        Pattern p = Pattern.compile(">.*<", Pattern.CANON_EQ | Pattern.DOTALL);
        Matcher m = p.matcher(tag);
        m.find();
        return tag.substring(m.start()+1, m.end()-1);
    }
    

    /**
     * Attempts to set the complete list size in the harvest configuration file,
     * so that one can track the progress of the harvest by periodically checking
     * the harvest file. If the complete list size is unknown, then it is set
     * to -1 in the config file, which indicates an unknown value.
     */
    public void setCompleteListSize()
    {
        //<resumptionToken completeListSize='819' cursor='0'>1950426</resumptionToken> 
        Pattern p = Pattern.compile("< *resumptionToken.*?>[^>]*< */ *resumptionToken *>", Pattern.CANON_EQ | Pattern.DOTALL);
        Matcher m = p.matcher(response);
        if(m.find()) // If there is a resumptionToken
        {
            String match = response.substring(m.start(), m.end());            
            Pattern list = Pattern.compile("completeListSize='[0-9]+'", Pattern.CANON_EQ);
            Matcher listMatch = list.matcher(match);
            if(listMatch.find()) //If there is a completeListSize
            {                
                String listSizeString = match.substring(listMatch.start(), listMatch.end());                
                Pattern digit = Pattern.compile("[0-9]+", Pattern.CANON_EQ);
                Matcher digitMatch = digit.matcher(listSizeString);
                digitMatch.find(); 
                
                int listSize = Integer.parseInt(listSizeString.substring(digitMatch.start(), digitMatch.end()));
                rep.completeListSize = listSize;
            }
            else
            {
                rep.completeListSize = -1;
            }
        }
        else
        {
            rep.completeListSize = -1;
        }        
    }

    /**
     * Gets the resumption token out of the xml response received from the repository
     * server.
     * @return the resumption token.
     */
    public String getResumptionToken()
    { // get the resumption token from the repsonse
        Pattern p = Pattern.compile("< *resumptionToken.*?>[^<]*< */ *resumptionToken *>", Pattern.CANON_EQ | Pattern.DOTALL);
        Matcher m = p.matcher(response);

        if(m.find())
        {
            String longResump = response.substring(m.start(), m.end());            
            return extractTagValue(longResump);
        }
        else
        {
            return "";
        }        
    }   

    /**
     * Takes an string containing a record, along with the records
     * id string, and returns the record stored in an {@link Harvester.OAIRecord}
     * object.
     * @param record the string record we are to turn into a record object
     * @param id the identification number of the record
     * @return the record stored in a <code>OAIRecord</code> object
     */
    public OAIRecord toRecord(String record, String id)
    {
        String identifier = id; // get the identifier
        String source = rep.getID();

        /*TO BE FIXED AT A LATER DATE
         * Even though we have specifically requested a certain type of record from
         * the server repository, we shouldnt assume they got it right and gave us
         * only the requested type of records. We should read the metadate type
         * from the record xml itself and then use that.
         */
        String metadataType = rep.getMetadataFormat();

        Pattern p = Pattern.compile("< *header *status *= *\"deleted\" *>");
        Matcher m = p.matcher(record);
        
        //Find DateStamp
		String dateStamp;
        Pattern datePattern = Pattern.compile("< *datestamp *>[^<]*< */ *datestamp *>", Pattern.CANON_EQ | Pattern.DOTALL);
        Matcher dateMatch = datePattern.matcher(record);
        if(dateMatch.find())
        {
            dateStamp = extractTagValue(record.substring(dateMatch.start(), dateMatch.end()));
        }
        else
        {
        	dateStamp = "unknown";
        }
        
        boolean deleted;
        if(m.find())
        {
            deleted = true;
        }
        else
        {
            deleted = false;
        }        

        // get the string representation of a node
        String xml = "";
        if (!deleted)  // get the metadata (xml) if the node is not deleted
        {
            Pattern metadataPattern = Pattern.compile("< *metadata *>(.*?)< */ *metadata *>", Pattern.CANON_EQ | Pattern.DOTALL);
            Matcher metadataMatcher = metadataPattern.matcher(record);
            if(metadataMatcher.find())
            {
                xml = record.substring(metadataMatcher.start(1), metadataMatcher.end(1));
            }
            else
            {
                conf.log.add("Malformed Record encountered with ID: "+id+" - No metadata! Ignoring and continuing",
                        "Malformed Record encountered with ID: "+id+" - No metadata! Ignoring and continuing");
            }            
        }
        //Get correct namespace
        String namespace = "";
        for(int i = 0; i < conf.formatList.length; i++)
        {

            if(metadataType.equals(conf.formatList[i].getPrefix()))
            {
                namespace = conf.formatList[i].getNamespace();
            }

        }

        // get the string representation of provenance content
        String origin = "";
            Pattern originPattern = Pattern.compile("< *originDescription.*?< */ *originDescription *>", Pattern.CANON_EQ | Pattern.DOTALL);
            Matcher originMatcher = originPattern.matcher(record);
            if(originMatcher.find())
            {
                origin = record.substring(originMatcher.start(), originMatcher.end());
            }
            //else
            //{
            //    conf.log.add("Malformed Record encountered with ID: "+id+" - No metadata! Ignoring and continuing",
            //            "Malformed Record encountered with ID: "+id+" - No metadata! Ignoring and continuing");
            //}            


        OAIRecord result = new OAIRecord(identifier, source, rep.getBaseURL() ,metadataType, xml, dateStamp, namespace, deleted, origin); // create a OAI record instance

        return result;
    }    
}
