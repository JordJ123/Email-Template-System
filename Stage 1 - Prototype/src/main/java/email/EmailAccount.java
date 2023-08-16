package email;

import com.sun.mail.util.MailConnectException;
import java.io.*;
import java.util.*;

import file.ExtendedFile;
import file.FileCanNotDeleteException;
import javafx.util.Pair;
import javax.mail.*;
import org.jetbrains.annotations.NotNull;
import email.address.EmailAddress;
import email.address.EmptyTagValueException;
import email.address.InvalidEmailAddressException;
import email.email.Email;
import file.directory.Directory;
import file.directory.DirectoryNotFoundException;
import file.serializable.OutOfDateClassException;
import file.serializable.SerializableFile;
import file.text.TextFile;
import file.text.TextFileNotFoundException;

/**
 * Represents an email account.
 * @author Jordan Jones
 */
public class EmailAccount implements Serializable {

    //CONSTANTS
    public static final String DETAILS_ERROR
        = "Email address and/or application password are incorrect";
    public static final String ACCOUNTS_PATH
        = ExtendedFile.SAVES_PATH + "/accounts";
    public static final String ACCOUNT_PATH = ACCOUNTS_PATH + "/%s";
    public static final String LOGIN_PATH = ACCOUNT_PATH + "/login.txt";
    public static final String HISTORY_PATH = ACCOUNT_PATH + "/history.ser";
    public static final String INBOXES_PATH = ACCOUNT_PATH + "/inboxes";
    public static final String TEMP_PATH = ExtendedFile.SAVES_PATH + "/temp";

    //Attributes
    private int accountNumber;
    private EmailAddress emailAddress;
    private String applicationPassword;
    private final HashMap<Character, TreeSet<EmailAddress>> history
        = new HashMap<>();
    private Session session;
    private boolean rememberMe;

    /**
     * Creates an email account.
     * @param accountNumber Number to represent the account in storage
     * @param emailAddress Email address of the account
     * @param applicationPassword Application password of the account
     * @param rememberMe Remember Me status
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws IOException Thrown if error with saving the email account (INT)
     * @throws MessagingException Thrown if error with java mail
     */
    public EmailAccount(int accountNumber, EmailAddress emailAddress,
        String applicationPassword, boolean rememberMe)
        throws HostConnectionFailureException, IOException, MessagingException {
        setAccountNumber(accountNumber);
        setEmailAddress(emailAddress);
        setApplicationPassword(applicationPassword);
        setSession();
        setRememberMe(rememberMe);
    }

    /**
     * Load an account from the device.
     * @param accountNumber Number that represents the account in storage
     * @return Null if the account exists
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws InvalidEmailAddressException Thrown if address format is invalid
     * @throws IOException Thrown if error with file related actions
     * @throws MessagingException Thrown if error with java mail server
     * @throws OutOfDateClassException Thrown if history file is out of date
     * @throws TextFileNotFoundException Thrown if login file can't be found
     */
    public static @NotNull EmailAccount load(int accountNumber)
        throws HostConnectionFailureException, InvalidEmailAddressException,
        IOException, MessagingException {
        EmailAccount emailAccount;
        try {
            Scanner scanner = new TextFile(String.format(LOGIN_PATH,
                accountNumber)).getContents();
            emailAccount = new EmailAccount(accountNumber,
                new EmailAddress(scanner.next()), scanner.next(), true);
            scanner.close();
        } catch (FileNotFoundException fileNotFoundException) {
            throw new TextFileNotFoundException(
                String.format(LOGIN_PATH, accountNumber));
        }

        try {
            emailAccount.addToHistory((new SerializableFile<EmailAddress[]>(
                String.format(HISTORY_PATH, accountNumber)).deserialize()));
        } catch (ClassNotFoundException classNotFoundException) {
            throw new OutOfDateClassException(EmailAccount.class.toString());
        } catch (FileNotFoundException fileNotFoundException) {
            emailAccount.addToHistory(
                new EmailAddress[]{emailAccount.getEmailAddress()});
        }
        return emailAccount;
    }

    /**
     * Sets the account number.
     * @param accountNumber Account number
     */
    private void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
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
     * @param applicationPassword Application password of the email account
     */
    private void setApplicationPassword(String applicationPassword) {
        this.applicationPassword = applicationPassword;
    }

