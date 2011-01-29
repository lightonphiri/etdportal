
/**
 * Date 12 April 2009
 * @author Lawrence Webley
 * This is a wrapper class to hold the details of a particular format (like dublin core)
 */
public class MetadataFormat 
{
    private String prefix;
    private String schema;
    private String namespace;
    public MetadataFormat(String formatPrefix,String formatSchema,String formatNamespace)
    {
        prefix = formatPrefix;
        schema = formatSchema;
        namespace = formatNamespace;
    }
    public String getPrefix()
    {
        return prefix;
    }
    public String getSchema()
    {
        return schema;
    }
    public String getNamespace()
    {
        return namespace;
    }
}
