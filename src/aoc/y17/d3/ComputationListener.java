package aoc.y17.d3;

import java.util.*;

@FunctionalInterface
interface ComputationListener<T> {

  void computationPerformed(ComputationEvent<? extends T> event);

}

final class ComputationEvent<T> {

  private final TwoWayMatrix<T> matrix;
  private final int x, y;
  private final Optional<T> oldValue;
  private final T newValue;

  ComputationEvent(TwoWayMatrix<T> matrix, int x, int y, Optional<T> oldValue, T newValue) {
    if (matrix == null || oldValue == null)
      throw new IllegalArgumentException();

    this.matrix = matrix;
    this.x = x;
    this.y = y;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  TwoWayMatrix<T> matrix() {
    return matrix;
  }

  int x() {
    return x;
  }

  int y() {
    return y;
  }

  Optional<T> oldValue() {
    return oldValue;
  }

  T newValue() {
    return newValue;
  }
}