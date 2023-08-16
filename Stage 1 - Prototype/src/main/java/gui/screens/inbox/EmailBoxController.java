package gui.screens.inbox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import javax.mail.Message;
import javax.mail.MessagingException;
import email.HostConnectionFailureException;
import email.address.InvalidEmailAddressException;
import file.FileCanNotDeleteException;
import gui.screens.Screen;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import email.email.Email;
import email.EmailInbox;
import email.address.EmailAddress;
import gui.Main;

/**
 * Controller for a draft email box.
 * @author Jordan Jones
 */
public class EmailBoxController {

    //CONSTANTS
    private static final int CONTENTS_LINE_LIMIT = 50;
    private static final String ATTACHMENTS = "%s Attachments";
    private static final String EMPTY_TEXT = "<none>";

    //FXML Attributes
    @FXML private HBox emailBox;
    @FXML private Label receivedDateLabel;
    @FXML private Label senderLabel;
    @FXML private Label recipientsLabel;
    @FXML private Label carbonCopiesLabel;
    @FXML private Label blindCarbonCopiesLabel;
    @FXML private Label subjectLabel;
    @FXML private Label contentsLabel;
    @FXML private Label attachmentNumberLabel;
    @FXML private VBox buttons;
    @FXML private Button spamButton;
    @FXML private Button editButton;
    @FXML private Button notSpamButton;
    @FXML private Button restoreButton;


    //Attributes
    private InboxScreenController inboxScreenController;
    private Email email;

    /**
     * Goes the view screen.
     * @throws FileCanNotDeleteException Thrown if a files can't be deleted
     * @throws InvalidEmailAddressException Thrown if there's an invalid address
     * @throws IOException Thrown if error with email's attachments
     * @throws MessagingException Thrown if error with the java mail server
     */
    @FXML
    private void onViewButton() throws FileCanNotDeleteException,
        InvalidEmailAddressException, IOException, MessagingException {
        getInboxScreenController().getRefreshService().cancel();
        if (getInboxScreenController().getEmailInbox().readEmail(getEmail())) {
            Main.INBOX_SCREEN.getController().saveEmailsLocally();
        }
        Main.VIEW_SCREEN.load();
        Main.VIEW_SCREEN.getController().setController(
            getInboxScreenController().getEmailInbox().getEmail(
                getEmail().getMessageId()),
            getInboxScreenController().getEmailInbox().getEmailInboxType());
        Screen.getPrimaryStage().sizeToScene();
    }

    /**
     * Sets the email as spam.
     * @throws HostConnectionFailureException Thrown if you can't connect
     * @throws IOException Thrown if error with the email box fxml
     * @throws MessagingException Thrown if error with email server
     */
    @FXML
    private void onSpamButton()
        throws HostConnectionFailureException, IOException, MessagingException {
        Main.getLoggedInAccount().spamEmail(getEmail());
        getInboxScreenController().getEmailBoxes().getChildren().remove(
            getEmailBox());
        getInboxScreenController().getEmails().remove(new Pair<>(getEmail()
            .getMessageId(), getEmail().getHeader().getReceivedDate()));
        getInboxScreenController().addNextEmailBox();
    }

    /**
     * Goes the edit screen.
     * @throws FileCanNotDeleteException Thrown if a files can't be deleted
     * @throws InvalidEmailAddressException Thrown if there's an invalid address
     * @throws IOException Thrown if error with email's attachments
     * @throws MessagingException Thrown if error with the java mail server
     */
    @FXML
    private void onEditButton() throws FileCanNotDeleteException,
        InvalidEmailAddressException, IOException, MessagingException {
        getInboxScreenController().getRefreshService().cancel();
        Main.CREATE_SCREEN.load();
        Main.CREATE_SCREEN.getController().setController(
            getInboxScreenController().getEmailInbox().getEmail(
            getEmail().getMessageId()));
        Screen.getPrimaryStage().sizeToScene();
    }



    /**
     * Sets the email as not spam.
     * @throws HostConnectionFailureException Thrown if you can't connect
     * @throws IOException Thrown if error with the email box fxml
     * @throws MessagingException Thrown if error with email server
     */
    @FXML
    private void onNotSpamButton()
        throws HostConnectionFailureException, IOException, MessagingException {
        Main.getLoggedInAccount().notSpamEmail(getEmail());
        getInboxScreenController().getEmailBoxes().getChildren().remove(
            getEmailBox());
        getInboxScreenController().getEmails().remove(new Pair<>(getEmail()
            .getMessageId(), getEmail().getHeader().getReceivedDate()));
        getInboxScreenController().addNextEmailBox();
    }

