package de.blackforestsolutions.dravelopstestsoftware.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;

import java.time.ZonedDateTime;
import java.util.Locale;

@TestConfiguration
public class ApiTokenConfiguration {

    @Value("${graphql.playground.tabs[0].variables.departureLatitude}")
    private Double departureLatitude;
    @Value("${graphql.playground.tabs[0].variables.departureLongitude}")
    private Double departureLongitude;
    @Value("${graphql.playground.tabs[0].variables.arrivalLatitude}")
    private Double arrivalLatitude;
    @Value("${graphql.playground.tabs[0].variables.arrivalLongitude}")
    private Double arrivalLongitude;
    @Value("${graphql.playground.tabs[0].variables.dateTime}")
    private String dateTime;
    @Value("${graphql.playground.tabs[0].variables.isArrivalDateTime}")
    private Boolean isArrivalDateTime;
    @Value("${graphql.playground.tabs[0].variables.language}")
    private Locale language;
    @Value("${graphql.playground.tabs[2].variables.text}")
    private String text;
    @Value("${graphql.playground.tabs[3].variables.radiusInKilometers}")
    private Double radiusInKilometers;


    @Bean
    public ApiToken testApiToken() {
        return new ApiToken.ApiTokenBuilder()
                .setDepartureCoordinate(new Point.PointBuilder(departureLongitude, departureLatitude).build())
                .setArrivalCoordinate(new Point.PointBuilder(arrivalLongitude, arrivalLatitude).build())
                .setDateTime(ZonedDateTime.parse(dateTime))
                .setIsArrivalDateTime(isArrivalDateTime)
                .setLanguage(language)
                .setDeparture(text)
                .setRadiusInKilometers(new Distance(radiusInKilometers, Metrics.KILOMETERS))
                .build();
    }


}
