
/**
 *
 * @author Lawrence Webley
 */
public class NavBar extends HtmlPane
{    
    /**
     * NavBar class generates the code for the navigation bar at the top of the page
     */
    public NavBar()
    {
        super();
    }

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
