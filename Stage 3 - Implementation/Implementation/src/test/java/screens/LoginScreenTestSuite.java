package screens;

import email.EmailAccount;
import email.HostConnectionFailureException;
import email.service.MissingEmailServiceInboxName;
import file.FileCanNotDeleteException;
import file.directory.Directory;
import gui.Main;
import gui.components.ExtendedPasswordField;
import gui.components.ExtendedTextField;
import gui.fxml.login.LoginScreenController;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.Start;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Objects;

/**
 * Test Suite for the Login Screen.
 * @author Jordan Jones
 */
public class LoginScreenTestSuite extends ScreenTestSuite {

    //CONSTANTS
    private static final String ACCOUNTS_BOX = "#accountsBox";
    private static final String EMAIL_ADDRESS = "#emailAddressTextField";
    private static final String PASSWORD_HIDDEN = "#passwordHiddenTextField";
    private static final String PASSWORD_VISIBLE = "#passwordVisibleTextField";
    private static final String PASSWORD_CHECKBOX = "#showPasswordCheckBox";
    private static final String ACCOUNT_TYPE = "#emailServiceComboBox";
    private static final String SMTP_TEXT_FIELD = "#smtpTextField";
    private static final String IMAP_TEXT_FIELD = "#imapTextField";
    private static final String LOGIN_BUTTON = "#loginButton";
    private static final String ERROR_LABEL = "#errorLabel";

    /**
     * Runs at the start of each test case (JavaFx Only).
     * @param stage Javafx Window
     * @throws FileCanNotDeleteException Thrown if accounts can't be deleted
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws IOException Thrown if error loading screen
     * @throws MessagingException Thrown if error with java mail
     * @throws MissingEmailServiceInboxName Thrown if service inbox name error
     */
    @Start
    private void start(Stage stage)
        throws FileCanNotDeleteException,
        IOException, HostConnectionFailureException, MessagingException,
        MissingEmailServiceInboxName {
        setup(stage, null, true, false);
    }

    /**
     * Runs at the end of each test case (JavaFx Only).
     * @param robot Robot that uses the program
     */
    @AfterEach
    private void afterEach(@NotNull FxRobot robot) {
        cleanup(robot);
    }


    /**
     * FUNCTIONAL 1+2+3+6+7 (Google) + NON-FUNCTIONAL 1+2
     * Tests if the login button works correctly for a Google account.
     * @param robot Robot that uses the program
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws MessagingException Thrown if error with java mail
     * @throws MissingEmailServiceInboxName Thrown if email service error
     */
    @Test
    void onLoginButtonGoogle(@NotNull FxRobot robot)
        throws HostConnectionFailureException, MessagingException,
        MissingEmailServiceInboxName {
        loginButton(robot, GOOGLE);
    }

    /**
     * FUNCTIONAL 1+2+3+6+7 (Outlook) + NON-FUNCTIONAL 1+2
     * Tests if the login button works correctly for an Outlook account.
     * @param robot Robot that uses the program
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws MessagingException Thrown if error with java mail
     * @throws MissingEmailServiceInboxName Thrown if email service error
     */
    @Test
    void onLoginButtonOutlook(@NotNull FxRobot robot)
        throws HostConnectionFailureException, MessagingException,
        MissingEmailServiceInboxName {
        loginButton(robot, OUTLOOK);
    }

