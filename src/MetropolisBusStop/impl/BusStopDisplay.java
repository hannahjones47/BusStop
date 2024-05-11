package MetropolisBusStop.impl;

import MetropolisBusStop.impl.exceptions.RouteDoesNotCallHereException;

import java.io.*;
import java.time.LocalTime;
import java.util.*;

public class BusStopDisplay implements BusInfoObserver {

    String id;
    String name;
    private Map<String, Route> routes;
    private Map<String, ExpectedBus> expectedBuses;

    public BusStopDisplay(File stopInfo, File routesFile, File ttInfo) throws IOException {
        this.routes = new HashMap<>();
        this.expectedBuses = new HashMap<>();

        loadRoutes(ttInfo, routesFile);
        loadStopInfo(stopInfo);
        addScheduledToExpected();
    }

    private void loadRoutes(File ttInfo, File routesFile) throws IOException {
        try (BufferedReader routesReader = new BufferedReader(new FileReader(routesFile))) {
            String routeInfoLine;
            int routeInfoLineCount = 0;

            while ((routeInfoLine = routesReader.readLine()) != null) {
                routeInfoLineCount++;
                if (routeInfoLineCount == 1) continue;

                String[] routeInfo = routeInfoLine.split(",");
                String routeNo = routeInfo[0];
                String destination = routeInfo[1];
                String origin = routeInfo[2];

                List<LocalTime> schedule = new ArrayList<>();
                try (BufferedReader ttInfoReader = new BufferedReader(new FileReader(ttInfo))) {
                    loadSchedule(ttInfoReader, routeNo, schedule);
                } catch (IOException e) {
                    throw new IOException("Error while attempting to read ttInfo", e);
                }

                Route route = new Route(routeNo, destination, origin, schedule);
                this.routes.put(routeNo, route);
            }
        }
    }

    private void loadSchedule(BufferedReader ttInfoReader, String routeNo, List<LocalTime> schedule) throws IOException {
        String ttInfoLine;
        while ((ttInfoLine = ttInfoReader.readLine()) != null) {
            String[] timetableInfo = ttInfoLine.split(",");
            String currentRouteNo = timetableInfo[0];

            if (currentRouteNo.equals(routeNo)) {
                for (int i = 1; i < timetableInfo.length; i++) {
                    LocalTime time = LocalTime.parse(timetableInfo[i]);
                    schedule.add(time);
                }
                break;
            }
        }
    }

    private void loadStopInfo(File stopInfo) throws IOException {

        try (BufferedReader stopInfoReader = new BufferedReader(new FileReader(stopInfo))) {

            String stopInfoLine;
            int stopInfoLineCount = 0;
            while ((stopInfoLine = stopInfoReader.readLine()) != null) {
                stopInfoLineCount++;
                if (stopInfoLineCount == 2) {
                    String[] busStopInfo = stopInfoLine.split(",");
                    if (busStopInfo.length >= 2) {
                        this.id = busStopInfo[0];
                        this.name = busStopInfo[1];
                    } else {
                        throw new IOException("Error while attempting to read stopInfo file, invalid format on line 2: " + stopInfoLine);
                    }
                    break;
                }
            }
        }
    }

    public String getId() { return this.id; }

    public String getName() { return this.name; }

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
    }

    public Map<String, Route> getCallingRoutes() {
        return Collections.unmodifiableMap(this.routes);
    }

    public Map<String, ExpectedBus> getExpectedBuses() {
        return Collections.unmodifiableMap(this.expectedBuses);
    }

    public List<LocalTime> getDepartureTimes(String routeNo) throws RouteDoesNotCallHereException {

        if (!this.routes.containsKey(routeNo))
            throw new RouteDoesNotCallHereException(routeNo);

        Route route = this.routes.get(routeNo);

        return Collections.unmodifiableList(route.schedule);
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

        // todo maybe i should refactor this to have a separate method to get the buses to display and then that would be testable.

        if (this.expectedBuses.size() < 10) {
            addScheduledToExpected();
        }

        Iterator<Map.Entry<String, ExpectedBus>> iterator = this.expectedBuses.entrySet().iterator();

        while (iterator.hasNext()) {
            ExpectedBus expectedBus = iterator.next().getValue();

            if ((expectedBus.status == BusStatus.cancelled && expectedBus.time.isBefore(t))
                 || ( expectedBus.time.plusMinutes(expectedBus.delay + 3).isBefore(t) )) {
                iterator.remove(); // todo this doesnt seem to work.
            }
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
            System.out.printf("%-10s%-10s%-21s%-10s%-10s%n",
                    rowData[0], rowData[1], rowData[2], rowData[3], rowData[4]);
        }

    }

    public String getStatusDisplayValue(BusStatus status, int delay){
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

    @Override
    public void updateBusAsDeparted(String routeNo, int journeyNo) {

        String busKey = "R" + routeNo + "J" + journeyNo;

        if (expectedBuses.containsKey(busKey)){
            ExpectedBus bus = expectedBuses.get(busKey);
            this.expectedBuses.remove(busKey);
        }
    }

    @Override
    public void updateBusAsDelayed(String routeNo, int journeyNo, int delay) {

        String busKey = "R" + routeNo + "J" + journeyNo;

        if (expectedBuses.containsKey(busKey)){
            ExpectedBus bus = expectedBuses.get(busKey);
            if (bus.status != BusStatus.cancelled){
                bus.updateStatus(BusStatus.delayed);
                bus.updateDelay(delay);
            }
        }
    }

    @Override
    public void updateBusAsCancelled(String routeNo, int journeyNo) {

        String busKey = "R" + routeNo + "J" + journeyNo;

        if (expectedBuses.containsKey(busKey)){
            ExpectedBus bus = expectedBuses.get(busKey);
            bus.updateStatus(BusStatus.cancelled);
        }
    }

}
