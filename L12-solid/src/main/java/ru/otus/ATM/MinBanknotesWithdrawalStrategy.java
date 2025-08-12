package ru.otus.ATM;

import java.util.*;

public class MinBanknotesWithdrawalStrategy implements CashWithdrawalStrategy {

    @Override
    public Map<Integer, Integer> withdraw(int amount, List<BanknoteCell> cells) {
        // Сортируем ячейки по убыванию номинала, чтобы использовать крупные купюры первыми
        List<BanknoteCell> sortedCells = new ArrayList<>(cells);
        sortedCells.sort(
                Comparator.comparingInt(BanknoteCell::getBanknoteNominal).reversed());

        Map<Integer, Integer> result = new LinkedHashMap<>();
        int remaining = amount;

        for (BanknoteCell cell : sortedCells) {
            int nominal = cell.getBanknoteNominal();
            int availableCount = cell.getCount();

            int needed = remaining / nominal;
            int toUse = Math.min(needed, availableCount);

            if (toUse > 0) {
                result.put(nominal, toUse);
                remaining -= toUse * nominal;
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
