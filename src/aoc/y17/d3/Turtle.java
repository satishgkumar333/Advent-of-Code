package aoc.y17.d3;

import java.util.*;
import java.util.function.*;

final class Turtle<T> {

  private int x, y;
  
  private int speed = 1;

  private Direction direction;

  private Function<? super Turtle<T>, ? extends T> computationPerCell;

  private final GrowingTwoWayMatrix<T> matrix = new GrowingTwoWayMatrix<>();

  private final List<ComputationListener<? super T>> computationListeners = new ArrayList<>();

  Turtle(Direction direction, T startingCellValue) {
    if (direction == null)
      throw new IllegalArgumentException();

    this.direction = direction;

    matrix.set(x, y, startingCellValue);
  }
  
  int x() {
    return x;
  }
  
  int y() {
    return y;
  }
  
  int speed() {
    return speed;
  }
  
  Direction direction() {
    return direction;
  }
  
  TwoWayMatrix<T> matrix() {
    return matrix.asReadOnlyMatrix();
  }

  Turtle<T> incrementSpeed() {
    speed++;
    return this;
  }
  
  Turtle<T> rotate(Rotation rotation) {
    direction = rotation.applyTo(direction);
    return this;
  }

  Turtle<T> setComputationPerCell(Function<? super Turtle<T>, ? extends T> computationPerCell) {
    this.computationPerCell = computationPerCell;
    return this;
  }

  private void moveOneCellForward() {
    switch (direction) {
      case RIGHT : x++; break;
      case DOWN  : y--; break;
      case LEFT  : x--; break;
      case UP    : y++; break;
    }
  }

  Turtle<T> moveForward() {
    for (int i = 0; i < speed; i++) {
      moveOneCellForward();
      
      if (computationPerCell != null) {
        T newValue = computationPerCell.apply(this);
        Optional<T> oldValue = matrix.set(x, y, newValue);
        fireComputationEvent(oldValue, newValue);
      }
    }

    return this;
  }

  Turtle<T> addComputationListener(ComputationListener<? super T> listener) {
    computationListeners.add(listener);
    return this;
  }

  private void fireComputationEvent(Optional<T> oldValue, T newValue) {
    ComputationEvent<T> event = new ComputationEvent<>(matrix.asReadOnlyMatrix(), x, y, oldValue, newValue);
    computationListeners.forEach(listener -> listener.computationPerformed(event));
  }
}