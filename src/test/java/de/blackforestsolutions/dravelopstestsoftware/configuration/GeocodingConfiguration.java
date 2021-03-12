package de.blackforestsolutions.dravelopstestsoftware.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class GeocodingConfiguration {

    public static final double MIN_DISTANCE_IN_KILOMETERS_TO_POINT = 0.0d;
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
    @Value("${stationpersistence.get.polygon.path}")
    private String stationsPersistencePolygonPath;
    @Value("${stationpersistence.get.box.path}")
    private String stationsPersistenceBoxPath;

    @Bean
    public String stationsPersistenceGeocodingPolygonUrl() {
        return ""
                .concat(stationPersistenceProtocol)
                .concat("://")
                .concat(stationsPersistenceHost)
                .concat(":")
                .concat(stationsPersistencePort)
                .concat("/")
                .concat(stationsPersistencePolygonPath);
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
                .concat(stationsPersistenceBoxPath);
    }
}
