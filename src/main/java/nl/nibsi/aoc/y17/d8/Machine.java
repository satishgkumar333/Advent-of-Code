package nl.nibsi.aoc.y17.d8;

import java.io.*;
import java.util.*;

import static java.util.stream.Collectors.*;

final class Machine {
  
  private final Map<String, Integer> registers = new HashMap<>();

  private int greatestValue;

  void executeInstruction(Instruction instruction) {
    instruction.executeOn(registers);
    int value = registers.getOrDefault(instruction.getRegisterName(), 0);
    if (value > greatestValue)
      greatestValue = value;
  }

  int currentGreatestValue() {
    return registers.values().stream().mapToInt(Integer::intValue).max().orElse(0);
  }

  int allTimeGreatestValue() {
    return greatestValue;
  }

  static List<Instruction> readInstructionsFromInput() {
    try (
      BufferedReader reader = new BufferedReader(new InputStreamReader(Machine.class.getResourceAsStream("input.txt")));
    ) {
      return reader.lines()
        .map(line -> Instruction.tryParseLine(line).orElseThrow(AssertionError::new))
        .collect(toList());
    }

    catch (IOException ex) {
      throw new AssertionError(ex);
    }
  }

  public static void main(String... args) {
    Machine machine = new Machine();
    readInstructionsFromInput().stream().forEach(machine::executeInstruction);
    System.out.println(machine.currentGreatestValue());
    System.out.println(machine.allTimeGreatestValue());
  }
}