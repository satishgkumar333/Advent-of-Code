package nl.nibsi.aoc.provider;

import java.time.*;
import java.util.*;

import nl.nibsi.aoc.*;
import nl.nibsi.aoc.spi.*;

public class QuickPuzzleProvider implements PuzzleProvider, PuzzleNameProvider {

  private final PuzzleProvider puzzleProvider;
  private final PuzzleNameProvider puzzleNameProvider;
  
  public QuickPuzzleProvider(Class<?> base) {
    this.puzzleProvider = new DefaultPuzzleProvider(base, "Puzzles.properties");
    this.puzzleNameProvider = new DefaultPuzzleNameProvider(base.getPackageName() + ".PuzzleNames");
  }

  @Override
  public Set<LocalDate> getPuzzleDates() {
    return puzzleProvider.getPuzzleDates();
  }

  @Override
  public Optional<Puzzle> getPuzzleForDate(LocalDate date) {
    return puzzleProvider.getPuzzleForDate(date);
  }

  @Override
  public Optional<String> getPuzzleName(LocalDate date, Locale locale) {
    return puzzleNameProvider.getPuzzleName(date, locale);
  }
}