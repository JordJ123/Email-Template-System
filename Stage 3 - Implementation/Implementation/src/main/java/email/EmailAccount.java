package email;

import com.sun.mail.util.MailConnectException;
import email.address.EmailAddress;
import email.address.EmptyTagValueException;
import email.address.InvalidEmailAddressException;
import email.email.Email;
import email.service.EmailService;
import email.service.MissingEmailServiceInboxName;
import email.service.NonSupportedEmailService;
import file.ExtendedFile;
import file.FileCanNotDeleteException;
import file.directory.Directory;
import file.serializable.SerializableFile;
import file.serializable.SerializableFileNotFoundException;
import file.text.TextFile;
import file.text.TextFileNotFoundException;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import javax.mail.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Represents an email account.
 * @author Jordan Jones
 */
public class EmailAccount implements Serializable {

    //CONSTANTS
    public static final String DETAILS_ERROR
        = "Email address and/or application password are incorrect\n"
        + "or might need an application password";
    public static final String ACCOUNTS_PATH
        = ExtendedFile.SAVES_PATH + "/accounts";
    public static final String ACCOUNT_PATH = ACCOUNTS_PATH + "/%s";
    public static final String LOGIN_PATH = ACCOUNT_PATH + "/login.txt";
    public static final String INBOXES_PATH = ACCOUNT_PATH + "/inboxes";
    public static final String TEMP_PATH = ExtendedFile.SAVES_PATH + "/temp";
    public static final String HISTORY_PATH
        = ExtendedFile.SAVES_PATH + "/history.ser";
    public static final String BACKLOG_PATH
        = ExtendedFile.SAVES_PATH + "/backlog.ser";

    //Static Attributes
    private static HashMap<Integer, EmailAccount> emailAccounts
        = new HashMap<>();
    private static HashMap<Character, TreeSet<String>> history
        = new HashMap<>();
    static {
        try {
            String[] strings = new SerializableFile<String[]>(
                HISTORY_PATH).deserialize();
            EmailAddress[] emailAddresses = new EmailAddress[strings.length];
            for (int i = 0; i < strings.length; i++) {
                emailAddresses[i] = new EmailAddress(strings[i]);
            }
            addToHistory(emailAddresses);
        } catch (SerializableFileNotFoundException serializableFileException) {
            //Adding history will create the file anyway
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.port", "587");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.store.protocol", "imaps");
        properties.setProperty("mail.imaps.partialfetch", "false");
    }

    //Attributes
    private int id;
    private EmailService emailService;
    private EmailAddress emailAddress;
    private String password;
    private Session session;
    private boolean rememberMe;

    /**
     * Creates an email account.
     * @param id Number to represent the account in storage
     * @param emailAddress Email address of the account
     * @param password Application password of the account
     * @param emailService Email service of the email account
     * @param rememberMe Remember Me status
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws NonSupportedEmailService Thrown if not supported email service
     */
    public EmailAccount(int id, EmailAddress emailAddress,
        String password, EmailService emailService, boolean rememberMe)
        throws HostConnectionFailureException, NonSupportedEmailService {
        setId(id);
        setEmailAddress(emailAddress);
        setPassword(password);
        setEmailService(emailService);
        setRememberMe(rememberMe);
        addEmailAccount(this);
    }

    /**
     * Gets the history of email addresses used with the system.
     * @return History of email addresses used with the system
     */
    public static HashMap<Character, TreeSet<String>> getHistory() {
        return history;
    }

    /**
     * Gets the list of email accounts.
     * @return List of email account
     */
    private static HashMap<Integer, EmailAccount> getEmailAccounts() {
        return emailAccounts;
    }

    /**
     * Adds an email account to the list of email accounts.
     * @param emailAccount Email account to add to the list of email accounts
     */
    public static void addEmailAccount(EmailAccount emailAccount) {
        getEmailAccounts().put(emailAccount.getId(), emailAccount);
    }

    /**
     * Gets the email account with the given id.
     * @param id Id of the email account
     * @return Email account
     */
    public static EmailAccount readEmailAccount(int id) {
        return getEmailAccounts().get(id);
    }

    /**
     * Load an account from the device.
     * @param accountNumber Number that represents the account in storage
     * @return Null if the account exists
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws InvalidEmailAddressException Thrown if address format is invalid
     * @throws IOException Thrown if error with file related actions
     * @throws NonSupportedEmailService Thrown if not supported email service
     * @throws TextFileNotFoundException Thrown if login file can't be found
     */
    public static @NotNull EmailAccount load(int accountNumber)
        throws HostConnectionFailureException, InvalidEmailAddressException,
        IOException, NonSupportedEmailService {
        EmailAccount emailAccount;
        try {
            Scanner scanner = new TextFile(String.format(LOGIN_PATH,
                accountNumber)).getContents();
            emailAccount = new EmailAccount(accountNumber,
                new EmailAddress(scanner.next()), scanner.next(),
                EmailService.EMAIL_SERVICES.get(scanner.next()), true);
            scanner.close();
        } catch (FileNotFoundException fileNotFoundException) {
            throw new TextFileNotFoundException(
                String.format(LOGIN_PATH, accountNumber));
        }
        return emailAccount;
    }

