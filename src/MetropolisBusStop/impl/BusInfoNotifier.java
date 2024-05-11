package MetropolisBusStop.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

class BusInfoNotifier extends Observable {
    private List<BusInfoObserver> observers = new ArrayList<>();

    public void addObserver(BusInfoObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(BusInfoObserver observer) {
        observers.remove(observer);
    }

    public void notifyObserversOfDelay(String routeNo, int journeyNo, int delay) {
        for (BusInfoObserver observer : observers) {
            observer.updateBusAsDelayed(routeNo, journeyNo, delay);
        }
    }

    public void notifyObserversOfCancellation(String routeNo, int journeyNo) {
        for (BusInfoObserver observer : observers) {
            observer.updateBusAsCancelled(routeNo, journeyNo);
        }
    }

    public void notifyObserversOfDeparture(String routeNo, int journeyNo) {
        for (BusInfoObserver observer : observers) {
            observer.updateBusAsDeparted(routeNo, journeyNo);
        }
    }

    public void updateBusStatusAsDelayed(String routeNo, int journeyNo, int delay) {
        notifyObserversOfDelay(routeNo, journeyNo, delay);
    }

    public void updateBusStatusAsCancelled(String routeNo, int journeyNo) {
        notifyObserversOfCancellation(routeNo, journeyNo);
    }

    public void updateBusStatusAsDeparted(String routeNo, int journeyNo) {
        notifyObserversOfDeparture(routeNo, journeyNo);
    }
}