package nl.nibsi.aoc.y17.d10;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import static java.util.stream.Collectors.*;
import static java.util.stream.Stream.*;

final class Problem {

  private final Knot knot;

  private int currentMark, stepSize;

  Problem(int numberOfMarks) {
    knot = new Knot(numberOfMarks);
  }

  void twistKnot(int length) {
    if (length < 0)
      throw new IllegalArgumentException();

    int numberOfMarks = knot.numberOfMarks();
    int endMark = (currentMark + length) % numberOfMarks;

    if (length > 0)
    	knot.pinchAndTwist(currentMark, endMark);

    currentMark = (endMark + stepSize) % numberOfMarks;
    stepSize++;
  }

  int getCheckSum() {
    return knot.marks().subList(0,2).stream().mapToInt(Integer::intValue).reduce(1, (a,b) -> a*b);
  }

  List<Integer> getSparseHash() {
    return knot.marks();
  }

  List<Integer> getDenseHash() {
    List<Integer> denseHash = new ArrayList<>();
    for (
      Iterator<Integer> it = getSparseHash().iterator();
      it.hasNext();
    ) {
      int hash = 0;
      for (int i = 0; i < 16 && it.hasNext(); i++)
        hash ^= it.next();

      denseHash.add(hash);
    }

    return denseHash;
  }

  String getDenseHashAsHexadecimalString() {
    return getDenseHash().stream().map(b -> String.format("%02x", b)).collect(joining(""));
  }

  @Override
  public String toString() {
    List<Integer> marks = knot.marks();
    return concat(concat(
      marks.subList(0, currentMark).stream().map(Object::toString),
      Stream.of("[" + marks.get(currentMark) + "]")),
      marks.subList(currentMark +1, marks.size()).stream().map(Object::toString)
    ).collect(joining(" "));
  }

  private static List<Integer> readLengthsFromInput() {
    try (
      Scanner scanner = new Scanner(new InputStreamReader(Problem.class.getResourceAsStream("input.txt")));
    ) {
      scanner.useDelimiter("\\s*,\\s*");
      List<Integer> lengths = new ArrayList<>();
      scanner.forEachRemaining(length -> lengths.add(Integer.parseInt(length)));
      return lengths;
    }
  }

  private static List<Integer> readInputAsAscii() {
    try (
      BufferedReader reader = new BufferedReader(new InputStreamReader(Problem.class.getResourceAsStream("input.txt")));
    ) {
      String line = reader.readLine();
      line = line == null ? "" : line.trim();
      return line.codePoints().boxed().collect(toList());
    }

    catch (IOException ex) {
      throw new AssertionError(ex);
    }
  }

  public static void main(String... args) {
    Problem problem = new Problem(256);
    List<Integer> asciiValues = readInputAsAscii();
    asciiValues.addAll(Arrays.asList(17, 31, 73, 47, 23));
    for (int i = 0; i < 64; i++)
      asciiValues.stream().forEachOrdered(problem::twistKnot);

    System.out.println(problem.getDenseHashAsHexadecimalString());
  }
}