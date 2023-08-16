package screens;

import email.EmailAccount;
import email.HostConnectionFailureException;
import email.address.EmailAddress;
import email.address.InvalidEmailAddressException;
import email.email.Email;
import email.email.body.tag.EmailListTag;
import email.email.body.tag.EmailTag;
import email.service.EmailService;
import email.service.MissingEmailServiceInboxName;
import email.service.NonSupportedEmailService;
import file.FileCanNotDeleteException;
import file.directory.Directory;
import gui.Main;
import gui.fxml.FXMLScreen;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;

import javax.mail.MessagingException;
import java.io.IOException;

/**
 * Test Suite for a Screen.
 * @author Jordan Jones
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ApplicationExtension.class)
public class ScreenTestSuite {

    //FXIDs
    protected static final String SCREEN_SPACE = "#screenSpace";
    protected static final String TAGS_SCREEN_ID = "#tagsScreen";

    //TEST VALUES
    protected static EmailAccount GOOGLE;
    protected static EmailAccount OUTLOOK;
    protected static EmailAccount OTHER;
    static {
        try {
            GOOGLE = new EmailAccount(0,
                new EmailAddress("testforets@gmail.com"),
                "tsunkjwntnhqcdxp", EmailService.EMAIL_SERVICES.get("Google"),
                true);
            OUTLOOK = new EmailAccount(0,
                new EmailAddress("testforets@outlook.com"),
                "zrsmllwzcebagnog", EmailService.EMAIL_SERVICES.get("Outlook"),
                true);
            OTHER = new EmailAccount(0,
                new EmailAddress("testforets@zohomail.eu"),
                "6fH9sNqQPbqf", new EmailService("Other", "smtp.zoho.eu",
                "imap.zoho.eu"), true);
        } catch (HostConnectionFailureException | InvalidEmailAddressException
            | NonSupportedEmailService e) {
            e.printStackTrace();
        }
    }
    protected static final String SUBJECT = "This is the subject";
    protected static final String CONTENTS
        = "fontStyle fontSize bold italic strikethrough subscript superscript "
        + "underline";
    protected static final EmailTag EMAIL_TAG = new EmailTag("TSTag");
    protected static final EmailTag EMAIL_LIST_TAG
        = new EmailListTag("TSListTag");
    protected static final String CONTENTS_WITH_TAGS
        = "Name: " + EMAIL_TAG + "<br>Coursework:<br>" + EMAIL_LIST_TAG;
    protected static final EmailTag[] EMAIL_TAGS
        = new EmailTag[]{EMAIL_TAG, EMAIL_LIST_TAG};
    protected static final String FILES_DIRECTORY = "src/test/resources";
    protected static final int WAIT_TIME = 1000;

    //Attributes
    private Stage stage;
    private EmailAccount emailAccount;
    private Email email;

    /**
     * Sets the stage.
     * @param stage Stage
     */
    protected void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Sets the email account.
     * @param emailAccount Email account
     * @param isSetSession True if it needs to set session
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws MessagingException Thrown if error with java mail
     * @throws MissingEmailServiceInboxName Thrown if email service error
     */
    protected void setEmailAccount(@NotNull EmailAccount emailAccount,
        boolean isSetSession)
        throws HostConnectionFailureException, MessagingException,
        MissingEmailServiceInboxName {
        this.emailAccount = emailAccount;
        if (isSetSession) {
            emailAccount.setSession();
        }
    }

    /**
     * Sets the email.
     * @param email Email to be set
     */
    protected void setEmail(Email email) {
        this.email = email;
    }

    /**
     * Gets the email account.
     * @return Email account
     */
    protected EmailAccount getEmailAccount() {
        return emailAccount;
    }

    /**
     * Gets the email.
     * @return Email
     */
    protected Email getEmail() {
        return email;
    }

    /**
     * Sets up the test suite environment.
     * @param stage Javafx Window
     * @param emailAccount Email account to store
     * @param isDeleteFiles True if deleting the file
     * @param isSetSession True if setting the account session
     * @throws FileCanNotDeleteException Thrown if accounts can't be deleted
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws IOException Thrown if error loading screen
     * @throws MessagingException Thrown if error with java mail
     * @throws MissingEmailServiceInboxName Thrown if missing an inbox name
     */
    protected void setup(@NotNull Stage stage, EmailAccount emailAccount,
        boolean isDeleteFiles, boolean isSetSession)
        throws FileCanNotDeleteException, IOException,
        HostConnectionFailureException, MessagingException,
        MissingEmailServiceInboxName {
        if (isDeleteFiles) {
            new Directory(EmailAccount.ACCOUNTS_PATH).deleteFiles();
        }
        if (emailAccount != null) {
            setEmailAccount(emailAccount, isSetSession);
            getEmailAccount().saveLogin();
        }
        Main.setLoggedInAccount(null);
        FXMLScreen.setPrimaryStage(stage, Main.ICON_FILE_NAME);
        FXMLScreen.getPrimaryStage().setMaximized(true);
        Main.BASE_SCREEN.load();
        setStage(stage);
    }

    /**
     * Code to run after test has finished.
     * @param robot Robot that the program uses
     */
    protected void cleanup(@NotNull FxRobot robot) {
        robot.interact(() -> Main.BASE_SCREEN.getController().clear());
        robot.interact(() -> {
            try {
                Main.INBOX_SCREEN.setController(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        robot.interact(() -> stage.close());
    }

    /**
     * Pauses the code at a given moment.
     */
    protected void pause() {
        try {
            Thread.sleep(1000000);
        } catch (Exception e) {
            ///
        }
    }

}
