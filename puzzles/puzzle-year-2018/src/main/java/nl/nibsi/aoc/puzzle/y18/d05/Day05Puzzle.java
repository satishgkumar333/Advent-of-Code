package nl.nibsi.aoc.puzzle.y18.d05;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import nl.nibsi.aoc.Puzzle;

import static java.util.stream.Collectors.toList;

public final class Day05Puzzle implements Puzzle {

  private static final class Polymer {

    private Deque<Integer> stackOfUnits = new ArrayDeque<>();

    private static boolean arePolarOpposites(int first, int second) {
      return first != second && (Character.toLowerCase(first) == second || Character.toUpperCase(first) == second);
    }

    private void addUnit(int unit) {
      if (!stackOfUnits.isEmpty() && arePolarOpposites(stackOfUnits.peek(), unit))
        stackOfUnits.pop();
      else
        stackOfUnits.push(unit);
    }

    private int length() {
      return stackOfUnits.size();
    }
  }

  @Override
  public String solveFirstPart(BufferedReader inputReader) throws IOException {
    Polymer polymer = new Polymer();
    inputReader.readLine().chars().forEachOrdered(unit -> polymer.addUnit(unit));
    return Integer.toString(polymer.length());
  }

  @Override
  public String solveSecondPart(BufferedReader inputReader) throws IOException {
    String line = inputReader.readLine();
    List<Integer> types = line.chars().map(Character::toLowerCase).boxed().distinct().collect(toList());

    int shortest = Integer.MAX_VALUE;
    for (int type: types) {
      String attempt = line.replaceAll(Pattern.quote(Character.toString((char) type)), "");
      attempt = attempt.replaceAll(Pattern.quote(Character.toString((char) Character.toUpperCase(type))), "");

      Polymer polymer = new Polymer();
      attempt.chars().forEachOrdered(unit -> polymer.addUnit(unit));
      shortest = Integer.min(shortest, polymer.length());
    }

    return Integer.toString(shortest);
  }
}