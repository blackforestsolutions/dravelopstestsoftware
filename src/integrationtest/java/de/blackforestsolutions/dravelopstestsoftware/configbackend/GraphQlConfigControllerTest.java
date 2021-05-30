package de.blackforestsolutions.dravelopstestsoftware.configbackend;


import com.fasterxml.jackson.databind.JavaType;
import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.GraphQlTab;
import de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsJsonMapper;
import de.blackforestsolutions.dravelopsgeneratedcontent.graphql.GraphQLApiConfig;
import de.blackforestsolutions.dravelopstestsoftware.configuration.GraphQlConfigConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(value = {GraphQlConfigConfiguration.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureMockMvc
public class GraphQlConfigControllerTest {

    @Autowired
    private String graphQlConfigBackendUrl;

    @Test
    void test_getGraphQlApiConfig_returns_a_graphQLApiConfig_object() throws Exception {
        DravelOpsJsonMapper mapper = new DravelOpsJsonMapper();

        ResponseEntity<String> result = getGraphQlApiConfig();
        GraphQLApiConfig mappedResult = mapper.readValue(result.getBody(), GraphQLApiConfig.class);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotEmpty();
        assertThat(mappedResult.getGraphql().getPlayground().getPageTitle()).isNotNull();
        assertThat(mappedResult.getGraphql().getPlayground().getSettings().getEditor().getTheme()).isNotNull();
        assertThat(mappedResult.getGraphql().getPlayground().getTabs().get(GraphQlTab.JOURNEY_QUERY.toString()).getArrivalPlaceholder()).isNotNull();
        assertThat(mappedResult.getGraphql().getPlayground().getTabs().get(GraphQlTab.JOURNEY_SUBSCRIPTION.toString()).getMaxResults()).isGreaterThan(0);
        assertThat(mappedResult.getGraphql().getPlayground().getTabs().get(GraphQlTab.NEAREST_ADDRESSES.toString()).getName()).isNotNull();
        assertThat(mappedResult.getGraphql().getPlayground().getTabs().get(GraphQlTab.NEAREST_STATIONS.toString()).getQuery()).isNotNull();
        assertThat(mappedResult.getGraphql().getPlayground().getTabs().get(GraphQlTab.ADDRESS_AUTOCOMPLETION.toString()).getLayers().size()).isGreaterThan(0);
        assertThat(mappedResult.getGraphql().getPlayground().getTabs().get(GraphQlTab.ALL_STATIONS.toString()).getName()).isNotNull();
        assertThat(mappedResult.getGraphql().getPlayground().getTabs().get(GraphQlTab.OPERATING_AREA.toString()).getMaxResults()).isGreaterThan(0);
    }

    @Test
    void test_putGraphQlApiConfig_returns_a_graphQLApiConfig_object() throws Exception {
        DravelOpsJsonMapper mapper = new DravelOpsJsonMapper();
        JavaType callStatusTabType = mapper.getTypeFactory().constructParametricType(CallStatus.class, GraphQlTab.class);
        JavaType callStatusListType = mapper.getTypeFactory().constructCollectionType(List.class, callStatusTabType);
        GraphQLApiConfig graphQLApiConfig = mapper.readValue(getGraphQlApiConfig().getBody(), GraphQLApiConfig.class);

        ResponseEntity<String> result = putGraphQlApiConfig(graphQLApiConfig);
        List<CallStatus<GraphQlTab>> mappedResult = mapper.readValue(result.getBody(), callStatusListType);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(mappedResult.size()).isEqualTo(4);
        assertThat(mappedResult).allMatch(graphQlTab -> graphQlTab.getStatus() != null);
    }

    private ResponseEntity<String> getGraphQlApiConfig() {
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        return restTemplate.getForEntity(graphQlConfigBackendUrl, String.class);
    }

    private ResponseEntity<String> putGraphQlApiConfig(GraphQLApiConfig apiConfig) {
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        return restTemplate.postForEntity(graphQlConfigBackendUrl, apiConfig, String.class);
    }
}
