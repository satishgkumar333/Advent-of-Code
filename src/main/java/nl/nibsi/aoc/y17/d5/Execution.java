package nl.nibsi.aoc.y17.d5;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

import static java.util.stream.Collectors.*;
import static java.util.stream.Stream.*;

final class Execution {

  static Execution read() {
    try (
      BufferedReader reader = new BufferedReader(new InputStreamReader(Execution.class.getResourceAsStream("input.txt")));
    ) {
      return new Execution(reader.lines().map(Integer::parseInt).collect(toList()));
    }

    catch (IOException ex) {
      throw new AssertionError(ex);
    }
  }

  private final List<AtomicInteger> offsets;
  private int programCounter;

  Execution(List<Integer> offsets) {
    this.offsets = offsets.stream().map(AtomicInteger::new).collect(toList());
  }

  boolean executeCurrentInstruction() {
    AtomicInteger offset = offsets.get(programCounter);
    programCounter += offset.getAndUpdate(off -> off >= 3 ? off -1 : off +1);
    return programCounter >= 0 && programCounter < offsets.size();
  }

  @Override
  public String toString() {
    return concat(concat(
      offsets.subList(0, programCounter).stream().map(Object::toString),
      Stream.of("(" + offsets.get(programCounter) + ")")),
      offsets.subList(programCounter +1, offsets.size()).stream().map(Object::toString)
    ).collect(joining(", "));
  }

  public static void main(String... args) {
    Execution execution = Execution.read();
    int steps = 1;
    while (execution.executeCurrentInstruction())
      steps++;

    System.out.println(steps);
  }
}