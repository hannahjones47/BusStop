package MetropolisBusStop.tests;

import MetropolisBusStop.impl.BusStatus;
import MetropolisBusStop.impl.BusStopDisplay;
import MetropolisBusStop.impl.Route;
import MetropolisBusStop.impl.exceptions.RouteDoesNotCallHereException;
import org.junit.jupiter.api.Test;
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

class BusStopDisplayTests {

    Path projectDir = Paths.get("").toAbsolutePath();
    File stopInfoFile = new File(projectDir.resolve("src/MetropolisBusStop/configurationData/stop_info.csv").toString());
    File routesFile = new File(projectDir.resolve("src/MetropolisBusStop/configurationData/routes.csv").toString());
    File timetableFile = new File(projectDir.resolve("src/MetropolisBusStop/configurationData/timetable.csv").toString());
    BusStopDisplay busStopDisplay;

    public BusStopDisplayTests() throws IOException {
        busStopDisplay = new BusStopDisplay(stopInfoFile, routesFile, timetableFile);
    }

    @Test
    void CreateBusStopDisplay_ValidTest() {
        assertEquals(busStopDisplay.getCallingRoutes().size(), 8);
        assertEquals("BS05", busStopDisplay.getId());
        assertEquals("Sweetspot", busStopDisplay.getName());
    }

    @Test
    void CreateBusStopDisplay_InvalidTest_StopInfoFile() {
        assertThrows(IOException.class, () -> {
            new BusStopDisplay(new File("nonExistentFile.csv"), routesFile, stopInfoFile);
        });
    }

    @Test
    void CreateBusStopDisplay_InvalidTest_RoutesFile() {
        assertThrows(IOException.class, () -> {
            new BusStopDisplay(stopInfoFile, new File("nonExistentFile.csv"), timetableFile);
        });
    }

    @Test
    void CreateBusStopDisplay_InvalidTest_TimetableInfoFile() {
        assertThrows(IOException.class, () -> {
            new BusStopDisplay(stopInfoFile, routesFile, new File("nonExistentFile.csv"));
        });
    }

    @ParameterizedTest
    @CsvSource({
            "25, 11:55, 11:57",
            "17, 05:01, 07:13",
            "15, 21:43, 21:44"
    })
    void GetTimeOfNextBus_ValidTest(String routeNo, String currentTime, String expectedNextTime) {

        try {
            LocalTime timeOfNextBus = busStopDisplay.getTimeOfNextBus(routeNo, LocalTime.parse(currentTime));
            assertEquals(timeOfNextBus, LocalTime.parse(expectedNextTime));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void GetTimeOfNextBus_InvalidTest_NotExistentRoute() {

        assertThrows(RouteDoesNotCallHereException.class, () -> {
            busStopDisplay.getTimeOfNextBus("7", LocalTime.of(13, 30));
        });
    }

    @Test
    void GetCallingRoutes_ValidTest() {

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

    @Test
    void GetDepartureTimes_ValidTest() {

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

    @Test
    void GetDepartureTimes_InvalidTest_NotExistentRoute() {

        assertThrows(RouteDoesNotCallHereException.class, () -> {
            busStopDisplay.getDepartureTimes("99");
        });
    }

    @Test
    void Display_ValidTest() {
        busStopDisplay.display(LocalTime.of(21,1)); // todo it doesnt esem to work if you input a time where there would be less than 10 buses coming after then that day.
    }

    @Test
    void UpdateBusAsDeparted_ValidTest() {
        busStopDisplay.updateBusAsDeparted("3", 1);
        assertFalse(busStopDisplay.getExpectedBuses().containsKey("R3J1"));
    }

    @Test
    void UpdateBusAsDelayed_ValidTest() {
        int delay = 5;
        busStopDisplay.updateBusAsDelayed("3", 1, delay);
        assertEquals(BusStatus.delayed, busStopDisplay.getExpectedBuses().get("R3J1").getStatus());
        assertEquals(delay, busStopDisplay.getExpectedBuses().get("R3J1").getDelay());
    }

    @Test
    void UpdateBusAsCancelled_ValidTest() {
        busStopDisplay.updateBusAsCancelled("3", 1);
        assertEquals(BusStatus.cancelled, busStopDisplay.getExpectedBuses().get("R3J1").getStatus());
    }

    @Test
    void UpdateBusAsDelayed_AlreadyCancelledTest() {
        busStopDisplay.updateBusAsCancelled("3", 1);
        busStopDisplay.updateBusAsDelayed("3", 1, 5);
        assertEquals(BusStatus.cancelled, busStopDisplay.getExpectedBuses().get("R3J1").getStatus());
        assertNotEquals(5, busStopDisplay.getExpectedBuses().get("R3J1").getDelay());
    }

    @Test
    void UpdateBusAsCancelled_NotExistentBusTest() {
        assertDoesNotThrow(() -> busStopDisplay.updateBusAsCancelled("99", 1));
    }

    @Test
    void UpdateBusAsDeparted_NotExistentBusTest() {
        assertDoesNotThrow(() -> busStopDisplay.updateBusAsDeparted("99", 1));
    }

    @Test
    void UpdateBusAsDelayed_NotExistentBusTest() {
        assertDoesNotThrow(() -> busStopDisplay.updateBusAsDelayed("99", 1, 5));
    }

    @Test
    void GetStatusDisplayValue_ValidTest() {
        assertEquals("on time", busStopDisplay.getStatusDisplayValue(BusStatus.onTime, 0));
        assertEquals("5 minutes delay", busStopDisplay.getStatusDisplayValue(BusStatus.delayed, 5));
        assertEquals("cancelled", busStopDisplay.getStatusDisplayValue(BusStatus.cancelled, 0));
    }
}

// todo test that exceptions are thrown, e.g. expected route can't have a negative delay or journeyNo