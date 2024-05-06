package MetropolisBusStop.impl;

import MetropolisBusStop.impl.exceptions.RouteDoesNotCallHereException;

import java.io.*;
import java.time.LocalTime;
import java.util.*;

public class BusStopDisplay {

    String id;
    String name;
    public Map<String, Route> routes; // todo am i allowed to make that public??
    public Map<String, ExpectedBus> expectedBuses;

    public BusStopDisplay(File stopInfo, File routesFile, File ttInfo) { // todo do i have to name this create?

        this.routes = new HashMap<String, Route>();

        // todo 1. for each row in routesInfo, a route object is created
        try (BufferedReader br = new BufferedReader(new FileReader(routesFile))) {
            String line;
            int lineCount = 0;
            while ((line = br.readLine()) != null) {
                lineCount ++;
                if (lineCount == 1) continue;
                String[] routeInfo = line.split(",");
                String routeNo = routeInfo[0];
                String destination = routeInfo[1];
                String origin = routeInfo[2];

                // get schedule for that route:
                List<LocalTime> schedule = new ArrayList<>();
                BufferedReader br2 = new BufferedReader(new FileReader(ttInfo));
                String line2;
                while ((line2 = br2.readLine()) != null) {
                    String[] timetableInfo = line2.split(",");
                    String currentRouteNo = timetableInfo[0];
                    if (currentRouteNo.equals(routeNo)){
                        for (int i = 1; i < timetableInfo.length; i++) {
                            LocalTime time = LocalTime.parse(timetableInfo[i]);
                            schedule.add(time);
                        }
                        break;
                    }
                }//todo error handle if schedule not found?

                Route route = new Route(routeNo, destination, origin, schedule);
                this.routes.put(routeNo, route);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // todo 2. a list of expected buses is created and then filled through operation addScheduledToExpected
        this.expectedBuses = new HashMap<String, ExpectedBus>();
        addScheduledToExpected();

        // todo 3. a get bus stop info from file
        try (BufferedReader br = new BufferedReader(new FileReader(stopInfo))) {
            String line;
            int lineCount = 0;
            while ((line = br.readLine()) != null) {
                lineCount++;
                if (lineCount == 2) {
                    String[] busStopInfo = line.split(",");
                    if (busStopInfo.length >= 2) {
                        this.id = busStopInfo[0];
                        this.name = busStopInfo[1];
                    } else {
                        System.err.println("Invalid format on line 2: " + line);
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addScheduledToExpected() {
        this.expectedBuses = new HashMap<String, ExpectedBus>();

        List<ExpectedBus> expectedBusList = new ArrayList<>();

        this.routes.forEach((key, route) -> {
            for (int i = 0; i < route.schedule.size(); i++) {
                LocalTime time = route.schedule.get(i);
                ExpectedBus expectedBus = new ExpectedBus(route.routeNo,
                                                            i + 1,
                                                            route.destination,
                                                            BusStatus.onTime,
                                                            time,
                                                            0);
                expectedBusList.add(expectedBus);
            }
        });

        expectedBusList.sort(Comparator.comparing(ExpectedBus::getTime));

        // todo check that the list is actually ordered
        for (ExpectedBus expectedBus : expectedBusList) {
            this.expectedBuses.put("R" + expectedBus.getRouteNo() + "J" + expectedBus.getJourneyNo(), expectedBus); // todo this key might need improving
        }

//todo dont know if this can go here bc it isnt listed on class diagrm , maybe need to put in constructor.
    }

    public Map<String, Route> getCallingRoutes() {
        // todo is this an unmodifiable collection ?
        return this.routes;
    }

    public List<LocalTime> getDepartureTimes(String routeNo) throws RouteDoesNotCallHereException {

        if (!this.routes.containsKey(routeNo))
            throw new RouteDoesNotCallHereException(routeNo);

        Route route = this.routes.get(routeNo);

        // todo is this an unmodifiable collection ?
        return route.schedule;
    }

    public LocalTime getTimeOfNextBus(String routeNo, LocalTime t) throws RouteDoesNotCallHereException {

        if (!this.routes.containsKey(routeNo))
            throw new RouteDoesNotCallHereException(routeNo);

        Route route = this.routes.get(routeNo); //todo assuming the schedule is always ordered?

        for (LocalTime time : route.schedule) {
            if (time.isAfter(t)) {
                return time;
            }
        }

        return null;
    }

    public void display (LocalTime t) {

        Iterator<Map.Entry<String, ExpectedBus>> iterator = this.expectedBuses.entrySet().iterator();

        while (iterator.hasNext()) {
            ExpectedBus expectedBus = iterator.next().getValue();

            if ((expectedBus.status == BusStatus.cancelled && expectedBus.time.isBefore(t))
                 || ( expectedBus.time.plusMinutes(expectedBus.delay + 3).isBefore(t) )) {
                iterator.remove();
            }
        }

        if (this.expectedBuses.size() < 10) {
            addScheduledToExpected();
        }

        List<ExpectedBus> busesToDisplay = new ArrayList<>(this.expectedBuses.values());
        busesToDisplay.sort(Comparator.comparing(ExpectedBus::getTime));
        List<ExpectedBus> firstTenBuses = busesToDisplay.subList(0, Math.min(10, busesToDisplay.size()));

        String[][] displayTableData = new String[firstTenBuses.size() + 1][5];
        displayTableData[0] = new String[]{"Number","Route", "Destination", "Due at", "Status"};

        for (int i = 0; i < firstTenBuses.size(); i++) {
            ExpectedBus expectedBus = firstTenBuses.get(i);
            displayTableData[i + 1][0] = String.valueOf(i + 1);
            displayTableData[i + 1][1] = expectedBus.routeNo;
            displayTableData[i + 1][2] = expectedBus.destination;
            displayTableData[i + 1][3] = expectedBus.time.toString();
            displayTableData[i + 1][4] = getStatusDisplayValue(expectedBus.status, expectedBus.delay);
        }

        for (String[] rowData : displayTableData) {
            for (String data : rowData) {
                System.out.printf("%-20s", data);
            }
            System.out.println();
        }
    }

    private String getStatusDisplayValue(BusStatus status, int delay){
        if (status == BusStatus.onTime){
            return "on time";
        }
        else if (status == BusStatus.delayed){
            return delay + " minutes delay";
        }
        else if (status == BusStatus.cancelled){
            return "cancelled";
        }
        return "error";
    }


}
