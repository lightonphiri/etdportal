import java.util.regex.*;
import java.text.*;
import java.util.TimeZone;
import java.sql.*;
import java.util.*;

class Test
{
   public static void main ( String [] args ) throws Exception
   {
//        Class.forName ("com.mysql.jdbc.Driver");
//        Connection con = DriverManager.getConnection ("jdbc:mysql://localhost:3306/dbuniona?useUnicode=true&amp;characterEncoding=utf-8&amp;useTimezone=true", "dbuniona", "dbuniona");
        
//        Statement stm = con.createStatement();
//        ResultSet rs = stm.executeQuery("SELECT Date FROM Archive WHERE ID = 'oai:union.ndltd.org:AUCKLAND/oai:researchspace.auckland.ac.nz:2292/3387'");
//        if (rs.next ())
//           System.out.println (rs.getTimestamp ("Date").toString ());

        SimpleDateFormat UTCDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        UTCDateFormatter.setTimeZone(tz);
        SimpleDateFormat MysqlDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone tz2 = TimeZone.getTimeZone("GMT+2");
        MysqlDateFormatter.setTimeZone(tz2);

//        Calendar cal = Calendar.getInstance();
//        cal.setTimeZone (TimeZone.getTimeZone("GMT+3"));
//        System.out.println (UTCDateFormatter.format ( Timestamp.valueOf (rs.getTimestamp ("Date").toString ()) ));

        System.out.println (UTCDateFormatter.format (
        MysqlDateFormatter.parse ("2010-10-11 15:53:53") 
        )
        );
   
/*        String aDate = args[0];

        SimpleDateFormat UTCDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        TimeZone tz1 = TimeZone.getTimeZone("UTC");
        UTCDateFormatter.setTimeZone(tz1);

        SimpleDateFormat MysqlDateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        TimeZone tz2 = TimeZone.getTimeZone("GMT+2");
        MysqlDateFormatter.setTimeZone(tz2);
        
        System.out.println ((MysqlDateFormatter.format(UTCDateFormatter.parse (aDate))));
*/

/*      String s = "2002-01-02T00:00:00Z!2012-12-12T23:59:59Z!!oai_dc!100!oai:union.ndltd.org:someset.edu/oai:someset.edu:1234";
      
      Pattern p = Pattern.compile ("([12][0-9]{3}-[01][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9]Z)!([12][0-9]{3}-[01][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9]Z)!([^!]*)!([^!]+)!([0-9]+)!(.*)");
      Matcher m = p.matcher (s);
      if (m.matches ())
         for ( int i=1; i<=m.groupCount (); i++ )
            System.out.println ("Group "+i+": "+m.group (i));
      else
         System.out.println ("no match!");
*/
   }
}
