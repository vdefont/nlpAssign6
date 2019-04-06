import java.util.*;

public class DiscountLMModel extends BaseLMModel {

  private double discount;
  private double totalCount;
  private HashMap<String, Double> alpha;

  public DiscountLMModel(String filename, double discount) {
    super(filename);
    this.discount = discount;

    // Calculate totalCount
    totalCount = 0;
    for (String word : counts.keySet()) {
      totalCount += counts.get(word);
    }

    buildAlpha();
  }

  private void unitTests() {
    // Check counts and bigrams (file processing)
    System.out.println(counts.toString());
    System.out.println(bigrams.toString());

    // Check alpha counts
    System.out.println(alpha.toString());

    // Check bigram probs
    System.out.println(getBigramProb(LINE_START, "a"));
    System.out.println(getBigramProb("a", "b"));
    System.out.println(getBigramProb("b", LINE_END));
  }

  private void buildAlpha() {

    // Loop through all words in vocab
    alpha = new HashMap<>();
    for (String word : bigrams.keySet()) {

      // Calculate reserved mass
      double reservedMass = discount * bigrams.get(word).size() / counts.get(word);

      // Calculate backoff mass
      double seenCount = 0;
      for (String seenWord : bigrams.get(word).keySet()) {
        seenCount += counts.get(seenWord);
      }
      double backoffMass = 1.0 - (seenCount / totalCount);

      // Calculate alpha
      double curAlpha = reservedMass / backoffMass;
      alpha.put(word, curAlpha);
    }
  }

	/**
	 * Returns p(second | first)
	 *
	 * @param first
	 * @param second
	 * @return the probability of the second word given the first word (as a probability)
	 */
	public double getBigramProb(String first, String second) {

    // If unseen, replace with UNK
    if (!counts.containsKey(first)) first = UNKNOWN;
    if (!counts.containsKey(second)) second = UNKNOWN;

    // Case 1 : Known bigram
    if (bigrams.get(first).containsKey(second)) {
      double bigramCount = bigrams.get(first).get(second);
      double firstCount = counts.get(first);
      return (bigramCount - discount) / firstCount;
    }

    // Case 2: Unknown bigram
    double alphaFirst = alpha.get(first);
    double probSecond = counts.get(second) / totalCount;
    return alphaFirst * probSecond;
  }

}
