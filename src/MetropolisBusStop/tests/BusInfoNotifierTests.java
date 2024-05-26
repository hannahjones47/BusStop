package MetropolisBusStop.tests;

import MetropolisBusStop.impl.BusInfoNotifier;
import MetropolisBusStop.impl.BusInfoObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the BusInfoNotifier class
 */
class BusInfoNotifierTests {
    BusInfoNotifier busInfoNotifier;
    TestBusInfoObserver observer;

    @BeforeEach
    void setUp() {
        busInfoNotifier = new BusInfoNotifier();
        observer = new TestBusInfoObserver();
    }

    /**
     * Test that verifies that an observer is added to the list of observers,
     * and therefore receives notifications.
     */
    @Test
    void addObserver_ValidTest() {
        busInfoNotifier.addObserver(observer);
        busInfoNotifier.notifyObserversOfDelay("3", 1, 5);
        assertTrue(observer.isUpdateBusAsDelayedCalled());
    }

    /**
     * Test that verifies that an observer is removed from the list of observers,
     * and therefore no longer receives notifications.
     */
    @Test
    void removeObserver_ValidTest() {
        busInfoNotifier.addObserver(observer);
        busInfoNotifier.removeObserver(observer);
        busInfoNotifier.notifyObserversOfDelay("3", 1, 5);
        assertFalse(observer.isUpdateBusAsDelayedCalled());
    }

    /**
     * Test that verifies that an observer is notified of a delay via the Observer pattern
     */
    @Test
    void notifyObserversOfDelay_ValidTest() {
        busInfoNotifier.addObserver(observer);
        busInfoNotifier.notifyObserversOfDelay("3", 1, 5);
        assertTrue(observer.isUpdateBusAsDelayedCalled());
    }

    /**
     * Test that verifies that an observer is notified of a cancellation via the Observer pattern
     */
    @Test
    void notifyObserversOfCancellation_ValidTest() {
        busInfoNotifier.addObserver(observer);
        busInfoNotifier.notifyObserversOfCancellation("3", 1);
        assertTrue(observer.isUpdateBusAsCancelledCalled());
    }

    /**
     * Test that verifies that an observer is notified of a departure via the Observer pattern
     */
    @Test
    void notifyObserversOfDeparture_ValidTest() {
        busInfoNotifier.addObserver(observer);
        busInfoNotifier.notifyObserversOfDeparture("3", 1);
        assertTrue(observer.isUpdateBusAsDepartedCalled());
    }

    /**
     * Test class that implements the BusInfoObserver interface for testing purposes
     */
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