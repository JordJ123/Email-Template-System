package gui.fxml.inbox;

import email.BacklogAction;
import email.EmailInbox;
import email.HostConnectionFailureException;
import email.address.EmailAddress;
import email.email.Email;
import email.service.MissingEmailServiceInboxName;
import file.FileCanNotDeleteException;
import gui.Main;
import gui.fxml.FXMLComponent;
import gui.fxml.FXMLController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Controller for a draft email box.
 * @author Jordan Jones
 */
public class EmailBoxController extends FXMLController {

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
     * @throws IOException Thrown if error with email's attachments
     */
    @FXML
    private void onViewButton() throws FileCanNotDeleteException, IOException {
        if (!getEmail().getIsRead()) {
            readEmail();
            new Thread(() -> {
                try {
                    getEmail().setIsRead(true);
                    getInboxScreenController().getEmailInbox().save();
                    getInboxScreenController().getEmailInbox()
                        .readEmail(getEmail());
                } catch (HostConnectionFailureException exception) {
                    try {
                        new BacklogAction(BacklogAction.Action.READ,
                            Main.getLoggedInAccount(),
                            getInboxScreenController().getEmailInbox()
                                .getEmailInboxType(), getEmail());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        ObservableList<Node> viewBoxSpaceChildren
            = getInboxScreenController().getViewBoxSpace().getChildren();
        viewBoxSpaceChildren.clear();
        FXMLComponent<ViewBoxController> viewBox
            = new FXMLComponent<>("inbox/ViewBox");
        viewBoxSpaceChildren.add(viewBox.load());
        getInboxScreenController().setViewBoxController(
            viewBox.getController());
        viewBox.getController().setController(getEmail(),
            getInboxScreenController().getEmailInbox().getEmailInboxType());
        getEmail().getBody().saveAttachmentsLocally();
    }

    /**
     * Sets the email as spam.
     * @throws IOException Thrown if error with the email box fxml
     * @throws MessagingException Thrown if error with email server
     */
    @FXML
    private void onSpamButton() throws IOException, MessagingException {
        try {
            Main.getLoggedInAccount().spamEmail(getEmail());
        } catch (HostConnectionFailureException | MissingEmailServiceInboxName
            exception) {
            new BacklogAction(BacklogAction.Action.SPAM,
                Main.getLoggedInAccount(), getEmail());
        }
        getInboxScreenController().getEmailBoxes().getChildren().remove(
            getEmailBox());
        getInboxScreenController().getEmailInbox().getEmails().remove(
            new Pair<>(getEmail().getMessageId(),
                getEmail().getHeader().getReceivedDate()));
        getInboxScreenController().getEmailInbox().save();
        getInboxScreenController().addNextEmailBox();
    }

    /**
     * Goes the edit screen.
     * @throws IOException Thrown if error with email's attachments
     */
    @FXML
    private void onEditButton() throws IOException {
        Main.BASE_SCREEN.getController().loadScreen(Main.CREATE_SCREEN, () -> {
            try {
                Main.CREATE_SCREEN.getController().setController(getEmail());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Sets the email as not spam.
     * @throws IOException Thrown if error with the email box fxml
     * @throws MessagingException Thrown if error with email server
     */
    @FXML
    private void onNotSpamButton() throws IOException, MessagingException {
        try {
            Main.getLoggedInAccount().notSpamEmail(getEmail());
        } catch (HostConnectionFailureException
            | MissingEmailServiceInboxName exception) {
            new BacklogAction(BacklogAction.Action.NOT_SPAM,
                Main.getLoggedInAccount(), getEmail());
        }
        getInboxScreenController().getEmailBoxes().getChildren().remove(
            getEmailBox());
        getInboxScreenController().getEmailInbox().getEmails().remove(
            new Pair<>(getEmail().getMessageId(),
                getEmail().getHeader().getReceivedDate()));
        getInboxScreenController().getEmailInbox().save();
        getInboxScreenController().addNextEmailBox();
    }

    /**
     * Restores the email from being deleted.
     * @throws IOException Thrown if error with the email box fxml
     * @throws MessagingException Thrown if error with email server
     */
    @FXML
    private void onRestoreButton() throws IOException, MessagingException {
        try {
            Main.getLoggedInAccount().restoreEmail(getEmail());
        } catch (HostConnectionFailureException | MissingEmailServiceInboxName
            exception) {
            new BacklogAction(BacklogAction.Action.RESTORE,
                Main.getLoggedInAccount(), getEmail());
        }
        getInboxScreenController().getEmailBoxes().getChildren().remove(
            getEmailBox());
        getInboxScreenController().getEmailInbox().getEmails().remove(
            new Pair<>(getEmail().getMessageId(),
                getEmail().getHeader().getReceivedDate()));
        getInboxScreenController().getEmailInbox().save();
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
            Main.getLoggedInAccount().deleteEmail(getEmail(),
                getInboxScreenController().getEmailInbox().getEmailInboxType());
        } catch (HostConnectionFailureException | MissingEmailServiceInboxName
            exception) {
            new BacklogAction(BacklogAction.Action.DELETE,
                Main.getLoggedInAccount(), getInboxScreenController()
                .getEmailInbox().getEmailInboxType(), getEmail());
        }
        getInboxScreenController().getEmailBoxes().getChildren().remove(
            getEmailBox());
        getInboxScreenController().getEmailInbox().getEmails().remove(
            new Pair<>(getEmail().getMessageId(),
                getEmail().getHeader().getReceivedDate()));
        getInboxScreenController().addNextEmailBox();
        if (getInboxScreenController().getViewBoxController() != null) {
            if (getInboxScreenController().getViewBoxController().getEmail()
                == getEmail()) {
                getInboxScreenController().getViewBoxSpace().getChildren()
                    .clear();
                getInboxScreenController().setViewBoxController(null);
            }
        }
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
        setSenderLabel(email.getHeader().getSender());
        setReceivedDateLabel(email.getHeader().getReceivedDate());
        setRecipientsLabel(
            email.getHeader().getRecipients(Message.RecipientType.TO));
        setCarbonCopiesLabel(
            email.getHeader().getRecipients(Message.RecipientType.CC));
        setBlindCarbonCopiesLabel(
            email.getHeader().getRecipients(Message.RecipientType.BCC));
        setSubjectLabel(email.getHeader().getSubject());
        setContentsLabel(email.getBody().getContents().getText());
        setAttachmentNumberLabel(email.getBody().getAttachmentNumber());
        setButtons();
        if (getEmail().getIsRead()) {
            readEmail();
        }
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
     * @throws EnumConstantNotPresentException Thrown if no code for given enum
     */
    private void setSenderLabel(EmailAddress sender) {
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
    private void setContentsLabel(@NotNull String contents) {
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
    }

    /**
     * Sets how many attachments the email has.
     * @param attachmentNumber Number of the attachments the email has
     */
    private void setAttachmentNumberLabel(int attachmentNumber) {
        if (attachmentNumber > 0) {
            this.attachmentNumberLabel.setText(
                String.format(ATTACHMENTS, attachmentNumber));
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
     * Gets the Recipients label.
     * @return Recipients label
     */
    private Label getRecipientsLabel() {
        return recipientsLabel;
    }

    /**
     * Gets the carbon copies label.
     * @return Carbon copies label
     */
    private Label getCarbonCopiesLabel() {
        return carbonCopiesLabel;
    }

    /**
     * Gets the blind carbon copies label.
     * @return Blind carbon copies label
     */
    private Label getBlindCarbonCopiesLabel() {
        return blindCarbonCopiesLabel;
    }

    /**
     * Gets the subject label.
     * @return Subject label
     */
    private Label getSubjectLabel() {
        return subjectLabel;
    }

    /**
     * Gets the contents label.
     * @return Attachment number label
     */
    private Label getContentsLabel() {
        return contentsLabel;
    }

    /**
     * Gets the attachment number label.
     * @return Attachment number label
     */
    private Label getAttachmentNumberLabel() {
        return attachmentNumberLabel;
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

    /**
     * Changes the style of the input box to read.
     */
    private void readEmail() {
        getRecipientsLabel().setStyle(getAttachmentNumberLabel()
            .getStyle().replace("-fx-font-weight: bold;", ""));
        getCarbonCopiesLabel().setStyle(getAttachmentNumberLabel()
            .getStyle().replace("-fx-font-weight: bold;", ""));
        getBlindCarbonCopiesLabel().setStyle(getAttachmentNumberLabel()
            .getStyle().replace("-fx-font-weight: bold;", ""));
        getSubjectLabel().setStyle(getAttachmentNumberLabel()
            .getStyle().replace("-fx-font-weight: bold;", ""));
        getContentsLabel().setStyle(getAttachmentNumberLabel()
            .getStyle().replace("-fx-font-weight: bold;", ""));
        getAttachmentNumberLabel().setStyle(getAttachmentNumberLabel()
            .getStyle().replace("-fx-font-weight: bold;", ""));
    }

}
