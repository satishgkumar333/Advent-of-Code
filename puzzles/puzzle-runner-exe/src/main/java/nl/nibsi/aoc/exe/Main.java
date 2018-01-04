package nl.nibsi.aoc.exe;

import javax.swing.*;

import nl.nibsi.aoc.AdventCalendar;
import nl.nibsi.aoc.swing.PuzzleRunnerForm;

final class Main {

  public static void main(String... args) {
    SwingUtilities.invokeLater(() -> {
      new PuzzleRunnerForm(AdventCalendar.load()).show();
    });
  }
}