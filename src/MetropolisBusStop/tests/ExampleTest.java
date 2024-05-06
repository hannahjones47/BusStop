package MetropolisBusStop.tests;

import MetropolisBusStop.impl.BusStopDisplay;
import MetropolisBusStop.impl.ExpectedBus;
import org.junit.jupiter.api.Test;
import MetropolisBusStop.impl.Example;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ExampleTest {

    @Test
    void add() {
        assertEquals(4, Example.add(2, 2));
    }

    @Test
    void add2() {
        assertEquals(5, Example.add(3, 2));
    }

    @Test
    void createBusStopDisplay() {
        Path projectDir = Paths.get("").toAbsolutePath();
        File stopInfoFile = new File(projectDir.resolve("src/MetropolisBusStop/configurationData/stop_info.csv").toString());
        File routesFile = new File(projectDir.resolve("src/MetropolisBusStop/configurationData/routes.csv").toString());
        File timetableFile = new File(projectDir.resolve("src/MetropolisBusStop/configurationData/timetable.csv").toString());

        BusStopDisplay busStopDisplay = new BusStopDisplay(stopInfoFile, routesFile, timetableFile);

        assertEquals(busStopDisplay.routes.size(), 8);
    }
}

// todo test that exceptions are thrown, e.g. expected route can't have a negative delay or journeyNo