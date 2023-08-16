package screens;

import java.io.IOException;

import file.FileCanNotDeleteException;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.VBox;
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
import email.EmailAccount;
import email.email.Email;
import file.ExtendedFile;
import file.directory.Directory;
import gui.Main;
import gui.screens.Screen;

/**
 * Test Suite for the View Screen.
 * @author Jordan Jones
 */
class ViewScreenTestSuite extends ScreenTestSuite {

    //CONSTANTS
    private static final String FILE_LINKS_ID = "#fileLinks";
    private static final String RETURN_BUTTON_ID = "#returnButton";

    /**
     * Runs at the start of each test case (JavaFx Only).
     * @param stage Javafx Window
     * @throws IOException Thrown if error with the login screen fxml
     */
    @Start
    private void start(Stage stage) throws IOException {
        Screen.setPrimaryStage(stage, Main.ICON_FILE_NAME);
        Main.VIEW_SCREEN.load();
    }

    /**
     * Tests if the return button goes to the default inbox.
     * @param robot Robot that uses the program
     * @throws FileCanNotDeleteException Thrown if a files can't be deleted
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws InvalidEmailAddressException Thrown if error with address format
     * @throws IOException Thrown if error with email attachments
     * @throws MessagingException Thrown if error with java mail
     */
    @Test
    void onReturnButtonDefaultInbox(@NotNull FxRobot robot)
        throws FileCanNotDeleteException, HostConnectionFailureException,
        InvalidEmailAddressException, IOException, MessagingException {
        returnButton(robot, EmailInbox.EmailInboxType.INBOX);
    }

    /**
     * Tests if the return button goes to the drafts inbox.
     * @param robot Robot that uses the program
     * @throws FileCanNotDeleteException Thrown if a files can't be deleted
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws InvalidEmailAddressException Thrown if error with address format
     * @throws IOException Thrown if error with email attachments
     * @throws MessagingException Thrown if error with java mail
     */
    @Test
    void onReturnButtonDraftsInbox(@NotNull FxRobot robot)
        throws FileCanNotDeleteException, HostConnectionFailureException,
        InvalidEmailAddressException, IOException, MessagingException {
        returnButton(robot, EmailInbox.EmailInboxType.DRAFTS);
    }

    /**
     * Tests if the return button goes to the sent inbox.
     * @param robot Robot that uses the program
     * @throws FileCanNotDeleteException Thrown if a files can't be deleted
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws InvalidEmailAddressException Thrown if error with address format
     * @throws IOException Thrown if error with email attachments
     * @throws MessagingException Thrown if error with java mail
     */
    @Test
    void onReturnButtonSentInbox(@NotNull FxRobot robot)
        throws FileCanNotDeleteException, HostConnectionFailureException,
        InvalidEmailAddressException, IOException, MessagingException {
        returnButton(robot, EmailInbox.EmailInboxType.SENT);
    }

    /**
     * Tests if the return button goes to the spam inbox.
     * @param robot Robot that uses the program
     * @throws FileCanNotDeleteException Thrown if a files can't be deleted
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws InvalidEmailAddressException Thrown if error with address format
     * @throws IOException Thrown if error with email attachments
     * @throws MessagingException Thrown if error with java mail
     */
    @Test
    void onReturnButtonSpamInbox(@NotNull FxRobot robot)
        throws FileCanNotDeleteException, HostConnectionFailureException,
        InvalidEmailAddressException, IOException, MessagingException {
        returnButton(robot, EmailInbox.EmailInboxType.SPAM);
    }

    /**
     * Tests if the return button goes to the bin inbox.
     * @param robot Robot that uses the program
     * @throws FileCanNotDeleteException Thrown if a files can't be deleted
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws InvalidEmailAddressException Thrown if error with address format
     * @throws IOException Thrown if error with email attachments
     * @throws MessagingException Thrown if error with java mail
     */
    @Test
    void onReturnButtonBinInbox(@NotNull FxRobot robot)
        throws FileCanNotDeleteException, HostConnectionFailureException,
        InvalidEmailAddressException, IOException, MessagingException {
        returnButton(robot, EmailInbox.EmailInboxType.BIN);
    }

    /**
     * Runs the return button for the given inbox.
     * @param robot Robot that uses the program
     * @param emailInboxType Type of inbox screen to return to
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws FileCanNotDeleteException Thrown if a files can't be deleted
     * @throws InvalidEmailAddressException Thrown if error with address format
     * @throws IOException Thrown if error with saving the email account (INT)
     * @throws MessagingException Thrown if error with java mail
     */
    private void returnButton(@NotNull FxRobot robot,
        EmailInbox.EmailInboxType emailInboxType)
        throws FileCanNotDeleteException, HostConnectionFailureException,
        InvalidEmailAddressException, IOException, MessagingException {
        setLoggedInAccount(false);
        robot.interact(() -> {
            try {
                Email email = Main.getLoggedInAccount()
                    .getInbox(emailInboxType).getEmail(1);
                Main.VIEW_SCREEN.getController()
                    .setController(email, emailInboxType);
                if (email.getBody().getAttachments().length > 0) {
                    Directory directory = new Directory(EmailAccount.TEMP_PATH);
                    for (ExtendedFile file : email.getBody().getAttachments()) {
                        Assertions.assertThat(directory.contains(file))
                            .isEqualTo(true);
                    }
                    ((Hyperlink) robot.lookup(FILE_LINKS_ID).queryAs(VBox.class)
                        .getChildren().get(0)).fire();
                    Screen.getPrimaryStage().toFront();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        robot.clickOn(RETURN_BUTTON_ID);
        Assertions.assertThat(Main.INBOX_SCREEN.getController()
            .getEmailInbox().getEmailInboxType()).isEqualTo(emailInboxType);
    }

}
