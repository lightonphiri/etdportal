/*
 * Writes the given data into html format
 */

/**
 *
 * @author Lawrence Webley
 */
public class HTMLWriter
{
    StringBuffer output;

    /**
     * Constructor.
     * Creates a new string buffer and adds to it all the html specific header tags and declarations.
     * To actaully use this class, see the format method
     */
    public HTMLWriter()
    {
        //html declaration
        output = new StringBuffer();
//        output.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\"\n");
//        output.append("     \"http://www.w3.org/TR/html4/strict.dtd\">\n");

        //header
        output.append("<html>\n");
        output.append("<head>\n");
        output.append("     <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n");
        output.append("     <title>OAI Harvester Control Panel</title>\n");
        output.append("     <link rel=\"stylesheet\" title=\"style\" type=\"text/css\" href=\"style/style.css\"/>\n");
        output.append("     <meta name=\"description\" content=\"Control panel for the harvester, which gets records from other database and stores them in the local database\">\n");
        output.append("     <meta name=\"author\" content=\"Lawrence Webley\">\n");
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
