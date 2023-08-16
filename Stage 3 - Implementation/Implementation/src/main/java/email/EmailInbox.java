package email;

import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.util.MailConnectException;
import email.address.EmailAddress;
import email.address.InvalidEmailAddressException;
import email.email.Email;
import email.email.EmailHeader;
import email.email.body.EmailBody;
import email.email.body.EmailContents;
import email.email.body.tag.EmailListTag;
import email.email.body.tag.EmailTag;
import email.service.EmailService;
import email.service.MissingEmailServiceInboxName;
import file.ExtendedFile;
import file.directory.DirectoryNotFoundException;
import file.serializable.SerializableFile;
import file.serializable.SerializableFileNotFoundException;
import gui.Main;
import javafx.util.Pair;
import org.jboss.resource.adapter.jdbc.remote.SerializableInputStream;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.MessageIDTerm;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Inbox containing multiple emails.
 * @author Jordan Jones
 */
public class EmailInbox {

    //Attributes
    private EmailAccount emailAccount;
    private EmailInboxType emailInboxType;
    private Session session;
    private Folder folder;
    private final TreeMap<Pair<String, Date>, Email> emails
        = new TreeMap<>((o1, o2) -> {
        int index = o2.getValue().compareTo(o1.getValue());
        if (index != 0) {
            return index;
        } else {
            if (o2.getKey().equals(o1.getKey())) {
                return 0;
            } else {
                return 1;
            }
        }
    });

    /**
     * Email inbox containing multiple emails.
     * @param emailAccount Email account the inbox is part of
     * @param emailInboxType Email inbox type
     */
    public EmailInbox(@NotNull EmailAccount emailAccount,
        EmailInboxType emailInboxType) {
        setEmailAccount(emailAccount);
        setEmailInboxType(emailInboxType);
    }

    /**
     * Represents the type of inbox.
     */
    public enum EmailInboxType {
        INBOX, DRAFTS, SENT, SPAM, BIN
    }

    /**
     * Sets the email account.
     * @param emailAccount Email account
     */
    private void setEmailAccount(EmailAccount emailAccount) {
        this.emailAccount = emailAccount;
    }

    /**
     * Sets the email inbox type.
     * @param emailInboxType Email inbox type
     */
    private void setEmailInboxType(EmailInboxType emailInboxType) {
        this.emailInboxType = emailInboxType;
    }

    /**
     * Sets the session of the inbox.
     * @throws HostConnectionFailureException Thrown if error with details
     * @throws MessagingException Thrown if error with server
     * @throws MissingEmailServiceInboxName Thrown if email service error
     */
    private void setSession() throws HostConnectionFailureException,
        MessagingException, MissingEmailServiceInboxName {
        if (this.session == null) {
            this.session = emailAccount.getSession();
        }
    }

    /**
     * Sets the folder of the inbox.
     * @throws EnumConstantNotPresentException Thrown if no code for enum
     * @throws HostConnectionFailureException Thrown if error with host account
     * @throws MessagingException Thrown if error with java mail
     * @throws MissingEmailServiceInboxName Thrown if email service error
     */
    private void setFolder()
        throws HostConnectionFailureException, MessagingException,
        MissingEmailServiceInboxName {
        if (this.folder == null) {
            setSession();
            Store store = getSession().getStore();
            try {
                store.connect(getEmailAccount().getEmailService()
                        .getImapHostName(),
                    getEmailAccount().getEmailAddress().getAddress(),
                    getEmailAccount().getPassword());
            } catch (MailConnectException mailConnectException) {
                throw new HostConnectionFailureException();
            }
            EmailService emailService = getEmailAccount().getEmailService();
            String inbox;
            System.out.println(emailService);
            switch (getEmailInboxType()) {
                case INBOX:
                    inbox = emailService.getDefaultInboxName();
                    break;
                case DRAFTS:
                    inbox = emailService.getDraftsInboxName();
                    break;
                case SENT:
                    inbox = emailService.getSentInboxName();
                    break;
                case SPAM:
                    inbox = emailService.getSpamInboxName();
                    break;
                case BIN:
                    inbox = emailService.getBinInboxName();
                    break;
                default:
                    throw new EnumConstantNotPresentException(
                        EmailInboxType.class, getEmailInboxType().toString());
            }
            this.folder = store.getFolder(inbox);
        }
    }

