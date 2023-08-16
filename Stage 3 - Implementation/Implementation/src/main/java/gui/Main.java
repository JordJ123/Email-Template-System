package gui;

import email.BacklogAction;
import email.EmailAccount;
import gui.fxml.FXMLComponent;
import gui.fxml.FXMLScreen;
import gui.fxml.base.BaseScreenController;
import gui.fxml.create.CreateScreenController;
import gui.fxml.inbox.InboxScreenController;
import gui.fxml.login.LoginScreenController;
import gui.fxml.tags.TagsScreenController;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Starts the program.
 * VM Options (--module-path "C:\Program Files\Java\javafx-sdk-18.0.2\lib"
 *      --add-modules=javafx.controls,javafx.fxml,javafx.graphics,javafx.web)
 * @author Jordan Jones
 */
public class Main extends Application {

    //sending authentication

    //CONSTANTS
    public static final String ICON_FILE_NAME = "logo.png";
    public static final int LARGE_FONT_SIZE = 24;
    public static final int SMALL_FONT_SIZE = 18;
    public static final FXMLScreen<BaseScreenController> BASE_SCREEN
        = new FXMLScreen<>("base/BaseScreen", "Email Template System");
    public static final FXMLComponent<LoginScreenController> LOGIN_SCREEN
        = new FXMLComponent<>("login/LoginScreen");
    public static final FXMLComponent<CreateScreenController> CREATE_SCREEN
        = new FXMLComponent<>("create/CreateScreen");
    public static final FXMLComponent<TagsScreenController> TAGS_SCREEN
        = new FXMLComponent<>("tags/TagsScreen");
    public static final FXMLComponent<InboxScreenController> INBOX_SCREEN
        = new FXMLComponent<>("inbox/InboxScreen");

    //Static Attributes
    //Gmail
        //jordan.jones090101@gmail.com + uesovppmernvlucd
        //jordan.joneswork090101@gmail.com + pdjpzuoiibhnfntc
    //Outlook
        //jordan.jones090101@gmail.com + zglhekllgfbuxkbd
        //jordan.jones090101@outlook.com
        //jordan.joneswork090101@outlook.com + oveipbkrwyppwvzp
    //Test
        //testforets@gmail.com + tsunkjwntnhqcdxp
        //testforets@outlook.com + zrsmllwzcebagnog
        //testforets@zohomail.eu + 6fH9sNqQPbqf
        //SMTP = smtp.zoho.eu + IMAP = imap.zoho.eu

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
        FXMLScreen.setPrimaryStage(primaryStage, ICON_FILE_NAME);
        FXMLScreen.getPrimaryStage().setMaximized(true);
        BASE_SCREEN.load();
        BacklogAction.loadBacklogActions();
    }

}
