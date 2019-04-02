import java.util.*;

public class WordAlign {

  // TODO:
  // - Read from file
  // - Print out desired output

  public WordAlign (String enSentenceFile, String frSentenceFile, int iterations, double probThresh) {
    Sentence s1 = new Sentence(new String[] {"a","b"},new String[] {"x","y"});
    Sentence s2 = new Sentence(new String[]{"a","d"},new String[] {"x","z"});
    Sentence[] sentences = new Sentence[] {s1, s2};

    // Initialize probabilities
    double initProb = 0.01;
    Map2D probs = new Map2D(initProb);

    for (int i = 0; i < iterations; i += 1) {
      runIteration(sentences, probs);
    }

    for (String e : probs.keySet()) {
      System.out.println("\n" + e);
      for (String f : probs.get(e).keySet()) {
        System.out.println(" " + f + ": " + probs.get(e).get(f));
      }
    }
  }

  private void runIteration (Sentence[] sentences, Map2D probs) {
    // Build sum of partial counts
    Map2D partCountsPair = new Map2D();
    Map1D partCountsSing = new Map1D();

    // Process each sentence, adding to partial counts
    for (Sentence s : sentences) {
      for (String f : s.fr) {

        // Build denominator: probability of (anything -> f)
        double denom = 0;
        for (String e : s.en) {
          denom += probs.get(e, f);
        }

        // Add partial count for each e-f pair
        for (String e : s.en) {
          double partCount = probs.get(e, f) / denom;
          partCountsPair.increment(e, f, partCount);
          partCountsSing.increment(e, partCount);
        }
      }
    }

    // Recompute probabilities
    for (String e : probs.keySet()) {
      for (String f : probs.get(e).keySet()) {
        double prob = partCountsPair.get(e, f) / partCountsSing.get(e);
        probs.put(e, f, prob);
      }
      probs.get(e).normalize();
    }
  }

  public static void main (String[] args) {
    String enSentenceFile = args[0];
    String frSentenceFile = args[1];
    int iterations = Integer.valueOf(args[2]);
    double probThresh = Double.valueOf(args[3]);

    new WordAlign(enSentenceFile, frSentenceFile, iterations, probThresh);
  }

}
