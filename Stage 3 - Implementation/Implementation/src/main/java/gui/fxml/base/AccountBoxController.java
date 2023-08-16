package gui.fxml.base;

import email.EmailAccount;
import file.FileCanNotDeleteException;
import file.html.HTMLVoidElement;
import gui.Main;
import gui.components.ComponentHandler;
import gui.components.ExtendedTextField;
import gui.components.ExtendedVBox;
import gui.fxml.FXMLController;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

/**
 * Controller for an account box.
 * @author Jordan Jones
 */
public class AccountBoxController extends FXMLController {

    //CONSTANTS
    public static final String NICKNAME_ERROR = "Enter a name";
    private static final String DEFAULT_NICKNAME = "Email";
    private static final String TOOLTIP_MESSAGE
        = "%s\nRight click for settings";

    //FXML Attributes
    @FXML private ExtendedVBox accountBox;
    @FXML private Label nicknameLabel;
    @FXML private HBox nicknameBox;
    @FXML private ExtendedTextField nicknameTextField;
    @FXML private Label emailAddressLabel;
    @FXML private Button nicknameAcceptButton;
    @FXML private ContextMenu contextMenu;


    //Static Attributes
    private static AccountBoxController currentSelectedAccountBoxController;
    private static AccountBoxController currentContextMenuAccountBoxController;

    //Attributes
    private EmailAccount emailAccount;

    /**
     * Sets the current selected account box controller.
     * @param currentSelectedAccountBoxController Current selected controller
     */
    private static void setCurrentSelectedAccountBoxController(
        AccountBoxController currentSelectedAccountBoxController) {
        AccountBoxController.currentSelectedAccountBoxController
            = currentSelectedAccountBoxController;
    }

    /**
     * Sets the account box controller with the context menu selected.
     * @param currentContextMenuAccountBoxController Account box controller
     */
    private static void setCurrentContextMenuAccountBoxController(
        AccountBoxController currentContextMenuAccountBoxController) {
        AccountBoxController.currentContextMenuAccountBoxController
            = currentContextMenuAccountBoxController;
    }

    /**
     * Gets the current selected account box controller.
     * @return Current selected account box controller
     */
    private static AccountBoxController
        getCurrentSelectedAccountBoxController() {
        return currentSelectedAccountBoxController;
    }

    /**
     * Gets the account box controller with the context menu selected.
     * @return Account box controller with the context menu selected
     */
    private static AccountBoxController
        getCurrentContextMenuAccountBoxController() {
        return currentContextMenuAccountBoxController;
    }

    /**
     * Gets the current selected account.
     */
    public static void deselectCurrentSelectedAccount() {
        if (getCurrentSelectedAccountBoxController() != null) {
            getCurrentSelectedAccountBoxController().deselectAccount();
        }
    }

