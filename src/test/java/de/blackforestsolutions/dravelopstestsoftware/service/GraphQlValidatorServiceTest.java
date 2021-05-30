package de.blackforestsolutions.dravelopstestsoftware.service;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.GraphQlTab;
import de.blackforestsolutions.dravelopstestsoftware.exceptionhandling.ExceptionHandlerService;
import de.blackforestsolutions.dravelopstestsoftware.exceptionhandling.ExceptionHandlerServiceImpl;
import de.blackforestsolutions.dravelopstestsoftware.service.stargateservice.GraphQlCallService;
import de.blackforestsolutions.dravelopstestsoftware.service.stargateservice.GraphQlCallServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Map;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getTestSoftwareApiTokens;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.CallStatusObjectMother.*;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.JourneyObjectMother.getFurtwangenToWaldkirchJourney;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.TravelPointObjectMother.getFurtwangenBirkeTravelPoint;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.TravelPointObjectMother.getFurtwangenTownChurchTravelPoint;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GraphQlValidatorServiceTest {

    private final GraphQlCallService graphQlCallService = mock(GraphQlCallServiceImpl.class);
    private final ExceptionHandlerService exceptionHandlerService = spy(ExceptionHandlerServiceImpl.class);

    private final GraphQlValidatorService classUnderTest = new GraphQlValidatorServiceImpl(graphQlCallService, exceptionHandlerService);

    @BeforeEach
    void init() {
        when(graphQlCallService.getJourneysBy(any(ApiToken.class)))
                .thenReturn(Flux.just(getFurtwangenToWaldkirchJourney()));
        when(graphQlCallService.getAutocompleteAddressesBy(any(ApiToken.class)))
                .thenReturn(Flux.just(getFurtwangenBirkeTravelPoint()));
        when(graphQlCallService.getNearestAddressesBy(any(ApiToken.class)))
                .thenReturn(Flux.just(getFurtwangenTownChurchTravelPoint()));
        when(graphQlCallService.getNearestStationsBy(any(ApiToken.class)))
                .thenReturn(Flux.just(getFurtwangenTownChurchTravelPoint()));
    }

    @Test
    void test_executeGraphQlTestsWith_apiTokenMap_returns_four_successful_callStatusTabs_when_all_graphQlQueries_were_successful() {
        Map<GraphQlTab, ApiToken> testData = getTestSoftwareApiTokens();

        Flux<CallStatus<GraphQlTab>> result = classUnderTest.executeGraphQlTestsWith(testData);

        StepVerifier.create(result)
                .assertNext(callStatusTab -> assertThat(callStatusTab).isEqualToComparingFieldByField(getSuccessfulJourneyCallStatusTab()))
                .assertNext(callStatusTab -> assertThat(callStatusTab).isEqualToComparingFieldByField(getSuccessfulAddressAutocompletionCallStatusTab()))
                .assertNext(callStatusTab -> assertThat(callStatusTab).isEqualToComparingFieldByField(getSuccessfulNearestAddressesCallStatusTab()))
                .assertNext(callStatusTab -> assertThat(callStatusTab).isEqualToComparingFieldByField(getSuccessfulNearestStationsCallStatusTab()))
                .verifyComplete();
    }

    @Test
    void test_executeGraphQlTestsWith_apiTokenMap_is_executed_correctly() {
        Map<GraphQlTab, ApiToken> testData = getTestSoftwareApiTokens();
        ArgumentCaptor<ApiToken> journeyApiTokenArg = ArgumentCaptor.forClass(ApiToken.class);
        ArgumentCaptor<ApiToken> autocompleteAddressesArg = ArgumentCaptor.forClass(ApiToken.class);
        ArgumentCaptor<ApiToken> nearestAddressesArg = ArgumentCaptor.forClass(ApiToken.class);
        ArgumentCaptor<ApiToken> nearestStationsArg = ArgumentCaptor.forClass(ApiToken.class);

        classUnderTest.executeGraphQlTestsWith(testData).collectList().block();

        verify(graphQlCallService, times(1)).getJourneysBy(journeyApiTokenArg.capture());
        verify(graphQlCallService, times(1)).getAutocompleteAddressesBy(autocompleteAddressesArg.capture());
        verify(graphQlCallService, times(1)).getNearestAddressesBy(nearestAddressesArg.capture());
        verify(graphQlCallService, times(1)).getNearestStationsBy(nearestStationsArg.capture());
        assertThat(journeyApiTokenArg.getValue()).isEqualToComparingFieldByField(testData.get(GraphQlTab.JOURNEY_QUERY));
        assertThat(autocompleteAddressesArg.getValue()).isEqualToComparingFieldByField(testData.get(GraphQlTab.ADDRESS_AUTOCOMPLETION));
        assertThat(nearestAddressesArg.getValue()).isEqualToComparingFieldByField(testData.get(GraphQlTab.NEAREST_ADDRESSES));
        assertThat(nearestStationsArg.getValue()).isEqualToComparingFieldByField(testData.get(GraphQlTab.NEAREST_STATIONS));
    }

    @Test
    void test_executeGraphQlTestsWith_apiTokenMap_returns_four_failed_callStatusTabs_when_all_graphQlQueries_were_failed() {
        Map<GraphQlTab, ApiToken> testData = getTestSoftwareApiTokens();
        when(graphQlCallService.getJourneysBy(any(ApiToken.class))).thenReturn(Flux.empty());
        when(graphQlCallService.getAutocompleteAddressesBy(any(ApiToken.class))).thenReturn(Flux.empty());
        when(graphQlCallService.getNearestAddressesBy(any(ApiToken.class))).thenReturn(Flux.empty());
        when(graphQlCallService.getNearestStationsBy(any(ApiToken.class))).thenReturn(Flux.empty());

        Flux<CallStatus<GraphQlTab>> result = classUnderTest.executeGraphQlTestsWith(testData);

        StepVerifier.create(result)
                .assertNext(callStatusTab -> assertThat(callStatusTab).isEqualToComparingFieldByField(getFailedJourneyCallStatusTab()))
                .assertNext(callStatusTab -> assertThat(callStatusTab).isEqualToComparingFieldByField(getFailedAddressAutocompletionCallStatusTab()))
                .assertNext(callStatusTab -> assertThat(callStatusTab).isEqualToComparingFieldByField(getFailedNearestAddressesCallStatusTab()))
                .assertNext(callStatusTab -> assertThat(callStatusTab).isEqualToComparingFieldByField(getFailedNearestStationsCallStatusTab()))
                .verifyComplete();
    }

    @Test
    void test_executeGraphQlTestsWith_apiTokenMap_and_no_error_for_one_graphQlQuery_returns_one_callStatusTab() {
        Map<GraphQlTab, ApiToken> testData = getTestSoftwareApiTokens();
        when(graphQlCallService.getJourneysBy(any(ApiToken.class))).thenReturn(Flux.error(new Exception()));
        when(graphQlCallService.getAutocompleteAddressesBy(any(ApiToken.class))).thenReturn(Flux.error(new Exception()));
        when(graphQlCallService.getNearestAddressesBy(any(ApiToken.class))).thenReturn(Flux.error(new Exception()));

        Flux<CallStatus<GraphQlTab>> result = classUnderTest.executeGraphQlTestsWith(testData);

        StepVerifier.create(result)
                .assertNext(callStatusTab -> assertThat(callStatusTab).isEqualToComparingFieldByField(getFailedJourneyCallStatusTab()))
                .assertNext(callStatusTab -> assertThat(callStatusTab).isEqualToComparingFieldByField(getFailedAddressAutocompletionCallStatusTab()))
                .assertNext(callStatusTab -> assertThat(callStatusTab).isEqualToComparingFieldByField(getFailedNearestAddressesCallStatusTab()))
                .assertNext(callStatusTab -> assertThat(callStatusTab).isEqualToComparingFieldByField(getSuccessfulNearestStationsCallStatusTab()))
                .verifyComplete();
    }

    @Test
    void test_executeGraphQlTestsWith_apiTokenMap_and_no_exception_for_one_graphQlQuery_returns_one_callStatusTabq() {
        Map<GraphQlTab, ApiToken> testData = getTestSoftwareApiTokens();
        when(graphQlCallService.getJourneysBy(any(ApiToken.class))).thenThrow(new NullPointerException());
        when(graphQlCallService.getAutocompleteAddressesBy(any(ApiToken.class))).thenThrow(new NullPointerException());
        when(graphQlCallService.getNearestAddressesBy(any(ApiToken.class))).thenThrow(new NullPointerException());

        Flux<CallStatus<GraphQlTab>> result = classUnderTest.executeGraphQlTestsWith(testData);

        StepVerifier.create(result)
                .assertNext(callStatusTab -> assertThat(callStatusTab).isEqualToComparingFieldByField(getFailedJourneyCallStatusTab()))
                .assertNext(callStatusTab -> assertThat(callStatusTab).isEqualToComparingFieldByField(getFailedAddressAutocompletionCallStatusTab()))
                .assertNext(callStatusTab -> assertThat(callStatusTab).isEqualToComparingFieldByField(getFailedNearestAddressesCallStatusTab()))
                .assertNext(callStatusTab -> assertThat(callStatusTab).isEqualToComparingFieldByField(getSuccessfulNearestStationsCallStatusTab()))
                .verifyComplete();
    }

    @Test
    void test_executeGraphQlTestsWith_apiToken_and_removed_journey_tab_returns_zero_callStatus_tabs() {
        Map<GraphQlTab, ApiToken> testData = getTestSoftwareApiTokens();
        testData.remove(GraphQlTab.JOURNEY_QUERY);

        Flux<CallStatus<GraphQlTab>> result = classUnderTest.executeGraphQlTestsWith(testData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void test_executeGraphQlTestsWith_apiToken_and_journey_tab_as_null_returns_zero_callStatus_tabs() {
        Map<GraphQlTab, ApiToken> testData = getTestSoftwareApiTokens();
        testData.put(GraphQlTab.JOURNEY_QUERY, null);

        Flux<CallStatus<GraphQlTab>> result = classUnderTest.executeGraphQlTestsWith(testData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void test_executeGraphQlTestsWith_apiToken_and_removed_address_autocompletion_tab_returns_zero_callStatus_tabs() {
        Map<GraphQlTab, ApiToken> testData = getTestSoftwareApiTokens();
        testData.remove(GraphQlTab.ADDRESS_AUTOCOMPLETION);

        Flux<CallStatus<GraphQlTab>> result = classUnderTest.executeGraphQlTestsWith(testData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void test_executeGraphQlTestsWith_apiToken_and_removed_nearest_addresses_tab_returns_zero_callStatus_tabs() {
        Map<GraphQlTab, ApiToken> testData = getTestSoftwareApiTokens();
        testData.remove(GraphQlTab.NEAREST_ADDRESSES);

        Flux<CallStatus<GraphQlTab>> result = classUnderTest.executeGraphQlTestsWith(testData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void test_executeGraphQlTestsWith_apiToken_and_removed_nearest_stations_tab_returns_zero_callStatus_tabs() {
        Map<GraphQlTab, ApiToken> testData = getTestSoftwareApiTokens();
        testData.remove(GraphQlTab.NEAREST_STATIONS);

        Flux<CallStatus<GraphQlTab>> result = classUnderTest.executeGraphQlTestsWith(testData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }
}
