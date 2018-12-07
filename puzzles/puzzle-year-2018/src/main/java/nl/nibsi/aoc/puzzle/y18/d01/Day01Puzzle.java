package nl.nibsi.aoc.puzzle.y18.d01;

import java.io.*;
import java.util.*;

import nl.nibsi.aoc.Puzzle;

import static java.util.stream.Collectors.toList;

public final class Day01Puzzle implements Puzzle {

  @Override
  public String solveFirstPart(BufferedReader inputReader) throws IOException {
    return Integer.toString(inputReader.lines().mapToInt(Integer::parseInt).sum());
  }

  @Override
  public String solveSecondPart(BufferedReader inputReader) throws IOException {
    List<Integer> shifts = inputReader.lines().map(Integer::parseInt).collect(toList());

    Set<Integer> frequencies = new HashSet<>();
    int frequency = 0;
    boolean found = false;

    while (!found) {
      for (int shift: shifts) {
        frequency += shift;
        if (frequencies.contains(frequency)) {
          found = true;
          break;
        }

        frequencies.add(frequency);
      }
    }

    return Integer.toString(frequency);
  }
}