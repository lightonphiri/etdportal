/**
*DatabaseUpdater creates a link with the Portal Database
*and updates any new records that have been captured by
*the harvester,HarvestingMain class.
*<p>
*The update is done to the Portal Database on a per record
*basis.
*@author Tatenda M. Chipeperekwa
*@date 2010-07-25
*/


   import java.sql.*;
   import java.io.IOException;
   import java.util.ArrayList;
   import java.util.Iterator;
   import java.util.List;
   import java.util.Date;
   import java.util.regex.*;
   import java.net.URL;
   import javax.xml.parsers.ParserConfigurationException;
   import org.w3c.dom.Document;
   import org.w3c.dom.Element;
   import org.w3c.dom.NodeList;
   import org.xml.sax.SAXException;
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
   import java.util.Properties;
 
    public class DatabaseUpdater
   {
   
   /**
   *Clears the contents of the database tables in the case of a batch update.
   *@param con connection object passed to the method from the ConfigurationManager.
   */
       public void clearDatabase(Connection con)
      {
       
         try{
            Statement stm = con.createStatement();
                   //Remove all entries in the Database before adding new data
            stm.addBatch("delete from RecordTitle");
            stm.addBatch("delete from RecordXML");
            stm.addBatch("delete from RecordDate");
            stm.executeBatch();
         
         }
             catch(SQLException sqle)
            {
               sqle.printStackTrace();
            }		 
         
      
      }
   
   /** 
   * Inserts a single record into the Portal Database.
   * @param record Record class instance with the details of the metadata
   *		   record to be inserted into the Portal Database.
   * @param con connection object passed to the method from the ConfigurationManager.
   */
   	
       public void insertRecord (Record record,Connection con,String lastHarvestDate,List<String> stopWords)
      {
      
         try {
            record.setPortalXML(getPortalXML(record));
            record.setRecordTitle(getField(record,"title"));     	
            record.setDate(getField(record,"date"));
  
            Statement stm = con.createStatement();
            Statement stm2 = con.createStatement();
         
            if(record != null)
            {		
               stm.addBatch("REPLACE INTO RecordTitle VALUES('"+record.getRecordTitle()+"','"+removeStopWords(record.getRecordTitle(),stopWords)+"','"+record.getRepositoryIdentifier()+"')" );
               stm.addBatch("REPLACE INTO RecordDate VALUES('"+record.getDate()+"','"+record.getRepositoryIdentifier()+"')" );
               stm.addBatch("REPLACE INTO RecordAffiliation VALUES('"+record.getAffiliation()+"','"+record.getRepositoryIdentifier()+"')" );
               stm.addBatch("REPLACE INTO RecordXML VALUES('"+record.getPortalXML()+"','"+record.getRepositoryIdentifier()+"')" ); 
               stm.addBatch("REPLACE INTO RecordLastHarvestDate VALUES('"+lastHarvestDate+"','"+record.getRepositoryIdentifier()+"')" ); 
               stm.executeBatch();
            }
            else{System.out.println("--Updating the database--\nError:Record is null,please check harvest source before updating the database");}
         }   	
          
             catch (SQLException sqle) {//catches any exceptions throw by queries on the database
            
               System.out.println(record.toString());
               sqle.printStackTrace();
            }
      
      
      }
      
      /**
      *Extracts a suitable title or date for the record,from all the different 
      *metadata formats that it is stored in in the main repository.
      *<p>
      *The title or date is extracted from the oai_marc format of the record,but
      *if this format is not available,then these are extracted from oai_etdms,
      *oai_rfc1807 or oai_dc respectively.
      * @param record Record class instance with the details of the metadata
      *		   record to be inserted into the Portal Database.
      * @param field <code>String</code> specifying either that a title or date
      *		     is to be extracted from the record.	
      */
       private String getField(Record record,String field)
      {
         String value = "Not Available";
         try{
            if(record.getMetadataFormats().contains("oai_marc"))
            {
               int index = record.getMetadataFormats().indexOf("oai_marc");
               Element docEle = record.getMetadataRecords().get(index).getDocumentElement();
            
            
               if(field.equals("title"))
               {         
                  NodeList varFieldList = docEle.getElementsByTagName("varfield");
                  if(varFieldList != null && varFieldList.getLength() > 0) {
                     for(int i = 0 ; i < varFieldList.getLength();i++) 
                     {
                     //get varfield element
                        Element var_element = (Element)varFieldList.item(i);
                        if(var_element.getAttribute("id").equals("245"))
                        {
                           NodeList subFieldList = var_element.getElementsByTagName("subfield");
                           if(subFieldList != null && subFieldList.getLength() > 0) {
                              for(int j = 0 ; j < subFieldList.getLength();j++) 
                              {
                                 System.out.println(subFieldList.getLength());  	
                              //get the subfield element
                                 Element subfield_element = (Element)subFieldList.item(j);
                              
                                 if(subfield_element.getAttribute("label").equals("a"))
                                 {
                                    value = getTagValue(var_element,"subfield");
                                    break;
                                 }
                              
                              }
                           
                           }
                        }
                     
                     }
                  
                  }
               
               }
               else if (field.equals("date"))
               {
                  value=getTagValue(docEle,"fixfield");
                  value = value.substring(6,10)+"-01-01";
               }
            }
            else if(record.getMetadataFormats().contains("oai_etdms"))
            { 
               int index = record.getMetadataFormats().indexOf("oai_etdms");
            
               Element docEle = record.getMetadataRecords().get(index).getDocumentElement();
            
               if(field.equals("title"))
               {
                  value=getTagValue(docEle,"title");
               }
               else if (field.equals("date"))
               {
                  value=getTagValue(docEle,"date");
               }
            
            
            }
            else if(record.getMetadataFormats().contains("oai_rfc1807"))
            {
               int index = record.getMetadataFormats().indexOf("oai_rfc1807");
               Element docEle = record.getMetadataRecords().get(index).getDocumentElement();
            
               if(field.equals("title"))
               {
                  value=getTagValue(docEle,"title");
               }
               else if (field.equals("date"))
               {
                  value=getTagValue(docEle,"entry");
               }
            
            }
            else if(record.getMetadataFormats().contains("oai_dc"))
            {
               int index = record.getMetadataFormats().indexOf("oai_dc");
               if (record.getMetadataRecords().get(index) != null) // [check for error document, hussein, 1feb2010]
               {
                  Element docEle = record.getMetadataRecords().get(index).getDocumentElement();
               
                  if(field.equals("title"))
                     value=getTagValue(docEle,"title");
                  else if (field.equals("date"))
                     value=getTagValue(docEle,"date");
               }
            
            }
         
         // field validation and fixing [hussein, 30jan2010]
            if (field.equals ("date"))
            {
               if (value.length()>10)
                  value=value.substring (0, 10);
               Pattern p = Pattern.compile ("[0-9]{4}\\-[0-9]{2}\\-[0-9]{2}");
               Matcher m = p.matcher (value);
               if (! m.find ())
                  value = "1980-01-01";
            
            }
            else
            {
               if (value.length()>100)
                  value = value.substring (0, 100);
            }
         
            value=value.replace("\'".charAt(0),"\"".charAt(0));
         //System.out.println (value);
         
         }
             catch(Exception e){e.printStackTrace();
            
            }
      
         if(field.equals ("date")&&value.equals("Not Available"))//additional error check for incomplete records
            value = "1980-01-01";	
      
         return value;  
      }
      
   /**
   *Extracts the tag value for elements in a <code>Document</code> tree.
   *@param recordElement <code>Element</code> representing the the tag in 
   *			the XML file that is to be extracted.
   *@param tagName the textual name of the tag to be extracted.
   *@return tagValue <code>String</code> representation of tag.
   */	
       private String getTagValue(Element recordElement, String tagName)
      {
         String tagValue="";//temporary variable
         try{
            NodeList tagList = recordElement.getElementsByTagNameNS ("*", tagName);
         	 
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
            //System.out.println("Cannot extract value for: " + tagName);
               return "Not Available";	
            }
      
      }
     
   
   /**
   *Creates a combined XML record of all the metadata formats for the current record.
   * @param record Record class instance with the details of the metadata
   *		   record to be inserted into the Portal Database.
   *
   */
       private String getPortalXML(Record record)
      {
      
         Properties propertyFinder = new Properties();//used to extract the required data to form records to be stored in the database
         String portalXML =  "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
             +"<records>";
         for(int i = 0 ;i<record.getMetadataRecords().size();i++)
         {
            portalXML+=getStringFromDocument(record.getMetadataRecords().get(i));
         }
      
         portalXML +="</records>";
      //removing the xml header from the individual records
         portalXML = portalXML.replaceAll ("\\<\\?xml[^\\>]+\\>", "");
      
      //removing punctuation marks that result in invalid SQL statements
         portalXML=portalXML.replace("\'".charAt(0),"\"".charAt(0));
      
         if(record.getMetadataRecords().size()==0)
         {
            portalXML="Not Available";
         }
      
         return portalXML;      
      }
   
   /**
   *Converts a <code>Document</code> to a <code>String</code>.
   *@param doc <code>Document</code> instance to be converted.
   */
       private String getStringFromDocument(Document doc)
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
            //System.out.println("In DatabaseUpdater getStringFromDocument");
               return null;
            }
      }
   
       private String removeStopWords(String title,List<String> stopWords)
      {	      			
         for(int i = 0;i<stopWords.size();i++)
         {
            if(title.startsWith(stopWords.get(i))){
               title = title.substring(stopWords.get(i).length());		
            
            }	
         }
         return title;	
      }
   
   }
