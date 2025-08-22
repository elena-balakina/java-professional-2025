package ru.otus.processor;

import ru.otus.model.Message;

public class ProcessorSwapField11And12 implements Processor {

    @Override
    public Message process(Message message) {
        var f11 = message.getField11();
        var f12 = message.getField12();

        return message.toBuilder().field11(f12).field12(f11).build();
    }
}
