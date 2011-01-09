/*
*@author TATENDA M. CHIPEPEREKWA
*@date 2010-07-25
*This class is used to parse the responses that
*are received from the server.
*
*/


   import java.io.IOException;
   import java.util.ArrayList;
   import java.util.Iterator;
   import java.util.List;
   import java.util.Date;
   import java.net.URL;

   import javax.xml.parsers.DocumentBuilder;
   import javax.xml.parsers.DocumentBuilderFactory;
   import javax.xml.parsers.ParserConfigurationException;

   import org.w3c.dom.Document;
   import org.w3c.dom.Element;
   import org.w3c.dom.NodeList;
   import org.xml.sax.SAXException;

   
   import java.io.*;
   import javax.xml.transform.Source;
   import javax.xml.transform.stream.StreamSource;
   import javax.xml.validation.*;
   import org.xml.sax.SAXException;
   import org.xml.sax.*;

   import javax.xml.parsers.*;
   import javax.xml.transform.*;
   import javax.xml.transform.dom.*;
   import javax.xml.transform.stream.*;
   //import org.apache.log4j.Logger;



    public class ResponseParser
   {
   
      private ResumptionToken tokenFound;	//this variable is used to determine	
   				      							//whether there are any resumption tokens
     					     						 	//in the data being parsed,and its value is
   				      							//used to run another request to harvest the
   				      							//repository.	
   
      private Boolean status;					//parsing status of a record
   
       public ResponseParser()
      {
         tokenFound=new ResumptionToken();
         status=true;
      }
   
      	
   /*
   *Stores/updates the resumption token found during the parsing of
	*the InputStream object returned from the harvesting request.
   */	 
       public void setTokenValue(String param)
      {
         tokenFound.setToken(param);
      }
      
       public String getTokenValue()
      {
         return tokenFound.toString();
      }
   
       public void setParsingStatus(Boolean nerror)
      {
         status=nerror;
      }      
       public Boolean getParsingStatus()
      {
         return status;
      }
   /* 
   * private Document parseXmlFile(InputStream response)
   * 
   * This method is used to parse the xml file, record.xml, that contains all the
   * results from the harvesting. It essentially takes the file and create
   * a DOM object that can be then manipulated to extract relevant information.
   *
   */
       public  Document parseXmlFile(InputStream response)
      {
         status = true; 
         Document records=null;//Document object model that stores them parsed xml file.
      
      
      	 //get the factory
         DocumentBuilderFactory recordsFactory = DocumentBuilderFactory.newInstance();
         recordsFactory.setNamespaceAware (true);
      
         try
         {
         
            //Using factory get an instance of document builder
            DocumentBuilder recordsBuilder = recordsFactory.newDocumentBuilder();
         
            //parse using builder to get DOM representation of the XML file
            records = recordsBuilder.parse(response);
         
         
         }
             catch(ParserConfigurationException pce) {
               setParsingStatus(false);
               pce.printStackTrace();
            }
             catch(SAXException se) {
               setParsingStatus(false);
               System.out.println("Error in parsing records.");
            //se.printStackTrace();
            }
             catch(IOException ioe) {
               setParsingStatus(false);
               ioe.printStackTrace();
            }
             catch (Exception e) {
               setParsingStatus(false);
               e.printStackTrace ();
            }   
      
      
         return records;//returns the DOM form of the OAI response
      
      }
   
   /* 
   * private Element parseDocument(Document records)
   * 
   * This method, after the creation of a DOM object, now iterates through each
   * record that was returned, and it adds to a list all the valid records e.g.
   * records that have not been deleted.
   *
   */
       private Element parseDocument(Document records)
      {
          //get the root elememt
         Element docEle = records.getDocumentElement();
      
      //determining the existence if a resumption token
         String tokenValue = getTagValue(docEle,"resumptionToken");
      
         if(!tokenValue.equals("")&&!tokenValue.equals("Not Available"))//resumptionToken found
         {
            setTokenValue(tokenValue);
         }
         else
         {
            setTokenValue("");//no resumptionToken found,set it to ""
         }
      
      
         return docEle;//returns the Document Element from the DOM object
      }
   
    /*
    *Used to return a list of records for all OAI requests that return record or
    *can be interpreted as returning records e.g ListIdentifiers,ListRecords
    */
       public List<Record> getRecords(InputStream response,String request)
      {
         List<Record> records_list=new ArrayList<Record>();//List to store all the records in the xml file returned.
         Document records = parseXmlFile(response);
         Element docEle = parseDocument(records);
      
         if(request.equals("ListIdentifiers"))
            records_list=listElements("header",docEle);
         else if(request.equals("ListRecords"))
            records_list=listElements("record",docEle);
      
         return records_list;
      
      }
    /*
    * Extracts the Document Element for each metadataFormat that a record appears in
    */
       public Document getMetadataElement(InputStream response)
      {
         Document records = parseXmlFile(response);
      
         return records;
      }
    
    /*
    *Extracts a list of records specified by a certain xml tag.
    */
       private List<Record> listElements (String listType,Element docEle)
      {
      
         List<Record> records_list=new ArrayList<Record>();//List to store all the records in the xml file returned.
      
       //get a nodelist elements
         NodeList recordList = docEle.getElementsByTagNameNS("*",listType);
         try{
            if(recordList != null && recordList.getLength() > 0) {
               for(int i = 0 ; i < recordList.getLength();i++) 
               {
                  Record new_record = new Record();
               
               //get the record element
                  Element rec_element = (Element)recordList.item(i);
               
               //get the Record object if it is valid
                  if(listType.equals("record"))
                     new_record = getRecord(rec_element);
                  else if(listType.equals("header"))
                  {  new_record = getIdentifier(rec_element);}
               
                  if(new_record!=null&&new_record.getStatus()!=true)//check if deleted          
                  {
                     records_list.add(new_record);	//add it to list if present
                  }
               }
            }
            else
            {//there are no <record> or <header> elements in the document
               System.out.println("Error Condition: Check response,no record/header elements found.");
               System.exit(0);
            }
         }
             catch(Exception e)
            {e.printStackTrace(); }
         return records_list;
      }
   /**
    * This method takes a record element,if the record has not been deleted
    * the values are then read in and an Record object is created and 
    * returned.
    * 
    * @param  recordElement
    * @return new_record
    */
       public Record getIdentifier(Element recordElement) 
      {
      	//creating a new Record
         Record new_record = new Record();
      //     	 new_record.setRecordHeader(recordElement);
      
      //creating temporary varibles to store the record information
         String repository_identifier="";                //Document repository identifier
         
      //Checking the status of the record
         String status_read=recordElement.getAttribute("status");
      //System.out.println(status_read);
         if(recordElement.hasAttribute("status"))
         {  //record is deleted,set true or otherwise	
            if(status_read.equals("deleted"))
               new_record.setStatus(true);	
         }
         else//deletion status information not available in ListIdentifiers.....every record assumed available?
         {
            try
            {	    repository_identifier=getTagValue(recordElement,"identifier");
               new_record.setRepositoryIdentifier(repository_identifier);
            	
                            //record is not deleted,set false or otherwise
               new_record.setStatus(false);	     
            
            }
                catch(Exception e)
               {
                  e.printStackTrace();
               //System.out.println("record could not be created,missing vital information,incorrect,incompatible..");
               //System.exit(0);
               
               }		
         
         }
      	
         return new_record;
      }
   
    /*
    *Extracts a list of MetadataFormats for a specified record.
    */
       public List<String> getMetadataFormats (InputStream response)
      {      
         List<String> formats=new ArrayList<String>();//List to store all the records in the xml file returned.
         Document records = parseXmlFile(response);
         Element docEle = records.getDocumentElement();
      
       //get a nodelist elements
         NodeList recordList = docEle.getElementsByTagNameNS("*","metadataFormat");
         try{
            if(recordList != null && recordList.getLength() > 0) {
               for(int i = 0 ; i < recordList.getLength();i++) 
               {
               //get the metadataFormat element
                  Element meta_element = (Element)recordList.item(i);
               
                  String new_format = getTagValue(meta_element,"metadataPrefix") ;
                  formats.add(new_format);
               }
            }
            else
            {//there are no <metadataFormat> elements in the document
               System.out.println("Error Condition: Check response,no metadataFormat elements found.");
               System.exit(0);
            }
         }
             catch(Exception e)
            {e.printStackTrace(); }
      
         return formats; 
      }
    /*
    *Extracts a list of Strings from the elements of the type given as an argument
    *to the method.
    */
       public List<String> getList(Element docEle, String listName)
      {
         List<String> itemList=new ArrayList<String>();
      
       //get a nodelist elements
         NodeList elementList = docEle.getElementsByTagNameNS("*",listName);
         try{
            if(elementList != null && elementList.getLength() > 0) {
               for(int i = 0 ; i < elementList.getLength();i++) 
               {
                  String listItem = "";
               //get the record element
                  Element list_element = (Element)elementList.item(i);
                  if(list_element.getFirstChild().getNodeValue() != null)
                  {
                     listItem=list_element.getFirstChild().getNodeValue();
                     itemList.add(listItem);	//add it to list if present  		    
                  }
               
               }
            }
            else
            {//there are no <"listName"> elements in the document
               return null;
            }
         }
             catch(Exception e)
            {e.printStackTrace(); }
      
         return itemList;
      }
   /**
    * This method takes a record element,if the record has not been deleted
    * the values are then read in and an Record object is created and 
    * returned.
    * 
    * @param recElement
    * @return new_record
    */
       public Record getRecord(Element recordElement) 
      {
      	//creating a new Record
         Record new_record = new Record();
      	
          //creating temporary varibles to store the record information
         String repository_identifier="";                //Document repository identifier
         String identifier="";                         	//Document identifier
         List<String> titles=new ArrayList<String>();     //Document Title(s)
         List<String> creators=new ArrayList<String>();	//Document Creator(s)
         String record_date="1900-01-01";
         String description="";         	              //of the record
         List<String> subjects=new ArrayList<String>();	//Subject(s) discussed
         String type="";                                  //the document type
         String format="";                                //the file format
         String xmlFormat="";					//the xml format of the meta-record
              	
      //for the dc format documents,the following section of
      		//code recurses through the document tree and collects
      //the relavant information for the record.
      
         //Checking the status of the record
         String status_read=getStatus(recordElement,"header");
      
         if(status_read.equals("deleted"))
         {
            //record is deleted,set true or otherwise
            new_record.setStatus(true);	
         }
         else
         {
            try
            {		 repository_identifier=getTagValue(recordElement,"identifier");
               identifier = getTagValue(recordElement,"dc:identifier");
               xmlFormat = getTagValue(recordElement,"metadata");
               description = getTagValue(recordElement,"dc:description");
               record_date= getTagValue(recordElement,"dc:date");
               type= getTagValue(recordElement,"dc:type");
               format= getTagValue(recordElement,"dc:format");
            	
               String apos="\'";
               String quote="\"";						
            
            
               NodeList titleList = recordElement.getElementsByTagNameNS("*","title");
               if(titleList != null && titleList.getLength() > 0) 
               {
                  for(int i = 0 ; i < titleList.getLength();i++) 
                  {
                     Element subject = (Element)titleList.item(i);
                     titles.add(subject.getFirstChild().getNodeValue().replace(apos.charAt(0),quote.charAt(0)));
                  }
               }
               else
               {titles=null;}	
            
            
               NodeList subjectList = recordElement.getElementsByTagNameNS("*","subject");
               if(subjectList != null && subjectList.getLength() > 0) 
               {
                  for(int i = 0 ; i < subjectList.getLength();i++) 
                  {
                     Element subject = (Element)subjectList.item(i);
                     subjects.add(subject.getFirstChild().getNodeValue());
                  }
               }
               else
               {subjects=null;}	
            	
            
               NodeList creatorList = recordElement.getElementsByTagNameNS("*","creator");
             
               if(creatorList != null && creatorList.getLength() > 0) 
               {
                  for(int i = 0 ; i < creatorList.getLength();i++) 
                  {
                     Element creator = (Element)creatorList.item(i);
                     creators.add(creator.getFirstChild().getNodeValue());
                  }
               }
               else
               {creators=null;}
            
            
            //used the .replace method to curb mySQL errors with the " ' " character used in mySQL query syntax				
               description=description.replace(apos.charAt(0),quote.charAt(0));
               new_record.setRepositoryIdentifier(repository_identifier);
               new_record.setIdentifier(identifier);
               new_record.setTitle(titles);
               new_record.setDescription(description);
               new_record.setDate(record_date);
               new_record.setType(type);
               new_record.setFormat(format);
               new_record.setSubjects(subjects);
               new_record.setCreators(creators);
               new_record.setXmlFormat(xmlFormat);
            			
                            //record is not deleted,set false or otherwise
               new_record.setStatus(false);	     
            
            }
                catch(Exception e)
               {
                  e.printStackTrace();
               //System.out.println("record could not be created,missing vital information,incorrect,incompatible..");
               //System.exit(0);
               
               }		
         
         }
      	
         return new_record;
      }
   	
   
   	
   //Used to get the tag value for elements in the document tree
       public String getTagValue(Element recordElement, String tagName)
      {
         String tagValue="";//temporary variable
         try{
            NodeList tagList = recordElement.getElementsByTagNameNS("*",tagName);
         	 
            if(tagList != null && tagList.getLength() > 0) 
            {
               Element tag = (Element)tagList.item(0);
               if(tag.getFirstChild().getNodeValue() != null)
               {
                  tagValue=tag.getFirstChild().getNodeValue();
                  return tagValue;
               }
               else{
                  return "";}
            }
            else
            {
               return "";}	
         }
             catch(Exception e)
            {	//Some records might have a tag but the tag might not contain any value
            //Uncomment the next line to view tag without value
               System.out.println("Cannot extract value for: " + tagName);
               return "Not Available";	
            }
      
      }			
   
   
    /* private String getStatus(Element recordElement, String tagName)
    *  
    * This method takes in the recordElement element,searches for the header tag, and
    * returns the status of the record.
    *
    * @param ele
    * @param tagName
    * @return textVal
    */
       private String getStatus(Element recordElement, String tagName) {
         String textVal = null;
         
         NodeList headerList = recordElement.getElementsByTagNameNS("*",tagName);
         if(headerList != null && headerList.getLength() > 0) {
            Element header = (Element)headerList.item(0);
            textVal = header.getAttribute("status");
         }
              	
         return textVal;
      }
   
      /* 
   * public String viewXmlFile(InputStream response)
   * 
   * This method is used to parse the xml file, record.xml, that contains all the
   * results from the harvesting. It essentially takes the file and create
   * a DOM object that can be then manipulated to extract relevant information.
   *
   */
       public String viewXmlFile(InputStream response,String xslFilename)
      {
         String test ="";
         try {
            // Create transformer factory
            TransformerFactory factory = TransformerFactory.newInstance();
         
            // Use the factory to create a template containing the xsl file
            Templates template = factory.newTemplates(new StreamSource(
                new FileInputStream(xslFilename)));
         
            // Use the template to create a transformer
            Transformer xformer = template.newTransformer();
         
            // Prepare the input file
            Source source = new StreamSource(response);
         
            // Create a new document to hold the results
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.newDocument();
            Result result = new DOMResult(doc);
         
            // Apply the xsl file to the source file and create the DOM tree
            xformer.transform(source, result);
         
            return getStringFromDocument(doc);
         } 
             catch (ParserConfigurationException e) {
            // An error occurred while creating an empty DOM document
               test=test+"parseConfigError";e.printStackTrace();
            } 
             catch (FileNotFoundException e) {
               test=test+"IOError\n"+e.getMessage();e.printStackTrace();
            } 
             catch (TransformerConfigurationException e) {
            // An error occurred in the XSL file
               test=test+"Transformer Configuration Error\n"+e.getMessage();
            } 
             catch (TransformerException e) {
            // An error occurred while applying the XSL file
               test=test+"Transformer Application Error\n"+e.getMessage();e.printStackTrace();
            }
         return test;
      
      }
      //method to convert Document to String
       public String getStringFromDocument(Document doc)
      {
         try
         {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
         }
             catch(TransformerException ex)
            {
               ex.printStackTrace();
               return null;
            }
      } 
   /**
    * Iterate through the list and print the 
    * content to console.
    * used for testing purposes..
   
       public void printData(){
      
         System.out.println("Number of Records Harvested '" +records_list.size() + "'.");
      
         Iterator it =records_list.iterator();
         while(it.hasNext()) 
         {
            System.out.println(it.next().toString());
         
         }
      }
    */
   
   
   }
