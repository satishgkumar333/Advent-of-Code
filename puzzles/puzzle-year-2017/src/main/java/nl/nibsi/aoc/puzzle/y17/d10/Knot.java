package nl.nibsi.aoc.puzzle.y17.d10;

import java.util.*;
import java.util.stream.*;

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;

final class Knot {

  private final List<Integer> marks;

  Knot(int numberOfMarks) {
    if (numberOfMarks < 0)
      throw new IllegalArgumentException();

    marks = IntStream.range(0, numberOfMarks).boxed().collect(toList());
  }

  int numberOfMarks() {
    return marks.size();
  }

  void pinchAndTwist(int start, int end) {
    int numberOfMarks = numberOfMarks();

    if (start < 0 || start >= numberOfMarks || end < 0 || end >= numberOfMarks)
      throw new IndexOutOfBoundsException();

    if (start < end)
      reverse(marks.subList(start, end));

    else {
      List<Integer> reversed = new ArrayList<>(marks.subList(start, numberOfMarks));
      reversed.addAll(marks.subList(0, end));
      reverse(reversed);

      Iterator<Integer> reversedIt = reversed.iterator();

      for (
        ListIterator<Integer> it = marks.listIterator(start);
        it.hasNext();
      ) {
        it.next();
        it.set(reversedIt.next());
      }

      for (
        ListIterator<Integer> it = marks.listIterator();
        reversedIt.hasNext();
      ) {
        it.next();
        it.set(reversedIt.next());
      }
    }
  }

  List<Integer> marks() {
    return unmodifiableList(marks);
  }
}