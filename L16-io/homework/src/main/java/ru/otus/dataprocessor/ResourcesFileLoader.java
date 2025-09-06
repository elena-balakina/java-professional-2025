package ru.otus.dataprocessor;

import jakarta.json.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.model.Measurement;

public class ResourcesFileLoader implements Loader {

    private static final Logger logger = LoggerFactory.getLogger(ResourcesFileLoader.class);
    private final String fileName;

    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<Measurement> load() {
        try (var jsonReader =
                Json.createReader(ResourcesFileLoader.class.getClassLoader().getResourceAsStream(fileName))) {
            JsonArray array = jsonReader.readArray();
            logger.info("Loaded {} items from {}", array.size(), fileName);

            List<Measurement> result = new ArrayList<>(array.size());
            for (JsonValue v : array) {
                JsonObject o = v.asJsonObject();
                result.add(new Measurement(
                        o.getString("name"), o.getJsonNumber("value").doubleValue()));
            }
            return result;
        } catch (Exception e) {
            logger.error("Failed to read {}", fileName, e);
            throw new FileProcessException(e);
        }
    }
}
