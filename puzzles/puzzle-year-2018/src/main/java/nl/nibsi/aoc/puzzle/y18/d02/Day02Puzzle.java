package nl.nibsi.aoc.puzzle.y18.d02;

import java.io.*;
import java.util.*;

import nl.nibsi.aoc.Puzzle;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public final class Day02Puzzle implements Puzzle {

  private static final class CharacterCounter {

    private final Map<Integer, Long> counts;

    private CharacterCounter(String line) {
      counts = line.chars().boxed().collect(groupingBy(Integer::valueOf, counting()));
    }

    private Set<Long> counts() {
      return new HashSet<>(counts.values());
    }
  }

  private static int difference(String first, String second) {
    int length = Integer.min(first.length(), second.length());

    int difference = 0;
    for (int i = 0; i < length; i++) {
      if (first.charAt(i) != second.charAt(i))
        difference++;
    }

    return difference;
  }

  private static int getIndexOfDifferingChar(String first, String second) {
    int length = Integer.min(first.length(), second.length());

    for (int i = 0; i < length; i++) {
      if (first.charAt(i) != second.charAt(i))
        return i;
    }

    return -1;
  }

  @Override
  public String solveFirstPart(BufferedReader inputReader) throws IOException {
    Map<Long, Long> countCounts = inputReader.lines()
      .map(CharacterCounter::new)
      .map(CharacterCounter::counts)
      .flatMap(Set::stream)
      .collect(groupingBy(Long::valueOf, counting()));

    long checksum = countCounts.get(2L) * countCounts.get(3L);

    return Long.toString(checksum);
  }

  @Override
  public String solveSecondPart(BufferedReader inputReader) throws IOException {
    List<String> lines = inputReader.lines().collect(toList());

    for (int i = 0; i < lines.size(); i++) {
      for (int j = i; j < lines.size(); j++) {
        String first = lines.get(i);
        String second = lines.get(j);
        if (difference(first, second) == 1) {
          int index = getIndexOfDifferingChar(first, second);
          return first.substring(0, index) + first.substring(index + 1, first.length());
        }
      }
    }

    return null;
  }
}