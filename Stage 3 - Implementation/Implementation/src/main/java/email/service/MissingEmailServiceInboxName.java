package email.service;

import org.jetbrains.annotations.NotNull;

/**
 * Thrown if an email service is missing an inbox name.
 * @author Jordan Jones
 */
public class MissingEmailServiceInboxName extends Exception {

    //CONSTANTS
    private static final String MESSAGE = "Email Service %s does not have a "
        + "name for the %s inbox. Possible options could include:\n%s";

    /**
     * Creates a missing email service inbox name error.
     * @param emailService Email service that is missing the inbox name
     * @param inbox Inbox the name is missing for
     * @param folderNames Possible names the inbox name could be
     */
    public MissingEmailServiceInboxName(@NotNull EmailService emailService,
        String inbox, String folderNames) {
        super(String.format(MESSAGE, emailService.getName(),
            inbox, folderNames));
    }

}