    /**
     * Functional 1+2+3+4+5+6+7 + NON-FUNCTIONAL 1+2
     * Tests if the login button works correctly for an other account.
     * @param robot Robot that uses the program
     */
    @Test
    void onLoginButtonOther(@NotNull FxRobot robot) {
        load(robot);
        robot.interact(() -> {
            robot.lookup(EMAIL_ADDRESS).queryAs(ExtendedTextField.class)
                .setText(OTHER.getEmailAddress().getAddress());
            robot.lookup(PASSWORD_HIDDEN).queryAs(ExtendedPasswordField.class)
                .setText(OTHER.getPassword());
            robot.lookup(ACCOUNT_TYPE).queryAs(ComboBox.class)
                .getSelectionModel().select("Other");
            robot.lookup(SMTP_TEXT_FIELD).queryAs(ExtendedTextField.class)
                .setText(OTHER.getEmailService().getSmtpHostName());
            robot.lookup(IMAP_TEXT_FIELD).queryAs(ExtendedTextField.class)
                .setText(OTHER.getEmailService().getImapHostName());
        });
        checkBox(robot);
        robot.clickOn(LOGIN_BUTTON);
        Assertions.assertThat(robot.lookup(ACCOUNTS_BOX)
            .queryAs(VBox.class).getChildren().size()).isEqualTo(1);
        Assertions.assertThat(Main.getLoggedInAccount().getEmailAddress())
            .isEqualTo(OTHER.getEmailAddress());
    }

    /**
     * FUNCTIONAL 1+2+3+6 + NON-FUNCTIONAL 3
     * Tests if the login button works correctly with an empty address.
     * @param robot Robot that uses the program
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws MessagingException Thrown if error with java mail
     * @throws MissingEmailServiceInboxName Thrown if email service error
     */
    @Test
    void onLoginButtonEmptyAddress(@NotNull FxRobot robot)
        throws HostConnectionFailureException, MessagingException,
        MissingEmailServiceInboxName {
        load(robot);
        setEmailAccount(GOOGLE, false);
        robot.interact(() -> {
            robot.lookup(PASSWORD_HIDDEN).queryAs(ExtendedPasswordField.class)
                .setText(getEmailAccount().getPassword());
            robot.lookup(ACCOUNT_TYPE).queryAs(ComboBox.class)
                .getSelectionModel().select(getEmailAccount()
                    .getEmailService().getName());
        });
        robot.clickOn(LOGIN_BUTTON);
        Assertions.assertThat(robot.lookup(EMAIL_ADDRESS).queryAs(
            ExtendedTextField.class).getPromptText()).isEqualTo(
            LoginScreenController.EMPTY_EMAIL_ERROR);
        Assertions.assertThat(robot.lookup(ACCOUNTS_BOX)
            .queryAs(VBox.class).getChildren().size()).isEqualTo(0);
        Assertions.assertThat(Main.getLoggedInAccount()).isEqualTo(null);
    }

    /**
     * FUNCTIONAL 1+2+3+6 + NON-FUNCTIONAL 4
     * Tests if the login button works correctly with an invalid address.
     * @param robot Robot that uses the program
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws MessagingException Thrown if error with java mail
     * @throws MissingEmailServiceInboxName Thrown if email service error
     */
    @Test
    void onLoginButtonInvalidEmail(@NotNull FxRobot robot)
        throws HostConnectionFailureException, MessagingException,
        MissingEmailServiceInboxName {
        load(robot);
        setEmailAccount(GOOGLE, false);
        robot.interact(() -> {
            robot.lookup(EMAIL_ADDRESS).queryAs(ExtendedTextField.class)
                .setText("jordawfwufb,.3.3r31r");
            robot.lookup(PASSWORD_HIDDEN).queryAs(ExtendedPasswordField.class)
                .setText("pdjpzuoiibhnfntc");
            robot.lookup(ACCOUNT_TYPE).queryAs(ComboBox.class)
                .getSelectionModel().select(getEmailAccount()
                    .getEmailService().getName());
        });
        robot.clickOn(LOGIN_BUTTON);
        Assertions.assertThat(robot.lookup(ERROR_LABEL).queryAs(
            Label.class).getText()).isEqualTo(
            LoginScreenController.INVALID_EMAIL_ERROR);
        Assertions.assertThat(robot.lookup(ACCOUNTS_BOX)
            .queryAs(VBox.class).getChildren().size()).isEqualTo(0);
        Assertions.assertThat(Main.getLoggedInAccount()).isEqualTo(null);
    }

