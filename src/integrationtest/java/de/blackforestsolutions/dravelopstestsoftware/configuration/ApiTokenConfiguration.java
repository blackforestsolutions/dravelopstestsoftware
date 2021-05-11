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

    /**
     * This section configures the apiToken for journey requests
     */
    @Value("${graphql.playground.tabs.JOURNEY_QUERY.variables.departureLatitude}")
    private Double departureLatitude;
    @Value("${graphql.playground.tabs.JOURNEY_QUERY.variables.departureLongitude}")
    private Double departureLongitude;
    @Value("${graphql.playground.tabs.JOURNEY_QUERY.variables.arrivalLatitude}")
    private Double arrivalLatitude;
    @Value("${graphql.playground.tabs.JOURNEY_QUERY.variables.arrivalLongitude}")
    private Double arrivalLongitude;
    @Value("${graphql.playground.tabs.JOURNEY_QUERY.variables.dateTime}")
    private String dateTime;
    @Value("${graphql.playground.tabs.JOURNEY_QUERY.variables.isArrivalDateTime}")
    private Boolean isArrivalDateTime;
    @Value("${graphql.playground.tabs.JOURNEY_QUERY.variables.language}")
    private Locale language;

    @Bean
    public ApiToken journeyUserRequestApiToken() {
        ApiToken apiToken = new ApiToken();

        apiToken.setDepartureCoordinate(new Point.PointBuilder(departureLongitude, departureLatitude).build());
        apiToken.setArrivalCoordinate(new Point.PointBuilder(arrivalLongitude, arrivalLatitude).build());
        apiToken.setDateTime(ZonedDateTime.parse(dateTime));
        apiToken.setIsArrivalDateTime(isArrivalDateTime);
        apiToken.setLanguage(language);

        return apiToken;
    }

    /*
    * This section configures the apiToken for autocompletion requests
     */
    @Value("${graphql.playground.tabs.ADDRESS_AUTOCOMPLETION.variables.text}")
    private String text;
    @Value("${graphql.playground.tabs.ADDRESS_AUTOCOMPLETION.variables.language}")
    private Locale autocompleteLanguage;

    @Bean
    public ApiToken autocompleteUserRequestApiToken() {
        ApiToken apiToken = new ApiToken();

        apiToken.setDeparture(text);
        apiToken.setLanguage(autocompleteLanguage);

        return apiToken;
    }

    /*
     * This section configures the apiToken for nearest addresses requests
     */
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.variables.longitude}")
    private Double nearestAddressesLongitude;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.variables.latitude}")
    private Double nearestAddressesLatitude;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.variables.radiusInKilometers}")
    private Double nearestAddressesRadiusInKilometers;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.variables.language}")
    private Locale nearestAddressesLanguage;

    @Bean
    public ApiToken nearestAddressesUserRequestApiToken() {
        ApiToken apiToken = new ApiToken();

        apiToken.setArrivalCoordinate(new Point.PointBuilder(nearestAddressesLongitude, nearestAddressesLatitude).build());
        apiToken.setRadiusInKilometers(new Distance(nearestAddressesRadiusInKilometers, Metrics.KILOMETERS));
        apiToken.setLanguage(nearestAddressesLanguage);

        return apiToken;
    }

    /*
     * This section configures the apiToken for nearest stations requests
     */
    @Value("${graphql.playground.tabs.NEAREST_STATIONS.variables.longitude}")
    private Double nearestStationsLongitude;
    @Value("${graphql.playground.tabs.NEAREST_STATIONS.variables.latitude}")
    private Double nearestStationsLatitude;
    @Value("${graphql.playground.tabs.NEAREST_STATIONS.variables.radiusInKilometers}")
    private Double nearestStationsRadiusInKilometers;
    @Value("${graphql.playground.tabs.NEAREST_STATIONS.variables.language}")
    private Locale nearestStationsLanguage;

    @Bean
    public ApiToken nearestStationsUserRequestApiToken() {
        ApiToken apiToken = new ApiToken();

        apiToken.setArrivalCoordinate(new Point.PointBuilder(nearestStationsLongitude, nearestStationsLatitude).build());
        apiToken.setRadiusInKilometers(new Distance(nearestStationsRadiusInKilometers, Metrics.KILOMETERS));
        apiToken.setLanguage(nearestStationsLanguage);

        return apiToken;
    }
}
