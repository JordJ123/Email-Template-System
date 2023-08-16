package screens.create;

import email.EmailInbox;
import email.HostConnectionFailureException;
import email.email.Email;
import email.email.body.tag.EmailListTag;
import email.service.MissingEmailServiceInboxName;
import file.FileCanNotDeleteException;
import gui.Main;
import gui.components.EmailAddressesTextField;
import gui.components.ExtendedTextField;
import gui.fxml.create.CreateScreenController;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.Start;
import screens.ScreenTestSuite;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;

/**
 * Test Suite for the Create Screen.
 * (Test Google, Outlook and Other Accounts with 5 draft emails)
 * (Check sent [No Tags] and drafts [Hidden Tags, No Tags + Empty])
 * @author Jordan Jones
 */
public class CreateScreenEditTestSuite extends CreateScreenTestSuite {

    /**
     * Runs at the start of each test case (JavaFx Only).
     * @param stage Javafx Window
     * @throws FileCanNotDeleteException Thrown if accounts can't be deleted
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws IOException Thrown if error loading screen
     * @throws MessagingException Thrown if error with java mail
     * @throws MissingEmailServiceInboxName Thrown if service inbox name error
     */
    @Start
    private void start(Stage stage) throws FileCanNotDeleteException,
        HostConnectionFailureException, IOException, MessagingException,
        MissingEmailServiceInboxName {
        setup(stage, OTHER, false, true);
        Main.setLoggedInAccount(getEmailAccount());
        setEmail(null);
    }

    /**
     * Runs at the end of each test case (JavaFx Only).
     * @param robot Robot that uses the program
     */
    @AfterEach
    private void afterEach(@NotNull FxRobot robot) {
        cleanup(robot);
    }

    /**
     * FUNCTIONAL 1-33+35+37 + NON-FUNCTIONAL 1-15+17+19-22+25
     * Tests if the send button goes to the tag screen.
     * @param robot Robot that uses the program
     * @throws ClassNotFoundException Thrown if error with local email inbox
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws IOException Thrown if error loading local email inbox
     */
    @Test
    void onSendButtonWithTags(@NotNull FxRobot robot)
        throws ClassNotFoundException, HostConnectionFailureException,
        IOException {
        emailLoad();
        load(robot);
        sendButtonWithTags(robot, SEND_BUTTON_ID);
        robot.lookup(TAGS_SCREEN_ID);
    }

    /**
     * FUNCTIONAL 1-25+34-35+37 + NON-FUNCTIONAL 1-15+17+19-22+25
     * Tests if the send button sends the email correctly.
     * @param robot Robot that uses the program
     * @throws ClassNotFoundException Thrown if error with local email inbox
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws IOException Thrown if error loading local email inbox
     */
    @Test
    void onSendButtonNoTags(@NotNull FxRobot robot)
        throws ClassNotFoundException, HostConnectionFailureException,
        IOException {
        emailLoad();
        load(robot);
        sendButtonNoTags(robot, SEND_BUTTON_ID);
        Assertions.assertThat(Main.INBOX_SCREEN.getController()
            .getEmailInbox().getEmailInboxType()).isEqualTo(
            EmailInbox.EmailInboxType.DRAFTS);
    }

    /**
     * FUNCTIONAL 1-32+35-37 + NON-FUNCTIONAL 1-15+17+19-22+25
     * Tests if the send button goes to the tag screen.
     * @param robot Robot that uses the program
     * @throws ClassNotFoundException Thrown if error with local email inbox
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws IOException Thrown if error loading local email inbox
     */
    @Test
    void onDraftButtonWithTags(@NotNull FxRobot robot)
        throws ClassNotFoundException, HostConnectionFailureException,
        IOException {
        emailLoad();
        load(robot);
        sendButtonWithTags(robot, DRAFT_BUTTON_ID);
        Assertions.assertThat(Main.INBOX_SCREEN.getController()
            .getEmailInbox().getEmailInboxType()).isEqualTo(
            EmailInbox.EmailInboxType.DRAFTS);
    }

