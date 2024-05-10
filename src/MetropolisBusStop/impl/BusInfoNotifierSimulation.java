package MetropolisBusStop.impl;

import MetropolisBusStop.impl.exceptions.BusDoesNotExistException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
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
//        updateBusStatus("33", 1, BusStatus.delayed, 3);
//        updateBusStatus("3", 3, BusStatus.cancelled, 0);
//        updateBusStatus("6", 1, BusStatus.cancelled, 0);
//        updateBusStatus("17", 20, BusStatus.delayed, 5);
//        updateBusStatus("3", 10, BusStatus.delayed, 5);
        for (Map.Entry<String, Integer> entry : busRoutes.entrySet()) {
            for (int i = 0; i < 10; i++){
                simulateUpdate(entry.getKey(), entry.getValue());
            }
        }

    }

    private void simulateUpdate(String routeNo, int numberTotalJourneys) {

        Random random = new Random();
        int delay = 0;

        BusStatus newStatus;
        switch (random.nextInt(3)) {
            case 0:
                newStatus = BusStatus.onTime;
                break;
            case 1:
                newStatus = BusStatus.delayed;
                delay = random.nextInt(10) + 1;
                break;
            case 2:
                newStatus = BusStatus.cancelled;
                break;
            default:
                newStatus = BusStatus.onTime;
                break;
        }

        int journeyNo = random.nextInt(numberTotalJourneys);

        updateBusStatus(routeNo, journeyNo, newStatus, delay);
    }
}