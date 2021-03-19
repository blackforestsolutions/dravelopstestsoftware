package de.blackforestsolutions.dravelopstestsoftware.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class JourneyConfiguration {

    /**
     * This section configures the baseUrl for otpMapperService.
     */
    @Value("${otpmapper.protocol}")
    private String otpMapperProtocol;
    @Value("${otpmapper.host}")
    private String otpMapperHost;
    @Value("${otpmapper.port}")
    private String otpMapperPort;
    @Value("${otpmapper.get.journey.path}")
    private String otpMapperJourneyPath;

    @Bean
    public String journeyOtpMapperUrl() {
        return ""
                .concat(otpMapperProtocol)
                .concat("://")
                .concat(otpMapperHost)
                .concat(":")
                .concat(otpMapperPort)
                .concat("/")
                .concat(otpMapperJourneyPath);
    }

    /**
     * This section configures the baseUrl for the routePersistenceApi.
     */
    @Value("${routepersistence.protocol}")
    private String routePersistenceProtocol;
    @Value("${routepersistence.host}")
    private String routePersistenceHost;
    @Value("${routepersistence.port}")
    private String routePersistencePort;
    @Value("${routepersistence.get.journey.path}")
    private String routePersistenceJourneyPath;

    @Bean
    public String journeyRoutePersistenceUrl() {
        return ""
                .concat(routePersistenceProtocol)
                .concat("://")
                .concat(routePersistenceHost)
                .concat(":")
                .concat(routePersistencePort)
                .concat("/")
                .concat(routePersistenceJourneyPath);
    }
}
