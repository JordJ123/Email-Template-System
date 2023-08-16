package email;

import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.util.MailConnectException;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.MessageIDTerm;

import email.email.body.tag.EmailListTag;
import email.email.body.tag.EmailTag;
import org.jboss.resource.adapter.jdbc.remote.SerializableInputStream;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import email.email.Email;
import email.email.body.EmailBody;
import email.email.body.EmailContents;
import email.email.EmailHeader;
import email.address.EmailAddress;
import email.address.InvalidEmailAddressException;
import file.ExtendedFile;

/**
 * Inbox containing multiple emails.
 * @author Jordan Jones
 */
public class EmailInbox {

    //Attributes
    private EmailInboxType emailInboxType;
    private Session session;
    private Folder folder;

    /**
     * Email inbox containing multiple emails.
     * @param emailAccount Email account the inbox is part of
     * @param emailInboxType Email inbox type
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws MessagingException Thrown if error with java mail server
     */
    public EmailInbox(@NotNull EmailAccount emailAccount,
        EmailInboxType emailInboxType)
        throws HostConnectionFailureException, MessagingException {
        setEmailInboxType(emailInboxType);
        setSession(emailAccount.getSession());
        setFolder(emailAccount, emailInboxType);
    }

    /**
     * Represents the type of inbox.
     */
    public enum EmailInboxType {
        INBOX, DRAFTS, SENT, SPAM, BIN
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
     * @param session Session of the inbox
     */
    private void setSession(Session session) {
        this.session = session;
    }

