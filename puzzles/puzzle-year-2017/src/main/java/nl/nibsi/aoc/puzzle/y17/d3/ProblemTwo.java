package nl.nibsi.aoc.puzzle.y17.d3;

import java.util.*;
import java.util.stream.*;

import static nl.nibsi.aoc.puzzle.y17.d3.Rotation.*;

final class ProblemTwo {
  
  private int lastComputation = 1;
  
  private final GrowingTwoWayMatrix<Integer> matrix = new GrowingTwoWayMatrix<>();
  {
    matrix.set(0, 0, lastComputation);
  }

  private final Turtle turtle = new Turtle(Direction.DOWN).addMovementListener(event -> updateLastComputation());

  private final int input;

  ProblemTwo(int input) {
    this.input = input;
  }

  private void updateLastComputation() {
    if (lastComputation > input)
      return;

    lastComputation = getValuesAdjacentToTurtle().sum();
    matrix.set(turtle.x(), turtle.y(), lastComputation);
  }

  int solve() {
    while (lastComputation < input) {
      turtle
        .incrementSpeed()
        .rotate(COUNTER_CLOCKWISE)
        .moveForward()
        .rotate(COUNTER_CLOCKWISE)
        .moveForward();
    }

    return lastComputation;
  }

  private IntStream getValuesAdjacentToTurtle() {
    return IntStream.rangeClosed(-1,1)
      .flatMap(dy -> IntStream.rangeClosed(-1,1)
        .filter(dx -> dx != 0 || dy != 0)
        .mapToObj(dx -> matrix.get(turtle.x()+dx, turtle.y()+dy))
        .filter(Optional::isPresent)
        .mapToInt(Optional::get)
      );
  }

  public static void main(String... args) {
    System.out.println(new ProblemTwo(368078).solve());
  }
}