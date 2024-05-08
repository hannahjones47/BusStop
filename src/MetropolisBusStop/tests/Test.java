package MetropolisBusStop.tests;

import MetropolisBusStop.impl.BusStopDisplay;
import MetropolisBusStop.impl.Route;
import MetropolisBusStop.impl.exceptions.RouteDoesNotCallHereException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class Test {
    Path projectDir = Paths.get("").toAbsolutePath();
    File stopInfoFile = new File(projectDir.resolve("src/MetropolisBusStop/configurationData/stop_info.csv").toString());
    File routesFile = new File(projectDir.resolve("src/MetropolisBusStop/configurationData/routes.csv").toString());
    File timetableFile = new File(projectDir.resolve("src/MetropolisBusStop/configurationData/timetable.csv").toString());
    BusStopDisplay busStopDisplay;

    @org.junit.jupiter.api.Test
    void CreateBusStopDisplay_ValidTest() {
        busStopDisplay = new BusStopDisplay(stopInfoFile, routesFile, timetableFile);
        assertEquals(busStopDisplay.getCallingRoutes().size(), 8);
    }

    @ParameterizedTest
    @CsvSource({
            "25, 11:55, 11:57",
            "17, 05:01, 07:13",
            "15, 21:43, 21:44"
    })
    void GetTimeOfNextBus_ValidTest(String routeNo, String currentTime, String expectedNextTime) {
        busStopDisplay = new BusStopDisplay(stopInfoFile, routesFile, timetableFile);

        try {
            LocalTime timeOfNextBus = busStopDisplay.getTimeOfNextBus(routeNo, LocalTime.parse(currentTime));
            assertEquals(timeOfNextBus, LocalTime.parse(expectedNextTime));
        } catch (Exception e) {
            fail();
        }
    }

    @org.junit.jupiter.api.Test
    void GetTimeOfNextBus_InvalidTest_NotExistentRoute() {
        busStopDisplay = new BusStopDisplay(stopInfoFile, routesFile, timetableFile);

        assertThrows(RouteDoesNotCallHereException.class, () -> {
            busStopDisplay.getTimeOfNextBus("7", LocalTime.of(13, 30));
        });
    }

    @org.junit.jupiter.api.Test
    void GetCallingRoutes_ValidTest() {
        busStopDisplay = new BusStopDisplay(stopInfoFile, routesFile, timetableFile);

        Map<String, Route> actualRoutes = busStopDisplay.getCallingRoutes();

        Map<String, Route> expectedRoutes = Map.of(
                "3", new Route("3", "Vila Nova", "Centennial Park", null),
                "6", new Route("6", "Constantine Street", "New Troy Boulevard", null),
                "12", new Route("12", "Topaz Lane", "Planet Square", null),
                "15", new Route("15", "Los Malos", "Gotham", null),
                "17", new Route("17", "Smallville", "Los Malos", null),
                "21", new Route("21", "Montgomery Avenue", "Smallvillle", null),
                "25", new Route("25", "Centennial Park", "Topaz Lane", null),
                "33", new Route("33", "Sweetspot", "Piazza Guapa", null)
        );

        assertEquals(expectedRoutes.size(), actualRoutes.size());
        expectedRoutes.forEach((routeNo, expectedRoute) ->
                assertEquals(expectedRoute.destination, actualRoutes.get(routeNo).destination));

    }

    @org.junit.jupiter.api.Test
    void GetDepartureTimes_ValidTest() {
        busStopDisplay = new BusStopDisplay(stopInfoFile, routesFile, timetableFile);

        List<LocalTime> expectedDepartureTimes = Arrays.asList(
                LocalTime.of(8, 11),
                LocalTime.of(9, 11),
                LocalTime.of(10, 11),
                LocalTime.of(11, 11),
                LocalTime.of(12, 11),
                LocalTime.of(13, 11),
                LocalTime.of(14, 11),
                LocalTime.of(15, 11),
                LocalTime.of(16, 11),
                LocalTime.of(17, 11),
                LocalTime.of(18, 11),
                LocalTime.of(19, 11),
                LocalTime.of(20, 11),
                LocalTime.of(21, 11)
        );

        List<LocalTime> actualDepartureTimes;
        try {
            actualDepartureTimes = busStopDisplay.getDepartureTimes("3");

            assertEquals(expectedDepartureTimes.size(), actualDepartureTimes.size());
            for (int i = 0; i < expectedDepartureTimes.size(); i++) {
                assertEquals(expectedDepartureTimes.get(i), actualDepartureTimes.get(i));
            }

        } catch (RouteDoesNotCallHereException e) {
            fail();
        }
    }

    @org.junit.jupiter.api.Test
    void GetDepartureTimes_InvalidTest_NotExistentRoute() {
        busStopDisplay = new BusStopDisplay(stopInfoFile, routesFile, timetableFile);

        assertThrows(RouteDoesNotCallHereException.class, () -> {
            busStopDisplay.getDepartureTimes("99");
        });
    }

    @org.junit.jupiter.api.Test
    void Display_ValidTest() {
        busStopDisplay = new BusStopDisplay(stopInfoFile, routesFile, timetableFile);

        busStopDisplay.display(LocalTime.of(21,0)); // todo it doesnt esem to work if you input a time where there would be less than 10 buses coming after then that day.
    }
}

// todo test that exceptions are thrown, e.g. expected route can't have a negative delay or journeyNo