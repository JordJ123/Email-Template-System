package gui.screens.inbox;

import java.io.IOException;
import java.util.*;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import javax.mail.MessagingException;
import org.jetbrains.annotations.NotNull;
import email.EmailInbox;
import email.address.InvalidEmailAddressException;
import email.email.Email;
import file.directory.DirectoryNotFoundException;
import file.serializable.SerializableFile;
import file.serializable.SerializableFileNotFoundException;
import gui.Main;

/**
 * Controller for the drafts screen.
 * @author Jordan Jones
 */
public class InboxScreenController {

    //CONSTANTS
    public static final int EMAILS_PER_PAGE = 5;
    private static final int MAX_PAGE_BUTTONS = 5;
    private static final int REFRESH_DELAY_MILLISECONDS = 1000;

    //FXML Attributes
    @FXML private VBox emailBoxes;
    @FXML private Button leftButton;
    @FXML private HBox pageButtons;
    @FXML private Button rightButton;

    //Attributes
    private EmailInbox emailInbox;
    private final TreeMap<Pair<String, Date>, Pair<Email, HBox>> emails
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
    private int pageNumber = 1;
    private SerializableFile<Email[]> emailsFile;
    private Service<Void> refreshService;

    /**
     * Navigates to the previous set of emails.
     * @throws IOException Thrown if error with an email box's fxml file
     */
    @FXML
    private void onLeftButton() throws IOException {
        setPageNumber(getPageNumber() - 1);
        navigationalButton();
    }

    /**
     * Navigates to the next set of emails.
     * @throws IOException Thrown if error with an email box's fxml file
     */
    @FXML
    private void onRightButton() throws IOException {
        setPageNumber(getPageNumber() + 1);
        navigationalButton();
    }

    /**
     * Returns to the home screen.
     * @throws IOException Thrown if error with the Home Screen's fxml file
     */
    @FXML
    private void onReturnButton() throws IOException {
        getRefreshService().cancel();
        Main.HOME_SCREEN.load();
    }

    /**
     * Sets the controller's data.
     * @param emailInbox Inbox to show the emails for
     * @throws ClassNotFoundException Thrown if an email class can't be found
     * @throws IOException Thrown if error with an email box's fxml file
     */
    public void setController(EmailInbox emailInbox)
        throws ClassNotFoundException, IOException {
        setEmailInbox(emailInbox);
        setEmailsFile();
        loadEmailsLocally();
        setEmailBoxes();
        setPageButtons();
        setRightButton();
        setRefreshService();
    }

    /**
     * Sets the email boxes.
     * @throws IOException Thrown if error with an email box's fxml file
     */
    private void setEmailBoxes() throws IOException {
        getEmailBoxes().getChildren().clear();
        int from = ((getPageNumber() - 1) * EMAILS_PER_PAGE);
        int to = Math.min(from + EMAILS_PER_PAGE, getEmails().size());
        for (Pair<Email, HBox> email : Arrays.copyOfRange(getEmails().values()
            .toArray(new Pair[0]), from, to)) {
            setEmailBox(email.getKey());
        }
    }

    /**
     * Sets the left button at the bottom of the window.
     */
    private void setLeftButton() {
        leftButton.setVisible(getPageNumber() != 1);
    }

