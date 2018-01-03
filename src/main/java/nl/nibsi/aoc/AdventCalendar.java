package nl.nibsi.aoc;

import java.time.*;
import java.time.format.*;
import java.util.*;

import nl.nibsi.aoc.spi.*;

import static java.time.format.DateTimeFormatter.*;
import static java.time.format.FormatStyle.*;

import static java.util.Collections.*;
import static java.util.ResourceBundle.*;
import static java.util.stream.Collectors.*;

public final class AdventCalendar {

  private static final String PUZZLE_NAME_BUNDLE_NAME = "nl.nibsi.aoc.PuzzleNames";

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

    this.puzzleNameProviders.add((date, locale) -> {
      try {
        return Optional.of(getBundle(PUZZLE_NAME_BUNDLE_NAME, locale).getString(date.format(ISO_LOCAL_DATE)));
      }

      catch (MissingResourceException ex) {
        return Optional.of(date.format(DateTimeFormatter.ofLocalizedDate(LONG).withLocale(locale)));
      }
    });
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
    Map<LocalDate, Puzzle> puzzles = ServiceLoader.load(PuzzleProvider.class).stream()
      .map(ServiceLoader.Provider::get)
      .flatMap(provider -> provider.getPuzzleDates().stream()
        .map(date -> new AbstractMap.SimpleImmutableEntry<>(date, provider.getPuzzleForDate(date)))
        .filter(entry -> entry.getValue().isPresent()))
      .collect(toMap(
        (entry) -> entry.getKey(),
        (entry) -> entry.getValue().get(),
        (first, second) -> first
      ));

    List<PuzzleNameProvider> puzzleNameProviders = ServiceLoader.load(PuzzleNameProvider.class).stream()
      .map(ServiceLoader.Provider::get)
      .collect(toList());

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
      .orElseGet(date::toString);
  }
}