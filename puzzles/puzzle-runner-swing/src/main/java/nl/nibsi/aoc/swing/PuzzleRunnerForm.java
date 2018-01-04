package nl.nibsi.aoc.swing;

import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.util.*;

import javax.swing.*;

import nl.nibsi.aoc.AdventCalendar;
import nl.nibsi.aoc.NamedAndDatedPuzzle;

import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

public final class PuzzleRunnerForm {

  private final AdventCalendar calendar;

  private final JFrame frame = new JFrame("Advent of Code");
  
  private final JComboBox<Year> yearPicker;
  private final JComboBox<NamedAndDatedPuzzle> puzzlePicker;

  public PuzzleRunnerForm(AdventCalendar calendar) {
    if (calendar == null)
      throw new IllegalArgumentException();

    this.calendar = calendar;
    
    this.yearPicker = new JComboBox<>(
      calendar.getPuzzleDates().stream()
        .map(Year::from)
        .distinct()
        .sorted()
        .toArray(Year[]::new)
    );
    
    this.puzzlePicker = new JComboBox<>();

    initComponents();
    initListeners();
  }

  public void show() {
    updatePuzzlePicker();
    frame.pack();
    frame.setVisible(true);
  }

  public void close() {
    frame.dispose();
  }
  
  public Optional<Year> selectedYear() {
    return Optional.ofNullable((Year) yearPicker.getSelectedItem());
  }
  
  public Optional<NamedAndDatedPuzzle> selectedPuzzle() {
    return Optional.ofNullable((NamedAndDatedPuzzle) puzzlePicker.getSelectedItem());
  }

  private void updatePuzzlePicker() {
    puzzlePicker.removeAllItems();
    selectedYear().ifPresent(selectedYear ->
      calendar.getPuzzleDates().stream()
        .filter(date -> Year.from(date).equals(selectedYear))
        .sorted()
        .map(calendar::getPuzzleForDate)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEachOrdered(puzzlePicker::addItem)
    );
  }

  private void updatePuzzle() {
    System.out.println(selectedPuzzle());
  }

  private void initComponents() {
    puzzlePicker.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel component = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        NamedAndDatedPuzzle puzzle = (NamedAndDatedPuzzle) value;
        if (puzzle != null)
          component.setText(puzzle.getName());

        return component;
      }
    });
    
    frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

    frame.setLayout(new GridBagLayout());
    GridBagConstraints constraints = new GridBagConstraints();

    frame.add(yearPicker, constraints);
    frame.add(puzzlePicker, constraints);
  }

  private void initListeners() {
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent event) {
        close();
      }
    });

      yearPicker.addItemListener(event -> updatePuzzlePicker());
    puzzlePicker.addItemListener(event -> updatePuzzle());
  }
}