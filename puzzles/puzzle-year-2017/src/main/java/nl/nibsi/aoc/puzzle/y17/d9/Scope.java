package nl.nibsi.aoc.puzzle.y17.d9;

import java.util.*;

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;

final class Scope {

  private final Scope parent;

  private final List<Scope> subscopes;

  private final StringBuilder data;

  private Scope(Scope parent, boolean isLeaf) {
    this.parent = parent;
    subscopes = isLeaf ? null : new ArrayList<>();
    data = isLeaf ? new StringBuilder() : null;
  }

  Scope(Scope parent) {
    this(parent, false);
  }

  Optional<Scope> parent() {
    return Optional.ofNullable(parent);
  }

  List<Scope> subscopes() {
    return unmodifiableList(subscopes);
  }

  boolean isRoot() {
    return parent == null;
  }

  boolean isLeaf() {
    return subscopes == null;
  }

  int depth() {
    if (isRoot())
      return 1;

    return parent.depth() +1;
  }

  int score() {
    if (isLeaf())
      return 0;

    return subscopes.stream().mapToInt(Scope::score).sum() + depth();
  }

  void addSubscope(Scope subscope) {
    if (isLeaf())
      throw new IllegalStateException();

    subscopes.add(subscope);
  }

  void appendChar(char c){
    Scope dataScope = this;
    if (!isLeaf())
      dataScope = getLastSubscopeIfLeafOrAddNewLeaf();

    dataScope.data.append(c);
  }

  private Scope getLastSubscopeIfLeafOrAddNewLeaf() {
    if (!subscopes.isEmpty()) {
      Scope last = subscopes.get(subscopes.size() -1);
      if (last.isLeaf())
        return last;
    }

    Scope newScope = new Scope(this, true);
    subscopes.add(newScope);
    return newScope;
  }

  @Override
  public String toString() {
    if (isLeaf())
      return data.toString();

    return subscopes.stream().map(Scope::toString).collect(joining(", ", "{", "}"));
  }
}