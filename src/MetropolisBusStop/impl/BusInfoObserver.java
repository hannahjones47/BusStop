package MetropolisBusStop.impl;

/**
 * Observer interface for the bus information system.
 */
public interface BusInfoObserver {

    /**
     * Updates the status of a bus to delayed.
     * @param routeNo the route number of the bus
     * @param journeyNo the journey number of the bus
     * @param delay the new delay of the bus
     */
    void updateBusAsDelayed(String routeNo, int journeyNo, int delay);

    /**
     * Updates the status of a bus to cancelled.
     * @param routeNo the route number of the bus
     * @param journeyNo the journey number of the bus
     */
    void updateBusAsCancelled(String routeNo, int journeyNo);

    /**
     * Updates the status of a bus to departed.
     * @param routeNo the route number of the bus
     * @param journeyNo the journey number of the bus
     */
    void updateBusAsDeparted(String routeNo, int journeyNo);
}