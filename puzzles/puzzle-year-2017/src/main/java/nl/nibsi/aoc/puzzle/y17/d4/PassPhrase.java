package nl.nibsi.aoc.puzzle.y17.d4;

import java.io.*;
import java.util.*;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;

final class PassPhrase {

  private PassPhrase() {}
  
  static List<String> readAll() {
    try (
      BufferedReader reader = new BufferedReader(new InputStreamReader(PassPhrase.class.getResourceAsStream("input.txt")));
    ) {
      return reader.lines().collect(toList());
    }

    catch (IOException ex) {
      throw new AssertionError(ex);
    }
  }
  
  static boolean containsNoDuplicates(String phrase) {
    String[] words = phrase.split("\\s+");

    for (int i = 0; i < words.length -1; i++)
      for (int j = i+1; j < words.length; j++)
        if (words[i].equals(words[j]))
          return false;

    return true;
  }
  
  static boolean containsNoAnagrams(String phrase) {
    String[] words = phrase.split("\\s+");

    for (int i = 0; i < words.length -1; i++)
      for (int j = i+1; j < words.length; j++)
        if (areAnagrams(words[i], words[j]))
          return false;

    return true;
  }

  static boolean areAnagrams(String first, String second) {
    if (first.length() != second.length())
      return false;

    char[] firstChars = first.toCharArray();
    char[] secondChars = second.toCharArray();

    sort(firstChars);
    sort(secondChars);

    return Arrays.equals(firstChars, secondChars);
  }
}