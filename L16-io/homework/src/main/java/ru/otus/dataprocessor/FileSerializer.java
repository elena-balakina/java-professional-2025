package ru.otus.dataprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSerializer implements Serializer {

    private static final Logger logger = LoggerFactory.getLogger(FileSerializer.class);
    private final String fileName;
    private final ObjectMapper mapper;

    public FileSerializer(String fileName, ObjectMapper mapper) {
        this.fileName = fileName;
        this.mapper = mapper;
    }

    @Override
    public void serialize(Map<String, Double> data) {
        try {
            mapper.writeValue(new File(fileName), data);
            logger.info("Data written to {}", fileName);
        } catch (IOException e) {
            logger.error("Failed to write to the file {}", fileName, e);
            throw new FileProcessException(e);
        }
    }
}
