package MetropolisBusStop.impl;

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

    public void addScheduledToExpected()
    {
        List<ExpectedBus> expectedBusList = new ArrayList<>();

        this.routes.forEach((key, route) -> {
            for (int i = 1; i < route.schedule.size(); i++) {
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

    public List<Route> getCallingRoutes() {
        // todo returns an unmodifiable collection of Routes served by the bus stop
        return null;
    }

    public List<LocalTime> getDepartureTimes() {
        // todo gets the bus stop's timetable for the given route as an unmodifiable collection of times
        return null;
    }

    public LocalTime getTimeOfNextBus(Route route, LocalTime t) {
        // todo gets the time of the next expected bus of a given route after a given time t as per bus stop timetable
        return null;
    }

    public void display (LocalTime t) {
        // todo displays the 10 next expected buses starting from a given time t (current time under normal circumstances)
    }


}
