package com.cirta.soft.kafkaconnect;

import com.cirta.soft.kafkaconnect.model.Weather;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.cirta.soft.kafkaconnect.schema.WeatherAPISchemaFields.DEG;
import static com.cirta.soft.kafkaconnect.schema.WeatherAPISchemaFields.DESCRIPTION;
import static com.cirta.soft.kafkaconnect.schema.WeatherAPISchemaFields.HUMIDITY;
import static com.cirta.soft.kafkaconnect.schema.WeatherAPISchemaFields.ICON;
import static com.cirta.soft.kafkaconnect.schema.WeatherAPISchemaFields.MAIN;
import static com.cirta.soft.kafkaconnect.schema.WeatherAPISchemaFields.PRESSURE;
import static com.cirta.soft.kafkaconnect.schema.WeatherAPISchemaFields.SPEED;
import static com.cirta.soft.kafkaconnect.schema.WeatherAPISchemaFields.TEMP;
import static com.cirta.soft.kafkaconnect.schema.WeatherAPISchemaFields.TEMP_MAX;
import static com.cirta.soft.kafkaconnect.schema.WeatherAPISchemaFields.TEMP_MIN;
import static com.cirta.soft.kafkaconnect.schema.WeatherAPISchemaFields.WEATHER;
import static com.cirta.soft.kafkaconnect.schema.WeatherAPISchemaFields.WIND;
import static com.cirta.soft.kafkaconnect.schema.WeatherAPISchemaFields.ID;
import static com.cirta.soft.kafkaconnect.schema.WeatherAPISchemaFields.NAME;
import static com.cirta.soft.kafkaconnect.schema.WeatherAPISchemas.KEY_SCHEMA;
import static com.cirta.soft.kafkaconnect.schema.WeatherAPISchemas.MAIN_SCHEMA;
import static com.cirta.soft.kafkaconnect.schema.WeatherAPISchemas.VALUE_SCHEMA;
import static com.cirta.soft.kafkaconnect.schema.WeatherAPISchemas.WEATHER_SCHEMA;
import static com.cirta.soft.kafkaconnect.schema.WeatherAPISchemas.WIND_SCHEMA;
import static java.util.stream.Collectors.toList;

public class WeatherAPITask extends SourceTask {

    WeatherAPIConfig config;
    WeatherAPIClient client;
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    @Override
    public void start(Map<String, String> props) {
        config = new WeatherAPIConfig(props);
        client = new WeatherAPIClient(config);
        isRunning.set(true);
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        if(!isRunning.get()) {
            return Collections.emptyList();
        }
        Thread.sleep(config.getPollFrequency());
        return client.getCurrentWeather()
                .stream()
                .map(weather -> new SourceRecord(sourcePartition(weather),
                        sourceOffset(),
                        config.getKafkaTopic(),
                        KEY_SCHEMA, buildKey(weather.getId()),
                        VALUE_SCHEMA, buildValue(weather)))
                .collect(toList());
    }

    private Map<String, ?> sourcePartition(Weather weather) {
        Map<String, String> sourcePartition = new HashMap<>();
        sourcePartition.put("location", weather.getName());
        return sourcePartition;
    }

    private Map<String, ?> sourceOffset() {
        return new HashMap<>();
    }

    private Struct buildKey(Long id) {
        return new Struct(KEY_SCHEMA)
                .put(ID, id);
    }

    private Struct buildValue(Weather weather) {
        return new Struct(VALUE_SCHEMA)
                .put(NAME, weather.getName())
                .put(MAIN, new Struct(MAIN_SCHEMA)
                        .put(TEMP, weather.getMain().getTemp())
                        .put(PRESSURE, weather.getMain().getPressure())
                        .put(HUMIDITY, weather.getMain().getHumidity())
                        .put(TEMP_MIN, weather.getMain().getTempMin())
                        .put(TEMP_MAX, weather.getMain().getTempMax()))
                .put(WIND, new Struct(WIND_SCHEMA)
                        .put(SPEED, weather.getWind().getSpeed())
                        .put(DEG, weather.getWind().getDeg()))
                .put(WEATHER, weather.getWeather()
                        .stream()
                        .map(weatherDetails -> new Struct(WEATHER_SCHEMA)
                                .put(ID, weatherDetails.getId())
                                .put(MAIN, weatherDetails.getMain())
                                .put(DESCRIPTION, weatherDetails.getDescription())
                                .put(ICON, weatherDetails.getIcon()))
                        .collect(toList()));
    }

    @Override
    public void stop() {
        isRunning.set(false);
    }

    @Override
    public String version() {
        return "1.0";
    }
}
