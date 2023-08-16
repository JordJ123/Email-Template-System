package screens;

import email.EmailAccount;
import email.EmailInbox;
import email.HostConnectionFailureException;
import email.service.MissingEmailServiceInboxName;
import file.FileCanNotDeleteException;
import file.directory.Directory;
import file.html.HTMLVoidElement;
import file.text.TextFile;
import gui.Main;
import gui.components.ExtendedTextField;
import gui.fxml.base.AccountBoxController;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * Test Suite for the Base Screen.
 * @author Jordan Jones
 */
public class BaseScreenTestSuite extends ScreenTestSuite {

    //CONSTANTS
    private static final String OLD_NICKNAME = "Email";
    private static final String NEW_NICKNAME = "Steve";

    //COMPONENTS
    private static final String ACCOUNTS_BOX = "#accountsBox";
    private static final String ACCOUNT_BOX = "#accountBox";
    private static final String NICKNAME_LABEL = "#nicknameLabel";
    private static final String EMAIL_ADDRESS_LABEL = "#emailAddressLabel";
    private static final String NICKNAME_ITEM = "#nicknameItem";
    private static final String NICKNAME_BOX = "#nicknameBox";
    private static final String NICKNAME_TEXT_FIELD = "#nicknameTextField";
    private static final String NICKNAME_ACCEPT_BUTTON
        = "#nicknameAcceptButton";
    private static final String NICKNAME_CANCEL_BUTTON
        = "#nicknameCancelButton";
    private static final String LOGOUT_ITEM = "#logoutItem";
    private static final String LOGIN_BUTTON = "#loginScreenButton";
    private static final String OPTIONS_BOX = "#optionsBox";
    private static final String CREATE_BUTTON = "#createButton";
    private static final String DEFAULT_BUTTON = "#defaultInboxButton";
    private static final String DRAFTS_BUTTON = "#draftsInboxButton";
    private static final String SENT_BUTTON = "#sentInboxButton";
    private static final String SPAM_BUTTON = "#spamInboxButton";
    private static final String BIN_BUTTON = "#binInboxButton";
    private static final String SCREEN_SPACE = "#screenSpace";
    private static final String LOGIN_SCREEN = "#loginScreen";
    private static final String CREATE_SCREEN = "#createScreen";
    private static final String INBOX_SCREEN = "#inboxScreen";

    /**
     * Runs at the start of each test case (JavaFx Only).
     * @param stage Javafx Window
     * @throws FileCanNotDeleteException Thrown if accounts can't be deleted
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws IOException Thrown if error loading screen
     * @throws MessagingException Thrown if error with java mail
     * @throws MissingEmailServiceInboxName Thrown if missing an inbox name
     */
    @Start
    private void start(Stage stage) throws FileCanNotDeleteException,
        HostConnectionFailureException, IOException, MessagingException,
        MissingEmailServiceInboxName {
        setup(stage, GOOGLE, true, false);
    }

    /**
     * Runs after every test.
     * @param robot Robot that uses the program
     */
    @AfterEach
    private void afterEach(@NotNull FxRobot robot) {
        cleanup(robot);
    }

    //Functional 1
    //Run Base main method and check if it is the base screen

    //Functional 2 + NF1+2+3+4
    /**
     * Tests if the account box button works correctly.
     * @param robot Robot that uses the program
     */
    @Test
    void onAccountBoxButton(@NotNull FxRobot robot) {

        //Before Click
        Assertions.assertThat(Main.getLoggedInAccount()).isEqualTo(null);
        Assertions.assertThat(robot.lookup(NICKNAME_LABEL).queryAs(Label.class)
            .getStyle()).isEqualTo("");
        Assertions.assertThat(robot.lookup(EMAIL_ADDRESS_LABEL)
            .queryAs(Label.class).getStyle()).isEqualTo("");
        Assertions.assertThat(robot.lookup(OPTIONS_BOX).queryAs(VBox.class)
            .isDisable()).isEqualTo(true);

        //Click
        robot.clickOn(LOGIN_BUTTON);
        robot.clickOn(ACCOUNT_BOX);

        //After Click
        Assertions.assertThat(Main.getLoggedInAccount().getEmailAddress())
            .isEqualTo(getEmailAccount().getEmailAddress());
        Assertions.assertThat(robot.lookup(NICKNAME_LABEL).queryAs(Label.class)
            .getStyle()).isEqualTo(HTMLVoidElement.BOLD.getJavaFxStyle());
        Assertions.assertThat(robot.lookup(EMAIL_ADDRESS_LABEL)
            .queryAs(Label.class).getStyle()).isEqualTo(
                HTMLVoidElement.BOLD.getJavaFxStyle());
        robot.sleep(WAIT_TIME);
        Assertions.assertThat(robot.lookup(OPTIONS_BOX).queryAs(VBox.class)
            .isDisable()).isEqualTo(false);
        Assertions.assertThat(robot.lookup(SCREEN_SPACE).queryAs(Pane.class)
            .getChildren().size()).isEqualTo(0);

    }

