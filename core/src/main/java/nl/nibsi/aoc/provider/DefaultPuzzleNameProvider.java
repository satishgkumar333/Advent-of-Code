package nl.nibsi.aoc.provider;

import java.time.*;
import java.util.*;

import nl.nibsi.aoc.spi.*;

import static java.time.format.DateTimeFormatter.*;

import static java.util.ResourceBundle.*;

public class DefaultPuzzleNameProvider implements PuzzleNameProvider {

  private final String resourceBundleName;

  public DefaultPuzzleNameProvider(String resourceBundleName) {
    if (resourceBundleName == null)
      throw new IllegalArgumentException();

    this.resourceBundleName = resourceBundleName;
  }

  @Override
  public Optional<String> getPuzzleName(LocalDate date, Locale locale) {
    try {
      return Optional.of(getBundle(resourceBundleName, locale).getString(date.format(ISO_LOCAL_DATE)));
    }

    catch (MissingResourceException ex) {
      return Optional.empty();
    }
  }
}