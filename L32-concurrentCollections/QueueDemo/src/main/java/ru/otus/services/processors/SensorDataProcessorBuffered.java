package ru.otus.services.processors;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.api.SensorDataProcessor;
import ru.otus.api.model.SensorData;
import ru.otus.lib.SensorDataBufferedWriter;

/**
 * Buffered data processor
 * Collect data and call method flush()
 */
@SuppressWarnings({"java:S1068", "java:S125"})
public class SensorDataProcessorBuffered implements SensorDataProcessor {
    private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorBuffered.class);

    private final int bufferSize;
    private final SensorDataBufferedWriter writer;

    private List<SensorData> dataBuffer = new ArrayList<>();
    private final Object lock = new Object();

    public SensorDataProcessorBuffered(int bufferSize, SensorDataBufferedWriter writer) {
        this.bufferSize = bufferSize;
        this.writer = writer;
    }

    @Override
    public void process(SensorData data) {
        boolean needFlush = false;
        synchronized (lock) {
            dataBuffer.add(data);
            if (dataBuffer.size() >= bufferSize) {
                // check limit and call method flush()
                needFlush = true;
            }
        }
        if (needFlush) {
            flush();
        }
    }

    public void flush() {
        List<SensorData> toWrite;
        synchronized (lock) {
            if (dataBuffer.isEmpty()) {
                return;
            }
            var sorted = new ArrayList<>(dataBuffer);
            sorted.sort(Comparator.comparing(SensorData::getMeasurementTime));

            int endExclusive = Math.min(bufferSize, sorted.size());
            toWrite = new ArrayList<>(sorted.subList(0, endExclusive));

            dataBuffer = new ArrayList<>(sorted.subList(endExclusive, sorted.size()));
        }

        try {
            if (!toWrite.isEmpty()) {
                writer.writeBufferedData(toWrite);
            }
        } catch (Exception e) {
            log.error("Ошибка в процессе записи буфера", e);
        }
    }

    @Override
    public void onProcessingEnd() {
        flush();
    }
}
