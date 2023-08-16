package gui.fxml.inbox;

import email.EmailInbox;
import email.HostConnectionFailureException;
import email.email.Email;
import gui.Main;
import gui.fxml.FXMLComponent;
import gui.fxml.FXMLController;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.*;

/**
 * Controller for the drafts screen.
 * @author Jordan Jones
 */
public class InboxScreenController extends FXMLController {

    //CONSTANTS
    public static final int EMAILS_PER_PAGE = 5;
    private static final int MAX_PAGE_BUTTONS = 5;
    private static final int REFRESH_DELAY_MILLISECONDS = 1000;

    //FXML Attributes
    @FXML private VBox emailBoxes;
    @FXML private Button leftButton;
    @FXML private HBox pageButtons;
    @FXML private Button rightButton;
    @FXML private Pane viewBoxSpace;
    @FXML private VBox sidebar;
    @FXML private Label loadLabel;

    //Attributes
    private EmailInbox emailInbox;
    private int pageNumber = 1;
    private HashMap<Email, HBox> emailHBoxes = new HashMap<>();
    private Service<Void> refreshService;
    private ViewBoxController viewBoxController;

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
     * Sets the controller's data.
     * @param emailInboxType Inbox to show the emails for
     * @throws ClassNotFoundException Thrown if an email class can't be found
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws IOException Thrown if error with an email box's fxml file
     */
    public void setController(EmailInbox.EmailInboxType emailInboxType)
        throws ClassNotFoundException, HostConnectionFailureException,
        IOException {
        setEmailInbox(Main.getLoggedInAccount().getInbox(emailInboxType));
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
        int to = Math.min(from + EMAILS_PER_PAGE, getEmailInbox().getEmails()
            .size());
        for (Email email : Arrays.copyOfRange(getEmailInbox().getEmails()
            .values().toArray(new Email[0]), from, to)) {
            setEmailBox(email);
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
            getEmailInbox().getEmails().size() / (double) EMAILS_PER_PAGE));
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
                button.setOnMouseEntered(mouseEvent -> onHoverEnter());
                button.setOnMouseExited(mouseEvent -> onHoverExit());
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
        rightButton.setVisible(getEmailInbox().getEmails().size()
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
     * @throws ClassNotFoundException Thrown if an email class can't be found
     * @throws IOException Thrown if error with an email box's fxml file
     */
    private void setEmailInbox(@NotNull EmailInbox emailInbox)
        throws ClassNotFoundException, IOException {
        this.emailInbox = emailInbox;
        emailInbox.load();
    }

    /**
     * Sets the page number of the screen.
     * @param pageNumber Page number of the screen
     */
    private void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     * Sets the refresh service.
     */
    private void setRefreshService() {
        if (getEmailBoxes().getChildren().size() != 0) {
            sidebar.getChildren().remove(loadLabel);
        }
        this.refreshService = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() {
                        while (!isCancelled()) {
                            System.out.println(getEmailInbox().getEmailInboxType());
                            try {
                                Pair<Email[], String[]> update
                                    = getEmailInbox().refresh();
                                refreshServiceAdd(update.getKey());
                                refreshServiceDelete(update.getValue());
                                Platform.runLater(() -> {
                                    if (sidebar.getChildren()
                                        .contains(loadLabel)) {
                                        sidebar.getChildren()
                                            .remove(loadLabel);
                                    }
                                });
                                Thread.sleep(REFRESH_DELAY_MILLISECONDS);
                            } catch (InterruptedException exception) {
                                return null;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        System.out.println(getEmailInbox().getEmailInboxType()
                            + " END");
                        return null;
                    };
                };
            }
        };
        refreshService.start();
    }

    /**
     * Sets the current view box controller.
     * @param viewBoxController View box controller
     */
    public void setViewBoxController(ViewBoxController viewBoxController) {
        this.viewBoxController = viewBoxController;
    }

    /**
     * Gets the email boxes.
     * @return Email boxes
     */
    public VBox getEmailBoxes() {
        return emailBoxes;
    }

    /**
     * Gets view box space.
     * @return View box space
     */
    public Pane getViewBoxSpace() {
        return viewBoxSpace;
    }

