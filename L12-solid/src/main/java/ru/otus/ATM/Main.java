package ru.otus.ATM;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Example 1
        List<Banknote> banknotes = List.of(Banknote.RUB_100, Banknote.RUB_500, Banknote.RUB_1000);
        Atm atm = new Atm(banknotes, new MinBanknotesWithdrawalStrategy());
        atm.deposit(1000, 1);
        atm.deposit(500, 2);
        atm.deposit(100, 10);
        System.out.println("Current balance = " + atm.getBalance()); // 3_000

        var withdrawalResult = atm.withdraw(1600); // выдаст 1x1000 + 1x500 + 1x100
        System.out.println("Withdrawal result: ");
        for (Map.Entry<Integer, Integer> entry : withdrawalResult.entrySet()) {
            System.out.println("Nominal " + entry.getKey() + ", withdrawal count " + entry.getValue());
        }
        System.out.println("Balance after withdraw = " + atm.getBalance()); // 1_400

        // Example 2
        List<Banknote> banknotes2 = List.of(Banknote.RUB_1000, Banknote.RUB_2000, Banknote.RUB_5000);
        Atm atm2 = new Atm(banknotes2, new MinBanknotesWithdrawalStrategy());
        atm2.deposit(5000, 2);
        atm2.deposit(2000, 5);
        atm2.deposit(1000, 10);
        System.out.println("Current balance = " + atm2.getBalance()); // 30_000

        var withdrawalResult2 = atm2.withdraw(25000); // выдаст 2x5000 + 5x2000 + 5x1000
        System.out.println("Withdrawal result: ");
        for (Map.Entry<Integer, Integer> entry : withdrawalResult2.entrySet()) {
            System.out.println("Nominal " + entry.getKey() + ", withdrawal count " + entry.getValue());
        }
        System.out.println("Balance after withdraw = " + atm2.getBalance()); // 5_000
    }
}
