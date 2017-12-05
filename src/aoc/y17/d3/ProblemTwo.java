package aoc.y17.d3;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static aoc.y17.d3.Rotation.*;

final class ProblemTwo {

  private final int input;
  
  private int lastComputation = 1;

  private final Turtle<Integer> turtle = new Turtle<>(Direction.RIGHT, lastComputation)
    .setComputationPerCell(ProblemTwo::getSumOfAdjacentElements)
    .addComputationListener(event -> updateLastComputation(event.newValue()));

  ProblemTwo(int input) {
    this.input = input;
  }

  private void updateLastComputation(int computation) {
    if (lastComputation < input)
      lastComputation = computation;
  }

  int solve() {
    while (lastComputation < input) {
      turtle
        .moveForward()
        .rotate(COUNTER_CLOCKWISE)
        .moveForward()
        .rotate(COUNTER_CLOCKWISE)
        .incrementSpeed();
    }

    return lastComputation;
  }

  private static int getSumOfAdjacentElements(Turtle<Integer> turtle) {
    return getElementsAdjacentTo(turtle.x(), turtle.y(), turtle.matrix()).mapToInt(Integer::intValue).sum();
  }

  private static <T> Stream<T> getElementsAdjacentTo(int x, int y, TwoWayMatrix<? extends T> matrix) {
    return IntStream.rangeClosed(-1,1)
      .mapToObj(dy -> IntStream.rangeClosed(-1,1)
        .filter(dx -> dx != 0 || dy != 0)
        .mapToObj(dx -> matrix.get(x+dx, y+dy)))
      .flatMap(Function.identity())
      .filter(Optional::isPresent)
      .map(Optional::get);
  }

  public static void main(String... args) {
    System.out.println(new ProblemTwo(368078).solve());
  }
}