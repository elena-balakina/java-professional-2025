package ru.otus.ATM;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AtmTest {

    private Atm atm;

    @BeforeEach
    void setUp() {
        atm = new Atm(
                List.of(Nominal.RUB_100, Nominal.RUB_500, Nominal.RUB_1000), new MinBanknotesWithdrawalStrategy());

        atm.deposit(Nominal.RUB_100, 10); // 1000
        atm.deposit(Nominal.RUB_500, 5); // 2500
        atm.deposit(Nominal.RUB_1000, 2); // 2000
    }

    @Test
    @DisplayName("Get balance calculation test")
    void getBalanceTest() {
        assertEquals(5500, atm.getBalance());
    }

    @Test
    @DisplayName("Successful withdrawal test")
    public void SuccessfulWithdrawalTest() {
        Map<Nominal, Integer> withdrawn = atm.withdraw(1600);
        // Ожидаем: 1 x 1000, 1 x 500, 1 x 100
        assertEquals(3, withdrawn.size());
        assertEquals(1, withdrawn.get(Nominal.RUB_1000));
        assertEquals(1, withdrawn.get(Nominal.RUB_500));
        assertEquals(1, withdrawn.get(Nominal.RUB_100));
        assertEquals(3900, atm.getBalance()); // 5500 - 1600
    }

    @Test
    @DisplayName("Impossible amount withdraw test")
    void withdrawImpossibleAmountTest() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> atm.withdraw(555));
        assertEquals("Cannot withdraw requested amount: 555", ex.getMessage());
    }

    @Test
    @DisplayName("Impossible to add banknote with not existing nominal test")
    void impossibleNominalTest() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> atm.deposit(Nominal.RUB_5000, 1));
        assertEquals("Nomial : 5000 is unsupported by current ATM", ex.getMessage());
    }
}
