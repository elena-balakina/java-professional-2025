package ru.otus.processor;

import java.time.LocalDateTime;
import java.util.function.Supplier;
import ru.otus.model.Message;

public class ProcessorThrowOnEvenSecond implements Processor {

    private final Supplier<LocalDateTime> timeSource;

    public ProcessorThrowOnEvenSecond() {
        this(LocalDateTime::now);
    }

    public ProcessorThrowOnEvenSecond(Supplier<LocalDateTime> timeSource) {
        this.timeSource = timeSource;
    }

    @Override
    public Message process(Message message) {
        int second = timeSource.get().getSecond();
        if (second % 2 == 0) {
            throw new EvenSecondException("Even second detected: " + second);
        }
        return message;
    }

    public static class EvenSecondException extends RuntimeException {
        public EvenSecondException(String message) {
            super(message);
        }
    }
}
