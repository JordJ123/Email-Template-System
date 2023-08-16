package screens.tags;

import email.EmailAccount;
import email.EmailInbox;
import email.HostConnectionFailureException;
import email.email.Email;
import email.email.body.tag.EmailListTag;
import email.email.body.tag.EmailTag;
import email.service.MissingEmailServiceInboxName;
import file.FileCanNotDeleteException;
import file.directory.Directory;
import gui.Main;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.Start;
import util.BulletPointType;
import javax.mail.MessagingException;
import java.io.IOException;

/**
 * Test Suite for the Tag Screen with a draft email
 * (5 drafts with recipients x3, normal and a list tag needed).
 * @author Jordan Jones
 */
public class TagsScreenEditTestSuite extends TagsScreenTestSuite {

    /**
     * Runs at the start of each test case (JavaFx Only).
     * @param stage Javafx Window
     * @throws FileCanNotDeleteException Thrown if accounts can't be deleted
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws IOException Thrown if error loading screen
     * @throws MessagingException Thrown if error with java mail
     * @throws MissingEmailServiceInboxName Thrown if service inbox name error
     */
    @Start
    private void start(Stage stage) throws
        FileCanNotDeleteException, HostConnectionFailureException,
        IOException, MessagingException, MissingEmailServiceInboxName {
        setup(stage, OTHER, false, true);
        Main.setLoggedInAccount(getEmailAccount());
        emailLoad();
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
     * FUNCTIONAL 1-6+10-13 + NON-FUNCTIONAL 2
     * Tests if the send button works correctly.
     * @param robot Robot that uses the program
     * @throws FileCanNotDeleteException Thrown if inboxes can't be deleted
     * @throws HostConnectionFailureException Thrown if error connecting to host
     */
    @Test
    void onSendButton(@NotNull FxRobot robot)
        throws HostConnectionFailureException, FileCanNotDeleteException {
        boolean isEmailLoaded = true;
        for (BulletPointType bulletPointType
            : BulletPointType.BULLET_POINT_TYPES) {
            if (isEmailLoaded) {
                isEmailLoaded = false;
            } else {
                emailLoad();
            }
            load(robot);
            EmailTag[] emailTags = getEmail().getBody().getContents().getTags();
            if (emailTags[0] instanceof EmailListTag) {
                ((EmailListTag) emailTags[0])
                    .setBulletPointType(bulletPointType);
            } else {
                ((EmailListTag) emailTags[1])
                    .setBulletPointType(bulletPointType);
            }
            sendButton(robot);
            Assertions.assertThat(Main.INBOX_SCREEN.getController()
                .getEmailInbox().getEmailInboxType()).isEqualTo(
                EmailInbox.EmailInboxType.DRAFTS);
        }
    }

    /**
     * Loads the email from the draft inbox.
     * @throws FileCanNotDeleteException Thrown if inboxes can't be deleted
     * @throws HostConnectionFailureException Thrown if error connecting to host
     */
    protected void emailLoad() throws FileCanNotDeleteException,
        HostConnectionFailureException {
        new Directory(String.format(EmailAccount.INBOXES_PATH,
            getEmailAccount().getId())).deleteFiles();
        EmailInbox emailInbox = Main.getLoggedInAccount().getInbox(
            EmailInbox.EmailInboxType.DRAFTS);
        emailInbox.refresh();
        setEmail(emailInbox.getEmails().values().toArray(new Email[0])[
            emailInbox.getEmails().size() - 1]);
    }

}
