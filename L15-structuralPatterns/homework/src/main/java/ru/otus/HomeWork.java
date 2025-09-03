package ru.otus;

import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.handler.ComplexProcessor;
import ru.otus.listener.ListenerPrinterConsole;
import ru.otus.listener.homework.HistoryListener;
import ru.otus.model.Message;
import ru.otus.model.ObjectForMessage;
import ru.otus.processor.*;

public class HomeWork {

    /*
    Реализовать to do:
      1. Добавить поля field11 - field13 (для field13 используйте класс ObjectForMessage)
      2. Сделать процессор, который поменяет местами значения field11 и field12
      3. Сделать процессор, который будет выбрасывать исключение в четную секунду (сделайте тест с гарантированным результатом)
            Секунда должна определяьться во время выполнения.
            Тест - важная часть задания
            Обязательно посмотрите пример к паттерну Мементо!
      4. Сделать Listener для ведения истории (подумайте, как сделать, чтобы сообщения не портились)
         Уже есть заготовка - класс HistoryListener, надо сделать его реализацию
         Для него уже есть тест, убедитесь, что тест проходит
    */

    private static final Logger logger = LoggerFactory.getLogger(HomeWork.class);

    public static void main(String[] args) {
        var processors = List.of(
                new ProcessorConcatFields(),
                new ProcessorSwapField11And12(),
                new LoggerProcessor(new ProcessorUpperField10()),
                new ProcessorThrowOnEvenSecond());

        var complexProcessor = new ComplexProcessor(processors, ex -> logger.error("Processing error", ex));
        var listenerPrinter = new ListenerPrinterConsole();
        complexProcessor.addListener(listenerPrinter);

        var historyListener = new HistoryListener();
        complexProcessor.addListener(historyListener);

        var object13 = new ObjectForMessage();
        object13.setData(Arrays.asList("a", "b", "c"));
        var message = new Message.Builder(1L)
                .field1("field1")
                .field2("field2")
                .field3("field3")
                .field6("field6")
                .field10("field10")
                .field11("LEFT")
                .field12("RIGHT")
                .field13(object13)
                .build();

        logger.info("original: {}", message);

        var result = complexProcessor.handle(message);
        logger.info("result:{}", result);

        var snapshot = historyListener.findMessageById(message.getId());
        snapshot.ifPresent(s -> logger.info("history snapshot: {}", s));

        complexProcessor.removeListener(listenerPrinter);
        complexProcessor.removeListener(historyListener);
    }
}
