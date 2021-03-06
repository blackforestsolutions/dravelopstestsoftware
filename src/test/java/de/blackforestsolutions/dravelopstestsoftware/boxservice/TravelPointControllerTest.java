package de.blackforestsolutions.dravelopstestsoftware.boxservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopstestsoftware.configuration.TravelPointConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopstestsoftware.testutil.TestUtils.getAutocompleteAddressesAssertions;
import static de.blackforestsolutions.dravelopstestsoftware.testutil.TestUtils.getNearestTravelPointsAssertions;

@Import(TravelPointConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class TravelPointControllerTest {

    @Autowired
    private String autocompleteAddressesBoxServiceUrl;

    @Autowired
    private String nearestAddressesBoxServiceUrl;

    @Autowired
    private ApiToken.ApiTokenBuilder travelPointApiToken;

    @Autowired
    private ExchangeStrategies exchangeStrategies;


    @Test
    void test_getAutocompleteAddresses_with_correct_apiToken_returns_travelPoints() {
        ApiToken testData = travelPointApiToken.build();

        Flux<TravelPoint> result = getAutocompleteAddresses(testData)
                .expectStatus()
                .isOk()
                .returnResult(TravelPoint.class)
                .getResponseBody();

        StepVerifier.create(result)
                .assertNext(getAutocompleteAddressesAssertions())
                .thenConsumeWhile(travelPoint -> true, getAutocompleteAddressesAssertions())
                .verifyComplete();
    }

    @Test
    void test_getAutocompleteAddresses_with_incorrect_apiToken_returns_zero_travelPoints() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(travelPointApiToken);
        testData.setDeparture("No TravelPoint is available in pelias for this string");

        Flux<String> result = getAutocompleteAddresses(testData.build())
                .expectStatus()
                .isOk()
                .returnResult(String.class)
                .getResponseBody();

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void test_getNearestAddresses_with_correct_apiToken_returns_travelPoints() {
        ApiToken testData = travelPointApiToken.build();

        Flux<TravelPoint> result = getNearestAddresses(testData)
                .expectStatus()
                .isOk()
                .returnResult(TravelPoint.class)
                .getResponseBody();

        StepVerifier.create(result)
                .assertNext(getNearestTravelPointsAssertions())
                .thenConsumeWhile(travelPoint -> true, getNearestTravelPointsAssertions())
                .verifyComplete();
    }

    @Test
    void test_getNearestAddresses_with_incorrect_apiToken_returns_zero_travelPoints() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(travelPointApiToken);
        testData.setArrivalCoordinate(new Point.PointBuilder(0.0d, 0.0d).build());
        testData.setRadiusInKilometers(new Distance(1.0d, Metrics.KILOMETERS));

        Flux<TravelPoint> result = getNearestAddresses(testData.build())
                .expectStatus()
                .isOk()
                .returnResult(TravelPoint.class)
                .getResponseBody();

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    private WebTestClient.ResponseSpec getAutocompleteAddresses(ApiToken requestToken) {
        return WebTestClient
                .bindToServer()
                .baseUrl(autocompleteAddressesBoxServiceUrl)
                .exchangeStrategies(exchangeStrategies)
                .build()
                .post()
                .body(Mono.just(requestToken), ApiToken.class)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange();
    }

    private WebTestClient.ResponseSpec getNearestAddresses(ApiToken requestToken) {
        return WebTestClient
                .bindToServer()
                .baseUrl(nearestAddressesBoxServiceUrl)
                .exchangeStrategies(exchangeStrategies)
                .build()
                .post()
                .body(Mono.just(requestToken), ApiToken.class)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange();
    }
}
