package nl.nibsi.aoc.y17.d12;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;

final class Graph<K,V,W> {
  
  private final Map<K, Vertex<K,V>> vertices = new HashMap<>();
  private final Map<K, Map<K, Edge<K,W>>> edges = new HashMap<>();
  
  private final boolean isDirected;
  
  Graph(boolean isDirected) {
    this.isDirected = isDirected;
  }
  
  boolean isDirected() {
    return isDirected;
  }
  
  boolean addVertex(Vertex<K,V> vertex) {
    if (vertex == null)
      throw new IllegalArgumentException();
    
    return vertices.putIfAbsent(vertex.key, vertex) == null;
  }
  
  boolean addVertex(K key, V value) {
    return addVertex(new Vertex<>(key, value));
  }
  
  boolean addVertex(K key) {
    return addVertex(new Vertex<>(key));
  }
  
  Optional<Vertex<K,V>> getVertex(K key) {
    return Optional.ofNullable(vertices.get(key));
  }

  Collection<Vertex<K,V>> vertices() {
    return unmodifiableCollection(vertices.values());
  }

  Stream<K> keys() {
    return vertices().stream().map(Vertex::key);
  }
  
  boolean addEdge(Edge<K,W> edge) {
    if (edge == null)
      throw new IllegalArgumentException();

    K origin = edge.origin;
    K destination = edge.destination;

    if (!(vertices.containsKey(origin) && vertices.containsKey(destination)))
      throw new IllegalArgumentException();
    
    if (edges.computeIfAbsent(origin, absent -> new HashMap<>()).putIfAbsent(destination, edge) == null) {
      if (!isDirected)
        edges.computeIfAbsent(destination, absent -> new HashMap<>()).put(origin, edge.reverse());
      
      return true;
    }
    
    return false;
  }
  
  boolean addEdge(K origin, K destination, W weight) {
    return addEdge(new Edge<>(origin, destination, weight));
  }
  
  boolean addEdge(K origin, K destination) {
    return addEdge(new Edge<>(origin, destination));
  }

  Stream<Edge<K,W>> edges() {
    return edges.values().stream().map(Map::values).flatMap(Collection::stream);
  }
  
  Stream<Edge<K,W>> getOutgoingEdges(K key) {
    return Stream.ofNullable(edges.get(key)).map(Map::values).flatMap(Collection::stream);
  }
  
  Stream<Edge<K,W>> getIncomingEdges(K key) {
    return edges.values().stream().map(Map::values).flatMap(Collection::stream).filter(edge -> edge.destination.equals(key));
  }

  Graph<K,V,W> createSubgraph(Collection<? extends K> keys, boolean isDirected) {
    Collection<K> copyOfKeys = new HashSet<>(keys);
    copyOfKeys.retainAll(vertices.keySet());

    Graph<K,V,W> subgraph = new Graph<>(isDirected);

    copyOfKeys.stream()
      .map(vertices::get)
      .forEach(subgraph::addVertex);

    copyOfKeys.stream()
      .flatMap(this::getOutgoingEdges)
      .filter(edge -> copyOfKeys.contains(edge.destination))
      .forEach(subgraph::addEdge);

    return subgraph;
  }
  
  Collection<Vertex<K,V>> getConnectedVertices(K key) {
    Map<K, Vertex<K,V>> connected = new HashMap<>();
    Queue<Vertex<K,V>> verticesToInvestigate = new ArrayDeque<>();
    getVertex(key).ifPresent(verticesToInvestigate::add);

    while (!verticesToInvestigate.isEmpty()) {
      Vertex<K,V> vertexToInvestigate = verticesToInvestigate.remove();
      K origin = vertexToInvestigate.key;
      
      if (connected.putIfAbsent(origin, vertexToInvestigate) != null)
        continue;

      getOutgoingEdges(origin)
        .map(Edge::destination)
        .map(vertices::get)
        .filter(neighbor -> !connected.containsKey(neighbor.key))
        .forEach(verticesToInvestigate::add);
    }

    return connected.values();
  }
  
  Collection<Graph<K,V,W>> getConnectedSubgraphs(boolean areDirected) {
    Collection<Graph<K,V,W>> subgraphs = new ArrayList<>();
    Queue<Vertex<K,V>> remainingVertices = new ArrayDeque<>(vertices.values());

    while (!remainingVertices.isEmpty()) {
      Collection<Vertex<K,V>> connectedVertices = getConnectedVertices(remainingVertices.remove().key);
      remainingVertices.removeAll(connectedVertices);
      Graph<K,V,W> subgraph = createSubgraph(connectedVertices.stream().map(Vertex::key).collect(toSet()), areDirected);
      subgraphs.add(subgraph);
    }

    return subgraphs;
  }
  
  private String edgeToString(Edge<K,W> edge) {
    return edge.toString(isDirected, vertices::get);
  }

  @Override
  public String toString() {
    return edges().map(this::edgeToString).collect(joining("\n"));
  }

  static final class Vertex<K,V> {

    private final K key;
    private final V value;

    Vertex(K key, V value) {
      if (key == null)
        throw new IllegalArgumentException();

      this.key = key;
      this.value = value;
    }

    Vertex(K key) {
      this(key, null);
    }

    K key() {
      return key;
    }

    Optional<V> value() {
      return Optional.ofNullable(value);
    }

    @Override
    public String toString() {
      if (value == null)
        return key.toString();
      else
        return String.format("(%s, %s)", key, value);
    }
  }

  static final class Edge<K,W> {

    private final K origin, destination;
    private final W weight;

    Edge(K origin, K destination, W weight) {
      if (origin == null || destination == null)
        throw new IllegalArgumentException();

      this.origin = origin;
      this.destination = destination;
      this.weight = weight;
    }

    Edge(K origin, K destination) {
      this(origin, destination, null);
    }

    K origin() {
      return origin;
    }

    K destination() {
      return destination;
    }

    Optional<W> weight() {
      return Optional.ofNullable(weight);
    }

    Edge<K,W> reverse() {
      return new Edge<>(destination, origin, weight);
    }
    
    private String toString(boolean isDirected, Function<? super K, ?> keyMapper) {
      return String.format(
        "%s %s%s-> %s",
        keyMapper.apply(origin),
        isDirected ? "" : "<",
        weight == null ? "" : String.format("-[%s]", weight),
        keyMapper.apply(destination)
      );
    }

    @Override
    public String toString() {
      return toString(true, key -> key);
    }
  }
}