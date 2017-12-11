package nl.nibsi.aoc.y17.d11;

import java.util.*;

import static java.util.Objects.*;

import static nl.nibsi.aoc.y17.d11.Direction.*;

final class Location {

  private final int x;
  private final int y;

  private Location(int x, int y) {
    this.x = x;
    this.y = y;
  }

  static Location origin() {
    return new Location(0,0);
  }

  int x() {
    return x;
  }

  int y() {
    return y;
  }

  Location go(Direction dir) {
    return new Location(
      x + dir.dx(),
      y + dir.dy()
    );
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Location))
      return false;

    Location other = (Location) obj;

    return this.x == other.x
        && this.y == other.y;
  }

  @Override
  public int hashCode() {
    return hash(x,y);
  }

  @Override
  public String toString() {
    return String.format("(%d, %d)", x,y);
  }

  Optional<Direction> nearerTo(Location target) {
    if (this.equals(target))
      return Optional.empty();

    int dx = target.x - x;
    int dy = target.y - y;

    return Optional.of(
      dx == 0 ?          dy > 0 ? NORTH      : SOUTH
              : dx > 0 ? dy > 0 ? NORTH_EAST : SOUTH_EAST
                       : dy > 0 ? NORTH_WEST : SOUTH_WEST
    );
  }
  
  Map<Direction, Integer> getShortestPath(Location target) {
    Map<Direction, Integer> shortestPath = new HashMap<>();

    Location location = this;
    while (!location.equals(target)) {
      Direction nearer = location.nearerTo(target).get();
      location = location.go(nearer);
      shortestPath.merge(nearer, 1, Integer::sum);
    }

    return shortestPath;
  }

  int distanceTo(Location target) {
    return getShortestPath(target).values().stream().mapToInt(Integer::intValue).sum();
  }
}