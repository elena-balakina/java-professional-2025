package ru.otus.ATM;

public class BanknoteCell {

    private final Nominal nominal;
    private int count;

    public BanknoteCell(Nominal banknote) {
        this.nominal = banknote;
        this.count = 0;
    }

    public Nominal getNominal() {
        return nominal;
    }

    public int getNominalValue() {
        return nominal.getValue();
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
            throw new IllegalArgumentException("Not enough banknotes in cell for nominal: " + nominal.getValue());
        }
        this.count -= count;
    }

    @Override
    public String toString() {
        return "Banknote cell{" + nominal.getValue() + "₽ x " + count + "}";
    }
}
