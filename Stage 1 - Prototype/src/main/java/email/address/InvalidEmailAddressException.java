package email.address;

import javax.mail.Message;

/**
 * Throws if a given email address text is not a valid email address.
 * @author Jordan Jones
 */
public class InvalidEmailAddressException extends Exception {

    //ERRORS
    private static final String MESSAGE
        = "Email address text given (%s) is in an invalid format";
    private static final String MESSAGE_WITH_TYPE
        = "%s Email address text given (%s) is in an invalid format";

    //Attributes
    private String address;
    private Message.RecipientType recipientType;

    /**
     * Creates the exception to be thrown with the given recipient type.
     * @param address Address of the invalid email address
     * @param recipientType Recipient type of the invalid email address
     */
    public InvalidEmailAddressException(String address,
        Message.RecipientType recipientType) {
        super(String.format(MESSAGE_WITH_TYPE, recipientType, address));
        setAddress(address);
        setRecipientType(recipientType);
    }

    /**
     * Creates the exception to be thrown.
     * @param address The address of the invalid email address
     */
    public InvalidEmailAddressException(String address) {
        super(String.format(MESSAGE, address));
        setAddress(address);
    }

    /**
     * Sets the address that is in the incorrect format.
     * @param address Address that is in the incorrect format
     */
    private void setAddress(String address) {
        this.address = address;
    }

    /**
     * Sets the recipient type the address was meant for.
     * @param recipientType Recipient type the address was meant for
     */
    private void setRecipientType(Message.RecipientType recipientType) {
        this.recipientType = recipientType;
    }

    /**
     * Gets the address that is in the incorrect format.
     * @return Address that is in the incorrect format
     */
    public String getAddress() {
        return address;
    }

    /**
     * Gets the recipient type the address was meant for.
     * @return  Recipient type the address was meant for
     */
    public Message.RecipientType getRecipientType() {
        return recipientType;
    }

}
