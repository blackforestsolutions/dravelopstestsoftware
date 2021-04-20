package de.blackforestsolutions.dravelopstestsoftware.stargateservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopstestsoftware.configuration.ApiTokenConfiguration;
import de.blackforestsolutions.dravelopstestsoftware.configuration.TravelPointConfiguration;
import de.blackforestsolutions.dravelopstestsoftware.service.stargateservice.GraphQlCallService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopstestsoftware.testutil.TestAssertions.*;

@Import(value = {ApiTokenConfiguration.class, TravelPointConfiguration.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TravelPointResolverTest {

    @Autowired
    private ApiToken testApiToken;

    @Autowired
    private GraphQlCallService classUnderTest;

    @Test
    void test_getAutocompleteAddressesBy_max_parameters_graphql_file_and_apiToken_returns_travelPoints() {
        ApiToken testData = new ApiToken(testApiToken);

        Flux<TravelPoint> result = classUnderTest.getAutocompleteAddressesBy(testData);

        StepVerifier.create(result)
                .assertNext(getAutocompleteAddressesAssertions())
                .thenConsumeWhile(travelPoint -> true, getAutocompleteAddressesAssertions())
                .verifyComplete();
    }

    @Test
    void test_getAutocompleteAddressesBy_max_parameters_graphql_file_and_incorrect_apiToken_returns_zero_travelPoints() {
        ApiToken testData = new ApiToken(testApiToken);
        testData.setDeparture("No TravelPoint is available in pelias for this string");

        Flux<TravelPoint> result = classUnderTest.getAutocompleteAddressesBy(testData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void test_getNearestAddressesBy_max_parameters_graphql_file_and_apiToken_returns_travelPoints() {
        ApiToken testData = new ApiToken(testApiToken);

        Flux<TravelPoint> result = classUnderTest.getNearestAddressesBy(testData);

        StepVerifier.create(result)
                .assertNext(getNearestTravelPointsAssertions())
                .thenConsumeWhile(travelPoint -> true, getNearestTravelPointsAssertions())
                .verifyComplete();
    }

    @Test
    void test_getNearestStationsBy_max_parameters_graphql_file_and_apiToken_returns_travelPoints() {
        ApiToken testData = new ApiToken(testApiToken);

        Flux<TravelPoint> result = classUnderTest.getNearestStationsBy(testData);

        StepVerifier.create(result)
                .assertNext(getNearestTravelPointsAssertions())
                .thenConsumeWhile(travelPoint -> true, getNearestTravelPointsAssertions())
                .verifyComplete();
    }

    @Test
    void test_getAllStations_with_graphql_file_returns_travelPoints() {

        Flux<TravelPoint> result = classUnderTest.getAllStations();

        StepVerifier.create(result)
                .assertNext(getAllStationsAssertions())
                .thenConsumeWhile(travelPoint -> true, getAllStationsAssertions())
                .verifyComplete();
    }


}
