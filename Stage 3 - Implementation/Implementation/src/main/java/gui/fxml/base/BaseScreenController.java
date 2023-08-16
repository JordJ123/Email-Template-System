package gui.fxml.base;

import email.EmailAccount;
import email.EmailInbox;
import email.HostConnectionFailureException;
import email.address.EmailAddress;
import email.address.InvalidEmailAddressException;
import email.service.EmailService;
import email.service.NonSupportedEmailService;
import file.FileCanNotDeleteException;
import file.directory.Directory;
import file.html.HTMLElement;
import file.text.TextFile;
import gui.Main;
import gui.components.ComponentHandler;
import gui.components.ExtendedLabel;
import gui.components.ExtendedVBox;
import gui.fxml.FXMLComponent;
import gui.fxml.FXMLController;
import gui.fxml.inbox.InboxScreenController;
import gui.fxml.login.LoginScreenController;
import javafx.animation.PauseTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

/**
 * Controller for the login screen.
 * @author Jordan Jones
 */
public class BaseScreenController extends FXMLController {

    //FXML Attributes
    @FXML private VBox accountsBox;
    @FXML private VBox optionsBox;
    @FXML private ExtendedLabel loginScreenButton;
    @FXML private ExtendedLabel createButton;
    @FXML private ExtendedLabel defaultInboxButton;
    @FXML private ExtendedLabel draftsInboxButton;
    @FXML private ExtendedLabel sentInboxButton;
    @FXML private ExtendedLabel spamInboxButton;
    @FXML private ExtendedLabel binInboxButton;
    @FXML private HBox screenSpace;

    //Attributes
    private final HashMap<EmailAccount, ExtendedVBox> accountBoxes
        = new HashMap<>();
    private Pair<ExtendedLabel,EventHandler> currentButton;

