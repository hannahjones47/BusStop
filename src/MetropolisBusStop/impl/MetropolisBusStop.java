package MetropolisBusStop.impl;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetropolisBusStop {

    private static int last_busStopId = 0;
    String id;
    String name;
    Map<String, Route> routes;
    Map<String, ExpectedBus> expectedBuses;

    public MetropolisBusStop(String name) {
        last_busStopId ++;
        this.id = "B" + last_busStopId;
        this.name = name;
        routes = new HashMap<String, Route>();
        expectedBuses = new HashMap<String, ExpectedBus>();

        // todo build objects holding display information from the config files.
        // todo also build a list of expected buses (method addScheduledToExpected).
    }

    public List<Route> getCallingRoutes() {
        // todo returns an unmodifiable collection of Routes served by the bus stop
        return null;
    }

    public List<LocalTime> getDepartureTimes() {
        // todo gets the bus stop's timetable for the given route as an unmodifiable collection of times
        return null;
    }

    public LocalTime getTimeOfNextBus(Route route, LocalTime t) {
        // todo gets the time of the next expected bus of a given route after a given time t as per bus stop timetable
        return null;
    }

    public void display (LocalTime t) {
        // todo displays the 10 next expected buses starting from a given time t (current time under normal circumstances)
    }


}
