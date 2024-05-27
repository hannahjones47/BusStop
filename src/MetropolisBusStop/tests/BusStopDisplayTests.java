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

/**
 * Test class for the BusStopDisplay class
 */
class BusStopDisplayTests {

    Path projectDir = Paths.get("").toAbsolutePath();
    File stopInfoFile = new File(projectDir.resolve("src/MetropolisBusStop/configurationData/stop_info.csv").toString());
    File routesFile = new File(projectDir.resolve("src/MetropolisBusStop/configurationData/routes.csv").toString());
    File timetableFile = new File(projectDir.resolve("src/MetropolisBusStop/configurationData/timetable.csv").toString());
    BusStopDisplay busStopDisplay;

    public BusStopDisplayTests() throws IOException {
        busStopDisplay = new BusStopDisplay(stopInfoFile, routesFile, timetableFile);
    }

    /**
     * Test that verifies that the BusStopDisplay constructor creates a BusStopDisplay object correctly.
     */
    @Test
    void CreateBusStopDisplay_ValidTest() {
        assertEquals(busStopDisplay.getCallingRoutes().size(), 8);
        assertEquals("BS05", busStopDisplay.getId());
        assertEquals("Sweetspot", busStopDisplay.getName());
    }

    /**
     * Test that verifies that an IOException is thrown when trying to create a BusStopDisplay object
     * for a non-existent stop info file.
     */
    @Test
    void CreateBusStopDisplay_InvalidTest_StopInfoFile() {
        assertThrows(IOException.class, () -> {
            new BusStopDisplay(new File("nonExistentFile.csv"), routesFile, stopInfoFile);
        });
    }

    /**
     * Test that verifies that an IOException is thrown when trying to create a BusStopDisplay object
     * for a non-existent route info file.
     */
    @Test
    void CreateBusStopDisplay_InvalidTest_RoutesFile() {
        assertThrows(IOException.class, () -> {
            new BusStopDisplay(stopInfoFile, new File("nonExistentFile.csv"), timetableFile);
        });
    }

    /**
     * Test that verifies that an IOException is thrown when trying to create a BusStopDisplay object
     * for a non-existent timetable info file.
     */
    @Test
    void CreateBusStopDisplay_InvalidTest_TimetableInfoFile() {
        assertThrows(IOException.class, () -> {
            new BusStopDisplay(stopInfoFile, routesFile, new File("nonExistentFile.csv"));
        });
    }

    /**
     * Test that verifies that the getTimeOfNextBus method returns the correct time of the next bus
     * for a route that calls at the bus stop.
     */
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

    /**
     * Test that verifies that a RouteDoesNotCallHereException exception is thrown when trying to
     * get the next time of a bus for a route that doesn't call at the bus stop.
     */
    @Test
    void GetTimeOfNextBus_InvalidTest_NotExistentRoute() {
        assertThrows(RouteDoesNotCallHereException.class, () -> {
            busStopDisplay.getTimeOfNextBus("7", LocalTime.of(13, 30));
        });
    }

    /**
     * Test that verifies that the getCallingRoutes method returns the correct calling routes for the bus stop.
     */
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

    /**
     * Test that verifies that the getDepartureTimes method returns the correct departure times
     * for a route that calls at the bus stop.
     */
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

    /**
     * Test that verifies that a RouteDoesNotCallHereException exception is thrown when trying to
     * get the departure times of a route that doesn't call at the bus stop.
     */
    @Test
    void GetDepartureTimes_InvalidTest_NotExistentRoute() {
        assertThrows(RouteDoesNotCallHereException.class, () -> {
            busStopDisplay.getDepartureTimes("99");
        });
    }

    /**
     * Test that verifies that the display method works correctly, throwing no exceptions.
     */
    @Test
    void Display_ValidTest() {
        busStopDisplay.display(LocalTime.of(12,0)); // todo it doesnt esem to work if you input a time where there would be less than 10 buses coming after then that day.
    }

    /**
     * Test that verifies that updating a bus as departed via the updateBusAsDeparted method works correctly.
     */
    @Test
    void UpdateBusAsDeparted_ValidTest() {
        busStopDisplay.updateBusAsDeparted("3", 1);
        assertFalse(busStopDisplay.getExpectedBuses().containsKey("R3J1"));
    }

    /**
     * Test that verifies that updating a bus as delayed via the updateBusAsDelayed method works correctly.
     */
    @Test
    void UpdateBusAsDelayed_ValidTest() {
        int delay = 5;
        busStopDisplay.updateBusAsDelayed("3", 1, delay);
        assertEquals(BusStatus.delayed, busStopDisplay.getExpectedBuses().get("R3J1").getStatus());
        assertEquals(delay, busStopDisplay.getExpectedBuses().get("R3J1").getDelay());
    }

    /**
     * Test that verifies that cancelling a bus via the updateBusAsCancelled method works correctly.
     */
    @Test
    void UpdateBusAsCancelled_ValidTest() {
        busStopDisplay.updateBusAsCancelled("3", 1);
        assertEquals(BusStatus.cancelled, busStopDisplay.getExpectedBuses().get("R3J1").getStatus());
    }

    /**
     * Test that verifies that a bus can't be updated as delayed if it has already been cancelled.
     */
    @Test
    void UpdateBusAsDelayed_AlreadyCancelledTest() {
        busStopDisplay.updateBusAsCancelled("3", 1);
        busStopDisplay.updateBusAsDelayed("3", 1, 5);
        assertEquals(BusStatus.cancelled, busStopDisplay.getExpectedBuses().get("R3J1").getStatus());
        assertNotEquals(5, busStopDisplay.getExpectedBuses().get("R3J1").getDelay());
    }

    /**
     * Test that verifies that a bus can't be updated as departed if it has already been cancelled.
     */
    @Test
    void UpdateBusAsDeparted_AlreadyCancelledTest() {
        busStopDisplay.updateBusAsCancelled("3", 1);
        busStopDisplay.updateBusAsDeparted("3", 1);
        assertEquals(BusStatus.cancelled, busStopDisplay.getExpectedBuses().get("R3J1").getStatus());
        assertNotEquals(5, busStopDisplay.getExpectedBuses().get("R3J1").getDelay());
    }

    /**
     * Test that verifies no exception is thrown when trying to update a bus that doesn't exist as cancelled.
     */
    @Test
    void UpdateBusAsCancelled_NotExistentBusTest() {
        assertDoesNotThrow(() -> busStopDisplay.updateBusAsCancelled("99", 1));
    }

    /**
     * Test that verifies no exception is thrown when trying to update a bus that doesn't exist as departed.
     */
    @Test
    void UpdateBusAsDeparted_NotExistentBusTest() {
        assertDoesNotThrow(() -> busStopDisplay.updateBusAsDeparted("99", 1));
    }

    /**
     * Test that verifies no exception is thrown when trying to update a bus that doesn't exist as delayed.
     */
    @Test
    void UpdateBusAsDelayed_NotExistentBusTest() {
        assertDoesNotThrow(() -> busStopDisplay.updateBusAsDelayed("99", 1, 5));
    }

    /**
     * Test that verifies the correct status display value is returned for each bus status.
     */
    @Test
    void GetStatusDisplayValue_ValidTest() {
        assertEquals("on time", busStopDisplay.getStatusDisplayValue(BusStatus.onTime, 0));
        assertEquals("5 minutes delay", busStopDisplay.getStatusDisplayValue(BusStatus.delayed, 5));
        assertEquals("cancelled", busStopDisplay.getStatusDisplayValue(BusStatus.cancelled, 0));
    }
}
