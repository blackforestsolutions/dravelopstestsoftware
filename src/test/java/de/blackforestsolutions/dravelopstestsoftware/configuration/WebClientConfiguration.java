package de.blackforestsolutions.dravelopstestsoftware.configuration;

import de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsJsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@SpringBootConfiguration
public class WebClientConfiguration {

    private static final int BIT_HIGHER_UNIT = 1024;

    @Value("${stargate.protocol}")
    private String protocol;
    @Value("${stargate.host}")
    private String host;
    @Value("${stargate.port}")
    private int port;
    @Value("${stargate.path}")
    private String path;
    @Value("${webclient.maxBufferSizeMb}")
    private int maxBufferSizeMb;

    @Bean
    public ExchangeStrategies exchangeStrategies() {
        return ExchangeStrategies.builder()
                .codecs(configurer -> {
                    configurer.defaultCodecs().maxInMemorySize(maxBufferSizeMb * BIT_HIGHER_UNIT * BIT_HIGHER_UNIT);
                    configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(new DravelOpsJsonMapper()));
                    configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(new DravelOpsJsonMapper()));
                })
                .build();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .exchangeStrategies(exchangeStrategies())
                .baseUrl(buildBaseUrl())
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    private String buildBaseUrl() {
        return ""
                .concat(protocol)
                .concat("://")
                .concat(host)
                .concat(":")
                .concat(String.valueOf(port))
                .concat(path);
    }

    private ExchangeFilterFunction logRequest() {
        return (clientRequest, next) -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers()
                    .forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            log.info("Body: ", clientRequest.body());
            return next.exchange(clientRequest);
        };
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("Response: {}", clientResponse.headers().asHttpHeaders().get("property-header"));
            return Mono.just(clientResponse);
        });
    }
}
