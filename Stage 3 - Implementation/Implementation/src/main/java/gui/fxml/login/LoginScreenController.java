package gui.fxml.login;

import email.EmailAccount;
import email.HostConnectionFailureException;
import email.address.EmailAddress;
import email.address.InvalidEmailAddressException;
import email.service.EmailService;
import email.service.MissingEmailServiceInboxName;
import email.service.NonSupportedEmailService;
import file.directory.Directory;
import file.text.TextFile;
import gui.Main;
import gui.components.ExtendedPasswordField;
import gui.components.ExtendedTextField;
import gui.fxml.FXMLController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Controller for the new login screen.
 * @author Jordan Jones
 */
public class LoginScreenController extends FXMLController {

    //CONSTANTS
    public static final String EMPTY_EMAIL_ERROR
        = "Please enter an email address";
    public static final String EMPTY_PASSWORD_ERROR
        = "Please enter a password";
    public static final String EMPTY_TYPE_ERROR
        = "Please select an account type";
    public static final String EMPTY_SMTP_ERROR
        = "Please enter a smtp hostname";
    public static final String EMPTY_IMAP_ERROR
        = "Please enter an imap hostname";
    public static final String INVALID_EMAIL_ERROR
        = "Please enter a valid email address";
    public static final String INVALID_TYPE_ERROR
        = "Unable to set-up inbox access for this account type";
    public static final String HOST_ERROR_EXCEPTION
        = ".\nThe account type or it's SMTP/IMAP hostnames could also be "
        + "incorrect";
    public static final String ALREADY_LOGGED_IN_ERROR
        = "This account is already logged in";

    //FXML Attributes
    @FXML private ExtendedTextField emailAddressTextField;
    @FXML private ExtendedPasswordField passwordHiddenTextField;
    @FXML private ExtendedTextField passwordVisibleTextField;
    @FXML private CheckBox showPasswordCheckBox;
    @FXML private ComboBox<String> emailServiceComboBox;
    @FXML private VBox smtpImapBox;
    @FXML private ExtendedTextField smtpTextField;
    @FXML private ExtendedTextField imapTextField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;

    /**
     * Runs when the Login Screen fxml file has loaded.
     */
    @FXML
    private void initialize() {
        getPasswordHiddenTextField().managedProperty().bind(
            getShowPasswordCheckBox().selectedProperty().not());
        getPasswordHiddenTextField().visibleProperty().bind(
            getShowPasswordCheckBox().selectedProperty().not());
        getPasswordVisibleTextField().managedProperty().bind(
            getShowPasswordCheckBox().selectedProperty());
        getPasswordVisibleTextField().visibleProperty().bind(
            getShowPasswordCheckBox().selectedProperty());
        getPasswordVisibleTextField().textProperty()
            .bindBidirectional(getPasswordHiddenTextField()
                .textProperty());
        getLoginButton().setDefaultButton(true);
        ArrayList<String> values = new ArrayList<>(EmailService.EMAIL_SERVICES
            .keySet());
        values.add("Other");
        getEmailServiceComboBox().setItems(FXCollections.observableList(
            Arrays.asList(values.toArray(new String[0]))));
    }

