


/**
 * A threaded version of the harvester. Takes and passes on the harvester args
 * and then runs a threaded instance of the harvester so that the webserver is
 * free to webserve.
 * @author Lawrence
 */
public class HarvesterThread extends Thread
{
    /** The arguments to be passed on when <code>run</code> is executed*/
    private String [] arguments;

    /**
     * Instantiates the <code>HarvesterThread</code> class.
     * <p>
     * Once instantiated, all you need call is run, and this class will run the
     * {@link Harvester.HarvesterThread} class in a seperate thread.
     * @param args the arguments to be passed on
     */
    public HarvesterThread(String [] args)
    {
        arguments = args;
    }

    /**
     * Starts the thread.
     * <p>
     * This effectivly is the same as running <code>Harvester.OAIHarvest.main(args)
     * </code> except that it will be run in a thread, so you will not have to wait
     * for the harvest to finish before being able to continue whatever it was
     * you were doing.
     */
    @Override
    public void run()
    {
        OAIHarvest.main(arguments);
    }
}
