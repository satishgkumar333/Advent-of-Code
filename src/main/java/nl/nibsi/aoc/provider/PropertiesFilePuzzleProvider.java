package nl.nibsi.aoc.provider;

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

import nl.nibsi.aoc.*;

import static java.time.format.DateTimeFormatter.*;

import static java.util.stream.Collectors.*;

public class PropertiesFilePuzzleProvider extends SimplePuzzleProvider {

  private static final String PUZZLE_PROPERTIES_FILE_NAME = "Puzzles.properties";

  public PropertiesFilePuzzleProvider() throws IOException {
    super(getPuzzlesFromProperties(readPropertiesFile(PUZZLE_PROPERTIES_FILE_NAME)));
  }
  
  private static Properties readPropertiesFile(String propertiesFileName) throws IOException {
    try (
      InputStream input = AdventCalendar.class.getResourceAsStream(propertiesFileName);
      Reader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
    ) {
      Properties properties = new Properties();
      properties.load(reader);
      return properties;
    }
  }

  private static Map<LocalDate, Class<? extends Puzzle>> getPuzzlesFromProperties(Properties properties) {
    return properties.entrySet().stream()
      .map(PropertiesFilePuzzleProvider::getStronglyTypedEntry)
      .filter(Optional::isPresent)
      .map(Optional::get)
      .collect(toMap(
        entry -> entry.getKey(),
        entry -> entry.getValue()
      ));
  }

  private static Optional<Map.Entry<LocalDate, Class<? extends Puzzle>>> getStronglyTypedEntry(Map.Entry<?,?> entry) {
    try {
      LocalDate date = LocalDate.parse(entry.getKey().toString(), ISO_LOCAL_DATE);
      Class<?> puzzleClass = Class.forName(entry.getValue().toString());
      if (Puzzle.class.isAssignableFrom(puzzleClass))
        return Optional.of(new AbstractMap.SimpleImmutableEntry<>(date, puzzleClass.asSubclass(Puzzle.class)));

      return Optional.empty();
    }

    catch (DateTimeParseException | ClassNotFoundException ex) {
      return Optional.empty();
    }
  }
}