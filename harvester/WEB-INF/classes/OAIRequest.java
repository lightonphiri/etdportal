
/* import declarations */
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
 * @version 1.9.6.25
 */
public class OAIRequest
{
     /** the harvester configuration object */
    HarvestConfiguration configuration; // configuration object

    /**
     * Starts the harvest by calling its internal <code>doHarvest</code> method.
     * Also sets the harvester configuration local variables.
     * @param config the harvester configuration for the repository being harvested.
     */
    public OAIRequest(HarvestConfiguration config)
    { // constructor that takes a HarvestConfiguration object
        configuration = config; // set the configuration object to the one passed in to constructor
        System.out.println("Starting with a request to " + configuration.getBaseURL());
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
            if (configuration.getDateFrom() != null)
            { // if there is a from date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                request += "&from=" + sdf.format(Timestamp.valueOf(configuration.getDateFrom()));
            }
            if(configuration.hasSetSpec())
            {
                request += "&set=" + configuration.getSetSpec();
            }
            request += "&metadataPrefix=" + configuration.getMetadataFormat();	// set the metadata prefix
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
    private void doHarvest()
    {
        //update harvest status
        try
        {
            String rtoken = ""; // at first there is no resumption token
            do {
                String request = generateRequest(rtoken);
                System.out.println(request);
                BufferedInputStream response = sendRequest(request);

                updateHarvestStatus();
                OAIResponseHandler handler = new OAIResponseHandler(response, configuration); // create a response handler to parse and store the response
                System.out.println("Storing the responses");
                handler.store(); // store the entries in the response in the database
                //update harvest status


                System.out.println("Getting the resumption token");
                rtoken = handler.getResumptionToken(); // get the resumption token from the response
            } while (!rtoken.equals("")); // if there is no resumption token, end the harvest
            //Harvest completed, so we know the list size
            configuration.completeListSize = configuration.cursor;
            updateHarvestStatus();
        }
        catch(Exception e)
        {
            configuration.setHarvestStatus(e.getMessage());
        }

    }

    /**
     * Called each time somthing signifigant happens in the class, this updates
     * the harvest status in the config object.
     */
    private void updateHarvestStatus()
    {
        if(configuration.completeListSize == -1)
        {
            configuration.setHarvestStatus("Harvesting ("+ configuration.cursor+"/???)");
        }
        else if (configuration.cursor < configuration.completeListSize)
        {
            configuration.setHarvestStatus("Harvesting ("+ configuration.cursor+"/"+configuration.completeListSize+")");
        }
        else
        {
            configuration.setHarvestStatus("Successfully harvested "+configuration.cursor+" records.");
        }
    }

    /**
     * Sends the http request to the server, and then returns the response
     * in a <code>BufferedInputStream</code> object.
     * @param request the request to be sent to the server
     * @return the response from the server
     */
    private BufferedInputStream sendRequest(String request)
    {
        try
        {
            URL url = new URL (configuration.getBaseURL()); // create URL with the base url of the server to harvest from
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
            System.out.println("Error sending request: "+e);
            return null;
        }

    }

    /**
     * Currently not used, this method is supposed to take an input stream
     * and validate it agains the XML schema. This validates the ENTIRE server response
     * which is impractical, since no records can then be salavaged if a single one
     * is broken. Current scheme validates on a record to record basis.
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
          System.out.println("There was an error validating an xml document from: " + configuration.getBaseURL());
          result = false;
        }
        catch(Exception e)
        {
          System.out.println("There was an error validating an xml document from: " + configuration.getBaseURL());
          result = false;
        }
        return result;
    }


}