    /**
     * FUNCTIONAL 1+2+3+6 + NON-FUNCTIONAL 5
     * Tests if the login button works correctly with an empty password.
     * @param robot Robot that uses the program
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws MessagingException Thrown if error with java mail
     * @throws MissingEmailServiceInboxName Thrown if email service error
     */
    @Test
    void onLoginButtonEmptyPassword(@NotNull FxRobot robot)
        throws HostConnectionFailureException, MessagingException,
        MissingEmailServiceInboxName {
        load(robot);
        setEmailAccount(GOOGLE, false);
        robot.interact(() -> {
            robot.lookup(EMAIL_ADDRESS).queryAs(ExtendedTextField.class)
                .setText(getEmailAccount().getEmailAddress().getAddress());
            robot.lookup(ACCOUNT_TYPE).queryAs(ComboBox.class)
                .getSelectionModel().select(getEmailAccount()
                    .getEmailService().getName());
        });
        robot.clickOn(LOGIN_BUTTON);
        Assertions.assertThat(robot.lookup(PASSWORD_HIDDEN).queryAs(
            ExtendedPasswordField.class).getPromptText()).isEqualTo(
            LoginScreenController.EMPTY_PASSWORD_ERROR);
        Assertions.assertThat(robot.lookup(ACCOUNTS_BOX)
            .queryAs(VBox.class).getChildren().size()).isEqualTo(0);
        Assertions.assertThat(Main.getLoggedInAccount()).isEqualTo(null);
    }

    /**
     * FUNCTIONAL 1+2+3+6 + NON-FUNCTIONAL 6
     * Tests if the login button works correctly with an empty account type.
     * @param robot Robot that uses the program
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws MessagingException Thrown if error with java mail
     * @throws MissingEmailServiceInboxName Thrown if email service error
     */
    @Test
    void onLoginButtonEmptyAccountType(@NotNull FxRobot robot)
        throws HostConnectionFailureException, MessagingException,
        MissingEmailServiceInboxName {
        load(robot);
        setEmailAccount(GOOGLE, false);
        robot.interact(() -> {
            robot.lookup(EMAIL_ADDRESS).queryAs(ExtendedTextField.class)
                .setText(getEmailAccount().getEmailAddress().getAddress());
            robot.lookup(PASSWORD_HIDDEN).queryAs(ExtendedPasswordField.class)
                .setText(getEmailAccount().getPassword());
        });
        robot.clickOn(LOGIN_BUTTON);
        Assertions.assertThat(robot.lookup(ERROR_LABEL).queryAs(
            Label.class).getText()).isEqualTo(
            LoginScreenController.EMPTY_TYPE_ERROR);
        Assertions.assertThat(robot.lookup(ACCOUNTS_BOX)
            .queryAs(VBox.class).getChildren().size()).isEqualTo(0);
        Assertions.assertThat(Main.getLoggedInAccount()).isEqualTo(null);
    }

    /**
     * FUNCTIONAL 1+2+3+4+5+6 + NON-FUNCTIONAL 7
     * Tests if the login button works correctly with an empty smtp password.
     * @param robot Robot that uses the program
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws MessagingException Thrown if error with java mail
     * @throws MissingEmailServiceInboxName Thrown if email service error
     */
    @Test
    void onLoginButtonEmptySmtp(@NotNull FxRobot robot)
        throws HostConnectionFailureException, MessagingException,
        MissingEmailServiceInboxName {
        load(robot);
        setEmailAccount(GOOGLE, false);
        robot.interact(() -> {
            robot.lookup(EMAIL_ADDRESS).queryAs(ExtendedTextField.class)
                .setText(getEmailAccount().getEmailAddress().getAddress());
            robot.lookup(PASSWORD_HIDDEN).queryAs(ExtendedPasswordField.class)
                .setText(getEmailAccount().getPassword());
            robot.lookup(ACCOUNT_TYPE).queryAs(ComboBox.class)
                .getSelectionModel().select("Other");
            robot.lookup(IMAP_TEXT_FIELD).queryAs(ExtendedTextField.class)
                .setText(getEmailAccount().getEmailService().getImapHostName());
        });
        robot.clickOn(LOGIN_BUTTON);
        Assertions.assertThat(robot.lookup(SMTP_TEXT_FIELD).queryAs(
            ExtendedTextField.class).getPromptText()).isEqualTo(
            LoginScreenController.EMPTY_SMTP_ERROR);
        Assertions.assertThat(robot.lookup(ACCOUNTS_BOX)
            .queryAs(VBox.class).getChildren().size()).isEqualTo(0);
        Assertions.assertThat(Main.getLoggedInAccount()).isEqualTo(null);
    }

