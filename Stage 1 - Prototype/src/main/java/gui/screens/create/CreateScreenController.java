package gui.screens.create;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

import file.FileCanNotDeleteException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javax.mail.Message;
import javax.mail.MessagingException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import email.*;
import email.address.EmailAddress;
import email.address.EmptyTagValueException;
import email.address.InvalidEmailAddressException;
import email.email.Email;
import email.email.body.EmailBody;
import email.email.body.EmailContents;
import email.email.EmailHeader;
import email.email.body.tag.EmailListTag;
import email.email.body.tag.EmailTag;
import file.ExtendedFile;
import file.directory.Directory;
import gui.Main;
import gui.components.EmailAddressesTextField;
import gui.components.ExtendedTextArea;
import gui.components.ExtendedTextField;
import gui.screens.Screen;

/**
 * Controller for the create screen.
 * @author Jordan Jones
 */
public class CreateScreenController {

    //CONSTANTS
    private static final Integer[] FONT_SIZES = {8, 9, 10, 11, 12, 14, 16, 18,
        20, 22, 24, 26, 28, 36, 48, 72};
    public static final String EMPTY_RECIPIENTS_ERROR
        = "Please enter at least one recipient";
    public static final String INVALID_ADDRESS_ERROR
        = "Invalid email address (%s) found in the %s text field";
    public static final String WHITESPACE_TAG_ERROR
        = "Tag name must not be empty or contain any whitespaces";
    public static final String CHARACTER_TAG_ERROR
        = "Tag name must not contain <!, !> or (List)";
    public static final String EXISTING_TAG_ERROR
        = "This tag name already exists";

    //FXML Attributes
    @FXML private EmailAddressesTextField recipientsTextField;
    @FXML private EmailAddressesTextField carbonCopiesTextField;
    @FXML private EmailAddressesTextField blindCarbonCopiesTextField;
    @FXML private ExtendedTextField subjectTextField;
    @FXML private ExtendedTextArea contentsTextArea;
    @FXML private ComboBox<String> fontFamilyComboBox;
    @FXML private ComboBox<Integer> fontSizeComboBox;
    @FXML private ExtendedTextField tagNameTextField;
    @FXML private VBox tagBoxes;
    @FXML private VBox selectedFileBoxes;
    @FXML private Label errorLabel;
    @FXML private VBox historyPopup;

    //Attributes
    private Email email;
    private final LinkedHashSet<EmailTag> emailTags = new LinkedHashSet<>();
    private final LinkedHashSet<ExtendedFile> selectedFiles
        = new LinkedHashSet<>();

    /**
     * Runs on initialization.
     */
    @FXML
    private void initialize() {
        getRecipientsTextField().focusedProperty().addListener(
            (ov, oldV, newV) -> {
                if (newV) {
                    textFieldHistory(getRecipientsTextField());
                } else {
                    getHistoryPopup().setVisible(false);
                }
        });
        getRecipientsTextField().textProperty().addListener(
            (observableValue, s, t1)
                -> textFieldHistory(getRecipientsTextField()));
        getCarbonCopiesTextField().focusedProperty().addListener(
            (ov, oldV, newV) -> {
                if (newV) {
                    textFieldHistory(getCarbonCopiesTextField());
                } else {
                    getHistoryPopup().setVisible(false);
                }
            });
        getCarbonCopiesTextField().textProperty().addListener(
            (observableValue, s, t1)
                -> textFieldHistory(getCarbonCopiesTextField()));
        getBlindCarbonCopiesTextField().focusedProperty().addListener(
            (ov, oldV, newV) -> {
                if (newV) {
                    textFieldHistory(getBlindCarbonCopiesTextField());
                } else {
                    getHistoryPopup().setVisible(false);
                }
            });
        getBlindCarbonCopiesTextField().textProperty().addListener(
            (observableValue, s, t1)
                -> textFieldHistory(getBlindCarbonCopiesTextField()));
    }

    /**
     * Changes the font family of the selected text.
     */
    @FXML
    private void onFontFamilyComboBox() {
        getErrorLabel().setText("");
        getContentsTextArea().fontFamilyChangeSelectedText(
            getFontFamilyComboBox().getValue());
    }

    /**
     * Changes the font size of the selected text.
     */
    @FXML
    private void onFontSizeComboBox() {
        getErrorLabel().setText("");
        getContentsTextArea().fontSizeChangeSelectedText(
            getFontSizeComboBox().getValue());
    }

    /**
     * Bolds the selected text.
     */
    @FXML
    private void onBoldButton() {
        getErrorLabel().setText("");
        getContentsTextArea().boldSelectedText();
    }