    /**
     * FUNCTIONAL 1-25+35-37 + NON-FUNCTIONAL 1-15+17+19-22+25
     * Tests if the send button sends the email correctly.
     * @param robot Robot that uses the program
     * @throws ClassNotFoundException Thrown if error with local email inbox
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws IOException Thrown if error loading local email inbox
     */
    @Test
    void onDraftButtonNoTags(@NotNull FxRobot robot)
        throws ClassNotFoundException, HostConnectionFailureException,
        IOException {
        emailLoad();
        load(robot);
        sendButtonNoTags(robot, DRAFT_BUTTON_ID);
        Assertions.assertThat(Main.INBOX_SCREEN.getController()
            .getEmailInbox().getEmailInboxType()).isEqualTo(
            EmailInbox.EmailInboxType.DRAFTS);
    }

    /**
     * NON-FUNCTIONAL 16 (Tag)
     * Tests if the tag button causes an error if the name is empty.
     * @param robot Robot that uses the program
     * @throws ClassNotFoundException Thrown if error with local email inbox
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws IOException Thrown if error loading local email inbox
     */
    @Test
    void onTagButtonEmptyName(@NotNull FxRobot robot)
        throws ClassNotFoundException, HostConnectionFailureException,
        IOException {
        emailLoad();
        load(robot);
        robot.clickOn(TAG_BUTTON_ID);
        Assertions.assertThat(robot.lookup(ERROR_LABEL_ID).queryAs(Label.class))
            .hasText(CreateScreenController.WHITESPACE_TAG_ERROR);
        ObservableList<Node> children
            = robot.lookup(TAG_BOXES_ID).queryAs(VBox.class).getChildren();
        int tags = 0;
        Email email = Main.CREATE_SCREEN.getController().getEmail();
        if (email != null) {
            tags = email.getBody().getContents().getTags().length;
        }
        Assertions.assertThat(children.size()).isEqualTo(tags);
    }

    /**
     * NON-FUNCTIONAL 16 (Tag List)
     * Tests if the tag list button causes an error if the name is empty.
     * @param robot Robot that uses the program
     * @throws ClassNotFoundException Thrown if error with local email inbox
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws IOException Thrown if error loading local email inbox
     */
    @Test
    void onTagListButtonEmptyName(@NotNull FxRobot robot)
        throws ClassNotFoundException, HostConnectionFailureException,
        IOException {
        emailLoad();
        load(robot);
        robot.clickOn(TAG_LIST_BUTTON_ID);
        Assertions.assertThat(robot.lookup(ERROR_LABEL_ID).queryAs(Label.class))
            .hasText(CreateScreenController.WHITESPACE_TAG_ERROR);
        ObservableList<Node> children
            = robot.lookup(TAG_BOXES_ID).queryAs(VBox.class).getChildren();
        int tags = 0;
        Email email = Main.CREATE_SCREEN.getController().getEmail();
        if (email != null) {
            tags = email.getBody().getContents().getTags().length;
        }
        Assertions.assertThat(children.size()).isEqualTo(tags);
    }