    /**
     * Restores the email from being deleted.
     * @throws HostConnectionFailureException Thrown if you can't connect
     * @throws IOException Thrown if error with the email box fxml
     * @throws MessagingException Thrown if error with email server
     */
    @FXML
    private void onRestoreButton()
        throws HostConnectionFailureException, IOException, MessagingException {
        Main.getLoggedInAccount().restoreEmail(getEmail());
        getInboxScreenController().getEmailBoxes().getChildren().remove(
            getEmailBox());
        getInboxScreenController().getEmails().remove(new Pair<>(getEmail()
            .getMessageId(), getEmail().getHeader().getReceivedDate()));
        getInboxScreenController().addNextEmailBox();
    }

    /**
     * Deletes the given email.
     * @throws IOException Thrown if error with the email box fxml
     * @throws MessagingException Thrown if error with the host's server
     */
    @FXML
    private void onDeleteButton() throws IOException, MessagingException {
        try {
            getInboxScreenController().getEmailInbox().deleteEmail(getEmail());
        } catch (ArrayIndexOutOfBoundsException e) {
            //Email already deleted from inbox so no can continue as normal
        }
        getInboxScreenController().getEmailBoxes().getChildren().remove(
            getEmailBox());
        getInboxScreenController().getEmails().remove(
            new Pair<>(getEmail().getMessageId(),
                getEmail().getHeader().getReceivedDate()));
        getInboxScreenController().addNextEmailBox();
    }

    /**
     * Sets the controller's data.
     * @param inboxScreenController Controller of the parent screen
     * @param email Email to display the details of
     */
    public void setController(InboxScreenController inboxScreenController,
        Email email) {
        setInboxScreenController(inboxScreenController);
        setEmail(email);
        setSenderLabel(email.getHeader().getSender(), email.getIsRead());
        setReceivedDateLabel(email.getHeader().getReceivedDate());
        setRecipientsLabel(
            email.getHeader().getRecipients(Message.RecipientType.TO),
            email.getIsRead());
        setCarbonCopiesLabel(
            email.getHeader().getRecipients(Message.RecipientType.CC),
            email.getIsRead());
        setBlindCarbonCopiesLabel(
            email.getHeader().getRecipients(Message.RecipientType.BCC),
            email.getIsRead());
        setSubjectLabel(email.getHeader().getSubject(),
            email.getIsRead());
        setContentsLabel(email.getBody().getContents().getText(),
            email.getIsRead());
        setAttachmentNumberLabel(email.getBody().getAttachmentNumber(),
            email.getIsRead());
        setButtons();
    }

    /**
     * Sets the inbox screen controller.
     * @param inboxScreenController Inbox screen controller
     */
    private void setInboxScreenController(
        InboxScreenController inboxScreenController) {
        this.inboxScreenController = inboxScreenController;
    }

    /**
     * Sets the email of the email box controller.
     * @param email Email to display the details of
     */
    private void setEmail(@NotNull Email email) {
        this.email = email;
    }

    /**
     * Sets the sender label.
     * @param sender Sender of the email
     * @param isRead True if the email has been read
     * @throws EnumConstantNotPresentException Thrown if no code for given enum
     */
    private void setSenderLabel(EmailAddress sender, boolean isRead) {
        EmailInbox.EmailInboxType emailInboxType
            = getInboxScreenController().getEmailInbox().getEmailInboxType();
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
        if (!isRead) {
            this.senderLabel.setStyle("-fx-font-weight: bold");
        }
    }

    /**
     * Sets the received date of the email.
     * @param receivedDate Received date of the email
     */
    private void setReceivedDateLabel(@NotNull Date receivedDate) {
        this.receivedDateLabel.setText(receivedDate.toString());
    }

    /**
     * Displays the recipients of the email.
     * @param recipients Recipients of the email
     * @param isRead True if the email has been read
     */
    private void setRecipientsLabel(EmailAddress[] recipients, boolean isRead) {
        String string = EmailAddress.arrayToString(recipients);
        if (string.equals("")) {
            string = EMPTY_TEXT;
        }
        this.recipientsLabel.setText(string);
        if (!isRead) {
            this.recipientsLabel.setStyle("-fx-font-weight: bold");
        }
    }

    /**
     * Displays the carbon copies of the email.
     * @param carbonCopies Carbon copies of the email
     * @param isRead True if the email has been read
     */
    private void setCarbonCopiesLabel(EmailAddress[] carbonCopies,
        boolean isRead) {
        String string = EmailAddress.arrayToString(carbonCopies);
        if (string.equals("")) {
            string = EMPTY_TEXT;
        }
        this.carbonCopiesLabel.setText(string);
        if (!isRead) {
            this.carbonCopiesLabel.setStyle("-fx-font-weight: bold");
        }
    }

