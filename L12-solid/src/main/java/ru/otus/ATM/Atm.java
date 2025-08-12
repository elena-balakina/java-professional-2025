package ru.otus.ATM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Atm {

    private final List<BanknoteCell> cells = new ArrayList<>();
    private final CashWithdrawalStrategy withdrawalStrategy;

    public Atm(List<Banknote> banknotes, CashWithdrawalStrategy withdrawalStrategy) {
        this.withdrawalStrategy = withdrawalStrategy;
        for (Banknote banknote : banknotes) {
            cells.add(new BanknoteCell(banknote));
        }
    }

    public List<BanknoteCell> getCells() {
        return Collections.unmodifiableList(cells);
    }

    public void deposit(int nominal, int count) {
        BanknoteCell cell = findCell(nominal);
        if (cell == null) {
            throw new IllegalArgumentException("Nomial : " + nominal + " is unsupported by current ATM");
        }
        cell.addCount(count);
    }

    public Map<Integer, Integer> withdraw(int sum) {
        // key - nominal, value - count to withdraw
        Map<Integer, Integer> withdrawalMap = withdrawalStrategy.withdraw(sum, cells);
        for (Map.Entry<Integer, Integer> entry : withdrawalMap.entrySet()) {
            BanknoteCell cell = findCell(entry.getKey());
            if (cell != null) {
                cell.removeCount(entry.getValue());
            }
        }
        return withdrawalMap;
    }

    public int getBalance() {
        return cells.stream()
                .mapToInt(cell -> cell.getBanknoteNominal() * cell.getCount())
                .sum();
    }

    private BanknoteCell findCell(int nominal) {
        return cells.stream()
                .filter(c -> c.getBanknoteNominal() == nominal)
                .findFirst()
                .orElse(null);
    }
}