    /**
     * Sets the folder of the inbox.
     * @param emailAccount Email account the inbox is from
     * @param emailInboxType Type of inbox that is wanted
     * @throws EnumConstantNotPresentException Thrown if no code for enum
     * @throws HostConnectionFailureException Thrown if error with details
     * @throws MessagingException Thrown if error with server
     */
    private void setFolder(@NotNull EmailAccount emailAccount,
        EmailInboxType emailInboxType)
        throws HostConnectionFailureException, MessagingException {
        Store store = getSession().getStore();
        try {
            store.connect("imap.gmail.com",
                emailAccount.getEmailAddress().getAddress(),
                emailAccount.getApplicationPassword());
        } catch (MailConnectException mailConnectException) {
            throw new HostConnectionFailureException();
        }
        String inbox;
        switch (emailInboxType) {
            case INBOX:
                inbox = "INBOX";
                break;
            case DRAFTS:
                inbox = "[Gmail]/Drafts";
                break;
            case SENT:
                inbox = "[Gmail]/Sent Mail";
                break;
            case SPAM:
                inbox = "[Gmail]/Spam";
                break;
            case BIN:
                inbox = "[Gmail]/Bin";
                break;
            default:
                throw new EnumConstantNotPresentException(EmailInboxType.class,
                    emailInboxType.toString());
        }
        folder = store.getFolder(inbox);
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
     */
    private Folder getFolder() {
        return folder;
    }

    /**
     * Gets the length of the inbox folder.
     * @return Length of the inbox folder
     * @throws MessagingException Thrown if error with email server
     */
    public int getLength() throws MessagingException {
        return getFolder().getMessageCount();
    }

    /**
     * Gets the emails ids of the inbox.
     * @return Email ids of the inbox
     * @throws MessagingException Thrown if error with email server
     */
    public synchronized String[] getEmailIds() throws MessagingException {
        getFolder().open(Folder.READ_ONLY);
        String[] emailIds = new String[getFolder().getMessageCount()];
        for (int i = 0; i < emailIds.length; i++) {
            emailIds[i] = ((IMAPMessage) getFolder().getMessage(i + 1))
                .getMessageID();
        }
        getFolder().close();
        return emailIds;
    }

    /**
     * Gets the emails ids of the inbox from the given index to the given index.
     * @param from Index to start from
     * @param to Index to end at
     * @return Email ids of the inbox
     * @throws MessagingException Thrown if error with email server
     */
    public synchronized String[] getEmailIds(int from, int to)
        throws MessagingException {
        getFolder().open(Folder.READ_ONLY);
        String[] emailIds = new String[(to - from) + 1];
        for (int i = 0; i < emailIds.length; i++) {
            emailIds[i] = ((IMAPMessage) getFolder().getMessage(from + i + 1))
                .getMessageID();
        }
        getFolder().close();
        return emailIds;
    }

    /**
     * Gets the emails found in the inbox.
     * @return Emails in the inbox
     * @throws InvalidEmailAddressException Thrown if address format is invalid
     * @throws IOException Thrown if error with an email attachment
     * @throws MessagingException Thrown if error with java mail server
     */
    public synchronized Email[] getEmails()
        throws InvalidEmailAddressException, MessagingException, IOException {
        getFolder().open(Folder.READ_ONLY);
        Email[] emails = new Email[getFolder().getMessageCount()];
        for (int i = 0; i < emails.length; i++) {
            Email email = getEmail(i + 1);
            emails[i] = email;
        }
        getFolder().close();
        return emails;
    }

    /**
     * Gets all emails based on the corresponding message ids.
     * @param messageIds Message ids to get emails from
     * @return Emails based on the corresponding message ids
     * @throws MessagingException Thrown if error with mail server
     * @throws InvalidEmailAddressException Thrown if error with an address
     * @throws IOException Thrown if error with an attachment file
     */
    public synchronized Email[] getMinimumEmails(String @NotNull [] messageIds)
        throws MessagingException, InvalidEmailAddressException, IOException {
        getFolder().open(Folder.READ_ONLY);
        Email[] emails = new Email[messageIds.length];
        for (int i = 0; i < emails.length; i++) {
            emails[i] = getMinimumEmail(messageIds[i]);
        }
        getFolder().close();
        return emails;
    }

    /**
     * Gets an email with the given message index.
     * @param messageIndex Index of the email
     * @return Email of the given index
     * @throws InvalidEmailAddressException Thrown if error with an address
     * @throws IOException Thrown if error with a file
     * @throws MessagingException Thrown if error with java mail
     */
    @Contract("_ -> new")
    public @NotNull Email getEmail(int messageIndex)
        throws InvalidEmailAddressException, MessagingException, IOException {
        boolean isClosed = false;
        if (!getFolder().isOpen()) {
            isClosed = true;
            getFolder().open(Folder.READ_ONLY);
        }
        IMAPMessage message = (IMAPMessage) getFolder()
            .getMessage(messageIndex);
        EmailHeader emailHeader = getEmailHeader(message);
        Email email = new Email(message.getMessageID(), emailHeader,
            getEmailBody(message, emailHeader),
            message.getFlags().contains(Flags.Flag.SEEN));
        if (isClosed) {
            getFolder().close();
        }
        return email;
    }

    /**
     * Gets an email with the given message id.
     * @param messageId ID of the email's message
     * @return Email of the given message id
     * @throws InvalidEmailAddressException Thrown if error with an address
     * @throws IOException Thrown if error with a file
     * @throws MessagingException Thrown if error with java mail
     */
    @Contract("_ -> new")
    public @NotNull Email getEmail(String messageId)
        throws InvalidEmailAddressException, MessagingException, IOException {
        boolean isClosed = false;
        if (!getFolder().isOpen()) {
            isClosed = true;
            getFolder().open(Folder.READ_ONLY);
        }
        IMAPMessage message = (IMAPMessage) getFolder()
            .search(new MessageIDTerm(messageId))[0];
        EmailHeader emailHeader = getEmailHeader(message);
        Email email = new Email(message.getMessageID(), emailHeader,
            getEmailBody(message, emailHeader),
            message.getFlags().contains(Flags.Flag.SEEN));
        if (isClosed) {
            getFolder().close();
        }
        return email;
    }

    /**
     * Gets a minimum email with the given message id.
     * @param messageId ID of the email's message
     * @return Email of the given message id
     * @throws InvalidEmailAddressException Thrown if error with an address
     * @throws IOException Thrown if error with a file
     * @throws MessagingException Thrown if error with java mail
     */
    private @NotNull Email getMinimumEmail(String messageId)
        throws InvalidEmailAddressException, IOException, MessagingException {
        IMAPMessage message = (IMAPMessage) getFolder()
            .search(new MessageIDTerm(messageId))[0];
        EmailHeader emailHeader = getEmailHeader(message);
        return new Email(message.getMessageID(), emailHeader,
            getMinimumEmailBody(message, emailHeader),
            message.getFlags().contains(Flags.Flag.SEEN));
    }

    /**
     * Gets the email header of an email.
     * @param message Message containing the email header information
     * @return Email header of an email
     * @throws InvalidEmailAddressException Thrown if error with an address
     * @throws MessagingException Thrown if error with host server
     */
    @Contract("_ -> new")
    private @NotNull EmailHeader getEmailHeader(IMAPMessage message)
        throws InvalidEmailAddressException, MessagingException {
        EmailAddress sender = getEmailSender(message);
        HashMap<Message.RecipientType, EmailAddress[]> recipients
            = new HashMap<>();
        recipients.put(Message.RecipientType.TO, getEmailRecipients(
            message, Message.RecipientType.TO));
        recipients.put(Message.RecipientType.CC, getEmailRecipients(
            message, Message.RecipientType.CC));
        recipients.put(Message.RecipientType.BCC, getEmailRecipients(
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
    private EmailAddress getEmailSender(@NotNull IMAPMessage message)
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
    private EmailAddress @NotNull [] getEmailRecipients(
        @NotNull IMAPMessage message, Message.RecipientType recipientType)
        throws InvalidEmailAddressException, MessagingException {
        Address[] recipients = message.getRecipients(recipientType);
        EmailAddress[] addresses;
        if (recipients != null) {
            addresses = new EmailAddress[recipients.length];
            for (int j = 0; j < addresses.length; j++) {
                addresses[j] = new EmailAddress(((InternetAddress)
                    message.getRecipients(recipientType)[j]).getAddress());
            }
        } else {
            addresses = new EmailAddress[0];
        }
        return addresses;
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
    private @NotNull EmailBody getEmailBody(@NotNull IMAPMessage message,
        EmailHeader emailHeader) throws IOException, MessagingException {
        Object messageBody = message.getContent();
        String text = "";
        EmailTag[] emailTags = new EmailTag[0];
        ExtendedFile[] files = new ExtendedFile[0];
        if (messageBody.getClass() == MimeMultipart.class) {
            MimeMultipart messageMultipart = (MimeMultipart) messageBody;
            text = getEmailText(messageMultipart);
            emailTags = getEmailTags(text);
            files = getEmailFiles(messageMultipart);
        } else if (messageBody.getClass() == String.class) {
            text = (String) messageBody;
        }
        return new EmailBody(emailHeader,
            new EmailContents(text, emailTags), files);
    }

    /**
     * Gets a minimum email body of an email.
     * @param message Message containing the email body information
     * @param emailHeader Corresponding email header
     * @return Email body of an email
     * @throws IOException Thrown if error with an attachment
     * @throws MessagingException Thrown if error with host server
     */
    @Contract("_, _ -> new")
    private @NotNull EmailBody getMinimumEmailBody(@NotNull IMAPMessage message,
        EmailHeader emailHeader) throws IOException, MessagingException {
        Object messageBody = message.getContent();
        String text = "";
        int attachmentNumber = 0;
        if (messageBody.getClass() == MimeMultipart.class) {
            MimeMultipart messageMultipart = (MimeMultipart) messageBody;
            text = getEmailText(messageMultipart);
            for (int j = 0; j < messageMultipart.getCount() - 1; j++) {
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
                    attachmentNumber++;
                }
            }
        } else if (messageBody.getClass() == String.class) {
            text = (String) messageBody;
        }
        return new EmailBody(emailHeader, new EmailContents(text),
            attachmentNumber);
    }

    /**
     * Reads the text of an email.
     * @param messageBody Body of the email that contains the text
     * @return Text of an email
     * @throws IOException Thrown if error with body part content
     * @throws MessagingException Thrown if error with connecting to server
     */
    private String getEmailText(@NotNull MimeMultipart messageBody)
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
    private EmailTag @NotNull [] getEmailTags(String text) {
        Matcher matcher = Pattern.compile(EmailTag.getRegex()).matcher(text);
        HashSet<EmailTag> emailTags = new HashSet<>();
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
        ArrayList<EmailTag> orderedEmailTags = new ArrayList<>(emailTags);
        orderedEmailTags.sort((tag1, tag2)
            -> tag1.getName().compareToIgnoreCase(tag2.getName()));
        return orderedEmailTags.toArray(new EmailTag[0]);
    }

    /**
     * Reads the file attachments of an email.
     * @param messageMultipart Message body containing the files.
     * @return File attachments of an email
     * @throws IOException Thrown if an error with a file
     * @throws MessagingException Thrown if an error with connecting to server
     */
    private ExtendedFile @NotNull [] getEmailFiles(
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
     * @throws MessagingException Thrown if error with email server
     * @throws IOException Thrown if error with message attachments
     */
    public synchronized void addEmail(@NotNull Email email)
        throws MessagingException, IOException {
        getFolder().open(Folder.READ_WRITE);
        MimeMessage message = email.generateMessage(getSession());
        message.setFlag(Flags.Flag.DRAFT, true);
        getFolder().appendMessages(new MimeMessage[]{message});
        getFolder().close();
    }

    /**
     * Reads a given email in the inbox.
     * @param email Email to be read
     * @return True if email has been read for the first time
     * @throws MessagingException Thrown if error with the server.
     */
    public synchronized boolean readEmail(@NotNull Email email)
        throws MessagingException {
        if (!email.getIsRead()) {
            email.setIsRead(true);
            getFolder().open(Folder.READ_WRITE);
            getFolder().search(new MessageIDTerm(email.getMessageId()))[0]
                .setFlag(Flags.Flag.SEEN, true);
            getFolder().close();
            return true;
        }
        return false;
    }

    /**
     * Updates the given email in the inbox.
     * @param email Email in the given inbox
     * @throws IOException Thrown if error with an email attachment
     * @throws MessagingException Thrown if error with java mail server
     */
    public synchronized void updateEmail(Email email)
        throws IOException, MessagingException {
        deleteEmail(email);
        addEmail(email);
    }

    /**
     * Deletes the given email from the inbox.
     * @param email Email to be deleted
     * @throws MessagingException Thrown if error with java mail server
     */
    public synchronized void deleteEmail(@NotNull Email email)
        throws MessagingException {
        getFolder().open(Folder.READ_WRITE);
        getFolder().search(new MessageIDTerm(email.getMessageId()))[0]
            .setFlag(Flags.Flag.DELETED, true);
        getFolder().close();
    }

    /**
     * Moves an email from the inbox to a given inbox.
     * @param email Email to move
     * @param toInbox Inbox to move email to
     * @throws MessagingException Thrown if error with email server
     */
    public synchronized void moveEmail(@NotNull Email email,
        @NotNull EmailInbox toInbox) throws MessagingException {
        getFolder().open(Folder.READ_WRITE);
        getFolder().copyMessages(getFolder().search(
            new MessageIDTerm(email.getMessageId())), toInbox.getFolder());
        getFolder().close();
    }

}