    /**
     * Selects the given account.
     * @param mouseEvent Mouse click event
     */
    @FXML
    private void onAccountBoxButton(@NotNull MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            getAccountBox().clickEffect();
            getNicknameLabel().setStyle(
                HTMLVoidElement.BOLD.getJavaFxStyle());
            getEmailAddressLabel().setStyle(
                HTMLVoidElement.BOLD.getJavaFxStyle());
            selectAccount();
            Main.BASE_SCREEN.getController().getOptionsBox().setDisable(true);
            PauseTransition transition = new PauseTransition(Duration.seconds(
                ComponentHandler.CLICK_EFFECT_DURATION
                    + ComponentHandler.CLICK_EFFECT_AFTERMATH));
            transition.setOnFinished(event -> {
                Main.BASE_SCREEN.getController().clear();
                Main.BASE_SCREEN.getController().getOptionsBox()
                    .setDisable(false);
            });
            transition.playFromStart();
        }
    }

    /**
     * Accepts the given nickname.
     * @throws IOException Thrown if error with saving email account
     */
    @FXML
    private void onNicknameBoxAcceptButton() throws IOException {
        setCurrentContextMenuAccountBoxController(null);
        if (!getNicknameTextField().isEmpty()) {
            setNicknameLabel(getNicknameTextField().getText());
            getEmailAccount().getEmailAddress().setNickname(
                getNicknameTextField().getText());
            getEmailAccount().saveLogin();
            getNicknameBox().setVisible(false);
        } else {
            getNicknameTextField().displayError(NICKNAME_ERROR);
        }
    }

    /**
     * Cancels the nickname change.
     */
    @FXML
    private void onNicknameBoxCancelButton() {
        setCurrentContextMenuAccountBoxController(null);
        getNicknameAcceptButton().setDefaultButton(false);
        getNicknameBox().setVisible(false);
    }

    /**
     * Shows the context menu.
     * @param event Context menu click event
     */
    @FXML
    private void onContextMenu(@NotNull ContextMenuEvent event) {
        if (getCurrentContextMenuAccountBoxController() != this) {
            getContextMenu().show(getAccountBox(), event.getScreenX(),
                event.getScreenY());
            getNicknameAcceptButton().setDefaultButton(true);
        }
    }

    /**
     * Opens up the change nickname input.
     */
    @FXML
    private void onChangeNicknameContextMenuAction() {
        if (getCurrentContextMenuAccountBoxController() != null) {
            getCurrentContextMenuAccountBoxController()
                .onNicknameBoxCancelButton();
        }
        setCurrentContextMenuAccountBoxController(this);
        getNicknameBox().setVisible(true);
        getNicknameTextField().setText(getNicknameLabel().getText());
    }

    /**
     * Logs the account out.
     * @throws FileCanNotDeleteException Thrown if account can't be deleted
     * @throws IOException Thrown if account can't be deleted
     */
    @FXML
    private void onLogoutContextMenuAction() throws FileCanNotDeleteException,
        IOException {
        if (Main.getLoggedInAccount() == getEmailAccount()) {
            deselectCurrentSelectedAccount();
        }
        Main.BASE_SCREEN.getController().removeAccountBox(getEmailAccount());
        getEmailAccount().delete();
    }

    /**
     * Sets the data of the account box.
     * @param emailAccount Email account of the account box
     */
    public void setController(@NotNull EmailAccount emailAccount) {
        setNicknameLabel(emailAccount.getEmailAddress().getNickname());
        setEmailAddressLabel(emailAccount.getEmailAddress().getAddress());
        setTooltip(emailAccount.getEmailAddress().getAddress());
        setEmailAccount(emailAccount);
    }

    /**
     * Sets the nickname of the account box.
     * @param nickname Nickname of the account box
     */
    private void setNicknameLabel(String nickname) {
        getNicknameLabel().setText(Objects.requireNonNullElse(nickname,
            DEFAULT_NICKNAME));
    }

    /**
     * Sets the email address of the account box.
     * @param emailAddress Email address of the account box
     */
    private void setEmailAddressLabel(String emailAddress) {
        emailAddressLabel.setText(emailAddress);
    }

    /**
     * Sets the email address of the tooltip.
     * @param emailAddress Email address of the account box
     */
    private void setTooltip(String emailAddress) {
        Tooltip tooltip = new Tooltip(String.format(TOOLTIP_MESSAGE,
            emailAddress));
        tooltip.setFont(new Font(14));
        tooltip.setShowDelay(Duration.millis(100));
        tooltip.setStyle("-fx-font-size: 14px;");
        Tooltip.install(getAccountBox(), tooltip);
    }

    /**
     * Sets the email account of the account box.
     * @param emailAccount Email account of the account box
     */
    private void setEmailAccount(EmailAccount emailAccount) {
        this.emailAccount = emailAccount;
    }

    /**
     * Gets the button of the box.
     * @return Button of the box
     */
    private ExtendedVBox getAccountBox() {
        return accountBox;
    }

    /**
     * Gets the nickname label.
     * @return Nickname label
     */
    private Label getNicknameLabel() {
        return nicknameLabel;
    }

    /**
     * Get nickname box.
     * @return Nickname box
     */
    private HBox getNicknameBox() {
        return nicknameBox;
    }

    /**
     * Gets the extended text field.
     * @return Extended text field
     */
    private ExtendedTextField getNicknameTextField() {
        return nicknameTextField;
    }

    /**
     * Gets the email address label.
     * @return Email address label
     */
    private Label getEmailAddressLabel() {
        return emailAddressLabel;
    }

    /**
     * Get nickname accept button.
     * @return Nickname accept button
     */
    private Button getNicknameAcceptButton() {
        return nicknameAcceptButton;
    }

    /**
     * Gets the context menu.
     * @return Context menu
     */
    private ContextMenu getContextMenu() {
        return contextMenu;
    }

    /**
     * Gets the email account of the account box.
     * @return Email account of the account box
     */
    private EmailAccount getEmailAccount() {
        return emailAccount;
    }

    /**
     * Selects the account box.
     */
    public void selectAccount() {
        deselectCurrentSelectedAccount();
        setCurrentSelectedAccountBoxController(this);
        getAccountBox().setOnMouseClicked(null);
        getAccountBox().setOnMouseEntered(null);
        onHoverExit();
        getNicknameLabel().setStyle(HTMLVoidElement.BOLD.getJavaFxStyle());
        getEmailAddressLabel().setStyle(HTMLVoidElement.BOLD.getJavaFxStyle());
        Main.setLoggedInAccount(getEmailAccount());
        Main.BASE_SCREEN.getController().getOptionsBox().setDisable(false);
        Main.BASE_SCREEN.getController().clear();
    }

    /**
     * Deselects the account box.
     */
    public void deselectAccount() {
        setCurrentSelectedAccountBoxController(null);
        getAccountBox().setOnMouseEntered(mouseEvent -> onHoverEnter());
        getAccountBox().setOnMouseClicked(this::onAccountBoxButton);
        getNicknameLabel().setStyle("");
        getEmailAddressLabel().setStyle("");
        Main.setLoggedInAccount(null);
        Main.BASE_SCREEN.getController().getOptionsBox().setDisable(true);
    }

}