    /**
     * FUNCTIONAL 1+2+3+4+5+6 + NON-FUNCTIONAL 8
     * Tests if the login button works correctly with an empty smtp password.
     * @param robot Robot that uses the program
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws MessagingException Thrown if error with java mail
     * @throws MissingEmailServiceInboxName Thrown if email service error
     */
    @Test
    void onLoginButtonEmptyImap(@NotNull FxRobot robot)
        throws HostConnectionFailureException, MessagingException,
        MissingEmailServiceInboxName {
        load(robot);
        setEmailAccount(GOOGLE, false);
        robot.interact(() -> {
            robot.lookup(EMAIL_ADDRESS).queryAs(ExtendedTextField.class)
                .setText(getEmailAccount().getEmailAddress().getAddress());
            robot.lookup(PASSWORD_HIDDEN).queryAs(ExtendedPasswordField.class)
                .setText(getEmailAccount().getPassword());
            robot.lookup(ACCOUNT_TYPE).queryAs(ComboBox.class)
                .getSelectionModel().select("Other");
            robot.lookup(SMTP_TEXT_FIELD).queryAs(ExtendedTextField.class)
                .setText(getEmailAccount().getEmailService().getSmtpHostName());
        });
        robot.clickOn(LOGIN_BUTTON);
        Assertions.assertThat(robot.lookup(IMAP_TEXT_FIELD).queryAs(
            ExtendedTextField.class).getPromptText()).isEqualTo(
            LoginScreenController.EMPTY_IMAP_ERROR);
        Assertions.assertThat(robot.lookup(ACCOUNTS_BOX)
            .queryAs(VBox.class).getChildren().size()).isEqualTo(0);
        Assertions.assertThat(Main.getLoggedInAccount()).isEqualTo(null);
    }

    /**
     * FUNCTIONAL 1+2+3+6 + NON-FUNCTIONAL 9 (Email Address)
     * Tests if the login button works correctly for incorrect address.
     * @param robot Robot that uses the program
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws MessagingException Thrown if error with java mail
     * @throws MissingEmailServiceInboxName Thrown if email service error
     */
    @Test
    void onLoginButtonIncorrectAddress(@NotNull FxRobot robot)
        throws HostConnectionFailureException, MessagingException,
        MissingEmailServiceInboxName {
        load(robot);
        setEmailAccount(GOOGLE, false);
        robot.interact(() -> {
            robot.lookup(EMAIL_ADDRESS).queryAs(ExtendedTextField.class)
                .setText("jwdwdw@gmail.com");
            robot.lookup(PASSWORD_HIDDEN).queryAs(ExtendedPasswordField.class)
                .setText(GOOGLE.getPassword());
            robot.lookup(ACCOUNT_TYPE).queryAs(ComboBox.class)
                .getSelectionModel().select(getEmailAccount()
                    .getEmailService().getName());
        });
        robot.clickOn(LOGIN_BUTTON);
        Assertions.assertThat(robot.lookup(ERROR_LABEL).queryAs(
            Label.class).getText()).isEqualTo(EmailAccount.DETAILS_ERROR);
        Assertions.assertThat(robot.lookup(ACCOUNTS_BOX)
            .queryAs(VBox.class).getChildren().size()).isEqualTo(0);
        Assertions.assertThat(Main.getLoggedInAccount()).isEqualTo(null);
    }

