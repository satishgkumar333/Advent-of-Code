package nl.nibsi.aoc.provider;

import java.io.*;
import java.lang.reflect.*;
import java.time.*;
import java.util.*;

import nl.nibsi.aoc.*;
import nl.nibsi.aoc.spi.*;

import static java.time.format.DateTimeFormatter.*;

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;

public class DefaultPuzzleProvider implements PuzzleProvider {

  private final Map<LocalDate, Constructor<? extends Puzzle>> puzzleConstructors;

  public DefaultPuzzleProvider(Map<? extends LocalDate, Class<? extends Puzzle>> puzzleClasses) {
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

  public DefaultPuzzleProvider(Properties properties) {
    this(getPuzzlesFromProperties(properties));
  }

  public DefaultPuzzleProvider(Class<?> base, String propertiesFileName) {
    this(readPropertiesFile(base, propertiesFileName));
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
      throw new RuntimeException(ex);
    }
  }

  private static Map<LocalDate, Class<? extends Puzzle>> getPuzzlesFromProperties(Properties properties) {
    return properties.entrySet().stream()
      .map(DefaultPuzzleProvider::getStronglyTypedEntry)
      .collect(toMap(
        Map.Entry::getKey,
        Map.Entry::getValue
      ));
  }

  private static Map.Entry<LocalDate, Class<? extends Puzzle>> getStronglyTypedEntry(Map.Entry<?,?> entry) {
    try {
      LocalDate date = LocalDate.parse(entry.getKey().toString(), ISO_LOCAL_DATE);
      Class<?> puzzleClass = Class.forName(entry.getValue().toString());
      return new AbstractMap.SimpleImmutableEntry<>(date, puzzleClass.asSubclass(Puzzle.class));
    }

    catch (ClassNotFoundException ex) {
      throw new IllegalArgumentException(ex);
    }
  }

  private static Properties readPropertiesFile(Class<?> base, String propertiesFileName) {
    try (
      InputStream input = base.getResourceAsStream(propertiesFileName);
      Reader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
    ) {
      Properties properties = new Properties();
      properties.load(reader);
      return properties;
    }

    catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }
}