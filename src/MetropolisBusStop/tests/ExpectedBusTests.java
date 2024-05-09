package MetropolisBusStop.tests;

import MetropolisBusStop.impl.BusStatus;
import MetropolisBusStop.impl.BusStopDisplay;
import MetropolisBusStop.impl.ExpectedBus;
import MetropolisBusStop.impl.Route;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExpectedBusTests {

    public ExpectedBusTests() {
    }

    @Test
    void CreateExpectedBus_InvalidTest_NegativeJourneyNo() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ExpectedBus("1",-1,"Destination", BusStatus.onTime, LocalTime.of(12,0), 0);
        });
    }

    @Test
    void CreateExpectedBus_InvalidTest_NegativeDelay() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ExpectedBus("3",0,"Destination", BusStatus.onTime, LocalTime.of(12,0), -1);
        });
    }

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

    @Test
    void CreateExpectedBus_ValidTest() {
        assertDoesNotThrow(() -> {
            new ExpectedBus("42",5, "Destination", BusStatus.onTime, LocalTime.of(10,30), 0);
        });
    }

}
