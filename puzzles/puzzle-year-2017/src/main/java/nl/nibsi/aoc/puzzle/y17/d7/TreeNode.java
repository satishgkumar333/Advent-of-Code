package nl.nibsi.aoc.puzzle.y17.d7;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;

final class TreeNode<K,V> {

  private final K key;

  private V value;
  
  private TreeNode<K,V> parent;
  private final Map<K, TreeNode<K,V>> children = new LinkedHashMap<>();

  TreeNode(K key, V value) {
    this.key = key;
    this.value = value;
  }

  K key() {
    return key;
  }

  V value() {
    return value;
  }
  
  Optional<TreeNode<K,V>> parent() {
    return Optional.ofNullable(parent);
  }

  Map<K, TreeNode<K,V>> children() {
    return unmodifiableMap(children);
  }
  
  boolean isRoot() {
    return parent == null;
  }

  void addChild(TreeNode<K,V> node) {
    if (!node.isRoot())
      throw new IllegalArgumentException();
    
    children.put(node.key, node);
    node.parent = this;
  }

  Optional<TreeNode<K,V>> findNode(K key) {
    if (this.key.equals(key))
      return Optional.of(this);

    return Optional.ofNullable(children.get(key)).or(() ->
      children.values().stream()
        .map(child -> child.findNode(key))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findAny()
    );
  }

  Optional<TreeNode<K,V>> getDifferentChild(Comparator<? super V> valueComparer) {
    if (children.size() < 3)
      return Optional.empty();

    Iterator<TreeNode<K,V>> iterator = children.values().iterator();
    TreeNode<K,V> firstChild = iterator.next();
    TreeNode<K,V> nextChild = iterator.next();

    if (valueComparer.compare(firstChild.value(), nextChild.value()) != 0) {
      TreeNode<K,V> thirdChild = iterator.next();
      return Optional.ofNullable(
        valueComparer.compare(firstChild.value(), thirdChild.value()) == 0 ? nextChild :
        valueComparer.compare(nextChild.value(), thirdChild.value()) == 0 ? firstChild : null
      );
    }

    while (iterator.hasNext()) {
      nextChild = iterator.next();
      if (valueComparer.compare(firstChild.value(), nextChild.value()) != 0)
        return Optional.of(nextChild);
    }

    return Optional.empty();
  }

  <U> TreeNode<K,U> mapValues(BiFunction<? super TreeNode<? extends K, ? extends U>, ? super V, U> mapper) {
    TreeNode<K,U> result = new TreeNode<>(key, null);
    children.values().stream().map(child -> child.mapValues(mapper)).forEachOrdered(result::addChild);
    result.value = mapper.apply(result, value);
    return result;
  }

  @Override
  public String toString() {
    return String.format("(%s, %s)", key, value);
  }

  static <T,K,V> TreeNode<K,V> buildTree(
    Collection<? extends T> items,
    Function<? super T, ? extends K> keyMapper,
    Function<? super T, ? extends Stream<? extends K>> subKeyMapper,
    Function<? super T, ? extends V> valueMapper
  ) {
    Map<K, TreeNode<K,T>> nodes = items.stream()
      .map(item -> new TreeNode<>(keyMapper.apply(item), item))
      .collect(toMap(TreeNode::key, item -> item));

    nodes.values().forEach(node -> subKeyMapper.apply(node.value()).map(nodes::get).forEach(node::addChild));

    TreeNode<K,T> root = nodes.values().stream().filter(TreeNode::isRoot).findAny().get();

    return root.mapValues((newNode, oldItem) -> valueMapper.apply(oldItem));
  }
}