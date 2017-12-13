package nl.nibsi.aoc.spi;

import java.time.*;
import java.util.*;

import nl.nibsi.aoc.*;

public interface PuzzleProvider {

  public Set<LocalDate> getPuzzleDates();

  public Optional<Puzzle> getPuzzleForDate(LocalDate date);

}