    /**
     * Displays the blind carbon copies of the email.
     * @param blindCarbonCopies Blind carbon copies of the email
     * @param isRead True if the email has been read
     */
    private void setBlindCarbonCopiesLabel(EmailAddress[] blindCarbonCopies,
        boolean isRead) {
        String string = EmailAddress.arrayToString(blindCarbonCopies);
        if (string.equals("")) {
            string = EMPTY_TEXT;
        }
        this.blindCarbonCopiesLabel.setText(string);
        if (!isRead) {
            this.blindCarbonCopiesLabel.setStyle("-fx-font-weight: bold");
        }
    }

    /**
     * Displays the subject of the email.
     * @param subject Subject of the email
     * @param isRead True if the email has been read
     */
    private void setSubjectLabel(@NotNull String subject, boolean isRead) {
        if (!subject.equals("")) {
            this.subjectLabel.setText(subject);
        } else {
            this.subjectLabel.setText(EMPTY_TEXT);
        }
        if (!isRead) {
            this.subjectLabel.setStyle("-fx-font-weight: bold");
        }
    }

    /**
     * Displays the contents of the email.
     * @param contents Contents of the email
     * @param isRead True if the email has been read
     */
    private void setContentsLabel(@NotNull String contents, boolean isRead) {
        String text = "";
        if (!contents.equals("")) {
            Document document = Jsoup.parse(contents);
            document.select("title").remove();
            String[] contentLines = document.text().split("\r\n");
            String firstLine = "";
            for (String contentLine : contentLines) {
                firstLine = contentLine.trim();
                if (!firstLine.equals("")) {
                    break;
                }
            }
            if (contentLines.length > 1) {
                if (firstLine.length() > CONTENTS_LINE_LIMIT) {
                    text = firstLine.substring(0, CONTENTS_LINE_LIMIT) + "...";
                } else {
                    text = firstLine + "...";
                }
            } else {
                if (firstLine.length() > CONTENTS_LINE_LIMIT) {
                    text = firstLine.substring(0, CONTENTS_LINE_LIMIT) + "...";
                } else {
                    text = firstLine;
                }
            }
        }
        this.contentsLabel.setText(text);
        if (!isRead) {
            this.contentsLabel.setStyle("-fx-font-weight: bold");
        }
    }

    /**
     * Sets how many attachments the email has.
     * @param attachmentNumber Number of the attachments the email has
     * @param isRead True if the email has been read
     */
    private void setAttachmentNumberLabel(int attachmentNumber,
        boolean isRead) {
        if (attachmentNumber > 0) {
            this.attachmentNumberLabel.setText(
                String.format(ATTACHMENTS, attachmentNumber));
        }
        if (!isRead) {
            this.attachmentNumberLabel.setStyle("-fx-font-weight: bold");
        }
    }

    /**
     * Sets the edit button.
     * @throws EnumConstantNotPresentException Thrown if no code for given enum
     */
    private void setButtons() {
        EmailInbox.EmailInboxType emailInboxType
            = getInboxScreenController().getEmailInbox().getEmailInboxType();
        ArrayList<Button> remove = new ArrayList<>(Arrays.asList(
            getSpamButton(), getEditButton(), getNotSpamButton(),
            getRestoreButton()));
        switch (emailInboxType) {
            case INBOX:
                remove.remove(getSpamButton());
                break;
            case DRAFTS:
                remove.remove(getEditButton());
                break;
            case SENT:
                //Do Nothing
                break;
            case SPAM:
                remove.remove(getNotSpamButton());
                break;
            case BIN:
                remove.remove(getRestoreButton());
                break;
            default:
                throw new EnumConstantNotPresentException(
                    EmailInbox.EmailInboxType.class, emailInboxType.toString());
        }
        getButtons().getChildren().removeAll(remove);
    }

    /**
     * Gets the main pain.
     * @return Main pane
     */
    private HBox getEmailBox() {
        return emailBox;
    }

    /**
     * Gets the pane containing the buttons.
     * @return Pane containing the buttons
     */
    private VBox getButtons() {
        return buttons;
    }

    /**
     * Gets the spam button.
     * @return Spam button
     */
    private Button getSpamButton() {
        return spamButton;
    }

    /**
     * Gets the edit button.
     * @return Edit button
     */
    private Button getEditButton() {
        return editButton;
    }

    /**
     * Gets the not spam button.
     * @return Not spam button
     */
    private Button getNotSpamButton() {
        return notSpamButton;
    }

    /**
     * Gets the restore button.
     * @return Restore button
     */
    private Button getRestoreButton() {
        return restoreButton;
    }

    /**
     * Gets the inbox screen controller.
     * @return Inbox screen controller
     */
    private InboxScreenController getInboxScreenController() {
        return inboxScreenController;
    }

    /**
     * Gets the email of the draft box.
     * @return Email of the draft box
     */
    private Email getEmail() {
        return email;
    }

}
