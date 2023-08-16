package screens;

import java.io.IOException;
import java.util.Objects;

import email.HostConnectionFailureException;
import email.address.EmailAddress;
import email.address.InvalidEmailAddressException;
import file.FileCanNotDeleteException;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.Start;
import email.EmailAccount;
import file.directory.Directory;
import file.text.TextFile;
import gui.Main;
import gui.components.ExtendedPasswordField;
import gui.components.ExtendedTextField;
import gui.screens.Screen;
import gui.screens.login.LoginScreenController;

import javax.mail.MessagingException;

/**
 * Test Suite for the Login Screen.
 * @author Jordan Jones
 */
class LoginScreenTestSuite extends ScreenTestSuite {

    //CONSTANTS
    private static final String EMAIL_ADDRESS_TEXT_FIELD_ID
        = "#emailAddressTextField";
    private static final String APPLICATION_PASSWORD_HIDDEN_TEXT_FIELD_ID
        = "#applicationPasswordHiddenTextField";
    private static final String APPLICATION_PASSWORD_VISIBLE_TEXT_FIELD_ID
        = "#applicationPasswordVisibleTextField";
    private static final String SHOW_PASSWORD_CHECK_BOX_ID
        = "#showPasswordCheckBox";
    private static final String REMEMBER_ME_CHECK_BOX_ID
        = "#rememberMeCheckBox";
    private static final String BACK_BUTTON_ID = "#backButton";
    private static final String LOGIN_BUTTON_ID = "#loginButton";
    private static final String ERROR_LABEL_ID = "#errorLabel";

    /**
     * Runs at the start of each test case (JavaFx Only).
     * @param stage Javafx Window
     * @throws FileCanNotDeleteException Thrown if a files can't be deleted
     * @throws IOException Thrown if error with the login screen fxml
     */
    @Start
    private void start(Stage stage) throws FileCanNotDeleteException,
        IOException {
        Screen.setPrimaryStage(stage, Main.ICON_FILE_NAME);
        new Directory(EmailAccount.ACCOUNTS_PATH).deleteFiles();
        Main.LOGIN_SCREEN.load();
    }

