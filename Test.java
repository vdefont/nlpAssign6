import java.util.*;

public class Test {


  public Test () {
    String x = "a^b^c";
    String[] w = x.split("\\^");
    System.out.println(w.length);
  }

  public static void main (String[] args) {
    new Test();
  }


}
