package screens.inbox;

import email.EmailInbox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;

import java.io.IOException;

/**
 * Test Suite for the Sent Inbox Screen.
 * @author Jordan Jones
 */
public class SentInboxScreenTestSuite extends InboxScreenTestSuite {

    /**
     * Runs at the start of each test case (JavaFx Only).
     * @param stage Javafx Window
     * @throws IOException Thrown if error with saving the email account (INT)
     */
    @Start
    private void start(Stage stage) throws IOException {
        testStart(stage, EmailInbox.EmailInboxType.SENT);
    }

    /**
     * Runs at the end of each test case (JavaFx Only).
     * @param robot Robot that uses the program
     */
    @AfterEach
    private void afterEach(@NotNull FxRobot robot) {
        cleanup(robot);
    }

}
