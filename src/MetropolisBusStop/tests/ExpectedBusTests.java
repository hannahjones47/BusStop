package MetropolisBusStop.tests;

import MetropolisBusStop.impl.BusStatus;
import MetropolisBusStop.impl.ExpectedBus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for the ExpectedBus class
 */
class ExpectedBusTests {

    /**
     * Test that verifies that an IllegalArgumentException is thrown when a negative journey number
     * is passed to the constructor
     */
    @Test
    void CreateExpectedBus_InvalidTest_NegativeJourneyNo() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ExpectedBus("1",-1,"Destination", BusStatus.onTime, LocalTime.of(12,0), 0);
        });
    }

    /**
     * Test that verifies that an IllegalArgumentException is thrown when a negative delay
     * is passed to the constructor
     */
    @Test
    void CreateExpectedBus_InvalidTest_NegativeDelay() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ExpectedBus("3",0,"Destination", BusStatus.onTime, LocalTime.of(12,0), -1);
        });
    }

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
    void CreateExpectedBus_InvalidTest_InvalidTime(String time) {
        assertThrows(Exception.class, () -> {
            new ExpectedBus("10",9,"Destination", BusStatus.onTime, LocalTime.parse(time), -1);
        });
    }

    /**
     * Test that verifies that a valid ExpectedBus object is created when valid parameters are passed to the constructor
     */
    @Test
    void CreateExpectedBus_ValidTest() {
        assertDoesNotThrow(() -> {
            new ExpectedBus("42",5, "Destination", BusStatus.onTime, LocalTime.of(10,30), 0);
        });
    }

}
