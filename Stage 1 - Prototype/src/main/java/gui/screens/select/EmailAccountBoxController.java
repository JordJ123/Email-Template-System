package gui.screens.select;

import email.EmailAccount;
import email.HostConnectionFailureException;
import email.address.InvalidEmailAddressException;
import file.FileCanNotDeleteException;
import file.directory.Directory;
import gui.Main;
import gui.screens.Screen;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.mail.MessagingException;
import java.io.IOException;

/**
 * Controller for the Email Account Box.
 * @author Jordan Jones
 */
public class EmailAccountBoxController {

    //FXML Attributes
    @FXML private HBox emailAccountBox;
    @FXML private Label emailAddressLabel;

    //Attributes
    private VBox emailAccountBoxes;
    private int accountNumber;

    /**
     * Selects the account to be loaded in.
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws InvalidEmailAddressException Thrown if error with email address
     * @throws IOException Thrown if error with loading or fxml
     * @throws MessagingException Thrown if error with java mail server
     */
    @FXML
    private void onSelectHBox() throws HostConnectionFailureException,
        InvalidEmailAddressException, IOException, MessagingException {
        Main.setLoggedInAccount(EmailAccount.load(getAccountNumber()));
        Main.HOME_SCREEN.load();
    }

    /**
     * Logs the account out of the device.
     * @throws FileCanNotDeleteException Thrown if a files can't be deleted
     * @throws IOException Thrown if error with the login screen fxml
     */
    @FXML
    private void onLogoutButton()
        throws FileCanNotDeleteException, IOException {
        Directory directory = new Directory(String.format(
            EmailAccount.ACCOUNT_PATH, getAccountNumber()));
        directory.deleteFiles();
        directory.delete();
        getEmailAccountBoxes().getChildren().remove(getEmailAccountBox());
        if (getEmailAccountBoxes().getChildren().size() == 0) {
            Main.LOGIN_SCREEN.load();
        } else {
            Screen.getPrimaryStage().sizeToScene();
        }
    }

    /**
     * Sets the data of the screen.
     * @param emailAccountBoxes Reference to the boxes containing this box
     * @param accountNumber Number that represents the account in storage
     * @param emailAddress Email address to display
     */
    public void setController(VBox emailAccountBoxes, int accountNumber,
        String emailAddress) {
        setEmailAddressLabel(emailAddress);
        setEmailAccountBoxes(emailAccountBoxes);
        setAccountNumber(accountNumber);
    }

    /**
     * Sets the email address to the label.
     * @param emailAddress Email address to be set to the label
     */
    private void setEmailAddressLabel(String emailAddress) {
        emailAddressLabel.setText(emailAddress);
    }

    /**
     * Sets the reference to the email account boxes containing this box.
     * @param emailAccountBoxes Reference to the boxes containing this box
     */
    private void setEmailAccountBoxes(VBox emailAccountBoxes) {
        this.emailAccountBoxes = emailAccountBoxes;
    }

    /**
     * Sets the number that represents the account in storage.
     * @param accountNumber Number that represents the account in storage
     */
    private void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the email account box.
     * @return Email account box
     */
    private HBox getEmailAccountBox() {
        return emailAccountBox;
    }

    /**
     * Sets the reference to the email account boxes containing this box.
     * @return Reference to the email account boxes containing this box
     */
    private VBox getEmailAccountBoxes() {
        return emailAccountBoxes;
    }

    /**
     * Gets the number that represents the account in storage.
     * @return Number that represents the account in storage
     */
    private int getAccountNumber() {
        return accountNumber;
    }

}
