package nl.nibsi.aoc.puzzle.y17.d8;

import java.util.*;
import java.util.regex.*;

final class Instruction {

  private static final Pattern INSTRUCTION_PATTERN = Pattern.compile(
    "^(?<register>[a-z]+) (?<incOrDec>inc|dec) (?<increment>-?\\d+) " +
    "if (?<conditionRegister>[a-z]+) (?<operator>>|<|>=|<=|==|!=) (?<conditionValue>-?\\d+)$"
  );

  private final String registerName, conditionRegisterName;
  private final boolean isIncrement;
  private final int increment, conditionValue;
  private final RelationalOperator operator;

  Instruction(
    String registerName,
    boolean isIncrement,
    int increment,
    String conditionRegisterName,
    RelationalOperator operator,
    int conditionValue
  ) {
    if (registerName == null || conditionRegisterName == null || operator == null)
      throw new IllegalArgumentException();

    this.registerName = registerName.trim();
    this.isIncrement = isIncrement;
    this.increment = increment;
    this.conditionRegisterName = conditionRegisterName.trim();
    this.operator = operator;
    this.conditionValue = conditionValue;

    if (
      this.registerName.isEmpty() || !this.registerName.equals(registerName) ||
      this.conditionRegisterName.isEmpty() || !this.conditionRegisterName.equals(conditionRegisterName)
    ) {
      throw new IllegalArgumentException();
    }
  }

  String getRegisterName() {
    return registerName;
  }

  void executeOn(Map<String, Integer> registers) {
    if (operator.apply(registers.getOrDefault(conditionRegisterName, 0), conditionValue))
      registers.merge(registerName, isIncrement ? increment : -increment, (existing, inc) -> existing + inc);
  }

  @Override
  public String toString() {
    return String.format(
      "%s %s %d if %s %s %d",
      registerName,
      isIncrement ? "inc" : "dec",
      increment,
      conditionRegisterName,
      operator,
      conditionValue
    );
  }

  static Optional<Instruction> tryParseLine(String line) {
    Matcher matcher = INSTRUCTION_PATTERN.matcher(line);
    if (!matcher.matches())
      return Optional.empty();

    String registerName = matcher.group("register");
    boolean isIncrement = matcher.group("incOrDec").equals("inc");
    int increment = Integer.parseInt(matcher.group("increment"));
    String conditionRegisterName = matcher.group("conditionRegister");
    RelationalOperator operator = RelationalOperator.parse(matcher.group("operator"));
    int conditionValue = Integer.parseInt(matcher.group("conditionValue"));

    return Optional.of(new Instruction(registerName, isIncrement, increment, conditionRegisterName, operator, conditionValue));
  }
}