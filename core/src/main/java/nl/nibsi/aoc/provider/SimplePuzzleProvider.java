package nl.nibsi.aoc.provider;

import java.lang.reflect.*;
import java.time.*;
import java.util.*;

import nl.nibsi.aoc.*;
import nl.nibsi.aoc.spi.*;

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;

public class SimplePuzzleProvider implements PuzzleProvider {

  private final Map<LocalDate, Constructor<? extends Puzzle>> puzzleConstructors;

  public SimplePuzzleProvider(Map<? extends LocalDate, Class<? extends Puzzle>> puzzleClasses) {
    this.puzzleConstructors = unmodifiableMap(puzzleClasses.entrySet().stream().collect(toMap(
      Map.Entry::getKey,
      entry -> {
        try {
          LocalDate date = entry.getKey();
          Class<? extends Puzzle> puzzleClass = entry.getValue();
          if (date == null || puzzleClass == null)
            throw new IllegalArgumentException();
          
          return entry.getValue().getConstructor();
        }
        catch (NoSuchMethodException ex) {
          throw new IllegalArgumentException(ex);
        }
      }
    )));
  }

  @Override
  public Set<LocalDate> getPuzzleDates() {
    return puzzleConstructors.keySet();
  }

  @Override
  public Optional<Puzzle> getPuzzleForDate(LocalDate date) {
    Constructor<? extends Puzzle> constructor = puzzleConstructors.get(date);
    if (constructor == null)
      return Optional.empty();

    try {
      return Optional.of(constructor.newInstance());
    }
    catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
      return Optional.empty();
    }
  }
}