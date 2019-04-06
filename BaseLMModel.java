import java.util.*;
import java.io.*;

public abstract class BaseLMModel implements LMModel {

  // Useful constants
  protected static String LINE_START = "<s>", LINE_END = "</s>", UNKNOWN = "<UNK>";

  protected HashMap<String, HashMap<String, Integer>> bigrams;
  protected HashMap<String, Integer> counts;

  public BaseLMModel(String filename) {
    // Set up hash maps
    bigrams = new HashMap<>();
    counts = new HashMap<>();

    // Read file and populate hash maps
    try {
      processFile(filename);
    } catch(IOException e){
      System.out.println("Error reading file: " + e);
    }
  }

  // Reads all lines and populates HashMaps
  protected void processFile(String filename) throws IOException {

    BufferedReader in = new BufferedReader(new FileReader(filename));

    HashSet<String> wordsSeen = new HashSet<>();

    // Read each line
    String line = in.readLine();
    int numLines = 0;
    while (line != null) {

      // Read all words
      String prevWord = LINE_START;
      String[] words = line.split(" ");
      for (int i = 0; i <= words.length; i += 1) {

        // Check if last word in line
        boolean isLastWord = (i == words.length);
        String curWord = isLastWord ? LINE_END : words[i];

        // Update count
        if (!isLastWord) {
          // If not seen, mark as seen and replace with "UNK"
          if (!wordsSeen.contains(curWord)) {
            wordsSeen.add(curWord);
            curWord = UNKNOWN;
          }

          // Increment count
          int curCount = counts.containsKey(curWord) ? counts.get(curWord) : 0;
          counts.put(curWord, curCount + 1);
        }

        // Ensure bigram key exists
        if (!bigrams.containsKey(prevWord)) {
          bigrams.put(prevWord, new HashMap<String, Integer>());
        }
        // Ensure count HashMap exists for key
        if (!bigrams.get(prevWord).containsKey(curWord)) {
          bigrams.get(prevWord).put(curWord, 0);
        }
        // Add bigram
        int prevCount = bigrams.get(prevWord).get(curWord);
        bigrams.get(prevWord).put(curWord, prevCount + 1);

        prevWord = curWord;
      }

      line = in.readLine();
      numLines += 1;
    }

    counts.put(LINE_START, numLines);
    counts.put(LINE_END, numLines);
  }

  /**
   * Given a sentence, return the log of the probability of the sentence based on the LM.
   *
   * @param sentWords the words in the sentence.  sentWords should NOT contain <s> or </s>.
   * @return the log probability
   */
  public double logProb(ArrayList<String> sentWords){

    double logSum = 0;

    // Add end-of-line word
    sentWords.add(LINE_END);

    // Make sure the first pair includes line start
    String lastWord = LINE_START;
    for (String curWord : sentWords) {
      // Calculate probability
      double prob = getBigramProb(lastWord, curWord);
      double logProb = Math.log(prob) / Math.log(10);
      logSum += logProb;

      // Prepare for next iteration of loop
      lastWord = curWord;
    }

    return logSum;
  }

  /**
   * Given a text file, calculate the perplexity of the text file, that is the negative average per word log
   * probability
   *
   * @param filename a text file.  The file will contain sentences WITHOUT <s> or </s>.
   * @return the perplexity of the text in file based on the LM
   */
  public double getPerplexity(String filename){

    try {
      BufferedReader in = new BufferedReader(new FileReader(filename));

      double logSum = 0;

      // Read each line
      String line = in.readLine();
      double numWords = 0.0;
      while (line != null) {
        // Calculate the log sum of sentence probability
        ArrayList<String> sentWords = new ArrayList<>(Arrays.asList(line.split(" ")));
        logSum += logProb(sentWords);
        // Track the number of words
        numWords += sentWords.size() + 1;
        line = in.readLine();
      }

      double perplexity = Math.pow(10.0, (-1.0/numWords) * logSum);
      return perplexity;
    } catch(IOException e) {
      System.out.println("Error reading file: " + e);
      return 0;
    }
  }

}