    //Functional 3+4 + NF5+6
    /**
     * Tests if the nickname accept button works correctly.
     * @param robot Robot that uses the program
     * @throws FileNotFoundException Thrown if the text file can't be found
     */
    @Test
    void onNicknameAcceptButton(@NotNull FxRobot robot)
        throws FileNotFoundException {

        //Check
        Assertions.assertThat(robot.lookup(NICKNAME_LABEL).queryAs(Label.class)
            .getText()).isEqualTo(OLD_NICKNAME);
        Scanner scanner = new TextFile(String.format(
            EmailAccount.LOGIN_PATH, 0)).getContents();
        scanner.next();
        Assertions.assertThat(scanner.next()).isEqualTo("'null'");
        scanner.close();

        //Click
        robot.rightClickOn(ACCOUNT_BOX);
        robot.clickOn(NICKNAME_ITEM);
        robot.lookup(NICKNAME_TEXT_FIELD).queryAs(ExtendedTextField.class)
            .setText(NEW_NICKNAME);
        robot.clickOn(NICKNAME_ACCEPT_BUTTON);

        //Check
        Assertions.assertThat(robot.lookup(NICKNAME_LABEL).queryAs(Label.class)
            .getText()).isEqualTo(NEW_NICKNAME);
        Scanner scannerTwo = new TextFile(String.format(
            EmailAccount.LOGIN_PATH, 0)).getContents();
        scannerTwo.next();
        Assertions.assertThat(scannerTwo.next()).isEqualTo("'" + NEW_NICKNAME
            + "'");
        scannerTwo.close();
        Assertions.assertThat(robot.lookup(NICKNAME_BOX)
            .queryAs(HBox.class).isVisible()).isEqualTo(false);
    }
    /**
     * Tests if the nickname accept button empty error appears correctly.
     * @param robot Robot that uses the program
     */
    @Test
    void onNicknameAcceptButtonEmptyError(@NotNull FxRobot robot) {
        robot.rightClickOn(ACCOUNT_BOX);
        robot.clickOn(NICKNAME_ITEM);
        ExtendedTextField nicknameTextField = robot.lookup(NICKNAME_TEXT_FIELD)
            .queryAs(ExtendedTextField.class);
        nicknameTextField.setText("");
        robot.clickOn(NICKNAME_ACCEPT_BUTTON);
        Assertions.assertThat(nicknameTextField.getPromptText())
            .isEqualTo(AccountBoxController.NICKNAME_ERROR);
    }

    //Functional 3+5
    /**
     * Tests if the nickname cancel button.
     * @param robot Robot that uses the program
     */
    @Test
    void onNicknameCancelButton(@NotNull FxRobot robot) {
        robot.rightClickOn(ACCOUNT_BOX);
        robot.clickOn(NICKNAME_ITEM);
        robot.clickOn(NICKNAME_CANCEL_BUTTON);
        Assertions.assertThat(robot.lookup(NICKNAME_BOX)
            .queryAs(HBox.class).isVisible()).isEqualTo(false);
    }

    //Functional 3+6
    /**
     * Tests if the account box logout context menu action works correctly.
     * @param robot Robot that uses the program
     */
    @Test
    void onLogoutContextMenuAction(@NotNull FxRobot robot) {

        //Pre-Check
        Assertions.assertThat(Main.getLoggedInAccount()).isEqualTo(null);
        Assertions.assertThat(robot.lookup(ACCOUNTS_BOX).queryAs(VBox.class)
            .getChildren().size()).isEqualTo(1);
        Assertions.assertThat(new Directory(String.format(
            EmailAccount.ACCOUNT_PATH, 0)).exists()).isEqualTo(true);

        //Click
        robot.clickOn(ACCOUNT_BOX);
        Assertions.assertThat(Main.getLoggedInAccount().getEmailAddress())
            .isEqualTo(getEmailAccount().getEmailAddress());
        robot.rightClickOn(ACCOUNT_BOX);
        robot.clickOn(LOGOUT_ITEM);

        //Post-Check
        Assertions.assertThat(Main.getLoggedInAccount()).isEqualTo(null);
        Assertions.assertThat(robot.lookup(ACCOUNTS_BOX).queryAs(VBox.class)
            .getChildren().size()).isEqualTo(0);
        Assertions.assertThat(new Directory(String.format(
            EmailAccount.ACCOUNT_PATH, 0)).exists()).isEqualTo(false);

    }

