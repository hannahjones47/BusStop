package MetropolisBusStop.tests;

import MetropolisBusStop.impl.BusStopDisplay;
import MetropolisBusStop.impl.exceptions.RouteDoesNotCallHereException;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class Test {
    Path projectDir = Paths.get("").toAbsolutePath();
    File stopInfoFile = new File(projectDir.resolve("src/MetropolisBusStop/configurationData/stop_info.csv").toString());
    File routesFile = new File(projectDir.resolve("src/MetropolisBusStop/configurationData/routes.csv").toString());
    File timetableFile = new File(projectDir.resolve("src/MetropolisBusStop/configurationData/timetable.csv").toString());
    BusStopDisplay busStopDisplay;

    @org.junit.jupiter.api.Test
    void createBusStopDisplay() {
        busStopDisplay = new BusStopDisplay(stopInfoFile, routesFile, timetableFile);
        assertEquals(busStopDisplay.routes.size(), 8);
    }

    @org.junit.jupiter.api.Test
    void testGetTimeOfNextBus() {
        busStopDisplay = new BusStopDisplay(stopInfoFile, routesFile, timetableFile);

        try {
            LocalTime timeOfNextBus = busStopDisplay.getTimeOfNextBus("25", LocalTime.of(11, 55));
            assertEquals(timeOfNextBus, LocalTime.of(11, 57));
        }
        catch (Exception e){
            fail();
        }
    }

    @org.junit.jupiter.api.Test
    void testGetTimeOfNextBus2() {
        busStopDisplay = new BusStopDisplay(stopInfoFile, routesFile, timetableFile);

        try {
            LocalTime timeOfNextBus = busStopDisplay.getTimeOfNextBus("17", LocalTime.of(7, 1));
            assertEquals(timeOfNextBus, LocalTime.of(7, 13));
        }
        catch (Exception e){
            fail();
        }
    }

    @org.junit.jupiter.api.Test
    void testGetTimeOfNextBus3() {
        busStopDisplay = new BusStopDisplay(stopInfoFile, routesFile, timetableFile);

        assertThrows(RouteDoesNotCallHereException.class, () -> {
            busStopDisplay.getTimeOfNextBus("7", LocalTime.of(13, 30));
        });
    }

    @org.junit.jupiter.api.Test
    void testDisplay() {
        busStopDisplay = new BusStopDisplay(stopInfoFile, routesFile, timetableFile);

        busStopDisplay.display(LocalTime.of(21,0)); // todo it doesnt esem to work if you input a time where there would be less than 10 buses coming after then that day.
    }
}

// todo test that exceptions are thrown, e.g. expected route can't have a negative delay or journeyNo