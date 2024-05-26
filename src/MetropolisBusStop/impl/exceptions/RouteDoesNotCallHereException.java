package MetropolisBusStop.impl.exceptions;

/**
 * Exception thrown when a route does not call at a bus stop
 */
public class RouteDoesNotCallHereException extends Exception {

    public RouteDoesNotCallHereException(String routeNo) {
        super ("No route with route number " + routeNo + " calls at this bus stop");
    }
}
