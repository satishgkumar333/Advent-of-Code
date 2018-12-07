package nl.nibsi.aoc.puzzle.y18.d06;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.*;
import java.util.regex.*;
import java.util.stream.*;

import nl.nibsi.aoc.Puzzle;

import static java.lang.Math.abs;
import static java.util.Comparator.comparingInt;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.rangeClosed;

public final class Day06Puzzle implements Puzzle {

  private static final class Position {

    private static final Pattern PATTERN = Pattern.compile("^\\s*(?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\s*$");

    private final int x, y;

    private Position(int x, int y) {
      this.x = x;
      this.y = y;
    }

    private static Position of(int x, int y) {
      return new Position(x, y);
    }

    private int x() {
      return x;
    }

    private int y() {
      return y;
    }

    private int getDistanceTo(Position other) {
      return abs(other.x - this.x) + abs(other.y - this.y);
    }

    private Stream<Position> neighbors() {
      return Stream.of(
        Position.of(x,   y-1),
        Position.of(x+1, y  ),
        Position.of(x,   y+1),
        Position.of(x-1, y  )
      );
    }

    @Override
    public boolean equals(Object object) {
      if (!(object instanceof Position))
        return false;

      Position other = (Position) object;

      return this.x == other.x
          && this.y == other.y;
    }

    @Override
    public int hashCode() {
      return Objects.hash(x, y);
    }

    @Override
    public String toString() {
      return String.format("%d, %d", x, y);
    }

    private static Position parse(String line) {
      Matcher matcher = PATTERN.matcher(line);
      if (!matcher.matches())
        throw new IllegalArgumentException();

      return Position.of(
        Integer.parseInt(matcher.group("x")),
        Integer.parseInt(matcher.group("y"))
      );
    }
  }

  private static final class Rectangle {

    private final Position topLeft;

    private final int width, height;

    private Rectangle(Position topLeft, int width, int height) {
      this.topLeft = topLeft;
      this.width = width;
      this.height = height;
    }

    private Position topLeft() {
      return topLeft;
    }

    private Position bottomRight() {
      return Position.of(topLeft.x + width - 1, topLeft.y + height - 1);
    }

    private boolean contains(Position position) {
      int x = position.x();
      int y = position.y();

      Position bottomRight = bottomRight();

      return x >= topLeft.x() && x <= bottomRight.x()
          && y >= topLeft.y() && y <= bottomRight.y();
    }

    private Stream<Position> surface() {
      Position bottomRight = bottomRight();

      return rangeClosed(topLeft.y(), bottomRight.y()).boxed().flatMap(
        y -> rangeClosed(topLeft.x(), bottomRight.x()).mapToObj(
          x -> Position.of(x, y)));
    }

    private Stream<Position> border() {
      Position bottomRight = bottomRight();
      return surface().filter(position ->
        position.x() == topLeft.x() || position.x() == bottomRight.x() ||
        position.y() == topLeft.y() || position.y() == bottomRight.y()
      );
    }
  }

  private static final class TerritoryMap {

    private final Map<Position, String> territories = new HashMap<>();

    private void putCenter(Position centerOfTerritory, String nameOfTerritory) {
      territories.put(centerOfTerritory, nameOfTerritory);
    }

    private Rectangle getBoundingRectangle() {
      int minX = territories.keySet().stream().min(comparingInt(Position::x)).map(Position::x).orElse(0);
      int maxX = territories.keySet().stream().max(comparingInt(Position::x)).map(Position::x).orElse(-1);
      int minY = territories.keySet().stream().min(comparingInt(Position::y)).map(Position::y).orElse(0);
      int maxY = territories.keySet().stream().max(comparingInt(Position::y)).map(Position::y).orElse(-1);

      return new Rectangle(new Position(minX, minY), maxX - minX + 1, maxY - minY + 1);
    }

    private Map<String, Long> getSizeOfTerritories() {
      return territories.entrySet().stream().collect(groupingBy(Entry::getValue, counting()));
    }
    
