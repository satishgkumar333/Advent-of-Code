package nl.nibsi.aoc.puzzle.y17.d3;

import java.util.*;
import java.util.function.*;

final class Turtle {

  private int x, y, speed;

  private Direction direction;

  private final List<Consumer<? super Turtle>> movementListeners = new ArrayList<>();

  Turtle(Direction startingDirection) {
    if (startingDirection == null)
      throw new IllegalArgumentException();

    direction = startingDirection;
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

  Turtle incrementSpeed() {
    speed++;
    return this;
  }
  
  Turtle rotate(Rotation rotation) {
    direction = rotation.applyTo(direction);
    return this;
  }

  Turtle moveForward() {
    for (int i = 0; i < speed; i++) {
      switch (direction) {
        case RIGHT : x++; break;
        case DOWN  : y--; break;
        case LEFT  : x--; break;
        case UP    : y++; break;
      }

      fireMoveEvent();
    }

    return this;
  }

  Turtle addMovementListener(Consumer<? super Turtle> listener) {
    movementListeners.add(listener);
    return this;
  }

  private void fireMoveEvent() {
    movementListeners.forEach(listener -> listener.accept(this));
  }
}