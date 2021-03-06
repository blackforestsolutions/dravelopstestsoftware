package de.blackforestsolutions.dravelopstestsoftware.otpmapperservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopstestsoftware.configuration.JourneyConfiguration;
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

import java.time.ZonedDateTime;

import static de.blackforestsolutions.dravelopstestsoftware.testutil.TestUtils.getArrivalAndDepartureLegAssertions;
import static de.blackforestsolutions.dravelopstestsoftware.testutil.TestUtils.getLegPropertiesAssertions;

@Import(JourneyConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class JourneyControllerTest {

    @Autowired
    private String journeyOtpMapperUrl;

    @Autowired
    private ApiToken.ApiTokenBuilder otpApiToken;

    @Autowired
    private ExchangeStrategies exchangeStrategies;

    @Test
    void test_getJourneysBy_correct_apiToken_return_journeys_with_correct_leg_properties() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(otpApiToken);
        testData.setDateTime(ZonedDateTime.now().plusDays(1L).withHour(12).withMinute(0).withSecond(0));

        Flux<Journey> result = getJourneysBy(testData.build())
                .expectStatus()
                .isOk()
                .returnResult(Journey.class)
                .getResponseBody();

        StepVerifier.create(result)
                .assertNext(getLegPropertiesAssertions())
                .thenConsumeWhile(journey -> true, getLegPropertiesAssertions())
                .verifyComplete();
    }

    @Test
    void test_getJourneysBy_correct_apiToken_return_journeys_with_correct_leg_properties_for_departure_and_arrival() {
        ApiToken testData = otpApiToken.build();

        Flux<Journey> result = getJourneysBy(testData)
                .expectStatus()
                .isOk()
                .returnResult(Journey.class)
                .getResponseBody();

        StepVerifier.create(result)
                .assertNext(getArrivalAndDepartureLegAssertions())
                .thenConsumeWhile(journey -> true, getArrivalAndDepartureLegAssertions())
                .verifyComplete();
    }

    @Test
    void test_getJourneysBy_incorrect_apiToken_returns_zero_journeys() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(otpApiToken);
        testData.setArrivalCoordinate(new Point.PointBuilder(0.0d, 0.0d).build());
        testData.setDepartureCoordinate(new Point.PointBuilder(0.0d, 0.0d).build());

        Flux<Journey> result = getJourneysBy(testData.build())
                .expectStatus()
                .isOk()
                .returnResult(Journey.class)
                .getResponseBody();

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    private WebTestClient.ResponseSpec getJourneysBy(ApiToken requestToken) {
        return WebTestClient
                .bindToServer()
                .baseUrl(journeyOtpMapperUrl)
                .exchangeStrategies(exchangeStrategies)
                .build()
                .post()
                .body(Mono.just(requestToken), ApiToken.class)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange();
    }

}
