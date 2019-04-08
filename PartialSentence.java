import java.util.*;

public class PartialSentence implements Comparable<PartialSentence> {

  private List<String> words;
  private Double logProb;

  public PartialSentence () {
    words = new ArrayList<>();
    logProb = 0.0;
  }

  public PartialSentence (PartialSentence root, String newWord, double newProb) {
    words = new ArrayList<>(root.getWords());
    words.add(newWord);
    logProb = root.getLogProb() + (Math.log(newProb) / Math.log(10.0));
  }

  public List<String> getWords () {
    return words;
  }

  public String getLastWord () {
    return words.get(words.size() - 1);
  }

  public Double getLogProb () {
    return logProb;
  }

  public int length () {
    return words.size();
  }

  @Override
  public int compareTo (PartialSentence other) {
    return other.getLogProb().compareTo(logProb); // Smaller is better
  }

  // Test to ensure functionality works
  public static void main (String[] args) {
    PartialSentence a = new PartialSentence();
    PartialSentence b = new PartialSentence(a, "first", 0.1);
    a = new PartialSentence(b, "second", 0.2);
    for (String w : a.getWords()) System.out.println(w);
    System.out.println(a.getLogProb());
    System.out.println(a.getLastWord());
  }

}
