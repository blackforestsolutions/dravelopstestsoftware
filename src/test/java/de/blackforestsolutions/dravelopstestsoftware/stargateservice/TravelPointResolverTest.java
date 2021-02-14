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

import static de.blackforestsolutions.dravelopstestsoftware.testutil.TestUtils.getAddressesAssertions;
import static de.blackforestsolutions.dravelopstestsoftware.testutil.TestUtils.getAllStationsAssertions;

@Import(TravelPointConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TravelPointResolverTest {

    private static final String TEXT_PARAM = "text";
    private static final String LANGUAGE_PARAM = "language";

    @Autowired
    private ApiToken.ApiTokenBuilder travelPointApiToken;

    @Autowired
    private WebClient webClient;

    @Test
    void test_getAddressesBy_max_parameters_graphql_file_and_apiToken_returns_travelPoints() {

        Flux<TravelPoint> result = getAddressesBy(travelPointApiToken.build());

        StepVerifier.create(result)
                .assertNext(getAddressesAssertions())
                .thenConsumeWhile(travelPoint -> true, getAddressesAssertions())
                .verifyComplete();
    }

    @Test
    void test_getAddressesBy_max_parameters_graphql_file_and_incorrect_apiToken_returns_zero_travelPoints() {
        ApiToken.ApiTokenBuilder travelPointUserRequestToken = new ApiToken.ApiTokenBuilder(this.travelPointApiToken);
        travelPointUserRequestToken.setDeparture("No TravelPoint is available in pelias for this string");

        Flux<TravelPoint> result = getAddressesBy(travelPointUserRequestToken.build());

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void test_getAllStations_with_grpahql_file_returns_travelPoints() {

        Flux<TravelPoint> result = getAllStations();

        StepVerifier.create(result)
                .assertNext(getAllStationsAssertions())
                .thenConsumeWhile(travelPoint -> true, getAllStationsAssertions())
                .verifyComplete();
    }

    private Flux<TravelPoint> getAddressesBy(ApiToken apiToken) {
        return GraphQLWebClient
                .newInstance(webClient, new DravelOpsJsonMapper())
                .flux("graphql/get-addresses-max-parameters.graphql", convertToGraphqlParametersWith(apiToken), TravelPoint.class);
    }

    private Flux<TravelPoint> getAllStations() {
        return GraphQLWebClient
                .newInstance(webClient, new DravelOpsJsonMapper())
                .flux("graphql/get-all-stations-parameters.graphql", TravelPoint.class);
    }

    private Map<String, Object> convertToGraphqlParametersWith(ApiToken apiToken) {
        return Map.of(
                TEXT_PARAM, apiToken.getDeparture(),
                LANGUAGE_PARAM, apiToken.getLanguage()
        );
    }


}
