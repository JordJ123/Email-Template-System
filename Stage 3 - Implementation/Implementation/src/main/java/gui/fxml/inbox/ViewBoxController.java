package gui.fxml.inbox;

import email.EmailAccount;
import email.EmailInbox;
import email.address.EmailAddress;
import email.email.Email;
import file.FileCanNotDeleteException;
import file.directory.Directory;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import org.jetbrains.annotations.NotNull;
import javax.mail.Message;
import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Controller for a draft email box.
 * @author Jordan Jones
 */
public class ViewBoxController {

    //CONSTANT
    private static final String EMPTY_TEXT = "<none>";

    //FXML Attributes
    @FXML private Label receivedDateLabel;
    @FXML private Label senderLabel;
    @FXML private Label recipientsLabel;
    @FXML private Label carbonCopiesLabel;
    @FXML private Label blindCarbonCopiesLabel;
    @FXML private Label subjectLabel;
    @FXML private WebView contentsWebView;
    @FXML private VBox fileLinks;

    //Attributes
    private Email email;

    /**
     * Sets the controller's data.
     * @param email Email to display the details of
     * @param emailInboxType Type of inbox the email is from
     * @throws FileCanNotDeleteException Thrown if a files can't be deleted
     * @throws IOException Thrown if error with saving the email attachments
     */
    public void setController(@NotNull Email email,
        EmailInbox.EmailInboxType emailInboxType)
        throws FileCanNotDeleteException, IOException {
        Directory directory = new Directory(EmailAccount.TEMP_PATH);
        if (!directory.exists()) {
            directory.create();
        } else {
            directory.deleteFiles();
        }
        setEmail(email);
        setReceivedDateLabel(email.getHeader().getReceivedDate());
        setSenderLabel(emailInboxType, email.getHeader().getSender());
        setRecipientsLabel(
            email.getHeader().getRecipients(Message.RecipientType.TO));
        setCarbonCopiesLabel(
            email.getHeader().getRecipients(Message.RecipientType.CC));
        setBlindCarbonCopiesLabel(
            email.getHeader().getRecipients(Message.RecipientType.BCC));
        setSubjectLabel(email.getHeader().getSubject());
        setContentsWebView(email.getBody().getContents().getText());
        setFileLinks(email.getBody().getAttachments());
    }

    /**
     * Sets the sender label.
     * @param emailInboxType Type of the inbox the email is from
     * @param sender Sender of the email
     * @throws EnumConstantNotPresentException Thrown if no code for inbox type
     */
    private void setSenderLabel(
        EmailInbox.@NotNull EmailInboxType emailInboxType,
        EmailAddress sender) {
        switch (emailInboxType) {
            case INBOX:
            case SPAM:
            case BIN:
                this.senderLabel.setText(sender.toString());
                break;
            case DRAFTS:
            case SENT:
                ((VBox) this.senderLabel.getParent()).getChildren()
                    .remove(this.senderLabel);
                break;
            default:
                throw new EnumConstantNotPresentException(
                    EmailInbox.EmailInboxType.class, emailInboxType.toString());
        }
    }

    /**
     * Sets the received date label.
     * @param receivedDate Received date
     */
    private void setReceivedDateLabel(@NotNull Date receivedDate) {
        this.receivedDateLabel.setText(receivedDate.toString());
    }

    /**
     * Displays the recipients of the email.
     * @param recipients Recipients of the email
     */
    private void setRecipientsLabel(EmailAddress[] recipients) {
        String string = EmailAddress.arrayToString(recipients);
        if (string.equals("")) {
            string = EMPTY_TEXT;
        }
        this.recipientsLabel.setText(string);
    }

    /**
     * Displays the carbon copies of the email.
     * @param carbonCopies Carbon copies of the email
     */
    private void setCarbonCopiesLabel(EmailAddress[] carbonCopies) {
        String string = EmailAddress.arrayToString(carbonCopies);
        if (string.equals("")) {
            string = EMPTY_TEXT;
        }
        this.carbonCopiesLabel.setText(string);
    }

    /**
     * Displays the blind carbon copies of the email.
     * @param blindCarbonCopies Blind carbon copies of the email
     */
    private void setBlindCarbonCopiesLabel(EmailAddress[] blindCarbonCopies) {
        String string = EmailAddress.arrayToString(blindCarbonCopies);
        if (string.equals("")) {
            string = EMPTY_TEXT;
        }
        this.blindCarbonCopiesLabel.setText(string);
    }

    /**
     * Displays the subject of the email.
     * @param subject Subject of the email
     */
    private void setSubjectLabel(@NotNull String subject) {
        if (!subject.equals("")) {
            this.subjectLabel.setText(subject);
        } else {
            this.subjectLabel.setText(EMPTY_TEXT);
        }
    }

    /**
     * Displays the contents of the email.
     * @param contents Contents of the email
     */
    private void setContentsWebView(String contents) {
        this.contentsWebView.getEngine().loadContent(contents);
    }

    /**
     * Sets the file links box.
     * @param files Files to add in the file link box.
     */
    private void setFileLinks(File @NotNull [] files) {
        for (File file : files) {
            Hyperlink hyperlink = new Hyperlink();
            hyperlink.setStyle("-fx-font-size: 14");
            hyperlink.setText(file.getName());
            hyperlink.setOnAction(event -> {
                try {
                    Runtime.getRuntime().exec("explorer /select, "
                        + file.getAbsolutePath());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            fileLinks.getChildren().add(hyperlink);
        }
    }

    /**
     * Sets the email.
     * @param email Email
     */
    private void setEmail(Email email) {
        this.email = email;
    }

    /**
     * Gets the email.
     * @return Email
     */
    public Email getEmail() {
        return email;
    }

}