    /**
     * FUNCTIONAL 1+2+3+6 + NON-FUNCTIONAL 9 (Password)
     * Tests if the login button works correctly for incorrect address.
     * @param robot Robot that uses the program
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws MessagingException Thrown if error with java mail
     * @throws MissingEmailServiceInboxName Thrown if email service error
     */
    @Test
    void onLoginButtonIncorrectPassword(@NotNull FxRobot robot)
        throws HostConnectionFailureException, MessagingException,
        MissingEmailServiceInboxName {
        load(robot);
        setEmailAccount(GOOGLE, false);
        robot.interact(() -> {
            robot.lookup(EMAIL_ADDRESS).queryAs(ExtendedTextField.class)
                .setText(GOOGLE.getEmailAddress().getAddress());
            robot.lookup(PASSWORD_HIDDEN).queryAs(ExtendedPasswordField.class)
                .setText("1234567812345678");
            robot.lookup(ACCOUNT_TYPE).queryAs(ComboBox.class)
                .getSelectionModel().select(getEmailAccount()
                    .getEmailService().getName());
        });
        robot.clickOn(LOGIN_BUTTON);
        Assertions.assertThat(robot.lookup(ERROR_LABEL).queryAs(
            Label.class).getText()).isEqualTo(EmailAccount.DETAILS_ERROR);
        Assertions.assertThat(robot.lookup(ACCOUNTS_BOX)
            .queryAs(VBox.class).getChildren().size()).isEqualTo(0);
        Assertions.assertThat(Main.getLoggedInAccount()).isEqualTo(null);
    }

    /**
     * FUNCTIONAL 1+2+3+6 + NON-FUNCTIONAL 10
     * Tests if the login button works correctly with an existing address.
     * @param robot Robot that uses the program
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws IOException Thrown if error with saving account
     * @throws MessagingException Thrown if error with java mail
     * @throws MissingEmailServiceInboxName Thrown if email service error
     */
    @Test
    void onLoginButtonAlreadyLoggedIn(@NotNull FxRobot robot)
        throws HostConnectionFailureException, IOException, MessagingException,
        MissingEmailServiceInboxName {
        GOOGLE.saveLogin();
        robot.interact(() -> robot.lookup(SCREEN_SPACE).queryAs(Pane.class)
            .getChildren().add(Main.LOGIN_SCREEN.load()));
        Assertions.assertThat(Objects.requireNonNull(new Directory(
            EmailAccount.ACCOUNTS_PATH).listFiles()).length).isEqualTo(1);
        Assertions.assertThat(Main.getLoggedInAccount()).isEqualTo(null);
        setEmailAccount(GOOGLE, false);
        loginValues(robot);
        robot.clickOn(LOGIN_BUTTON);
        Assertions.assertThat(Objects.requireNonNull(new Directory(
            EmailAccount.ACCOUNTS_PATH).listFiles()).length).isEqualTo(1);
        Assertions.assertThat(Main.getLoggedInAccount()).isEqualTo(null);
        Assertions.assertThat(robot.lookup(ERROR_LABEL).queryAs(Label.class)
            .getText()).isEqualTo(
            LoginScreenController.ALREADY_LOGGED_IN_ERROR);
    }

    /**
     * FUNCTIONAL 1+2+3+4+5+6 + NON-FUNCTIONAL 11
     * Tests if the login button works correctly for a host error.
     * @param robot Robot that uses the program
     */
    @Test
    void onLoginButtonHostError(@NotNull FxRobot robot) {
        load(robot);
        robot.interact(() -> {
            robot.lookup(EMAIL_ADDRESS).queryAs(ExtendedTextField.class)
                .setText("testforets@zohomail.eu");
            robot.lookup(PASSWORD_HIDDEN).queryAs(ExtendedPasswordField.class)
                .setText("6fH9sNqQPbqf");
            robot.lookup(ACCOUNT_TYPE).queryAs(ComboBox.class)
                .getSelectionModel().select("Other");
            robot.lookup(SMTP_TEXT_FIELD).queryAs(ExtendedTextField.class)
                .setText("fuhewjjodwfgeu");
            robot.lookup(IMAP_TEXT_FIELD).queryAs(ExtendedTextField.class)
                .setText("wdbufh9j0whg");
        });
        robot.clickOn(LOGIN_BUTTON);
        Assertions.assertThat(robot.lookup(ERROR_LABEL).queryAs(
            Label.class).getText()).isEqualTo(
            HostConnectionFailureException.MESSAGE
                + LoginScreenController.HOST_ERROR_EXCEPTION);
        Assertions.assertThat(robot.lookup(ACCOUNTS_BOX)
            .queryAs(VBox.class).getChildren().size()).isEqualTo(0);
        Assertions.assertThat(Main.getLoggedInAccount()).isEqualTo(null);
    }

