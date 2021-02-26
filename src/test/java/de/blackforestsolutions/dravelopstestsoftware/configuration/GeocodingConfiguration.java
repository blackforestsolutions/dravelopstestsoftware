package de.blackforestsolutions.dravelopstestsoftware.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class GeocodingConfiguration {

    public static final int MIN_POLYGON_POINTS = 3;
    public static final double MIN_WGS_84_LONGITUDE = -180.0d;
    public static final double MAX_WGS_84_LONGITUDE = 180.0d;
    public static final double MIN_WGS_84_LATITUDE = -90.0d;
    public static final double MAX_WGS_84_LATITUDE = 90.0d;

    /**
     * This section configures the geocoding urls for the stationPersistenceApi
     */
    @Value("${stationpersistence.protocol}")
    private String stationPersistenceProtocol;
    @Value("${stationpersistence.host}")
    private String stationsPersistenceHost;
    @Value("${stationpersistence.port}")
    private String stationsPersistencePort;
    @Value("${stationpersistence.geocoding.controller.polygon.path}")
    private String stationsPersistenceGeocodingControllerPolygon;
    @Value("${stationpersistence.geocoding.controller.box.path}")
    private String stationsPersistenceGeocodingControllerBox;

    @Bean
    public String stationsPersistenceGeocodingPolygonUrl() {
        return ""
                .concat(stationPersistenceProtocol)
                .concat("://")
                .concat(stationsPersistenceHost)
                .concat(":")
                .concat(stationsPersistencePort)
                .concat("/")
                .concat(stationsPersistenceGeocodingControllerPolygon);
    }

    @Bean
    public String stationsPersistenceGeocodingBoxUrl() {
        return ""
                .concat(stationPersistenceProtocol)
                .concat("://")
                .concat(stationsPersistenceHost)
                .concat(":")
                .concat(stationsPersistencePort)
                .concat("/")
                .concat(stationsPersistenceGeocodingControllerBox);
    }
}
