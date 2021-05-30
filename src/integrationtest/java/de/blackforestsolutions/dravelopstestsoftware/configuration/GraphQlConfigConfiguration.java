package de.blackforestsolutions.dravelopstestsoftware.configuration;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class GraphQlConfigConfiguration {

    @Value("${configbackend.protocol}")
    private String configbackendProtocol;
    @Value("${configbackend.host}")
    private String configbackendHost;
    @Value("${configbackend.port}")
    private String configbackendPort;
    @Value("${configbackend.graphqlconfig.path}")
    private String graphQlConfigBackendPath;

    @Bean
    public String graphQlConfigBackendUrl() {
        return ""
                .concat(configbackendProtocol)
                .concat("://")
                .concat(configbackendHost)
                .concat(":")
                .concat(configbackendPort)
                .concat("/")
                .concat(graphQlConfigBackendPath);
    }
}
