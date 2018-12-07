package nl.nibsi.aoc.puzzle.y18.d07;

import java.io.*;
import java.util.*;

import nl.nibsi.aoc.Puzzle;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toSet;


public final class Day07Puzzle implements Puzzle {

  private static final int MINIMUM_TIME_PER_STEP = 60;

  private static final int NUMBER_OF_WORKERS = 5;

  private static <V> Graph<Character, V> readDependencyGraph(BufferedReader inputReader) throws IOException {
    Graph<Character, V> graph = new Graph<>();
    inputReader.lines().map(Instruction::parse).forEach(instruction ->
      graph.addEdgeIfAbsent(instruction.first(), instruction.second()));

    return graph;
  }

  @Override
  public String solveFirstPart(BufferedReader inputReader) throws IOException {
    Graph<Character, Void> graph = readDependencyGraph(inputReader);
    StringBuilder orderedSteps = new StringBuilder();

    for (NavigableSet<Graph<Character, Void>.Node> roots = graph.findRoots(); !roots.isEmpty(); roots = graph.findRoots()) {
      char step = roots.first().key();
      orderedSteps.append(step);
      graph.removeNode(step);
    }

    return orderedSteps.toString();
  }

  @Override
  public String solveSecondPart(BufferedReader inputReader) throws IOException {
    Graph<Character, Integer> graph = readDependencyGraph(inputReader);
    graph.nodes().forEach((step, node) -> node.setValue(step - 'A' + MINIMUM_TIME_PER_STEP + 1));

    StringBuilder orderedSteps = new StringBuilder();

    Set<Graph<Character, Integer>.Node> inProgress = graph.findRoots().stream()
      .limit(NUMBER_OF_WORKERS)
      .collect(toCollection(TreeSet::new));

    int second;
    for (second = 0; !inProgress.isEmpty(); second++) {
      StringBuilder workers = new StringBuilder(inProgress.stream().map(node -> node.key()).map(Object::toString).collect(joining("  ")));
      for (int i = inProgress.size(); i < NUMBER_OF_WORKERS; i++)
        workers.append("  .");

      System.out.printf("%3d  %s  %s%n", second, workers, orderedSteps);

      new TreeSet<>(inProgress).forEach(node -> {
        char step = node.key();

        int workLeft = node.value() - 1;

        if (workLeft == 0) {
          orderedSteps.append(step);
          graph.removeNode(step);
          inProgress.remove(node);

          inProgress.addAll(
            graph.findRoots().stream()
              .filter(root -> !inProgress.contains(root))
              .limit(NUMBER_OF_WORKERS - inProgress.size())
              .collect(toSet())
          );
        }

        else {
          node.setValue(workLeft);
        }
      });
    }

    return Integer.toString(second);
  }
}