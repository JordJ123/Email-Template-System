package email;

/**
 * Exception thrown when there is a failure connecting to the host.
 * @author Jordan Jones
 */
public class HostConnectionFailureException extends Exception {

    //CONSTANTS
    private static final String MESSAGE = "Failed to connect to host. Please "
        + "check internet connection or try again later";

    /**
     * Creates the exception to be thrown.
     */
    public HostConnectionFailureException() {
        super(MESSAGE);
    }

}
