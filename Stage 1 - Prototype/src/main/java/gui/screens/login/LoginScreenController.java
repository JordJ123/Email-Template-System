package gui.screens.login;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

import file.text.TextFile;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javax.mail.*;
import email.EmailAccount;
import email.HostConnectionFailureException;
import email.address.EmailAddress;
import email.address.InvalidEmailAddressException;
import file.directory.Directory;
import gui.Main;
import gui.components.ExtendedPasswordField;
import gui.components.ExtendedTextField;

/**
 * Controller for the login screen.
 * @author Jordan Jones
 */
public class LoginScreenController {

    //ERRORS
    public static final String EMPTY_EMAIL_ERROR
        = "Please enter an email address";
    public static final String EMPTY_AP_ERROR
        = "Please enter a application password";
    public static final String INVALID_EMAIL_ERROR
        = "Please enter a valid email address";

    //FXML Attributes
    @FXML private ExtendedTextField emailAddressTextField;
    @FXML private ExtendedPasswordField applicationPasswordHiddenTextField;
    @FXML private ExtendedTextField applicationPasswordVisibleTextField;
    @FXML private CheckBox showPasswordCheckBox;
    @FXML private CheckBox rememberMeCheckBox;
    @FXML private Button backButton;
    @FXML private Label errorLabel;

    /**
     * Runs when the Login Screen fxml file has loaded.
     */
    @FXML
    private void initialize() {
        getApplicationPasswordHiddenTextField().managedProperty().bind(
            getShowPasswordCheckBox().selectedProperty().not());
        getApplicationPasswordHiddenTextField().visibleProperty().bind(
            getShowPasswordCheckBox().selectedProperty().not());
        getApplicationPasswordVisibleTextField().managedProperty().bind(
            getShowPasswordCheckBox().selectedProperty());
        getApplicationPasswordVisibleTextField().visibleProperty().bind(
            getShowPasswordCheckBox().selectedProperty());
        getApplicationPasswordVisibleTextField().textProperty()
            .bindBidirectional(getApplicationPasswordHiddenTextField()
                .textProperty());
        if (Objects.requireNonNull(new Directory(EmailAccount.ACCOUNTS_PATH)
            .listFiles()).length == 0) {
            getBackButton().setVisible(false);
        }
    }

    /**
     * Goes back to the select screen.
     * @throws IOException Thrown if error with the select screen fxml.
     */
    @FXML
    private void onBackButton() throws IOException {
        Main.SELECT_SCREEN.load();
    }

    /**
     * Logins into the given account.
     * @throws IOException Thrown if error with the create screen fxml
     * @throws MessagingException Thrown if error with the java mail server
     */
    @FXML
    private void onLoginButton() throws IOException, MessagingException {
        boolean isValid = true;
        if (getEmailAddressTextField().isEmpty()) {
            getEmailAddressTextField().displayError(EMPTY_EMAIL_ERROR);
            isValid = false;
        }
        if (getApplicationPasswordHiddenTextField().isEmpty()) {
            getApplicationPasswordHiddenTextField().displayError(
                EMPTY_AP_ERROR);
            isValid = false;
        }
        if (isValid) {
            try {
                int accountNumber = -1;
                File[] files =
                    new Directory(EmailAccount.ACCOUNTS_PATH).listFiles();
                if (files != null && files.length > 0) {
                    for (int i = 0; i < files.length; i++) {
                        int fileNumber = Integer.parseInt(files[i].getName());
                        Scanner scanner = new Scanner(new TextFile(files[i]
                            + "/login.txt"));
                        if (scanner.next().equals(getEmailAddressTextField()
                            .getText())) {
                            accountNumber = fileNumber;
                            getRememberMeCheckBox().selectedProperty()
                                .set(true);
                        } else if (i != fileNumber) {
                            accountNumber = i;
                            scanner.close();
                            break;
                        }
                        scanner.close();
                    }
                }
                if (accountNumber == -1) {
                    accountNumber = files.length;
                }
                Main.setLoggedInAccount(new EmailAccount(accountNumber,
                    new EmailAddress(getEmailAddressTextField().getText()),
                    getApplicationPasswordHiddenTextField().getText(),
                    getRememberMeCheckBox().isSelected()));
                Main.getLoggedInAccount().saveLogin();
                Main.HOME_SCREEN.load();
            } catch (AuthenticationFailedException
                | HostConnectionFailureException authenticationException) {
                getErrorLabel().setText(authenticationException.getMessage());
            } catch (InvalidEmailAddressException e) {
                getErrorLabel().setText(INVALID_EMAIL_ERROR);
            }
        }
    }

    /**
     * Gets the email address text field.
     * @return Email address text field
     */
    private ExtendedTextField getEmailAddressTextField() {
        return emailAddressTextField;
    }

    /**
     * Gets the application password hidden text field.
     * @return Application password hidden text field
     */
    private ExtendedPasswordField getApplicationPasswordHiddenTextField() {
        return applicationPasswordHiddenTextField;
    }

    /**
     * Gets the application password visible text field.
     * @return Application password visible text field
     */
    private ExtendedTextField getApplicationPasswordVisibleTextField() {
        return applicationPasswordVisibleTextField;
    }

    /**
     * Gets the show password check box.
     * @return Show password check box
     */
    private CheckBox getShowPasswordCheckBox() {
        return showPasswordCheckBox;
    }

    /**
     * Gets the remember-me check box.
     * @return Remember-me check box
     */
    private CheckBox getRememberMeCheckBox() {
        return rememberMeCheckBox;
    }

    /**
     * Gets the back button.
     * @return Back button
     */
    private Button getBackButton() {
        return backButton;
    }

    /**
     * Gets the error label.
     * @return Error label
     */
    private Label getErrorLabel() {
        return errorLabel;
    }

}
