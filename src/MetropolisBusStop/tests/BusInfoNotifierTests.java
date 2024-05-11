package MetropolisBusStop.tests;

import MetropolisBusStop.impl.BusInfoNotifier;
import MetropolisBusStop.impl.BusInfoObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusInfoNotifierTests {
    BusInfoNotifier busInfoNotifier;
    TestBusInfoObserver observer;

    @BeforeEach
    void setUp() {
        busInfoNotifier = new BusInfoNotifier();
        observer = new TestBusInfoObserver();
    }

    @Test
    void addObserver_ValidTest() {
        busInfoNotifier.addObserver(observer);
        busInfoNotifier.notifyObserversOfDelay("3", 1, 5);
        assertTrue(observer.isUpdateBusAsDelayedCalled());
    }

    @Test
    void removeObserver_ValidTest() {
        busInfoNotifier.addObserver(observer);
        busInfoNotifier.removeObserver(observer);
        busInfoNotifier.notifyObserversOfDelay("3", 1, 5);
        assertFalse(observer.isUpdateBusAsDelayedCalled());
    }

    @Test
    void notifyObserversOfDelay_ValidTest() {
        busInfoNotifier.addObserver(observer);
        busInfoNotifier.notifyObserversOfDelay("3", 1, 5);
        assertTrue(observer.isUpdateBusAsDelayedCalled());
    }

    @Test
    void notifyObserversOfCancellation_ValidTest() {
        busInfoNotifier.addObserver(observer);
        busInfoNotifier.notifyObserversOfCancellation("3", 1);
        assertTrue(observer.isUpdateBusAsCancelledCalled());
    }

    @Test
    void notifyObserversOfDeparture_ValidTest() {
        busInfoNotifier.addObserver(observer);
        busInfoNotifier.notifyObserversOfDeparture("3", 1);
        assertTrue(observer.isUpdateBusAsDepartedCalled());
    }

    class TestBusInfoObserver implements BusInfoObserver {
        private boolean isUpdateBusAsDelayedCalled = false;
        private boolean isUpdateBusAsCancelledCalled = false;
        private boolean isUpdateBusAsDepartedCalled = false;

        @Override
        public void updateBusAsDelayed(String routeNo, int journeyNo, int delay) {
            isUpdateBusAsDelayedCalled = true;
        }

        @Override
        public void updateBusAsCancelled(String routeNo, int journeyNo) {
            isUpdateBusAsCancelledCalled = true;
        }

        @Override
        public void updateBusAsDeparted(String routeNo, int journeyNo) {
            isUpdateBusAsDepartedCalled = true;
        }

        public boolean isUpdateBusAsDelayedCalled() {
            return isUpdateBusAsDelayedCalled;
        }

        public boolean isUpdateBusAsCancelledCalled() {
            return isUpdateBusAsCancelledCalled;
        }

        public boolean isUpdateBusAsDepartedCalled() {
            return isUpdateBusAsDepartedCalled;
        }
    }
}