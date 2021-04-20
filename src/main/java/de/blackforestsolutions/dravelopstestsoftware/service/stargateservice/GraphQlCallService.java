package de.blackforestsolutions.dravelopstestsoftware.service.stargateservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopstestsoftware.model.Polygon;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GraphQlCallService {
    Flux<Journey> getJourneysBy(ApiToken apiToken);

    Flux<TravelPoint> getNearestAddressesBy(ApiToken apiToken);

    Flux<TravelPoint> getNearestStationsBy(ApiToken apiToken);

    Flux<TravelPoint> getAutocompleteAddressesBy(ApiToken apiToken);

    Flux<TravelPoint> getAllStations();

    Mono<Polygon> getOperatingArea();
}
