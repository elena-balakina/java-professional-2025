package ru.otus.listener.homework;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import ru.otus.listener.Listener;
import ru.otus.model.Message;
import ru.otus.model.ObjectForMessage;

public class HistoryListener implements Listener, HistoryReader {

    private final Map<Long, Message> history = new ConcurrentHashMap<>();

    @Override
    public void onUpdated(Message msg) {
        history.put(msg.getId(), copyMessage(msg));
    }

    @Override
    public Optional<Message> findMessageById(long id) {
        return Optional.ofNullable(history.get(id)).map(this::copyMessage);
    }

    private Message copyMessage(Message src) {
        ObjectForMessage ofmCopy = null;
        if (src.getField13() != null) {
            ofmCopy = new ObjectForMessage();
            if (src.getField13().getData() != null) {
                ofmCopy.setData(new ArrayList<>(src.getField13().getData()));
            }
        }

        return new Message.Builder(src.getId())
                .field1(src.getField1())
                .field2(src.getField2())
                .field3(src.getField3())
                .field4(src.getField4())
                .field5(src.getField5())
                .field6(src.getField6())
                .field7(src.getField7())
                .field8(src.getField8())
                .field9(src.getField9())
                .field10(src.getField10())
                .field11(src.getField11())
                .field12(src.getField12())
                .field13(ofmCopy)
                .build();
    }
}
