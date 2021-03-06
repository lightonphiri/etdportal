import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.validation.SchemaFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;


/** OAI record - Class to represent a record in OAI-PMH format
 * @author Alexander van Olst
 * @author Lawrence Webley (Javadoc only)
 * @author Hussein Suleman
 * @version 1.9.5.12
 */
public class OAIRecord {
    /** the unique id of the record */
	private String ID;
    /** the name of the source this record came from */
    private String source;
    /** the url this record was harvested from */
    private String baseURL;
    /** the metadata format this record is in */
    private String type;
    /** the raw xml metadata */
    private String xml;
    /** the dataStamp that came with the harvested record */
    private String dateStamp;
    /** the xml namespace of this records type */
    private String nameSpace;
    /** whether the record is flagged as deleted or not */
    private boolean deleted;
    /** original originDescription entry */
    private String origin;

    /**
     * Constructor to create a record object out of the constituent variables
     * that make up a record.
     * @param id the identification number for the record
     * @param Source the url of the repository it was taken from
     * @param metadata the format of the metadata (eg <code>oai_dc</code>)
     * @param contents the actual metadata content
     * @param isDeleted whether or not the record is deleted or not.
     */
	public OAIRecord (String id, String Source, String baseURL, String metadata, String contents, String datestamp, String nameSpace, boolean isDeleted, String origin ){
		ID = id;
		source = Source;
		this.baseURL = baseURL;
		type = metadata;
		xml = contents;
		deleted = isDeleted;
		dateStamp = datestamp;
		this.nameSpace = nameSpace;
		this.origin = origin;
	}

	// getters and setters
    /**
     * Gets the record ID.
     * @return the identification number
     */
	public String getID() {
		return ID;
	}
    /**
     * Sets the record ID
     * @param id the identification number
     */
	public void setID(String id) {
		ID = id;
	}
    /**
     * Gets the source url of the record
     * @return the url source of the record
     */
	public String getSource() {
		return source;
	}
    /**
     * Sets the url source of the record
     * @param source the url soruce of the record
     */
	public void setSource(String source) {
		this.source = source;
	}

    /**
     * Gets the type of metadata stored in this records
     * @return the type of metadata stored in this records
     */
	public String getType() {
		return type;
	}
	
	/**
	 * Constructs and returns the about field for the record using provenance format
	 */
	public String getAboutField()
	{
		StringBuffer data = new StringBuffer("");
		data.append("<provenance\n");
		data.append("xmlns=\"http://www.openarchives.org/OAI/2.0/provenance\"\n");
		data.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
		data.append("xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/provenance \n");
		data.append("http://www.openarchives.org/OAI/2.0/provenance.xsd\">\n");
		
		SimpleDateFormat UTCDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        UTCDateFormatter.setTimeZone(tz);
		
		data.append(" <originDescription harvestDate=\""+UTCDateFormatter.format(new Date())+"\" altered=\"false\">\n");
		data.append("  <baseURL>"+baseURL+"</baseURL>\n");
		data.append("  <identifier>"+ID+"</identifier>\n");
		data.append("  <datestamp>"+dateStamp+"</datestamp>\n");
		data.append("  <metadataNamespace>"+nameSpace+"</metadataNamespace>\n");
		data.append (origin);
		data.append(" </originDescription>\n");
		data.append("</provenance>\n");
		
		return data.toString();
		
	}
    /**
     * Sets the type of metadata stored in this records
     * @param type the type of metadata stored in this records
     */
	public void setType(String type) {
		this.type = type;
	}

    /**
     * Gets the xml metadata stored in this record, formatting it in such
     * a way that it wont break sql queries
     * @return the xml metadata stored in this record
     */
	public String getXml() {
	
//           String s = xml.replace ("\\", "\\\\");
//           s = s.replace ("'", "\\'");
//	   return s;
           return xml;
	
	/*	StringBuffer out;
        String temp;
		out = new StringBuffer("");
		temp =  xml;
		while (temp.indexOf("'") >= 0){ // while there is at least one ' remaining

			out.append(temp.substring(0,temp.indexOf("'")) + "\\'"); // insert a \ to escape it in the sql query
			temp = temp.substring(temp.indexOf("'")+1);
		}
		out.append(temp);
		return out.toString();
		*/
	}

    /**
     * Gets the raw xml metadata unprocessed. Warning: this may contain escape sequences that will break sql statments!
     * @return the raw xml metadata as it was received from the repository server.
     */
	public String getXmlRaw(){
		return xml;
	}
    /**
     * Sets the xml metadata in this record
     * @param xml the xml metadata to set.
     */
	public void setXml(String xml) {
		this.xml = xml;
	}

    /**
     * Returns whether or not this record is marked as deleted or not.
     * @return 1 if this record is flaged as deleted. False otherwise.
     */
	public int isDeleted() {
	   if (deleted)
	      return 1;
	   return 0;   
	}

    /**
     * Sets whether or not this record should be marked as deleted
     * @param deleted whether or not this record is marked as deleted
     */
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

    /**
     * Returns a human readable representation of this record. Used mainly for
     * debuging purposes.
     * @return a human readable representation of the record.
     */
    @Override
    public String toString(){ // used for testing,
    	return "***RECORD***\n" + ID + "\n" + type + "\n" + source + "\n" + xml + "\n***END***";
    	
    }

    /**
     * Runs xml validation on this records metadata in order to check if it is valid.
     * @return true if the metadata validates successfully.
     */
   public boolean selfValidate(SchemaFactory factory, Config conf)
   {
      // return valid for deleted records
      if (deleted)
         return true;

      //First remove any ' and " that might break the sql storage statement
      xml.replaceAll("[\"\']", "\\\"");
        
      // use simple XML parser for validation
      try {
         //read in the xml config file
         DocumentBuilderFactory docBuilderFac = DocumentBuilderFactory.newInstance();
         DocumentBuilder docBuilder = docBuilderFac.newDocumentBuilder();
         Document doc = docBuilder.parse ( new InputSource ( new StringReader (xml)));
         doc.getDocumentElement().normalize();
         Element root = doc.getDocumentElement();
      } catch ( Exception e ) {                  
         return false;
      }

      return true;		
   }

}
