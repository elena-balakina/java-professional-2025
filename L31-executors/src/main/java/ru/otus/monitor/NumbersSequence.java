package ru.otus.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumbersSequence {
    private static final Logger logger = LoggerFactory.getLogger(NumbersSequence.class);
    private int currentNumber = 1;
    private int direction = 1; // +1 - going up, -1 - going down
    private String currentThread = "thread1";

    public static void main(String[] args) {
        NumbersSequence numbersSequence = new NumbersSequence();
        new Thread(() -> numbersSequence.action("thread1")).start();
        new Thread(() -> numbersSequence.action("thread2")).start();
    }

    private synchronized void action(String threadName) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                while (!currentThread.equals(threadName)) {
                    this.wait();
                }

                logger.info(threadName + " : " + currentNumber);

                if ("thread1".equals(threadName)) {
                    currentThread = "thread2";
                } else {
                    if (currentNumber == 10) {
                        direction = -1;
                    } else if (currentNumber == 1) {
                        direction = 1;
                    }
                    currentNumber += direction;
                    currentThread = "thread1";
                }

                sleep();
                notifyAll();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void sleep() {
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
