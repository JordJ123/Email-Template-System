package gui.screens.tags;

import java.io.IOException;
import java.util.*;

import email.EmailInbox;
import file.FileCanNotDeleteException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.mail.Message;
import javax.mail.MessagingException;
import org.jetbrains.annotations.NotNull;
import email.email.Email;
import email.HostConnectionFailureException;
import email.address.EmailAddress;
import email.address.EmptyTagValueException;
import email.email.body.tag.EmailListTag;
import email.email.body.tag.EmailTag;
import gui.Main;
import gui.screens.Screen;

/**
 * Controller for the emailTags screen.
 * @author Jordan Jones
 */
public class TagsScreenController {

    //CONSTANTS
    public static final String MISSING_TAG_VALUE_ERROR
        = "%s is missing a tag value";

    //FXML Attributes
    @FXML private ComboBox<EmailAddress> emailAddressComboBox;
    @FXML private VBox tagBoxes;
    @FXML private Label errorLabel;

    //Attributes
    private Email email;
    private EmailAddress currentEmailAddress;
    private final HashMap<EmailTag, Object> tagBoxControllers = new HashMap<>();

    /**
     * Runs when the combo box email is changed.
     * @throws IOException Thrown if error with the list tag box fxml
     */
    @FXML
    private void onEmailAddressComboBox() throws IOException {
        if (getCurrentEmailAddress() != null) {
            for (EmailTag emailTag
                : getEmail().getBody().getContents().getTags()) {
                if (emailTag instanceof EmailListTag) {
                    ListTagBoxController listTagBoxController
                        = (ListTagBoxController) getTagBoxControllers()
                        .get(emailTag);
                    getCurrentEmailAddress().setTagValue(emailTag,
                        listTagBoxController.getTagValues());
                    listTagBoxController.setListTagValueTextFieldsBox(
                        getEmailAddressComboBox().getValue()
                            .getTagValue(emailTag));
                } else {
                    TagBoxController tagBoxController = (TagBoxController)
                        getTagBoxControllers().get(emailTag);
                    getCurrentEmailAddress().setTagValue(emailTag,
                        new String[]{tagBoxController.getTagValue()});
                    tagBoxController.setTagValueTextField(
                        getEmailAddressComboBox().getValue()
                            .getTagValue(emailTag)[0]);
                }
            }
            setCurrentEmailAddress(getEmailAddressComboBox().getValue());
        }
    }

    /**
     * Sends the email with the new email tag values.
     * @throws ClassNotFoundException Thrown if inbox class can't be found
     * @throws EmptyTagValueException Thrown if sending email with no tag values
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws IOException Thrown if error email attachments and home fxml
     * @throws MessagingException Thrown if error with java mail server
     */
    @FXML
    private void onSendButton() throws ClassNotFoundException,
        EmptyTagValueException, HostConnectionFailureException,
        IOException, MessagingException {
        getErrorLabel().setText("");
        boolean isSuccessful = true;
        for (EmailTag emailTag : getEmail().getBody().getContents().getTags()) {
            if (emailTag instanceof EmailListTag) {
                ListTagBoxController listTagBoxController
                    = (ListTagBoxController) getTagBoxControllers()
                    .get(emailTag);
                if (listTagBoxController.hasTagValues()) {
                    getCurrentEmailAddress().setTagValue(emailTag,
                        listTagBoxController.getTagValues());
                } else {
                    isSuccessful = false;
                    listTagBoxController.displayTagError();
                }
            } else {
                TagBoxController tagBoxController = (TagBoxController)
                    getTagBoxControllers().get(emailTag);
                if (tagBoxController.hasTagValue()) {
                    getCurrentEmailAddress().setTagValue(emailTag,
                        new String[]{tagBoxController.getTagValue()});
                } else {
                    isSuccessful = false;
                    tagBoxController.displayTagError();
                }
            }
        }
        if (isSuccessful) {
            for (EmailAddress emailAddress
                : getEmailAddressComboBox().getItems()) {
                for (EmailTag emailTag
                    : getEmail().getBody().getContents().getTags()) {
                    for (String value : emailAddress.getTagValue(emailTag)) {
                        if (value.equals("")) {
                            getErrorLabel().setText(String.format(
                                MISSING_TAG_VALUE_ERROR, emailAddress));
                            return;
                        }
                    }
                }
            }
            Main.getLoggedInAccount().sendEmail(getEmail());
            if (getEmail().getMessageId() != null) {
                Main.INBOX_SCREEN.load();
                Main.INBOX_SCREEN.getController().setController(
                    Main.getLoggedInAccount().getInbox(
                        EmailInbox.EmailInboxType.DRAFTS));
                Screen.getPrimaryStage().sizeToScene();
            } else {
                Main.HOME_SCREEN.load();
            }

        }
    }

