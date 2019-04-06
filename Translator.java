import java.util.*;
import java.io.*;

public class Translator {

  // Chosen by tuneParameters method
  private static final double BEST_DISCOUNT = 0.5;

  public Translator () {

    LMModel lm = new DiscountLMModel("data/lm/all", BEST_DISCOUNT);

    String inputFile = "data/probs.csv";
    String outFile = "data/likelyWords.csv";
    int maxCandidates = 3;
    makeLikelyWordsFile(inputFile, outFile, maxCandidates);

    //Map<String,Map<String,Double>> likelyWords = readLikelyWordsFile(outFile);

    // TODO
    // - Rerun WordAlign and use ^ as a separator (this is found nowhere)
    // - Rerun makeLikelyWordsFile
    // - Run logic where you build up sentences word-by-word

  }

  // Results: 100.95, 100.16, 113.80, 144.84
  // Best: 0.5 discount factor
  private void tuneParameters () {
    double[] discounts = new double[] {0.8,0.5,0.1,0.01};
    for (double discount : discounts) {
      System.out.println(discount);
      LMModel lm = new DiscountLMModel("data/lm/train", discount);
      System.out.println(lm.getPerplexity("data/lm/dev"));
    }
  }

  // WORKS
  private class EnWordProb implements Comparable<EnWordProb> {
    private String enWord;
    private Double prob;
    public EnWordProb (String enWord, double prob) {
      this.enWord = enWord;
      this.prob = prob;
    }
    @Override
    public int compareTo (EnWordProb other) {
      return prob.compareTo(other.getProb());
    }
    public String getEnWord () {
      return enWord;
    }
    public Double getProb () {
      return prob;
    }
  }

  // WORKS
  private Map<String,Map<String,Double>> readLikelyWordsFile (String inFile) {
    Map<String,Map<String,Double>> likelyWords = new HashMap<>();
    try {
      BufferedReader file = new BufferedReader(new FileReader(inFile));
      String line = file.readLine();
      while (line != null) {
        String[] words = line.split("\\^");
        String fr = words[0];
        String en = words[1];
        double prob = Double.valueOf(words[2]);

        // Ensure key
        if (!likelyWords.containsKey(fr)) likelyWords.put(fr, new HashMap<>());
        // Add to map
        likelyWords.get(fr).put(en, prob);

        line = file.readLine();
      }
      file.close();
    } catch (IOException e) {
      System.out.println("Error reading file: " + e);
    }
    return likelyWords;
  }

  // WORKS
  // <maxCandidates> most likely words to explain each foreign word
  private void makeLikelyWordsFile (String inputFile, String outFile, int maxCandidates) {

    // Start by building priority queue
    Map<String,PriorityQueue<EnWordProb>> likelyWordsQ = new HashMap<>();

    try {
      BufferedReader file = new BufferedReader(new FileReader(inputFile));
      String line = file.readLine();
      while (line != null) {
        String[] words = line.split("\\^");
        String en = words[0];
        String fr = words[1];
        double prob = Double.valueOf(words[2]);

        // Ensure key
        if (!likelyWordsQ.containsKey(fr)) likelyWordsQ.put(fr, new PriorityQueue<>());
        // Add to priority queue
        likelyWordsQ.get(fr).add(new EnWordProb(en, prob));
        // Ensure that the queue isn't too large
        if (likelyWordsQ.get(fr).size() > maxCandidates) likelyWordsQ.get(fr).poll();

        line = file.readLine();
      }
      file.close();
    } catch (IOException e) {
      System.out.println("Error reading file: " + e);
    }

    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));

      for (String fr : likelyWordsQ.keySet()) {
        Map<String,Double> m = new HashMap<>();
        PriorityQueue<EnWordProb> q = likelyWordsQ.get(fr);
        while (q.size() > 0) {
          EnWordProb enP = q.poll();
          String en = enP.getEnWord();
          double p = enP.getProb();
          writer.write(String.join("^", fr, en, "" + p) + "\n");
        }
      }

      writer.close();
    } catch (IOException e) {
      System.out.println("Error writing to file");
    }
  }

  public static void main (String[] args) {

    new Translator();

  }

}
