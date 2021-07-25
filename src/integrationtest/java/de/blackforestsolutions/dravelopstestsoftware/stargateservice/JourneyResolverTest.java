package de.blackforestsolutions.dravelopstestsoftware.stargateservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopstestsoftware.configuration.ApiTokenConfiguration;
import de.blackforestsolutions.dravelopstestsoftware.configuration.JourneyConfiguration;
import de.blackforestsolutions.dravelopstestsoftware.service.stargateservice.GraphQlCallService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopstestsoftware.testutil.TestAssertions.getArrivalAndDepartureLegAssertions;
import static de.blackforestsolutions.dravelopstestsoftware.testutil.TestAssertions.getGraphQlLegPropertiesAssertions;

@Import(value = {ApiTokenConfiguration.class, JourneyConfiguration.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class JourneyResolverTest {

    @Autowired
    private ApiToken journeyUserRequestApiToken;

    @Autowired
    private GraphQlCallService classUnderTest;

    @Test
    void test_getJourneysBy_max_parameters_graphql_file_and_apiToken_returns_journeys_with_correct_leg_properties() {
        ApiToken testData = new ApiToken(journeyUserRequestApiToken);

        Flux<Journey> result = classUnderTest.getJourneysBy(testData);

        StepVerifier.create(result)
                .assertNext(getGraphQlLegPropertiesAssertions())
                .thenConsumeWhile(journey -> true, getGraphQlLegPropertiesAssertions())
                .verifyComplete();
    }

    @Test
    void test_getJourneysBy_max_parameters_graphql_file_and_apiToken_returns_journeys_with_correct_leg_properties_for_departure_and_arrival() {
        ApiToken testData = new ApiToken(journeyUserRequestApiToken);

        Flux<Journey> result = classUnderTest.getJourneysBy(testData);

        StepVerifier.create(result)
                .assertNext(getArrivalAndDepartureLegAssertions())
                .thenConsumeWhile(journey -> true, getArrivalAndDepartureLegAssertions())
                .verifyComplete();
    }

    @Test
    void test_getJourneysBy_max_parameters_graphql_file_and_incorrect_apiToken_returns_zero_journeys() {
        ApiToken journeyApiToken = new ApiToken(journeyUserRequestApiToken);
        journeyApiToken.setDepartureCoordinate(new Point.PointBuilder(0.0d, 0.0d).build());
        journeyApiToken.setArrivalCoordinate(new Point.PointBuilder(0.0d, 0.0d).build());

        Flux<Journey> result = classUnderTest.getJourneysBy(journeyApiToken);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }
}
