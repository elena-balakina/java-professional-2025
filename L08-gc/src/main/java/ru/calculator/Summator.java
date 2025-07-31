package ru.calculator;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class Summator {
    private int sum = 0;
    private int prevValue = 0;
    private int prevPrevValue = 0;
    private int sumLastThreeValues = 0;
    private int someValue = 0;
    // !!! эта коллекция должна остаться. Заменять ее на счетчик нельзя.
    private final List<Data> listValues = new ArrayList<>();

    private final SecureRandom random;

    public Summator(SecureRandom random) {
        this.random = random;
    }

    // !!! сигнатуру метода менять нельзя
    public void calc(Data data) {
        listValues.add(data);
        if (listValues.size() % 100_000 == 0) {
            listValues.clear();
        }

        int value = data.getValue();
        int rnd = random.nextInt();

        sum += value + rnd;
        sumLastThreeValues = value + prevValue + prevPrevValue;
        prevPrevValue = prevValue;
        prevValue = value;
        int base = (sumLastThreeValues * sumLastThreeValues / (value + 1) - sum);

        for (int idx = 0; idx < 3; idx++) {
            someValue += base;
            someValue = Math.abs(someValue) + listValues.size();
        }
    }

    public int getSum() {
        return sum;
    }

    public int getPrevValue() {
        return prevValue;
    }

    public int getPrevPrevValue() {
        return prevPrevValue;
    }

    public int getSumLastThreeValues() {
        return sumLastThreeValues;
    }

    public int getSomeValue() {
        return someValue;
    }
}
