package email.email;

import java.io.Serializable;
import java.util.*;
import javax.mail.Message;
import org.jetbrains.annotations.NotNull;
import email.address.EmailAddress;

/**
 * Represents an email header.
 * @author Jordan Jones
 */
public class EmailHeader implements Serializable {

    //CONSTANTS
    private static final String TO_STRING = "Recipient: %s\n"
        + "Carbon Copies: %s\n" + "Blind Carbon Copies: %s\n"
        + "ReceivedDate: %s\n" + "Subject: %s\n";

    //Attributes
    private EmailAddress sender;
    private final HashMap<Message.RecipientType, HashSet<EmailAddress>>
        recipients = new HashMap<>();
    private String subject;
    private Date receivedDate;

    /**
     * Creates an email header.
     * @param sender Sender of the email
     * @param recipients Recipients of the email
     * @param receivedDate Date the email was received
     * @param subject Subject of the email
     */
    public EmailHeader(EmailAddress sender,
        HashMap<Message.RecipientType, EmailAddress[]> recipients,
        Date receivedDate, String subject) {
        setEmailHeader(sender, recipients, subject);
        setReceivedDate(receivedDate);
    }

    /**
     * Creates an email header.
     * @param sender Sender of the email
     * @param recipients Recipients of the email
     * @param subject Subject of the email
     */
    public EmailHeader(EmailAddress sender,
        HashMap<Message.RecipientType, EmailAddress[]> recipients,
        String subject) {
        setEmailHeader(sender, recipients, subject);
    }

    /**
     * Sets the details of the header.
     * @param sender Sender of the email
     * @param recipients Recipient of the email
     * @param subject Subject of the email
     */
    private void setEmailHeader(EmailAddress sender,
        HashMap<Message.RecipientType, EmailAddress[]> recipients,
        String subject) {
        setSender(sender);
        setRecipients(recipients);
        setSubject(subject);
    }

    /**
     * Set the sender of the email.
     * @param sender Sender of the email
     */
    private void setSender(EmailAddress sender) {
        this.sender = sender;
    }

    /**
     * Sets the recipient of the email.
     * @param recipients Recipient of the email
     */
    private void setRecipients(
        @NotNull HashMap<Message.RecipientType, EmailAddress[]> recipients) {
        setRecipientsType(recipients, Message.RecipientType.TO);
        setRecipientsType(recipients, Message.RecipientType.CC);
        setRecipientsType(recipients, Message.RecipientType.BCC);
    }

    /**
     * Sets the recipients of the email for a given recipient type.
     * @param recipients Recipients of the email
     * @param recipientType Given recipient type
     */
    private void setRecipientsType(
        @NotNull HashMap<Message.RecipientType, EmailAddress[]> recipients,
        Message.RecipientType recipientType) {
        HashSet<EmailAddress> emailAddresses
            = this.recipients.get(recipientType);
        if (emailAddresses != null) {
            HashSet<EmailAddress> tempRecipients = new HashSet<>();
            for (EmailAddress recipient : recipients.get(recipientType)) {
                if (emailAddresses.contains(recipient)) {
                    for (EmailAddress oldRecipient : emailAddresses) {
                        if (recipient.equals(oldRecipient)) {
                            tempRecipients.add(oldRecipient);
                            break;
                        }
                    }
                } else {
                    tempRecipients.add(recipient);
                }
            }
            this.recipients.put(recipientType, tempRecipients);
        } else {
            this.recipients.put(recipientType,
                new HashSet<>(Arrays.asList(recipients.get(recipientType))));
        }
    }

    /**
     * Sets the received date of the email.
     * @param receivedDate Received date of the email
     */
    private void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    /**
     * Sets the subject of the email.
     * @param subject Subject of the email
     */
    private void setSubject(String subject) {
        this.subject = Objects.requireNonNullElse(subject, "");
    }

    /**
     * Gets all the email recipients.
     * @return All the email recipients
     */
    public EmailAddress[] getAllAddresses() {
        HashSet<EmailAddress> emailAddresses = new HashSet<>();
        emailAddresses.add(getSender());
        for (HashSet<EmailAddress> hashSet : recipients.values()) {
            emailAddresses.addAll(hashSet);
        }
        return emailAddresses.toArray(EmailAddress[]::new);
    }

    /**
     * Get the sender of the email.
     * @return Sender of the email
     */
    public EmailAddress getSender() {
        return sender;
    }

    /**
     * Gets the recipients of the email for a given type.
     * @param recipientType Given recipient type
     * @return Recipient of the email for a given type
     */
    public EmailAddress[] getRecipients(Message.RecipientType recipientType) {
        return recipients.get(recipientType).toArray(new EmailAddress[0]);
    }

    /**
     * Gets the subject of the email.
     * @return Subject of the email
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Gets the received date.
     * @return Received date
     */
    public Date getReceivedDate() {
        return receivedDate;
    }

    /**
     * Gets the header in a string format.
     * @return Header in a string format
     */
    @Override
    public String toString() {

        //To Recipients
        StringBuilder recipientsString = new StringBuilder();
        for (EmailAddress recipient : getRecipients(Message.RecipientType.TO)) {
            recipientsString.append(recipient.getAddress()).append(" ");
        }

        //CC Recipients
        StringBuilder ccString = new StringBuilder();
        for (EmailAddress recipient : getRecipients(Message.RecipientType.CC)) {
            ccString.append(recipient.getAddress()).append(" ");
        }

        //BCC Recipients
        StringBuilder bccString = new StringBuilder();
        for (EmailAddress recipient
            : getRecipients(Message.RecipientType.BCC)) {
            bccString.append(recipient.getAddress()).append(" ");
        }

        return String.format(TO_STRING, recipientsString, ccString, bccString,
            getReceivedDate(), getSubject());

    }

}
