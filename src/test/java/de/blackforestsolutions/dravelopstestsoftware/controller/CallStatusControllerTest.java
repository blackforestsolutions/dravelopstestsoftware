package de.blackforestsolutions.dravelopstestsoftware.controller;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.GraphQlTab;
import de.blackforestsolutions.dravelopstestsoftware.service.GraphQlValidatorService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getTestSoftwareApiTokens;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.CallStatusObjectMother.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CallStatusControllerTest {

    private final GraphQlValidatorService graphQlValidatorService = mock(GraphQlValidatorService.class);

    private final CallStatusController classUnderTest = new CallStatusController(graphQlValidatorService);

    @Test
    void test_executeTestsWith_apiTokenMap_is_executed_correctly_and_returns_callStatusTabs() {
        Map<GraphQlTab, ApiToken> testData = getTestSoftwareApiTokens();
        when(graphQlValidatorService.executeGraphQlTestsWith(anyMap()))
                .thenReturn(getExampleCallStatusTabsResult());

        List<CallStatus<GraphQlTab>> result = classUnderTest.executeTestsWith(testData).block();

        assertThat(result.size()).isEqualTo(4);
        assertThat(result.get(0)).isEqualToComparingFieldByField(getSuccessfulJourneyCallStatusTab());
        assertThat(result.get(1)).isEqualToComparingFieldByField(getSuccessfulAddressAutocompletionCallStatusTab());
        assertThat(result.get(2)).isEqualToComparingFieldByField(getSuccessfulNearestAddressesCallStatusTab());
        assertThat(result.get(3)).isEqualToComparingFieldByField(getSuccessfulNearestStationsCallStatusTab());
        verify(graphQlValidatorService, times(1)).executeGraphQlTestsWith(anyMap());
    }

    @Test
    void test_executeTestsWith_is_executed_correctly_when_no_results_are_available_and_returns_empty_list() {
        Map<GraphQlTab, ApiToken> testData = Collections.emptyMap();
        when(graphQlValidatorService.executeGraphQlTestsWith(anyMap()))
                .thenReturn(Flux.empty());

        Mono<List<CallStatus<GraphQlTab>>> result = classUnderTest.executeTestsWith(testData);

        verify(graphQlValidatorService, times(1)).executeGraphQlTestsWith(anyMap());
        StepVerifier.create(result)
                .assertNext(callStatuses -> assertThat(callStatuses.size()).isEqualTo(0))
                .verifyComplete();
    }

    private Flux<CallStatus<GraphQlTab>> getExampleCallStatusTabsResult() {
        return Flux.just(
                getSuccessfulJourneyCallStatusTab(),
                getSuccessfulAddressAutocompletionCallStatusTab(),
                getSuccessfulNearestAddressesCallStatusTab(),
                getSuccessfulNearestStationsCallStatusTab()
        );
    }
}
