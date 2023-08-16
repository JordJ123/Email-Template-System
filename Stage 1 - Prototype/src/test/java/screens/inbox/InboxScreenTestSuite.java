package screens.inbox;

import email.EmailAccount;
import email.EmailInbox;
import email.HostConnectionFailureException;
import email.address.InvalidEmailAddressException;
import file.FileCanNotDeleteException;
import file.text.TextFileNotFoundException;
import gui.screens.inbox.InboxScreenController;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import gui.Main;
import gui.screens.Screen;
import screens.ScreenTestSuite;

import javax.mail.MessagingException;
import java.io.IOException;

/**
 * Test Suite for the Inbox Screen.
 * @author Jordan Jones
 */
public class InboxScreenTestSuite extends ScreenTestSuite {

    //IDS
    protected static final String EMAIL_BOXES_ID = "#emailBoxes";
    protected static final String BUTTONS_ID = "#buttons";
    private static final String VIEW_BUTTON = "#viewButton";
    private static final String DELETE_BUTTON = "#deleteButton";
    private static final String LEFT_BUTTON_ID = "#leftButton";
    private static final String RIGHT_BUTTON_ID = "#rightButton";
    private static final String PAGE_BUTTONS_ID = "#pageButtons";
    private static final String CURRENT_PAGE_ID = "#pageNumber";
    private static final String RETURN_BUTTON_ID = "#returnButton";

    //CONSTANTS
    private static final int WAIT_FOR_EMAILS_TIME = 1000;

    /**
     * Tests if the left arrow button works correctly.
     * @param robot Robot that uses the program
     */
    @Test
    void onRightLeftButtons(@NotNull FxRobot robot) {

        //Right Button
        waitForEmails(robot);
        Assertions.assertThat(robot.lookup(LEFT_BUTTON_ID).queryAs(Button.class)
            .isVisible()).isEqualTo(false);
        robot.clickOn(RIGHT_BUTTON_ID);
        Assertions.assertThat(robot.lookup(CURRENT_PAGE_ID).queryAs(Label.class)
            .getText()).isEqualTo("2");
        Assertions.assertThat(robot.lookup(RIGHT_BUTTON_ID)
            .queryAs(Button.class).isVisible()).isEqualTo(true);
        Assertions.assertThat(robot.lookup(LEFT_BUTTON_ID).queryAs(Button.class)
            .isVisible()).isEqualTo(true);
        robot.clickOn(RIGHT_BUTTON_ID);
        Assertions.assertThat(robot.lookup(CURRENT_PAGE_ID).queryAs(Label.class)
            .getText()).isEqualTo("3");

        //Left Button
        Assertions.assertThat(robot.lookup(RIGHT_BUTTON_ID)
            .queryAs(Button.class).isVisible()).isEqualTo(false);
        robot.clickOn(LEFT_BUTTON_ID);
        Assertions.assertThat(robot.lookup(CURRENT_PAGE_ID).queryAs(Label.class)
            .getText()).isEqualTo("2");
        Assertions.assertThat(robot.lookup(RIGHT_BUTTON_ID)
            .queryAs(Button.class).isVisible()).isEqualTo(true);
        Assertions.assertThat(robot.lookup(LEFT_BUTTON_ID).queryAs(Button.class)
            .isVisible()).isEqualTo(true);
        robot.clickOn(LEFT_BUTTON_ID);
        Assertions.assertThat(robot.lookup(CURRENT_PAGE_ID).queryAs(Label.class)
            .getText()).isEqualTo("1");

        //Final
        Assertions.assertThat(robot.lookup(RIGHT_BUTTON_ID)
            .queryAs(Button.class).isVisible()).isEqualTo(true);
        Assertions.assertThat(robot.lookup(LEFT_BUTTON_ID).queryAs(Button.class)
            .isVisible()).isEqualTo(false);

    }

    /**
     * Tests if the page buttons work correctly.
     * @param robot Robot that uses the program
     */
    @Test
    void onPageButtons(@NotNull FxRobot robot) {
        waitForEmails(robot);
        robot.interact(() -> ((Button) robot.lookup(PAGE_BUTTONS_ID)
            .queryAs(HBox.class).getChildren().get(2)).fire());
        Assertions.assertThat(robot.lookup(CURRENT_PAGE_ID).queryAs(Label.class)
            .getText()).isEqualTo("3");
    }

    /**
     * Tests if the return button works correctly.
     * @param robot Robot that uses the program
     */
    @Test
    void onReturnButton(@NotNull FxRobot robot) {
        robot.clickOn(RETURN_BUTTON_ID);
        Assertions.assertThat(Screen.getPrimaryStage().getTitle()).isEqualTo(
            Main.HOME_SCREEN.getWindowTitle());
    }

    /**
     * Tests if the view button works.
     * @param robot Robot that uses the program
     */
    @Test
    void onViewButton(@NotNull FxRobot robot) {
        waitForEmails(robot);
        robot.clickOn(VIEW_BUTTON);
        Assertions.assertThat(Screen.getPrimaryStage().getTitle()).isEqualTo(
            Main.VIEW_SCREEN.getWindowTitle());
    }

    /**
     * Tests if the delete button works correctly.
     * @param robot Robot that uses the program
     */
    @Test
    void onDeleteButton(@NotNull FxRobot robot) {
        waitForEmails(robot);
        robot.clickOn(DELETE_BUTTON);
        Assertions.assertThat(robot.lookup(EMAIL_BOXES_ID).queryAs(VBox.class)
            .getChildren().size()).isEqualTo(
            InboxScreenController.EMAILS_PER_PAGE);
    }

    /**
     * Codes to run at the start of each test.
     * @param stage Javafx Window
     * @param emailInboxType Type of the inbox to be opened
     * @throws ClassNotFoundException Thrown if error loading email inbox
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws FileCanNotDeleteException Thrown if a files can't be deleted
     * @throws InvalidEmailAddressException Thrown if error with address format
     * @throws IOException Thrown if error with saving the email account (INT)
     * @throws MessagingException Thrown if error with java mail
     */
    protected void testStart(Stage stage,
        EmailInbox.EmailInboxType emailInboxType) throws ClassNotFoundException,
        FileCanNotDeleteException, HostConnectionFailureException,
        InvalidEmailAddressException, IOException, MessagingException {
        try {
            Main.setLoggedInAccount(EmailAccount.load(0));
        } catch (TextFileNotFoundException textFileNotFoundException) {
            setLoggedInAccount(true);
        }
        Screen.setPrimaryStage(stage, Main.ICON_FILE_NAME);
        Main.INBOX_SCREEN.load();
        Main.INBOX_SCREEN.getController().setController(Main
            .getLoggedInAccount().getInbox(emailInboxType));
    }

    /**
     * Waits for the emails in the inbox to load.
     * @param robot Robot that uses the program
     */
    protected void waitForEmails(@NotNull FxRobot robot) {
        VBox emailBoxes = robot.lookup(EMAIL_BOXES_ID).queryAs(VBox.class);
        while (emailBoxes.getChildren().size() < 1) {
            robot.sleep(WAIT_FOR_EMAILS_TIME);
        }
    }

}