    /**
     * Sets the page buttons.
     */
    private void setPageButtons() {
        pageButtons.getChildren().clear();
        int from = Math.max(1, getPageNumber() - 2);
        int to = Math.min(from + (MAX_PAGE_BUTTONS - 1), (int) Math.ceil(
            getEmails().size() / (double) EMAILS_PER_PAGE));
        for (int i = from; i <= to; i++) {
            if (i != getPageNumber()) {
                Button button = new Button(Integer.toString(i));
                int finalI = i;
                button.setOnAction(actionEvent -> {
                    setPageNumber(finalI);
                    try {
                        navigationalButton();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                button.getStyleClass().add("btn-sm");
                button.getStyleClass().add("btn-primary");
                pageButtons.getChildren().add(button);
            } else {
                Label label = new Label(Integer.toString(i));
                label.setId("pageNumber");
                label.setTextFill(Color.WHITE);
                label.setStyle("-fx-text-fill: #fff;  -fx-fill: #fff; "
                    + "-fx-background-color: #f0ad4e; "
                    + "-fx-border-color: #eea236;");
                label.getStyleClass().add("btn-sm");
                pageButtons.getChildren().add(label);
            }

        }
    }

    /**
     * Sets the right buttons at the bottom of the window.
     */
    private void setRightButton() {
        rightButton.setVisible(getEmails().size()
            > (getPageNumber() * EMAILS_PER_PAGE));
    }

    /**
     * Sets email box onto the list.
     * @param email Email to create box for
     * @throws IOException Thrown if error with the email box fxml
     */
    private void setEmailBox(@NotNull Email email) throws IOException {
        getEmailBoxes().getChildren().add(emailBox(email));
    }

    /**
     * Sets email box onto the list with a given index.
     * @param index Index to place the email box
     * @param email Email to create box for
     * @throws IOException Thrown if error with the email box fxml
     */
    private void setEmailBox(int index, @NotNull Email email)
        throws IOException {
        getEmailBoxes().getChildren().add(index, emailBox(email));
    }

    /**
     * Sets the email inbox.
     * @param emailInbox Email inbox
     */
    private void setEmailInbox(EmailInbox emailInbox) {
        this.emailInbox = emailInbox;
    }

    /**
     * Sets the page number of the screen.
     * @param pageNumber Page number of the screen
     */
    private void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     * Sets the emails file to save the emails locally to.
     */
    private void setEmailsFile() {
        emailsFile = new SerializableFile<>(Main.getLoggedInAccount()
            .getInboxesPath() + "/" + getEmailInbox()
            .getEmailInboxType().toString().toLowerCase(Locale.ROOT) + ".ser");
    }

    /**
     * Sets the refresh service.
     */
    private void setRefreshService() {
        this.refreshService = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws InterruptedException {
                        boolean refreshing = true;
                        while (refreshing) {
                            try {
                                refresh();
                            } catch (Exception e) {
                                refreshing = false;
                            }
                            Thread.sleep(REFRESH_DELAY_MILLISECONDS);
                        }
                        return null;
                    }
                };
            }
        };
        refreshService.start();
    }

    /**
     * Gets the email boxes.
     * @return Email boxes
     */
    public VBox getEmailBoxes() {
        return emailBoxes;
    }

    /**
     * Gets the email inbox.
     * @return Email inbox
     */
    public EmailInbox getEmailInbox() {
        return emailInbox;
    }

    /**
     * Gets the email map.
     * @return Email map
     */
    public TreeMap<Pair<String, Date>, Pair<Email, HBox>> getEmails() {
        return emails;
    }

    /**
     * Get page number.
     * @return Page number
     */
    private int getPageNumber() {
        return pageNumber;
    }

    /**
     * Gets the file containing locally stored emails.
     * @return File containing locally stored emails
     */
    private SerializableFile<Email[]> getEmailsFile() {
        return emailsFile;
    }

    /**
     * Gets the refresh service.
     * @return Refresh service
     */
    public Service<Void> getRefreshService() {
        return refreshService;
    }

    /**
     * Saves the emails locally.
     * @throws IOException Thrown if error with serializable file
     */
    public void saveEmailsLocally() throws IOException {
        Email[] emails = new Email[getEmails().size()];
        int i = 0;
        for (Pair<Email, HBox> pair : getEmails().values()) {
            emails[i] = pair.getKey();
            i++;
        }
        try {
            getEmailsFile().serialize(emails);
        } catch (DirectoryNotFoundException directoryNotFoundException) {
            getEmailsFile().getDirectory().create();
            getEmailsFile().serialize(emails);
        }
    }

    /**
     * Loads the emails locally.
     * @throws ClassNotFoundException Thrown if an email class can't be found
     * @throws IOException Thrown if error with an email box's fxml file
     */
    private void loadEmailsLocally()
        throws ClassNotFoundException, IOException  {
        try {
            for (Email email : getEmailsFile().deserialize()) {
                getEmails().put(new Pair<>(email.getMessageId(), email
                    .getHeader().getReceivedDate()), new Pair<>(email, null));
            }
        } catch (SerializableFileNotFoundException fileNotFoundException) {
            //No files to load;
        }
    }

    /**
     * Code to run when a navigational button is clicked.
     * @throws IOException Thrown if error with an email box's fxml file
     */
    private void navigationalButton() throws IOException {
        setLeftButton();
        setPageButtons();
        setRightButton();
        setEmailBoxes();
    }

    /**
     * Refreshes the inbox.
     * @throws InvalidEmailAddressException Thrown if error with an address
     * @throws IOException Thrown if error with an email's attachments
     * @throws MessagingException Thrown if error with email server
     */
    private void refresh()
        throws InvalidEmailAddressException, IOException, MessagingException {
        HashSet<String> inboxIds = new HashSet<>(Arrays.asList(
            getEmailInbox().getEmailIds()));
        addEmails(inboxIds);
        deleteEmails(inboxIds);
    }