    /**
     * Italicizes the selected text.
     */
    @FXML
    private void onItalicButton() {
        getErrorLabel().setText("");
        getContentsTextArea().italicizeSelectedText();
    }

    /**
     * Strikethrough the selected text.
     */
    @FXML
    private void onStrikethroughButton() {
        getErrorLabel().setText("");
        getContentsTextArea().strikethroughSelectedText();
    }

    /**
     * Subscript the selected text.
     */
    @FXML
    private void onSubscriptButton() {
        getErrorLabel().setText("");
        getContentsTextArea().subscriptSelectedText();
    }

    /**
     * Superscript the selected text.
     */
    @FXML
    private void onSuperscriptButton() {
        getErrorLabel().setText("");
        getContentsTextArea().superscriptSelectedText();
    }

    /**
     * Underlines the selected text.
     */
    @FXML
    private void onUnderlineButton() {
        getErrorLabel().setText("");
        getContentsTextArea().underlineSelectedText();
    }

    /**
     * Sets the selected line a bullet point line.
     */
    @FXML
    private void onBulletPointButton() {
        getErrorLabel().setText("");
        getContentsTextArea().addBulletPoint();
    }

    /**
     * Adds a tag to the list of emailTags.
     * @throws IOException Thrown if error with tag fxml
     */
    @FXML
    private void onTagButton() throws IOException {
        tagButton(new EmailTag(getTagNameTextField().getText().trim()));
    }

    /**
     * Adds a list tag to the list of emailTags.
     * @throws IOException Thrown if error with tag fxml
     */
    @FXML
    private void onTagListButton() throws IOException {
        tagButton(new EmailListTag(getTagNameTextField().getText().trim()));
    }

    /**
     * Selects a file attachment.
     * @throws IOException Thrown if there is an error with the selected files
     */
    @FXML
    private void onSelectAttachmentButton() throws IOException {
        getErrorLabel().setText("");
        addNewFiles(new FileChooser().showOpenMultipleDialog(
            Screen.getPrimaryStage()));
    }

    /**
     * Sends the email based on the given details.
     * @throws ClassNotFoundException Thrown if error with inbox screen file
     * @throws EmptyTagValueException Thrown if an address does not have a tag
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws FileCanNotDeleteException Thrown if a files can't be deleted
     * @throws IOException Thrown if error with inbox screen inbox and fxml
     * @throws MessagingException Thrown if error with java mail server
     */
    @FXML
    private void onSendButton() throws ClassNotFoundException,
        EmptyTagValueException, FileCanNotDeleteException,
        HostConnectionFailureException, IOException, MessagingException {
        try {
            getErrorLabel().setText("");
            if (!getRecipientsTextField().isEmpty()
                || !getCarbonCopiesTextField().isEmpty()
                || !getBlindCarbonCopiesTextField().isEmpty()) {
                EmailHeader emailHeader = createEmailHeader();
                EmailBody emailBody = createEmailBody(emailHeader);
                if (getEmail() == null) {
                    setEmail(new Email(emailHeader, emailBody));
                } else {
                    getEmail().setEmail(emailHeader, emailBody);
                }
                if (getEmail().getBody().getContents().getTags().length > 0) {
                    Main.TAGS_SCREEN.load();
                    Main.TAGS_SCREEN.getController().setController(getEmail());
                    Screen.getPrimaryStage().sizeToScene();
                } else {
                    Main.getLoggedInAccount().sendEmail(getEmail());
                    onCancelButton();
                }
            } else {
                getErrorLabel().setText(EMPTY_RECIPIENTS_ERROR);
            }
        } catch (InvalidEmailAddressException invalidEmailAddressException) {
            getErrorLabel().setText(String.format(INVALID_ADDRESS_ERROR,
                invalidEmailAddressException.getAddress(),
                invalidEmailAddressException.getRecipientType()
                    .toString().toUpperCase()));
        }
    }

