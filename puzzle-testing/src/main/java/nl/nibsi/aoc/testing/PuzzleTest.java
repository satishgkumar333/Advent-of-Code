package nl.nibsi.aoc.testing;

import java.io.*;

import org.junit.*;

import nl.nibsi.aoc.Puzzle;

import static org.junit.Assert.assertTrue;

public abstract class PuzzleTest {

  private Puzzle puzzle;
  private BufferedReader inputReader;

  @Before
  public void setUp() throws Exception {
    puzzle = createPuzzle();
    inputReader = createInput();
  }

  @After
  public void tearDown() throws Exception {
    inputReader.close();
    inputReader = null;
    puzzle = null;
  }

  protected abstract Puzzle createPuzzle() throws Exception;

  protected BufferedReader createInput() throws Exception {
    Class<? extends PuzzleTest> testClass = getClass();
    InputStream input = testClass.getResourceAsStream(testClass.getSimpleName() + ".input");
    return new BufferedReader(new InputStreamReader(input, "UTF-8"));
  }

  protected Secret createPartOneAnswer() throws Exception {
    Class<? extends PuzzleTest> testClass = getClass();
    return readSecret(testClass, testClass.getSimpleName() + ".part1.secret");
  }

  protected Secret createPartTwoAnswer() throws Exception {
    Class<? extends PuzzleTest> testClass = getClass();
    return readSecret(testClass, testClass.getSimpleName() + ".part2.secret");
  }

  private static Secret readSecret(Class<? extends PuzzleTest> testClass, String resourceName) throws IOException {
    try (
      InputStream input = testClass.getResourceAsStream(resourceName);
      BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
    ) {
      return Secret.parse(reader.readLine());
    }
  }

  @Test
  public void testFirstPart() throws Exception {
    try (
      Secret answer = createPartOneAnswer();
    ) {
      char[] guess = puzzle.solveFirstPart(inputReader).toCharArray();
      assertTrue(answer.isCorrectAttempt(guess));
    }
  }

  @Test
  public void testSecondPart() throws Exception {
    try (
      Secret answer = createPartTwoAnswer();
    ) {
      char[] guess = puzzle.solveSecondPart(inputReader).toCharArray();
      assertTrue(answer.isCorrectAttempt(guess));
    }
  }
}