    /**
     * Gets the email account.
     * @return Email account
     */
    private EmailAccount getEmailAccount() {
        return emailAccount;
    }

    /**
     * Gets the email inbox type.
     * @return Email inbox type
     */
    public EmailInboxType getEmailInboxType() {
        return emailInboxType;
    }

    /**
     * Gets the inbox session.
     * @return Inbox session
     */
    private Session getSession() {
        return session;
    }

    /**
     * Gets the inbox folder.
     * @return Inbox folder
     * @throws HostConnectionFailureException Thrown if error with host account
     * @throws MessagingException Thrown if error with java mail
     */
    private Folder getFolder() throws HostConnectionFailureException,
        MessagingException, MissingEmailServiceInboxName {
        if (folder == null) {
            setFolder();
        }
        return folder;
    }

    /**
     * Gets the email map.
     * @return Email map
     */
    public TreeMap<Pair<String, Date>, Email> getEmails() {
        return emails;
    }

    /**
     * Saves the emails locally.
     * @throws IOException Thrown if error with serializable file
     */
    public void save() throws IOException {
        Email[] emails = new Email[getEmails().size()];
        int i = 0;
        for (Email email : getEmails().values()) {
            emails[i] = email;
            i++;
        }
        SerializableFile<Email[]> serializableFile = new SerializableFile<>(
            getEmailAccount().getInboxesPath() + "/"
                + getEmailInboxType().toString().toLowerCase() + ".ser");
        try {
            serializableFile.serialize(emails);
        } catch (DirectoryNotFoundException directoryNotFoundException) {
            serializableFile.getDirectory().create();
            serializableFile.serialize(emails);
        }
    }

    /**
     * Loads the emails locally.
     * @throws ClassNotFoundException Thrown if an email class can't be found
     * @throws IOException Thrown if error with an email box's fxml file
     */
    public void load()
        throws ClassNotFoundException, IOException  {
        SerializableFile<Email[]> serializableFile = new SerializableFile<>(
            getEmailAccount().getInboxesPath() + "/"
                + getEmailInboxType().toString().toLowerCase() + ".ser");
        try {
            for (Email email : serializableFile.deserialize()) {
                getEmails().put(new Pair<>(email.getMessageId(), email
                    .getHeader().getReceivedDate()), email);
            }
        } catch (SerializableFileNotFoundException fileNotFoundException) {
            //No files to load;
        }
    }

