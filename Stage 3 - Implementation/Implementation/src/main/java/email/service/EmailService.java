package email.service;

import com.sun.mail.util.MailConnectException;
import email.EmailAccount;
import email.HostConnectionFailureException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;
import java.util.HashMap;

/**
 * Represents an email service provider.
 * @author Jordan Jones
 */
public class EmailService {

    //CONSTANTS
    public static final HashMap<String, EmailService> EMAIL_SERVICES
        = new HashMap<>();
    static {
        EMAIL_SERVICES.put("Google", new EmailService("Google",
            "smtp.gmail.com", "imap.gmail.com"));
        EMAIL_SERVICES.put("Outlook", new EmailService("Outlook",
            "smtp-mail.outlook.com", "outlook.office365.com"));
    }

    //Attributes
    private String name;
    private String smtpHostName;
    private String imapHostName;
    private String defaultInboxName;
    private String draftsInboxName;
    private String sentInboxName;
    private String spamInboxName;
    private String binInboxName;

    /**
     * Creates an email service.
     * @param name Name
     * @param smtpHostName SMTP host name
     * @param imapHostName IMAP host name
     */
    public EmailService(String name, String smtpHostName, String imapHostName) {
        setName(name);
        setSmtpHostName(smtpHostName);
        setImapHostName(imapHostName);
    }

    /**
     * Returns an existing service if there is one for the given hostnames.
     * @param smtp SMTP hostname
     * @param imap IMAP hostname
     * @return Existing service if there is one, otherwise a null value
     */
    public static @Nullable EmailService emailService(
        String smtp, String imap) {
        for (EmailService emailService : EMAIL_SERVICES.values()) {
            if (emailService.getSmtpHostName().equals(smtp)
                && emailService.getImapHostName().equals(imap)) {
                return emailService;
            }
        }
        return null;
    }

    /**
     * Returns a name of the given email service otherwise the smtp+imap combo.
     * @param smtp SMTP hostname
     * @param imap IMAP hostname
     * @return Service name if there is one, otherwise the smtp+imap combo
     */
    public static String emailServiceName(String smtp, String imap) {
        EmailService emailService = emailService(smtp, imap);
        if (emailService != null) {
            return emailService.getName();
        } else {
            return smtp + " " + imap;
        }
    }

    /**
     * Sets the name.
     * @param name Name
     */
    private void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the SMTP host name.
     * @param smtpHostName SMTP host name
     */
    private void setSmtpHostName(String smtpHostName) {
        this.smtpHostName = smtpHostName;
    }

    /**
     * Sets the IMAP host name.
     * @param imapHostName IMAP host name
     */
    private void setImapHostName(String imapHostName) {
        this.imapHostName = imapHostName;
    }

    /**
     * Sets the names of the inboxes.
     * @param emailAccount Email account
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws MessagingException Thrown if error with java mail
     * @throws MissingEmailServiceInboxName Thrown if error the email service
     */
    public void setInboxNames(@NotNull EmailAccount emailAccount)
        throws HostConnectionFailureException, MessagingException,
        MissingEmailServiceInboxName {
        Store store = emailAccount.getSession().getStore();
        try {
            store.connect(emailAccount.getEmailService()
                    .getImapHostName(),
                emailAccount.getEmailAddress().getAddress(),
                emailAccount.getPassword());
        } catch (MailConnectException mailConnectException) {
            throw new HostConnectionFailureException();
        }
        Folder[] folders = store.getDefaultFolder().list("*");
        StringBuilder folderNames = new StringBuilder();
        for (Folder folder : folders) {
            if ((folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
                String folderName = folder.getFullName();
                String comparison = folderName.toLowerCase();
                folderNames.append(folderName).append("\n");
                if (comparison.contains("inbox")) {
                    setDefaultInboxName(folderName);
                } else if (comparison.contains("drafts")) {
                    setDraftsInboxName(folderName);
                } else if (comparison.contains("sent")) {
                    setSentInboxName(folderName);
                } else if (comparison.contains("spam")
                    || comparison.contains("junk")) {
                    setSpamInboxName(folderName);
                } else if (comparison.contains("bin")
                    || comparison.contains("deleted")
                    || comparison.contains("trash")) {
                    setBinInboxName(folderName);
                }
            }
        }
        String errorInbox = "";
        if (getDefaultInboxName() == null) {
            errorInbox = "Default";
        } else if (getDraftsInboxName() == null) {
            errorInbox = "Drafts";
        } else if (getSentInboxName() == null) {
            errorInbox = "Sent";
        } else if (getSpamInboxName() == null) {
            errorInbox = "Spam";
        } else if (getBinInboxName() == null) {
            errorInbox = "Bin";
        }
        if (!errorInbox.equals("")) {
            throw new MissingEmailServiceInboxName(this, errorInbox,
                folderNames.toString());
        }
    }

    /**
     * Sets the default inbox name.
     * @param defaultInboxName Default inbox name
     */
    private void setDefaultInboxName(String defaultInboxName) {
        this.defaultInboxName = defaultInboxName;
    }

    /**
     * Sets the drafts inbox name.
     * @param draftsInboxName Drafts inbox name
     */
    private void setDraftsInboxName(String draftsInboxName) {
        this.draftsInboxName = draftsInboxName;
    }

    /**
     * Sets the sent inbox name.
     * @param sentInboxName Sent inbox name
     */
    private void setSentInboxName(String sentInboxName) {
        this.sentInboxName = sentInboxName;
    }

    /**
     * Sets the spam inbox name.
     * @param spamInboxName Spam inbox name
     */
    private void setSpamInboxName(String spamInboxName) {
        this.spamInboxName = spamInboxName;
    }

    /**
     * Sets the bin inbox name.
     * @param binInboxName Bin inbox name
     */
    private void setBinInboxName(String binInboxName) {
        this.binInboxName = binInboxName;
    }

    /**
     * Gets the name.
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the SMTP host name.
     * @return SMTP host name
     */
    public String getSmtpHostName() {
        return smtpHostName;
    }

    /**
     * Gets the IMAP host name.
     * @return IMAP host name
     */
    public String getImapHostName() {
        return imapHostName;
    }

    /**
     * Gets the default inbox name.
     * @return Default inbox name
     */
    public String getDefaultInboxName() {
        return defaultInboxName;
    }

    /**
     * Gets the drafts inbox name.
     * @return Drafts inbox name
     */
    public String getDraftsInboxName() {
        return draftsInboxName;
    }

    /**
     * Gets the sent inbox name.
     * @return Sent inbox name
     */
    public String getSentInboxName() {
        return sentInboxName;
    }

    /**
     * Gets the spam inbox name.
     * @return Spam inbox name
     */
    public String getSpamInboxName() {
        return spamInboxName;
    }

    /**
     * Gets the bin inbox name.
     * @return Bin inbox name
     */
    public String getBinInboxName() {
        return binInboxName;
    }

    /**
     * Returns the email service in a string format.
     * @return Email service in a string format
     */
    @Override
    public String toString() {
        return "EmailService{" +
            "name='" + name + '\'' +
            ", smtpHostName='" + smtpHostName + '\'' +
            ", imapHostName='" + imapHostName + '\'' +
            ", defaultInboxName='" + defaultInboxName + '\'' +
            ", draftsInboxName='" + draftsInboxName + '\'' +
            ", sentInboxName='" + sentInboxName + '\'' +
            ", spamInboxName='" + spamInboxName + '\'' +
            ", binInboxName='" + binInboxName + '\'' +
            '}';
    }
}
