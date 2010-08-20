
import java.sql.*;

/**
 * A class to connect to a mysql db and store OAIRecord objects
 * @author Alexander van Olst
 * @author Lawrence Webley
 * @version 1.9.6.27
 */
public class DatabaseConnection {

    /** The config object for the currently harvesting repository */
	private HarvestConfiguration configuration;
    /** The sql Statment we use to execute queries and updates */
	private Statement stm;
    /** The connection to the sql database. Used to create the Statement */
	private Connection conn;
    /** contains whether the class is currenly connected to the database */
	private boolean connected;

    /**
     * Sets up the local variables and instantiates the class.
     * @param config the configuration object for the currently harvesting repository
     */
	public DatabaseConnection (HarvestConfiguration config)
	{
		connected = false;
		configuration = config;
	}

    /**
     * Attempts to connect to the specified database.
     * <p>
     * Database user settings and login info is stored in the Configuration.xml file
     * and is read in during the startup phase of the harvster. It is stored
     * in the <code>HarvestConfiguration</code> object
     * @return whether the class was successfull in connecting to the database
     */
	public boolean Connect(){
		connected = true;
		try {
			Class.forName("com.mysql.jdbc.Driver"); // use the jdbc driver
			conn = DriverManager.getConnection (configuration.getDatabaseURL(), configuration.getDatabaseUser(), configuration.getDatabasePassword()); // get a connection
			stm = conn.createStatement(); // create a statement
			if(stm == null)//if there are no errors, yet the statement was not created - normally incorrect authentification
			{
				connected = false;
				System.out.println("Error: Could not connect to database. Incorrect Authentication Details?");
			}
		} catch (Exception e){
			connected = false;
			System.out.println("Error connecting to database: "+e);
		}
		return connected;
	}

    /**
     * Given a <code>OAIRecord</code> object, this method will store said record
     * in the database.
     * <p>
     * This should probably be rewritten to only add a insert to the statement batch
     * and then once a hundred or so records have been added to the batch, the
     * statement should be executed. At the moment it executes the batch for each
     * run of this method.
     * @param aRecord the record to be stored in the database
     */
	public void insert(OAIRecord aRecord){

		try { // try execute the query
			String query = "INSERT INTO Archive VALUES('" +aRecord.getID() + "', CURRENT_TIMESTAMP, '" + aRecord.getType() + "','" + aRecord.getSource()+ "','" + aRecord.getXml() + "'," +aRecord.isDeleted() +",'About field')";
			stm.addBatch(query);
			//System.out.println("Added: " + aRecord.getXml());
			stm.executeBatch();//this line does the work  ?? perhaps i should do several stm.addBatch(query) before calling this and have this function contain a 

		} catch (Exception e){
			//System.out.println(e);
			// do nothing, exceptions will be thrown for duplicates
		}
	}
	



}
