package screens.tags;

import email.HostConnectionFailureException;
import email.address.InvalidEmailAddressException;
import file.FileCanNotDeleteException;
import gui.Main;
import gui.screens.Screen;
import javafx.stage.Stage;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;

import javax.mail.MessagingException;
import java.io.IOException;

/**
 * Test Suite for the Tag Screen with a new email.
 * @author Jordan Jones
 */
public class TagsScreenNewEmailTestSuite extends TagsScreenTestSuite {

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
        setEmail(true);
        Screen.setPrimaryStage(stage, Main.ICON_FILE_NAME);
        Main.TAGS_SCREEN.load();
        Main.TAGS_SCREEN.getController().setController(getEmail());
        Screen.getPrimaryStage().sizeToScene();
    }

    /**
     * Tests if the send button works correctly.
     * @param robot Robot that uses the program
     */
    @Test
    void onSendButton(@NotNull FxRobot robot) {
        sendButton(robot);
        Assertions.assertThat(Screen.getPrimaryStage().getTitle()).isEqualTo(
            Main.HOME_SCREEN.getWindowTitle());
    }

}
