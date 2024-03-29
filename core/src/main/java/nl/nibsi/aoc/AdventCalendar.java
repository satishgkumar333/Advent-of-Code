package nl.nibsi.aoc;

import java.time.*;
import java.time.format.*;
import java.util.*;

import nl.nibsi.aoc.spi.PuzzleNameProvider;
import nl.nibsi.aoc.spi.PuzzleProvider;

import static java.time.format.FormatStyle.LONG;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableSet;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public final class AdventCalendar {

  private final Map<LocalDate, Puzzle> puzzles;
  private final List<PuzzleNameProvider> puzzleNameProviders;

  private AdventCalendar(
    Map<? extends LocalDate, ? extends Puzzle> puzzles,
    Collection<? extends PuzzleNameProvider> puzzleNameProviders
  ) {
    if (puzzles == null || puzzleNameProviders == null)
      throw new IllegalArgumentException();

    try {
      if (puzzles.containsKey(null))
        throw new IllegalArgumentException();
    } catch (NullPointerException ex) {
      // The map can't contain null keys, so we're good.
    }

    this.puzzles = new TreeMap<>(puzzles);
    this.puzzleNameProviders = new ArrayList<>(puzzleNameProviders);

    if (this.puzzles.containsValue(null) || this.puzzleNameProviders.contains(null))
      throw new IllegalArgumentException();
  }

  public AdventCalendar(Map<? extends LocalDate, ? extends Puzzle> puzzles, PuzzleNameProvider puzzleNameProvider) {
    this(puzzles, singleton(puzzleNameProvider));
  }

  public AdventCalendar(Map<? extends LocalDate, ? extends Puzzle> puzzles) {
    this(puzzles, emptySet());
  }

  public AdventCalendar() {
    this(emptyMap(), emptySet());
  }

  public static AdventCalendar load() {
    Map<LocalDate, Puzzle> puzzles = new TreeMap<>();
    for (PuzzleProvider provider: ServiceLoader.load(PuzzleProvider.class))
      for (LocalDate date: provider.getPuzzleDates())
        provider.getPuzzleForDate(date).ifPresent(puzzle -> puzzles.putIfAbsent(date, puzzle));

    List<PuzzleNameProvider> puzzleNameProviders = new ArrayList<>();
    for (PuzzleNameProvider provider: ServiceLoader.load(PuzzleNameProvider.class))
      puzzleNameProviders.add(provider);

    return new AdventCalendar(puzzles, puzzleNameProviders);
  }

  public Set<LocalDate> getPuzzleDates() {
    return unmodifiableSet(puzzles.keySet());
  }

  public Optional<NamedAndDatedPuzzle> getPuzzleForDate(LocalDate date) {
    return puzzles.containsKey(date)
      ? Optional.of(new NamedAndDatedPuzzle(this, date))
      : Optional.empty();
  }

  String getPuzzleName(LocalDate date, Locale locale) {
    return puzzleNameProviders.stream()
      .map(provider -> provider.getPuzzleName(date, locale))
      .filter(Optional::isPresent)
      .map(Optional::get)
      .findFirst()
      .orElseGet(() -> date.format(DateTimeFormatter.ofLocalizedDate(LONG).withLocale(locale)));
  }
}