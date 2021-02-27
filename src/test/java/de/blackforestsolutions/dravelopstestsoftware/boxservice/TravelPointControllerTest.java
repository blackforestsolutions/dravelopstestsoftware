package de.blackforestsolutions.dravelopstestsoftware.boxservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopstestsoftware.testutil.TestUtils.getAddressesAssertions;

@Import(TravelPointConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class TravelPointControllerTest {

    @Autowired
    private String travelPointBoxServiceUrl;

    @Autowired
    private ApiToken.ApiTokenBuilder travelPointApiToken;

    @Autowired
    private ExchangeStrategies exchangeStrategies;


    @Test
    void test_retrievePeliasTravelPoints_with_correct_apiToken_returns_travelPoints() {
        ApiToken testData = travelPointApiToken.build();

        Flux<TravelPoint> result = retrievePeliasTravelPoints(testData)
                .expectStatus()
                .isOk()
                .returnResult(TravelPoint.class)
                .getResponseBody();

        StepVerifier.create(result)
                .assertNext(getAddressesAssertions())
                .thenConsumeWhile(travelPoint -> true, getAddressesAssertions())
                .verifyComplete();
    }

    @Test
    void test_retrievePeliasTravelPoints_with_incorrect_apiToken_returns_zero_travelPoints() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(travelPointApiToken);
        testData.setDeparture("No TravelPoint is available in pelias for this string");

        Flux<String> result = retrievePeliasTravelPoints(testData.build())
                .expectStatus()
                .isOk()
                .returnResult(String.class)
                .getResponseBody();

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    private WebTestClient.ResponseSpec retrievePeliasTravelPoints(ApiToken requestToken) {
        return WebTestClient
                .bindToServer()
                .baseUrl(travelPointBoxServiceUrl)
                .exchangeStrategies(exchangeStrategies)
                .build()
                .post()
                .body(Mono.just(requestToken), ApiToken.class)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange();
    }
}
