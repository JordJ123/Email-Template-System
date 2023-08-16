package gui.fxml.create;

import email.EmailAccount;
import email.EmailInbox;
import email.HostConnectionFailureException;
import email.address.EmailAddress;
import email.address.EmptyTagValueException;
import email.address.InvalidEmailAddressException;
import email.email.Email;
import email.email.EmailHeader;
import email.email.body.EmailBody;
import email.email.body.EmailContents;
import email.email.body.tag.EmailListTag;
import email.email.body.tag.EmailTag;
import email.service.MissingEmailServiceInboxName;
import file.ExtendedFile;
import file.FileCanNotDeleteException;
import file.directory.Directory;
import gui.Main;
import gui.components.EmailAddressesTextField;
import gui.components.ExtendedTextArea;
import gui.components.ExtendedTextField;
import gui.fxml.FXMLComponent;
import gui.fxml.FXMLController;
import gui.fxml.FXMLScreen;
import gui.fxml.inbox.ViewBoxController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import util.BulletPointType;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controller for the create screen.
 * @author Jordan Jones
 */
public class CreateScreenController extends FXMLController {

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
    @FXML private HBox buttons;
    @FXML private Button cancelButton;
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
        getFontSizeComboBox().setCellFactory(param -> {
            final ListCell<Integer> cell = new ListCell<>() {
                @Override
                public void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        setText(item.toString());
                    }
                }
            };
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
                getFontSizeComboBox().getSelectionModel().clearSelection();
                getFontSizeComboBox().getSelectionModel().select(
                    cell.getItem());
                e.consume();
            });
            return cell;
        });
    }

    /**
     * Changes the font family of the selected text.
     */
    @FXML
    private void onFontFamilyComboBox() {
        if (getFontFamilyComboBox().getValue() != null) {
            getErrorLabel().setText("");
            finishSelection();
            getContentsTextArea().fontFamilyChangeSelectedText(
                getFontFamilyComboBox().getValue());
        }
    }

    /**
     * Changes the font size of the selected text.
     */
    @FXML
    private void onFontSizeComboBox() {
        if (getFontSizeComboBox().getValue() != null) {
            getErrorLabel().setText("");
            finishSelection();
            getContentsTextArea().fontSizeChangeSelectedText(
                getFontSizeComboBox().getValue());
        }
    }

    /**
     * Bolds the selected text.
     */
    @FXML
    private void onBoldButton() {
        getErrorLabel().setText("");
        finishSelection();
        getContentsTextArea().boldSelectedText();
    }

    /**
     * Italicizes the selected text.
     */
    @FXML
    private void onItalicButton() {
        getErrorLabel().setText("");
        finishSelection();
        getContentsTextArea().italicizeSelectedText();
    }

    /**
     * Strikethrough the selected text.
     */
    @FXML
    private void onStrikethroughButton() {
        getErrorLabel().setText("");
        finishSelection();
        getContentsTextArea().strikethroughSelectedText();
    }

    /**
     * Subscript the selected text.
     */
    @FXML
    private void onSubscriptButton() {
        getErrorLabel().setText("");
        finishSelection();
        getContentsTextArea().subscriptSelectedText();
    }

    /**
     * Superscript the selected text.
     */
    @FXML
    private void onSuperscriptButton() {
        getErrorLabel().setText("");
        finishSelection();
        getContentsTextArea().superscriptSelectedText();
    }

    /**
     * Underlines the selected text.
     */
    @FXML
    private void onUnderlineButton() {
        getErrorLabel().setText("");
        finishSelection();
        getContentsTextArea().underlineSelectedText();
    }

    /**
     * Sets the selected line as a round bullet point line.
     */
    @FXML
    private void onRoundBulletPointButton() {
        bulletPointButton(BulletPointType.ROUND_BULLET_POINT);
    }

    /**
     * Sets the selected line as a square bullet point line.
     */
    @FXML
    private void onSquareBulletPointButton() {
        bulletPointButton(BulletPointType.SQUARE_BULLET_POINT);
    }

    /**
     * Sets the selected line as a number bullet point line.
     */
    @FXML
    private void onNumberBulletPointButton() {
        bulletPointButton(BulletPointType.NUMBER_BULLET_POINT);
    }

    /**
     * Sets the selected line as a number bullet point line.
     */
    @FXML
    private void onLetterBulletPointButton() {
        bulletPointButton(BulletPointType.LETTER_BULLET_POINT);
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
            FXMLScreen.getPrimaryStage()));
    }

    /**
     * Sends the email based on the given details.
     * @throws EmptyTagValueException Thrown if an address does not have a tag
     * @throws IOException Thrown if error with inbox screen inbox and fxml
     * @throws MessagingException Thrown if error with java mail server
     */
    @FXML
    private void onSendButton() throws EmptyTagValueException,
        IOException, MessagingException {
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
                    Main.BASE_SCREEN.getController().loadScreen(
                        Main.TAGS_SCREEN, () -> {
                            try {
                                Main.TAGS_SCREEN.getController()
                                    .setController(getEmail());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                } else {
                    Main.getLoggedInAccount().sendEmail(getEmail());
                    if (getEmail().getMessageId() == null) {
                        Main.BASE_SCREEN.getController().clear();
                    } else {
                        Main.BASE_SCREEN.getController().loadScreen(
                            Main.INBOX_SCREEN, () -> {
                            try {
                                Main.BASE_SCREEN.getController().inboxScreen(
                                    EmailInbox.EmailInboxType.DRAFTS);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
            } else {
                getErrorLabel().setText(EMPTY_RECIPIENTS_ERROR);
            }
        } catch (HostConnectionFailureException | MissingEmailServiceInboxName
            exception) {
            getErrorLabel().setText(new HostConnectionFailureException()
                .getMessage());
        } catch (InvalidEmailAddressException invalidEmailAddressException) {
            getErrorLabel().setText(String.format(INVALID_ADDRESS_ERROR,
                invalidEmailAddressException.getAddress(),
                invalidEmailAddressException.getRecipientType()
                    .toString().toUpperCase()));
        }
    }

    /**
     * Creates the email as a draft email.
     * @throws IOException Thrown if error with inbox screen inbox and fxml
     * @throws MessagingException Thrown if error with java mail server
     */
    @FXML
    private void onDraftButton() throws IOException, MessagingException {
        try {
            getErrorLabel().setText("");
            EmailHeader emailHeader = createEmailHeader();
            EmailBody emailBody = createEmailBody(emailHeader);
            if (getEmail() == null || getEmail().getMessageId() == null
                || getEmail().getMessageId() == "") {
                Main.getLoggedInAccount().getInbox(
                    EmailInbox.EmailInboxType.DRAFTS).addEmail(
                        new Email(emailHeader, emailBody));
                Main.BASE_SCREEN.getController().clear();
            } else {
                getEmail().setEmail(emailHeader, emailBody);
                Main.getLoggedInAccount().getInbox(
                    EmailInbox.EmailInboxType.DRAFTS).updateEmail(getEmail());
                Main.BASE_SCREEN.getController().loadScreen(
                    Main.INBOX_SCREEN, () -> {
                        try {
                            Main.BASE_SCREEN.getController().inboxScreen(
                                EmailInbox.EmailInboxType.DRAFTS);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
            }
        } catch (HostConnectionFailureException
            | MissingEmailServiceInboxName exception) {
            getErrorLabel().setText(new HostConnectionFailureException()
                .getMessage());
        } catch (InvalidEmailAddressException invalidEmailAddressException) {
            getErrorLabel().setText(String.format(INVALID_ADDRESS_ERROR,
                invalidEmailAddressException.getAddress(),
                invalidEmailAddressException.getRecipientType()
                    .toString().toUpperCase()));
        }
    }

    /**
     * Returns to the draft screen.
     * @throws IOException Thrown if error with inbox screen inbox and fxml
     */
    @FXML
    private void onCancelButton() throws IOException {
        Main.BASE_SCREEN.getController().loadScreen(Main.INBOX_SCREEN, () -> {
            try {
                Main.BASE_SCREEN.getController().inboxScreen(
                    EmailInbox.EmailInboxType.DRAFTS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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
        setCancelButton();
        if (getEmail() != null) {
            getRecipientsTextField().setText(EmailAddress.arrayToString(
                email.getHeader().getRecipients(Message.RecipientType.TO)));
            getCarbonCopiesTextField().setText(EmailAddress.arrayToString(
                email.getHeader().getRecipients(Message.RecipientType.CC)));
            getBlindCarbonCopiesTextField().setText(EmailAddress.arrayToString(
                email.getHeader().getRecipients(Message.RecipientType.BCC)));
            getHistoryPopup().getChildren().clear();
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
        List<String> fonts = Font.getFamilies();
        fontFamilyComboBox.setItems(FXCollections.observableList(fonts));
        fontFamilyComboBox.setCellFactory(param -> {
            final ListCell<String> cell = new ListCell<>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setText(item);
                        setStyle("-fx-font: " + Main.SMALL_FONT_SIZE + "px \""
                            + item + "\";");
                    }
                }
            };
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
                getFontFamilyComboBox().getSelectionModel().clearSelection();
                getFontFamilyComboBox().getSelectionModel().select(
                    cell.getItem());
                e.consume();
            });
            return cell;
        });
    }

    /**
     * Sets the items of the font size combo box.
     */
    private void setFontSizeComboBox() {
        fontSizeComboBox.setItems(
            FXCollections.observableList(Arrays.asList(FONT_SIZES)));
    }

    /**
     * Sets if the cancel button should be visible or not.
     */
    private void setCancelButton() {
        if (getEmail() == null || getEmail().getMessageId() == null
            || getEmail().getMessageId().equals("")) {
            getButtons().getChildren().remove(cancelButton);
        }
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
        FXMLComponent<TagBoxController> tagBox
            = new FXMLComponent<>("create/TagBox");
        getTagBoxes().getChildren().add(tagBox.load());
        tagBox.getController().setController(this, emailTag);
    }

    /**
     * Sets a file box in the file box holder.
     * @param file File that the file box is for
     * @throws IOException Thrown if error with the fxml
     */
    private void setFileBox(ExtendedFile file) throws IOException {
        FXMLComponent<SelectedFileBoxController> fileBox
            = new FXMLComponent<>("create/SelectedFileBox");
        getSelectedFileBoxes().getChildren().add(fileBox.load());
        fileBox.getController().setController(this, file);
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
     * Gets the buttons.
     * @return Button
     */
    private HBox getButtons() {
        return buttons;
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
        String[] emailAddresses = new String[0];
        if (!start.equals("")) {
             emailAddresses = EmailAccount.findSimilarHistory(start);
        }
        if (emailAddresses.length > 0) {
            getHistoryPopup().setVisible(true);
            for (String emailAddress : emailAddresses) {
                Label label = new Label(emailAddress);
                label.hoverProperty().addListener(
                    (observableValue, aBoolean, t1) -> {
                        if (label.isHover()) {
                            label.setTextFill(Color.WHITE);
                            label.setStyle("-fx-background-color: #3366CC;"
                                + "-fx-font-size: 18");
                        } else {
                            label.setTextFill(Color.BLACK);
                            label.setStyle("-fx-background-color: #ffffff;"
                                + "-fx-font-size: 18");
                        }
                });
                label.setOnMouseClicked(mouseEvent -> {
                    textField.addText(emailAddress.substring(start.length()));
                    textField.positionCaret(textField.getText().length());
                });
                label.setMaxWidth(Double.MAX_VALUE);
                label.setMaxHeight(Double.MAX_VALUE);
                label.setStyle("-fx-font-size: 18");
                getHistoryPopup().getChildren().add(label);
            }
            getHistoryPopup().relocate(textField.getLayoutX(),
                textField.getLayoutY() + textField.getHeight());
        } else {
            getHistoryPopup().setVisible(false);
        }
    }

    /**
     * Sets the selected line as a bullet point line.
     * @param bulletPointType Bullet point type to set to the line
     */
    private void bulletPointButton(BulletPointType bulletPointType) {
        getErrorLabel().setText("");
        getContentsTextArea().addBulletPointType(bulletPointType);
    }

    /**
     * Adds a tag to the tag list.
     * @param emailTag Tag to add to the tag list
     * @throws IOException Thrown if error with tag fxml
     */
    private void tagButton(@NotNull EmailTag emailTag) throws IOException {
        getErrorLabel().setText("");
        if (!emailTag.getName().replaceAll("\\(List\\)", "").equals("")) {
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

    /**
     * Finish the selection to for tag values.
     */
    private void finishSelection() {

        //Before
        String beforeText = getContentsTextArea().getText(0,
            getContentsTextArea().getSelection().getStart());
        if (getContentsTextArea().getSelectedText().matches(
            "(?s)^((?:(?!<\\!).)*)!>.*")) {
            if (beforeText.matches("(?s)^.*<!((?:(?!\\!>).)*)")) {
                getContentsTextArea().selectRange(beforeText.lastIndexOf("<!"),
                    getContentsTextArea().getSelection().getEnd());
            }
        } else if (getContentsTextArea().getSelectedText().matches("(?s)^>.*")) {
            if (beforeText.matches("(?s)^.*<!((?:(?!\\!>).)*)!")) {
                getContentsTextArea().selectRange(beforeText.lastIndexOf("<!"),
                    getContentsTextArea().getSelection().getEnd());
            }
        }

        //After
        String afterText = getContentsTextArea().getText(getContentsTextArea()
            .getSelection().getEnd(), getContentsTextArea().getText().length());
        String regex = ".*<!((?:(?!\\!>).)*)";
        if (getContentsTextArea().getSelectedText().matches("(?s)^.*<!((?:(?!\\!>).)*)")) {
            if (afterText.matches("(?s)^((?:(?!<\\!).)*)!>.*")) {
                getContentsTextArea().selectRange(getContentsTextArea()
                    .getSelection().getStart(), getContentsTextArea()
                    .getSelection().getEnd() + afterText.indexOf("!>") + 2);
            }
        } else if (getContentsTextArea().getSelectedText().matches("(?s)^.*<")) {
            if (afterText.matches("(?s)^!((?:(?!<\\!).)*)!>.*")) {
                getContentsTextArea().selectRange(getContentsTextArea()
                    .getSelection().getStart(), getContentsTextArea()
                    .getSelection().getEnd() + afterText.indexOf("!>") + 2);
            }
        }

    }

}
