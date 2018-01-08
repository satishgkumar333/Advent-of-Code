package nl.nibsi.aoc.puzzle.y17.d11;

import java.io.*;
import java.util.*;

final class Problem {

  private static List<Direction> readDirectionsFromInput() {
    try (
      Scanner scanner = new Scanner(new InputStreamReader(Problem.class.getResourceAsStream("input.txt")));
    ) {
      List<Direction> directions = new ArrayList<>();
      scanner.useDelimiter("\\s*,\\s*");
      scanner.forEachRemaining(dir -> directions.add(Direction.parse(dir)));
      return directions;
    }
  }

  public static void main(String... args) {
    int furthestDistance = 0;
    Location target = Location.origin();
    for (Direction direction: readDirectionsFromInput()) {
      target = target.go(direction);
      int distance = Location.origin().distanceTo(target);
      if (distance > furthestDistance)
        furthestDistance = distance;
    }

    System.out.println(furthestDistance);
  }
}