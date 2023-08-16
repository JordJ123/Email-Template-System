package email.email;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

import email.EmailAccount;
import email.HostConnectionFailureException;
import email.service.MissingEmailServiceInboxName;
import gui.Main;
import javafx.util.Pair;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.jetbrains.annotations.NotNull;
import email.address.EmailAddress;
import email.address.EmptyTagValueException;
import email.email.body.EmailBody;

/**
 * Represents an email.
 * @author Jordan Jones
 */
public class Email implements Serializable {

    //CONSTANTS
    private static final String TO_STRING = "%s%s";

    //Attributes
    private String messageId = null;
    private EmailHeader header;
    private EmailBody body;
    private boolean isRead = true;

    /**
     * Creates an email.
     * @param messageId Message id of the email
     * @param header Header of the email
     * @param body Body of the email
     * @param isRead True if the email has been read
     */
    public Email(String messageId, EmailHeader header, EmailBody body,
        boolean isRead) {
        setMessageId(messageId);
        setEmail(header, body);
        setIsRead(isRead);
    }

    /**
     * Creates an email.
     * @param header Header of the email
     * @param body Body of the email
     */
    public Email(EmailHeader header, EmailBody body) {
        setEmail(header, body);
    }

    /**
     * Sets all the main details of an email.
     * @param header Header of the email
     * @param body Body of the email
     */
    public void setEmail(EmailHeader header, EmailBody body) {
        setHeader(header);
        setBody(body);
    }

    /**
     * Sets the message id of the email.
     * @param messageId Message of the email
     */
    private void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * Sets the header of the email.
     * @param header Header of the email
     */
    private void setHeader(EmailHeader header) {
        this.header = header;
    }

    /**
     * Sets the body of the email.
     * @param body Body of the email
     */
    private void setBody(EmailBody body) {
        this.body = body;
    }

    /**
     * Sets if the email has been read.
     * @param isRead True if the email has been read
     */
    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    /**
     * Gets the message id of the email.
     * @return message id of the email
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Gets the header of the email.
     * @return Header of the email
     */
    public EmailHeader getHeader() {
        return header;
    }

    /**
     * Gets the body of the email.
     * @return Body of the email
     */
    public EmailBody getBody() {
        return body;
    }

    /**
     * Gets if the email has been read.
     * @return True if the email has been read
     */
    public boolean getIsRead() {
        return isRead;
    }

    /**
     * Generates the message of the email for draft purposes.
     * @param emailAccount Email account that the message is to be sent from
     * @return Message to be part of the email
     * @throws IOException Thrown if issue with attachment files
     * @throws MessagingException Thrown if issue with generating message
     */
    public MimeMessage generateMessage(@NotNull EmailAccount emailAccount)
        throws IOException, MessagingException, MissingEmailServiceInboxName, HostConnectionFailureException {
        MimeMessage message = new MimeMessage(emailAccount.getSession());
        message.setSender(new InternetAddress(
            emailAccount.getEmailAddress().getAddress()));
        for (EmailAddress recipient
            : getHeader().getRecipients(Message.RecipientType.TO)) {
            message.addRecipient(Message.RecipientType.TO,
                new InternetAddress(recipient.getAddress()));
        }
        for (EmailAddress carbonCopies
            : getHeader().getRecipients(Message.RecipientType.CC)) {
            message.addRecipient(Message.RecipientType.CC,
                new InternetAddress(carbonCopies.getAddress()));
        }
        for (EmailAddress blindCarbonCopies
            : getHeader().getRecipients(Message.RecipientType.BCC)) {
            message.addRecipient(Message.RecipientType.BCC,
                new InternetAddress(blindCarbonCopies.getAddress()));
        }
        message.setSubject(getHeader().getSubject());
        Multipart messageMultipart = new MimeMultipart();
        MimeBodyPart textBodyPart = new MimeBodyPart();
        Multipart textMultipart = new MimeMultipart();
        MimeBodyPart textBP = new MimeBodyPart();
        textBP.setContent(getBody().getContents().getText(),
            "text/html; charset=utf-8");
        textMultipart.addBodyPart(textBP);
        textBodyPart.setContent(textMultipart);
        messageMultipart.addBodyPart(textBodyPart);
        generateMessageAttachments(messageMultipart);
        if (messageMultipart.getCount() > 0) {
            message.setContent(messageMultipart);
        }
        return message;
    }

    /**
     * Generates the message of the email.
     * @param emailAccount Email account that the message is to be sent from
     * @param group Set of email addresses that the email is for
     * @return Message to be part of the email
     * @throws EmptyTagValueException Thrown if an address' tag value is empty
     * @throws IOException Thrown if issue with attachment files
     * @throws MessagingException Thrown if issue with generating message
     */
    public MimeMessage generateMessageWithTags(
        @NotNull EmailAccount emailAccount,
        @NotNull ArrayList<Pair<EmailAddress, Message.RecipientType>> group)
        throws EmptyTagValueException, IOException, MessagingException,
        MissingEmailServiceInboxName, HostConnectionFailureException {
        MimeMessage message = new MimeMessage(emailAccount.getSession());
        message.setSender(new InternetAddress(
            emailAccount.getEmailAddress().getAddress()));
        for (Pair<EmailAddress, Message.RecipientType> emailAddress
            : group) {
            message.addRecipient(emailAddress.getValue(),
                new InternetAddress(emailAddress.getKey().getAddress()));
        }
        message.setSubject(getHeader().getSubject());
        Multipart messageMultipart = new MimeMultipart();
        MimeBodyPart textBodyPart = new MimeBodyPart();
        Multipart textMultipart = new MimeMultipart();
        MimeBodyPart textBP = new MimeBodyPart();
        textBP.setContent(getBody().getContents()
            .getFilledText(group.get(0).getKey()), "text/html; charset=utf-8");
        textMultipart.addBodyPart(textBP);
        textBodyPart.setContent(textMultipart);
        messageMultipart.addBodyPart(textBodyPart);
        generateMessageAttachments(messageMultipart);
        if (messageMultipart.getCount() > 0) {
            message.setContent(messageMultipart);
        }
        return message;
    }

    /**
     * Generates the attachments for a message.
     * @param messageMultipart Message multipart ot add the attachment to
     * @throws IOException Thrown if error with the file being attached
     * @throws MessagingException Thrown if error with the multipart
     */
    private void generateMessageAttachments(Multipart messageMultipart)
        throws IOException, MessagingException {
        if (getBody().getAttachments().length > 0) {
            for (File attachment : getBody().getAttachments()) {
                MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                Multipart attachmentMultipart = new MimeMultipart();
                MimeBodyPart attachmentBP = new MimeBodyPart();
                attachmentBP.attachFile(attachment);
                attachmentMultipart.addBodyPart(attachmentBP);
                attachmentBodyPart.setContent(attachmentMultipart);
                messageMultipart.addBodyPart(attachmentBodyPart);
            }
        }
    }

    /**
     * Checks if the two emails are equal based on their message id.
     * @param o Checks if two emails are equal based on their message id
     * @return True if the two emails are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Email email = (Email) o;
        return Objects.equals(getMessageId(), email.getMessageId());
    }

    /**
     * Gets the hash code of the email's message id.
     * @return Hash code the email's message id
     */
    @Override
    public int hashCode() {
        return Objects.hash(getMessageId());
    }

    /**
     * Converts the attributes of the email into a printable format.
     * @return Email in a printable format
     */
    @Override
    public String toString() {
        return String.format(TO_STRING, getHeader(), getBody());
    }

}
