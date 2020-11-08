package de.blackforestsolutions.dravelopstestsoftware.stargateservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.kickstart.spring.webclient.boot.GraphQLRequest;
import graphql.kickstart.spring.webclient.boot.GraphQLWebClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Import(JourneyResolverConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JourneyResolverTest {


//    @Test
//    void test_() {
//
//        webClient.flux()
//
//
//    }
//
//    private Flux<String> test(String resource) {
//        return GraphQLWebClient
//                .newInstance(WebClient.create(), new ObjectMapper())
//                .flux()
//    }
}
