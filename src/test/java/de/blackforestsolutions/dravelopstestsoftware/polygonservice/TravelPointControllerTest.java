package de.blackforestsolutions.dravelopstestsoftware.polygonservice;

import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Consumer;

import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.retrieveJsonToPojo;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.toJson;
import static org.assertj.core.api.Assertions.assertThat;

@Import(TravelPointControllerConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class TravelPointControllerTest {

    @Autowired
    private String travelPointControllerUrl;

    @Autowired
    private ApiToken.ApiTokenBuilder polygonApiToken;


    @Test
    void test_retrievePeliasTravelPoints_with_correct_apiToken_returns_travelPoints() {
        ApiToken testData = polygonApiToken.build();

        Flux<String> result = retrievePeliasTravelPoints(testData)
                .expectStatus()
                .isOk()
                .returnResult(String.class)
                .getResponseBody();

        StepVerifier.create(result)
                .assertNext(getTravelPointAssertions())
                .thenConsumeWhile(travelPointJson -> true, getTravelPointAssertions())
                .verifyComplete();
    }

    @Test
    void test_retrievePeliasTravelPoints_with_incorrect_apiToken_returns_zero_travelPoints() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(polygonApiToken);
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
                .baseUrl(travelPointControllerUrl)
                .build()
                .post()
                .body(Mono.just(toJson(requestToken)), String.class)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange();
    }

    private Consumer<String> getTravelPointAssertions() {
        return travelPointJson -> {
            TravelPoint travelPoint = retrieveJsonToPojo(travelPointJson, TravelPoint.class);
            assertThat(travelPoint).isNotNull();
            assertThat(travelPoint.getName()).isNotEmpty();
            assertThat(travelPoint.getPoint()).isNotNull();
            assertThat(travelPoint.getPlatform()).isEmpty();
            assertThat(travelPoint.getArrivalTime()).isNull();
            assertThat(travelPoint.getDepartureTime()).isNull();
        };
    }
}
