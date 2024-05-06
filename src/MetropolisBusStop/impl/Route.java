package MetropolisBusStop.impl;

import java.time.LocalTime;
import java.util.List;

public class Route {

    String routeNo;
    String destination;
    String origin;
    List<LocalTime> schedule;

    public Route (String routeNo, String destination, String origin, List<LocalTime> schedule) {
        this.routeNo = routeNo;
        this.destination = destination;
        this.origin = origin;
        this.schedule = schedule;
    }
}
