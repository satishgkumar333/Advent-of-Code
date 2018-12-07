package nl.nibsi.aoc.puzzle.y18.d03;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

import nl.nibsi.aoc.Puzzle;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public final class Day03Puzzle implements Puzzle {
  
  private static final class Position {
    
    private final int x, y;
    
    private Position(int x, int y) {
      this.x = x;
      this.y = y;
    }
    
    private int x() {
      return x;
    }
    
    private int y() {
      return y;
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
  }

  private static final class Claim {

    private static Pattern PATTERN = Pattern.compile(
      "^\\s*#(?<id>\\d+)\\s*@\\s*(?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\s*:\\s*(?<width>\\d+)\\s*x\\s*(?<height>\\d+)\\s*$"
    );

    private final String id;

    private final int x, y, width, height;

    private Claim(String id, int x, int y, int width, int height) {
      if (id == null)
        throw new IllegalArgumentException("The ID must not be null.");

      this.id = id.trim();

      if (this.id.isEmpty() || !this.id.equals(id))
        throw new IllegalArgumentException("The ID must be a non-empty trimmed string.");

      this.x = x;
      this.y = y;

      if (width < 0 || height < 0)
        throw new IllegalArgumentException("The width and height must not be negative.");

      this.width = width;
      this.height = height;
    }

    private String id() {
      return id;
    }

    private int x() {
      return x;
    }

    private int y() {
      return y;
    }

    private int width() {
      return width;
    }

    private int height() {
      return height;
    }

    private Stream<Position> surface() {
      return IntStream.range(y, y + height).boxed().flatMap(
        y -> IntStream.range(x, x + width).mapToObj(
          x -> new Position(x, y)));
    }

    private static Optional<Claim> parse(String line) {
      Matcher matcher = PATTERN.matcher(line);
      if (!matcher.matches())
        return Optional.empty();

      return Optional.of(new Claim(
        matcher.group("id"),
        Integer.parseInt(matcher.group("x")),
        Integer.parseInt(matcher.group("y")),
        Integer.parseInt(matcher.group("width")),
        Integer.parseInt(matcher.group("height"))
      ));
    }
  }

  @Override
  public String solveFirstPart(BufferedReader inputReader) throws IOException {
    Map<Position, Long> claimedPositionCounts = inputReader.lines()
      .map(Claim::parse)
      .filter(Optional::isPresent)
      .map(Optional::get)
      .flatMap(Claim::surface)
      .collect(groupingBy(identity(), counting()));

    return Long.toString(claimedPositionCounts.values().stream().filter(count -> count > 1).count());
  }

  @Override
  public String solveSecondPart(BufferedReader inputReader) throws IOException {
    Collection<Claim> claims = inputReader.lines()
      .map(Claim::parse)
      .filter(Optional::isPresent)
      .map(Optional::get)
      .collect(toList());

    Map<Position, Long> claimedPositionCounts = claims.stream()
      .flatMap(Claim::surface)
      .collect(groupingBy(identity(), counting()));

    return claims.stream()
      .filter(claim -> claim.surface().allMatch(position -> claimedPositionCounts.get(position) == 1))
      .findAny()
      .map(Claim::id)
      .orElse(null);
  }
}