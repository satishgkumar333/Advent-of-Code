package nl.nibsi.aoc.puzzle.y17.d8;

import java.util.*;

enum RelationalOperator {

  LESS_THAN             ("<",  (a,b) -> a < b),
  GREATER_THAN          (">",  (a,b) -> a > b),
  LESS_THAN_OR_EQUAL    ("<=", (a,b) -> a <= b),
  GREATER_THAN_OR_EQUAL (">=", (a,b) -> a >= b),
  EQUAL                 ("==", (a,b) -> a == b),
  NOT_EQUAL             ("!=", (a,b) -> a != b);

  private final String symbol;
  private final IntBiPredicate predicate;

  private RelationalOperator(String symbol, IntBiPredicate predicate) {
    this.symbol = symbol;
    this.predicate = predicate;
  }

  boolean apply(int left, int right) {
    return predicate.test(left, right);
  }

  @Override
  public String toString() {
    return symbol;
  }

  static RelationalOperator parse(String string) {
    return Arrays.stream(values())
      .filter(operator -> operator.symbol.equals(string))
      .findAny()
      .orElseThrow(IllegalArgumentException::new);
  }
}