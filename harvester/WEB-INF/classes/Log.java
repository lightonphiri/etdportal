
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/*
 * Small class which is used to write all loged activity to file. Can also print out
 * critical messages so that cron will email them to the admin (for harvesting).
 */

/**
 *
 * @author Lawrence Webley
 */
public class Log
{
    BufferedWriter out;

    /*
     * Constructor which uses the given string to open a logfile at that location
     */
    public Log(String logName) throws IOException
    {
        File logFile = new File(logName);
        if(!logFile.exists())
        {
            logFile.createNewFile();
        }

        out = new BufferedWriter(new FileWriter(logFile, true));
        
    }

    /*
     * Adds a message to the log file, prefaced by the current date and time
     */
    public void add(String logMessage)
    {
        SimpleDateFormat UTCDateFormatter = new SimpleDateFormat("d MMM yyyy hh:mm:ss a:");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        UTCDateFormatter.setTimeZone(tz);
        try
        {
            out.write(UTCDateFormatter.format(new Date()) + " " +logMessage+"\n");
            out.flush();
        }
        catch(IOException e)
        {
            System.out.println(UTCDateFormatter.format(new Date()) +" " + "failed to write to log file!");
        }

    }
    
    /*
     * Use this method for critical errors. Adds a message to the log file prefaced
     * by the date and time as well as adding a message to System.out, which will be
     * emailed to the admin by cron.
     */
    public void add(String logMessage, String consoleMessage)
    {
        SimpleDateFormat UTCDateFormatter = new SimpleDateFormat("d MMM yyyy hh:mm:ss a:");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        UTCDateFormatter.setTimeZone(tz);

        add(logMessage);
        System.out.println(UTCDateFormatter.format(new Date()) +" " + consoleMessage);
    }


}