    private long getCombinedDistances(Position position) {
      return territories.keySet().stream().mapToLong(position::getDistanceTo).sum();
    }

    private Map<Position, Long> getCombinedDistances() {
      return getBoundingRectangle().surface().collect(toMap(identity(), this::getCombinedDistances));
    }

    private void expandTerritories() {
      final class Claim {

        private final Position position;
        private final String nameOfTerritory;
        private final int distanceFromCenterOfTerritory;

        private Claim(Position position, String nameOfTerritory, int distanceFromCenterOfTerritory) {
          this.position = position;
          this.nameOfTerritory = nameOfTerritory;
          this.distanceFromCenterOfTerritory = distanceFromCenterOfTerritory;
        }

        private Position position() {
          return position;
        }

        private String nameOfTerritory() {
          return nameOfTerritory;
        }

        private int distanceFromCenterOfTerritory() {
          return distanceFromCenterOfTerritory;
        }
      }

      Map<Position, List<Claim>> claimsByPosition = getBoundingRectangle().surface()
        .flatMap(position -> territories.entrySet().stream()
          .map(territory -> new Claim(position, territory.getValue(), territory.getKey().getDistanceTo(position)))
        )
        .collect(groupingBy(
          Claim::position
        ));

      Map<Position, Integer> shortestDistances = claimsByPosition.entrySet().stream()
        .collect(toMap(
          Entry::getKey,
          claim -> claim.getValue().stream().mapToInt(Claim::distanceFromCenterOfTerritory).min().getAsInt()
        ));

      claimsByPosition.forEach((position, claims) -> claims.removeIf(claim -> claim.distanceFromCenterOfTerritory() > shortestDistances.get(position)));
      claimsByPosition.values().removeIf(claims -> claims.size() != 1);

      claimsByPosition.entrySet().stream().forEachOrdered(claim -> territories.putIfAbsent(claim.getKey(), claim.getValue().iterator().next().nameOfTerritory()));
    }

    private void removeBorderTerritories() {
      Set<String> borderTerritories = getBoundingRectangle().border().map(territories::get).distinct().filter(Objects::nonNull).collect(toSet());
      territories.values().removeAll(borderTerritories);
    }

    private void printTo(PrintStream out) {
      Rectangle bounds = getBoundingRectangle();
      Position topLeft = bounds.topLeft();
      Position bottomRight = bounds.bottomRight();

      for (int y = topLeft.y(); y <= bottomRight.y(); y++) {
        for (int x = topLeft.x(); x <= bottomRight.x(); x++) {
          String territoryName = territories.get(Position.of(x, y));
          if (territoryName != null)
            out.print(territoryName);
          else
            out.print('.');
        }

        out.println();
      }
    }
  }

  @Override
  public String solveFirstPart(BufferedReader inputReader) throws IOException {
    TerritoryMap map = new TerritoryMap();

    AtomicInteger i = new AtomicInteger('A');
    inputReader.lines().map(Position::parse).forEachOrdered(position -> map.putCenter(position, Character.toString((char) i.getAndIncrement())));

    map.expandTerritories();
    map.removeBorderTerritories();
    Map<String, Long> territorySizes = map.getSizeOfTerritories();

    return Long.toString(territorySizes.values().stream().mapToLong(Long::longValue).max().getAsLong());
  }

  @Override
  public String solveSecondPart(BufferedReader inputReader) throws IOException {
    TerritoryMap map = new TerritoryMap();

    AtomicInteger i = new AtomicInteger('A');
    inputReader.lines().map(Position::parse).forEachOrdered(position -> map.putCenter(position, Character.toString((char) i.getAndIncrement())));

    Map<Position, Long> combinedDistances = map.getCombinedDistances();
    combinedDistances.values().removeIf(distance -> distance >= 10000);

    return Integer.toString(combinedDistances.size());
  }
}