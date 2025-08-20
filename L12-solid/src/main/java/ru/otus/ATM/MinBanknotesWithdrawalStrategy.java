package ru.otus.ATM;

import java.util.*;

public class MinBanknotesWithdrawalStrategy implements CashWithdrawalStrategy {

    @Override
    public Map<Nominal, Integer> withdraw(int amount, List<BanknoteCell> cells) {
        // Сортируем ячейки по убыванию номинала, чтобы использовать крупные купюры первыми
        List<BanknoteCell> sortedCells = new ArrayList<>(cells);
        sortedCells.sort(Comparator.comparingInt(BanknoteCell::getNominalValue).reversed());

        Map<Nominal, Integer> result = new LinkedHashMap<>();
        int remaining = amount;

        for (BanknoteCell cell : sortedCells) {
            Nominal nominal = cell.getNominal();
            int nominalValue = nominal.getValue();
            int availableCount = cell.getCount();

            int needed = remaining / nominalValue;
            int toUse = Math.min(needed, availableCount);

            if (toUse > 0) {
                result.put(nominal, toUse);
                remaining -= toUse * nominalValue;
            }

            if (remaining == 0) {
                break;
            }
        }

        if (remaining > 0) {
            throw new IllegalArgumentException("Cannot withdraw requested amount: " + amount);
        }

        return result;
    }
}
