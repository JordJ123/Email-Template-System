package screens;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.HashMap;

import email.EmailInbox;
import file.FileCanNotDeleteException;
import file.directory.Directory;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import email.EmailAccount;
import email.HostConnectionFailureException;
import email.address.EmailAddress;
import email.address.InvalidEmailAddressException;
import email.email.Email;
import email.email.EmailHeader;
import email.email.body.EmailBody;
import email.email.body.EmailContents;
import email.email.body.tag.EmailListTag;
import email.email.body.tag.EmailTag;
import file.ExtendedFile;
import gui.Main;

/**
 * Base Test Suite for a Screen.
 * @author Jordan Jones
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ApplicationExtension.class)
public class ScreenTestSuite {

    //TEST VALUES
    protected static final String SUBJECT = "This is the subject";
    protected static final String CONTENTS
        = "This is the start, this is the middle, the is the end";
    protected static final EmailTag EMAIL_TAG = new EmailTag("TSTag");
    protected static final EmailTag EMAIL_LIST_TAG
        = new EmailListTag("TSListTag");
    protected static final String CONTENTS_WITH_TAGS
        = "Name: " + EMAIL_TAG + "<br>Coursework:<br>" + EMAIL_LIST_TAG;
    protected static final EmailTag[] EMAIL_TAGS
        = new EmailTag[]{EMAIL_TAG, EMAIL_LIST_TAG};
    protected static final String FILES_DIRECTORY = "src/test/resources";

    //Attributes
    private Email email;

    /**
     * Sets the logged in account.
     * @param rememberMe True if the account should be saved
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws FileCanNotDeleteException Thrown if a files can't be deleted
     * @throws InvalidEmailAddressException Thrown if error with address format
     * @throws IOException Thrown if error with saving the email account (INT)
     * @throws MessagingException Thrown if error with java mail
     */
    protected void setLoggedInAccount(boolean rememberMe)
        throws FileCanNotDeleteException, HostConnectionFailureException,
        InvalidEmailAddressException, IOException, MessagingException {
        new Directory(EmailAccount.ACCOUNTS_PATH).deleteFiles();
        Main.setLoggedInAccount(new EmailAccount(1,
            new EmailAddress("jordan.joneswork090101@gmail.com"),
            "pdjpzuoiibhnfntc", rememberMe));
        Main.getLoggedInAccount().saveLogin();
    }

    /**
     * Creates a test email.
     * @param isNew True if to create a new email, false if to get from drafts
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws FileCanNotDeleteException Thrown if a files can't be deleted
     * @throws InvalidEmailAddressException Thrown if error with address format
     * @throws IOException Thrown if error with saving the email account (INT)
     * @throws MessagingException Thrown if error with java mail
     */
    protected void setEmail(boolean isNew) throws FileCanNotDeleteException,
        HostConnectionFailureException, InvalidEmailAddressException,
        IOException, MessagingException {
        setLoggedInAccount(false);
        if (isNew) {
            HashMap<Message.RecipientType, EmailAddress[]> recipients
                = new HashMap<>();
            recipients.put(Message.RecipientType.TO, new EmailAddress[]{
                new EmailAddress("jordan.jones090101@gmail.com")});
            recipients.put(Message.RecipientType.CC, new EmailAddress[]{
                new EmailAddress("jordan.joneswork090101@gmail.com")});
            recipients.put(Message.RecipientType.BCC, new EmailAddress[]{
                new EmailAddress("1910397@swansea.ac.uk")});
            EmailHeader emailHeader = new EmailHeader(Main.getLoggedInAccount()
                .getEmailAddress(), recipients, SUBJECT);
            email = new Email(emailHeader, new EmailBody(emailHeader,
                new EmailContents(CONTENTS_WITH_TAGS, EMAIL_TAGS),
                new ExtendedFile[0]));
        } else {
            email = Main.getLoggedInAccount().getInbox(
                EmailInbox.EmailInboxType.DRAFTS).getEmail(1);
        }
    }

    /**
     * Gets the test email of the test suite.
     * @return Test email of the test suite
     */
    protected Email getEmail() {
        return email;
    }

}
