package de.blackforestsolutions.dravelopstestsoftware.otpmapperservice;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.geo.Point;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getOtpMapperApiToken;

@TestConfiguration
public class JourneyControllerConfiguration {

    /**
     * This section configures the otp request token from application-int-<customer>.properties file
     */
    @Value("#{T(Double).parseDouble('${test.apitokens.otpmapper.departureCoordinateLongitude}')}")
    private double departureCoordinateLongitude;
    @Value("#{T(Double).parseDouble('${test.apitokens.otpmapper.departureCoordinateLatitude}')}")
    private double departureCoordinateLatitude;
    @Value("#{T(Double).parseDouble('${test.apitokens.otpmapper.arrivalCoordinateLongitude}')}")
    private double arrivalCoordinateLongitude;
    @Value("#{T(Double).parseDouble('${test.apitokens.otpmapper.arrivalCoordinateLatitude}')}")
    private double arrivalCoordinateLatitude;

    @Bean
    @ConfigurationProperties(prefix = "test.apitokens.otpmapper")
    public ApiToken.ApiTokenBuilder otpMapperApiToken() {
        return new ApiToken.ApiTokenBuilder(getOtpMapperApiToken())
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
