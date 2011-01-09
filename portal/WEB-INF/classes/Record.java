/**
*The Record Class forms an object from the xml record
*that is read from the repository.
*
*
*@author TATENDA M. CHIPEPEREKWA
*@date 21/12/09
*
*/
   import java.io.IOException;
   import java.util.Iterator;
   import java.util.ArrayList;
   import java.util.List;
   import java.util.Date;
   import org.w3c.dom.Document;
    public class Record
   {
      private String repository_identifier;		//Documents repository identifier
      private String affiliation;					//The institution from which the record was harvested
      private String identifier;						//Documents metadata identifier
      private List<String> titles;					//Document Title(s)
      private List<String> creators;				//Document Creator(s)
      private String record_date;
      private String description;				
      private List<String> subjects;				//Subjects discussed
      private Boolean status;							//Stores the deleted attribute of a record
      private String type;								//the document type
      private String format;							//the file format
      private String xmlFormat;
      private List<String> metadataFormats;			//list of the records metadata formats in a repository	
      private List<Document> metadataRecords;		//stores a list of the record in its different metadataFormats in DOM form
      private String portalXML;							//stores the string representation of all the metadata formats of the record
      private String recordTitle;
      private Document record_header;					//stores the header for the document returned in the ListIdentifiers request
      public Boolean error;
   
   /**
   *Default constructor.
   */
       public Record()
      {
         repository_identifier="";  		
			identifier="";				
			titles=new ArrayList<String>();		
         creators=new ArrayList<String>();	
         record_date="1900-01-01";
         description="";				
         subjects=new ArrayList<String>();		
         type="";					
         format="";					
         status=false;				
         xmlFormat="";
         metadataFormats=new ArrayList<String>();			
         metadataRecords=new ArrayList<Document>();
         portalXML="";
         recordTitle="";
         affiliation="Not Set";
      }
   	 
      //Mutator methods
   
       public void setRecordHeader(Document m_header)
      {
         record_header=m_header;
      }
       public void setAffiliation(String m_affiliation)
      {
         affiliation=m_affiliation;
      }
       public void setPortalXML(String m_portalXML)
      {
         portalXML=m_portalXML;
      }
       public void setIdentifier(String m_identifier)
      {
         identifier=m_identifier;
      }
       public void setRepositoryIdentifier(String m_identifier)
      {
         repository_identifier=m_identifier;
      }
   
       public void setXmlFormat(String param)
      {
         xmlFormat=param;
      }
       public void setMetadataFormats(List<String> m_formats)
      {
         metadataFormats=m_formats;
      }	 
       public void setMetadataRecords(List<Document> m_records)
      {
         metadataRecords=m_records;
      }	 
       public void setTitle(List<String> m_title)
      {
         titles=m_title;
      }
       public void setRecordTitle(String m_record)
      {
         recordTitle=m_record;
      }
       public void setType(String m_type)
      {
         type=m_type;
      }	
   
       public void setFormat(String m_format)
      {
         format=m_format;
      }	
   	 
       public void setCreators(List<String> m_creators)
      {		 
         creators=m_creators;
      }
   	 
   	 
       public void setDate(String m_record_date)
      {		 
         record_date=m_record_date;
      }
   	 
   	 
       public void setDescription(String m_description)
      {		 
         description=m_description;
      }
   
       public void setSubjects(List<String> m_subjects)
      {		 
         subjects=m_subjects;
      }
   	
   	
       public void setStatus(Boolean m_status)
      {		 
         status=m_status;
      }
   
   //Accessor methods
       public Document getRecordHeader()
      {
         return record_header;
      }
       public String getType()
      {
         return type;
      }
   
       public String getXmlFormat()
      {
         return xmlFormat;
      }	
   
       public String getFormat()
      {
         return format;
      }
       public String getAffiliation()
      {
         return affiliation;
      }
   
       public String getIdentifier()
      {		 
         return identifier;
      }
       public String getPortalXML()
      {		 
         return portalXML;
      } 
       public String getRepositoryIdentifier()
      {		 
         return repository_identifier;
      }
   
       public List<String> getTitle()
      {
         return titles;
      }
       public String getRecordTitle()
      {
         return recordTitle;
      }
       public List<String> getMetadataFormats()
      {
         return metadataFormats;
      }
       public List<Document> getMetadataRecords()
      {
         return metadataRecords;
      }
       public List<String> getCreators()
      {		 
         return creators;
      }
   	 
   	 
       public String getDate()
      {		 
         return record_date;
      }
   	 
   	 
       public String getDescription()
      {		 
         return description;
      }
   
       public List<String> getSubjects()
      {		 
         return subjects;
      }
   
   		
       public Boolean getStatus()
      {		 
         return status;
      }
   
   /**
   *Checks if a record is valid i.e. it has a certain number of fields
   *including the title field.
   *@return <code>true</code> if all fields are available,otherwise <code>false</code>
   */
       public Boolean checkValidRecord()
      {
      
         if(getTitle().equals("")||getCreators().equals("")||getSubjects().equals(""))
         {
            return false;
         }
         else
         {
            return true;
         }
      
      }
   	
       public String toString()
      {
      
         String fullRecord="";
      
      	
      //fullRecord+=  //"Identifier: "+ getIdentifier() + "\n"+
         fullRecord+="<b>Title:</b>&nbsp;&nbsp; \n";
      
         if(getTitle()!=null)//if there are any creators
         {
            for(int x = 0;x<getTitle().size();x++)
            {
               fullRecord+="<big>"+getTitle().get(x)+"</big></br>\n";
            
            }
         }
      
         fullRecord+="<b>Type:</b>&nbsp;&nbsp;" +getType()+"</br>\n"+
             "<b>Format:</b>&nbsp;&nbsp; "+getFormat()+"</br>\n"+
             "<b>Creators:</b>&nbsp;&nbsp; \n";	
      
         if(getCreators()!=null)//if there are any creators
         {
            for(int x = 0;x<getCreators().size();x++)
            {	
               if(x>0)
                  fullRecord+=";";
               fullRecord+=getCreators().get(x)+"\n";	
            }
         }
      
         fullRecord+="</br><b>Subjects:</b>&nbsp;&nbsp;\n";
      
         if(getSubjects()!=null)//if there are any subjects
         {
            for(int x = 0;x<getSubjects().size();x++)
            {
               if(x>0)
                  fullRecord+=";";
               fullRecord+=getSubjects().get(x)+"\n";	
            }
         }
      
         fullRecord+= "</br><b>Date:</b>&nbsp;&nbsp; " + getDate()+"</br>\n"+
              "<b>Description:</b>&nbsp;&nbsp; "+getDescription()+"\n";
      			  		  
         return  fullRecord;  
      		  
      		
      
      }   
   
   }
