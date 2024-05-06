package MetropolisBusStop.impl;

import java.time.LocalTime;
import java.util.List;

public class Route {

    private static int last_routeNo = 0;
    String routeNo;
    String destination;
    String origin;
    List<LocalTime> schedule;

    public Route (String destination, String origin, List<LocalTime> schedule) {
        last_routeNo ++;
        this.routeNo = "R" + last_routeNo;
        this.destination = destination;
        this.origin = origin;
        this.schedule = schedule;
    }
}
