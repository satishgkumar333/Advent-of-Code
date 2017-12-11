package nl.nibsi.aoc.y17.d11;

import java.util.*;

enum Direction {
  
  NORTH_WEST (-1,  1),
  NORTH      ( 0,  2),
  NORTH_EAST ( 1,  1),
  SOUTH_EAST ( 1, -1),
  SOUTH      ( 0, -2),
  SOUTH_WEST (-1, -1);

  private final int dx, dy;

  private Direction(int dx, int dy) {
    this.dx = dx;
    this.dy = dy;
  }

  int dx() {
    return dx;
  }

  int dy() {
    return dy;
  }

  static Direction parse(String dir) {
    switch (dir.toLowerCase(Locale.ROOT)) {
      case "nw" : return NORTH_WEST;
      case "n"  : return NORTH;
      case "ne" : return NORTH_EAST;
      case "se" : return SOUTH_EAST;
      case "s"  : return SOUTH;
      case "sw" : return SOUTH_WEST;

      default   : throw new IllegalArgumentException();
    }
  }
}