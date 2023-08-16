package email.address;

import email.email.body.tag.EmailTag;
import org.jetbrains.annotations.NotNull;

/**
 * Error thrown if trying to input an empty tag value.
 * @author Jordan Jones
 */
public class EmptyTagValueException extends Exception {

    //CONSTANTS
    private static final String MESSAGE = "%s does not have a tag value for "
        + "the tag %s";

    //Attributes
    private EmailAddress emailAddress;
    private EmailTag emailTag;

    /**
     * Creates an empty tag value.
     * @param emailAddress Email address error is about
     * @param emailTag Tag that the email address does not have a value for
     */
    public EmptyTagValueException(
        @NotNull EmailAddress emailAddress, @NotNull EmailTag emailTag) {
        super(String.format(MESSAGE, emailAddress.getAddress(),
            emailTag.getName()));
        setEmailAddress(emailAddress);
        setTag(emailTag);
    }

    /**
     * Sets the email address of the error.
     * @param emailAddress Email Address of the error
     */
    private void setEmailAddress(EmailAddress emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Sets the tag of the error.
     * @param emailTag Tag of the error
     */
    private void setTag(EmailTag emailTag) {
        this.emailTag = emailTag;
    }

    /**
     * Gets the email address of the error.
     * @return Email Address of the error
     */
    public EmailAddress getEmailAddress() {
        return emailAddress;
    }

    /**
     * Gets the tag of the error.
     * @return Tag of the error
     */
    public EmailTag getTag() {
        return emailTag;
    }

}
