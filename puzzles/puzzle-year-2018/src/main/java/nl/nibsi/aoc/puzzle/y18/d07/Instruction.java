package nl.nibsi.aoc.puzzle.y18.d07;

import java.util.regex.*;

final class Instruction {

  private static final Pattern PATTERN = Pattern.compile(
    "^Step (?<first>\\p{Upper}) must be finished before step (?<second>\\p{Upper}) can begin.$"
  );

  private final char first, second;

  Instruction(char first, char second) {
    this.first  = first;
    this.second = second;
  }

  char first() {
    return first;
  }

  char second() {
    return second;
  }

  @Override
  public String toString() {
    return String.format("%s -> %s", first, second);
  }

  static Instruction parse(String line) {
    Matcher matcher = PATTERN.matcher(line);
    if (!matcher.matches())
      throw new IllegalArgumentException();

    return new Instruction(
      matcher.group("first").charAt(0),
      matcher.group("second").charAt(0)
    );
  }
}