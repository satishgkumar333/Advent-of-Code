package nl.nibsi.aoc.puzzle.y17.d7;

import java.io.*;
import java.util.*;

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;

final class Program {

  static List<Program> readAll() {
    try (
      BufferedReader reader = new BufferedReader(new InputStreamReader(Program.class.getResourceAsStream("input.txt")));
    ) {
      return reader.lines().map(Program::parse).collect(toList());
    }

    catch (IOException ex) {
      throw new AssertionError(ex);
    }
  }

  static TreeNode<String, Program> readAllAsTree() {
    return TreeNode.buildTree(
      Program.readAll(),
      Program::name,
      program -> program.subProgramNames().stream(),
      program -> program
    );
  }

  private final String name;
  private final int weight;
  private final Set<String> subProgramNames;

  Program(String name, int weight, Collection<String> subProgramNames) {
    if (name == null || subProgramNames == null)
      throw new IllegalArgumentException();

    this.name = name.trim();
    this.weight = weight;
    this.subProgramNames = unmodifiableSet(new LinkedHashSet<>(subProgramNames));

    if (this.name.isEmpty() || !this.name.equals(name))
      throw new IllegalArgumentException();

    if (this.subProgramNames.contains(null))
      throw new IllegalArgumentException();
  }

  String name() {
    return name;
  }

  int weight() {
    return weight;
  }

  Set<String> subProgramNames() {
    return subProgramNames;
  }

  @Override
  public String toString() {
    return String.format("%s (%d)%s", name, weight, subProgramNames.isEmpty()
      ? ""
      : " -> " + String.join(", ", subProgramNames)
    );
  }

  static Program parse(String line) {
    Scanner scanner = new Scanner(line);

    String name = scanner.next("[a-z]+");
    StringBuilder weightBuilder = new StringBuilder(scanner.next("[(]\\d+[)]"));

    int weight = Integer.parseInt(weightBuilder.deleteCharAt(0).deleteCharAt(weightBuilder.length() -1).toString());
    List<String> subProgramNames = new ArrayList<>();

    if (scanner.hasNext("->")) {
      scanner.next();
      scanner.useDelimiter("[\\s,]+");
      while (scanner.hasNext("[a-z]+"))
        subProgramNames.add(scanner.next());
    }

    return new Program(name, weight, subProgramNames);
  }
}