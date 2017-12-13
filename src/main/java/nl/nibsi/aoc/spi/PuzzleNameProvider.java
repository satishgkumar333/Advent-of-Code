package nl.nibsi.aoc.spi;

import java.time.*;
import java.util.*;

public interface PuzzleNameProvider {

  public Optional<String> getPuzzleName(LocalDate date, Locale locale);

}