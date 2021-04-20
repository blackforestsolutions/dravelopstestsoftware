package de.blackforestsolutions.dravelopstestsoftware.service.stargateservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsJsonMapper;
import de.blackforestsolutions.dravelopstestsoftware.model.Polygon;
import graphql.kickstart.spring.webclient.boot.GraphQLWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

@Service
public class GraphQlCallServiceImpl implements GraphQlCallService {

    // JourneyResolver
    public static final String DEPARTURE_LATITUDE_PARAM = "departureLatitude";
    public static final String DEPARTURE_LONGITUDE_PARAM = "departureLongitude";
    public static final String ARRIVAL_LATITUDE_PARAM = "arrivalLatitude";
    public static final String ARRIVAL_LONGITUDE_PARAM = "arrivalLongitude";
    public static final String DATE_TIME_PARAM = "dateTime";
    public static final String IS_ARRIVAL_DATE_TIME_PARAM = "isArrivalDateTime";

    // TravelPointResolver
    public static final String TEXT_PARAM = "text";
    public static final String LONGITUDE_PARAM = "longitude";
    public static final String LATITUDE_PARAM = "latitude";
    public static final String RADIUS_IN_KILOMETERS_PARAMS = "radiusInKilometers";

    // JourneyResolver and TravelPointResolver
    public static final String LANGUAGE_PARAM = "language";

    private final WebClient webClient;

    @Autowired
    public GraphQlCallServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Flux<Journey> getJourneysBy(ApiToken apiToken) {
        return GraphQLWebClient
                .newInstance(webClient, new DravelOpsJsonMapper())
                .flux("graphql/get-journeys-max-parameters.graphql", convertToGraphqlParametersWith(apiToken), Journey.class);
    }

    @Override
    public Flux<TravelPoint> getNearestAddressesBy(ApiToken apiToken) {
        return GraphQLWebClient
                .newInstance(webClient, new DravelOpsJsonMapper())
                .flux("graphql/get-nearest-addresses-max-parameters.graphql", convertNearestApiTokenToGraphqlParametersWith(apiToken), TravelPoint.class);
    }

    @Override
    public Flux<TravelPoint> getNearestStationsBy(ApiToken apiToken) {
        return GraphQLWebClient
                .newInstance(webClient, new DravelOpsJsonMapper())
                .flux("graphql/get-nearest-stations-max-parameters.graphql", convertNearestApiTokenToGraphqlParametersWith(apiToken), TravelPoint.class);
    }

    @Override
    public Flux<TravelPoint> getAutocompleteAddressesBy(ApiToken apiToken) {
        return GraphQLWebClient
                .newInstance(webClient, new DravelOpsJsonMapper())
                .flux("graphql/get-autocomplete-addresses-max-parameters.graphql", convertAutocompleteAddressesApiTokenToGraphqlParametersWith(apiToken), TravelPoint.class);
    }

    @Override
    public Flux<TravelPoint> getAllStations() {
        return GraphQLWebClient
                .newInstance(webClient, new DravelOpsJsonMapper())
                .flux("graphql/get-all-stations-parameters.graphql", TravelPoint.class);
    }

    @Override
    public Mono<Polygon> getOperatingArea() {
        return GraphQLWebClient
                .newInstance(webClient, new DravelOpsJsonMapper())
                .post("graphql/get-polygon.graphql", Polygon.class);
    }

    private Map<String, Object> convertAutocompleteAddressesApiTokenToGraphqlParametersWith(ApiToken apiToken) {
        Objects.requireNonNull(apiToken.getDeparture(), "departure is not allowed to be null");
        Objects.requireNonNull(apiToken.getLanguage(), "language is not allowed to be null");
        return Map.of(
                TEXT_PARAM, apiToken.getDeparture(),
                LANGUAGE_PARAM, apiToken.getLanguage()
        );
    }

    private Map<String, Object> convertNearestApiTokenToGraphqlParametersWith(ApiToken apiToken) {
        Objects.requireNonNull(apiToken.getArrivalCoordinate(), "arrivalCoordinate is not allowed to be null");
        Objects.requireNonNull(apiToken.getRadiusInKilometers(), "radiusInKilometers is not allowed to be null");
        Objects.requireNonNull(apiToken.getLanguage(), "language is not allowed to be null");
        return Map.of(
                LONGITUDE_PARAM, apiToken.getArrivalCoordinate().getX(),
                LATITUDE_PARAM, apiToken.getArrivalCoordinate().getY(),
                RADIUS_IN_KILOMETERS_PARAMS, apiToken.getRadiusInKilometers().getValue(),
                LANGUAGE_PARAM, apiToken.getLanguage()
        );
    }

    private Map<String, Object> convertToGraphqlParametersWith(ApiToken apiToken) {
        Objects.requireNonNull(apiToken.getDepartureCoordinate(), "departureCoordinate is not allowed to be null");
        Objects.requireNonNull(apiToken.getArrivalCoordinate(), "arrivalCoordinate is not allowed to be null");
        Objects.requireNonNull(apiToken.getDateTime(), "dateTime is not allowed to be null");
        Objects.requireNonNull(apiToken.getIsArrivalDateTime(), "isArrivalDateTime is not allowed to be null");
        Objects.requireNonNull(apiToken.getLanguage(), "language is not allowed to be null");
        return Map.of(
                DEPARTURE_LATITUDE_PARAM, apiToken.getDepartureCoordinate().getY(),
                DEPARTURE_LONGITUDE_PARAM, apiToken.getDepartureCoordinate().getX(),
                ARRIVAL_LATITUDE_PARAM, apiToken.getArrivalCoordinate().getY(),
                ARRIVAL_LONGITUDE_PARAM, apiToken.getArrivalCoordinate().getX(),
                DATE_TIME_PARAM, apiToken.getDateTime(),
                IS_ARRIVAL_DATE_TIME_PARAM, apiToken.getIsArrivalDateTime(),
                LANGUAGE_PARAM, apiToken.getLanguage().toString()
        );
    }
}
