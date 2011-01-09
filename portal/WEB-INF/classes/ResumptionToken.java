/*
*@author TATENDA M. CHIPEPEREKWA
*@date 2010-07-25
*The ResumptionToken Class forms an object that represents the
*resumptionToken returned by the repository in a list request.
*
*
*/

    public class ResumptionToken
   {
      private String token;				//the resumption token itself
      private String expirationDate;	//indicates when the resumption token ceases to be valid
      private int completeListSize;		//the cardinality of the complete list
      private int cursor;					//a count of the number of elements of the complete list thus far returned.
   
       public ResumptionToken()
      {
      	  token="";
         expirationDate="";
         completeListSize=0;
         cursor=0;
    
      }
   	 
       public ResumptionToken(String r_token,String r_expirationDate,int r_completeListSize,int r_cursor)
      {
	  token=r_token;
         expirationDate=r_expirationDate;
         completeListSize=r_completeListSize;
         cursor=r_cursor;
      
      }
   	 
      public void setToken(String r_token)
      {
			token=r_token;
      }
		
       public void setExpirationDate(String r_expirationDate)
      {
         expirationDate=r_expirationDate;
      }
   	 
       public void setCompleteListSize(int r_completeListSize)
      {
         completeListSize=r_completeListSize;
      }
   	 
       public void setCursor(int r_cursor)
      {		 
         cursor=r_cursor;
      }
   	 
		 public String getToken()
      {
         return token;
      }
       public String getExpirationDate()
      {
         return expirationDate;
      }
   	 
       public int getCompleteListSize()
      {
         return completeListSize;
      }
   	 
       public int getCursor()
      {		 
         return cursor;
      }

      //this method can be changed to return the values that are in accordance to the
      //format of the resumption token being used by the repository being harvested 
      //from	
      public String toString()
      {
		return token;
      }
		
   	 
   }