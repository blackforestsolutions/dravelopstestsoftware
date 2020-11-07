package de.blackforestsolutions.dravelopstestsoftware.testutil;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
@ConfigurationPropertiesBinding
public class ZonedDateTimeConverter implements Converter<String, ZonedDateTime> {

    @Override
    public ZonedDateTime convert(@NonNull String date) {
        return ZonedDateTime.parse(date);
    }
}