    /**
     * NON-FUNCTIONAL 18 (Tag)
     * Tests if the tag button causes an error if a tag already has the name.
     * @param robot Robot that uses the program
     * @throws ClassNotFoundException Thrown if error with local email inbox
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws IOException Thrown if error loading local email inbox
     */
    @Test
    void onTagButtonExistingTag(@NotNull FxRobot robot)
        throws ClassNotFoundException, HostConnectionFailureException,
        IOException {
        emailLoad();
        load(robot);
        ExtendedTextField tagNameTextField = robot.lookup(TAG_NAME_TEXT_FIELD)
            .queryAs(ExtendedTextField.class);
        tagNameTextField.setText(ScreenTestSuite.EMAIL_TAG.getName());
        robot.clickOn(TAG_LIST_BUTTON_ID);
        tagNameTextField.setText(ScreenTestSuite.EMAIL_TAG.getName());
        robot.clickOn(TAG_BUTTON_ID);
        Assertions.assertThat(robot.lookup(ERROR_LABEL_ID).queryAs(Label.class))
            .hasText(CreateScreenController.EXISTING_TAG_ERROR);
        ObservableList<Node> children
            = robot.lookup(TAG_BOXES_ID).queryAs(VBox.class).getChildren();
        int tags = 0;
        Email email = Main.CREATE_SCREEN.getController().getEmail();
        if (email != null) {
            tags = email.getBody().getContents().getTags().length;
        }
        Assertions.assertThat(children.size()).isEqualTo(tags + 1);
        Assertions.assertThat(((Button) ((HBox) children.get(
            children.size() - 1)).getChildren().get(0)).getText()).contains(
            EmailListTag.LIST_TAG_IDENTIFIER);
    }

    /**
     * NON-FUNCTIONAL 18 (Tag List)
     * Tests if the tag list button causes an error if the name is in use.
     * @param robot Robot that uses the program
     * @throws ClassNotFoundException Thrown if error with local email inbox
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws IOException Thrown if error loading local email inbox
     */
    @Test
    void onTagListButtonExistingTag(@NotNull FxRobot robot)
        throws ClassNotFoundException, HostConnectionFailureException,
        IOException {
        emailLoad();
        load(robot);
        ExtendedTextField tagNameTextField = robot.lookup(TAG_NAME_TEXT_FIELD)
            .queryAs(ExtendedTextField.class);
        tagNameTextField.setText(ScreenTestSuite.EMAIL_TAG.getName());
        robot.clickOn(TAG_BUTTON_ID);
        tagNameTextField.setText(ScreenTestSuite.EMAIL_TAG.getName());
        robot.clickOn(TAG_LIST_BUTTON_ID);
        Assertions.assertThat(robot.lookup(ERROR_LABEL_ID).queryAs(Label.class))
            .hasText(CreateScreenController.EXISTING_TAG_ERROR);
        ObservableList<Node> children
            = robot.lookup(TAG_BOXES_ID).queryAs(VBox.class).getChildren();
        int tags = 0;
        Email email = Main.CREATE_SCREEN.getController().getEmail();
        if (email != null) {
            tags = email.getBody().getContents().getTags().length;
        }
        Assertions.assertThat(children.size()).isEqualTo(tags + 1);
        Assertions.assertThat(((Button) ((HBox) children.get(
                children.size() - 1)).getChildren().get(0)).getText())
            .doesNotContain(EmailListTag.LIST_TAG_IDENTIFIER);
    }

    /**
     * NON-FUNCTIONAL 23 (Send Button)
     * Tests if the send button correctly shows the empty recipient error.
     * @param robot Robot that uses the program
     * @throws ClassNotFoundException Thrown if error with local email inbox
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws IOException Thrown if error loading local email inbox
     */
    @Test
    void onSendButtonEmptyValues(@NotNull FxRobot robot)
        throws ClassNotFoundException, HostConnectionFailureException,
        IOException {
        emailLoad();
        load(robot);
        robot.interact(() -> {
            robot.lookup(RECIPIENTS_TEXT_FIELD_ID)
                .queryAs(EmailAddressesTextField.class).setText("");
            robot.lookup(CARBON_COPIES_TEXT_FIELD_ID)
                .queryAs(EmailAddressesTextField.class).setText("");
            robot.lookup(BLIND_CARBON_COPIES_TEXT_FIELD_ID)
                .queryAs(EmailAddressesTextField.class).setText("");
        });
        robot.clickOn(SEND_BUTTON_ID);
        Assertions.assertThat(robot.lookup(ERROR_LABEL_ID).queryAs(Label.class))
            .hasText(CreateScreenController.EMPTY_RECIPIENTS_ERROR);
    }

