package de.blackforestsolutions.dravelopstestsoftware.stargateservice;

import de.blackforestsolutions.dravelopstestsoftware.model.Polygon;
import de.blackforestsolutions.dravelopstestsoftware.service.stargateservice.GraphQlCallService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopstestsoftware.configuration.GeocodingConfiguration.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class PolygonResolverTest {

    @Autowired
    private GraphQlCallService classUnderTest;

    @Test
    void test_getOperatingArea_returns_a_polygon() {

        Mono<Polygon> result = classUnderTest.getOperatingArea();

        StepVerifier.create(result)
                .assertNext(polygon -> {
                    assertThat(polygon.getPoints().size()).isGreaterThanOrEqualTo(MIN_POLYGON_POINTS);
                    assertThat(polygon.getPoints()).allMatch(coordinate -> coordinate.getX() >= MIN_WGS_84_LONGITUDE);
                    assertThat(polygon.getPoints()).allMatch(coordinate -> coordinate.getX() <= MAX_WGS_84_LONGITUDE);
                    assertThat(polygon.getPoints()).allMatch(coordinate -> coordinate.getY() >= MIN_WGS_84_LATITUDE);
                    assertThat(polygon.getPoints()).allMatch(coordinate -> coordinate.getY() <= MAX_WGS_84_LATITUDE);
                })
                .verifyComplete();
    }
}
