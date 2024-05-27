package MetropolisBusStop.impl;

import MetropolisBusStop.impl.exceptions.RouteDoesNotCallHereException;

import java.io.*;
import java.time.LocalTime;
import java.util.*;

/**
 * The BusStopDisplay class implements the BusInfoObserver interface and represents a display
 * at a bus stop. It maintains a list of expected buses and provides methods to update the
 * status of these buses based on notifications received. It also provides methods to get
 * the departure times for a specific route. The class also provides a method to display the
 * next buses due at the bus stop after a given time. The display includes the route number,
 * destination, due time and status of each bus. If a bus is delayed or cancelled, this is
 * reflected in the display.
 */
public class BusStopDisplay implements BusInfoObserver {

    String id;
    String name;
    private Map<String, Route> routes;
    private Map<String, ExpectedBus> expectedBuses;

    /**
     * Constructs a BusStopDisplay object and initialises it with data from the given files.
     *
     * @param stopInfo the file containing stop information
     * @param routesFile the file containing route information
     * @param ttInfo the file containing timetable information
     * @throws IOException if an I/O error occurs while reading the files
     */
    public BusStopDisplay(File stopInfo, File routesFile, File ttInfo) throws IOException {
        this.routes = new HashMap<>();
        this.expectedBuses = new HashMap<>();

        loadRoutes(ttInfo, routesFile);
        addScheduledToExpected();
        loadStopInfo(stopInfo);
    }

    /**
     * Loads routes from the given files and populates the routes map.
     *
     * @param ttInfo the file containing timetable information
     * @param routesFile the file containing route information
     * @throws IOException if an I/O error occurs while reading the files
     */
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

