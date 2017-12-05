package aoc.y17.d3;

import java.util.*;
import java.util.function.*;

import static java.util.Collections.*;

final class GrowingTwoWayArray<T> {

  private final List<T> nonNegativeElements = new ArrayList<>();
  private final List<T> negativeElements = new ArrayList<>();

  Optional<T> get(int index) {
    List<T> list = nonNegativeElements;
    if (index < 0) {
      list = negativeElements;
      index = -index -1;
    }

    if (index >= list.size())
      return Optional.empty();

    return Optional.ofNullable(list.get(index));
  }

  Optional<T> set(int index, T element) {
    List<T> list = nonNegativeElements;
    if (index < 0) {
      list = negativeElements;
      index = -index -1;
    }

    int spaceNeeded = index - list.size() + 1;
    if (spaceNeeded > 0)
      list.addAll(nCopies(spaceNeeded, null));

    return Optional.ofNullable(list.set(index, element));
  }

  T setIfAbsent(int index, Supplier<? extends T> supplier) {
    Optional<T> existing = get(index);
    if (existing.isPresent())
      return existing.get();

    T element = supplier.get();
    set(index, element);
    return element;
  }
}