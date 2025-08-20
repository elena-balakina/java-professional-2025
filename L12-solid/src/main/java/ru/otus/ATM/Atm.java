package ru.otus.ATM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Atm {

    private final List<BanknoteCell> cells = new ArrayList<>();
    private final CashWithdrawalStrategy withdrawalStrategy;

    public Atm(List<Nominal> banknotes, CashWithdrawalStrategy withdrawalStrategy) {
        this.withdrawalStrategy = withdrawalStrategy;
        for (Nominal banknote : banknotes) {
            cells.add(new BanknoteCell(banknote));
        }
    }

    public List<BanknoteCell> getCells() {
        return Collections.unmodifiableList(cells);
    }

    public void deposit(Nominal nominal, int count) {
        BanknoteCell cell = findCell(nominal.getValue());
        if (cell == null) {
            throw new IllegalArgumentException("Nomial : " + nominal.getValue() + " is unsupported by current ATM");
        }
        cell.addCount(count);
    }

    public Map<Nominal, Integer> withdraw(int sum) {
        // key - Nominal, value - count to withdraw
        Map<Nominal, Integer> withdrawalMap = withdrawalStrategy.withdraw(sum, cells);
        for (Map.Entry<Nominal, Integer> entry : withdrawalMap.entrySet()) {
            BanknoteCell cell = findCell(entry.getKey().getValue());
            if (cell != null) {
                cell.removeCount(entry.getValue());
            }
        }
        return withdrawalMap;
    }

    public int getBalance() {
        return cells.stream()
                .mapToInt(cell -> cell.getNominalValue() * cell.getCount())
                .sum();
    }

    private BanknoteCell findCell(int nominal) {
        return cells.stream()
                .filter(c -> c.getNominalValue() == nominal)
                .findFirst()
                .orElse(null);
    }
}
