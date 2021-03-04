package de.blackforestsolutions.dravelopstestsoftware.configuration.converter;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class DistanceConverter implements Converter<String, Distance> {

    @Override
    public Distance convert(@NonNull String distanceInKilometers) {
        return new Distance(Double.parseDouble(distanceInKilometers), Metrics.KILOMETERS);
    }
}