    /**
     * Loads the login page.
     * @param robot Robot that uses the program
     */
    private void load(@NotNull FxRobot robot) {
        robot.interact(() -> robot.lookup(SCREEN_SPACE).queryAs(Pane.class)
            .getChildren().add(Main.LOGIN_SCREEN.load()));
        Assertions.assertThat(robot.lookup(ACCOUNTS_BOX)
            .queryAs(VBox.class).getChildren().size()).isEqualTo(0);
        Assertions.assertThat(Main.getLoggedInAccount()).isEqualTo(null);
    }

    /**
     * Runs the login button.
     * @param robot Robot that uses the program
     * @param emailAccount Email account to test
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws MessagingException Thrown if error with java mail
     * @throws MissingEmailServiceInboxName Thrown if email service error
     */
    private void loginButton(@NotNull FxRobot robot, EmailAccount emailAccount)
        throws HostConnectionFailureException, MessagingException,
        MissingEmailServiceInboxName {
        load(robot);
        setEmailAccount(emailAccount, false);
        loginValues(robot);
        checkBox(robot);
        robot.clickOn(LOGIN_BUTTON);
        Assertions.assertThat(robot.lookup(ACCOUNTS_BOX)
            .queryAs(VBox.class).getChildren().size()).isEqualTo(1);
        Assertions.assertThat(Main.getLoggedInAccount().getEmailAddress())
            .isEqualTo(getEmailAccount().getEmailAddress());
    }

    /**
     * Sets the login values.
     * @param robot Robot that uses the program
     */
    private void loginValues(@NotNull FxRobot robot) {
        robot.interact(() -> {
            robot.lookup(EMAIL_ADDRESS).queryAs(ExtendedTextField.class)
                .setText(getEmailAccount().getEmailAddress().getAddress());
            robot.lookup(PASSWORD_HIDDEN).queryAs(ExtendedPasswordField.class)
                .setText(getEmailAccount().getPassword());
            robot.lookup(ACCOUNT_TYPE).queryAs(ComboBox.class)
                .getSelectionModel().select(getEmailAccount()
                    .getEmailService().getName());
        });
    }

    /**
     * Clicks the checkbox.
     * @param robot Robot that uses the program
     */
    private void checkBox(@NotNull FxRobot robot) {
        ExtendedPasswordField hidden = robot.lookup(PASSWORD_HIDDEN)
            .queryAs(ExtendedPasswordField.class);
        ExtendedTextField visible = robot.lookup(PASSWORD_VISIBLE)
            .queryAs(ExtendedTextField.class);
        Assertions.assertThat(hidden.isVisible()).isEqualTo(true);
        Assertions.assertThat(visible.isVisible()).isEqualTo(false);
        robot.lookup(PASSWORD_CHECKBOX).queryAs(CheckBox.class)
            .setSelected(true);
        Assertions.assertThat(hidden.isVisible()).isEqualTo(false);
        Assertions.assertThat(visible.isVisible()).isEqualTo(true);
        robot.lookup(PASSWORD_CHECKBOX).queryAs(CheckBox.class)
            .setSelected(false);
        Assertions.assertThat(hidden.isVisible()).isEqualTo(true);
        Assertions.assertThat(visible.isVisible()).isEqualTo(false);
    }

}
