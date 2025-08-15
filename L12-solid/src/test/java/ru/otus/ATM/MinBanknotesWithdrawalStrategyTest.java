package ru.otus.ATM;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MinBanknotesWithdrawalStrategyTest {

    @Test
    @DisplayName("Withdraw with minimum banknotes count test")
    void withdrawMinimumBanknotesTest() {
        List<BanknoteCell> cells = List.of(with(1000, 1), with(500, 2), with(100, 5));

        MinBanknotesWithdrawalStrategy strategy = new MinBanknotesWithdrawalStrategy();
        Map<Nominal, Integer> result = strategy.withdraw(1600, cells);

        assertEquals(3, result.size());
        assertEquals(1, result.get(Nominal.RUB_1000));
        assertEquals(1, result.get(Nominal.RUB_500));
        assertEquals(1, result.get(Nominal.RUB_100));
    }

    @Test
    @DisplayName("Withdraw not possible test")
    void withdrawNotPossibleTest() {
        List<BanknoteCell> cells = List.of(with(1000, 0), with(500, 1), with(100, 1));

        MinBanknotesWithdrawalStrategy strategy = new MinBanknotesWithdrawalStrategy();
        assertThrows(IllegalArgumentException.class, () -> strategy.withdraw(2000, cells));
    }

    private BanknoteCell with(int nominal, int count) {
        Nominal banknote = Nominal.fromValue(nominal);
        BanknoteCell cell = new BanknoteCell(banknote);
        cell.addCount(count);
        return cell;
    }
}
