package screens.inbox;

import email.EmailInbox;
import gui.fxml.inbox.InboxScreenController;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import java.io.IOException;

/**
 * Test Suite for the Default Inbox Screen.
 * @author Jordan Jones
 */
public class DefaultInboxScreenTestSuite extends InboxScreenTestSuite {

    //CONSTANTS
    private static final String SPAM_BUTTON = "#spamButton";

    /**
     * Runs at the start of each test case (JavaFx Only).
     * @param stage Javafx Window
     * @throws IOException Thrown if error with saving the email account (INT)
     */
    @Start
    private void start(Stage stage) throws IOException {
        testStart(stage, EmailInbox.EmailInboxType.INBOX);
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
     * FUNCTIONAL 1-12+26-28 + NON-FUNCTIONAL 1-6
     * Tests if the spam button works correctly.
     * @param robot Robot that uses the program
     */
    @Test
    void onSpamButton(@NotNull FxRobot robot) {
        waitForEmails(robot);
        robot.clickOn(SPAM_BUTTON);
        Assertions.assertThat(robot.lookup(BUTTONS_ID).queryAs(VBox.class)
            .getChildren().size()).isEqualTo(2);
        Assertions.assertThat(robot.lookup(EMAIL_BOXES_ID).queryAs(VBox.class)
            .getChildren().size()).isEqualTo(
                InboxScreenController.EMAILS_PER_PAGE);
    }

}
