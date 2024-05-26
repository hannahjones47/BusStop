package MetropolisBusStop.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

/**
 * Main class to run the simulation
 */
public class Demo {
    public static void main(String[] args) throws IOException {

        Path projectDir = Paths.get("").toAbsolutePath();
        File stopInfoFile = new File(projectDir.resolve("src/MetropolisBusStop/configurationData/stop_info.csv").toString());
        File routesFile = new File(projectDir.resolve("src/MetropolisBusStop/configurationData/routes.csv").toString());
        File timetableFile = new File(projectDir.resolve("src/MetropolisBusStop/configurationData/timetable.csv").toString());

        BusInfoNotifierSimulation busInfoNotifierSimulation = new BusInfoNotifierSimulation();

        BusStopDisplay busStopDisplay = new BusStopDisplay(stopInfoFile, routesFile, timetableFile);

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