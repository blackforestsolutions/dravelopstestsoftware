package de.blackforestsolutions.dravelopstestsoftware.controller;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.GraphQlTab;
import de.blackforestsolutions.dravelopstestsoftware.service.GraphQlValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tests")
public class CallStatusController {

    private final GraphQlValidatorService graphQlValidatorService;

    @Autowired
    public CallStatusController(GraphQlValidatorService graphQlValidatorService) {
        this.graphQlValidatorService = graphQlValidatorService;
    }

    @RequestMapping(value = "/execute", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<CallStatus<GraphQlTab>>> executeTestsWith(Map<GraphQlTab, ApiToken> testData) {
        return graphQlValidatorService.executeGraphQlTestsWith(testData)
                .collectList();
    }
}