    /**
     * Adds the given addresses to the system's history if not added already.
     * @param emailAddresses Email addresses to add to the system's history
     * @throws IOException Thrown if error with saving the history
     */
    public static void addToHistory(EmailAddress @NotNull [] emailAddresses)
        throws IOException {
        for (EmailAddress emailAddress : emailAddresses) {
            char first = emailAddress.getAddress().toCharArray()[0];
            getHistory().computeIfAbsent(first,
                k -> new TreeSet<>(Comparator.naturalOrder()));
            getHistory().get(first).add(emailAddress.getAddress());
        }
        saveHistory();
    }

    /**
     * Saves the system's history details to the device.
     * @throws IOException Thrown if error with the save file
     */
    private static void saveHistory() throws IOException {
        ArrayList<String> emailAddresses = new ArrayList<>();
        for (TreeSet<String> treeSet : getHistory().values()) {
            emailAddresses.addAll(treeSet);
        }
        SerializableFile<String[]> serializableFile
            = new SerializableFile<>(HISTORY_PATH);
        serializableFile.serialize(
            emailAddresses.toArray(String[]::new));
    }

    /**
     * Finds similar address in the system's history based on the start string.
     * @param start Start of the email addresses to be found
     * @return Email addresses that start with the given string (not equals)
     */
    public static String[] findSimilarHistory(@NotNull String start) {
        ArrayList<String> emailAddresses = new ArrayList<>();
        char first = start.toCharArray()[0];
        if (getHistory().get(first) != null) {
            boolean foundSet = false;
            for (String emailAddress : getHistory().get(first)) {
                if (emailAddress.startsWith(start)) {
                    foundSet = true;
                    if (!emailAddress.equals(start)) {
                        emailAddresses.add(emailAddress);
                    }
                } else {
                    if (foundSet) {
                        break;
                    }
                }
            }
        }
        return emailAddresses.toArray(String[]::new);
    }

    /**
     * Sets the account number.
     * @param id Account number
     */
    private void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the email service.
     * @param emailService Email service
     */
    private void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Sets the email address of the email account.
     * @param emailAddress Email address of the account
     */
    private void setEmailAddress(EmailAddress emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Gets the application password of the email account.
     * @param password Application password of the email account
     */
    private void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the session for the email account.
     * @throws AuthenticationFailedException Thrown if details are incorrect
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws MessagingException Thrown if error with java mail server
     * @throws MessagingException Thrown if error with java mail server
     */
    public void setSession()
        throws HostConnectionFailureException, MessagingException,
        MissingEmailServiceInboxName {
        Transport transport = null;
        try {
            changeSMTPHost();
            session = Session.getInstance(System.getProperties(),
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication
                    getPasswordAuthentication() {
                        return new PasswordAuthentication(
                            getEmailAddress().getAddress(),
                            getPassword());
                    }
                });
            transport = session.getTransport();
            transport.connect(getEmailAddress().getAddress(), getPassword());
            getEmailService().setInboxNames(this);
        } catch (AuthenticationFailedException authenticationFailedException) {
            session = null;
            System.out.println(authenticationFailedException.getMessage());
            throw new AuthenticationFailedException(DETAILS_ERROR);
        } catch (MailConnectException mailConnectException) {
            session = null;
            throw new HostConnectionFailureException();
        } finally {
            if (transport != null) {
                transport.close();
            }
        }
    }

    /**
     * Sets the rememberMe status of the email account.
     * @param rememberMe Remember me status
     */
    private void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    /**
     * Gets the account number.
     * @return Account number
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the email service.
     * @return Email service
     */
    public EmailService getEmailService() {
        return emailService;
    }

    /**
     * Gets the email address of the email account.
     * @return Email address of the account
     */
    public EmailAddress getEmailAddress() {
        return emailAddress;
    }

    /**
     * Gets the application password of the email account.
     *
     * @return Application password of the account
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the account's session with the server.
     * @return Account's session with the server
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws MessagingException Thrown if error with java mail server
     */
    public Session getSession() throws HostConnectionFailureException,
        MessagingException, MissingEmailServiceInboxName {
        if (session == null) {
            setSession();
        }
        return session;
    }