                Route r = new Route(routeNo, destination, origin, schedule);
                this.routes.put(routeNo, r);
            }
        }
    }

    /**
     * Loads the schedule for a specific route from the timetable file.
     *
     * @param ttInfoReader the BufferedReader for reading the timetable file
     * @param routeNo the route number for which to load the schedule
     * @param schedule the list to populate with schedule times
     * @throws IOException if an I/O error occurs while reading the file
     */
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

    /**
     * Loads bus stop information from the given file.
     *
     * @param stopInfo the file containing stop information
     * @throws IOException if an I/O error occurs while reading the file
     */
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

    /**
     * Adds scheduled buses to the expected buses map.
     */
    private void addScheduledToExpected() {
        this.expectedBuses = new HashMap<String, ExpectedBus>();

        List<ExpectedBus> ebs = new ArrayList<>();

        this.routes.forEach((key, route) -> {
            for (int i = 0; i < route.schedule.size(); i++) {
                LocalTime t = route.schedule.get(i);
                ExpectedBus eb = new ExpectedBus(route.routeNo,
                                        i + 1,
                                                  route.destination,
                                                  BusStatus.onTime,
                                                  t,
                                            0);
                ebs.add(eb);
            }
        });

        ebs.sort(Comparator.comparing(ExpectedBus::getTime));

        for (ExpectedBus eb : ebs) {
            this.expectedBuses.put("R" + eb.getRouteNo() + "J" + eb.getJourneyNo(), eb);
        }
    }

    /**
     * Returns an unmodifiable map of the routes that call at this bus stop.
     *
     * @return an unmodifiable map of routes
     */
    public Map<String, Route> getCallingRoutes() {
        return Collections.unmodifiableMap(this.routes);
    }

    /**
     * Returns a list of departure times for a given route number.
     *
     * @param routeNo the route number
     * @return a list of departure times
     * @throws RouteDoesNotCallHereException if the route does not call at this stop
     */
    public List<LocalTime> getDepartureTimes(String routeNo) throws RouteDoesNotCallHereException {

        if (!this.routes.containsKey(routeNo))
            throw new RouteDoesNotCallHereException(routeNo);

        Route route = this.routes.get(routeNo);

        return Collections.unmodifiableList(route.schedule);
    }

    /**
     * Returns the time of the next bus for a given route number after a given time.
     *
     * @param routeNo the route number
     * @param t the time after which to find the next bus
     * @return the time of the next bus
     * @throws RouteDoesNotCallHereException if the route does not call at this stop
     */
    public LocalTime getTimeOfNextBus(String routeNo, LocalTime t) throws RouteDoesNotCallHereException {

        if (!this.routes.containsKey(routeNo))
            throw new RouteDoesNotCallHereException(routeNo);

        Route r = this.routes.get(routeNo);

        for (LocalTime time : r.schedule) {
            if (time.isAfter(t)) {
                return time;
            }
        }

        return null;
    }

    /**
     * Displays the next ten buses due at the bus stop after the given time.
     *
     * @param t the time after which to display the next buses
     */
    public void display (LocalTime t) {

        List<ExpectedBus> busesToDisplay = getBusesToDisplay(t);

        String[][] di = new String[busesToDisplay.size() + 1][5];
        di[0] = new String[]{"Number","Route", "Destination", "Due at", "Status"};

        for (int i = 0; i < busesToDisplay.size(); i++) {
            ExpectedBus expectedBus = busesToDisplay.get(i);
            di[i + 1][0] = String.valueOf(i + 1);
            di[i + 1][1] = expectedBus.routeNo;
            di[i + 1][2] = expectedBus.destination;
            di[i + 1][3] = expectedBus.time.toString();
            di[i + 1][4] = getStatusDisplayValue(expectedBus.status, expectedBus.delay);
        }

        for (String[] rowData : di) {
            System.out.printf("%-10s%-10s%-21s%-10s%-10s%n",
                    rowData[0], rowData[1], rowData[2], rowData[3], rowData[4]);
        }
    }

    /**
     * Returns a list of the next ten buses due at the bus stop after the given time.
     *
     * @param t the time after which to find the next buses
     * @return a list of the next ten buses
     */
    private List<ExpectedBus> getBusesToDisplay(LocalTime t) {
        if (this.expectedBuses.size() < 10) {
            addScheduledToExpected();
        }

        Iterator<Map.Entry<String, ExpectedBus>> iterator = this.expectedBuses.entrySet().iterator();

        while (iterator.hasNext()) {
            ExpectedBus eb = iterator.next().getValue();

            if ((eb.status == BusStatus.cancelled && eb.time.isBefore(t))
                    || (eb.time.plusMinutes(eb.delay + 3).isBefore(t))) {
                iterator.remove(); // todo check this works?
            }
        }

        List<ExpectedBus> busesToDisplay = new ArrayList<>(this.expectedBuses.values());
        busesToDisplay.sort(Comparator.comparing(ExpectedBus::getTime));
        return busesToDisplay.subList(0, Math.min(10, busesToDisplay.size()));
    }

    /**
     * Returns a display value for the status of a bus.
     *
     * @param status the status of the bus
     * @param delay the delay of the bus
     * @return a display value for the status
     */
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

    /**
     * Updates the status of a given bus to departed.
     *
     * @param routeNo the route number of the bus to update
     * @param journeyNo the journey number of the bus to update
     */
    @Override
    public void updateBusAsDeparted(String routeNo, int journeyNo) {

        String busKey = "R" + routeNo + "J" + journeyNo;

        if (expectedBuses.containsKey(busKey)){
            ExpectedBus bus = expectedBuses.get(busKey);
            if (bus.status != BusStatus.cancelled) {
                this.expectedBuses.remove(busKey);
            }
        }
    }

    /**
     * Updates the status of a given bus to delayed.
     *
     * @param routeNo the route number of the bus to update
     * @param journeyNo the journey number of the bus to update
     * @param delay the delay duration in minutes
     */
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

    /**
     * Updates the status of a given bus to cancelled.
     *
     * @param routeNo the route number of the bus to update
     * @param journeyNo the journey number of the bus to update
     */
    @Override
    public void updateBusAsCancelled(String routeNo, int journeyNo) {

        String busKey = "R" + routeNo + "J" + journeyNo;

        if (expectedBuses.containsKey(busKey)){
            ExpectedBus bus = expectedBuses.get(busKey);
            bus.updateStatus(BusStatus.cancelled);
        }
    }

    /**
     * Returns the ID of the bus stop.
     *
     * @return the bus stop ID
     */
    public String getId() { return this.id; }

    /**
     * Returns the name of the bus stop.
     *
     * @return the bus stop name
     */
    public String getName() { return this.name; }

    /**
     * Returns an unmodifiable map of the expected buses at this bus stop.
     *
     * @return an unmodifiable map of expected buses
     */
    public Map<String, ExpectedBus> getExpectedBuses() {
        return Collections.unmodifiableMap(this.expectedBuses);
    }

}