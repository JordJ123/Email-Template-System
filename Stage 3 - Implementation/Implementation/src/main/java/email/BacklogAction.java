package email;

import email.email.Email;
import file.serializable.SerializableFile;
import gui.fxml.FXMLScreen;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;

public class BacklogAction implements Serializable {

    //Static Attributes
    private static HashSet<BacklogAction> backlogActions;

    //Attributes
    private Action action;
    private int emailAccountId;
    private EmailInbox.EmailInboxType emailInboxType;
    private Email email;

    /**
     * Creates a backlog action.
     * @param action Action being backlogged
     * @param emailAccount Email account the action is for
     * @param emailInboxType Email inbox type
     * @param email Email the action is for
     * @throws IOException Thrown if error with saving backlog actions
     */
    public BacklogAction(Action action, EmailAccount emailAccount,
        EmailInbox.EmailInboxType emailInboxType, Email email)
        throws IOException {
        setEmailInboxType(emailInboxType);
        setBacklogAction(action, emailAccount, email);
    }

    /**
     * Creates a backlog action.
     * @param action Action being backlogged
     * @param emailAccount Email account the action is for
     * @param email Email the action is for
     * @throws IOException Thrown if error with saving backlog actions
     */
    public BacklogAction(Action action, EmailAccount emailAccount,
        Email email) throws IOException {
        setBacklogAction(action, emailAccount, email);
    }

    /**
     * Possible actions.
     */
    public enum Action {
        READ, SPAM, NOT_SPAM, RESTORE, DELETE
    }

    /**
     * Gets the backlog actions.
     * @return Backlog actions
     */
    private static HashSet<BacklogAction> getBacklogActions() {
        return backlogActions;
    }

    /**
     * Adds a backlog action.
     * @param backlogAction Backlog action
     * @throws IOException Thrown if error with saving backlog actions
     */
    private static void addBacklogAction(BacklogAction backlogAction)
        throws IOException {
        if (getBacklogActions().add(backlogAction)) {
            new SerializableFile<HashSet<BacklogAction>>(
                EmailAccount.BACKLOG_PATH).serialize(getBacklogActions());
        }
        if (getBacklogActions().size() > 0) {
            runBacklogActions();
        }
    }

    /**
     * Loads the backlog actions.
     * @throws ClassNotFoundException Thrown if file is for a non-local class
     * @throws IOException Thrown if error deserializing the file
     */
    public static void loadBacklogActions()
        throws ClassNotFoundException, IOException {
        SerializableFile<HashSet<BacklogAction>> backlogSer =
            new SerializableFile<>(EmailAccount.BACKLOG_PATH);
        if (backlogSer.exists()) {
            backlogActions = backlogSer.deserialize();
        } else {
            backlogActions = new HashSet<>();
        }
        if (backlogActions.size() > 0) {
            runBacklogActions();
        }
    }

    /**
     * Runs the backlog actions.
     */
    private static void runBacklogActions() {
        new Thread(() -> {
            Iterator<BacklogAction> iterator = getBacklogActions()
                .iterator();
            while (getBacklogActions().size() > 0) {
                try {
                    while (iterator.hasNext()) {
                        if (FXMLScreen.getIsClosed()) {
                            return;
                        }
                        BacklogAction backlogAction = iterator.next();
                        switch (backlogAction.getAction()) {
                            case READ:
                                EmailAccount.readEmailAccount(backlogAction
                                        .getEmailAccountId()).getInbox(
                                        backlogAction.getEmailInboxType())
                                    .readEmail(backlogAction.getEmail());
                                break;
                            case SPAM:
                                EmailAccount.readEmailAccount(backlogAction
                                    .getEmailAccountId()).spamEmail(
                                    backlogAction.getEmail());
                                break;
                            case NOT_SPAM:
                                EmailAccount.readEmailAccount(backlogAction
                                    .getEmailAccountId()).notSpamEmail(
                                    backlogAction.getEmail());
                                break;
                            case RESTORE:
                                EmailAccount.readEmailAccount(backlogAction
                                    .getEmailAccountId()).restoreEmail(
                                    backlogAction.getEmail());
                            case DELETE:
                                EmailAccount.readEmailAccount(backlogAction
                                        .getEmailAccountId()).deleteEmail(
                                            backlogAction.getEmail(),
                                            backlogAction.getEmailInboxType());
                                break;
                            default:
                                throw new EnumConstantNotPresentException(
                                    Action.class,
                                    backlogAction.getAction().toString());
                        }
                        iterator.remove();
                    }
                } catch (ArrayIndexOutOfBoundsException aioobException) {
                    //Email does not exist anymore
                    iterator.remove();
                } catch (HostConnectionFailureException hcfException) {
                    //Just keep trying until host has been connected
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Sets the backlog action attributes.
     * @param action Action being backlogged
     * @param emailAccount Email account the action is for
     * @param email Email the action is for
     * @throws IOException Thrown if error saving the backlog action
     */
    private void setBacklogAction(Action action, EmailAccount emailAccount,
        Email email) throws IOException {
        setAction(action);
        setEmailAccountId(emailAccount);
        setEmail(email);
        addBacklogAction(this);
    }

    /**
     * Sets the action.
     * @param action Action being backlogged
     */
    private void setAction(Action action) {
        this.action = action;
    }

    /**
     * Sets the email account.
     * @param emailAccount Email account
     */
    private void setEmailAccountId(EmailAccount emailAccount) {
        this.emailAccountId = emailAccount.getId();
    }

    /**
     * Sets the email inbox type.
     * @param emailInboxType Email inbox type
     */
    private void setEmailInboxType(EmailInbox.EmailInboxType emailInboxType) {
        this.emailInboxType = emailInboxType;
    }

    /**
     * Sets the email.
     * @param email Email
     */
    private void setEmail(Email email) {
        this.email = email;
    }

    /**
     * Gets the action.
     * @return Action
     */
    private Action getAction() {
        return action;
    }

    /**
     * Gets the email account id.
     * @return Email account id
     */
    private int getEmailAccountId() {
        return emailAccountId;
    }

    /**
     * Gets the email inbox type.
     * @return Email inbox type
     */
    private EmailInbox.EmailInboxType getEmailInboxType() {
        return emailInboxType;
    }

    /**
     * Gets the email.
     * @return Email
     */
    private Email getEmail() {
        return email;
    }

    /**
     * Checks if the given object is the same backlog action.
     * @param obj Object to compare to
     * @return True if the backlog actions are the same
     */
    public boolean equals(Object obj) {
        if (obj instanceof BacklogAction) {
            BacklogAction objBa = (BacklogAction) obj;
            return getAction().equals(objBa.getAction())
                && getEmail().getMessageId().equals(
                    objBa.getEmail().getMessageId());
        }
        return false;
    }

    /**
     * Gets the hash code of the backlog action.
     * @return Hash code of the backlog action
     */
    @Override
    public int hashCode() {
        return Objects.hash(getAction(), getEmail().getMessageId());
    }

}
