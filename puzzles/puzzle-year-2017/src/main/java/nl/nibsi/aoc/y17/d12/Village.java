package nl.nibsi.aoc.y17.d12;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import static java.util.stream.Collectors.*;

final class Village {

  private static final Pattern LINE_PATTERN = Pattern.compile("^(?<origin>\\d+) <-> (?<destinations>\\d+(?:,\\s*\\d+)*)$");

  private final Graph<Integer, Void, Void> programGraph = new Graph<>(false);

  boolean addPipe(int firstProgramId, int secondProgramId) {
    programGraph.addVertex(firstProgramId);
    programGraph.addVertex(secondProgramId);
    return programGraph.addEdge(firstProgramId, secondProgramId);
  }

  Map<Integer, Set<Integer>> getGroups() {
    return programGraph.getConnectedSubgraphs(programGraph.isDirected()).stream().collect(toMap(
      subgraph -> subgraph.keys().min(Integer::compare).get(),
      subgraph -> subgraph.keys().collect(toSet())
    ));
  }

  @Override
  public String toString() {
    return programGraph.toString();
  }
  
  static Village readVillageFromInput() {
    try (
      BufferedReader reader = new BufferedReader(new InputStreamReader(Village.class.getResourceAsStream("input.txt")));
    ) {
      Village village = new Village();
      reader.lines()
        .map(LINE_PATTERN::matcher)
        .forEach(matcher -> {
          if (!matcher.matches())
            throw new AssertionError();
          
          int origin = Integer.parseInt(matcher.group("origin"));
          
          Arrays.stream(matcher.group("destinations").split("\\s*,\\s*"))
            .mapToInt(Integer::parseInt)
            .forEach(destination -> village.addPipe(origin, destination));
        });
      
      return village;
    }
    
    catch (IOException ex) {
      throw new AssertionError(ex);
    }
  }

  public static void main(String... args) {
    System.out.println(readVillageFromInput().getGroups().size());
  }
}