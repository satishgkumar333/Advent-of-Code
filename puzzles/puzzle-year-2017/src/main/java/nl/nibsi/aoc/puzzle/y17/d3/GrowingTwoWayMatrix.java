package nl.nibsi.aoc.puzzle.y17.d3;

import java.util.*;

final class GrowingTwoWayMatrix<T> implements TwoWayMatrix<T> {

  private final GrowingTwoWayArray<GrowingTwoWayArray<T>> rows = new GrowingTwoWayArray<>();

  private final TwoWayMatrix<T> readOnlyView = (x,y) -> get(x,y);

  @Override
  public Optional<T> get(int x, int y) {
    return rows.get(y).flatMap(row -> row.get(x));
  }

  Optional<T> set(int x, int y, T element) {
    return rows.setIfAbsent(y, GrowingTwoWayArray::new).set(x, element);
  }

  TwoWayMatrix<T> asReadOnlyMatrix() {
    return readOnlyView;
  }
}