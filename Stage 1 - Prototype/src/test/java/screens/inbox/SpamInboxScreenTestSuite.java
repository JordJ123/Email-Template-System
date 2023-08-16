package screens.inbox;

import java.io.IOException;

import file.FileCanNotDeleteException;
import gui.screens.inbox.InboxScreenController;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.mail.MessagingException;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import email.EmailInbox;
import email.HostConnectionFailureException;
import email.address.InvalidEmailAddressException;

/**
 * Test Suite for the Spam Inbox Screen.
 * @author Jordan Jones
 */
public class SpamInboxScreenTestSuite extends InboxScreenTestSuite {

    //CONSTANTS
    private static final String NOT_SPAM_BUTTON = "#notSpamButton";

    /**
     * Runs at the start of each test case (JavaFx Only).
     * @param stage Javafx Window
     * @throws ClassNotFoundException Thrown if error loading email inbox
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws FileCanNotDeleteException Thrown if a files can't be deleted
     * @throws InvalidEmailAddressException Thrown if error with address format
     * @throws IOException Thrown if error with saving the email account (INT)
     * @throws MessagingException Thrown if error with java mail
     */
    @Start
    private void start(Stage stage) throws ClassNotFoundException,
        FileCanNotDeleteException, HostConnectionFailureException,
        InvalidEmailAddressException, IOException, MessagingException {
        testStart(stage, EmailInbox.EmailInboxType.SPAM);
    }

    /**
     * Tests if the edit button works correctly.
     * @param robot Robot that uses the program
     */
    @Test
    void onNotSpamButton(@NotNull FxRobot robot) {
        waitForEmails(robot);
        Assertions.assertThat(robot.lookup(BUTTONS_ID).queryAs(VBox.class)
            .getChildren().size()).isEqualTo(3);
        robot.clickOn(NOT_SPAM_BUTTON);
        Assertions.assertThat(robot.lookup(EMAIL_BOXES_ID).queryAs(VBox.class)
            .getChildren().size()).isEqualTo(
            InboxScreenController.EMAILS_PER_PAGE);
    }

}
