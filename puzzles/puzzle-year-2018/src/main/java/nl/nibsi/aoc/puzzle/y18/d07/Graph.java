package nl.nibsi.aoc.puzzle.y18.d07;

import java.util.*;
import java.util.Map.Entry;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableMap;
import static java.util.Comparator.comparing;
import static java.util.Objects.hash;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toSet;

final class Graph<K extends Comparable<? super K>, V> {

  private final NavigableMap<K, Node> nodes = new TreeMap<>();

  private final NavigableMap<K, NavigableSet<Edge>> outgoingEdges = new TreeMap<>();

  private final NavigableMap<K, NavigableSet<Edge>> incomingEdges = new TreeMap<>();

  NavigableMap<K, Node> nodes() {
    return new TreeMap<>(nodes);
  }

  Node addNodeIfAbsent(K key) {
    if (key == null)
      throw new IllegalArgumentException();

    return nodes.computeIfAbsent(key, Node::new);
  }

  Node addNodeIfAbsent(K key, V value) {
    if (key == null)
      throw new IllegalArgumentException();

    return nodes.computeIfAbsent(key, absent -> new Node(absent, value));
  }

  Graph<K,V> removeNode(K key) {
    Graph<K,V> removedGraph = new Graph<>();

    Node removedNode = nodes.remove(key);
    if (removedNode == null)
      return removedGraph;

    removedGraph.addNodeIfAbsent(key);

    Set<Edge> edges = outgoingEdges.remove(key);
    if (edges != null)
      edges.forEach(edge -> {
        incomingEdges.get(edge.to().key()).remove(edge);
        removedGraph.addEdgeIfAbsent(edge.from().key(), edge.to().key());
      });

    edges = incomingEdges.remove(key);
    if (edges != null)
      edges.forEach(edge -> {
        outgoingEdges.get(edge.from().key()).remove(edge);
        removedGraph.addEdgeIfAbsent(edge.from().key(), edge.to().key());
      });

    return removedGraph;
  }

  Edge addEdgeIfAbsent(K from, K to) {
    Node fromNode = addNodeIfAbsent(from);
    Node toNode   = addNodeIfAbsent(to);

    Edge edge = new Edge(fromNode, toNode);

    Set<Edge> edges = outgoingEdges.computeIfAbsent(from, absent -> new TreeSet<>(comparing(Edge::to)));
    if (!edges.contains(edge))
      edges.add(edge);

    edges = incomingEdges.computeIfAbsent(to, absent -> new TreeSet<>(comparing(Edge::from)));
    if (!edges.contains(edge))
      edges.add(edge);

    return edge;
  }

  NavigableSet<Node> findRoots() {
    Set<K> keysWithIncomingEdge = incomingEdges.entrySet().stream()
      .filter(entry -> !entry.getValue().isEmpty())
      .map(Entry::getKey)
      .collect(toSet());

    return nodes.values().stream()
      .filter(node -> !keysWithIncomingEdge.contains(node.key()))
      .collect(toCollection(TreeSet::new));
  }

  final class Node implements Comparable<Node> {

    private final K key;

    private V value;

    private Node(K key) {
      this.key = key;
    }

    private Node(K key, V value) {
      this(key);
      setValue(value);
    }

    Graph<K,V> graph() {
      return Graph.this;
    }

    K key() {
      return key;
    }

    V value() {
      return value;
    }

    void setValue(V value) {
      this.value = value;
    }

    @Override
    public int compareTo(Node other) {
      if (!other.graph().equals(this.graph()))
        throw new IllegalArgumentException();

      return this.key().compareTo(other.key());
    }

    @Override
    public boolean equals(Object object) {
      if (!(object instanceof Graph.Node))
        return false;

      Graph<?,?>.Node other = (Graph.Node) object;

      return this.graph().equals(other.graph())
          && this.key()  .equals(other.key());
    }

    @Override
    public int hashCode() {
      return hash(graph(), key());
    }

    @Override
    public String toString() {
      return key().toString();
    }
  }

  final class Edge {

    private final Node from, to;

    private Edge(Node from, Node to) {
      this.from = from;
      this.to = to;
    }

    Node from() {
      return from;
    }

    Node to() {
      return to;
    }

    @Override
    public boolean equals(Object object) {
      if (!(object instanceof Graph.Edge))
        return false;

      Graph<?,?>.Edge other = (Graph.Edge) object;

      return this.from().equals(other.from())
          && this.to().equals(other.to());
    }

    @Override
    public int hashCode() {
      return hash(from(), to());
    }

    @Override
    public String toString() {
      return format("%s -> %s", from(), to());
    }
  }
}