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
import javafx.scene.control.ComboBox;
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
import email.HostConnectionFailureException;
import email.address.InvalidEmailAddressException;
import gui.Main;
import gui.screens.Screen;

/**
 * Test Suite for the Create Screen.
 * @author Jordan Jones
 */
class CreateScreenNewEmailTestSuite extends CreateScreenTestSuite {

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
        Main.CREATE_SCREEN.getController().setController(null);
        Screen.getPrimaryStage().toFront();
    }

    /**
     * Tests if the send button sends the email correctly.
     * @param robot Robot that uses the program
     */
    @Test
    void onSendButton(@NotNull FxRobot robot) {
        sendButtonNoTags(robot, SEND_BUTTON_ID);
        Assertions.assertThat(Screen.getPrimaryStage().getTitle()).isEqualTo(
            Main.HOME_SCREEN.getWindowTitle());
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
        sendButtonNoTags(robot, DRAFT_BUTTON_ID);
        Assertions.assertThat(Screen.getPrimaryStage().getTitle()).isEqualTo(
            Main.HOME_SCREEN.getWindowTitle());
    }

    /**
     * Tests if the draft button goes to the home screen, even with tags.
     * @param robot Robot that uses the program
     */
    @Test
    void onDraftButtonWithTags(@NotNull FxRobot robot) {
        sendButtonWithTags(robot, DRAFT_BUTTON_ID);
        Assertions.assertThat(Screen.getPrimaryStage().getTitle()).isEqualTo(
            Main.HOME_SCREEN.getWindowTitle());
    }

    /**
     * Tests if the drafts button still saves the email, even with empty values.
     * @param robot Robot that uses the program
     */
    @Test
    void onDraftButtonEmptyValues(@NotNull FxRobot robot) {
        robot.clickOn(DRAFT_BUTTON_ID);
        Assertions.assertThat(Screen.getPrimaryStage().getTitle()).isEqualTo(
            Main.HOME_SCREEN.getWindowTitle());
    }

    /**
     * Tests if the cancel button goes to the correct screen.
     * @param robot Robot that uses the program
     */
    @Test
    void onCancelButton(@NotNull FxRobot robot) {
        robot.clickOn(CANCEL_BUTTON_ID);
        Assertions.assertThat(Screen.getPrimaryStage().getTitle())
            .isEqualTo(Main.HOME_SCREEN.getWindowTitle());
    }

    /**
     * Tests if the given send button works correctly with no tags.
     * @param robot Robot that uses the program
     * @param buttonId Send button that is being clicked
     */
    protected void sendButtonNoTags(@NotNull FxRobot robot, String buttonId) {
        inputValues(robot);
        robot.clickOn(buttonId);
    }

    /**
     * Tests if the given send button works correctly with tags.
     * @param robot Robot that uses the program
     * @param buttonId Send button that is being clicked
     */
    protected void sendButtonWithTags(@NotNull FxRobot robot, String buttonId) {
        inputValues(robot);
        ExtendedTextField tagNameTextField = robot.lookup(TAG_NAME_TEXT_FIELD)
            .queryAs(ExtendedTextField.class);
        ObservableList<Node> children
            = robot.lookup(TAG_BOXES_ID).queryAs(VBox.class).getChildren();
        tagNameTextField.setText(EMAIL_TAG.getName());
        robot.clickOn(TAG_BUTTON_ID);
        Assertions.assertThat(((Button) ((HBox) children.get(
            children.size() - 1)).getChildren().get(0)).getText())
            .doesNotContain(EmailListTag.LIST_TAG_IDENTIFIER);
        robot.interact(() -> ((Button) ((HBox) children.get(
            children.size() - 1)).getChildren().get(0)).fire());
        tagNameTextField.setText(EMAIL_LIST_TAG.getName().split(
            EmailListTag.getRegex())[0]);
        robot.clickOn(TAG_LIST_BUTTON_ID);
        Assertions.assertThat(((Button) ((HBox) children.get(
            children.size() - 1)).getChildren().get(0)).getText()).contains(
            EmailListTag.LIST_TAG_IDENTIFIER);
        robot.interact(() -> ((Button) ((HBox) children.get(
            children.size() - 1)).getChildren().get(0)).fire());
        robot.clickOn(buttonId);
    }

    /**
     * Inputs values into the fields.
     * @param robot Robot that uses the program
     */
    protected void inputValues(@NotNull FxRobot robot) {
        robot.interact(() -> {
            robot.lookup(RECIPIENTS_TEXT_FIELD_ID)
                .queryAs(EmailAddressesTextField.class).addText(RECIPIENTS);
            robot.lookup(CARBON_COPIES_TEXT_FIELD_ID)
                .queryAs(EmailAddressesTextField.class).addText(RECIPIENTS);
            robot.lookup(BLIND_CARBON_COPIES_TEXT_FIELD_ID)
                .queryAs(EmailAddressesTextField.class).addText(RECIPIENTS);
            robot.lookup(SUBJECT_TEXT_FIELD_ID).queryAs(ExtendedTextField.class)
                .addText(SUBJECT);
            ExtendedTextArea extendedTextArea = robot.lookup(
                CONTENTS_TEXT_AREA_ID).queryAs(ExtendedTextArea.class);
            extendedTextArea.replaceText(CONTENTS);
            selectText(extendedTextArea);
            robot.lookup(FONT_FAMILY_COMBO_BOX_ID).queryAs(ComboBox.class)
                .getSelectionModel().selectFirst();
            robot.lookup(FONT_SIZE_COMBO_BOX_ID).queryAs(ComboBox.class)
                .getSelectionModel().selectFirst();
        });
        robot.clickOn(BOLD_BUTTON_ID);
        robot.clickOn(ITALIC_BUTTON_ID);
        robot.clickOn(STRIKETHROUGH_BUTTON_ID);
        robot.clickOn(SUBSCRIPT_BUTTON_ID);
        robot.clickOn(SUPERSCRIPT_BUTTON_ID);
        robot.clickOn(UNDERLINE_BUTTON_ID);
        robot.clickOn(BULLET_POINT_BUTTON_ID);
    }

}
