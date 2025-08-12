package ru.otus.ATM;

import java.util.List;
import java.util.Map;

interface CashWithdrawalStrategy {

    /**
     * @param amount сумма к выдаче
     * @param cells список ячеек с купюрами
     * @return map: key — номинал, value — количество купюр
     * @throws IllegalArgumentException если сумму выдать невозможно
     */
    Map<Integer, Integer> withdraw(int amount, List<BanknoteCell> cells);
}
