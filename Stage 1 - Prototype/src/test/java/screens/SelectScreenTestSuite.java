package screens;

import java.io.IOException;

import file.FileCanNotDeleteException;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.mail.MessagingException;
import org.jetbrains.annotations.NotNull;
import org.testfx.assertions.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import email.EmailAccount;
import email.HostConnectionFailureException;
import email.address.EmailAddress;
import email.address.InvalidEmailAddressException;
import file.directory.Directory;
import gui.Main;
import gui.screens.Screen;

/**
 * Test Suite for the Select Screen.
 * @author Jordan Jones
 */
public class SelectScreenTestSuite extends ScreenTestSuite {

    //CONSTANTS
    private static final String EMAIL_ACCOUNT_BOXES = "#emailAccountBoxes";
    private static final String EMAIL_ACCOUNT_BOX = "#emailAccountBox";
    private static final String LOGOUT_BUTTON_ID = "#logoutButton";
    private static final String ADD_BUTTON_ID = "#addButton";

    //Attributes
    private EmailAccount emailAccount;

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
        InvalidEmailAddressException, IOException,
        HostConnectionFailureException, MessagingException {
        Screen.setPrimaryStage(stage, Main.ICON_FILE_NAME);
        new Directory(EmailAccount.ACCOUNTS_PATH).deleteFiles();
        emailAccount = new EmailAccount(1, new EmailAddress(
            "jordan.joneswork090101@gmail.com"), "pdjpzuoiibhnfntc", true);
        emailAccount.saveLogin();
        new EmailAccount(2, new EmailAddress(
            "jordan.joneswork090101@gmail.com"), "pdjpzuoiibhnfntc", true)
            .saveLogin();
        Main.SELECT_SCREEN.load();
    }

    /**
     * Tests if selecting a hbox correctly logs into the email of the hbox.
     * @param robot Robot that uses the program
     */
    @Test
    void onSelectHBox(@NotNull FxRobot robot) {
        robot.clickOn(EMAIL_ACCOUNT_BOX);
        Assertions.assertThat(Screen.getPrimaryStage().getTitle()).isEqualTo(
            Main.HOME_SCREEN.getWindowTitle());
        Assertions.assertThat(Main.getLoggedInAccount().getAccountNumber())
            .isEqualTo(emailAccount.getAccountNumber());
    }

    /**
     * Tests if the logout button works correctly.
     * @param robot Robot that uses the program
     */
    @Test
    void onLogoutButton(@NotNull FxRobot robot) {
        VBox emailAccountBoxes = robot.lookup(EMAIL_ACCOUNT_BOXES)
            .queryAs(VBox.class);
        Assertions.assertThat(emailAccountBoxes.getChildren().size())
            .isEqualTo(2);
        robot.clickOn(LOGOUT_BUTTON_ID);
        Assertions.assertThat(emailAccountBoxes.getChildren().size())
            .isEqualTo(1);
        robot.clickOn(LOGOUT_BUTTON_ID);
        Assertions.assertThat(Screen.getPrimaryStage().getTitle()).isEqualTo(
            Main.LOGIN_SCREEN.getWindowTitle());
    }

    /**
     * Tests if the add button works correctly.
     * @param robot Robot that uses the program
     */
    @Test
    void onAddButton(@NotNull FxRobot robot) {
        robot.clickOn(ADD_BUTTON_ID);
        Assertions.assertThat(Screen.getPrimaryStage().getTitle()).isEqualTo(
            Main.LOGIN_SCREEN.getWindowTitle());
    }

}
