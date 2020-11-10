package de.blackforestsolutions.dravelopstestsoftware.configuration;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.geo.Point;

@TestConfiguration
public class JourneyConfiguration {

    /**
     * This section configures the journey token from application-int-<customer>.properties file
     */
    @Value("${test.apitokens[0].departureCoordinateLongitude}")
    private Double departureCoordinateLongitude;
    @Value("${test.apitokens[0].departureCoordinateLatitude}")
    private Double departureCoordinateLatitude;
    @Value("${test.apitokens[0].arrivalCoordinateLongitude}")
    private Double arrivalCoordinateLongitude;
    @Value("${test.apitokens[0].arrivalCoordinateLatitude}")
    private Double arrivalCoordinateLatitude;

    @Bean
    @ConfigurationProperties(prefix = "test.apitokens[0]")
    public ApiToken.ApiTokenBuilder journeyApiToken() {
        return new ApiToken.ApiTokenBuilder()
                .setDepartureCoordinate(new Point(departureCoordinateLongitude, departureCoordinateLatitude))
                .setArrivalCoordinate(new Point(arrivalCoordinateLongitude, arrivalCoordinateLatitude));
    }

    /**
     * This section configures the baseUrl for different stages like dev, prod etc.
     */
    @Value("${otpmapper.protocol}")
    private String otpMapperProtocol;
    @Value("${otpmapper.host}")
    private String otpMapperHost;
    @Value("${otpmapper.port}")
    private String otpMapperPort;
    @Value("${otpmapper.journey.controller.path}")
    private String otpMapperJourneyControllerPath;

    @Bean
    public String journeyControllerUrl() {
        return ""
                .concat(otpMapperProtocol)
                .concat("://")
                .concat(otpMapperHost)
                .concat(":")
                .concat(otpMapperPort)
                .concat("/")
                .concat(otpMapperJourneyControllerPath);
    }
}