    /**
     * Creates the email as a draft email.
     * @throws ClassNotFoundException Thrown if error with inbox screen file
     * @throws FileCanNotDeleteException Thrown if a files can't be deleted
     * @throws IOException Thrown if error with inbox screen inbox and fxml
     * @throws MessagingException Thrown if error with java mail server
     */
    @FXML
    private void onDraftButton() throws ClassNotFoundException,
        FileCanNotDeleteException, IOException, MessagingException {
        try {
            getErrorLabel().setText("");
            EmailHeader emailHeader = createEmailHeader();
            EmailBody emailBody = createEmailBody(emailHeader);
            if (getEmail() == null) {
                Main.getLoggedInAccount().getInbox(
                    EmailInbox.EmailInboxType.DRAFTS).addEmail(
                        new Email(emailHeader, emailBody));
            } else {
                getEmail().setEmail(emailHeader, emailBody);
                Main.getLoggedInAccount().getInbox(
                    EmailInbox.EmailInboxType.DRAFTS).updateEmail(getEmail());
            }
            onCancelButton();
        } catch (HostConnectionFailureException hostConnectionException) {
            getErrorLabel().setText(hostConnectionException.getMessage());
        } catch (InvalidEmailAddressException invalidEmailAddressException) {
            getErrorLabel().setText(String.format(INVALID_ADDRESS_ERROR,
                invalidEmailAddressException.getAddress(),
                invalidEmailAddressException.getRecipientType()
                    .toString().toUpperCase()));
        }
    }

    /**
     * Cancels the create email operation and goes back to the home page.
     * @throws ClassNotFoundException Thrown if error with inbox screen file
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws FileCanNotDeleteException Thrown if a files can't be deleted
     * @throws InvalidEmailAddressException Thrown if error with inbox addresses
     * @throws IOException Thrown if error with inbox screen inbox and fxml
     * @throws MessagingException Thrown if error with java mail server
     */
    @FXML
    private void onCancelButton() throws ClassNotFoundException,
        FileCanNotDeleteException, HostConnectionFailureException,
        InvalidEmailAddressException, IOException, MessagingException {
        getErrorLabel().setText("");
        try {
            new Directory(EmailAccount.TEMP_PATH).deleteFiles();
        } catch (NullPointerException nullPointerException) {
            //Nothing to delete
        }
        if (getEmail() == null || getEmail().getMessageId() == null) {
            Main.HOME_SCREEN.load();
        } else {
            Main.INBOX_SCREEN.load();
            Main.INBOX_SCREEN.getController().setController(
                Main.getLoggedInAccount().getInbox(
                    EmailInbox.EmailInboxType.DRAFTS));
            Screen.getPrimaryStage().sizeToScene();
        }
    }

    /**
     * Sets the attributes of the controller.
     * @param email Email to edit
     * @throws FileCanNotDeleteException Thrown if a files can't be deleted
     * @throws IOException Thrown if error with setting the email
     */
    public void setController(Email email) throws FileCanNotDeleteException,
        IOException {
        getContentsTextArea().setShortcuts();
        setFontFamilyComboBox();
        setFontSizeComboBox();
        setEmail(email);
        if (getEmail() != null) {
            getRecipientsTextField().setText(EmailAddress.arrayToString(
                email.getHeader().getRecipients(Message.RecipientType.TO)));
            getCarbonCopiesTextField().setText(EmailAddress.arrayToString(
                email.getHeader().getRecipients(Message.RecipientType.CC)));
            getBlindCarbonCopiesTextField().setText(EmailAddress.arrayToString(
                email.getHeader().getRecipients(Message.RecipientType.BCC)));
            getSubjectTextField().setText(email.getHeader().getSubject());
            setContentsTextArea(email);
            for (EmailTag emailTag : email.getBody().getContents().getTags()) {
                getTags().add(emailTag);
                setTagBox(emailTag);
            }
            new Directory(EmailAccount.TEMP_PATH).deleteFiles();
            getEmail().getBody().saveAttachmentsLocally();
            getSelectedFiles().addAll(Arrays.asList(
                email.getBody().getAttachments()));
            for (ExtendedFile file : email.getBody().getAttachments()) {
                setFileBox(file);
            }
        }
    }

    /**
     * Sets the contents text area contents.
     * @param email Email to get contents from
     */
    private void setContentsTextArea(@NotNull Email email) {
        contentsTextArea.addHTMLText(email.getBody().getContents().getText());
    }

    /**
     * Sets the items of the font family combo box.
     */
    private void setFontFamilyComboBox() {
        fontFamilyComboBox.setItems(
            FXCollections.observableList(Font.getFamilies()));
    }

    /**
     * Sets the items of the font size combo box.
     */
    private void setFontSizeComboBox() {
        fontSizeComboBox.setItems(
            FXCollections.observableList(Arrays.asList(FONT_SIZES)));
    }

    /**
     * Sets the email to be edited.
     * @param email Email to be edited
     */
    private void setEmail(Email email) {
        this.email = email;
    }

