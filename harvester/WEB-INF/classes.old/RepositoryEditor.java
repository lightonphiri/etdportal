

/**
 *
 * @author Lawrence Webley
 */
public class RepositoryEditor extends HtmlPane
{
    public RepositoryEditor()
    {
        super();
    }

    public StringBuffer getRepoEditor()
    {
        output.append("     <div id=\"main\">\n");
        output.append("         <h2>Repository set up</h2>\n");
        output.append("         <fieldset>\n");
        output.append("             <legend>Repository Settings</legend>\n");
        //Input
        output.append("             <form name=\"input\" action=\"controlPanel\" method=\"get\">\n");
        output.append("             Repository Name:\n");
        output.append("             <input type=\"text\" name=\"name\" />\n");
        output.append("             <br />");
        output.append("             URL to servlet:\n");
        output.append("             <input type=\"text\" name=\"url\" />\n");
        output.append("             <br />");
        output.append("             Metadata format to harvest:\n");
        output.append("             <input type=\"text\" name=\"meta\" />\n");
        output.append("             <br />");
        output.append("             Harvest interval:\n");
        output.append("             <input type=\"text\" name=\"interval\" />\n");
        output.append("             <br />");
        output.append("             Repository comment (Optional):\n");
        output.append("             <input type=\"text\" name=\"comment\" />\n");
        output.append("             <br />");
        output.append("             <input type=\"submit\" value=\"Save Repository\" />\n");

        output.append("             </form>\n");
        output.append("         </fieldset>\n");
        output.append("     </div>\n");
        
        output.append("");
        output.append("");

        return output;
    }
}