    /**
     * NON-FUNCTIONAL 23 (Draft Button)
     * Tests if the drafts button still saves the email, even with empty values.
     * @param robot Robot that uses the program
     * @throws ClassNotFoundException Thrown if error with local email inbox
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws IOException Thrown if error loading local email inbox
     */
    @Test
    void onDraftButtonEmptyValues(@NotNull FxRobot robot)
        throws ClassNotFoundException, HostConnectionFailureException,
        IOException {
        emailLoad();
        load(robot);
        robot.interact(() -> {
            robot.lookup(RECIPIENTS_TEXT_FIELD_ID)
                .queryAs(EmailAddressesTextField.class).setText("");
            robot.lookup(CARBON_COPIES_TEXT_FIELD_ID)
                .queryAs(EmailAddressesTextField.class).setText("");
            robot.lookup(BLIND_CARBON_COPIES_TEXT_FIELD_ID)
                .queryAs(EmailAddressesTextField.class).setText("");
        });
        robot.clickOn(DRAFT_BUTTON_ID);
        robot.lookup(TAGS_SCREEN_ID);
    }

    /**
     * NON-FUNCTIONAL 24 (Send Button)
     * Tests if the send button correctly shows the invalid recipient error.
     * @param robot Robot that uses the program
     * @throws ClassNotFoundException Thrown if error with local email inbox
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws IOException Thrown if error loading local email inbox
     */
    @Test
    void onSendButtonInvalidRecipient(@NotNull FxRobot robot)
        throws ClassNotFoundException, HostConnectionFailureException,
        IOException {
        emailLoad();
        load(robot);
        robot.interact(() -> robot.lookup(RECIPIENTS_TEXT_FIELD_ID)
            .queryAs(EmailAddressesTextField.class).setText(INVALID_RECIPIENT));
        robot.clickOn(SEND_BUTTON_ID);
        Assertions.assertThat(robot.lookup(ERROR_LABEL_ID).queryAs(Label.class))
            .hasText(String.format(CreateScreenController.INVALID_ADDRESS_ERROR,
                INVALID_RECIPIENT, Message.RecipientType.TO
                    .toString().toUpperCase()));
    }


    /**
     * NON-FUNCTIONAL 24 (Tags Button)
     * Tests if the draft button correctly shows the invalid recipient error.
     * @param robot Robot that uses the program
     * @throws ClassNotFoundException Thrown if error with local email inbox
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws IOException Thrown if error loading local email inbox
     */
    @Test
    void onDraftButtonInvalidRecipient(@NotNull FxRobot robot)
        throws ClassNotFoundException, HostConnectionFailureException,
        IOException {
        emailLoad();
        load(robot);
        robot.interact(() -> robot.lookup(RECIPIENTS_TEXT_FIELD_ID)
            .queryAs(EmailAddressesTextField.class).setText(INVALID_RECIPIENT));
        robot.clickOn(DRAFT_BUTTON_ID);
        Assertions.assertThat(robot.lookup(ERROR_LABEL_ID).queryAs(Label.class))
            .hasText(String.format(CreateScreenController.INVALID_ADDRESS_ERROR,
                INVALID_RECIPIENT, Message.RecipientType.TO
                    .toString().toUpperCase()));
    }

    /**
     * Loads the email.
     * @throws ClassNotFoundException Thrown if error with local email inbox
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws IOException Thrown if error loading local email inbox
     */
    protected void emailLoad()
        throws ClassNotFoundException, HostConnectionFailureException,
        IOException {
        EmailInbox emailInbox = Main.getLoggedInAccount().getInbox(
            EmailInbox.EmailInboxType.DRAFTS);
        emailInbox.load();
        if (emailInbox.getEmails().size() == 0) {
            emailInbox.refresh();
        }
        setEmail(emailInbox.getEmails().values().toArray(new Email[0])[
            emailInbox.getEmails().size() - 1]);
    }

}
