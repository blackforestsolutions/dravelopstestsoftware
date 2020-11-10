package de.blackforestsolutions.dravelopstestsoftware.stargateservice;

import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsJsonMapper;
import de.blackforestsolutions.dravelopstestsoftware.configuration.JourneyConfiguration;
import graphql.kickstart.spring.webclient.boot.GraphQLWebClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.geo.Point;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Map;

import static de.blackforestsolutions.dravelopstestsoftware.testutil.TestUtils.getArrivalAndDepartureLegAssertions;
import static de.blackforestsolutions.dravelopstestsoftware.testutil.TestUtils.getLegPropertiesAssertions;

@Import(JourneyConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JourneyResolverTest {

    private static final String DEPARTURE_LATITUDE_PARAM = "departureLatitude";
    private static final String DEPARTURE_LONGITUDE_PARAM = "departureLongitude";
    private static final String ARRIVAL_LATITUDE_PARAM = "arrivalLatitude";
    private static final String ARRIVAL_LONGITUDE_PARAM = "arrivalLongitude";
    private static final String DATE_TIME_PARAM = "dateTime";
    private static final String OPTIMIZATION_PARAM = "optimize";
    private static final String IS_ARRIVAL_DATE_TIME_PARAM = "isArrivalDateTime";
    private static final String LANGUAGE_PARAM = "language";

    @Autowired
    private ApiToken.ApiTokenBuilder journeyApiToken;

    @Autowired
    private WebClient webClient;

    @Test
    void test_getJourneysBy_max_parameters_graphql_file_and_apiToken_returns_journeys_with_correct_leg_properties() {

        Flux<Journey> result = getJourneysBy(journeyApiToken.build());

        StepVerifier.create(result)
                .assertNext(getLegPropertiesAssertions())
                .thenConsumeWhile(journey -> true, getLegPropertiesAssertions())
                .verifyComplete();
    }

    @Test
    void test_getJourneysBy_max_parameters_graphql_file_and_apiToken_returns_journeys_with_correct_leg_properties_for_departure_and_arrival() {

        Flux<Journey> result = getJourneysBy(journeyApiToken.build());

        StepVerifier.create(result)
                .assertNext(getArrivalAndDepartureLegAssertions())
                .thenConsumeWhile(journey -> true, getArrivalAndDepartureLegAssertions())
                .verifyComplete();
    }

    @Test
    void test_getJourneysBy_max_parameters_graphql_file_and_incorrect_apiToken_returns_zero_journeys() {
        ApiToken.ApiTokenBuilder journeyApiToken = new ApiToken.ApiTokenBuilder(this.journeyApiToken);
        journeyApiToken.setDepartureCoordinate(new Point(0.0d, 0.0d));
        journeyApiToken.setArrivalCoordinate(new Point(0.0d, 0.0d));

        Flux<Journey> result = getJourneysBy(journeyApiToken.build());

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    private Flux<Journey> getJourneysBy(ApiToken apiToken) {
        return GraphQLWebClient
                .newInstance(webClient, new DravelOpsJsonMapper())
                .flux("graphql/get-journeys-max-parameters.graphql", convertToGraphqlParametersWith(apiToken), Journey.class);
    }

    private Map<String, Object> convertToGraphqlParametersWith(ApiToken apiToken) {
        return Map.of(
                DEPARTURE_LATITUDE_PARAM, apiToken.getDepartureCoordinate().getY(),
                DEPARTURE_LONGITUDE_PARAM, apiToken.getDepartureCoordinate().getX(),
                ARRIVAL_LATITUDE_PARAM, apiToken.getArrivalCoordinate().getY(),
                ARRIVAL_LONGITUDE_PARAM, apiToken.getArrivalCoordinate().getX(),
                DATE_TIME_PARAM, apiToken.getDateTime(),
                OPTIMIZATION_PARAM, apiToken.getOptimize(),
                IS_ARRIVAL_DATE_TIME_PARAM, apiToken.getIsArrivalDateTime(),
                LANGUAGE_PARAM, apiToken.getLanguage().toString()
        );
    }
}
