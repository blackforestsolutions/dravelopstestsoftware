package de.blackforestsolutions.dravelopstestsoftware.travelpointsimporterservice;

import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopstestsoftware.configuration.TravelPointConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TravelPointConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class TravelPointControllerTest {

    @Autowired
    private String travelPointsImporterUrl;

    @Autowired
    private ExchangeStrategies exchangeStrategies;

    @Test
    void test_getAllTravelPoints_() {

        Flux<TravelPoint> result = getAllTravelPoints()
                .expectStatus()
                .isOk()
                .returnResult(TravelPoint.class)
                .getResponseBody();

        StepVerifier.create(result)
                .assertNext(getAllTravelPointAssertions())
                .thenConsumeWhile(travelPoint -> true, getAllTravelPointAssertions())
                .verifyComplete();
    }

    private WebTestClient.ResponseSpec getAllTravelPoints() {
        return WebTestClient
                .bindToServer()
                .baseUrl(travelPointsImporterUrl)
                .exchangeStrategies(exchangeStrategies)
                .build()
                .get()
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange();
    }

    private static Consumer<TravelPoint> getAllTravelPointAssertions() {
        return travelPoint -> {
            assertThat(travelPoint.getName()).isNotEmpty();
            assertThat(travelPoint.getPoint()).isNotNull();
            assertThat(travelPoint.getArrivalTime()).isNull();
            assertThat(travelPoint.getDepartureTime()).isNull();
            assertThat(travelPoint.getPlatform()).isNotNull();
        };
    }
}