    /**
     * Gets a given inbox.
     * @param emailInboxType Inbox type
     * @return Inbox
     * @throws HostConnectionFailureException Thrown when can't connect to host
     */
    public EmailInbox getInbox(EmailInbox.EmailInboxType emailInboxType)
        throws HostConnectionFailureException {
        return new EmailInbox(this, emailInboxType);
    }

    /**
     * Gets remember me status.
     * @return Remember me status
     */
    private boolean getRememberMe() {
        return rememberMe;
    }

    /**
     * Gets the account path of the email account.
     * @return Account path of the email account
     */
    public String getAccountPath() {
        return String.format(ACCOUNT_PATH, getId());
    }

    /**
     * Gets the login path of the email account.
     * @return Login path of the email account
     */
    public String getLoginPath() {
        return String.format(LOGIN_PATH, getId());
    }

    /**
     * Gets the inbox path of the email account.
     * @return Inbox path of the email account
     */
    public String getInboxesPath() {
        return String.format(INBOXES_PATH, getId());
    }

    /**
     * Saves the account's login details to the device.
     * @throws IOException Thrown if error with the save file
     */
    public void saveLogin() throws IOException {
        if (getRememberMe()) {
            Directory directory = new Directory(getAccountPath());
            if (!directory.exists()) {
               directory.create();
            }
            TextFile textFile = new TextFile(getLoginPath());
            StringBuilder contents = new StringBuilder();
            contents.append(getEmailAddress().getAddress());
            if (getEmailAddress().getAddress() != null) {
                contents.append(" ").append("'")
                    .append(getEmailAddress().getNickname()).append("'");
            }
            contents.append(" ").append(getPassword());
            EmailService emailService = EmailService.EMAIL_SERVICES
                .get(getEmailService().getName());
            if (emailService != null) {
                contents.append(" ").append(getEmailService().getName());
            } else {
                contents.append(" ").append(getEmailService().getSmtpHostName())
                    .append(" ").append(getEmailService().getImapHostName());
            }
            textFile.setContents(contents.toString());
        }
    }

    /**
     * Deletes the account's details from the device.
     * @throws FileCanNotDeleteException Thrown if files can't be deleted
     * @throws IOException Thrown if a files can't be deleted
     */
    public void delete() throws IOException, FileCanNotDeleteException {
        new Directory(getAccountPath()).remove();
    }

    /**
     * String version of the email account.
     * @return String version of the email account
     */
    public String toString() {
        return getEmailAddress().toString();
    }

    /**
     * Sends the email.
     * @param email Email to send
     * @throws EmptyTagValueException Thrown if an address' tag value is empty
     * @throws HostConnectionFailureException Thrown if it can't connect to host
     * @throws IOException Thrown if issue with attachment files
     * @throws MessagingException Thrown if issue with generating message
     * @throws MissingEmailServiceInboxName Thrown if error with inbox name
     */
    public void sendEmail(@NotNull Email email) throws EmptyTagValueException,
        HostConnectionFailureException, IOException, MessagingException,
        MissingEmailServiceInboxName {
        try {
            if (email.getMessageId() != null) {
                getInbox(EmailInbox.EmailInboxType.DRAFTS).deleteEmail(email);
            }
            if (email.getBody().getContents().getTags().length == 0) {
                changeSMTPHost();
                Transport.send(email.generateMessage(this));
            } else {
                sendEmailWithTags(email);
            }
            addToHistory(email.getHeader().getAllAddresses());
        } catch (MailConnectException mailConnectException) {
            throw new HostConnectionFailureException();
        }
    }

