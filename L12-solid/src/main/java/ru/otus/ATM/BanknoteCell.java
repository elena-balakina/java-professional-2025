package ru.otus.ATM;

public class BanknoteCell {

    private final Banknote banknote;
    private int count;

    public BanknoteCell(Banknote banknote) {
        this.banknote = banknote;
        this.count = 0;
    }

    public Banknote getBanknote() {
        return banknote;
    }

    public int getBanknoteNominal() {
        return banknote.getValue();
    }

    public int getCount() {
        return count;
    }

    public void addCount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count cannot be negative");
        }
        this.count += count;
    }

    public void removeCount(int count) {
        if (count > this.count) {
            throw new IllegalArgumentException("Not enough banknotes in cell for nominal: " + banknote.getValue());
        }
        this.count -= count;
    }

    @Override
    public String toString() {
        return "Banknote cell{" + banknote.getValue() + "₽ x " + count + "}";
    }
}
