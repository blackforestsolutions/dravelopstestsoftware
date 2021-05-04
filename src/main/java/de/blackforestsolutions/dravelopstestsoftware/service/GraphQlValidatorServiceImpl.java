package de.blackforestsolutions.dravelopstestsoftware.service;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.GraphQlTab;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopstestsoftware.exceptionhandling.ExceptionHandlerService;
import de.blackforestsolutions.dravelopstestsoftware.service.stargateservice.GraphQlCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class GraphQlValidatorServiceImpl implements GraphQlValidatorService {

    private final GraphQlCallService graphQlCallService;
    private final ExceptionHandlerService exceptionHandlerService;

    @Autowired
    public GraphQlValidatorServiceImpl(GraphQlCallService graphQlCallService, ExceptionHandlerService exceptionHandlerService) {
        this.graphQlCallService = graphQlCallService;
        this.exceptionHandlerService = exceptionHandlerService;
    }

    @Override
    public Flux<CallStatus<GraphQlTab>> executeGraphQlTestsWith(Map<GraphQlTab, ApiToken> testData) {
        try {
            Objects.requireNonNull(testData.get(GraphQlTab.JOURNEY_QUERY), "journeyTabTestData is not allowed to be null");
            Objects.requireNonNull(testData.get(GraphQlTab.ADDRESS_AUTOCOMPLETION), "addressAutocompletionTabTestData is not allowed to be null");
            Objects.requireNonNull(testData.get(GraphQlTab.NEAREST_ADDRESSES), "nearestAddressesTabTestData is not allowed to be null");
            Objects.requireNonNull(testData.get(GraphQlTab.NEAREST_STATIONS), "nearestStationsTabTestData is not allowed to be null");
            return Flux.merge(
                    executeGraphQlTestWith(testData.get(GraphQlTab.JOURNEY_QUERY), GraphQlTab.JOURNEY_QUERY, graphQlCallService::getJourneysBy),
                    executeGraphQlTestWith(testData.get(GraphQlTab.ADDRESS_AUTOCOMPLETION), GraphQlTab.ADDRESS_AUTOCOMPLETION, graphQlCallService::getAutocompleteAddressesBy),
                    executeGraphQlTestWith(testData.get(GraphQlTab.NEAREST_ADDRESSES), GraphQlTab.NEAREST_ADDRESSES, graphQlCallService::getNearestAddressesBy),
                    executeGraphQlTestWith(testData.get(GraphQlTab.NEAREST_STATIONS), GraphQlTab.NEAREST_STATIONS, graphQlCallService::getNearestStationsBy)
            );
        } catch (Exception e) {
            return exceptionHandlerService.handleExceptions(e);
        }
    }

    private <T> Mono<CallStatus<GraphQlTab>> executeGraphQlTestWith(ApiToken testToken, GraphQlTab tab, Function<ApiToken, Flux<T>> graphqlCallService) {
        try {
            return Mono.just(testToken)
                    .flatMapMany(token -> graphqlCallService.apply(testToken))
                    .next()
                    .map(value -> new CallStatus<>(tab, Status.SUCCESS, null))
                    .switchIfEmpty(Mono.just(new CallStatus<>(tab, Status.FAILED, null)))
                    .onErrorResume(exceptionHandlerService::handleException);
        } catch (Exception e) {
            return exceptionHandlerService.handleException(e);
        }
    }

}
