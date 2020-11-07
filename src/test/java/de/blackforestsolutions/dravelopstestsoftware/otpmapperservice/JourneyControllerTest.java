package de.blackforestsolutions.dravelopstestsoftware.otpmapperservice;

import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.geo.Point;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Consumer;

import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.retrieveJsonToPojo;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.toJson;
import static org.assertj.core.api.Assertions.assertThat;

@Import(JourneyControllerConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class JourneyControllerTest {

    @Autowired
    private String journeyControllerUrl;

    @Autowired
    private ApiToken.ApiTokenBuilder otpMapperApiToken;


    @Test
    void test_retrieveOpenTripPlannerJourneys_with_correct_apiToken_return_journeys_with_correct_leg_properties() {
        ApiToken testData = otpMapperApiToken.build();

        Flux<String> result = retrieveOpenTripPlannerJourneys(testData)
                .expectStatus()
                .isOk()
                .returnResult(String.class)
                .getResponseBody();

        StepVerifier.create(result)
                .assertNext(getLegPropertiesAssertions())
                .thenConsumeWhile(journeyJson -> true, getLegPropertiesAssertions())
                .verifyComplete();
    }

    @Test
    void test_retrieveOpenTripPlannerJourneys_with_correct_apiToken_return_journeys_with_correct_leg_properties_for_departure_and_arrival() {
        ApiToken testData = otpMapperApiToken.build();

        Flux<String> result = retrieveOpenTripPlannerJourneys(testData)
                .expectStatus()
                .isOk()
                .returnResult(String.class)
                .getResponseBody();

        StepVerifier.create(result)
                .assertNext(getArrivalAndDepartureLegAssertions())
                .thenConsumeWhile(journeyJson -> true, getArrivalAndDepartureLegAssertions())
                .verifyComplete();
    }

    @Test
    void test_retrieveOpenTripPlannerJourneys_with_incorrect_apiToken_returns_zero_journeys() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(otpMapperApiToken);
        testData.setArrivalCoordinate(new Point(0.0d, 0.0d));
        testData.setDepartureCoordinate(new Point(0.0d, 0.0d));

        Flux<String> result = retrieveOpenTripPlannerJourneys(testData.build())
                .expectStatus()
                .isOk()
                .returnResult(String.class)
                .getResponseBody();

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    private Consumer<String> getLegPropertiesAssertions() {
        return journeyJson -> {
            Journey journey = retrieveJsonToPojo(journeyJson, Journey.class);
            assertThat(journey.getLegs())
                    .allMatch(leg -> leg.getDeparture() != null)
                    .allMatch(leg -> !leg.getDeparture().getName().isEmpty())
                    .allMatch(leg -> leg.getDeparture().getPoint() != null)
                    .allMatch(leg -> leg.getArrival() != null)
                    .allMatch(leg -> !leg.getArrival().getName().isEmpty())
                    .allMatch(leg -> leg.getArrival().getPoint() != null);
            assertThat(journey.getLegs())
                    .first()
                    .matches(leg -> leg.getDeparture().getArrivalTime() == null);
            assertThat(journey.getLegs())
                    .last()
                    .matches(leg -> leg.getArrival().getDepartureTime() == null);
        };
    }

    private Consumer<String> getArrivalAndDepartureLegAssertions() {
        return journeyJson -> {
            Journey journey = retrieveJsonToPojo(journeyJson, Journey.class);
            assertThat(journey.getLegs())
                    .allMatch(leg -> leg.getDeparture() != null)
                    .allMatch(leg -> !leg.getDeparture().getName().isEmpty())
                    .allMatch(leg -> leg.getDeparture().getPoint() != null)
                    .allMatch(leg -> leg.getArrival() != null)
                    .allMatch(leg -> !leg.getArrival().getName().isEmpty())
                    .allMatch(leg -> leg.getArrival().getPoint() != null);
            assertThat(journey.getLegs())
                    .first()
                    .matches(leg -> leg.getDeparture().getArrivalTime() == null);
            assertThat(journey.getLegs())
                    .last()
                    .matches(leg -> leg.getArrival().getDepartureTime() == null);
        };
    }

    private WebTestClient.ResponseSpec retrieveOpenTripPlannerJourneys(ApiToken requestToken) {
        return WebTestClient
                .bindToServer()
                .baseUrl(journeyControllerUrl)
                .build()
                .post()
                .body(Mono.just(toJson(requestToken)), String.class)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange();
    }

}
