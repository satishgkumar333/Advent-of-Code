package nl.nibsi.aoc.y17.d7;

import java.util.*;

import static java.util.Comparator.*;

final class ProblemTwo {

  private ProblemTwo() {}

  public static void main(String... args) {
    TreeNode<String, Program> root = Program.readAllAsTree();
    TreeNode<String, Program> node = root
      .mapValues((newNode, oldProgram) -> new Program(
        oldProgram.name(),
        newNode.children().values().stream()
          .map(TreeNode::value)
          .mapToInt(Program::weight)
          .sum() + oldProgram.weight(),
        oldProgram.subProgramNames()
      ));

    boolean foundUnbalanced = false;
    while (!foundUnbalanced) {
      Optional<TreeNode<String, Program>> different = node.getDifferentChild(comparingInt(Program::weight));
      if (different.isPresent())
        node = different.get();
      else {
        foundUnbalanced = true;
      }
    }

    System.out.println(root.findNode(node.value().name()).get().value());
    System.out.println(root.findNode(node.parent().get().children().values().iterator().next().value().name()).get().value());
  }
}