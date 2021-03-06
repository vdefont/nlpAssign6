import java.util.*;

// A Hash Map that automatically handles cases where the key doesn't exist yet 
public class Map1D {

  private Map<String, Double> map;
  private Double initVal;

  // This constructor adds keys on "get" operations
  public Map1D (double initVal) {
    this.map = new HashMap<>();
    this.initVal = initVal;
  }
  public Map1D () {
    this.map = new HashMap<>();
  }

  public boolean containsKey (String k) {
    return map.containsKey(k);
  }

  public double get (String k) {
    if (!map.containsKey(k)) {
      if (initVal == null) return 0;
      map.put(k, initVal);
    }
    return map.get(k);
  }

  public void put (String k, double v) {
    map.put(k, v);
  }

  public void increment (String k, double v) {
    double oldVal = 0;
    if (map.containsKey(k)) {
      oldVal = map.get(k);
    }
    double newVal = oldVal + v;
    map.put(k, newVal);
  }

  // Ensures that all the values sum to 1
  public void normalize () {
    double sum = getSum();
    for (String k : map.keySet()) {
      double newVal = map.get(k) / sum;
      map.put(k, newVal);
    }
  }

  public Set<String> keySet () {
    return map.keySet();
  }

  private double getSum () {
    double sum = 0;
    for (String k : map.keySet()) {
      sum += map.get(k);
    }
    return sum;
  }

}
