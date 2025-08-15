package ru.otus.ATM;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Example 1
        List<Nominal> banknotes = List.of(Nominal.RUB_100, Nominal.RUB_500, Nominal.RUB_1000);
        Atm atm = new Atm(banknotes, new MinBanknotesWithdrawalStrategy());
        atm.deposit(Nominal.RUB_1000, 1);
        atm.deposit(Nominal.RUB_500, 2);
        atm.deposit(Nominal.RUB_100, 10);
        System.out.println("Current balance = " + atm.getBalance()); // 3_000

        var withdrawalResult = atm.withdraw(1600); // выдаст 1x1000 + 1x500 + 1x100
        System.out.println("Withdrawal result: ");
        for (Map.Entry<Nominal, Integer> entry : withdrawalResult.entrySet()) {
            System.out.println("Nominal " + entry.getKey().getValue() + ", withdrawal count " + entry.getValue());
        }
        System.out.println("Balance after withdraw = " + atm.getBalance()); // 1_400

        // Example 2
        List<Nominal> banknotes2 = List.of(Nominal.RUB_1000, Nominal.RUB_2000, Nominal.RUB_5000);
        Atm atm2 = new Atm(banknotes2, new MinBanknotesWithdrawalStrategy());
        atm2.deposit(Nominal.RUB_5000, 2);
        atm2.deposit(Nominal.RUB_2000, 5);
        atm2.deposit(Nominal.RUB_1000, 10);
        System.out.println("Current balance = " + atm2.getBalance()); // 30_000

        var withdrawalResult2 = atm2.withdraw(25000); // выдаст 2x5000 + 5x2000 + 5x1000
        System.out.println("Withdrawal result: ");
        for (Map.Entry<Nominal, Integer> entry : withdrawalResult2.entrySet()) {
            System.out.println("Nominal " + entry.getKey().getValue() + ", withdrawal count " + entry.getValue());
        }
        System.out.println("Balance after withdraw = " + atm2.getBalance()); // 5_000
    }
}
