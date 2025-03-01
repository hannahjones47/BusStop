package MetropolisBusStop.tests;

import MetropolisBusStop.impl.Route;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Route class
 */
class RouteTests {

    /**
     * Test that verifies that an exception is thrown when an invalid time is passed to the constructor
     */
    @ParameterizedTest
    @CsvSource({
            "24:00",
            "25:30",
            "23:60",
            "08:70"
    })
    void CreateRoute_InvalidTest_InvalidTime(String time) {
        assertThrows(Exception.class, () -> {
            List<LocalTime> schedule = new ArrayList<>();
            schedule.add(LocalTime.parse(time));
            new Route("1","Destination", "Origin", schedule);
        });
    }

    /**
     * Test that verifies that a valid Route object is created when valid parameters are passed to the constructor
     */
    @ParameterizedTest
    @CsvSource({
            "23:59",
            "12:20",
            "03:59",
            "00:00"
    })
    void CreateRoute_ValidTest(String time) {
        assertDoesNotThrow(() -> {
            List<LocalTime> schedule = new ArrayList<>();
            schedule.add(LocalTime.parse(time));
            new Route("1","Destination", "Origin", schedule);
        });
    }

}