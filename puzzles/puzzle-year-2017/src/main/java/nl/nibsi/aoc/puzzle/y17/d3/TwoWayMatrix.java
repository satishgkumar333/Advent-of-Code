package nl.nibsi.aoc.puzzle.y17.d3;

import java.util.*;

interface TwoWayMatrix<T> {

  Optional<T> get(int x, int y);

}