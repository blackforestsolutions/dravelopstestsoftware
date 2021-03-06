package de.blackforestsolutions.dravelopstestsoftware.stargateservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsJsonMapper;
import de.blackforestsolutions.dravelopstestsoftware.configuration.TravelPointConfiguration;
import graphql.kickstart.spring.webclient.boot.GraphQLWebClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Map;

import static de.blackforestsolutions.dravelopstestsoftware.testutil.TestUtils.*;

@Import(TravelPointConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TravelPointResolverTest {

    private static final String TEXT_PARAM = "text";
    private static final String LANGUAGE_PARAM = "language";
    private static final String LONGITUDE_PARAM = "longitude";
    private static final String LATITUDE_PARAM = "latitude";
    private static final String RADIUS_IN_KILOMETERS_PARAMS = "radiusInKilometers";

    @Autowired
    private ApiToken.ApiTokenBuilder travelPointApiToken;

    @Autowired
    private WebClient webClient;

    @Test
    void test_getAutocompleteAddressesBy_max_parameters_graphql_file_and_apiToken_returns_travelPoints() {

        Flux<TravelPoint> result = getAutocompleteAddressesBy(travelPointApiToken.build());

        StepVerifier.create(result)
                .assertNext(getAutocompleteAddressesAssertions())
                .thenConsumeWhile(travelPoint -> true, getAutocompleteAddressesAssertions())
                .verifyComplete();
    }

    @Test
    void test_getAutocompleteAddressesBy_max_parameters_graphql_file_and_incorrect_apiToken_returns_zero_travelPoints() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(this.travelPointApiToken);
        testData.setDeparture("No TravelPoint is available in pelias for this string");

        Flux<TravelPoint> result = getAutocompleteAddressesBy(testData.build());

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void test_getNearestAddressesBy_max_parameters_graphql_file_and_apiToken_returns_travelPoints() {
        ApiToken testData = travelPointApiToken.build();

        Flux<TravelPoint> result = getNearestAddressesBy(testData);

        StepVerifier.create(result)
                .assertNext(getNearestTravelPointsAssertions())
                .thenConsumeWhile(travelPoint -> true, getNearestTravelPointsAssertions())
                .verifyComplete();
    }

    @Test
    void test_getNearestStationsBy_max_parameters_graphql_file_and_apiToken_returns_travelPoints() {
        ApiToken testData = travelPointApiToken.build();

        Flux<TravelPoint> result = getNearestStationsBy(testData);

        StepVerifier.create(result)
                .assertNext(getNearestTravelPointsAssertions())
                .thenConsumeWhile(travelPoint -> true, getNearestTravelPointsAssertions())
                .verifyComplete();
    }

    @Test
    void test_getAllStations_with_graphql_file_returns_travelPoints() {

        Flux<TravelPoint> result = getAllStations();

        StepVerifier.create(result)
                .assertNext(getAllStationsAssertions())
                .thenConsumeWhile(travelPoint -> true, getAllStationsAssertions())
                .verifyComplete();
    }

    private Flux<TravelPoint> getAutocompleteAddressesBy(ApiToken apiToken) {
        return GraphQLWebClient
                .newInstance(webClient, new DravelOpsJsonMapper())
                .flux("graphql/get-autocomplete-addresses-max-parameters.graphql", convertAutocompleteAddressesApiTokenToGraphqlParametersWith(apiToken), TravelPoint.class);
    }

    private Flux<TravelPoint> getNearestAddressesBy(ApiToken apiToken) {
        return GraphQLWebClient
                .newInstance(webClient, new DravelOpsJsonMapper())
                .flux("graphql/get-nearest-addresses-max-parameters.graphql", convertNearestApiTokenToGraphqlParametersWith(apiToken), TravelPoint.class);
    }

    private Flux<TravelPoint> getNearestStationsBy(ApiToken apiToken) {
        return GraphQLWebClient
                .newInstance(webClient, new DravelOpsJsonMapper())
                .flux("graphql/get-nearest-stations-max-parameters.graphql", convertNearestApiTokenToGraphqlParametersWith(apiToken), TravelPoint.class);
    }

    private Flux<TravelPoint> getAllStations() {
        return GraphQLWebClient
                .newInstance(webClient, new DravelOpsJsonMapper())
                .flux("graphql/get-all-stations-parameters.graphql", TravelPoint.class);
    }

    private Map<String, Object> convertAutocompleteAddressesApiTokenToGraphqlParametersWith(ApiToken apiToken) {
        return Map.of(
                TEXT_PARAM, apiToken.getDeparture(),
                LANGUAGE_PARAM, apiToken.getLanguage()
        );
    }

    private Map<String, Object> convertNearestApiTokenToGraphqlParametersWith(ApiToken apiToken) {
        return Map.of(
                LONGITUDE_PARAM, apiToken.getArrivalCoordinate().getX(),
                LATITUDE_PARAM, apiToken.getArrivalCoordinate().getY(),
                RADIUS_IN_KILOMETERS_PARAMS, apiToken.getRadiusInKilometers().getValue(),
                LANGUAGE_PARAM, apiToken.getLanguage()
        );
    }


}
