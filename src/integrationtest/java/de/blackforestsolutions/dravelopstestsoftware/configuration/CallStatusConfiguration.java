package de.blackforestsolutions.dravelopstestsoftware.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CallStatusConfiguration {

    /**
     * This section configured the call status url for testsoftware
     */
    @Value("${testsoftware.protocol}")
    private String protocol;
    @Value("${testsoftware.host}")
    private String host;
    @Value("${testsoftware.port}")
    private String port;
    @Value("${testsoftware.get.test.path}")
    private String path;

    @Bean
    public String testsoftwareCallStatusUrl() {
        return ""
                .concat(protocol)
                .concat("://")
                .concat(host)
                .concat(":")
                .concat(port)
                .concat("/")
                .concat(path);
    }
}
