import java.sql.*;
import java.util.ArrayList;

/**
 * A class to connect to a mysql db and store OAIRecord objects
 * @author Alexander van Olst
 * @author Lawrence Webley
 * @author Hussein Suleman
 * @version 1.9.6.27
 */
public class Database {

   /** The sql Statment we use to execute queries and updates */
   private Statement stm;
   /** The connection to the sql database. Used to create the Statement */
   private Connection conn;
   /** contains whether the class is currenly connected to the database */
   private boolean connected;
   /** configuration for database settings */
   private Config conf;

   /**
    * Sets up the local variables and instantiates the class.
    * @param config the configuration object for the currently harvesting repository
    */
   public Database ( Config aConf )
   {
      connected = false;
      conf = aConf;
   }

   /**
    * Attempts to connect to the specified database.
    * <p>
    * Database user settings and login info is stored in the Configuration.xml file
    * and is read in during the startup phase of the harvster. It is stored
    * in the <code>HarvestConfiguration</code> object
    * @return whether the class was successfull in connecting to the database
    */
   public boolean connect ()
   {
      connected = true;
      try {
         Class.forName(conf.getDbDriver ()); // use the jdbc driver
         conn = DriverManager.getConnection (conf.getDbAddress (), conf.getDbUsername (), conf.getDbPassword ()); // get a connection
         stm = conn.createStatement(); // create a statement
         if(stm == null)//if there are no errors, yet the statement was not created - normally incorrect authentification
         {
            connected = false;
            conf.log.add("Error: Could not connect to database. Incorrect Authentication Details?", "Error: Could not connect to database. Incorrect Authentication Details?");

         }
      } catch (Exception e) {
         connected = false;
         conf.log.add("Error connecting to database: "+e, "Error connecting to database: "+e);
      }
      return connected;
   }

   /**
    * Given a <code>OAIRecord</code> object, this method will add the said
    * record to the batch, which will then later be executed to add it to the
    * database.
    *  
    * @param aRecord the record to be stored in the database
    */
    public void addToBatch ( OAIRecord aRecord, Repository rep )
   {
      try {
         String about = aRecord.getAboutField();
         
         String query = "REPLACE INTO Archive VALUES('" +aRecord.getID() +
            "', CURRENT_TIMESTAMP, '" + aRecord.getType() + "','" + aRecord.getSource()+
            "','" + aRecord.getXml() + "'," +aRecord.isDeleted() +",'"+about+
            "','"+rep.getID()+"')";
         stm.addBatch(query);
      } catch (Exception e){
          conf.log.add("Error adding a record to the batch: "+e);
         //System.out.println(e);
         // do nothing, exceptions will be thrown for duplicates
      }
   }

   /**
   *  Executes all the requests that have been added to the batch and disconnect database
   */
   public void executeBatch()
   {
      try {
         stm.executeBatch();
         stm.close ();
         conn.close ();
      } catch(Exception e) {
          conf.log.add("Error occured while storing batch of records: \n"+e, "Error occured while storing batch of records: \n"+e);
         
      }
   }
   
   /**
    * load repository details from database
    */
   public boolean loadRepository ( Repository rep )
   {
      boolean found = false;
      try {
         stm.executeQuery ("select * from Repositories where ID='" + rep.getID () + "'");
         ResultSet rs = stm.getResultSet ();
         if (rs.next ())
         {
            found = true;
            rep.setID (rs.getString ("ID"));
            rep.setName (rs.getString ("name"));
            rep.setBaseURL (rs.getString ("baseURL"));
            rep.setMetadataFormat (rs.getString ("metadataFormat"));
            rep.setSetSpec (rs.getString ("setSpec"));
            rep.setDateFrom (rs.getString ("dateFrom"));
            rep.setHarvestInterval (rs.getInt ("harvestInterval"));
            rep.setRunning (rs.getInt ("isRunning"));
            rep.setHarvestStatus (rs.getString ("harvestStatus"));
         }
         
         rs.close ();
         stm.close ();
         conn.close ();
      } catch ( Exception e ) {
         // found is already false
      }
         
      if (found)
         return true;
      else
         return false;   
   }
   
   /**
    * save repository details to database
    */
   public boolean saveRepository ( Repository rep )
   {
      try {
         stm.executeUpdate ("replace into Repositories (ID, name, baseURL, metadataFormat, setSpec, dateFrom, harvestInterval, harvestStatus, isRunning) values ("+
                            "'" + rep.getID () + "', " +
                            "'" + rep.getName () + "', " +
                            "'" + rep.getBaseURL () + "', " +
                            "'" + rep.getMetadataFormat () + "', " +
                            "'" + rep.getSetSpec () + "', " +
                            "'" + rep.getDateFrom () + "', " +
                            "'" + rep.getHarvestInterval () + "', " +
                            "'" + rep.getHarvestStatus () + "', " +
                            "'" + rep.getRunning () + "')");
         stm.close ();
         conn.close ();
      } catch ( Exception e ) {
         return false;
      }
      return true;
   }  

   /**
    * delete repository from database
    */
   public boolean deleteRepository ( Repository rep )
   {
      try {
         stm.executeUpdate ("delete from Repositories where ID=\'"+rep.getID ()+"\'");
         stm.close ();
         stm = conn.createStatement ();
         stm.executeUpdate ("update Archive set Deleted=\'1\' where Source=\'"+rep.getID ()+"\'");
         stm.close ();
         conn.close ();
      } catch ( Exception e ) {
         return false;
      }
      return true;
   }  
   
   /*
    * list IDs of repositories
    */
   public String[] getRepositoryIDList ()
   {
      // use a variable length list
      ArrayList<String> repoList = new ArrayList<String> ();

      // get entries from database
      try {
         stm.executeQuery ("select ID from Repositories");
         ResultSet rs = stm.getResultSet ();
         while (rs.next ())
            repoList.add (rs.getString ("ID"));
         
         rs.close ();
         stm.close ();
         conn.close ();
      } catch ( Exception e ) {
          conf.log.add("Failed to get repository list! Error: "+e);
      }
      
      // convert list to a simple String list
      String[] rList = new String[repoList.size()];
      for ( int i=0; i<repoList.size(); i++ )
         rList[i] = repoList.get (i);

      return rList;
   } 

   /*
    * list of repositories
    */
   public ArrayList<Repository> getRepositoryList ()
   {
      // use a variable length list
      ArrayList<Repository> repoList = new ArrayList<Repository> ();

      // get entries from database
      try {
         stm.executeQuery ("select * from Repositories");
         ResultSet rs = stm.getResultSet ();
         while (rs.next ())
            repoList.add (new Repository (conf, rs.getString ("ID"), rs.getString ("name"), rs.getString ("baseURL"), 
                                          rs.getString ("metadataFormat"), rs.getString ("setSpec"), 
                                          rs.getString ("dateFrom"), rs.getInt ("harvestInterval"), 
                                          rs.getInt ("isRunning"), rs.getString ("harvestStatus")));
         rs.close ();
         stm.close ();
         
         Statement stm2 = conn.createStatement ();
         stm2.executeQuery ("select source,count(source) from Archive group by source");
         ResultSet rs2 = stm2.getResultSet ();
         while (rs2.next ())
         {
            for ( int i=0; i<repoList.size(); i++ )
               if (repoList.get (i).getID ().equals (rs2.getString ("source")))
                  repoList.get (i).setNumberOfRecords (rs2.getInt ("count(source)"));
         }
         rs2.close ();
         stm2.close ();  
         
         conn.close ();
      } catch ( Exception e ) {
          conf.log.add("Failed to get repository list! Error: "+e);
      }

      return repoList;
   } 
}
