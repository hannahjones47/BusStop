package MetropolisBusStop.impl.exceptions;

public class RouteDoesNotCallHereException extends Exception {

    public RouteDoesNotCallHereException(String routeNo) {
        super ("No route with route number " + routeNo + " calls at this bus stop");
    }
}
