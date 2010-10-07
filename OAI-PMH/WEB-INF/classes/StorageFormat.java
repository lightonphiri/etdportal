

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Date 12 April 2009
 * @author Lawrence Webley
 * This is a sort of wrapper class to hold the details of a particular format (like dublin core)
 */
public class StorageFormat 
{
    private String name;
    private String metaDesc;
    public StorageFormat(String formatName,String metadata)
    {
        name = formatName;
        metaDesc = metadata;
    }
    public String getName()
    {
        return name;
    }
    public String getMeta()
    {
        return metaDesc;
    }
}