    /**
     * Returns to the create screen.
     * @throws FileCanNotDeleteException Thrown if a files can't be deleted
     * @throws IOException Thrown if error with create screen fxml file
     */
    @FXML
    private void onCancelButton()
        throws FileCanNotDeleteException, IOException {
        for (EmailTag emailTag : getEmail().getBody().getContents().getTags()) {
            if (emailTag instanceof EmailListTag) {
                ListTagBoxController listTagBoxController =
                    ((ListTagBoxController) getTagBoxControllers()
                        .get(emailTag));
                getCurrentEmailAddress().setTagValue(emailTag,
                    listTagBoxController.getTagValues());
            } else {
                TagBoxController tagBoxController =
                    ((TagBoxController) getTagBoxControllers()
                        .get(emailTag));
                getCurrentEmailAddress().setTagValue(emailTag,
                    new String[]{tagBoxController.getTagValue()});
            }
        }
        Main.CREATE_SCREEN.load();
        Main.CREATE_SCREEN.getController().setController(getEmail());
        Screen.getPrimaryStage().sizeToScene();
    }

    /**
     * Sets the controller's data.
     * @param email Email that is being edited
     * @throws IOException Thrown if error with tag fxml file
     */
    public void setController(@NotNull Email email) throws IOException {
        setEmail(email);
        setComboBox();
        setCurrentEmailAddress(getEmailAddressComboBox().getValue());
        setTagBoxes();
    }

    /**
     * Sets the combo box of the screen.
     */
    private void setComboBox() {
        HashSet<EmailAddress> emailAddresses = new HashSet<>();
        emailAddresses.addAll(Arrays.asList(
            getEmail().getHeader().getRecipients(Message.RecipientType.TO)));
        emailAddresses.addAll(Arrays.asList(
            getEmail().getHeader().getRecipients(Message.RecipientType.CC)));
        emailAddresses.addAll(Arrays.asList(
            getEmail().getHeader().getRecipients(Message.RecipientType.BCC)));
        ArrayList<EmailAddress> orderedAddresses
            = new ArrayList<>(emailAddresses);
        orderedAddresses.sort((address1, address2) ->
            address1.getAddress().compareToIgnoreCase(address2.getAddress()));
        getEmailAddressComboBox().setItems(
            FXCollections.observableList(orderedAddresses));
        getEmailAddressComboBox().getSelectionModel().selectFirst();
    }

    /**
     * Sets the tag box of the screen.
     * @throws IOException Thrown if error with loading tag box fxml
     */
    private void setTagBoxes() throws IOException {
        for (EmailTag emailTag : getEmail().getBody().getContents().getTags()) {
            FXMLLoader fxmlLoader;
            HBox tagBox;
            if (emailTag instanceof EmailListTag) {
                fxmlLoader
                    = new FXMLLoader(getClass().getResource("ListTagBox.fxml"));
                tagBox = fxmlLoader.load();
                ListTagBoxController listTagBoxController
                    = fxmlLoader.getController();
                listTagBoxController.setController((EmailListTag) emailTag,
                    getCurrentEmailAddress().getTagValue(emailTag));
                getTagBoxControllers().put(emailTag, listTagBoxController);
            } else {
                fxmlLoader
                    = new FXMLLoader(getClass().getResource("TagBox.fxml"));
                tagBox = fxmlLoader.load();
                TagBoxController tagBoxController = fxmlLoader.getController();
                tagBoxController.setController(emailTag,
                    getCurrentEmailAddress().getTagValue(emailTag)[0]);
                getTagBoxControllers().put(emailTag, tagBoxController);
            }
            Screen.getPrimaryStage().sizeToScene();
            getTagBoxes().getChildren().add(tagBox);
        }
    }

    /**
     * Sets the email that is being edited.
     * @param email Email that is being edited
     */
    private void setEmail(Email email) {
        this.email = email;
    }

    /**
     * Sets the current email address.
     * @param emailAddress Current email address
     */
    private void setCurrentEmailAddress(EmailAddress emailAddress) {
        this.currentEmailAddress = emailAddress;
    }

    /**
     * Gets the email address combo box.
     * @return Email address combo box
     */
    private ComboBox<EmailAddress> getEmailAddressComboBox() {
        return emailAddressComboBox;
    }

    /**
     * Gets the tag boxes.
     * @return Tag boxes
     */
    private VBox getTagBoxes() {
        return tagBoxes;
    }

    /**
     * Gets the error label.
     * @return Error label
     */
    private Label getErrorLabel() {
        return errorLabel;
    }

    /**
     * Gets the email.
     * @return Email
     */
    private Email getEmail() {
        return email;
    }

    /**
     * Gets the current email address.
     * @return Current email address
     */
    private EmailAddress getCurrentEmailAddress() {
        return currentEmailAddress;
    }

    /**
     * Gets the tag box controllers.
     * @return Tag box controllers
     */
    private HashMap<EmailTag, Object> getTagBoxControllers() {
        return tagBoxControllers;
    }

}
