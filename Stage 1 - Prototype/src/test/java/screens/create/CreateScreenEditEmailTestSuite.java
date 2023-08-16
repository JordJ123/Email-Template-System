package screens.create;

import java.io.IOException;

import email.email.body.tag.EmailListTag;
import file.FileCanNotDeleteException;
import gui.components.EmailAddressesTextField;
import gui.components.ExtendedTextArea;
import gui.components.ExtendedTextField;
import gui.screens.create.CreateScreenController;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.mail.MessagingException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.Start;
import email.EmailInbox;
import email.HostConnectionFailureException;
import email.address.InvalidEmailAddressException;
import gui.Main;
import gui.screens.Screen;

/**
 * Test Suite for the Create Screen.
 * @author Jordan Jones
 */
class CreateScreenEditEmailTestSuite extends CreateScreenTestSuite {

    /**
     * Runs at the start of each test case (JavaFx Only).
     * @param stage Javafx Window
     * @throws HostConnectionFailureException Thrown if error connecting to host
     * @throws FileCanNotDeleteException Thrown if a files can't be deleted
     * @throws InvalidEmailAddressException Thrown if error with address format
     * @throws IOException Thrown if error with saving the email account (INT)
     * @throws MessagingException Thrown if error with java mail
     */
    @Start
    private void start(Stage stage) throws FileCanNotDeleteException,
        HostConnectionFailureException, InvalidEmailAddressException,
        IOException, MessagingException {
        setLoggedInAccount(false);
        Screen.setPrimaryStage(stage, Main.ICON_FILE_NAME);
        Main.CREATE_SCREEN.load();
        Main.CREATE_SCREEN.getController().setController(
            Main.getLoggedInAccount().getInbox(
                EmailInbox.EmailInboxType.DRAFTS).getEmail(1));
        Screen.getPrimaryStage().toFront();
    }

    /**
     * Tests if the send button sends the email correctly.
     * @param robot Robot that uses the program
     */
    @Test
    void onSendButton(@NotNull FxRobot robot) {
        robot.clickOn(SEND_BUTTON_ID);
        Assertions.assertThat(Main.INBOX_SCREEN.getController()
            .getEmailInbox().getEmailInboxType()).isEqualTo(
            EmailInbox.EmailInboxType.DRAFTS);
    }

    /**
     * Tests if the send button goes to the tag screen.
     * @param robot Robot that uses the program
     */
    @Test
    void onSendButtonWithTags(@NotNull FxRobot robot) {
        sendButtonWithTags(robot, SEND_BUTTON_ID);
        Assertions.assertThat(Screen.getPrimaryStage().getTitle()).isEqualTo(
            Main.TAGS_SCREEN.getWindowTitle());
    }

    /**
     * Tests if the send button correctly shows the empty recipient error.
     * @param robot Robot that uses the program
     */
    @Test
    void onSendButtonEmptyValues(@NotNull FxRobot robot) {
        robot.interact(() -> {
            robot.lookup(RECIPIENTS_TEXT_FIELD_ID)
                .queryAs(EmailAddressesTextField.class).setText("");
            robot.lookup(CARBON_COPIES_TEXT_FIELD_ID)
                .queryAs(EmailAddressesTextField.class).setText("");
            robot.lookup(BLIND_CARBON_COPIES_TEXT_FIELD_ID)
                .queryAs(EmailAddressesTextField.class).setText("");
            robot.lookup(SUBJECT_TEXT_FIELD_ID).queryAs(ExtendedTextField.class)
                .setText("");
            ExtendedTextArea extendedTextArea = robot.lookup(
                CONTENTS_TEXT_AREA_ID).queryAs(ExtendedTextArea.class);
            extendedTextArea.replaceText("");
        });
        robot.clickOn(SEND_BUTTON_ID);
        Assertions.assertThat(robot.lookup(ERROR_LABEL_ID).queryAs(Label.class))
            .hasText(CreateScreenController.EMPTY_RECIPIENTS_ERROR);
    }

    /**
     * Tests if the draft button drafts the email correctly.
     * @param robot Robot that uses the program
     */
    @Test
    void onDraftButton(@NotNull FxRobot robot) {
        robot.clickOn(DRAFT_BUTTON_ID);
        Assertions.assertThat(Main.INBOX_SCREEN.getController()
            .getEmailInbox().getEmailInboxType()).isEqualTo(
            EmailInbox.EmailInboxType.DRAFTS);
    }

    /**
     * Tests if the draft button goes to the home screen, even with tags.
     * @param robot Robot that uses the program
     */
    @Test
    void onDraftButtonWithTags(@NotNull FxRobot robot) {
        sendButtonWithTags(robot, DRAFT_BUTTON_ID);
        Assertions.assertThat(Main.INBOX_SCREEN.getController()
            .getEmailInbox().getEmailInboxType()).isEqualTo(
            EmailInbox.EmailInboxType.DRAFTS);
    }

    /**
     * Tests if the drafts button still saves the email, even with empty values.
     * @param robot Robot that uses the program
     */
    @Test
    void onDraftButtonEmptyValues(@NotNull FxRobot robot) {
        robot.clickOn(DRAFT_BUTTON_ID);
        Assertions.assertThat(Main.INBOX_SCREEN.getController()
            .getEmailInbox().getEmailInboxType()).isEqualTo(
            EmailInbox.EmailInboxType.DRAFTS);
    }

    /**
     * Tests if the cancel button goes to the correct screen.
     * @param robot Robot that uses the program
     */
    @Test
    void onCancelButton(@NotNull FxRobot robot) {
        robot.clickOn(CANCEL_BUTTON_ID);
        Assertions.assertThat(Main.INBOX_SCREEN.getController()
            .getEmailInbox().getEmailInboxType()).isEqualTo(
            EmailInbox.EmailInboxType.DRAFTS);
    }

    /**
     * Tests if the given send button works correctly with tags.
     * @param robot Robot that uses the program
     * @param buttonId Send button that is being clicked
     */
    protected void sendButtonWithTags(@NotNull FxRobot robot, String buttonId) {
        robot.interact(() -> robot.lookup(RECIPIENTS_TEXT_FIELD_ID)
            .queryAs(EmailAddressesTextField.class).setText(RECIPIENTS));
        ExtendedTextField tagNameTextField = robot.lookup(TAG_NAME_TEXT_FIELD)
            .queryAs(ExtendedTextField.class);
        ObservableList<Node> children
            = robot.lookup(TAG_BOXES_ID).queryAs(VBox.class).getChildren();
        tagNameTextField.setText(EMAIL_TAG.getName());
        robot.clickOn(TAG_BUTTON_ID);
        Assertions.assertThat(((Button) ((HBox) children.get(
            children.size() - 1)).getChildren().get(0)).getText()).isEqualTo(
            EMAIL_TAG.getName());
        tagNameTextField.setText(EMAIL_LIST_TAG.getName().split(
            EmailListTag.getRegex())[0]);
        robot.clickOn(TAG_LIST_BUTTON_ID);
        checkError(robot);
        Assertions.assertThat(((Button) ((HBox) children.get(
            children.size() - 1)).getChildren().get(0)).getText()).isEqualTo(
            EMAIL_LIST_TAG.getName());
        robot.clickOn(((HBox) children.get(children.size() - 1)).getChildren()
            .get(0));
        robot.clickOn(buttonId);
    }

}
