package de.blackforestsolutions.dravelopstestsoftware.stationpersistenceservice;

import de.blackforestsolutions.dravelopsdatamodel.Box;
import de.blackforestsolutions.dravelopstestsoftware.configuration.GeocodingConfiguration;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopstestsoftware.configuration.GeocodingConfiguration.*;
import static org.assertj.core.api.Assertions.assertThat;

@Import(GeocodingConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GeocodingControllerTest {

    @Autowired
    private String stationsPersistenceGeocodingPolygonUrl;

    @Autowired
    private String stationsPersistenceGeocodingBoxUrl;

    @Autowired
    private ExchangeStrategies exchangeStrategies;

    @Test
    void test_getOperatingPolygon_returns_a_polygon() {

        Mono<Polygon> result = getOperatingPolygon()
                .expectStatus()
                .isOk()
                .returnResult(Polygon.class)
                .getResponseBody()
                .singleOrEmpty();

        StepVerifier.create(result)
                .assertNext(polygon -> {
                    assertThat(polygon.getExteriorRing().getCoordinates().length).isGreaterThanOrEqualTo(MIN_POLYGON_POINTS);
                    assertThat(polygon.getExteriorRing().getCoordinates()).allMatch(coordinate -> coordinate.getX() >= MIN_WGS_84_LONGITUDE);
                    assertThat(polygon.getExteriorRing().getCoordinates()).allMatch(coordinate -> coordinate.getX() <= MAX_WGS_84_LONGITUDE);
                    assertThat(polygon.getExteriorRing().getCoordinates()).allMatch(coordinate -> coordinate.getY() >= MIN_WGS_84_LATITUDE);
                    assertThat(polygon.getExteriorRing().getCoordinates()).allMatch(coordinate -> coordinate.getY() <= MAX_WGS_84_LATITUDE);
                })
                .verifyComplete();

    }

    @Test
    void test_getOperatingBox_returns_a_box() {

        Mono<Box> result = getOperatingBox()
                .expectStatus()
                .isOk()
                .returnResult(Box.class)
                .getResponseBody()
                .singleOrEmpty();

        StepVerifier.create(result)
                .assertNext(box -> {
                    assertThat(box.getTopLeft()).isNotNull();
                    assertThat(box.getBottomRight()).isNotNull();
                    assertThat(box.getBottomRight().getX()).isGreaterThanOrEqualTo(MIN_WGS_84_LONGITUDE);
                    assertThat(box.getBottomRight().getX()).isLessThanOrEqualTo(MAX_WGS_84_LONGITUDE);
                    assertThat(box.getBottomRight().getY()).isGreaterThanOrEqualTo(MIN_WGS_84_LATITUDE);
                    assertThat(box.getBottomRight().getY()).isLessThanOrEqualTo(MAX_WGS_84_LATITUDE);
                })
                .verifyComplete();

    }

    private WebTestClient.ResponseSpec getOperatingPolygon() {
        return WebTestClient
                .bindToServer()
                .baseUrl(stationsPersistenceGeocodingPolygonUrl)
                .exchangeStrategies(exchangeStrategies)
                .build()
                .get()
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange();
    }


    private WebTestClient.ResponseSpec getOperatingBox() {
        return WebTestClient
                .bindToServer()
                .baseUrl(stationsPersistenceGeocodingBoxUrl)
                .exchangeStrategies(exchangeStrategies)
                .build()
                .get()
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange();
    }


}