    /**
     * Gets the email inbox.
     * @return Email inbox
     */
    public EmailInbox getEmailInbox() {
        return emailInbox;
    }

    /**
     * Get page number.
     * @return Page number
     */
    private int getPageNumber() {
        return pageNumber;
    }

    /**
     * Gets the email boxes.
     * @return Email boxes
     */
    private HashMap<Email, HBox> getEmailHBoxes() {
        return emailHBoxes;
    }


    /**
     * Gets the refresh service.
     * @return Refresh service
     */
    public Service<Void> getRefreshService() {
        return refreshService;
    }

    /**
     * Gets the view box controller.
     * @return View box controller
     */
    public ViewBoxController getViewBoxController() {
        return viewBoxController;
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
     * Creates an email box.
     * @param email Email to create the email box for
     * @return Email box
     * @throws IOException Thrown if error with the email box fxml
     */
    private HBox emailBox(@NotNull Email email) throws IOException {
        HBox emailBox = getEmailHBoxes().get(email);
        if (emailBox == null) {
            FXMLComponent<EmailBoxController> emailBoxLoader
                = new FXMLComponent<>("inbox/EmailBox");
            emailBox = (HBox) emailBoxLoader.load();
            emailBoxLoader.getController().setController(this, email);
            getEmailHBoxes().put(email, emailBox);
        }
        return emailBox;
    }

    /**
     * Adds the next email from the next page to the end of the email box list.
     * @throws IOException Thrown if error with the email box fxml
     */
    public void addNextEmailBox() throws IOException {
        if (getEmailInbox().getEmails().size()
            >= EMAILS_PER_PAGE * getPageNumber()) {
            setEmailBox(getEmailInbox().getEmails().values().toArray(
                new Email[0])[(EMAILS_PER_PAGE * getPageNumber()) - 1]);
            setRightButton();
            setPageButtons();
            setLeftButton();
        }
    }

    /**
     * Add section of the refresh service.
     * @param emails Emails to add to the email boxes
     */
    private void refreshServiceAdd(Email[] emails) {
        Platform.runLater(() -> {
            try {
                HashMap<Integer, Email> emailHashMap = new HashMap<>();
                for (Email email : emails) {
                    Pair<String, Date> pair = new Pair<>(email.getMessageId(),
                        email.getHeader().getReceivedDate());
                    int index = new ArrayList<>(getEmailInbox().getEmails()
                        .keySet()).indexOf(pair);
                    if (index >= ((getPageNumber() - 1) * EMAILS_PER_PAGE)
                        && index < (getPageNumber() * EMAILS_PER_PAGE)) {
                        index -= ((getPageNumber() - 1) * EMAILS_PER_PAGE);
                        emailHashMap.put(index, email);
                    }
                }
                for (int integer : emailHashMap.keySet()) {
                    setEmailBox(integer, emailHashMap.get(
                        integer));
                }
                while (getEmailBoxes().getChildren().size() > EMAILS_PER_PAGE) {
                    getEmailBoxes().getChildren().remove(
                        getEmailBoxes().getChildren().size() - 1);
                }
                setRightButton();
                setPageButtons();
                setLeftButton();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Delete section of the refresh service.
     * @param deleteIds Ids of the emails to delete
     */
    private void refreshServiceDelete(String[] deleteIds) {
        Platform.runLater(() -> {
            try {
                int replace = 0;
                for (String deleteId : deleteIds) {
                    for (Email email : getEmailHBoxes().keySet()) {
                        if (email.getMessageId().equals(deleteId)) {
                            HBox emailBox = getEmailHBoxes().get(email);
                            getEmailHBoxes().remove(email);
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
                int from = (getPageNumber() * EMAILS_PER_PAGE) - replace;
                int to = Math.min(from + replace,
                    getEmailInbox().getEmails().size());
                for (int i = from; i < to; i++) {
                    setEmailBox(getEmailInbox().getEmails().values().toArray(
                        new Email[0])[i]);
                }
                setRightButton();
                setPageButtons();
                setLeftButton();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Cancels the refresh service.
     */
    public void refreshServiceCancel() {
        if (getRefreshService() != null) {
            getRefreshService().cancel();
        }
    }

}
