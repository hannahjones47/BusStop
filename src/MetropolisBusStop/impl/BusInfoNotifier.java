package MetropolisBusStop.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * The BusInfoNotifier class is responsible for managing a list of observers
 * (instances of BusInfoObserver) and notifying them about bus status updates
 * such as delays, cancellations and departures.
 * This class follows the Observer design pattern.
 */
public class BusInfoNotifier {
    private List<BusInfoObserver> observers = new ArrayList<>();

    /**
     * Adds an observer to the list of observers.
     *
     * @param observer the observer to be added
     */
    public void addObserver(BusInfoObserver observer) {
        observers.add(observer);
    }

    /**
     * Removes an observer from the list of observers.
     *
     * @param observer the observer to be removed
     */
    public void removeObserver(BusInfoObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifies all observers that a bus is delayed.
     *
     * @param routeNo the route number of the delayed bus
     * @param journeyNo the journey number of the delayed bus
     * @param delay the delay duration in minutes
     */
    public void notifyObserversOfDelay(String routeNo, int journeyNo, int delay) {
        for (BusInfoObserver observer : observers) {
            observer.updateBusAsDelayed(routeNo, journeyNo, delay);
        }
    }

    /**
     * Notifies all observers that a bus is cancelled.
     *
     * @param routeNo the route number of the cancelled bus
     * @param journeyNo the journey number of the cancelled bus
     */
    public void notifyObserversOfCancellation(String routeNo, int journeyNo) {
        for (BusInfoObserver observer : observers) {
            observer.updateBusAsCancelled(routeNo, journeyNo);
        }
    }

    /**
     * Notifies all observers that a bus has departed.
     *
     * @param routeNo the route number of the departing bus
     * @param journeyNo the journey number of the departing bus
     */
    public void notifyObserversOfDeparture(String routeNo, int journeyNo) {
        for (BusInfoObserver observer : observers) {
            observer.updateBusAsDeparted(routeNo, journeyNo);
        }
    }
}