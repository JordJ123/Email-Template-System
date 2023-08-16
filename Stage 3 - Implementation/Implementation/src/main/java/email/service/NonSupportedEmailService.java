package email.service;

/**
 * Exception for when a non-supported email service is requested.
 * @author Jordan Jones
 */
public class NonSupportedEmailService extends Exception {

    //CONSTANTS
    public static final String MESSAGE = "Requested email service is not "
        + "supported (%s)";

    /**
     * Creates the non-supported email service exception.
     * @param emailService Email service that is requested
     */
    public NonSupportedEmailService(String emailService) {
        super(String.format(MESSAGE, emailService));
    }

}
