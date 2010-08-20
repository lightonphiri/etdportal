
/**
 * A class to wrap the given body data into a valid <code>html</code> code.
 * <p>
 * Given html code for the main body of a page, using the <code>div</code>'s of
 * <code> header, nav, main, sidebar</code> and <code>footer</code>, this class
 * will wrap the given <code>div</code>'s so that the final product is valid
 * html code, with a linked css stylesheet which will lay out those
 * <code>div</code>'s correctly.
 * @author Lawrence Webley
 */
public class HTMLWriter
{
    /** the final html output */
    StringBuffer output;

    /**
     * Constructor.
     * Creates a new string buffer and adds to it all the html specific header tags and declarations.
     * To actaully use this class, see the <code>format</code> method
     */
    public HTMLWriter()
    {
        //html declaration
        output = new StringBuffer();
        //output.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\"\n");
        //output.append("     \"http://www.w3.org/TR/html4/strict.dtd\">\n");

        //header
        output.append("<html lang=\"en\">\n");
        output.append("<head>\n");
        output.append("     <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n");
        output.append("     <title>OAI Harvester Control Panel</title>\n");
        output.append("     <meta name=\"description\" content=\"Control panel for the harvester, which gets records from other database and stores them in the local database\">\n");
        output.append("     <meta name=\"author\" content=\"Lawrence Webley\">\n");
        output.append("     <link rel=\"stylesheet\" title=\"style\" type=\"text/css\" href=\"style/style.css\"/>\n");
        output.append("     <link rel=\"stylesheet\" type=\"text/css\" title=\"style\" href=\"controlPanel?loc=styleSheet\">\n");
        //add a java script for deletion confirmation
        output.append("     <script type=\"text/javascript\">\n");
        output.append("         function executeOnDelete()\n");
        output.append("         {\n");
        output.append("         var res = confirm(\"Are u sure you want to delete this repository?\");\n");
        output.append("         if(res)\n");
        output.append("             return true;\n");
        output.append("         else\n");
        output.append("             return false;\n");
        output.append("         }\n");
        output.append("     </script>\n");
        //and close the script elemnts.
        output.append("</head>\n");
    }

    /**
     * Takes a <tt>StringBuffer</tt> and surrounds it with the html tags and
     * declarations that are neccesary for user viewing. Also links it to a
     * stylesheet
     * 
     * @param content the div content that will be wrapped in an html body.
     * @return output the final formatted html that is ready to send.
     */
    public StringBuffer format(StringBuffer content)
    {
        output.append(addBody(content));
        output.append("</html>\n");
        return output;
    }

    /**
     * Adds body tags to the given <tt>StringBuffer</tt>.
     * @param content The div content to be wrapped in body tags.
     * @return bodyContent The given content, wrapped in body tags and a "wrap" div.
     */
    public StringBuffer addBody(StringBuffer content)
    {
        StringBuffer bodyContent = new StringBuffer();
        bodyContent.append("<body>\n");
        bodyContent.append("<div id=\"wrap\">\n");

        bodyContent.append(content);

        bodyContent.append("</div>\n");
        bodyContent.append("</body>\n");

        return bodyContent;

    }
}
