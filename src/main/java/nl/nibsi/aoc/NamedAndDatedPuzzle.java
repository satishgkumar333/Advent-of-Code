package nl.nibsi.aoc;

import java.io.*;
import java.time.*;
import java.util.*;

public final class NamedAndDatedPuzzle implements Puzzle {

  private final AdventCalendar calendar;
  private final LocalDate date;

  NamedAndDatedPuzzle(AdventCalendar calendar, LocalDate date) {
    if (calendar == null || date == null)
      throw new IllegalArgumentException();

    this.calendar = calendar;
    this.date = date;

    if (!this.calendar.getPuzzleDates().contains(this.date))
      throw new IllegalArgumentException();
  }

  public LocalDate getDate() {
    return date;
  }

  public String getName(Locale locale) {
    return calendar.getPuzzleName(date, locale);
  }

  public String getName() {
    return getName(Locale.getDefault());
  }

  private Puzzle actualPuzzle() {
    return calendar.getPuzzleForDate(date).get();
  }

  @Override
  public String solveFirstPart(Reader inputReader) throws IOException {
    return actualPuzzle().solveFirstPart(inputReader);
  }

  @Override
  public String solveSecondPart(Reader inputReader) throws IOException {
    return actualPuzzle().solveSecondPart(inputReader);
  }

  @Override
  public String toString() {
    return getName();
  }
}