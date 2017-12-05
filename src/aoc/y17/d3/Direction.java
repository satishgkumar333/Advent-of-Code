package aoc.y17.d3;

enum Direction {

  RIGHT, DOWN, LEFT, UP;
  
}

enum Rotation {

  NONE, CLOCKWISE, REVERSE, COUNTER_CLOCKWISE;

  Direction applyTo(Direction direction) {
    if (direction == null)
      throw new IllegalArgumentException();

    Direction[] directions = Direction.values();

    return directions[(direction.ordinal() + this.ordinal()) % directions.length];
  }
}