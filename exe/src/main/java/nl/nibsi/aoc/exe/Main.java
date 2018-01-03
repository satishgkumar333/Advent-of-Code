package nl.nibsi.aoc.exe;

import javax.swing.*;

import nl.nibsi.aoc.*;
import nl.nibsi.aoc.swing.*;

final class Main {

  public static void main(String... args) {
    SwingUtilities.invokeLater(() -> {
      new PuzzleRunnerForm(AdventCalendar.load()).show();
    });
  }
}