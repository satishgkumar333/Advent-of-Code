package aoc.y17.d4;

final class ProblemOne {

  public static void main(String... args) {
    System.out.println(PassPhrase.readAll().stream().filter(PassPhrase::containsNoDuplicates).count());
  }
}