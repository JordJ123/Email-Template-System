package screens;

import java.io.IOException;

import file.FileCanNotDeleteException;
import javafx.stage.Stage;
import javax.mail.MessagingException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.Start;
import email.EmailInbox;
import email.HostConnectionFailureException;
import email.address.InvalidEmailAddressException;
import gui.Main;
import gui.screens.Screen;

/**
 * Test Suite for the Home Screen.
 * @author Jordan Jones
 */
class HomeScreenTestSuite extends ScreenTestSuite {

    //CONSTANTS
    private static final String CREATE_BUTTON_ID = "#createButton";
    private static final String INBOX_BUTTON_ID = "#inboxButton";
    private static final String DRAFTS_BUTTON_ID = "#draftsButton";
    private static final String SENT_BUTTON_ID = "#sentButton";
    private static final String SPAM_BUTTON_ID = "#spamButton";
    private static final String BIN_BUTTON_ID = "#binButton";
    private static final String SWITCH_BUTTON_ID = "#switchButton";

    /**
     * Runs at the start of each test case (JavaFx Only).
     * @param stage Javafx Window
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws FileCanNotDeleteException Thrown if a files can't be deleted
     * @throws InvalidEmailAddressException Thrown if error with address format
     * @throws IOException Thrown if error with saving the email account (INT)
     * @throws MessagingException Thrown if error with java mail
     */
    @Start
    private void start(Stage stage) throws FileCanNotDeleteException,
        HostConnectionFailureException, InvalidEmailAddressException,
        IOException, MessagingException {
        Screen.setPrimaryStage(stage, Main.ICON_FILE_NAME);
        setLoggedInAccount(false);
        Main.HOME_SCREEN.load();
    }

    /**
     * Tests if the create button goes to the correct screen.
     * @param robot Robot that uses the program
     */
    @Test
    void onCreateButton(@NotNull FxRobot robot) {
        robot.clickOn(CREATE_BUTTON_ID);
        Assertions.assertThat(Screen.getPrimaryStage().getTitle()).isEqualTo(
            Main.CREATE_SCREEN.getWindowTitle());
    }

    /**
     * Tests if the inbox button goes to the correct screen.
     * @param robot Robot that uses the program
     */
    @Test
    void onInboxButton(@NotNull FxRobot robot) {
        robot.clickOn(INBOX_BUTTON_ID);
        Assertions.assertThat(Main.INBOX_SCREEN.getController()
            .getEmailInbox().getEmailInboxType()).isEqualTo(
            EmailInbox.EmailInboxType.INBOX);
    }

    /**
     * Tests if the drafts button goes to the correct screen.
     * @param robot Robot that uses the program
     */
    @Test
    void onDraftsButton(@NotNull FxRobot robot) {
        robot.clickOn(DRAFTS_BUTTON_ID);
        Assertions.assertThat(Main.INBOX_SCREEN.getController()
            .getEmailInbox().getEmailInboxType()).isEqualTo(
            EmailInbox.EmailInboxType.DRAFTS);
    }

    /**
     * Tests if the sent button goes to the correct screen.
     * @param robot Robot that uses the program
     */
    @Test
    void onSentButton(@NotNull FxRobot robot) {
        robot.clickOn(SENT_BUTTON_ID);
        Assertions.assertThat(Main.INBOX_SCREEN.getController()
            .getEmailInbox().getEmailInboxType()).isEqualTo(
            EmailInbox.EmailInboxType.SENT);
    }

    /**
     * Tests if the spam button goes to the correct screen.
     * @param robot Robot that uses the program
     */
    @Test
    void onSpamButton(@NotNull FxRobot robot) {
        robot.clickOn(SPAM_BUTTON_ID);
        Assertions.assertThat(Main.INBOX_SCREEN.getController()
            .getEmailInbox().getEmailInboxType()).isEqualTo(
            EmailInbox.EmailInboxType.SPAM);
    }

    /**
     * Tests if the bin button goes to the correct screen.
     * @param robot Robot that uses the program
     */
    @Test
    void onBinButton(@NotNull FxRobot robot) {
        robot.clickOn(BIN_BUTTON_ID);
        Assertions.assertThat(Main.INBOX_SCREEN.getController()
            .getEmailInbox().getEmailInboxType()).isEqualTo(
            EmailInbox.EmailInboxType.BIN);
    }

    /**
     * Tests if the create button goes to the correct screen.
     * @param robot Robot that uses the program
     */
    @Test
    void onSwitchButton(@NotNull FxRobot robot) {
        robot.clickOn(SWITCH_BUTTON_ID);
        Assertions.assertThat(Screen.getPrimaryStage().getTitle()).isEqualTo(
            Main.SELECT_SCREEN.getWindowTitle());
    }

}
