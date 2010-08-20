/**
 * Contains the classes that make up the metadata harvester.
 * <p>
 * The harvester starts with the {@link Harvester.OAIHarvest} class which (if no arguments are specified)
 * checks all valid config files in the sources directory, and harvests the repositories
 * represented by each if they need to be harvested. The decision of whether to harvest
 * each of the repositories is made in the {@link Harvester.OAIScheduledHarvest}
 * class. All reading and writing of config files is handled in the
 * {@link Harvester.HarvestConfiguration} class. {@link Harvester.DatabaseConnection}
 * is responsible for how the harvester connects to, and stores the records in a
 * database. The current version stores records in a Mysql database, but the
 * harvester could easily store them to a different database should that class
 * be replaced. {@link Harvester.OAIRecord} is a representation of a single
 * harvested record and {@link Harvester.OAIRequest}is responsible for
 * communicating with the remote server. {@link Harvester.OAIResponseHandler}
 * seperates the response into its constituent records.
 * @author Alexander van Olst
 * @author Lawrence Webley
 */
package Harvester;