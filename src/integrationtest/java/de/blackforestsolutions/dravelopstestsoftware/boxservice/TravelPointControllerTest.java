package de.blackforestsolutions.dravelopstestsoftware.boxservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopstestsoftware.configuration.ApiTokenConfiguration;
import de.blackforestsolutions.dravelopstestsoftware.configuration.TravelPointConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopstestsoftware.testutil.TestAssertions.getAutocompleteAddressesAssertions;
import static de.blackforestsolutions.dravelopstestsoftware.testutil.TestAssertions.getNearestTravelPointsAssertions;

@Import(value = {ApiTokenConfiguration.class, TravelPointConfiguration.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureWebTestClient
public class TravelPointControllerTest {

    @Autowired
    private String autocompleteAddressesBoxServiceUrl;

    @Autowired
    private String nearestAddressesBoxServiceUrl;

    @Autowired
    private ApiToken autocompleteUserRequestApiToken;

    @Autowired
    private ApiToken nearestAddressesUserRequestApiToken;

    @Autowired
    private ExchangeStrategies exchangeStrategies;


    @Test
    void test_getAutocompleteAddresses_with_correct_apiToken_returns_travelPoints() {
        ApiToken testData = new ApiToken(autocompleteUserRequestApiToken);

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
        ApiToken testData = new ApiToken(autocompleteUserRequestApiToken);
        testData.setDeparture("No TravelPoint is available in pelias for this string");

        Flux<String> result = getAutocompleteAddresses(testData)
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
        ApiToken testData = new ApiToken(nearestAddressesUserRequestApiToken);

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
        ApiToken testData = new ApiToken(nearestAddressesUserRequestApiToken);
        testData.setArrivalCoordinate(new Point.PointBuilder(0.0d, 0.0d).build());
        testData.setRadiusInKilometers(new Distance(1.0d, Metrics.KILOMETERS));

        Flux<TravelPoint> result = getNearestAddresses(testData)
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