    /**
     * Logins into the given account.
     * @throws IOException Thrown if error with the create screen fxml
     * @throws MessagingException Thrown if error with the java mail server
     */
    @FXML
    private void onLoginButton() throws IOException, MessagingException {
        getErrorLabel().setText("");
        if (!areEmptyValues()) {
            try {
                String smtp = getSmtpTextField().getText().strip();
                String imap = getImapTextField().getText().strip();
                String serviceCheck = serviceCheckName(smtp, imap);
                boolean isExisting = false;
                File[] files =
                    new Directory(EmailAccount.ACCOUNTS_PATH).listFiles();
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        Scanner scanner = new Scanner(new TextFile(file
                            + "/login.txt"));
                        String address = scanner.next();
                        scanner.next();
                        scanner.next();
                        String service = scanner.next();
                        if (scanner.hasNext()) {
                            service = service + " " + scanner.next();
                        }
                        scanner.close();
                        if (address.equals(getEmailAddressTextField().getText())
                            && service.equals(serviceCheck)) {
                            isExisting = true;
                            break;
                        }
                    }
                }
                if (!isExisting) {
                    int accountNumber = -1;
                    if (files != null && files.length > 0) {
                        for (int i = 0; i < files.length; i++) {
                            if (!Integer.toString(i).equals(
                                files[i].getName())) {
                                accountNumber = i;
                            }
                        }
                    }
                    if (accountNumber == -1) {
                        assert files != null;
                        accountNumber = files.length;
                    }
                    EmailService emailService;
                    if (!getEmailServiceComboBox().getValue().equals("Other")) {
                        emailService = EmailService.EMAIL_SERVICES
                            .get(getEmailServiceComboBox().getValue());
                    } else {
                        emailService = new EmailService(serviceCheck,
                            smtp, imap);
                    }
                    EmailAccount emailAccount = new EmailAccount(accountNumber,
                        new EmailAddress(getEmailAddressTextField().getText()),
                        getPasswordHiddenTextField().getText(),
                        emailService, true);
                    emailAccount.setSession();
                    emailAccount.saveLogin();
                    Main.BASE_SCREEN.getController().addAccountBox(
                        emailAccount).selectAccount();
                } else {
                    getErrorLabel().setText(ALREADY_LOGGED_IN_ERROR);
                }
            } catch (AuthenticationFailedException
                | NonSupportedEmailService e) {
                getErrorLabel().setText(e.getMessage());
            } catch (HostConnectionFailureException e) {
                getErrorLabel().setText(e.getMessage() + HOST_ERROR_EXCEPTION);
            } catch (InvalidEmailAddressException e) {
                getErrorLabel().setText(INVALID_EMAIL_ERROR);
            } catch (MissingEmailServiceInboxName e) {
                System.out.println(e.getMessage());
                getErrorLabel().setText(INVALID_TYPE_ERROR);
            }
        }
    }

    /**
     * Checks if the smtp and imap values need to be visible.
     */
    @FXML
    private void onEmailServiceComboBox() {
        getSmtpImapBox().setVisible(getEmailServiceComboBox().getValue()
            .equals("Other"));
    }

    /**
     * Gets the email address text field.
     * @return Email address text field
     */
    private ExtendedTextField getEmailAddressTextField() {
        return emailAddressTextField;
    }

    /**
     * Gets the password hidden text field.
     * @return Password hidden text field
     */
    private ExtendedPasswordField getPasswordHiddenTextField() {
        return passwordHiddenTextField;
    }

    /**
     * Gets the password visible text field.
     * @return Password visible text field
     */
    private ExtendedTextField getPasswordVisibleTextField() {
        return passwordVisibleTextField;
    }

    /**
     * Gets the show password check box.
     * @return Show password check box
     */
    private CheckBox getShowPasswordCheckBox() {
        return showPasswordCheckBox;
    }

    /**
     * Gets the email service combo box.
     * @return Email service combo box
     */
    private ComboBox<String> getEmailServiceComboBox() {
        return emailServiceComboBox;
    }

    /**
     * Gets the smtp imap box.
     * @return Smtp imap box
     */
    private VBox getSmtpImapBox() {
        return smtpImapBox;
    }

    /**
     * Gets the smtp text field.
     * @return Smtp text field
     */
    private ExtendedTextField getSmtpTextField() {
        return smtpTextField;
    }

    /**
     * Gets the imap text field.
     * @return Imap text field
     */
    private ExtendedTextField getImapTextField() {
        return imapTextField;
    }

    /**
     * Gets the login button.
     * @return Login button
     */
    public Button getLoginButton() {
        return loginButton;
    }

    /**
     * Gets the error label.
     * @return Error label
     */
    private Label getErrorLabel() {
        return errorLabel;
    }

    /**
     * Checks if any of the inputs have empty values.
     * @return Input empty values
     */
    private boolean areEmptyValues() {
        boolean emptyValues = false;
        if (getEmailAddressTextField().isEmpty()) {
            getEmailAddressTextField().displayError(EMPTY_EMAIL_ERROR);
            emptyValues = true;
        }
        if (getPasswordHiddenTextField().isEmpty()) {
            getPasswordHiddenTextField().displayError(EMPTY_PASSWORD_ERROR);
            emptyValues = true;
        }
        if (getEmailServiceComboBox().getValue() == null) {
            getErrorLabel().setText(EMPTY_TYPE_ERROR);
            emptyValues = true;
        } else if (getEmailServiceComboBox().getValue().equals("Other")) {
            if (getSmtpTextField().isEmpty()) {
                getSmtpTextField().displayError(EMPTY_SMTP_ERROR);
                emptyValues = true;
            }
            if (getImapTextField().isEmpty()) {
                getImapTextField().displayError(EMPTY_IMAP_ERROR);
                emptyValues = true;
            }
        }
        return emptyValues;
    }

    /**
     * Returns the service check name.
     * @param smtp SMTP hostname
     * @param imap IMAP hostname
     * @return Service check name
     */
    private String serviceCheckName(String smtp, String imap) {
        if (!getEmailServiceComboBox().getValue().equals("Other")) {
            return EmailService.EMAIL_SERVICES
                .get(getEmailServiceComboBox().getValue()).getName();
        } else {
            return EmailService.emailServiceName(smtp, imap);
        }
    }

}
