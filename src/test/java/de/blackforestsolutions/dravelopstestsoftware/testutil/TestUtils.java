package de.blackforestsolutions.dravelopstestsoftware.testutil;

import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public class TestUtils {

    public static Consumer<Journey> getLegPropertiesAssertions() {
        return journey -> {
            assertThat(journey.getLegs())
                    .allMatch(leg -> leg.getDeparture() != null)
                    .allMatch(leg -> !leg.getDeparture().getName().isEmpty())
                    .allMatch(leg -> leg.getDeparture().getPoint() != null)
                    .allMatch(leg -> leg.getArrival() != null)
                    .allMatch(leg -> !leg.getArrival().getName().isEmpty())
                    .allMatch(leg -> leg.getArrival().getPoint() != null);
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
            assertThat(journey.getLanguage().getLanguage().length()).isEqualTo(2);
            assertThat(journey.getLegs())
                    .allMatch(leg -> leg.getDeparture() != null)
                    .allMatch(leg -> !leg.getDeparture().getName().isEmpty())
                    .allMatch(leg -> leg.getDeparture().getPoint() != null)
                    .allMatch(leg -> leg.getArrival() != null)
                    .allMatch(leg -> !leg.getArrival().getName().isEmpty())
                    .allMatch(leg -> leg.getArrival().getPoint() != null);
            assertThat(journey.getLegs())
                    .first()
                    .matches(leg -> leg.getDeparture().getArrivalTime() == null);
            assertThat(journey.getLegs())
                    .last()
                    .matches(leg -> leg.getArrival().getDepartureTime() == null);
        };
    }

    public static Consumer<TravelPoint> getTravelPointAssertions() {
        return travelPoint -> {
            assertThat(travelPoint).isNotNull();
            assertThat(travelPoint.getName()).isNotEmpty();
            assertThat(travelPoint.getPoint()).isNotNull();
            assertThat(travelPoint.getPlatform()).isEmpty();
            assertThat(travelPoint.getArrivalTime()).isNull();
            assertThat(travelPoint.getDepartureTime()).isNull();
        };
    }
}
