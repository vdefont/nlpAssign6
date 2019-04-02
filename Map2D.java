import java.util.*;

// 2D HashMap that automatically handles cases where keys don't exist yet 
public class Map2D {

  Map<String,Map1D> map;
  Double initVal;

  // This constructor adds keys on "get" operations
  public Map2D (double initVal) {
    this.map = new HashMap<>();
    this.initVal = initVal;
  }

  public Map2D () {
    this.map = new HashMap<>();
  }

  public Map1D get (String a) {
    if (map.containsKey(a)) return map.get(a);
    return null;
  }

  public double get (String a, String b) {
    if (!map.containsKey(a)) {
      if (initVal == null) return 0;
      map.put(a, new Map1D(initVal));
    }
    return map.get(a).get(b);
  }

  public void put (String a, String b, double v) {
    if (!map.containsKey(a)) {
      if (initVal != null) map.put(a, new Map1D(initVal));
      else map.put(a, new Map1D());
    }
    map.get(a).put(b, v);
  }

  public void increment (String a, String b, double v) {
    double oldVal = get(a, b);
    double newVal = oldVal + v;
    put(a, b, newVal);
  }

  public Set<String> keySet () {
    return map.keySet();
  }

}