    /**
     * Tests if the back button works correctly.
     * @param robot Robot that uses the program
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws FileCanNotDeleteException Thrown if a files can't be deleted
     * @throws InvalidEmailAddressException Thrown if error with address format
     * @throws IOException Thrown if error with saving the email account (INT)
     * @throws MessagingException Thrown if error with java mail
     */
    @Test
    void onBackButton(@NotNull FxRobot robot)
        throws FileCanNotDeleteException, HostConnectionFailureException,
        InvalidEmailAddressException, IOException, MessagingException {
        Assertions.assertThat(robot.lookup(BACK_BUTTON_ID).queryAs(Button.class)
            .isVisible()).isEqualTo(false);
        setLoggedInAccount(true);
        robot.interact(() -> {
            try {
                Main.LOGIN_SCREEN.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        robot.clickOn(BACK_BUTTON_ID);
        Assertions.assertThat(Screen.getPrimaryStage().getTitle()).isEqualTo(
            Main.SELECT_SCREEN.getWindowTitle());
    }

    /**
     * Tests if the account details log in correctly if they are correct.
     * @param robot Robot that uses the program
     */
    @Test
    void onLoginButton(@NotNull FxRobot robot) {
        robot.lookup(EMAIL_ADDRESS_TEXT_FIELD_ID).queryAs(
            ExtendedTextField.class).setText("jordan.jones090101@gmail.com");
        robot.lookup(APPLICATION_PASSWORD_HIDDEN_TEXT_FIELD_ID).queryAs(
            ExtendedPasswordField.class).setText("uesovppmernvlucd");
        robot.clickOn(LOGIN_BUTTON_ID);
        Assertions.assertThat(Screen.getPrimaryStage().getTitle()).isEqualTo(
            Main.HOME_SCREEN.getWindowTitle());
    }

    /**
     * Tests if both inputs show the correct error message when empty.
     * @param robot Robot that uses the program
     */
    @Test
    void onLoginButtonBothInputsEmpty(@NotNull FxRobot robot) {
        robot.clickOn(LOGIN_BUTTON_ID);
        Assertions.assertThat(robot.lookup(EMAIL_ADDRESS_TEXT_FIELD_ID)
            .queryAs(ExtendedTextField.class).getPromptText()).isEqualTo(
            LoginScreenController.EMPTY_EMAIL_ERROR);
        Assertions.assertThat(robot.lookup(
            APPLICATION_PASSWORD_HIDDEN_TEXT_FIELD_ID)
            .queryAs(ExtendedPasswordField.class).getPromptText()).isEqualTo(
            LoginScreenController.EMPTY_AP_ERROR);
    }

    /**
     * Tests if the login input show the correct error message when empty.
     * @param robot Robot that uses the program
     */
    @Test
    void onLoginButtonEmailAddressEmpty(@NotNull FxRobot robot) {
        robot.lookup(APPLICATION_PASSWORD_HIDDEN_TEXT_FIELD_ID).queryAs(
            ExtendedPasswordField.class).setText("pdjpzuoiibhnfntc");
        robot.clickOn(LOGIN_BUTTON_ID);
        Assertions.assertThat(robot.lookup(EMAIL_ADDRESS_TEXT_FIELD_ID)
            .queryAs(ExtendedTextField.class).getPromptText()).isEqualTo(
                LoginScreenController.EMPTY_EMAIL_ERROR);
        Assertions.assertThat(robot.lookup(
            APPLICATION_PASSWORD_HIDDEN_TEXT_FIELD_ID)
            .queryAs(ExtendedPasswordField.class).getPromptText()).isNotEqualTo(
            LoginScreenController.EMPTY_AP_ERROR);
    }

    /**
     * Tests if the password input show the correct error message when empty.
     * @param robot Robot that uses the program
     */
    @Test
    void onLoginButtonApplicationPasswordEmpty(@NotNull FxRobot robot) {
        robot.lookup(EMAIL_ADDRESS_TEXT_FIELD_ID).queryAs(
            ExtendedTextField.class).setText(
            "jordan.joneswork090101@gmail.com");
        robot.clickOn(LOGIN_BUTTON_ID);
        Assertions.assertThat(robot.lookup(EMAIL_ADDRESS_TEXT_FIELD_ID)
            .queryAs(ExtendedTextField.class).getPromptText()).isNotEqualTo(
            LoginScreenController.EMPTY_EMAIL_ERROR);
        Assertions.assertThat(robot.lookup(
            APPLICATION_PASSWORD_HIDDEN_TEXT_FIELD_ID)
            .queryAs(ExtendedPasswordField.class).getPromptText()).isEqualTo(
            LoginScreenController.EMPTY_AP_ERROR);
    }

    /**
     * Tests if the correct error message appears when the details are invalid.
     * @param robot Robot that uses the program
     */
    @Test
    @Order(1)
    void onLoginButtonInvalidDetails(@NotNull FxRobot robot) {
        robot.lookup(EMAIL_ADDRESS_TEXT_FIELD_ID).queryAs(
            ExtendedTextField.class).setText(
                "jordan.joneswork090101@gmail.com");
        robot.lookup(APPLICATION_PASSWORD_HIDDEN_TEXT_FIELD_ID).queryAs(
            ExtendedPasswordField.class).setText("1234567812345678");
        robot.clickOn(LOGIN_BUTTON_ID);
        Assertions.assertThat(robot.lookup(ERROR_LABEL_ID).queryAs(Label.class))
            .hasText(EmailAccount.DETAILS_ERROR);
    }

    /**
     * Tests if the correct error message appears if the email is invalid.
     * @param robot Robot that uses the program
     */
    @Test
    void onLoginButtonInvalidEmailAddress(@NotNull FxRobot robot) {
        robot.lookup(EMAIL_ADDRESS_TEXT_FIELD_ID).queryAs(
            ExtendedTextField.class).setText("jordan.joneswork0ewewfgmail.com");
        robot.lookup(APPLICATION_PASSWORD_HIDDEN_TEXT_FIELD_ID).queryAs(
            ExtendedPasswordField.class).setText("pdjpzuoiibhnfntc");
        robot.clickOn(LOGIN_BUTTON_ID);
        Assertions.assertThat(robot.lookup(ERROR_LABEL_ID).queryAs(Label.class))
            .hasText(LoginScreenController.INVALID_EMAIL_ERROR);
    }

    /**
     * Tests if the login works after the show password check box is clicked.
     * @param robot Robot that uses the program
     */
    @Test
    void onShowPasswordCheckBoxTrue(@NotNull FxRobot robot) {
        robot.lookup(SHOW_PASSWORD_CHECK_BOX_ID).queryAs(CheckBox.class)
            .selectedProperty().set(true);
        robot.lookup(EMAIL_ADDRESS_TEXT_FIELD_ID).queryAs(
            ExtendedTextField.class).setText(
            "jordan.joneswork090101@gmail.com");
        robot.lookup(APPLICATION_PASSWORD_VISIBLE_TEXT_FIELD_ID).queryAs(
            ExtendedTextField.class).setText("pdjpzuoiibhnfntc");
        robot.clickOn(LOGIN_BUTTON_ID);
        Assertions.assertThat(robot.lookup(ERROR_LABEL_ID).queryAs(Label.class))
            .hasText("");
    }

    /**
     * Tests if the login works using the visible field without the box checked.
     * @param robot Robot that uses the program
     */
    @Test
    void onShowPasswordCheckBoxFalse(@NotNull FxRobot robot) {
        robot.lookup(SHOW_PASSWORD_CHECK_BOX_ID).queryAs(CheckBox.class)
            .selectedProperty().set(false);
        robot.lookup(EMAIL_ADDRESS_TEXT_FIELD_ID).queryAs(
            ExtendedTextField.class).setText(
            "jordan.joneswork090101@gmail.com");
        robot.lookup(APPLICATION_PASSWORD_VISIBLE_TEXT_FIELD_ID).queryAs(
            ExtendedTextField.class).setText("pdjpzuoiibhnfntc");
        robot.clickOn(LOGIN_BUTTON_ID);
        Assertions.assertThat(robot.lookup(ERROR_LABEL_ID).queryAs(Label.class))
            .hasText("");
    }

    /**
     * On remember me check box true for new account details.
     * @param robot Robot that uses the program
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws InvalidEmailAddressException Thrown if error with address format
     * @throws IOException Thrown if error with saving the email account (INT)
     * @throws MessagingException Thrown if error with java mail
     */
    @Test
    void onRememberMeCheckBoxTrueNew(@NotNull FxRobot robot)
        throws HostConnectionFailureException, MessagingException,
        InvalidEmailAddressException, IOException {
        Assertions.assertThat(new Directory(String.format(
            EmailAccount.ACCOUNT_PATH, 1)).exists()).isEqualTo(false);
        new EmailAccount(0, new EmailAddress(
            "jordan.joneswork090101@gmail.com"), "pdjpzuoiibhnfntc",
            true).saveLogin();
        new EmailAccount(2, new EmailAddress(
            "jordan.joneswork090101@gmail.com"), "pdjpzuoiibhnfntc",
            true).saveLogin();
        robot.lookup(REMEMBER_ME_CHECK_BOX_ID).queryAs(CheckBox.class)
            .selectedProperty().set(true);
        onLoginButton(robot);
        Assertions.assertThat(new Directory(String.format(
            EmailAccount.ACCOUNT_PATH, 1)).exists()).isEqualTo(true);
    }

    /**
     * On remember me check box true for duplicate account details.
     * @param robot Robot that uses the program
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws InvalidEmailAddressException Thrown if error with address format
     * @throws IOException Thrown if error with saving the email account (INT)
     * @throws MessagingException Thrown if error with java mail
     */
    @Test
    void onRememberMeCheckBoxTrueDuplicate(@NotNull FxRobot robot)
        throws HostConnectionFailureException, InvalidEmailAddressException,
        MessagingException, IOException {
        new EmailAccount(0, new EmailAddress("jordan.jones090101@gmail.com"),
            "uesovppmernvlucd", true).saveLogin();
        robot.lookup(REMEMBER_ME_CHECK_BOX_ID).queryAs(CheckBox.class)
            .selectedProperty().set(true);
        onLoginButton(robot);
        Assertions.assertThat(Objects.requireNonNull(new Directory(
            EmailAccount.ACCOUNTS_PATH).listFiles()).length).isEqualTo(1);
    }

    /**
     * On remember me check box false.
     * @param robot Robot that uses the program
     */
    @Test
    void onRememberMeCheckBoxFalse(@NotNull FxRobot robot) {
        robot.lookup(REMEMBER_ME_CHECK_BOX_ID).queryAs(CheckBox.class)
            .selectedProperty().set(false);
        onLoginButton(robot);
        Assertions.assertThat(new TextFile(Main.getLoggedInAccount()
            .getAccountPath()).exists()).isEqualTo(false);
    }

}