    /**
     * Sends the email with the tag values.
     * @param email Email to send
     * @throws EmptyTagValueException Thrown if an address' tag value is empty
     * @throws HostConnectionFailureException Thrown if it can't connect to host
     * @throws IOException Thrown if issue with attachment files
     * @throws MessagingException Thrown if issue with generating message
     * @throws MissingEmailServiceInboxName Thrown if error with inbox name
     */
    private void sendEmailWithTags(@NotNull Email email)
        throws EmptyTagValueException, HostConnectionFailureException,
        IOException, MessagingException, MissingEmailServiceInboxName {

        //Sets emailTags for same addresses in different recipient types
        ArrayList<EmailAddress> allAddresses = new ArrayList<>();
        allAddresses.addAll(Arrays.asList(
            email.getHeader().getRecipients(Message.RecipientType.TO)));
        allAddresses.addAll(Arrays.asList(
            email.getHeader().getRecipients(Message.RecipientType.CC)));
        allAddresses.addAll(Arrays.asList(
            email.getHeader().getRecipients(Message.RecipientType.BCC)));
        for (EmailAddress emailAddress : allAddresses) {
            for (EmailAddress otherAddress : allAddresses) {
                if (emailAddress.equals(otherAddress)
                    && emailAddress != otherAddress) {
                    if (!emailAddress.getTagValues()[0][0].equals("")) {
                        otherAddress.setTagValuesHashMap(
                            emailAddress.getTagValuesHashMap());
                    }
                }
            }
        }

        //Sets the email address and recipient type pairs
        ArrayList<Pair<EmailAddress, Message.RecipientType>> emailAddresses
            = new ArrayList<>();
        for (EmailAddress emailAddress
            : email.getHeader().getRecipients(Message.RecipientType.TO)) {
            emailAddresses.add(
                new Pair<>(emailAddress, Message.RecipientType.TO));
        }
        for (EmailAddress emailAddress
            : email.getHeader().getRecipients(Message.RecipientType.CC)) {
            emailAddresses.add(
                new Pair<>(emailAddress, Message.RecipientType.CC));
        }
        for (EmailAddress emailAddress
            : email.getHeader().getRecipients(Message.RecipientType.BCC)) {
            emailAddresses.add(
                new Pair<>(emailAddress, Message.RecipientType.BCC));
        }

        //Groups of email addresses with the same tag values
        HashMap<Integer, ArrayList<Pair<EmailAddress, Message.RecipientType>>>
            groups = new HashMap<>();
        for (Pair<EmailAddress, Message.RecipientType> emailAddress
            : emailAddresses) {
            int hashValue = emailAddress.getKey().getTagValuesHashCode();
            if (!groups.containsKey(hashValue)) {
                groups.put(hashValue, new ArrayList<>());
            }
            groups.get(hashValue).add(emailAddress);
        }

        //Sends an email to each email address group
        for (ArrayList<Pair<EmailAddress, Message.RecipientType>>
            group : groups.values()) {
            changeSMTPHost();
            Transport.send(email.generateMessageWithTags(this, group));
        }
    }

    /**
     * Adds an email to spam.
     * @param email Email to add as spam
     * @throws HostConnectionFailureException Thrown if you can't connect
     * @throws MessagingException             Thrown if error with email server
     */
    public void spamEmail(Email email)
        throws HostConnectionFailureException, MessagingException, MissingEmailServiceInboxName, IOException {
        getInbox(EmailInbox.EmailInboxType.INBOX).moveEmail(email,
            getInbox(EmailInbox.EmailInboxType.SPAM));
        getInbox(EmailInbox.EmailInboxType.INBOX).deleteEmail(email);
    }

    /**
     * Removes an email from spam.
     * @param email Email to remove from spam
     * @throws HostConnectionFailureException Thrown if you can't connect
     * @throws MessagingException             Thrown if error with email server
     */
    public void notSpamEmail(Email email)
        throws HostConnectionFailureException, MessagingException, MissingEmailServiceInboxName, IOException {
        getInbox(EmailInbox.EmailInboxType.SPAM).moveEmail(email,
            getInbox(EmailInbox.EmailInboxType.INBOX));
        getInbox(EmailInbox.EmailInboxType.SPAM).deleteEmail(email);
    }

    /**
     * Restores an email from the bin.
     * @param email Email to restore from the bin
     * @throws HostConnectionFailureException Thrown if you can't connect
     * @throws MessagingException             Thrown if error with email server
     */
    public void restoreEmail(Email email)
        throws HostConnectionFailureException, MessagingException, MissingEmailServiceInboxName, IOException {
        getInbox(EmailInbox.EmailInboxType.BIN).moveEmail(email,
            getInbox(EmailInbox.EmailInboxType.INBOX));
        getInbox(EmailInbox.EmailInboxType.BIN).deleteEmail(email);
    }

    /**
     * Deletes the given email.
     * @param email Email to delete
     */
    public void deleteEmail(Email email,
        @NotNull EmailInbox.EmailInboxType emailInboxType)
        throws HostConnectionFailureException, IOException,
        MessagingException, MissingEmailServiceInboxName {
        if (emailInboxType == EmailInbox.EmailInboxType.INBOX) {
            getInbox(EmailInbox.EmailInboxType.INBOX).moveEmail(email,
                getInbox(EmailInbox.EmailInboxType.BIN));
            getInbox(EmailInbox.EmailInboxType.INBOX).deleteEmail(email);
        } else {
            getInbox(emailInboxType).deleteEmail(email);
        }
    }

    /**
     * Changes the smtp host of the system properties.
     */
    private void changeSMTPHost() {
        System.getProperties().remove("mail.smtp.host");
        System.getProperties().setProperty("mail.smtp.host",
            getEmailService().getSmtpHostName());
    }

}
