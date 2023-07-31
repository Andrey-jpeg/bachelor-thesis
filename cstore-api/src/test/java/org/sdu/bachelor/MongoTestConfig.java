package org.sdu.bachelor;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Configuration
@Profile({"test-it"})
public class MongoTestConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.host}")
    private String host;

    @Value("${spring.data.mongodb.port}")
    private int port;

    private final List<Converter<?, ?>> converters = new ArrayList<Converter<?, ?>>();

    @Override
    public MongoCustomConversions customConversions() {
        converters.add(new MongoTestConfig.ZonedDateTimeReadConverter());
        converters.add(new MongoTestConfig.ZonedDateTimeWriteConverter());
        return new MongoCustomConversions(converters);
    }

    @Override
    @Bean
    public MongoClient mongoClient() {
        ServerAddress serverAddress = new ServerAddress(host, port);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyToSslSettings(builder -> builder.enabled(false))
                .applyToClusterSettings(builder -> builder.hosts(Arrays.asList(serverAddress)))
                .build();

        return MongoClients.create(settings);
    }


    private static class ZonedDateTimeReadConverter implements Converter<Date, ZonedDateTime> {
        @Override
        public ZonedDateTime convert(Date date) {
            return date.toInstant().atZone(ZoneOffset.UTC);
        }
    }

    private static class ZonedDateTimeWriteConverter implements Converter<ZonedDateTime, Date> {
        @Override
        public Date convert(ZonedDateTime zonedDateTime) {
            return Date.from(zonedDateTime.toInstant());
        }
    }

    @Override
    protected String getDatabaseName() {
        return "cstore";
    }
}