    //Functional 7+8 + NF7+8
    /**
     * Tests if the login button work correctly.
     * @param robot Robot that uses the program
     */
    @Test
    void onLoginButtonAccount(@NotNull FxRobot robot) {
        Assertions.assertThat(Main.getLoggedInAccount()).isEqualTo(null);
        robot.clickOn(ACCOUNT_BOX);
        Assertions.assertThat(Main.getLoggedInAccount().getEmailAddress())
            .isEqualTo(getEmailAccount().getEmailAddress());
        robot.clickOn(LOGIN_BUTTON);
        robot.lookup(LOGIN_SCREEN);
        Assertions.assertThat(Main.getLoggedInAccount()).isEqualTo(null);
    }
    /**
     * Tests if the login button work correctly.
     * @param robot Robot that uses the program
     * @throws FileCanNotDeleteException Thrown if accounts can't be deleted
     */
    @Test
    void onLoginButtonNoAccount(@NotNull FxRobot robot)
        throws FileCanNotDeleteException {
        new Directory(EmailAccount.ACCOUNTS_PATH).deleteFiles();
        Assertions.assertThat(Main.getLoggedInAccount()).isEqualTo(null);
        robot.clickOn(LOGIN_BUTTON);
        robot.lookup(LOGIN_SCREEN);
        Assertions.assertThat(Main.getLoggedInAccount()).isEqualTo(null);
    }

    //Functional 7+9+10 + NF7+8
    /**
     * Tests if the create button works correctly.
     * @param robot Robot that uses the program
     */
    @Test
    void onCreateButton(@NotNull FxRobot robot) {
        selectAccountBox(robot);
        robot.clickOn(CREATE_BUTTON);
        robot.lookup(CREATE_SCREEN);
    }

    //Functional 7+9+11 + NF7+8
    /**
     * Tests if the default inbox button works correctly.
     * @param robot Robot that uses the program
     */
    @Test
    void onDefaultInboxButton(@NotNull FxRobot robot) {
        selectAccountBox(robot);
        robot.clickOn(DEFAULT_BUTTON);
        robot.lookup(INBOX_SCREEN);
        robot.sleep(WAIT_TIME);
        Assertions.assertThat(Main.INBOX_SCREEN.getController().getEmailInbox()
            .getEmailInboxType()).isEqualTo(EmailInbox.EmailInboxType.INBOX);
    }

    //Functional 7+9+12 + NF7+8
    /**
     * Tests if the drafts inbox button works correctly.
     * @param robot Robot that uses the program
     */
    @Test
    void onDraftsInboxButton(@NotNull FxRobot robot) {
        selectAccountBox(robot);
        robot.clickOn(DRAFTS_BUTTON);
        robot.lookup(INBOX_SCREEN);
        robot.sleep(WAIT_TIME);
        Assertions.assertThat(Main.INBOX_SCREEN.getController().getEmailInbox()
            .getEmailInboxType()).isEqualTo(EmailInbox.EmailInboxType.DRAFTS);
    }

    //Functional 7+9+13 + NF7+8
    /**
     * Tests if the sent inbox button works correctly.
     * @param robot Robot that uses the program
     */
    @Test
    void onSentInboxButton(@NotNull FxRobot robot) {
        selectAccountBox(robot);
        robot.clickOn(SENT_BUTTON);
        robot.lookup(INBOX_SCREEN);
        robot.sleep(WAIT_TIME);
        Assertions.assertThat(Main.INBOX_SCREEN.getController().getEmailInbox()
            .getEmailInboxType()).isEqualTo(EmailInbox.EmailInboxType.SENT);
    }

    //Functional 7+9+14 + NF7+8
    /**
     * Tests if the spam inbox button works correctly.
     * @param robot Robot that uses the program
     */
    @Test
    void onSpamInboxButton(@NotNull FxRobot robot) {
        selectAccountBox(robot);
        robot.clickOn(SPAM_BUTTON);
        robot.lookup(INBOX_SCREEN);
        robot.sleep(WAIT_TIME);
        Assertions.assertThat(Main.INBOX_SCREEN.getController().getEmailInbox()
            .getEmailInboxType()).isEqualTo(EmailInbox.EmailInboxType.SPAM);
    }

    //Functional 7+9+15 + NF7+8
    /**
     * Tests if the bin inbox button works correctly.
     * @param robot Robot that uses the program
     */
    @Test
    void onBinInboxButton(@NotNull FxRobot robot) {
        selectAccountBox(robot);
        robot.clickOn(BIN_BUTTON);
        robot.lookup(INBOX_SCREEN);
        robot.sleep(WAIT_TIME);
        Assertions.assertThat(Main.INBOX_SCREEN.getController().getEmailInbox()
            .getEmailInboxType()).isEqualTo(EmailInbox.EmailInboxType.BIN);
    }

    /**
     * Adds and selects an account box.
     * @param robot Robot that uses the program
     */
    private void selectAccountBox(@NotNull FxRobot robot) {
        Assertions.assertThat(Main.getLoggedInAccount()).isEqualTo(null);
        Assertions.assertThat(robot.lookup(NICKNAME_LABEL).queryAs(Label.class)
            .getStyle()).isEqualTo("");
        Assertions.assertThat(robot.lookup(EMAIL_ADDRESS_LABEL)
            .queryAs(Label.class).getStyle()).isEqualTo("");
        Assertions.assertThat(robot.lookup(OPTIONS_BOX).queryAs(VBox.class)
            .isDisable()).isEqualTo(true);
        robot.clickOn(ACCOUNT_BOX);
    }

}
