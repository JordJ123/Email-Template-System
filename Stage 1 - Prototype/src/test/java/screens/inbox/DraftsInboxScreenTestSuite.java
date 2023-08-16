package screens.inbox;

import java.io.IOException;
import file.FileCanNotDeleteException;
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
import gui.Main;
import gui.screens.Screen;

/**
 * Test Suite for the Drafts Inbox Screen.
 * @author Jordan Jones
 */
public class DraftsInboxScreenTestSuite extends InboxScreenTestSuite {

    //CONSTANTS
    private static final String EDIT_BUTTON = "#editButton";

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
        testStart(stage, EmailInbox.EmailInboxType.DRAFTS);
    }

    /**
     * Tests if the edit button works correctly.
     * @param robot Robot that uses the program
     */
    @Test
    void onEditButton(@NotNull FxRobot robot) {
        waitForEmails(robot);
        Assertions.assertThat(robot.lookup(BUTTONS_ID).queryAs(VBox.class)
            .getChildren().size()).isEqualTo(3);
        robot.clickOn(EDIT_BUTTON);
        Assertions.assertThat(Screen.getPrimaryStage().getTitle()).isEqualTo(
            Main.CREATE_SCREEN.getWindowTitle());
    }

}
