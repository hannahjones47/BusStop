package MetropolisBusStop.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {

        Path projectDir = Paths.get("").toAbsolutePath();
        File stopInfoFile = new File(projectDir.resolve("src/MetropolisBusStop/configurationData/stop_info.csv").toString());
        File routesFile = new File(projectDir.resolve("src/MetropolisBusStop/configurationData/routes.csv").toString());
        File timetableFile = new File(projectDir.resolve("src/MetropolisBusStop/configurationData/timetable.csv").toString());

        BusInfoNotifierSimulation busInfoNotifierSimulation = new BusInfoNotifierSimulation();

        BusStopDisplay busStopDisplay = null;
        try {
            busStopDisplay = new BusStopDisplay(stopInfoFile, routesFile, timetableFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        busInfoNotifierSimulation.addObserver(busStopDisplay);

        while (true) {

            busStopDisplay.display(LocalTime.now());
            System.out.println();

            busInfoNotifierSimulation.simulate();
            try {
                TimeUnit.SECONDS.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}