

/**
 * NavBar class generates the code for the navigation bar at the top of the page
 * @author Lawrence Webley
 */
public class NavBar extends HtmlPane
{
    /**
     * Basic constructor that intializes its parent constructor.
     */
    public NavBar()
    {
        super();
    }

    /**
     * Generates the html for the navigation bar at the top of the page, including
     * links to add a new repository, or list available repositories.
     * @return A stringbuffer containing the html code for the <code>nav</code>
     * (navigation) division of the webpage.
     */
    public StringBuffer getNav()
    {
        output.append("    <div id=\"nav\">\n");
        output.append("        <ul>\n");
        output.append("            <li><a href=\"controlPanel?loc=add\">Add a new repository</a></li>\n");
        output.append("            <li><a href=\"controlPanel?loc=list\">List available repositories</a></li>\n");
        output.append("        </ul>\n");
        output.append("    </div>\n");

        return output;
    }
}
