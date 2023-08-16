package gui;

import java.util.Objects;
import javafx.application.Application;
import javafx.stage.Stage;
import email.EmailAccount;
import file.directory.Directory;
import gui.screens.Screen;
import gui.screens.create.CreateScreenController;
import gui.screens.home.HomeScreenController;
import gui.screens.inbox.InboxScreenController;
import gui.screens.login.LoginScreenController;
import gui.screens.tags.TagsScreenController;
import gui.screens.view.ViewScreenController;

/**
 * Starts the program.
 * VM Options (--module-path "C:\Program Files\Java\javafx-sdk-18.0.2\lib"
 *      --add-modules=javafx.controls,javafx.fxml,javafx.graphics,javafx.web)
 * @author Jordan Jones
 */
public class Main extends Application {

    //CONSTANTS
    public static final String ICON_FILE_NAME = "logo.png";
    public static final Screen<CreateScreenController> CREATE_SCREEN
        = new Screen<>("create/CreateScreen", "Create Email");
    public static final Screen<HomeScreenController> HOME_SCREEN
        = new Screen<>("home/HomeScreen", "Home");
    public static final Screen<InboxScreenController> INBOX_SCREEN
        = new Screen<>("inbox/InboxScreen", "Inbox");
    public static final Screen<LoginScreenController> LOGIN_SCREEN
        = new Screen<>("login/LoginScreen", "Gmail Login");
    public static final Screen<ViewScreenController> SELECT_SCREEN
        = new Screen<>("select/SelectScreen", "Select Email Account");
    public static final Screen<TagsScreenController> TAGS_SCREEN
        = new Screen<>("tags/TagsScreen", "Enter Tag Values");
    public static final Screen<ViewScreenController> VIEW_SCREEN
        = new Screen<>("view/ViewScreen", "View Email");

    //Static Attributes
    //jordan.jones090101@gmail.com + uesovppmernvlucd
    //jordan.joneswork090101@gmail.com + pdjpzuoiibhnfntc
    private static EmailAccount loggedInAccount;

    /**
     * Sets the logged in account.
     * @param emailAccount Email account to be logged in
     */
    public static void setLoggedInAccount(EmailAccount emailAccount) {
        Main.loggedInAccount = emailAccount;
    }

    /**
     * Gets the logged in account.
     * @return Logged-in email account
     */
    public static EmailAccount getLoggedInAccount() {
        return loggedInAccount;
    }

    /**
     * Launches javafx.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Loads up the first screen.
     * @param primaryStage Window to contain a given screen
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Screen.setPrimaryStage(primaryStage, ICON_FILE_NAME);
        if (Objects.requireNonNull(new Directory(EmailAccount.ACCOUNTS_PATH)
            .listFiles()).length > 0) {
            SELECT_SCREEN.load();
        } else {
            LOGIN_SCREEN.load();
        }
    }
}
