import java.util.*;
import java.lang.*;
import java.net.*;

public class GetClassDirectory
{
  public static void main(String args[]) {
      
      String url= new GetClassDirectory().getClass().getProtectionDomain().getCodeSource().getLocation().toString().substring (5);
      //new GetClassDirectory().getClass().getResource("GetClassDirectory.class");
          System.out.println(url);
            }
            }
