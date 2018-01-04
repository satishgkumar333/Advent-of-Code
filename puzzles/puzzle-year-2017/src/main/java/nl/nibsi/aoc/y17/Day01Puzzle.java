package nl.nibsi.aoc.y17;

import java.io.*;

import nl.nibsi.aoc.Puzzle;

public final class Day01Puzzle implements Puzzle {

  @Override
  public String solveFirstPart(BufferedReader inputReader) throws IOException {
    String line = inputReader.readLine();
    String shifted = new StringBuilder(line).deleteCharAt(0).append(line.charAt(0)).toString();

    return Integer.toString(getSumOfMatchingChars(line, shifted));
  }

  @Override
  public String solveSecondPart(BufferedReader inputReader) throws IOException {
    String line = inputReader.readLine();
    String shifted = line.substring(line.length() / 2) + line.substring(0, line.length() / 2);

    return Integer.toString(getSumOfMatchingChars(line, shifted));
  }

  private static int getSumOfMatchingChars(String first, String second) {
    int sum = 0;
    for (int i = 0; i < first.length(); i++) {
      char c = first.charAt(i);
      if (c == second.charAt(i))
        sum += Character.digit(c, 10);
    }

    return sum;
  }
}