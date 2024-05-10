package MetropolisBusStop.impl.exceptions;

public class BusDoesNotExistException extends Exception {

    public BusDoesNotExistException(String routeNo, int journeyNo) {
        super ("There is no expected bus on route number " + routeNo + " and journey number " + journeyNo);
    }
}