    /**
     * Adds emails if it needs to.
     * @param inboxIds Ids of emails in the inbox on the server
     * @throws InvalidEmailAddressException Thrown if error with an address
     * @throws IOException Thrown if error with an email's attachments
     * @throws MessagingException Thrown if error with email server
     */
    private void addEmails(@NotNull HashSet<String> inboxIds)
        throws InvalidEmailAddressException, IOException, MessagingException {
        HashSet<String> addIds = new HashSet<>();
        for (String newId : inboxIds) {
            boolean isNew = true;
            for (Pair<String, Date> pair : getEmails().keySet()) {
                if (pair.getKey().equals(newId)) {
                    isNew = false;
                    break;
                }
            }
            if (isNew) {
                addIds.add(newId);
            }
        }
        if (addIds.size() > 0) {
            Email[] emails = getEmailInbox().getMinimumEmails(
                addIds.toArray(new String[0]));
            Platform.runLater(() -> {
                try {
                    for (Email email : emails) {
                        Main.getLoggedInAccount().addToHistory(
                            email.getHeader().getAllAddresses());
                        Pair<String, Date> pair = new Pair<>(email
                            .getMessageId(), email.getHeader()
                            .getReceivedDate());
                        getEmails().put(pair, new Pair<>(email, null));
                    }
                    saveEmailsLocally();
                    HashMap<Integer, Email> emailHashMap = new HashMap<>();
                    for (Email email : emails) {
                        Pair<String, Date> pair = new Pair<>(email
                            .getMessageId(), email.getHeader()
                            .getReceivedDate());
                        int index = new ArrayList<>(getEmails().keySet())
                            .indexOf(pair);
                        if (index >= ((pageNumber - 1) * EMAILS_PER_PAGE)
                            && index < (pageNumber * EMAILS_PER_PAGE)) {
                            index -= ((pageNumber - 1) * EMAILS_PER_PAGE);
                            emailHashMap.put(index, email);
                        }
                    }
                    for (int integer : emailHashMap.keySet()) {
                        setEmailBox(integer, emailHashMap.get(integer));
                    }
                    if (getEmailBoxes().getChildren().size()
                        > EMAILS_PER_PAGE) {
                        getEmailBoxes().getChildren().subList(
                            EMAILS_PER_PAGE, EMAILS_PER_PAGE
                                + emailHashMap.size());
                    }
                    setRightButton();
                    setPageButtons();
                    setLeftButton();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Deletes emails if it needs to.
     * @param inboxIds Ids of emails in the inbox on the server
     */
    private void deleteEmails(@NotNull HashSet<String> inboxIds) {
        HashSet<String> deleteIds = new HashSet<>();
        for (Pair<String, Date> pair : getEmails().keySet()) {
            if (!inboxIds.contains(pair.getKey())) {
                deleteIds.add(pair.getKey());
            }
        }
        if (deleteIds.size() > 0) {
            Platform.runLater(() -> {
                try {
                    int replace = 0;
                    for (String deleteId : deleteIds) {
                        for (Pair<String, Date> pair : getEmails().keySet()) {
                            if (pair.getKey().equals(deleteId)) {
                                HBox emailBox = getEmails().get(pair)
                                    .getValue();
                                getEmails().remove(pair);
                                if (emailBox != null) {
                                    if (getEmailBoxes().getChildren().remove(
                                        emailBox)) {
                                        replace++;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    saveEmailsLocally();
                    int from = (getPageNumber() * EMAILS_PER_PAGE) - replace;
                    int to = Math.min(from + replace, getEmails().size());
                    for (int i = from; i < to; i++) {
                        setEmailBox((Email) getEmails().values().toArray(
                            new Pair[0])[i].getKey());
                    }
                    setRightButton();
                    setPageButtons();
                    setLeftButton();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Creates an email box.
     * @param email Email to create the email box for
     * @return Email box
     * @throws IOException Thrown if error with the email box fxml
     */
    private HBox emailBox(@NotNull Email email) throws IOException {
        Pair<String, Date> pair = new Pair<>(email.getMessageId(),
            email.getHeader().getReceivedDate());
        HBox emailBox = getEmails().get(pair).getValue();
        if (emailBox == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "EmailBox.fxml"));
            emailBox = fxmlLoader.load();
            ((EmailBoxController) fxmlLoader.getController())
                .setController(this, email);
            getEmails().replace(pair, new Pair<>(email, emailBox));
        }
        return emailBox;
    }

    /**
     * Adds the next email from the next page to the end of the email box list.
     * @throws IOException Thrown if error with the email box fxml
     */
    public void addNextEmailBox() throws IOException {
        if (getEmails().size() >= EMAILS_PER_PAGE * pageNumber) {
            setEmailBox((Email) getEmails().values().toArray(new Pair[0])
                [(EMAILS_PER_PAGE * pageNumber) - 1].getKey());
            setRightButton();
            setPageButtons();
            setLeftButton();
        }
    }

}
