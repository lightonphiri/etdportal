

/**
 * An abstract class which all panes of the final html output inherit from.
 * @author Lawrence Webley
 */
public abstract class HtmlPane
{
    /** Stores the html for the pane */
    protected StringBuffer output;

    /**
     * This is a basic skeleton class that uses a <code>stringbuffer</code> to store an html
     * representation for a pane in the control panel.
     */
    public HtmlPane()
    {
        output = new StringBuffer();
    }

}
