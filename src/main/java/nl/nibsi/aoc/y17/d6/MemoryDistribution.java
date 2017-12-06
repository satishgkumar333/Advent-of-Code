package nl.nibsi.aoc.y17.d6;

import java.util.*;

import static java.util.Arrays.*;

final class MemoryDistribution {

  private final int[] banks;

  MemoryDistribution(Collection<Integer> banks) {
    this.banks = banks.stream().mapToInt(Integer::intValue).toArray();
    validateBanks();
  }

  MemoryDistribution(int... banks) {
    this.banks = Arrays.copyOf(banks, banks.length);
    validateBanks();
  }

  private void validateBanks() {
    if (banks.length == 0)
      throw new IllegalArgumentException();

    if (stream(banks).anyMatch(blockCount -> blockCount < 0))
      throw new IllegalArgumentException();
  }

  private void validateBankIndex(int index) {
    if (index < 0 || index >= banks.length)
      throw new IndexOutOfBoundsException();
  }

  int numberOfBanks() {
    return banks.length;
  }

  int getBlockCount(int bankIndex) {
    validateBankIndex(bankIndex);
    return banks[bankIndex];
  }
  
  int findIndexOfGreatestBank() {
    int greatestBankIndex = -1;
    int greatestBlockCount = -1;
      
    for (int i = 0; i < banks.length; i++) {
      int blockCount = banks[i];
      if (blockCount > greatestBlockCount) {
        greatestBankIndex = i;
        greatestBlockCount = blockCount;
      }
    }
    
    return greatestBankIndex;
  }

  MemoryDistribution redistribute(int bankIndex) {
    int blockCount = getBlockCount(bankIndex);
    int[] banks = Arrays.copyOf(this.banks, this.banks.length);
    banks[bankIndex] = 0;

    while (blockCount > 0) {
      bankIndex++;
      bankIndex %= banks.length;
      banks[bankIndex]++;
      blockCount--;
    }

    return new MemoryDistribution(banks);
  }

  MemoryDistribution cycle() {
    return redistribute(findIndexOfGreatestBank());
  }

  RedistributionPeriod findRedistributionPeriod() {
    Set<MemoryDistribution> distributions = new LinkedHashSet<>();

    MemoryDistribution lastDistribution;
    for (
      lastDistribution = this;
      !distributions.contains(lastDistribution);
      lastDistribution = lastDistribution.cycle()
    ) {
      distributions.add(lastDistribution);
    }

    int cycleCountFromOriginal = -1;
    MemoryDistribution firstOfPeriod = null;

    for (
      Iterator<MemoryDistribution> iterator = distributions.iterator();
      !lastDistribution.equals(firstOfPeriod);
      firstOfPeriod = iterator.next()
    ) {
      cycleCountFromOriginal++;
    }

    int cycleCountOfPeriod = distributions.size() - cycleCountFromOriginal;

    return new RedistributionPeriod(this, firstOfPeriod, cycleCountFromOriginal, cycleCountOfPeriod);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof MemoryDistribution))
      return false;

    MemoryDistribution other = (MemoryDistribution) obj;

    return Arrays.equals(this.banks, other.banks);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(banks);
  }

  @Override
  public String toString() {
    return Arrays.toString(banks);
  }

  private static final class RedistributionPeriod {

    private final MemoryDistribution original, firstOfPeriod;
    private final int cycleCountFromOriginal, cycleCountOfPeriod;

    private RedistributionPeriod(
      MemoryDistribution original,
      MemoryDistribution firstOfPeriod,
      int cycleCountFromOriginal,
      int cycleCountOfPeriod
    ) {
      this.original = original;
      this.firstOfPeriod = firstOfPeriod;
      this.cycleCountFromOriginal = cycleCountFromOriginal;
      this.cycleCountOfPeriod = cycleCountOfPeriod;
    }

    MemoryDistribution original() {
      return original;
    }

    MemoryDistribution firstOfPeriod() {
      return firstOfPeriod;
    }

    int cycleCountBetweenOriginalAndFirstOfPeriod() {
      return cycleCountFromOriginal;
    }

    int cycleCountOfPeriod() {
      return cycleCountOfPeriod;
    }
  }

  public static void main(String... args) {
    MemoryDistribution input = new MemoryDistribution(4, 1, 15, 12, 0, 9, 9, 5, 5, 8, 7, 3, 14, 5, 12, 3);
    RedistributionPeriod period = input.findRedistributionPeriod();
    System.out.println(period.cycleCountOfPeriod());
  }
}