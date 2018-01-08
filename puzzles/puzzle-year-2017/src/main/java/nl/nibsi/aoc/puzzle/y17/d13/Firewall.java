package nl.nibsi.aoc.puzzle.y17.d13;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import static java.lang.Math.*;
import static java.util.stream.Collectors.*;

final class Firewall {

  private final NavigableMap<Integer, Integer> rangePerLayer;

  Firewall(Map<Integer, Integer> rangePerLayer) {
    if (rangePerLayer == null)
      throw new IllegalArgumentException();

    this.rangePerLayer = new TreeMap<>(rangePerLayer);
  }

  int getLayerCount() {
    return rangePerLayer.lastKey() +1;
  }

  int getRange(int depth) {
    return rangePerLayer.getOrDefault(depth, 0);
  }

  OptionalInt getScannerPosition(int depth, int picoSecond) {
    int range = getRange(depth);
    if (range == 0)
      return OptionalInt.empty();

    int maxIndex = range - 1;
    int period = maxIndex * 2;
    int phase = picoSecond % period;
    int position = maxIndex - abs(phase - maxIndex);

    return OptionalInt.of(position);
  }
  
  boolean blocksPacket(int depth, int delay) {
    return getScannerPosition(depth, depth+delay).orElse(-1) == 0;
  }

  boolean blocksPacket(int delay) {
    return IntStream.range(0, getLayerCount())
      .anyMatch(depth -> blocksPacket(depth, delay));
  }

  int getSeverity(int delay) {
    return IntStream.range(1, getLayerCount())
      .filter(depth -> blocksPacket(depth, delay))
      .map(depth -> depth * getRange(depth))
      .sum();
  }

  private static Firewall readFirewallFromInput() {
    try (
      BufferedReader reader = new BufferedReader(new InputStreamReader(Firewall.class.getResourceAsStream("input.txt")));
    ) {
      return new Firewall(reader.lines().map(line -> line.split(":")).collect(toMap(
        layerAndRange -> Integer.parseInt(layerAndRange[0].trim()),
        layerAndRange -> Integer.parseInt(layerAndRange[1].trim())
      )));
    }

    catch (IOException ex) {
      throw new AssertionError(ex);
    }
  }

  public static void main(String... args) {
    Firewall firewall = readFirewallFromInput();
    System.out.println(IntStream.iterate(0, delay -> delay + 1).dropWhile(firewall::blocksPacket).findFirst().getAsInt());
  }
}