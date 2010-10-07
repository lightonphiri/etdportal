/**
*Creates Lucene Documents for indexing a metadata record.
*@author Tatenda M. Chipeperekwa
*@date 2010-07-25
*
*/
  import java.io.*;
  import org.apache.lucene.util.Parameter;
  import org.apache.lucene.document.DateTools;
  import org.apache.lucene.document.Document;
  import org.apache.lucene.document.Field;
  import org.apache.lucene.document.Field.Index;
  import org.apache.lucene.document.Field.Store;
  import javax.xml.parsers.DocumentBuilder;
  import javax.xml.parsers.DocumentBuilderFactory;
  import javax.xml.parsers.ParserConfigurationException;
  import org.w3c.dom.Element;
  import org.w3c.dom.NodeList;
  import org.xml.sax.SAXException;
  import java.util.*;	


  public class FileDocument {

    /**
    *Creates Lucene Documents for indexing a metadata record.
    *@param record_to_convert harvested record to be indexed.
    *@param configPath application path used to locate the directory containing
    *			the XSLT that transforms the xml into indexable fields.
    *@return the Lucene Document used for indexing.
    */  
    public Document getDocument(Record record_to_convert,String configPath)
	{
		// make a new, empty document
		Document record_document = new Document();
		
		// Add the identifier of the file as a field named "repository_identifier".  Use a field that is 
		// indexed (i.e. searchable), but don't tokenize the field into words.
		record_document.add(new Field("repository_identifier", record_to_convert.getRepositoryIdentifier(), Field.Store.YES,Field.Index.TOKENIZED ));
		record_document.add(new Field("repository_identifier_updater", record_to_convert.getRepositoryIdentifier(), Field.Store.YES,Field.Index.UN_TOKENIZED ));

		
		String full_doc = "";								//String representation of record
		InputStream record_to_transform = null;		//InputStream used for transformations on the records xml format	
		

		try {//converting the into an inputStream for XSLT trasformtion
		  record_to_transform = new ByteArrayInputStream(record_to_convert.getPortalXML().getBytes("UTF-8"));
		
		  //extracts the String representation of the record that is optimized for indexing.
		  full_doc  += (new ResultFormat()).viewRecord(record_to_transform,configPath+"index.xsl",configPath);
		    
		  record_to_transform = new ByteArrayInputStream(full_doc.getBytes("UTF-8"));

		  Element docEle = getFieldElement(record_to_transform);
		  Record fields = getFields(docEle);
		 
		if(fields.getTitle()!=null)//if there are any titles
		{	
			for(int x = 0;x<fields.getTitle().size();x++)
			{
			// Add the titles of the file as a field named "title".  Use a field that is 
			// indexed (i.e. searchable),tokenize the field into words.
			record_document.add(new Field("title", fields.getTitle().get(x), Field.Store.YES, Field.Index.TOKENIZED));
			//System.out.println(fields.getTitle().get(x));
			}

		}else
		{//no title specified
			record_document.add(new Field("title", "Not Available", Field.Store.YES, Field.Index.UN_TOKENIZED));
		  
		}
		
		
		
		if(fields.getCreators()!=null)//if there are any creators
		{	
			String creators = "";
			for(int x = 0;x<fields.getCreators().size();x++)
			{
			// Add the creators of the file as a field named "creators".  Use a field that is 
			// indexed (i.e. searchable),tokenize the field into words.
			record_document.add(new Field("creator", fields.getCreators().get(x), Field.Store.YES, Field.Index.TOKENIZED));
			//System.out.println(fields.getCreators().get(x));
			}

		}else
		{//no creator specified
			record_document.add(new Field("creator", "Not Available", Field.Store.YES, Field.Index.UN_TOKENIZED));
		  
		}

		record_document.add(new Field("description",fields.getDescription(), Field.Store.YES, Field.Index.TOKENIZED));

		record_document.add(new Field("affiliation",record_to_convert.getAffiliation(), Field.Store.YES, Field.Index.TOKENIZED));
		
		record_document.add(new Field("portalXML",record_to_convert.getPortalXML(), Field.Store.YES, Field.Index.TOKENIZED));
	
		} catch (UnsupportedEncodingException e) {
		  e.printStackTrace();


		}catch(IOException e)
		{
		  e.printStackTrace();
		  }
		
      // returns the document
      return record_document;
    }
/**
*Default Constructor.
*/
    public FileDocument() {}

/**
*Extracts the title,date and the description from a recordElement.
*@param recordElement the document element with the required fields.
*@return a record that contains the required fields.
*/
      public Record getFields(Element recordElement) 
      {
      	//creating a new Record
         Record new_record = new Record();
      	
	      //creating temporary varibles to store the record information
		   List<String> titles=new ArrayList<String>();     //Document Title(s)
     		List<String> creators=new ArrayList<String>();	//Document Creator(s)
      	        String description="";         	              //of the record
	
		try
		{		 description = getTagValue(recordElement,"index:description");
				 
				  NodeList titleList = recordElement.getElementsByTagName("index:title");
	    			  if(titleList != null && titleList.getLength() > 0) 
				  {
					for(int i = 0 ; i < titleList.getLength();i++) 
           				{
             				  Element subject = (Element)titleList.item(i);
			                titles.add(subject.getFirstChild().getNodeValue());
					}
         			  }else
				  {titles=null;}	
	
				NodeList creatorList = recordElement.getElementsByTagName("index:creator");
				 
         			  if(creatorList != null && creatorList.getLength() > 0) 
				  {
					for(int i = 0 ; i < creatorList.getLength();i++) 
           				{
             				  Element creator = (Element)creatorList.item(i);
			                creators.add(creator.getFirstChild().getNodeValue());
					}
         			  }else
				  {creators=null;}

				new_record.setTitle(titles);			
				new_record.setDescription(description);
				new_record.setCreators(creators);
				
          	}catch(Exception e)
		{
			e.printStackTrace();
			//System.out.println("record could not be created,missing vital information,incorrect,incompatible..");
			//System.exit(0);
			
		}		

         
      	
         return new_record;
      }
/**
*Extracts the tag value for elements in a <code>Document</code> tree.
*@param recordElement <code>Element</code> representing the the tag in 
*			the XML file that is to be extracted.
*@param tagName the textual name of the tag to be extracted.
*@return tagValue <code>String</code> representation of tag.
*/	private String getTagValue(Element recordElement, String tagName)
	{
		String tagValue="";//temporary variable
		try{
				  NodeList tagList = recordElement.getElementsByTagName(tagName);
				 
         			  if(tagList != null && tagList.getLength() > 0) 
				  {
             				  Element tag = (Element)tagList.item(0);
			                	if(tag.getFirstChild().getNodeValue() != null)
						{
							tagValue=tag.getFirstChild().getNodeValue();
						       return tagValue;
						}
						else{return "";}
         			  }else
				  {return "";}	
		}catch(Exception e)
		{	//Some records might have a tag but the tag might not contain any value
			//Uncomment the next line to view tag without value
			//System.out.println("Cannot extract value for: " + tagName);
			return "Not Available";	
		}
		
	}
/**
*Creates a Document Element for the record whose fields are
*to be extracted.
*@param record_to_transform InputStream that contains the record
*			    to be converted to a Document Element used when parsing
*			    for the title, data and description.
*@return the Document Element that has been extracted.	  
*/
         private  Element getFieldElement(InputStream record_to_transform)
      {
	 Element docEle=null;
         org.w3c.dom.Document records=null;//Document object model that stores them parsed xml file.
   
   	
      	 //get the factory
         DocumentBuilderFactory recordsFactory = DocumentBuilderFactory.newInstance();
      
         try
         {
         
            //Using factory get an instance of document builder
            DocumentBuilder recordsBuilder = recordsFactory.newDocumentBuilder();
         
            //parse using builder to get DOM representation of the XML file
            records = recordsBuilder.parse(record_to_transform);
            
	       //get the root elememt
          docEle = records.getDocumentElement();
         
         }
             catch(ParserConfigurationException pce) {
               pce.printStackTrace();
            }
             catch(SAXException se) {
               se.printStackTrace();
            }
             catch(IOException ioe) {
               ioe.printStackTrace();
            }
	  
	  return docEle;

      }

  }
      