    /**
     * Runs on initialization.
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws InvalidEmailAddressException Thrown if address has wrong format
     * @throws IOException Thrown if error with the fxml
     * @throws NonSupportedEmailService Thrown if not supported email service
     */
    @FXML
    private void initialize() throws HostConnectionFailureException,
        InvalidEmailAddressException, IOException, NonSupportedEmailService {
        Directory directory = new Directory(EmailAccount.ACCOUNTS_PATH);
        if (!directory.exists()) {
            directory.create();
        }
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            Scanner scanner = new TextFile(String.format(
                EmailAccount.LOGIN_PATH, file.getName())).getContents();
            String address = scanner.next();
            scanner.useDelimiter("'");
            scanner.next();
            String nickname = scanner.next();
            scanner.useDelimiter(" ");
            scanner.next();
            EmailAddress emailAddress;
            if (!nickname.equals("null")) {
                emailAddress = new EmailAddress(address, nickname);
            } else {
                emailAddress = new EmailAddress(address);
            }
            String password = scanner.next();
            String next = scanner.next();
            EmailService emailService = EmailService.EMAIL_SERVICES
                .get(next);
            if (emailService == null) {
                emailService = new EmailService("Other", next, scanner.next());
            }
            addAccountBox(new EmailAccount(Integer.parseInt(file.getName()),
                emailAddress, password, emailService, true));
            scanner.close();
        }
    }

    /**
     * Opens the login screen.
     */
    @FXML
    private void onLoginButton() {
        AccountBoxController.deselectCurrentSelectedAccount();
        loadScreen(getLoginScreenButton(), Main.LOGIN_SCREEN, null);
    }

    /**
     * Opens the create screen.
     */
    @FXML
    private void onCreateButton() {
        loadScreen(getCreateButton(), Main.CREATE_SCREEN, () -> {
            try {
                Main.CREATE_SCREEN.getController().setController(null);
            } catch (FileCanNotDeleteException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Opens the default inbox screen.
     */
    @FXML
    private void onDefaultInboxButton() {
        loadScreen(getDefaultInboxButton(), Main.INBOX_SCREEN, () -> {
            try {
                inboxScreen(EmailInbox.EmailInboxType.INBOX);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Opens the drafts inbox screen.
     */
    @FXML
    private void onDraftsInboxButton() {
        loadScreen(getDraftsInboxButton(), Main.INBOX_SCREEN, () -> {
            try {
                inboxScreen(EmailInbox.EmailInboxType.DRAFTS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Opens the sent inbox screen.
     */
    @FXML
    private void onSentInboxButton() {
        loadScreen(getSentInboxButton(), Main.INBOX_SCREEN, () -> {
            try {
                inboxScreen(EmailInbox.EmailInboxType.SENT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Opens the spam inbox screen.
     */
    @FXML
    private void onSpamInboxButton() {
        loadScreen(getSpamInboxButton(), Main.INBOX_SCREEN, () -> {
            try {
                inboxScreen(EmailInbox.EmailInboxType.SPAM);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Opens the bin inbox screen.
     */
    @FXML
    private void onBinInboxButton() {
        loadScreen(getBinInboxButton(), Main.INBOX_SCREEN, () -> {
            try {
                inboxScreen(EmailInbox.EmailInboxType.BIN);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Sets the current button.
     * @param currentButton Current button
     */
    private void setCurrentButton(@NotNull ExtendedLabel currentButton) {
        unsetCurrentButton();
        this.currentButton = new Pair<>(currentButton,
            currentButton.getOnMouseClicked());
        currentButton.setOnMouseClicked(null);
        currentButton.setOnMouseEntered(null);
        onHoverExit();
    }

    /**
     * Gets the accounts box.
     * @return Account box
     */
    public VBox getAccountsBox() {
        return accountsBox;
    }

    /**
     * Gets the options box.
     * @return Options box
     */
    public VBox getOptionsBox() {
        return optionsBox;
    }

    /**
     * Gets the login button.
     * @return Login button
     */
    private ExtendedLabel getLoginScreenButton() {
        return loginScreenButton;
    }

    /**
     * Gets the create button.
     * @return Create button
     */
    private ExtendedLabel getCreateButton() {
        return createButton;
    }

    /**
     * Gets the default inbox button.
     * @return Default inbox button
     */
    private ExtendedLabel getDefaultInboxButton() {
        return defaultInboxButton;
    }

    /**
     * Gets the drafts inbox button.
     * @return Drafts inbox button
     */
    private ExtendedLabel getDraftsInboxButton() {
        return draftsInboxButton;
    }

    /**
     * Gets the sent inbox button.
     * @return Sent inbox button
     */
    private ExtendedLabel getSentInboxButton() {
        return sentInboxButton;
    }

    /**
     * Gets the spam inbox button.
     * @return Spam inbox button
     */
    private ExtendedLabel getSpamInboxButton() {
        return spamInboxButton;
    }

    /**
     * Gets the bin inbox button.
     * @return Bin inbox button
     */
    private ExtendedLabel getBinInboxButton() {
        return binInboxButton;
    }

    /**
     * Gets the screen space pane.
     * @return Screen space pane
     */
    private Pane getScreenSpace() {
        return screenSpace;
    }

    /**
     * Gets the account boxes.
     * @return Account boxes
     */
    private HashMap<EmailAccount, ExtendedVBox> getAccountBoxes() {
        return accountBoxes;
    }

    /**
     * Gets the current button.
     * @return Current button
     */
    private Pair<ExtendedLabel, EventHandler> getCurrentButton() {
        return currentButton;
    }

    /**
     * Adds an email box for the given email account.
     * @param emailAccount Email account to add email account box for
     * @return Controller for the created account box.
     * @throws IOException Thrown if error with the fxml
     */
    public AccountBoxController addAccountBox(EmailAccount emailAccount)
        throws IOException {
        FXMLComponent<AccountBoxController> fxmlComponent
            = new FXMLComponent<>("base/AccountBox");
        ExtendedVBox accountBox = (ExtendedVBox) fxmlComponent.load();
        getAccountsBox().getChildren().add(accountBox);
        AccountBoxController controller = fxmlComponent.getController();
        controller.setController(emailAccount);
        getAccountBoxes().put(emailAccount, accountBox);
        EmailAccount.addToHistory(
            new EmailAddress[]{emailAccount.getEmailAddress()});
        return controller;
    }

    /**
     * Removes the account box for the given email account.
     * @param emailAccount Email account to remove box for
     */
    public void removeAccountBox(EmailAccount emailAccount) {
        ExtendedVBox accountBox = getAccountBoxes().get(emailAccount);
        getAccountsBox().getChildren().remove(accountBox);
        getAccountBoxes().remove(emailAccount);
    }

    /**
     * Loads the given screen into the screen space with a button effect.
     * @param button Button that has been clicked
     * @param screen Screen to load
     * @param afterLoad Code to run after the screen has loaded
     */
    private void loadScreen(@NotNull ExtendedLabel button,
        @NotNull FXMLComponent<?> screen, Runnable afterLoad) {
        removeFunctionality();
        setCurrentButton(button);
        button.setStyle(button.getStyle() + HTMLElement.BOLD.getJavaFxStyle());
        button.clickEffect();
        PauseTransition transition = new PauseTransition(Duration.seconds(
            ComponentHandler.CLICK_EFFECT_DURATION
                + ComponentHandler.CLICK_EFFECT_AFTERMATH));
        transition.setOnFinished(event -> {
            try {
                loadScreen(screen, afterLoad);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        transition.playFromStart();
    }

    /**
     * Loads the given screen into the screen space.
     * @param screen Screen to load
     * @param afterLoad Code to run after the screen has loaded
     * @throws IOException Thrown if error with loading the fxml file
     */
    public void loadScreen(@NotNull FXMLComponent<?> screen, Runnable afterLoad)
        throws IOException {
        removeFunctionality();
        getScreenSpace().getChildren().clear();
        getScreenSpace().getChildren().add(screen.load());
        if (afterLoad != null) {
            afterLoad.run();
        }
    }

    /**
     * Opens a given inbox after a specific inbox button has been clicked.
     * @param emailInboxType Type of the email inbox to open
     * @throws ClassNotFoundException Thrown if error with loading emails
     * @throws HostConnectionFailureException Thrown if error with file
     * attachments
     * @throws IOException Thrown if error with file attachments
     */
    public void inboxScreen(EmailInbox.EmailInboxType emailInboxType)
        throws ClassNotFoundException, HostConnectionFailureException,
        IOException {
        Main.INBOX_SCREEN.getController().setController(emailInboxType);
    }

    /**
     * Closes the current screen.
     */
    public void clear() {
        unsetCurrentButton();
        removeFunctionality();
        getScreenSpace().getChildren().clear();
    }

    /**
     * Unsets the current button.
     */
    private void unsetCurrentButton() {
        if (getCurrentButton() != null) {
            getCurrentButton().getKey().setStyle("");
            getCurrentButton().getKey().setOnMouseEntered(mouseEvent
                -> onHoverEnter());
            getCurrentButton().getKey().setOnMouseClicked(
                this.currentButton.getValue());
        }
    }

    /**
     * Remove functionality.
     */
    private void removeFunctionality() {
        InboxScreenController bsc = Main.INBOX_SCREEN.getController();
        if (bsc != null) {
            bsc.refreshServiceCancel();
        }
        LoginScreenController nlsc = Main.LOGIN_SCREEN.getController();
        if (nlsc != null) {
            nlsc.getLoginButton().setDefaultButton(false);
        }
    }

}
