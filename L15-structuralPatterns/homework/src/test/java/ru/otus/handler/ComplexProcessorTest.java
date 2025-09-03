package ru.otus.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.listener.Listener;
import ru.otus.model.Message;
import ru.otus.processor.Processor;
import ru.otus.processor.ProcessorSwapField11And12;
import ru.otus.processor.ProcessorThrowOnEvenSecond;

class ComplexProcessorTest {

    @Test
    @DisplayName("Тестируем вызовы процессоров")
    void handleProcessorsTest() {
        // given
        var message = new Message.Builder(1L).field7("field7").build();

        var processor1 = mock(Processor.class);
        when(processor1.process(message)).thenReturn(message);

        var processor2 = mock(Processor.class);
        when(processor2.process(message)).thenReturn(message);

        var processors = List.of(processor1, processor2);

        var complexProcessor = new ComplexProcessor(processors, (ex) -> {});

        // when
        var result = complexProcessor.handle(message);

        // then
        verify(processor1).process(message);
        verify(processor2).process(message);
        assertThat(result).isEqualTo(message);
    }

    @Test
    @DisplayName("Тестируем обработку исключения")
    void handleExceptionTest() {
        // given
        var message = new Message.Builder(1L).field8("field8").build();

        var processor1 = mock(Processor.class);
        when(processor1.process(message)).thenThrow(new RuntimeException("Test Exception"));

        var processor2 = mock(Processor.class);
        when(processor2.process(message)).thenReturn(message);

        var processors = List.of(processor1, processor2);

        var complexProcessor = new ComplexProcessor(processors, (ex) -> {
            throw new TestException(ex.getMessage());
        });

        // when
        assertThatExceptionOfType(TestException.class).isThrownBy(() -> complexProcessor.handle(message));

        // then
        verify(processor1, times(1)).process(message);
        verify(processor2, never()).process(message);
    }

    @Test
    @DisplayName("Тестируем уведомления")
    void notifyTest() {
        // given
        var message = new Message.Builder(1L).field9("field9").build();

        var listener = mock(Listener.class);

        var complexProcessor = new ComplexProcessor(new ArrayList<>(), (ex) -> {});

        complexProcessor.addListener(listener);

        // when
        complexProcessor.handle(message);
        complexProcessor.removeListener(listener);
        complexProcessor.handle(message);

        // then
        verify(listener, times(1)).onUpdated(message);
    }

    private static class TestException extends RuntimeException {
        public TestException(String message) {
            super(message);
        }
    }

    @Test
    @DisplayName("Тестируем свайп полей 11 и 12")
    void swap11and12() {
        var msg = new Message.Builder(1L).field11("LEFT").field12("RIGHT").build();

        var swapped = new ProcessorSwapField11And12().process(msg);

        assertEquals("RIGHT", swapped.getField11());
        assertEquals("LEFT", swapped.getField12());
        // и исходное сообщение не изменилось (иммутабельность)
        assertEquals("LEFT", msg.getField11());
        assertEquals("RIGHT", msg.getField12());
    }

    @Test
    @DisplayName("Тестируем, что выбрасывается исплючение в четную секунду")
    void throwsOnEvenSecond() {
        Supplier<LocalDateTime> even = () -> LocalDateTime.of(2025, 1, 1, 0, 0, 2);
        var p = new ProcessorThrowOnEvenSecond(even);

        var msg = new Message.Builder(1L).build();
        assertThrows(ProcessorThrowOnEvenSecond.EvenSecondException.class, () -> p.process(msg));
    }

    @Test
    @DisplayName("Тестируем, что не выбрасывается исплючение в нечетную секунду")
    void passesOnOddSecond() {
        Supplier<LocalDateTime> odd = () -> LocalDateTime.of(2025, 1, 1, 0, 0, 3);
        var p = new ProcessorThrowOnEvenSecond(odd);

        var msg = new Message.Builder(1L).build();
        var res = p.process(msg);

        assertSame(msg, res, "Message should pass through unchanged on odd second");
    }
}
