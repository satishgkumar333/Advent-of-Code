package nl.nibsi.aoc.exe;

import java.util.*;

import nl.nibsi.aoc.*;

import static nl.nibsi.aoc.AdventCalendar.*;

final class Main {

  public static void main(String... args) {
    AdventCalendar calendar = load();

    calendar.getPuzzleDates().stream()
      .map(calendar::getPuzzleForDate)
      .map(Optional::get)
      .forEachOrdered(System.out::println);
  }
}