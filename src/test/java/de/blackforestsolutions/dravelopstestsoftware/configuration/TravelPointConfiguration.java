package de.blackforestsolutions.dravelopstestsoftware.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TravelPointConfiguration {

    /**
     * This section configures the travelPoint token from application-int-<customer>.properties file
     */
    @Bean
    @ConfigurationProperties(prefix = "test.apitokens[0]")
    public ApiToken.ApiTokenBuilder travelPointApiToken() {
        return new ApiToken.ApiTokenBuilder();
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
    @Value("${boxservice.travelpoint.controller.path}")
    private String boxServiceTravelPointControllerPath;

    @Bean
    public String travelPointBoxServiceUrl() {
        return ""
                .concat(boxServiceProtocol)
                .concat("://")
                .concat(boxServiceHost)
                .concat(":")
                .concat(boxServicePort)
                .concat("/")
                .concat(boxServiceTravelPointControllerPath);
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
