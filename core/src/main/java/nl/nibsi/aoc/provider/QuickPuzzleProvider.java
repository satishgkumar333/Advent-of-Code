package nl.nibsi.aoc.provider;

import java.time.*;
import java.util.*;

import nl.nibsi.aoc.Puzzle;
import nl.nibsi.aoc.spi.PuzzleNameProvider;
import nl.nibsi.aoc.spi.PuzzleProvider;

public class QuickPuzzleProvider implements PuzzleProvider, PuzzleNameProvider {

  private final PuzzleProvider puzzleProvider;
  private final PuzzleNameProvider puzzleNameProvider;
  
  public QuickPuzzleProvider() {
    Class<? extends QuickPuzzleProvider> providerClass = getClass();
    this.puzzleProvider = new DefaultPuzzleProvider(providerClass, "Puzzles.properties");
    this.puzzleNameProvider = new DefaultPuzzleNameProvider(providerClass.getPackage().getName() + ".PuzzleNames");
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