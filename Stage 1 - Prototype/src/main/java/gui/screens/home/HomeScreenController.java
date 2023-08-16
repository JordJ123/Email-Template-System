package gui.screens.home;

import java.io.IOException;

import file.FileCanNotDeleteException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javax.mail.MessagingException;
import email.EmailInbox;
import email.HostConnectionFailureException;
import gui.Main;
import gui.screens.FXMLLoadException;
import gui.screens.Screen;

/**
 * Controller for the home screen.
 * @author Jordan Jones
 */
public class HomeScreenController {

    //FXML Attributes
    @FXML private Label errorLabel;

    /**
     * Takes you to the create screen.
     * @throws FileCanNotDeleteException Thrown if a files can't be deleted
     * @throws IOException Thrown if it can't find fxml file
     */
    @FXML
    private void onCreateButton()
        throws FileCanNotDeleteException, IOException  {
        getErrorLabel().setText("");
        Main.CREATE_SCREEN.load();
        Main.CREATE_SCREEN.getController().setController(null);
        Screen.getPrimaryStage().sizeToScene();
    }

    /**
     * Takes you to the inbox screen.
     * @throws ClassNotFoundException Thrown if error with screen's inbox emails
     * @throws IOException Thrown if it can't find fxml file
     * @throws MessagingException Thrown if error with java mail server
     */
    @FXML
    private void onInboxButton() throws ClassNotFoundException,
        IOException, MessagingException {
        inboxButton(EmailInbox.EmailInboxType.INBOX);
    }

    /**
     * Takes you to the drafts inbox screen.
     * @throws ClassNotFoundException Thrown if error with screen's inbox emails
     * @throws IOException Thrown if it can't find fxml file
     * @throws MessagingException Thrown if error with java mail server
     */
    @FXML
    private void onDraftsButton() throws ClassNotFoundException,
        IOException, MessagingException {
        inboxButton(EmailInbox.EmailInboxType.DRAFTS);
    }

    /**
     * Takes you to the sent inbox screen.
     * @throws ClassNotFoundException Thrown if error with screen's inbox emails
     * @throws IOException Thrown if it can't find fxml file
     * @throws MessagingException Thrown if error with java mail server
     */
    @FXML
    private void onSentButton() throws ClassNotFoundException,
        IOException, MessagingException {
        inboxButton(EmailInbox.EmailInboxType.SENT);
    }

    /**
     * Takes you to the spam inbox screen.
     * @throws ClassNotFoundException Thrown if error with screen's inbox emails
     * @throws IOException Thrown if it can't find fxml file
     * @throws MessagingException Thrown if error with java mail server
     */
    @FXML
    private void onSpamButton() throws ClassNotFoundException,
        IOException, MessagingException {
        inboxButton(EmailInbox.EmailInboxType.SPAM);
    }

    /**
     * Takes you to the bin inbox screen.
     * @throws ClassNotFoundException Thrown if error with screen's inbox emails
     * @throws IOException Thrown if it can't find fxml file
     * @throws MessagingException Thrown if error with java mail server
     */
    @FXML
    private void onBinButton() throws ClassNotFoundException,
        IOException, MessagingException {
        inboxButton(EmailInbox.EmailInboxType.BIN);
    }

    /**
     * Logouts and takes you to the login screen.
     * @throws IOException Thrown if it can't find fxml file
     */
    @FXML
    private void onSwitchButton() throws IOException {
        getErrorLabel().setText("");
        Main.setLoggedInAccount(null);
        Main.SELECT_SCREEN.load();
    }

    /**
     * Gets the error label.
     * @return Error label
     */
    private Label getErrorLabel() {
        return errorLabel;
    }

    /**
     * Opens a given inbox after a specific inbox button has been clicked.
     * @param emailInboxType Type of the email inbox to open
     * @throws ClassNotFoundException Thrown if error with loading emails
     * @throws IOException Thrown if error with file attachments
     * @throws MessagingException Thrown if error with messages
     */
    private void inboxButton(EmailInbox.EmailInboxType emailInboxType)
        throws ClassNotFoundException, IOException, MessagingException {
        getErrorLabel().setText("");
        try {
            Main.INBOX_SCREEN.load();
            Main.INBOX_SCREEN.getController().setController(
                Main.getLoggedInAccount().getInbox(emailInboxType));
            Screen.getPrimaryStage().sizeToScene();
        } catch (FXMLLoadException fxmlLoadException) {
            if (fxmlLoadException.getCause()
                instanceof HostConnectionFailureException) {
                getErrorLabel().setText(
                    fxmlLoadException.getCause().getMessage());
            } else {
                throw fxmlLoadException;
            }
        } catch (HostConnectionFailureException hostConnectionException) {
            getErrorLabel().setText(hostConnectionException.getMessage());
        }
    }

}