    /**
     * Sets the session for the email account.
     * @throws AuthenticationFailedException Thrown if details are incorrect
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws MessagingException Thrown if error with java mail server
     */
    public void setSession()
        throws HostConnectionFailureException, MessagingException {
        Transport transport = null;
        try {
            Properties properties = System.getProperties();
            properties.setProperty("mail.smtp.host", "smtp.gmail.com");
            properties.setProperty("mail.smtp.post", "465");
            properties.setProperty("mail.smtp.ssl.enable", "true");
            properties.setProperty("mail.smtp.auth", "true");
            properties.setProperty("mail.store.protocol", "imaps");
            properties.setProperty("mail.imaps.partialfetch", "false");
            session = Session.getInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication
                    getPasswordAuthentication() {
                        return new PasswordAuthentication(
                            getEmailAddress().getAddress(),
                            getApplicationPassword());
                    }
                });
            transport = getSession().getTransport();
            transport.connect("smtp.gmail.com", getEmailAddress().getAddress(),
                getApplicationPassword());
        } catch (AuthenticationFailedException authenticationFailedException) {
            throw new AuthenticationFailedException(DETAILS_ERROR);
        } catch (MailConnectException mailConnectException) {
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
    public int getAccountNumber() {
        return accountNumber;
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
    public String getApplicationPassword() {
        return applicationPassword;
    }

    /**
     * Gets the account's session with the server.
     * @return Account's session with the server
     */
    public Session getSession() {
        return session;
    }

    /**
     * Gets a given inbox.
     * @param emailInboxType Inbox type
     * @return Inbox
     * @throws HostConnectionFailureException Thrown when can't connect to host
     * @throws MessagingException Thrown when error with email server
     */
    public EmailInbox getInbox(EmailInbox.EmailInboxType emailInboxType)
        throws HostConnectionFailureException, MessagingException {
        return new EmailInbox(this, emailInboxType);
    }

    /**
     * Gets the history of email addresses used with this account.
     * @return History of email addresses used with this account
     */
    public HashMap<Character, TreeSet<EmailAddress>> getHistory() {
        return history;
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
        return String.format(ACCOUNT_PATH, getAccountNumber());
    }

    /**
     * Gets the login path of the email account.
     * @return Login path of the email account
     */
    public String getLoginPath() {
        return String.format(LOGIN_PATH, getAccountNumber());
    }

    /**
     * Gets the history path of the email account.
     * @return History path of the email account
     */
    public String getHistoryPath() {
        return String.format(HISTORY_PATH, getAccountNumber());
    }

    /**
     * Gets the inbox path of the email account.
     * @return Inbox path of the email account
     */
    public String getInboxesPath() {
        return String.format(INBOXES_PATH, getAccountNumber());
    }

    /**
     * Saves the account's login details to the device.
     * @throws IOException Thrown if error with the save file
     */
    public void saveLogin() throws IOException {
        if (getRememberMe() && !(new Directory(getAccountPath()).exists())) {
            new Directory(getAccountPath()).create();
            TextFile textFile = new TextFile(getLoginPath());
            String contents = getEmailAddress() + " "
                + getApplicationPassword();
            textFile.setContents(contents);
        }
    }

    /**
     * Saves the account's history details to the device.
     * @throws IOException Thrown if error with the save file
     */
    private void saveHistory() throws IOException {
        if (getRememberMe()) {
            ArrayList<EmailAddress> emailAddresses = new ArrayList<>();
            for (TreeSet<EmailAddress> treeSet : getHistory().values()) {
                emailAddresses.addAll(treeSet);
            }
            SerializableFile<EmailAddress[]> serializableFile
                = new SerializableFile<>(getHistoryPath());
            try {
                serializableFile.serialize(
                    emailAddresses.toArray(EmailAddress[]::new));
            } catch (DirectoryNotFoundException directoryNotFoundException) {
                new Directory(getAccountPath()).create();
                serializableFile.serialize(
                    emailAddresses.toArray(EmailAddress[]::new));
            }
        }
    }

    /**
     * Deletes the account's details from the device.
     * @throws FileCanNotDeleteException Thrown if a files can't be deleted
     */
    public void delete() throws FileCanNotDeleteException {
        new Directory(getAccountPath()).deleteFiles();
    }

    /**
     * String version of the email account.
     * @return String version of the email account
     */
    public String toString() {
        return getEmailAddress() + " " + getApplicationPassword();
    }

    /**
     * Sends the email.
     * @param email Email to send
     * @throws EmptyTagValueException Thrown if an address' tag value is empty
     * @throws HostConnectionFailureException Thrown if it can't connect to host
     * @throws IOException Thrown if issue with attachment files
     * @throws MessagingException Thrown if issue with generating message
     */
    public void sendEmail(@NotNull Email email) throws EmptyTagValueException,
        HostConnectionFailureException, IOException, MessagingException {
        try {
            if (email.getMessageId() != null) {
                getInbox(EmailInbox.EmailInboxType.DRAFTS).deleteEmail(email);
            }
            if (email.getBody().getContents().getTags().length == 0) {
                Transport.send(email.generateMessage(getSession()));
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
     * @throws IOException Thrown if issue with attachment files
     * @throws MessagingException Thrown if issue with generating message
     */
    private void sendEmailWithTags(@NotNull Email email)
        throws EmptyTagValueException, IOException, MessagingException {

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
            Transport.send(email.generateMessageWithTags(getSession(), group));
        }

    }

    /**
     * Adds an email to spam.
     * @param email Email to add as spam
     * @throws HostConnectionFailureException Thrown if you can't connect
     * @throws MessagingException             Thrown if error with email server
     */
    public void spamEmail(Email email)
        throws HostConnectionFailureException, MessagingException {
        getInbox(EmailInbox.EmailInboxType.INBOX).moveEmail(email,
            getInbox(EmailInbox.EmailInboxType.SPAM));
    }

    /**
     * Removes an email from spam.
     * @param email Email to remove from spam
     * @throws HostConnectionFailureException Thrown if you can't connect
     * @throws MessagingException             Thrown if error with email server
     */
    public void notSpamEmail(Email email)
        throws HostConnectionFailureException, MessagingException {
        getInbox(EmailInbox.EmailInboxType.SPAM).moveEmail(email,
            getInbox(EmailInbox.EmailInboxType.INBOX));
    }

    /**
     * Restores an email from the bin.
     * @param email Email to restore from the bin
     * @throws HostConnectionFailureException Thrown if you can't connect
     * @throws MessagingException             Thrown if error with email server
     */
    public void restoreEmail(Email email)
        throws HostConnectionFailureException, MessagingException {
        getInbox(EmailInbox.EmailInboxType.BIN).moveEmail(email,
            getInbox(EmailInbox.EmailInboxType.INBOX));
    }

    /**
     * Adds the given addresses to the account's history if not added already.
     * @param emailAddresses Email addresses to add to the account's history
     * @throws IOException Thrown if error with saving the history
     */
    public void addToHistory(EmailAddress @NotNull [] emailAddresses)
        throws IOException {
        for (EmailAddress emailAddress : emailAddresses) {
            char first = emailAddress.getAddress().toCharArray()[0];
            if (getHistory().get(first) == null) {
                getHistory().put(first, new TreeSet<>(
                    Comparator.comparing(EmailAddress::getAddress)));
            }
            getHistory().get(first).add(emailAddress);
        }
        saveHistory();
    }

    /**
     * Finds similar address in the account's history based on the start string.
     * @param start Start of the email addresses to be found
     * @return Email addresses that start with the given string (not equals)
     */
    public EmailAddress[] findSimilarHistory(@NotNull String start) {
        ArrayList<EmailAddress> emailAddresses = new ArrayList<>();
        char first = start.toCharArray()[0];
        if (getHistory().get(first) != null) {
            boolean foundSet = false;
            for (EmailAddress emailAddress : getHistory().get(first)) {
                if (emailAddress.getAddress().startsWith(start)) {
                    foundSet = true;
                    if (!emailAddress.getAddress().equals(start)) {
                        emailAddresses.add(emailAddress);
                    }
                } else {
                    if (foundSet) {
                        break;
                    }
                }
            }
        }
        return emailAddresses.toArray(EmailAddress[]::new);
    }

}
