package de.blackforestsolutions.dravelopstestsoftware.service;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.GraphQlTab;
import reactor.core.publisher.Flux;

import java.util.Map;

public interface GraphQlValidatorService {
    Flux<CallStatus<GraphQlTab>> executeGraphQlTestsWith(Map<GraphQlTab, ApiToken> testData);
}
