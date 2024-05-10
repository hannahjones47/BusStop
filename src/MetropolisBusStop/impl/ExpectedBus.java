package MetropolisBusStop.impl;

import java.time.LocalTime;
import java.util.List;

public class ExpectedBus {

    String routeNo;
    int journeyNo;
    String destination;
    BusStatus status;
    LocalTime time;
    int delay;

    public ExpectedBus(String routeNo, int journeyNo, String destination, BusStatus status, LocalTime time, int delay) {

        if (journeyNo < 0)
            throw new IllegalArgumentException ("Negative journey number!");

        if (delay < 0)
            throw new IllegalArgumentException ("Negative delay!");

        this.routeNo = routeNo;
        this.journeyNo = journeyNo;
        this.destination = destination;
        this.status = status;
        this.time = time;
        this.delay = delay;
    }

    public LocalTime getTime() { return time; }

    public String getRouteNo() { return routeNo; }

    public int getJourneyNo() { return journeyNo; }

    public void updateStatus(BusStatus newStatus) {
        this.status = newStatus;
    }

    public void updateDelay(int newDelay) {
        if (newDelay < 0)
            throw new IllegalArgumentException ("Negative delay!");

        this.delay = newDelay;
    }
}
