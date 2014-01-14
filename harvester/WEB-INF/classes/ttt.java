import java.net.URLEncoder;

public class ttt {

   public static void main(String[] args) throws Exception {
   
      String param1Before = "hello there";
      String param1After = URLEncoder.encode(param1Before, "UTF-8");
      System.out.println("param1 before:" + param1Before);
      System.out.println("param1 after:" + param1After);
                           
      String param2Before = "good-bye, %friend";
      String param2After = URLEncoder.encode(param2Before, "UTF-8");
      System.out.println("param2 before:" + param2Before);
      System.out.println("param2 after:" + param2After);
   }
}
                                                      