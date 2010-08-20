/* import declarations */
import java.io.InputStream;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class to parse and store the response to an OAI Request
 * @author Alexander van Olst
 * @author Lawrence Webley
 * @version 1.9.6.24
 */
public class OAIResponseHandler
{
    /** the xml document (DOM3)*/
    Document document;
    /** the harvester configuration object */
    HarvestConfiguration configuration;

    /**
     * Constructor that takes a input stream from a response from a repository server
     * and then creates a document instance out of it.
     * @param is the input stream response from a repository server
     * @param config the harvester configuration object containing the settigns
     * pertinent to this harvest.
     */
    public OAIResponseHandler(InputStream is, HarvestConfiguration config)
    {
        DocumentBuilder dbuilder = null;
        configuration = config;
        try { // try create an instance of document builder
                dbuilder = (DocumentBuilderFactory.newInstance()).newDocumentBuilder(); // make a document builder

                document = dbuilder.parse(is); // the document builder parses the input stream

                //Node n = (Node) document.getDocumentElement();
                //System.out.println(nodeToString(n));

        } catch (Exception e)
        {
                System.out.println("Unexpected error parsing XML file");
                document = null;
        }
    }

    /**
     * Uses the document instance created in the constructor to seperate out each
     * record from the xml received from the repository server, and store it in
     * the database. uses the <code>getRecords</code> method to retrieve the
     * seperated out records.
     */
    public void store()
    { // store the response in the database

        ArrayList<OAIRecord> results = getRecords(); // get a list of the records in the document
        DatabaseConnection dbc = new DatabaseConnection( configuration );
        //connect to the database and exit with an error code if it could not connect to the database.
        if(!dbc.Connect())
        {
            configuration.setRunning(false);
            configuration.setHarvestStatus("Error: Failed to store records in local SQL database!");
            System.exit(1);
        }
        //If available, get the complete size of the repo.
        setCompleteListSize();

        //factory for the W3C XML Schema language
        SchemaFactory factory =  SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

        if (results != null)
        {
            for (int i = 0; i < results.size(); i++)
            {
                OAIRecord rec = results.get(i);
                rec.setSource(configuration.getBaseURL()); // update the record
                rec.setType(configuration.getMetadataFormat()); // update the record
                if(rec.selfValidate(factory))
                {
                    dbc.insert(rec);
                    configuration.cursor++;
                }
                else
                {
                    System.err.println("Record with ID: ("+rec.getID()+") failed validation and was not added...");
                }
            }
        }
    }

    /**
     * Decomposes the xml received from the server into its constituent records
     * and then returns them as an <code>ArrayList</code>.
     * @return an array list of {@link Harvester.OAIRecord}s.
     */
    public ArrayList<OAIRecord> getRecords()
    {
        ArrayList<OAIRecord> result = new ArrayList<OAIRecord>();

        Element element = document.getDocumentElement(); // get the document element

        NodeList nl = element.getElementsByTagName("record"); // get a nodelist with all record tags

        if(nl != null && nl.getLength() > 0)
        { // if there are records in the nodelist
            for(int i = 0 ; i < nl.getLength();i++)  // interate through the nodelist
            {
                //get the record element
                Element rec = (Element)nl.item(i); // get the item

                String identifier = getTextValue(rec,"identifier"); // get the identifier
                NodeList metadataNode = rec.getElementsByTagName("metadata"); // get the metadata
                Element metaDataElement = (Element) metadataNode.item(0); // get the element
                result.add(elementToRecord(metaDataElement, identifier)); // add it to the result ArrayList

            }
        }
        return result;
    }

    /**
     * Attempts to set the complete list size in the harvest configuration file,
     * so that one can track the progress of the harvest by periodically checking
     * the harvest file. If the complete list size is unknown, then it is set
     * to -1 in the config file, which indicates an unknown value.
     */
    public void setCompleteListSize()
    {
        Element element = document.getDocumentElement(); // get the document element
        NodeList nl = element.getElementsByTagName("resumptionToken");
        Element resToken = (Element)nl.item(0);
        try
        {
            String listSize = resToken.getAttribute("completeListSize");
            if(!listSize.equals(""))
            {
                configuration.completeListSize = Long.parseLong(listSize);
                System.out.println("Total list size is "+ listSize);
            }
            else
            {// I.E. we dont know.
                configuration.completeListSize = -1;
            }
        }catch(NullPointerException e)
        {//if there is no resumptionToken element, we dont know the size either.
            configuration.completeListSize = -1;
        }
    }

    /**
     * Gets the resumption token out of the xml response received from the repository
     * server.
     * @return the resumption token.
     */
    public String getResumptionToken()
    { // get the resumption token from the repsonse
        return getTextValue(document.getDocumentElement(),"resumptionToken"); // get the resumption token
    }

   /**
    * Takes an element and that tag that relates to it and attempts
    * to turn it into a string.
    * @param ele the element that we want to convert to text.
    * @param tagName the tag name of the element in question
    * @return the string value of an element
    */
    private String getTextValue(Element ele, String tagName)
    {
        String textVal = "";
        if (ele == null)// if the element is null, return an empty string
        {
            return "deleted";
        }

        NodeList nl = ele.getElementsByTagName(tagName); // get a nodelist with all the elements with the tagname

        if(nl != null && nl.getLength() > 0)
        {
            Element el = (Element)nl.item(0);
            if (el.getFirstChild() != null)
            textVal = el.getFirstChild().getNodeValue(); // get the text value
        }
        return textVal;
    }

    /**
     * Takes an element containing a record, along with the records
     * id string, and returns the record stored in an {@link Harvester.OAIRecord}
     * object.
     * @param el the document element that contains the record
     * @param id the identification number of the record
     * @return the record stored in a <code>OAIRecord</code> object
     */
    public  OAIRecord elementToRecord(Element el, String id)
    {
        String identifier = id; // get the identifier
        String source = ""; // this will be set by the Get class as it isnt contained in the element passed to this function
        String metadataType = "";// this will also be of the type harvested by Get, so it will be set in get

        String status = getTextValue(el,"status");
        boolean deleted;
        //System.out.println(status);
        if ((status != null) && (status.equals("deleted"))) // check whether the status is set to deleted
                deleted = true;
        else deleted = false;

        // get the string representation of a node
        String xml = "";
        if (!deleted)  // get the metadata (xml) if the node is not deleted
        {
            xml = nodeToString((Node) el);
        }
        OAIRecord result = new OAIRecord(identifier,source,metadataType,xml, deleted); // create a OAI record instance
        return result;
    }

    /**
     * Converts a document node into a string.
     * @param node the node to be converted
     * @return a string representation of the original node.
     */
    public static String nodeToString(Node node)
    {
        Document doc = node.getOwnerDocument();
        DOMImplementation impl = doc.getImplementation();
        DOMImplementationLS factory = (DOMImplementationLS) impl.getFeature("LS", "3.0");
        LSSerializer serializer = factory.createLSSerializer();
        String result = serializer.writeToString(node);
        if (result.indexOf(">") >= 0 ) result = result.substring(result.indexOf(">")+1, result.length()); // strips the output of the first line, which should not be appearing
        return result;
    }
}
