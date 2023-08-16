package screens.tags;

import email.HostConnectionFailureException;
import email.address.EmailAddress;
import email.address.InvalidEmailAddressException;
import email.email.Email;
import email.email.EmailHeader;
import email.email.body.EmailBody;
import email.email.body.EmailContents;
import email.email.body.tag.EmailListTag;
import email.service.MissingEmailServiceInboxName;
import file.ExtendedFile;
import file.FileCanNotDeleteException;
import gui.Main;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import screens.ScreenTestSuite;
import util.BulletPointType;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.HashMap;

/**
 * Test Suite for the Tag Screen with a new email.
 * (Test Google, Outlook and Other Accounts)
 * (Check sent [Tags])
 * @author Jordan Jones
 */
public class TagsScreenNewTestSuite extends TagsScreenTestSuite {

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
    private void start(Stage stage) throws FileCanNotDeleteException,
        HostConnectionFailureException, IOException, MessagingException,
        MissingEmailServiceInboxName {
        setup(stage, OTHER, false, true);
        Main.setLoggedInAccount(getEmailAccount());
        HashMap<Message.RecipientType, EmailAddress[]> recipients
            = new HashMap<>();
        recipients.put(Message.RecipientType.TO, new EmailAddress[]{
            GOOGLE.getEmailAddress()});
        recipients.put(Message.RecipientType.CC, new EmailAddress[]{
            OUTLOOK.getEmailAddress()});
        recipients.put(Message.RecipientType.BCC, new EmailAddress[]{
            OTHER.getEmailAddress()});
        EmailHeader emailHeader = new EmailHeader(Main.getLoggedInAccount()
            .getEmailAddress(), recipients, SUBJECT);
        setEmail(new Email(emailHeader, new EmailBody(emailHeader,
            new EmailContents(CONTENTS_WITH_TAGS, EMAIL_TAGS),
            new ExtendedFile[0])));
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
     * FUNCTIONAL 1-6+10-11+13 + NON-FUNCTIONAL 2
     * Tests if the send button works correctly.
     * @param robot Robot that uses the program
     */
    @Test
    void onSendButton(@NotNull FxRobot robot) {
        for (BulletPointType bulletPointType
            : BulletPointType.BULLET_POINT_TYPES) {
            HashMap<Message.RecipientType, EmailAddress[]> recipients
                = new HashMap<>();
            recipients.put(Message.RecipientType.TO, new EmailAddress[]{
                GOOGLE.getEmailAddress()});
            recipients.put(Message.RecipientType.CC, new EmailAddress[]{
                OUTLOOK.getEmailAddress()});
            recipients.put(Message.RecipientType.BCC, new EmailAddress[]{
                OTHER.getEmailAddress()});
            EmailHeader emailHeader = new EmailHeader(Main.getLoggedInAccount()
                .getEmailAddress(), recipients, SUBJECT);
            setEmail(new Email(emailHeader, new EmailBody(emailHeader,
                new EmailContents(CONTENTS_WITH_TAGS, EMAIL_TAGS),
                new ExtendedFile[0])));
            load(robot);
            ((EmailListTag) getEmail().getBody().getContents().getTags()[1])
                .setBulletPointType(bulletPointType);
            sendButton(robot);
            Assertions.assertThat(robot.lookup(ScreenTestSuite.SCREEN_SPACE)
                .queryAs(Pane.class).getChildren().size()).isEqualTo(0);
        }
    }

}
