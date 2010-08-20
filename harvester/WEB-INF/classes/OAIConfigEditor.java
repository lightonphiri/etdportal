

/* import statements */
import java.util.Scanner;
import java.util.Properties;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Command line tool to manipulate the configuration files
 * of the OAI Harvester. This is to be used when manually editing files, whilst
 * making sure the XML is still valid.
 * @author Alexander van Olst
 * @author Lawrence Webley (Javadoc only)
 * @version 1.9.6.24
 */
public class OAIConfigEditor{

    /**
     * Main method, starts up the command line tool.
     * @param args
     */
	public static void main (String[] args){
		
		Scanner keyboard = new Scanner (System.in);

		boolean done = false;
		int option = 0;
		while (!done){ // repeat until a valid choice chosen
			System.out.println("---OAI Configuration Editor---\n");	
			System.out.println("1. View/edit database options");
			System.out.println("2. View/edit harvest options");
			System.out.print  ("Enter choice: ");
			try {
				option = keyboard.nextInt(); // get the next integer
				keyboard.nextLine(); // clear input buffer
				System.out.println(); // blank line
				if ( (option >= 1) && (option <= 2) ) // check it is valid
					done = true;
				else System.out.println("Please choose a valid option"); // complain if not valid
			} catch (Exception e){
				System.out.println("Please choose a valid option"); // complain if error
			}
		}

		if (option == 1) // if database editor chosen
			editDatabaseOptions(); // run the database edit method
		else if (option == 2) // if havest editor chosen
			editHarvestOptions(keyboard); // 
	}

    /**
     * Allows editing of database options.
     * <p>
     * This isnt actually implemented - Alex never finished coding it, and since
     * I dont see any real use for this tool with the web-based config tool in
     * place, i havent finished it off.
     */
	private static void editDatabaseOptions(){ // function to edit database options
		System.out.println("To be implemented");
	}

    /**
     * This method allows you to edit the harvest options. It will
     * also allow you to create a new repository to harvest. This is used via
     * the main method, and is so accessed via there.
     * @param keyboard the scanner object that will hook the keyboard
     */
	private static void editHarvestOptions(Scanner keyboard){ // function to edit harvest options
		boolean done = false;
		System.out.println("\n---Editing harvest options---\n");
		int option = -1;
		String[] files = null;
		while (!done){
			System.out.println("Select a harvest to edit: ");
			File sources = new File("sources"); // file object for the sources directory
			files = sources.list(); // get the children of sources directory 

			if (files != null){
				for (int i = 0; i < files.length; i++) // list existing files
					System.out.println(i + ". " + files[i]); // print each entry
			}

			System.out.println(files.length + ". CREATE NEW FILE");
			System.out.print  ("Enter choice: "); // get option
			
			
			try{
				option = keyboard.nextInt();
				keyboard.nextLine(); // clear input buffer
				System.out.println();
				if ( (option >= 0) && (option <= files.length) ) // check option is valid
					done = true;
				else System.out.println("Please choose a valid option");
			} catch (Exception e){
				System.out.println("Please choose a valid option");
			}

		}

		Properties config = new Properties();
		
		if (option == files.length){

			System.out.println("Creating a new configuration file");
			
			System.out.print  ("Enter a filename: ");
			String filename = keyboard.nextLine();

			config.setProperty("baseURL","http://");
			config.setProperty("metadataFormat","oai_dc");
			config.setProperty("harvestStatus","Never run");
			config.setProperty("harvestInterval","300000");	
			config.setProperty("isRunning", "false" );

			try {
				FileOutputStream ofs = new FileOutputStream("sources/" + filename);
				config.storeToXML(ofs,"OAI Metadata Harvester Settings File");
				ofs.close();
			} catch (Exception e){
				System.out.println("Error, quitting");
			}

		}

		if (option != files.length){ // if the file has not just been created
			
		}

	}

}
