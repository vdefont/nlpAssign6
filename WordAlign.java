import java.util.*;
import java.io.*;

// TODO make more efficient so it runs on full example

public class WordAlign {

  private static final String NULL_STRING = "NULLnullNULL";

  // Reads from files, runs E-M iterations, prints output
  public WordAlign (String enSentenceFile, String frSentenceFile, int iterations, double probThresh) {
    List<Sentence> sentences = getSentences(enSentenceFile, frSentenceFile);

    // Initialize probabilities
    double initProb = 0.01;
    Map2D probs = new Map2D(initProb);

    for (int i = 0; i < iterations; i += 1) {
      runIteration(sentences, probs);
    }

    for (String e : probs.keySet()) {
      for (String f : probs.get(e).keySet()) {
        double prob = probs.get(e).get(f);
        if (prob >= probThresh) {
          // Replace null string
          String eOut = e.equals(NULL_STRING) ? "NULL" : e;
          System.out.println(eOut + "\t\t" + f + "\t\t" + prob);
        }
      }
    }
  }

  // Reads files and returns a list of sentences (pairs of word lists)
  private List<Sentence> getSentences (String enSentenceFile, String frSentenceFile) {

    List<Sentence> sentences = new ArrayList<>();

    try {

      BufferedReader enFile = new BufferedReader(new FileReader(enSentenceFile));
      BufferedReader frFile = new BufferedReader(new FileReader(frSentenceFile));

      String enLine = enFile.readLine();
      String frLine = frFile.readLine();
      while (enLine != null) {

        // Add in NULL word
        enLine = NULL_STRING + " " + enLine;

        String[] enWords = enLine.split(" ");
        String[] frWords = frLine.split(" ");
        Sentence s = new Sentence(enWords, frWords);
        sentences.add(s);

        enLine = enFile.readLine();
        frLine = frFile.readLine();
      }

    } catch (IOException e) {
      System.out.println("Error reading file: " + e);
    }

    return sentences;

  }

  // Given sentences and probabilities, executes an E-M iteration
  private void runIteration (List<Sentence> sentences, Map2D probs) {
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
