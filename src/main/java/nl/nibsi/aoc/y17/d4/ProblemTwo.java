package nl.nibsi.aoc.y17.d4;

final class ProblemTwo {

  public static void main(String... args) {
    System.out.println(PassPhrase.readAll().stream().filter(PassPhrase::containsNoAnagrams).count());
  }
}