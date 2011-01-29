import java.io.BufferedInputStream;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;


/**
 * Class to send a request and get the response from a OAI source
 * @author Alexander van Olst
 * @author Lawrence Webley
 # @author Hussein Suleman
 * @version 1.9.6.25
 */
public class OAIRequest
{
   /** Repository configuration */
   Repository rep; 
   /** Database configuration */
   Config conf;

   /**
    * Starts the harvest by calling its internal <code>doHarvest</code> method.
    * Also sets the harvester configuration local variables.
    * @param config the harvester configuration for the repository being harvested.
    */
   public OAIRequest ( Config aConf, Repository r )
   { 
      conf = aConf; 
      rep = r;
      doHarvest();
   }

    /**
     * Returns the http request that is to be sent to the server.
     * <p>
     * If there is a resumption token, it will use that to create the request.
     * Otherwise, it will generate a fresh request if the resumption token
     * parameter is empty.
     * @param rtoken the resumption token
     * @return the full request that is to be sent back to the Repository.
     */
    private String generateRequest(String rtoken)
    { // generate a request, adding the resumption token if necessary
        String request = "verb=ListRecords";

        if (rtoken.equals(""))
        { // if there is no resumption token
            if (! "".equals (rep.getDateFrom()))
            { // if there is a from date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                request += "&from=" + sdf.format(Timestamp.valueOf(rep.getDateFrom()));
            }
            if(! "".equals (rep.getSetSpec()))
            {
                request += "&set=" + rep.getSetSpec();
            }
            request += "&metadataPrefix=" + rep.getMetadataFormat();	// set the metadata prefix
        }
        else
        {
            request += "&resumptionToken=" + rtoken;
        }

        return request;
    }

    /**
     * Actually performs the harvest. Sends off the request to the repository server
     * and then receives the reponse, storing it in a <code>OAIResponseHandeler</code>,
     * where it is then stored in the database.
     */
    private void doHarvest ()
    {
        //update harvest status
        try
        {
            String rtoken = ""; // at first there is no resumption token
            do {
                String request = generateRequest(rtoken);
                conf.log.add("OAI request: " + rep.getBaseURL () + "?" + request);
                BufferedInputStream response = sendRequest(request);

                updateHarvestStatus();
                OAIResponseHandler handler = new OAIResponseHandler (conf, response, rep); // create a response handler to parse and store the response
                conf.log.add("Storing the responses...");
                handler.store(); // store the entries in the response in the database
                //update harvest status

                conf.log.add("Getting resumption token...");
                rtoken = handler.getResumptionToken(); // get the resumption token from the response
                if (! rtoken.equals (""))
                   conf.log.add ("resumptionToken: " + rtoken);
            } while (!rtoken.equals("")); // if there is no resumption token, end the harvest
            //Harvest completed, so we know the list size
            rep.completeListSize = rep.cursor;
            updateHarvestStatus();
        }
        catch(Exception e)
        {
            rep.updateHarvestStatus (e.getMessage());
        }
    }

    /**
     * Called each time somthing signifigant happens in the class, this updates
     * the harvest status in the config object.
     */
    private void updateHarvestStatus()
    {
        if (rep.completeListSize == -1)
        {
            rep.updateHarvestStatus ("Harvesting ("+ rep.cursor+"/???)");
        }
        else if (rep.cursor < rep.completeListSize)
        {
            rep.updateHarvestStatus ("Harvesting ("+ rep.cursor+"/"+rep.completeListSize+")");
        }
        else
        {
            rep.updateHarvestStatus ("Harvested "+rep.cursor+" records");
        }
    }

    /**
     * Sends the http request to the server, and then returns the response
     * in a <code>BufferedInputStream</code> object.
     * @param request the request to be sent to the server
     * @return the response from the server
     */
    private BufferedInputStream sendRequest(String request) throws Exception
    {
        try
        {
            URL url = new URL (rep.getBaseURL()); // create URL with the base url of the server to harvest from
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true); // do output is true
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());

            wr.write(request);
            wr.flush();

            BufferedInputStream response = new BufferedInputStream(connection.getInputStream()); // create a buffered stream to read from
            /* this is so we can use the same stream twice, once to validate, and once to retrieve the data */

            response.mark(2147483647); // sets the max amount of readable bytes, with the maximum amount that can be read from a stream

            return response;

        }
        catch (Exception e)
        {
            conf.log.add("Error sending request to remote repository: "+e,
                    "Error sending request to remote repository: "+e);
            throw new Exception("Failed to communicate with remote server");
        }

    }

    /**
     * Currently not used, this method is supposed to take an input stream
     * and validate it agains the XML schema. This validates the ENTIRE server response
     * which is impractical, since no records can then be salavaged if a single one
     * is broken. Current scheme validates on a record to record basis
     * @param is the response from the repository server
     * @return whether or not the response is valid or not.
     */
    private boolean validateResponse(InputStream is)
    {
        boolean result = true;
        try
        {
            //factory for the W3C XML Schema language
            SchemaFactory factory =
            SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

            //Compiling the schema.

            URL url = new URL("http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd");
            Schema schema = factory.newSchema(url);

            //validator from the schema.
            Validator validator = schema.newValidator();

            //Parsing the document
            StreamSource source = new StreamSource(is);

            //Checking the document
            validator.validate(source);
        }
        catch (SAXException ex)
        {
          conf.log.add("There was an error validating an xml document from: " + rep.getBaseURL(),
                  "There was an error validating an xml document from: " + rep.getBaseURL());
          result = false;
        }
        catch(Exception e)
        {
          conf.log.add("There was an error validating an xml document from: " + rep.getBaseURL(),
                  "There was an error validating an xml document from: " + rep.getBaseURL());
          result = false;
        }
        return result;
    }


}
