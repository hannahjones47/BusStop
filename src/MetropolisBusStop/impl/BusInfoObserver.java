package MetropolisBusStop.impl;

public interface BusInfoObserver {
    void updateBusAsDelayed(String routeNo, int journeyNo, int delay);

    void updateBusAsCancelled(String routeNo, int journeyNo);

    void updateBusAsDeparted(String routeNo, int journeyNo);
}