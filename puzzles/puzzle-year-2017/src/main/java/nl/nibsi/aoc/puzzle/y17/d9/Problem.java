package nl.nibsi.aoc.puzzle.y17.d9;

import java.io.*;

import static java.util.stream.Collectors.*;

final class Problem {

  private Problem() {}

  private static Reader getInputAsReader() {
    return new BufferedReader(new InputStreamReader(Problem.class.getResourceAsStream("input.txt")));
  }

  public static void main(String... args) throws Exception {
    try (
      Reader reader = getInputAsReader();
    ) {
      StreamProcessor processor = new StreamProcessor(reader);
      while (!processor.isRootScopeClosed())
        processor.processNextChar();

      Scope root = processor.currentScope();
      System.out.println(root.score());
      System.out.println(processor.garbageRemovedCount());
    }
  }
}