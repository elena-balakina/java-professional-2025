package ru.otus.ATM;

public enum Banknote {
    RUB_100(100),
    RUB_500(500),
    RUB_1000(1000),
    RUB_2000(2000),
    RUB_5000(5000);

    private final int value;

    Banknote(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Banknote fromValue(int value) {
        for (Banknote b : values()) {
            if (b.getValue() == value) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unsupported banknote value: " + value);
    }
}
