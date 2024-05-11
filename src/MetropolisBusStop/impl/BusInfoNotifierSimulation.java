package MetropolisBusStop.impl;

import java.util.*;

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

    public void simulate() {

        for (Map.Entry<String, Integer> entry : busRoutes.entrySet()) {
            for (int i = 0; i < 10; i++){
                simulateUpdate(entry.getKey(), entry.getValue());
            }
        }
    }

    private void simulateUpdate(String routeNo, int numberTotalJourneys) {

        Random random = new Random();
        int journeyNo = random.nextInt(numberTotalJourneys);

        switch (random.nextInt(3)) {
            case 0:
                updateBusStatusAsDeparted(routeNo, journeyNo);
                break;
            case 1:
                int delay = random.nextInt(10) + 1;
                updateBusStatusAsDelayed(routeNo, journeyNo, delay);
                break;
            case 2:
                updateBusStatusAsCancelled(routeNo, journeyNo);
                break;
        }
    }
}