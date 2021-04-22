package de.blackforestsolutions.dravelopstestsoftware.service.stargateservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GraphQlCallServiceTest {

    private final WebClient webClient = WebClient.create();
    private final GraphQlCallService classUnderTest = new GraphQlCallServiceImpl(webClient);

    @Test
    void test_getJourneysBy_apiToken_and_departureCoordinate_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getJourneyUserRequestToken());
        testData.setDepartureCoordinate(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.getJourneysBy(testData));
    }

    @Test
    void test_getJourneysBy_apiToken_and_arrivalCoordinate_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getJourneyUserRequestToken());
        testData.setArrivalCoordinate(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.getJourneysBy(testData));
    }

    @Test
    void test_getJourneysBy_apiToken_and_dateTime_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getJourneyUserRequestToken());
        testData.setDateTime(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.getJourneysBy(testData));
    }

    @Test
    void test_getJourneysBy_apiToken_and_isArrivalDateTime_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getJourneyUserRequestToken());
        testData.setIsArrivalDateTime(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.getJourneysBy(testData));
    }

    @Test
    void test_getJourneysBy_apiToken_and_language_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getJourneyUserRequestToken());
        testData.setLanguage(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.getJourneysBy(testData));
    }

    @Test
    void test_getNearestAddressesBy_apiToken_and_arrivalCoordinate_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getNearestAddressesUserRequestToken());
        testData.setArrivalCoordinate(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.getNearestAddressesBy(testData));
    }

    @Test
    void test_getNearestAddressesBy_apiToken_and_radiusInKilometers_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getNearestAddressesUserRequestToken());
        testData.setRadiusInKilometers(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.getNearestAddressesBy(testData));
    }

    @Test
    void test_getNearestAddressesBy_apiToken_and_language_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getNearestAddressesUserRequestToken());
        testData.setLanguage(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.getNearestAddressesBy(testData));
    }

    @Test
    void test_getNearestStationsBy_apiToken_and_arrivalCoordinate_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getNearestStationsUserRequestToken());
        testData.setArrivalCoordinate(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.getNearestStationsBy(testData));
    }

    @Test
    void test_getNearestStationsBy_apiToken_and_radiusInKilometers_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getNearestStationsUserRequestToken());
        testData.setRadiusInKilometers(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.getNearestStationsBy(testData));
    }

    @Test
    void test_getNearestStationsBy_apiToken_and_language_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getNearestStationsUserRequestToken());
        testData.setLanguage(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.getNearestStationsBy(testData));
    }

    @Test
    void test_getAutocompleteAddressesBy_apiToken_and_departure_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getAutocompleteUserRequestToken());
        testData.setDeparture(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.getAutocompleteAddressesBy(testData));
    }

    @Test
    void test_getAutocompleteAddressesBy_apiToken_and_language_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getAutocompleteUserRequestToken());
        testData.setLanguage(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.getAutocompleteAddressesBy(testData));
    }
}
