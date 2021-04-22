package de.blackforestsolutions.dravelopstestsoftware.testsoftware;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.GraphQlTab;
import de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsJsonMapper;
import de.blackforestsolutions.dravelopstestsoftware.configuration.ApiTokenConfiguration;
import de.blackforestsolutions.dravelopstestsoftware.configuration.CallStatusConfiguration;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.*;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.CallStatusObjectMother.*;
import static org.assertj.core.api.Assertions.assertThat;

@Import(value = {ApiTokenConfiguration.class, CallStatusConfiguration.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class CallStatusControllerTest {

    @Autowired
    private ApiToken journeyUserRequestApiToken;

    @Autowired
    private ApiToken autocompleteUserRequestApiToken;

    @Autowired
    private ApiToken nearestAddressesUserRequestApiToken;

    @Autowired
    private ApiToken nearestStationsUserRequestApiToken;

    @Autowired
    private String testsoftwareCallStatusUrl;

    @Test
    void test_executeTestsWith_correct_apiToken_returns_successful_callStatusTabs() throws JsonProcessingException {
        DravelOpsJsonMapper mapper = new DravelOpsJsonMapper();
        JavaType callStatusTabType = mapper.getTypeFactory().constructParametricType(CallStatus.class, GraphQlTab.class);
        JavaType callStatusListType = mapper.getTypeFactory().constructCollectionType(List.class, callStatusTabType);
        Map<GraphQlTab, ApiToken> testData = new HashMap<>();
        testData.put(GraphQlTab.JOURNEY, journeyUserRequestApiToken);
        testData.put(GraphQlTab.ADDRESS_AUTOCOMPLETION, autocompleteUserRequestApiToken);
        testData.put(GraphQlTab.NEAREST_ADDRESSES, nearestAddressesUserRequestApiToken);
        testData.put(GraphQlTab.NEAREST_STATIONS, nearestStationsUserRequestApiToken);

        ResponseEntity<String> result = executeTestsWith(testData);
        List<CallStatus<GraphQlTab>> mappedResult = mapper.readValue(result.getBody(), callStatusListType);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result).isNotNull();
        assertThat(mappedResult.size()).isEqualTo(4);
        assertThat(mappedResult).extracting(
                CallStatus::getStatus,
                CallStatus::getCalledObject
        ).containsExactlyInAnyOrder(
                Tuple.tuple(getSuccessfulNearestStationsCallStatusTab().getStatus(), getSuccessfulNearestStationsCallStatusTab().getCalledObject()),
                Tuple.tuple(getSuccessfulNearestAddressesCallStatusTab().getStatus(), getSuccessfulNearestAddressesCallStatusTab().getCalledObject()),
                Tuple.tuple(getSuccessfulAddressAutocompletionCallStatusTab().getStatus(), getSuccessfulAddressAutocompletionCallStatusTab().getCalledObject()),
                Tuple.tuple(getSuccessfulJourneyCallStatusTab().getStatus(), getSuccessfulJourneyCallStatusTab().getCalledObject())
        );
    }

    @Test
    void test_executeTestsWith_incorrect_apiToken_returns_failed_callStatusTabs() throws JsonProcessingException {
        DravelOpsJsonMapper mapper = new DravelOpsJsonMapper();
        JavaType callStatusTabType = mapper.getTypeFactory().constructParametricType(CallStatus.class, GraphQlTab.class);
        JavaType callStatusListType = mapper.getTypeFactory().constructCollectionType(List.class, callStatusTabType);
        Map<GraphQlTab, ApiToken> testData = new HashMap<>();
        testData.put(GraphQlTab.JOURNEY, getIncorrectJourneyUserRequestToken());
        testData.put(GraphQlTab.ADDRESS_AUTOCOMPLETION, getIncorrectAutocompleteUserRequestToken());
        testData.put(GraphQlTab.NEAREST_ADDRESSES, getIncorrectNearestAddressesUserRequestToken());
        testData.put(GraphQlTab.NEAREST_STATIONS, getIncorrectNearestStationsUserRequestToken());

        ResponseEntity<String> result = executeTestsWith(testData);
        List<CallStatus<GraphQlTab>> mappedResult = mapper.readValue(result.getBody(), callStatusListType);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result).isNotNull();
        assertThat(mappedResult.size()).isEqualTo(4);
        assertThat(mappedResult).extracting(
                CallStatus::getStatus,
                CallStatus::getCalledObject
        ).containsExactlyInAnyOrder(
                Tuple.tuple(getFailedNearestStationsCallStatusTab().getStatus(), getFailedNearestStationsCallStatusTab().getCalledObject()),
                Tuple.tuple(getFailedNearestAddressesCallStatusTab().getStatus(), getFailedNearestAddressesCallStatusTab().getCalledObject()),
                Tuple.tuple(getFailedAddressAutocompletionCallStatusTab().getStatus(), getFailedAddressAutocompletionCallStatusTab().getCalledObject()),
                Tuple.tuple(getFailedJourneyCallStatusTab().getStatus(), getFailedJourneyCallStatusTab().getCalledObject())
        );
    }


    private ResponseEntity<String> executeTestsWith(Map<GraphQlTab, ApiToken> testData) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        return restTemplate.postForEntity(testsoftwareCallStatusUrl, buildRequestBodyWith(testData), String.class);
    }

    private HttpEntity<String> buildRequestBodyWith(Map<GraphQlTab, ApiToken> testData) throws JsonProcessingException {
        DravelOpsJsonMapper mapper = new DravelOpsJsonMapper();
        String body = mapper.writeValueAsString(testData);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }
}
