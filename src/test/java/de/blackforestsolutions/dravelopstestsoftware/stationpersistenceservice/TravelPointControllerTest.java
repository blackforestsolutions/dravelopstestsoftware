package de.blackforestsolutions.dravelopstestsoftware.stationpersistenceservice;

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

import static de.blackforestsolutions.dravelopstestsoftware.testutil.TestUtils.getAllStationsAssertions;

@Import(TravelPointConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class TravelPointControllerTest {

    @Autowired
    private String stationsPersistenceTravelPointUrl;

    @Autowired
    private ExchangeStrategies exchangeStrategies;

    @Test
    void test_getAllStations_returns_travelPoints() {

        Flux<TravelPoint> result = getAllStations()
                .expectStatus()
                .isOk()
                .returnResult(TravelPoint.class)
                .getResponseBody();

        StepVerifier.create(result)
                .assertNext(getAllStationsAssertions())
                .thenConsumeWhile(travelPoint -> true, getAllStationsAssertions())
                .verifyComplete();
    }

    private WebTestClient.ResponseSpec getAllStations() {
        return WebTestClient
                .bindToServer()
                .baseUrl(stationsPersistenceTravelPointUrl)
                .exchangeStrategies(exchangeStrategies)
                .build()
                .get()
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange();
    }
}
