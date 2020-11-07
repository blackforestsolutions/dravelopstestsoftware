package de.blackforestsolutions.dravelopstestsoftware.polygonservice;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TravelPointControllerConfiguration {

    /**
     * This section configures the polygon request token from application-int-<customer>.properties file
     */
    @Bean
    @ConfigurationProperties(prefix = "test.apitokens.polygon")
    public ApiToken.ApiTokenBuilder polygonApiToken() {
        return new ApiToken.ApiTokenBuilder();
    }

    /**
     * This section configures the baseUrl for different stages like dev, prod etc.
     */
    @Value("${polygon.protocol}")
    private String polygonProtocol;
    @Value("${polygon.host}")
    private String polygonHost;
    @Value("${polygon.port}")
    private String polygonPort;
    @Value("${polygon.travelpoint.controller.path}")
    private String polygonTravelPointControllerPath;

    @Bean
    public String travelPointControllerUrl() {
        return ""
                .concat(polygonProtocol)
                .concat("://")
                .concat(polygonHost)
                .concat(":")
                .concat(polygonPort)
                .concat("/")
                .concat(polygonTravelPointControllerPath);
    }

}
