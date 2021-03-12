package de.blackforestsolutions.dravelopstestsoftware.stargateservice;

import de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsJsonMapper;
import de.blackforestsolutions.dravelopstestsoftware.model.Polygon;
import graphql.kickstart.spring.webclient.boot.GraphQLWebClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopstestsoftware.configuration.GeocodingConfiguration.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class PolygonResolverTest {

    @Autowired
    private WebClient webClient;

    @Test
    void test_getOperatingArea_returns_a_polygon() {

        Mono<Polygon> result = getOperatingArea();

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

    private Mono<Polygon> getOperatingArea() {
        return GraphQLWebClient
                .newInstance(webClient, new DravelOpsJsonMapper())
                .post("graphql/get-polygon.graphql", Polygon.class);
    }
}
