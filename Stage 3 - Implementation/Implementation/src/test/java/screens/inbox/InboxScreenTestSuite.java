package screens.inbox;

import email.EmailAccount;
import email.EmailInbox;
import email.HostConnectionFailureException;
import file.FileCanNotDeleteException;
import file.directory.Directory;
import gui.Main;
import gui.fxml.FXMLScreen;
import gui.fxml.inbox.InboxScreenController;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import screens.ScreenTestSuite;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Test Suite for the Inbox Screen (Make sure there are 15 emails only).
 * @author Jordan Jones
 */
public class InboxScreenTestSuite extends ScreenTestSuite {

    //IDS
    protected static final String EMAIL_BOXES_ID = "#emailBoxes";
    protected static final String EMAIL_BOX_ID = "#emailBox";
    protected static final String BUTTONS_ID = "#buttons";
    private static final String VIEW_BUTTON = "#viewButton";
    private static final String DELETE_BUTTON = "#deleteButton";
    private static final String LEFT_BUTTON_ID = "#leftButton";
    private static final String RIGHT_BUTTON_ID = "#rightButton";
    private static final String PAGE_BUTTONS_ID = "#pageButtons";
    private static final String CURRENT_PAGE_ID = "#pageNumber";
    private static final String VIEW_BOX_ID = "#viewBox";

    //CONSTANTS
    private static final int WAIT_FOR_EMAILS_TIME = 1000;

    /**
     * FUNCTIONAL 1-12+13-22 + NON-FUNCTIONAL 1-6+7-8
     * Tests if the view button works.
     * @param robot Robot that uses the program
     */
    @Test
    void onViewButton(@NotNull FxRobot robot) {
        waitForEmails(robot);
        robot.clickOn(EMAIL_BOX_ID);
        robot.lookup(VIEW_BOX_ID);
    }

    /**
     * FUNCTIONAL 1-12+23-25 + NON-FUNCTIONAL 1-6
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
     * FUNCTIONAL 1-12+37-38 + NON-FUNCTIONAL 1-6+9-10
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
     * FUNCTIONAL 1-12+39 + NON-FUNCTIONAL 1-6+11-12
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
     * Codes to run at the start of each test.
     * @param stage Javafx Window
     * @param emailInboxType Type of the inbox to be opened
     * @throws IOException Thrown if error with saving the email account (INT)
     */
    protected void testStart(Stage stage,
        EmailInbox.EmailInboxType emailInboxType) throws IOException {
        setStage(stage);
        FXMLScreen.setPrimaryStage(stage, Main.ICON_FILE_NAME);
        FXMLScreen.getPrimaryStage().setMaximized(true);
        try {
            new Directory(EmailAccount.ACCOUNTS_PATH).deleteDirectories();
        } catch (FileCanNotDeleteException e) {
            //
        }
        Main.BASE_SCREEN.load();
        Main.setLoggedInAccount(OTHER);
        Main.BASE_SCREEN.getController().loadScreen(Main.INBOX_SCREEN,
            () -> {
                try {
                    Main.INBOX_SCREEN.getController().setController(
                        emailInboxType);
                } catch (ClassNotFoundException | HostConnectionFailureException
                    | IOException e) {
                    e.printStackTrace();
                }
            });
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
