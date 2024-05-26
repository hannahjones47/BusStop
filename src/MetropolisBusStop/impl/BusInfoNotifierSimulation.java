package MetropolisBusStop.impl;

import java.util.*;

/**
 * The BusInfoNotifierSimulation class extends BusInfoNotifier and simulates
 * bus updates for a set of predefined bus routes. It uses a static block to
 * initialize bus route data and provides methods to simulate bus status updates.
 */
class BusInfoNotifierSimulation extends BusInfoNotifier {

    private static final Map<String, Integer> busRoutes = new HashMap<>();

    static {
        busRoutes.put("3", 14);
        busRoutes.put("6", 62);
        busRoutes.put("12", 30);
        busRoutes.put("15", 54);
        busRoutes.put("17", 27);
        busRoutes.put("21", 14);
        busRoutes.put("25", 30);
        busRoutes.put("33", 62);
    }

    /**
     * Simulates bus updates for all predefined bus routes.
     */
    public void simulate() {

        for (Map.Entry<String, Integer> entry : busRoutes.entrySet()) {
            for (int i = 0; i < 10; i++){
                simulateUpdate(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Simulates a single update for a given bus route.
     *
     * @param routeNo the route number of the bus
     * @param numberTotalJourneys the total number of journeys for the bus route
     */
    private void simulateUpdate(String routeNo, int numberTotalJourneys) {

        Random random = new Random();
        int journeyNo = random.nextInt(numberTotalJourneys);

        switch (random.nextInt(3)) {
            case 0:
                notifyObserversOfDeparture(routeNo, journeyNo);
                break;
            case 1:
                int delay = random.nextInt(10) + 1;
                notifyObserversOfDelay(routeNo, journeyNo, delay);
                break;
            case 2:
                notifyObserversOfCancellation(routeNo, journeyNo);
                break;
        }
    }
}