    /**
     * Sets a tag box in the tag box holder.
     * @param emailTag Tag that the tag box is for
     * @throws IOException Thrown if error with the fxml
     */
    private void setTagBox(EmailTag emailTag) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
            "TagBox.fxml"));
        HBox tagBox = fxmlLoader.load();
        ((TagBoxController) fxmlLoader.getController())
            .setController(this, emailTag);
        getTagBoxes().getChildren().add(tagBox);
        Screen.getPrimaryStage().sizeToScene();
    }

    /**
     * Sets a file box in the file box holder.
     * @param file File that the file box is for
     * @throws IOException Thrown if error with the fxml
     */
    private void setFileBox(ExtendedFile file) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
            "SelectedFileBox.fxml"));
        HBox selectedFileBox = fxmlLoader.load();
        ((SelectedFileBoxController) fxmlLoader.getController())
            .setController(this, file);
        getSelectedFileBoxes().getChildren().add(selectedFileBox);
        Screen.getPrimaryStage().sizeToScene();
    }

    /**
     * Gets the recipients text field.
     * @return Recipients text field
     */
    private EmailAddressesTextField getRecipientsTextField() {
        return recipientsTextField;
    }

    /**
     * Gets the carbon copies text field.
     * @return Carbon copies text field
     */
    private EmailAddressesTextField getCarbonCopiesTextField() {
        return carbonCopiesTextField;
    }

    /**
     * Gets the blind carbon copies text field.
     * @return Blind carbon copies text field
     */
    private EmailAddressesTextField getBlindCarbonCopiesTextField() {
        return blindCarbonCopiesTextField;
    }

    /**
     * Gets the subject text field.
     * @return Subject text field
     */
    private ExtendedTextField getSubjectTextField() {
        return subjectTextField;
    }

    /**
     * Gets the contents text area.
     * @return Contents text area
     */
    public ExtendedTextArea getContentsTextArea() {
        return contentsTextArea;
    }

    /**
     * Gets the font family combo box.
     * @return Font family combo box
     */
    private ComboBox<String> getFontFamilyComboBox() {
        return fontFamilyComboBox;
    }

    /**
     * Gets the font size combo box.
     * @return Font size combo box
     */
    private ComboBox<Integer> getFontSizeComboBox() {
        return fontSizeComboBox;
    }

    /**
     * Gets the tag name text field.
     * @return Tag name text field
     */
    private ExtendedTextField getTagNameTextField() {
        return tagNameTextField;
    }

    /**
     * Gets the tag boxes.
     * @return Tag Boxes
     */
    public VBox getTagBoxes() {
        return tagBoxes;
    }

    /**
     * Gets the selected file boxes.
     * @return Selected file boxes
     */
    public VBox getSelectedFileBoxes() {
        return selectedFileBoxes;
    }

    /**
     * Gets the error label.
     * @return Error label
     */
    private Label getErrorLabel() {
        return errorLabel;
    }

    /**
     * Gets the history popup.
     * @return History popup
     */
    private VBox getHistoryPopup() {
        return historyPopup;
    }

    /**
     * Gets the email.
     * @return Email
     */
    public Email getEmail() {
        return email;
    }

    /**
     * Gets the emailTags.
     * @return Tags
     */
    public LinkedHashSet<EmailTag> getTags() {
        return emailTags;
    }

    /**
     * Gets the selected files.
     * @return Selected files
     */
    public LinkedHashSet<ExtendedFile> getSelectedFiles() {
        return selectedFiles;
    }

    /**
     * Shows email address history when typing in a text field.
     * @param textField Extended text field being typed into
     */
    private void textFieldHistory(
        @NotNull EmailAddressesTextField textField) {
        getHistoryPopup().getChildren().clear();
        String[] emails = (textField.getText() + " ").split(",");
        String start = emails[emails.length - 1].strip();
        EmailAddress[] emailAddresses = new EmailAddress[0];
        if (!start.equals("")) {
             emailAddresses = Main.getLoggedInAccount()
                .findSimilarHistory(start);
        }
        if (emailAddresses.length > 0) {
            getHistoryPopup().setVisible(true);
            for (EmailAddress emailAddress : emailAddresses) {
                Label label = new Label(emailAddress.getAddress());
                label.hoverProperty().addListener(
                    (observableValue, aBoolean, t1) -> {
                        if (label.isHover()) {
                            label.setTextFill(Color.WHITE);
                            label.setStyle("-fx-background-color: #3366CC");
                        } else {
                            label.setTextFill(Color.BLACK);
                            label.setStyle("-fx-background-color: #ffffff");
                        }
                });
                label.setOnMouseClicked(mouseEvent -> {
                    textField.addText(
                        emailAddress.getAddress().substring(start.length()));
                    textField.positionCaret(textField.getText().length());
                });
                label.setMaxWidth(Double.MAX_VALUE);
                label.setMaxHeight(Double.MAX_VALUE);
                getHistoryPopup().getChildren().add(label);
            }
            getHistoryPopup().relocate(textField.getLayoutX(),
                textField.getLayoutY() + textField.getHeight());
        } else {
            getHistoryPopup().setVisible(false);
        }
    }

    /**
     * Adds a tag to the tag list.
     * @param emailTag Tag to add to the tag list
     * @throws IOException Thrown if error with tag fxml
     */
    private void tagButton(EmailTag emailTag) throws IOException {
        getErrorLabel().setText("");
        if (!getTagNameTextField().isEmpty()) {
            if (!getTagNameTextField().getText().contains(EmailTag.TAG_START)
                && !getTagNameTextField().getText().contains(EmailTag.TAG_END)
                && !getTagNameTextField().getText().contains(
                    EmailListTag.LIST_TAG_IDENTIFIER)) {
                if (getTags().add(emailTag)) {
                    getTagNameTextField().setText("");
                    setTagBox(emailTag);
                } else {
                    getErrorLabel().setText(EXISTING_TAG_ERROR);
                }
            } else {
                getErrorLabel().setText(CHARACTER_TAG_ERROR);
            }
        } else {
            getErrorLabel().setText(WHITESPACE_TAG_ERROR);
        }
    }

    /**
     * Adds new files to the email.
     * @param newFiles New files to be added
     * @throws IOException Thrown if error with a file
     */
    public void addNewFiles(List<File> newFiles) throws IOException {
        if (newFiles != null) {
            for (File file : newFiles) {
                ExtendedFile extendedFile = new ExtendedFile(
                    EmailAccount.TEMP_PATH + "/" + file.getName());
                Files.copy(file.toPath(), extendedFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
                getSelectedFiles().add(extendedFile);
                setFileBox(extendedFile);
            }
        }
    }

    /**
     * Creates an email header.
     * @return Email header
     * @throws InvalidEmailAddressException Thrown if error an address format
     */
    @Contract(" -> new")
    private @NotNull EmailHeader createEmailHeader()
        throws InvalidEmailAddressException {
        HashMap<Message.RecipientType, EmailAddress[]> recipients
            = new HashMap<>();
        recipients.put(Message.RecipientType.TO,
            createEmailHeaderRecipients(Message.RecipientType.TO));
        recipients.put(Message.RecipientType.CC,
            createEmailHeaderRecipients(Message.RecipientType.CC));
        recipients.put(Message.RecipientType.BCC,
            createEmailHeaderRecipients(Message.RecipientType.BCC));
        return new EmailHeader(
            Main.getLoggedInAccount().getEmailAddress(),
            recipients, getSubjectTextField().getText());
    }

    /**
     * Creates recipients for the email header.
     * @param recipientType Type of recipient
     * @return Recipients for a given recipient type.
     * @throws InvalidEmailAddressException Thrown if an error with an address
     */
    private EmailAddress @NotNull [] createEmailHeaderRecipients(
        Message.RecipientType recipientType)
        throws InvalidEmailAddressException {
        EmailAddress[] emailAddresses = new EmailAddress[0];
        if (Message.RecipientType.TO.equals(recipientType)) {
            emailAddresses = getRecipientsTextField()
                .getEmailAddresses(recipientType);
        } else if (Message.RecipientType.CC.equals(recipientType)) {
            emailAddresses = getCarbonCopiesTextField()
                .getEmailAddresses(recipientType);
        } else if (Message.RecipientType.BCC.equals(recipientType)) {
            emailAddresses = getBlindCarbonCopiesTextField()
                .getEmailAddresses(recipientType);
        }
        HashSet<EmailAddress> recipients
            = new HashSet<>(List.of(emailAddresses));
        if (getEmail() != null) {
            for (EmailAddress emailAddress
                : getEmail().getHeader().getRecipients(recipientType)) {
                if (recipients.contains(emailAddress)) {
                    recipients.remove(emailAddress);
                    recipients.add(emailAddress);
                }
            }
        }
        return recipients.toArray(new EmailAddress[0]);
    }

    /**
     * Creates an email body.
     * @param emailHeader Header of the same email
     * @return Email body
     */
    @Contract("_ -> new")
    private @NotNull EmailBody createEmailBody(EmailHeader emailHeader) {
        getTags().removeIf(emailTag -> !getContentsTextArea().getText()
            .contains(emailTag.toString()));
        return new EmailBody(emailHeader,
            new EmailContents(getContentsTextArea().toHTML(),
                getTags().toArray(new EmailTag[0])),
            getSelectedFiles().toArray(new ExtendedFile[0]));
    }

}
