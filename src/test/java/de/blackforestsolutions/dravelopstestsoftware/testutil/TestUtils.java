package de.blackforestsolutions.dravelopstestsoftware.testutil;

import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import org.springframework.data.geo.Metrics;

import java.util.function.Consumer;

import static de.blackforestsolutions.dravelopstestsoftware.configuration.GeocodingConfiguration.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TestUtils {

    public static Consumer<Journey> getLegPropertiesAssertions() {
        return journey -> {
            assertThat(journey.getLegs())
                    .allMatch(leg -> leg.getDelayInMinutes().toMillis() >= 0)
                    .allMatch(leg -> leg.getDistanceInKilometers().getValue() > 0)
                    .allMatch(leg -> leg.getVehicleType() != null)
                    .allMatch(leg -> leg.getWaypoints() != null)
                    .allMatch(leg -> leg.getWaypoints().stream().allMatch(waypoint -> waypoint.getX() >= MIN_WGS_84_LONGITUDE))
                    .allMatch(leg -> leg.getWaypoints().stream().allMatch(waypoint -> waypoint.getX() <= MAX_WGS_84_LONGITUDE))
                    .allMatch(leg -> leg.getWaypoints().stream().allMatch(waypoint -> waypoint.getY() >= MIN_WGS_84_LATITUDE))
                    .allMatch(leg -> leg.getWaypoints().stream().allMatch(waypoint -> waypoint.getY() <= MAX_WGS_84_LATITUDE))
                    .allMatch(leg -> leg.getIntermediateStops().size() == 0 || leg.getIntermediateStops().size() > 0)
                    .allMatch(leg -> leg.getVehicleName() != null)
                    .allMatch(leg -> leg.getVehicleNumber() != null);
            assertThat(journey.getLegs())
                    .first()
                    .matches(leg -> leg.getDeparture().getArrivalTime() == null);
            assertThat(journey.getLegs())
                    .last()
                    .matches(leg -> leg.getArrival().getDepartureTime() == null);
        };
    }

    public static Consumer<Journey> getArrivalAndDepartureLegAssertions() {
        return journey -> {
            assertThat(journey.getLegs())
                    .allMatch(leg -> leg.getDeparture() != null)
                    .allMatch(leg -> !leg.getDeparture().getName().isEmpty())
                    .allMatch(leg -> leg.getDeparture().getPoint() != null)
                    .allMatch(leg -> leg.getDeparture().getPoint().getX() >= MIN_WGS_84_LONGITUDE)
                    .allMatch(leg -> leg.getDeparture().getPoint().getX() <= MAX_WGS_84_LONGITUDE)
                    .allMatch(leg -> leg.getDeparture().getPoint().getY() >= MIN_WGS_84_LATITUDE)
                    .allMatch(leg -> leg.getDeparture().getPoint().getY() <= MAX_WGS_84_LATITUDE)
                    .allMatch(leg -> leg.getDeparture().getDistanceInKilometers() == null)
                    .allMatch(leg -> leg.getArrival() != null)
                    .allMatch(leg -> !leg.getArrival().getName().isEmpty())
                    .allMatch(leg -> leg.getArrival().getPoint() != null)
                    .allMatch(leg -> leg.getArrival().getPoint().getX() >= MIN_WGS_84_LONGITUDE)
                    .allMatch(leg -> leg.getArrival().getPoint().getX() <= MAX_WGS_84_LONGITUDE)
                    .allMatch(leg -> leg.getArrival().getPoint().getY() >= MIN_WGS_84_LATITUDE)
                    .allMatch(leg -> leg.getArrival().getPoint().getY() <= MAX_WGS_84_LATITUDE)
                    .allMatch(leg -> leg.getArrival().getDistanceInKilometers() == null);
            assertThat(journey.getLegs())
                    .first()
                    .matches(leg -> leg.getDeparture().getArrivalTime() == null);
            assertThat(journey.getLegs())
                    .last()
                    .matches(leg -> leg.getArrival().getDepartureTime() == null);
        };
    }

    public static Consumer<TravelPoint> getAutocompleteAddressesAssertions() {
        return travelPoint -> {
            assertThat(travelPoint).isNotNull();
            assertThat(travelPoint.getName()).isNotEmpty();
            assertThat(travelPoint.getPoint()).isNotNull();
            assertThat(travelPoint.getPoint().getX()).isGreaterThanOrEqualTo(MIN_WGS_84_LONGITUDE);
            assertThat(travelPoint.getPoint().getX()).isLessThanOrEqualTo(MAX_WGS_84_LONGITUDE);
            assertThat(travelPoint.getPoint().getY()).isGreaterThanOrEqualTo(MIN_WGS_84_LATITUDE);
            assertThat(travelPoint.getPoint().getY()).isLessThanOrEqualTo(MAX_WGS_84_LATITUDE);
            assertThat(travelPoint.getPlatform()).isEmpty();
            assertThat(travelPoint.getArrivalTime()).isNull();
            assertThat(travelPoint.getDepartureTime()).isNull();
        };
    }

    public static Consumer<TravelPoint> getNearestAddressesAssertions() {
        return travelPoint -> {
            assertThat(travelPoint).isNotNull();
            assertThat(travelPoint.getName()).isNotEmpty();
            assertThat(travelPoint.getPoint()).isNotNull();
            assertThat(travelPoint.getPoint().getX()).isGreaterThanOrEqualTo(MIN_WGS_84_LONGITUDE);
            assertThat(travelPoint.getPoint().getX()).isLessThanOrEqualTo(MAX_WGS_84_LONGITUDE);
            assertThat(travelPoint.getPoint().getY()).isGreaterThanOrEqualTo(MIN_WGS_84_LATITUDE);
            assertThat(travelPoint.getPoint().getY()).isLessThanOrEqualTo(MAX_WGS_84_LATITUDE);
            assertThat(travelPoint.getDistanceInKilometers()).isNotNull();
            assertThat(travelPoint.getDistanceInKilometers().getValue()).isGreaterThanOrEqualTo(MIN_DISTANCE_IN_KILOMETERS_TO_POINT);
            assertThat(travelPoint.getDistanceInKilometers().getMetric()).isEqualTo(Metrics.KILOMETERS);
            assertThat(travelPoint.getDepartureTime()).isNull();
            assertThat(travelPoint.getArrivalTime()).isNull();
            assertThat(travelPoint.getPlatform()).isEmpty();
        };
    }

    public static Consumer<TravelPoint> getAllStationsAssertions() {
        return travelPoint -> {
            assertThat(travelPoint.getName()).isNotEmpty();
            assertThat(travelPoint.getPoint()).isNotNull();
            assertThat(travelPoint.getPoint().getX()).isGreaterThanOrEqualTo(MIN_WGS_84_LONGITUDE);
            assertThat(travelPoint.getPoint().getX()).isLessThanOrEqualTo(MAX_WGS_84_LONGITUDE);
            assertThat(travelPoint.getPoint().getY()).isGreaterThanOrEqualTo(MIN_WGS_84_LATITUDE);
            assertThat(travelPoint.getPoint().getY()).isLessThanOrEqualTo(MAX_WGS_84_LATITUDE);
            assertThat(travelPoint.getArrivalTime()).isNull();
            assertThat(travelPoint.getDepartureTime()).isNull();
            assertThat(travelPoint.getPlatform()).isNotNull();
        };
    }
}