    /**
     * Refreshes the inbox's emails with the email server.
     * @return Added emails and deleted email ids
     */
    public Pair<Email[], String[]> refresh() {
        Email[] add = new Email[0];
        String[] delete = new String[0];
        try {
            HashSet<String> emailIds = new HashSet<>(Arrays.asList(emailIds()));
            add = refreshAdd(emailIds);
            delete = refreshDelete(emailIds);
            save();
        } catch (HostConnectionFailureException exception) {
            // No internet connection so try again
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Pair<>(add, delete);
    }


    /**
     * Adds emails if it needs to.
     * @param emailIds Ids of emails to convert into messages
     * @throws HostConnectionFailureException Thrown if error with host account
     * @throws InvalidEmailAddressException Thrown if error with an address
     * @throws IOException Thrown if error with an email's attachments
     * @throws MessagingException Thrown if error with email server
     * @return Added ids
     */
    private Email @NotNull [] refreshAdd(@NotNull HashSet<String> emailIds)
        throws HostConnectionFailureException, InvalidEmailAddressException,
        IOException, MessagingException, MissingEmailServiceInboxName {
        HashSet<String> addIds = new HashSet<>();
        for (String emailId : emailIds) {
            boolean isNew = true;
            for (Pair<String, Date> pair : getEmails().keySet()) {
                if (pair.getKey().equals(emailId)) {
                    isNew = false;
                    break;
                }
            }
            if (isNew) {
                addIds.add(emailId);
            }
        }
        Email[] emails = emails(addIds.toArray(new String[0]));
        for (Email email : emails) {
            EmailAccount.addToHistory(email.getHeader().getAllAddresses());
            Pair<String, Date> pair = new Pair<>(email
                .getMessageId(), email.getHeader()
                .getReceivedDate());
            getEmails().put(pair, email);
        }
        return emails;
    }

    /**
     * Deletes emails if it needs to.
     * @param emailIds Ids of emails to convert into messages
     * @return Deleted email ids
     */
    private String @NotNull [] refreshDelete(
        @NotNull HashSet<String> emailIds) {
        HashSet<String> deleteIds = new HashSet<>();
        for (Pair<String, Date> pair : getEmails().keySet()) {
            if (!emailIds.contains(pair.getKey())) {
                deleteIds.add(pair.getKey());
            }
        }
        for (String deleteId : deleteIds) {
            for (Pair<String, Date> pair : getEmails().keySet()) {
                if (pair.getKey().equals(deleteId)) {
                    getEmails().remove(pair);
                    break;
                }
            }
        }
        return deleteIds.toArray(new String[0]);
    }

    /**
     * Gets the emails ids of the inbox.
     * @return Email ids of the inbox
     * @throws HostConnectionFailureException Thrown if error with host account
     * @throws MessagingException Thrown if error with java mail
     */
    public synchronized String[] emailIds()
        throws HostConnectionFailureException, MessagingException,
        MissingEmailServiceInboxName {
        getFolder().open(Folder.READ_ONLY);
        ArrayList<String> emailIds = new ArrayList<>();
        try {
            int i = 1;
            while (true) {
                try {
                    emailIds.add(((IMAPMessage) getFolder().getMessage(i))
                        .getMessageID());
                    i++;
                } catch (MessagingException messagingException) {
                    if (messagingException.getMessage() != null
                        && messagingException.getMessage().equals(
                        "Failed to load IMAP envelope")) {
                        //Not an email
                    } else if (messagingException instanceof
                        MessageRemovedException) {
                        System.out.println("Message Removed Exception");
                        i++;
                    } else {
                        throw messagingException;
                    }
                }
            }
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            //No more email ids
        }
        getFolder().close();
        return emailIds.toArray(new String[0]);
    }

    /**
     * Gets all emails based on the corresponding message ids.
     * @param messageIds Message ids to get emails from
     * @return Emails based on the corresponding message ids
     * @throws HostConnectionFailureException Thrown if error with host account
     * @throws InvalidEmailAddressException Thrown if error with an address
     * @throws IOException Thrown if error with an attachment file
     * @throws MessagingException Thrown if error with java mail
     */
    public synchronized Email[] emails(String @NotNull [] messageIds)
        throws HostConnectionFailureException, IOException,
        InvalidEmailAddressException, MessagingException, MissingEmailServiceInboxName {
        getFolder().open(Folder.READ_ONLY);
        Email[] emails = new Email[messageIds.length];
        for (int i = 0; i < emails.length; i++) {
            emails[i] = email(messageIds[i]);
        }
        getFolder().close();
        return emails;
    }

    /**
     * Gets an email with the given message id.
     * @param messageId ID of the email's message
     * @return Email of the given message id
     * @throws HostConnectionFailureException Thrown if error with host account
     * @throws InvalidEmailAddressException Thrown if error with an address
     * @throws IOException Thrown if error with an attachment file
     * @throws MessagingException Thrown if error with java mail
     */
    @Contract("_ -> new")
    public @NotNull Email email(String messageId)
        throws HostConnectionFailureException, IOException,
        InvalidEmailAddressException, MessagingException, MissingEmailServiceInboxName {
        boolean isClosed = false;
        if (!getFolder().isOpen()) {
            isClosed = true;
            getFolder().open(Folder.READ_ONLY);
        }
        IMAPMessage message = (IMAPMessage) getFolder()
            .search(new MessageIDTerm(messageId))[0];
        EmailHeader emailHeader = emailHeader(message);
        Email email = new Email(message.getMessageID(), emailHeader,
            emailBody(message, emailHeader),
            message.getFlags().contains(Flags.Flag.SEEN));
        if (isClosed) {
            getFolder().close();
        }
        return email;
    }

    /**
     * Gets the email header of an email.
     * @param message Message containing the email header information
     * @return Email header of an email
     * @throws InvalidEmailAddressException Thrown if error with an address
     * @throws MessagingException Thrown if error with host server
     */
    @Contract("_ -> new")
    private @NotNull EmailHeader emailHeader(IMAPMessage message)
        throws InvalidEmailAddressException, MessagingException {
        EmailAddress sender = emailSender(message);
        HashMap<Message.RecipientType, EmailAddress[]> recipients
            = new HashMap<>();
        recipients.put(Message.RecipientType.TO, emailRecipients(
            message, Message.RecipientType.TO));
        recipients.put(Message.RecipientType.CC, emailRecipients(
            message, Message.RecipientType.CC));
        recipients.put(Message.RecipientType.BCC, emailRecipients(
            message, Message.RecipientType.BCC));
        return new EmailHeader(sender,
            recipients, message.getReceivedDate(), message.getSubject());
    }

    /**
     * Gets the email sender of an email.
     * @param message Message storing the sender information
     * @return Email sender
     * @throws InvalidEmailAddressException Thrown if error with the address
     * @throws MessagingException Thrown if error with host server
     */
    private EmailAddress emailSender(@NotNull IMAPMessage message)
        throws InvalidEmailAddressException, MessagingException {
        InternetAddress sender = ((InternetAddress) message.getSender());
        EmailAddress emailAddress;
        try {
            emailAddress = new EmailAddress(
                sender.getAddress(), sender.getPersonal());
        } catch (InvalidEmailAddressException invalidEmailAddressException) {
            try {
                emailAddress = new EmailAddress(
                    sender.getPersonal().substring(1,
                        sender.getPersonal().length() - 1),
                    sender.getAddress().split(":;")[0]);
            } catch (InvalidEmailAddressException iEE) {
                emailAddress = new EmailAddress(
                    sender.getPersonal().substring(
                        sender.getPersonal().indexOf("<") + 1,
                        sender.getPersonal().indexOf(">")),
                    sender.getPersonal().substring(0,
                        sender.getPersonal().indexOf("<")));
            }
        } catch (NullPointerException exception) {
            emailAddress = getEmailAccount().getEmailAddress();
        }
        return emailAddress;
    }

    /**
     * Reads the recipients of an email.
     * @param message Message storing the recipients data
     * @param recipientType Type of recipients that are wanted
     * @return Recipients of the email of the given type
     * @throws InvalidEmailAddressException Thrown if error with an address
     * @throws MessagingException Thrown if error with connecting to server
     */
    private EmailAddress @NotNull [] emailRecipients(
        @NotNull IMAPMessage message, Message.RecipientType recipientType)
        throws InvalidEmailAddressException, MessagingException {
        Address[] recipients = message.getRecipients(recipientType);
        ArrayList<EmailAddress> addresses = new ArrayList<>();
        if (recipients != null) {
            for (int j = 0; j < recipients.length; j++) {
                String string = ((InternetAddress)
                    message.getRecipients(recipientType)[j]).getAddress();
                if (!string.endsWith(":;")) {
                    addresses.add(new EmailAddress(string));
                }
            }
        }
        return addresses.toArray(new EmailAddress[0]);
    }

    /**
     * Gets an email body of an email.
     * @param message Message containing the email body information
     * @param emailHeader Corresponding email header
     * @return Email body of an email
     * @throws IOException Thrown if error with an attachment
     * @throws MessagingException Thrown if error with host server
     */
    @Contract("_, _ -> new")
    private @NotNull EmailBody emailBody(@NotNull IMAPMessage message,
        EmailHeader emailHeader) throws IOException, MessagingException {
        Object messageBody = message.getContent();
        String text = "";
        EmailTag[] emailTags = new EmailTag[0];
        ExtendedFile[] files = new ExtendedFile[0];
        if (messageBody.getClass() == MimeMultipart.class) {
            MimeMultipart messageMultipart = (MimeMultipart) messageBody;
            text = emailText(messageMultipart);
            emailTags = emailTags(text);
            files = emailFiles(messageMultipart);
        } else if (messageBody.getClass() == String.class) {
            text = (String) messageBody;
        }
        return new EmailBody(emailHeader,
            new EmailContents(text, emailTags), files);
    }

    /**
     * Reads the text of an email.
     * @param messageBody Body of the email that contains the text
     * @return Text of an email
     * @throws IOException Thrown if error with body part content
     * @throws MessagingException Thrown if error with connecting to server
     */
    private @NotNull String emailText(@NotNull MimeMultipart messageBody)
        throws IOException, MessagingException {
        Object textContents = messageBody.getBodyPart(0).getContent();
        while (textContents.getClass() == MimeMultipart.class) {
            textContents = ((MimeMultipart) textContents)
                .getBodyPart(0).getContent();
        }
        return textContents.toString();
    }

    /**
     * Read the emailTags of an email.
     * @param text Text of an email to get the emailTags from
     * @return Tags of an email
     */
    private EmailTag @NotNull [] emailTags(String text) {
        Matcher matcher = Pattern.compile(EmailTag.getRegex()).matcher(text);
        LinkedHashSet<EmailTag> emailTags = new LinkedHashSet<>();
        while (matcher.find()) {
            String string = matcher.group();
            String tagName = string.substring(EmailTag.TAG_START.length(),
                string.length() - EmailTag.TAG_END.length());
            if (tagName.contains(EmailListTag.LIST_TAG_IDENTIFIER)) {
                emailTags.add(new EmailListTag(tagName.split(
                    EmailListTag.getRegex())[0]));
            } else {
                emailTags.add(new EmailTag(tagName));
            }
        }
        return emailTags.toArray(new EmailTag[0]);
    }

    /**
     * Reads the file attachments of an email.
     * @param messageMultipart Message body containing the files.
     * @return File attachments of an email
     * @throws IOException Thrown if an error with a file
     * @throws MessagingException Thrown if an error with connecting to server
     */
    private ExtendedFile @NotNull [] emailFiles(
        @NotNull MimeMultipart messageMultipart)
        throws IOException, MessagingException {
        int attachmentCount = messageMultipart.getCount() - 1;
        ArrayList<ExtendedFile> files = new ArrayList<>();
        for (int j = 0; j < attachmentCount; j++) {
            MimeBodyPart attachment;
            try {
                attachment = (MimeBodyPart) ((MimeMultipart)
                    messageMultipart.getBodyPart(j + 1).getContent())
                    .getBodyPart(0);
            } catch (ClassCastException classCastException) {
                attachment = (MimeBodyPart)
                    messageMultipart.getBodyPart(j + 1);
            }
            if (attachment.getFileName() != null) {
                ExtendedFile file = new ExtendedFile(
                    EmailAccount.TEMP_PATH + "/" + attachment.getFileName(),
                    new SerializableInputStream(attachment.getInputStream()));
                files.add(file);
            }
        }
        return files.toArray(new ExtendedFile[0]);
    }

    /**
     * Adds an email to the inbox.
     * @param email Email to be added to the inbox
     * @throws HostConnectionFailureException Thrown if error with host account
     * @throws MessagingException Thrown if error with email server
     * @throws IOException Thrown if error with message attachments
     * @throws MissingEmailServiceInboxName Thrown if email service error
     */
    public synchronized void addEmail(@NotNull Email email)
        throws MessagingException, IOException, HostConnectionFailureException,
        MissingEmailServiceInboxName {
        getFolder().open(Folder.READ_WRITE);
        setSession();
        MimeMessage message = email.generateMessage(getEmailAccount());
        message.setFlag(Flags.Flag.DRAFT, true);
        getFolder().appendMessages(new MimeMessage[]{message});
        getFolder().close();
    }

    /**
     * Reads a given email in the inbox.
     * @param email Email to be read
     * @throws HostConnectionFailureException Thrown if error with host account
     * @throws MessagingException Thrown if error with the server
     */
    public synchronized void readEmail(@NotNull Email email)
        throws HostConnectionFailureException, MessagingException, MissingEmailServiceInboxName {
        email.setIsRead(true);
        getFolder().open(Folder.READ_WRITE);
        getFolder().search(new MessageIDTerm(email.getMessageId()))[0]
            .setFlag(Flags.Flag.SEEN, true);
        getFolder().close();
    }

    /**
     * Updates the given email in the inbox.
     * @param email Email in the given inbox
     * @throws HostConnectionFailureException Thrown if error with host account
     * @throws IOException Thrown if error with an email attachment
     * @throws MessagingException Thrown if error with java mail server
     */
    public synchronized void updateEmail(Email email)
        throws HostConnectionFailureException, IOException, MessagingException, MissingEmailServiceInboxName {
        deleteEmail(email);
        addEmail(email);
    }

    /**
     * Deletes the given email from the inbox.
     * @param email Email to be deleted
     * @throws HostConnectionFailureException Thrown if error with a folder
     * @throws IOException Thrown if error with the backlog
     * @throws MessagingException Thrown if error with the server
     * @throws MissingEmailServiceInboxName Thrown if missing inbox name
     */
    public synchronized void deleteEmail(@NotNull Email email)
        throws HostConnectionFailureException, IOException, MessagingException,
        MissingEmailServiceInboxName {
        try {
            getFolder().open(Folder.READ_WRITE);
            getFolder().search(new MessageIDTerm(email.getMessageId()))[0]
                .setFlag(Flags.Flag.DELETED, true);
        } catch (ArrayIndexOutOfBoundsException arrayIndexOobException) {
            //Email already deleted from inbox so can continue as normal
        } catch (HostConnectionFailureException hostConnectionException) {
            new BacklogAction(BacklogAction.Action.DELETE,
                getEmailAccount(), getEmailInboxType(), email);
        }
        if (getFolder().isOpen()) {
            getFolder().close();
        }
        getEmails().remove(new Pair<>(email.getMessageId(),
            email.getHeader().getReceivedDate()));
        save();
    }

    /**
     * Moves an email from the inbox to a given inbox.
     * @param email Email to move
     * @param toInbox Inbox to move email to
     * @throws HostConnectionFailureException Thrown if error with host account
     * @throws MessagingException Thrown if error with the server
     */
    public synchronized void moveEmail(@NotNull Email email,
        @NotNull EmailInbox toInbox) throws HostConnectionFailureException,
        MessagingException, MissingEmailServiceInboxName {
        getFolder().open(Folder.READ_WRITE);
        try {
            getFolder().copyMessages(getFolder().search(
                new MessageIDTerm(email.getMessageId())), toInbox.getFolder());
        } catch (MessagingException exception) {
            if (!exception.getMessage().contains("APPEND")) {
                exception.printStackTrace();
            }
        }
        getFolder().close();
    }

    public void closeFolder() throws MessagingException {
        if (folder != null && folder.isOpen()) {
            folder.close();
        }
    }

}
