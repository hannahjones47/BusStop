package MetropolisBusStop.impl;

import MetropolisBusStop.impl.exceptions.BusDoesNotExistException;

interface BusInfoObserver {
    void updateBusInfo(String routeNo, int journeyNo, BusStatus newBusStatus, int delay) throws BusDoesNotExistException;
}