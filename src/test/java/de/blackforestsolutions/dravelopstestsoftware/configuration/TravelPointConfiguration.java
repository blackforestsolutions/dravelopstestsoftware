package de.blackforestsolutions.dravelopstestsoftware.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TravelPointConfiguration {

    /**
     * This section configures the travelPoint token from application-int-<customer>.properties file
     */
    @Value("${test.apitokens[0].arrivalCoordinateLongitude}")
    private Double arrivalCoordinateLongitude;
    @Value("${test.apitokens[0].arrivalCoordinateLatitude}")
    private Double arrivalCoordinateLatitude;

    @Bean
    @ConfigurationProperties(prefix = "test.apitokens[0]")
    public ApiToken.ApiTokenBuilder travelPointApiToken() {
        return new ApiToken.ApiTokenBuilder()
                .setArrivalCoordinate(new Point.PointBuilder(arrivalCoordinateLongitude, arrivalCoordinateLatitude).build());
    }

    /**
     * This section configures the baseUrl for the boxservice.
     */
    @Value("${boxservice.protocol}")
    private String boxServiceProtocol;
    @Value("${boxservice.host}")
    private String boxServiceHost;
    @Value("${boxservice.port}")
    private String boxServicePort;
    @Value("${boxservice.autocomplete.addresses.controller.path}")
    private String boxServiceAutocompleteAddressesControllerPath;
    @Value("${boxservice.nearest.addresses.controller.path}")
    private String boxServiceNearestAddressesControllerPath;

    @Bean
    public String autocompleteAddressesBoxServiceUrl() {
        return ""
                .concat(boxServiceProtocol)
                .concat("://")
                .concat(boxServiceHost)
                .concat(":")
                .concat(boxServicePort)
                .concat("/")
                .concat(boxServiceAutocompleteAddressesControllerPath);
    }

    @Bean
    public String nearestAddressesBoxServiceUrl() {
        return ""
                .concat(boxServiceProtocol)
                .concat("://")
                .concat(boxServiceHost)
                .concat(":")
                .concat(boxServicePort)
                .concat("/")
                .concat(boxServiceNearestAddressesControllerPath);
    }


    /**
     * This section configures the travelPoint Url for the stationPersistenceApi
     */
    @Value("${stationpersistence.protocol}")
    private String stationPersistenceProtocol;
    @Value("${stationpersistence.host}")
    private String stationsPersistenceHost;
    @Value("${stationpersistence.port}")
    private String stationsPersistencePort;
    @Value("${stationpersistence.travelpoint.controller.path}")
    private String stationsPersistenceTravelPointControllerPath;

    @Bean
    public String stationsPersistenceTravelPointUrl() {
        return ""
                .concat(stationPersistenceProtocol)
                .concat("://")
                .concat(stationsPersistenceHost)
                .concat(":")
                .concat(stationsPersistencePort)
                .concat("/")
                .concat(stationsPersistenceTravelPointControllerPath);
    }

}
