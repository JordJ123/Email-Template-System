package gui.components;

import email.address.EmailAddress;
import email.address.InvalidEmailAddressException;

import javax.mail.Message;

/**
 * Extended text field component that contains multiple email addresses.
 * @author Jordan Jones
 */
public class EmailAddressesTextField extends ExtendedTextField {

    /**
     * Creates an extended text field to enter email addresses.
     */
    public EmailAddressesTextField() {
        super(0);
    }

    /**
     * Gets the email addresses entered in the text field.
     * @param recipientType Type of recipient
     * @return Email addresses entered in the text field
     * @throws InvalidEmailAddressException Thrown if there is an invalid email
     */
    public EmailAddress[] getEmailAddresses(Message.RecipientType recipientType)
        throws InvalidEmailAddressException {
        try {
            if (!isEmpty()) {
                String[] emailAddressTexts = getText().split(",\\s|,");
                EmailAddress[] emailAddresses
                    = new EmailAddress[emailAddressTexts.length];
                for (int i = 0; i < emailAddresses.length; i++) {
                    emailAddresses[i] = new EmailAddress(
                        emailAddressTexts[i].trim());
                }
                return emailAddresses;
            } else {
                return new EmailAddress[0];
            }
        } catch (InvalidEmailAddressException invalidEmailAddressException) {
            throw new InvalidEmailAddressException(
                invalidEmailAddressException.getAddress(), recipientType);
        }
    }

}
