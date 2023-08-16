package email.email.body;

import java.io.IOException;
import java.io.Serializable;
import javax.mail.Message;
import org.jetbrains.annotations.NotNull;
import email.address.EmailAddress;
import email.email.EmailHeader;
import file.ExtendedFile;

/**
 * Represents an email body.
 * @author Jordan Jones
 */
public class EmailBody implements Serializable {

    //Attributes
    private EmailContents contents;
    private ExtendedFile[] attachments;
    private int attachmentNumber;

    /**
     * Creates an email body.
     * @param header Header of the same email
     * @param contents Contents of the email
     * @param attachments Attachments of the email
     */
    public EmailBody(EmailHeader header, EmailContents contents,
        ExtendedFile[] attachments) {
        setContents(header, contents);
        setAttachments(attachments);
        setAttachmentNumber(attachments.length);
    }

    /**
     * Creates a minimum email body.
     * @param header Header of the same email
     * @param contents Contents of the email
     * @param attachmentNumber Number of attachments
     */
    public EmailBody(EmailHeader header, EmailContents contents,
        int attachmentNumber) {
        setContents(header, contents);
        setAttachmentNumber(attachmentNumber);
    }

    /**
     * Sets the contents of the email.
     * @param header Header of the same email
     * @param contents Contents of the email
     */
    private void setContents(@NotNull EmailHeader header,
        EmailContents contents) {
        this.contents = contents;
        if (this.contents.getTags() != null) {
            for (EmailAddress emailAddress
                : header.getRecipients(Message.RecipientType.TO)) {
                emailAddress.setTags(this.contents.getTags());
            }
            for (EmailAddress emailAddress
                : header.getRecipients(Message.RecipientType.CC)) {
                emailAddress.setTags(this.contents.getTags());
            }
            for (EmailAddress emailAddress
                : header.getRecipients(Message.RecipientType.BCC)) {
                emailAddress.setTags(this.contents.getTags());
            }
        }
    }

    /**
     * Sets the attachments of the email.
     * @param attachments Attachments of the email
     */
    private void setAttachments(ExtendedFile[] attachments) {
        this.attachments = attachments;
    }

    /**
     * Sets the number of attachments.
     * @param attachmentNumber Number of attachments
     */
    private void setAttachmentNumber(int attachmentNumber) {
        this.attachmentNumber = attachmentNumber;
    }

    /**
     * Gets the contents of the email.
     * @return Contents of the email
     */
    public EmailContents getContents() {
        return contents;
    }

    /**
     * Gets the attachments of the email.
     * @return Attachments of the email
     */
    public ExtendedFile[] getAttachments() {
        return attachments;
    }

    /**
     * Gets the number of attachments.
     * @return Number of attachments
     */
    public int getAttachmentNumber() {
        return attachmentNumber;
    }

    /**
     * Saves the attachments locally.
     * @throws IOException Thrown if error with saving the file to the system
     */
    public void saveAttachmentsLocally() throws IOException {
        for (ExtendedFile file : getAttachments()) {
            file.create();
        }
    }

    /**
     * Gets body as a string.
     * @return Body as a string
     */
    @Override
    public String toString() {
        return getContents().getText() + "\n";
    }

}
