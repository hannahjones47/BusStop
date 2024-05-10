package MetropolisBusStop.impl;

import MetropolisBusStop.impl.exceptions.BusDoesNotExistException;

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

    public void notifyObservers(String routeNo, int journeyNo, BusStatus newBusStatus, int delay) {
        for (BusInfoObserver observer : observers) {
            try{
                observer.updateBusInfo(routeNo, journeyNo, newBusStatus, delay);
            }
            catch (BusDoesNotExistException e){
                System.out.printf("BusDoesNotExistException: ", e);
            }
        }
    }

    public void updateBusStatus(String routeNo, int journeyNo, BusStatus newBusStatus, int delay) {
        notifyObservers(routeNo, journeyNo, newBusStatus, delay);
    }
}