package screens.tags;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.mail.Message;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import email.address.EmailAddress;
import file.html.HTMLVoidElement;
import gui.Main;
import gui.components.EmailAddressesTextField;
import gui.components.ExtendedTextArea;
import gui.components.ExtendedTextField;
import gui.screens.Screen;
import gui.screens.tags.TagBoxController;
import gui.screens.tags.TagsScreenController;
import screens.ScreenTestSuite;
import screens.create.CreateScreenTestSuite;

/**
 * Test Suite for the Tag Screen.
 * @author Jordan Jones
 */
public class TagsScreenTestSuite extends ScreenTestSuite {

    //IDS
    private static final String EMAIL_ADDRESS_COMBO_BOX_ID
        = "#emailAddressComboBox";
    private static final String TAG_BOXES_ID = "#tagBoxes";
    private static final String TAG_VALUE_TEXT_FIELD_ID
        = "#tagValueTextField";
    private static final String LIST_TAG_VALUE_TEXT_FIELDS_BOX_ID
        = "#listTagValueTextFieldsBox";
    private static final String ADD_TAG_VALUE_TEXT_FIELD_BUTTON
        = "#addTagValueTextFieldButton";
    private static final String LIST_TAG_VALUE_TEXT_FIELD_ID
        = "#listTagValueTextField";
    private static final String ERROR_LABEL_ID = "#errorLabel";
    private static final String SEND_BUTTON_ID = "#sendButton";
    private static final String CANCEL_BUTTON_ID = "#cancelButton";

    //TEST VALUES
    private static final String TAG_VALUE = "Jordan";
    private static final String LIST_TAG_VALUE_1 = "Programming";
    private static final String LIST_TAG_VALUE_2 = "Mathematics";
    private static final String LIST_TAG_VALUE_3 = "Theory";

    /**
     * Tests if changing the vale in the email address combo box work correctly.
     * @param robot Robot that uses the program
     */
    @Test
    void onEmailAddressComboBox(@NotNull FxRobot robot) {
        ExtendedTextField tagValueTextField = robot.lookup(
            TAG_VALUE_TEXT_FIELD_ID).queryAs(ExtendedTextField.class);
        robot.interact(() -> {
            tagValueTextField.setText("Jordan");
            robot.lookup(EMAIL_ADDRESS_COMBO_BOX_ID).queryAs(ComboBox.class)
                .getSelectionModel().select(1);
        });
        Assertions.assertThat(tagValueTextField.getText()).isEqualTo("");
        robot.interact(() -> robot.lookup(EMAIL_ADDRESS_COMBO_BOX_ID)
            .queryAs(ComboBox.class).getSelectionModel().select(0));
        Assertions.assertThat(tagValueTextField.getText()).isEqualTo(TAG_VALUE);
    }

    /**
     * Adds a tag value text field to the list of text fields for the list tag.
     * @param robot Robot that uses the program
     */
    @Test
    void onAddTagValueTextFieldButton(@NotNull FxRobot robot) {
        VBox listTagValueTextFields = robot.lookup(
            LIST_TAG_VALUE_TEXT_FIELDS_BOX_ID).queryAs(VBox.class);
        Assertions.assertThat(listTagValueTextFields.getChildren().size())
            .isEqualTo(1);
        robot.clickOn(ADD_TAG_VALUE_TEXT_FIELD_BUTTON);
        Assertions.assertThat(listTagValueTextFields.getChildren().size())
            .isEqualTo(2);
    }

    /**
     * Moves the tag value text field up the list of tag values.
     * @param robot Robot that uses the program
     */
    @Test
    void onListTagValueTextFieldUpButton(@NotNull FxRobot robot) {
        VBox listTagValueTextFields = robot.lookup(
            LIST_TAG_VALUE_TEXT_FIELDS_BOX_ID).queryAs(VBox.class);
        robot.clickOn(ADD_TAG_VALUE_TEXT_FIELD_BUTTON);
        robot.clickOn(ADD_TAG_VALUE_TEXT_FIELD_BUTTON);
        HBox listTagValueTextField = (HBox)
            listTagValueTextFields.getChildren().get(2);
        Button upButton = (Button) listTagValueTextField.getChildren().get(1);
        Button downButton = (Button) listTagValueTextField.getChildren().get(2);
        Assertions.assertThat(upButton.isVisible()).isEqualTo(true);
        Assertions.assertThat(downButton.isVisible()).isEqualTo(false);
        robot.interact(upButton::fire);
        Assertions.assertThat(upButton.isVisible()).isEqualTo(true);
        Assertions.assertThat(downButton.isVisible()).isEqualTo(true);
        robot.interact(upButton::fire);
        Assertions.assertThat(upButton.isVisible()).isEqualTo(false);
        Assertions.assertThat(downButton.isVisible()).isEqualTo(true);
    }

    /**
     * Moves the tag value text field down the list of tag values.
     * @param robot Robot that uses the program
     */
    @Test
    void onListTagValueTextFieldDownButton(@NotNull FxRobot robot) {
        VBox listTagValueTextFields = robot.lookup(
            LIST_TAG_VALUE_TEXT_FIELDS_BOX_ID).queryAs(VBox.class);
        robot.clickOn(ADD_TAG_VALUE_TEXT_FIELD_BUTTON);
        robot.clickOn(ADD_TAG_VALUE_TEXT_FIELD_BUTTON);
        HBox listTagValueTextField = (HBox)
            listTagValueTextFields.getChildren().get(0);
        Button upButton = (Button) listTagValueTextField.getChildren().get(1);
        Button downButton = (Button) listTagValueTextField.getChildren().get(2);
        Assertions.assertThat(upButton.isVisible()).isEqualTo(false);
        Assertions.assertThat(downButton.isVisible()).isEqualTo(true);
        robot.interact(downButton::fire);
        Assertions.assertThat(upButton.isVisible()).isEqualTo(true);
        Assertions.assertThat(downButton.isVisible()).isEqualTo(true);
        robot.interact(downButton::fire);
        Assertions.assertThat(upButton.isVisible()).isEqualTo(true);
        Assertions.assertThat(downButton.isVisible()).isEqualTo(false);
    }

    /**
     * Moves the tag value text field down the list of tag values.
     * @param robot Robot that uses the program
     */
    @Test
    void onListTagValueTextFieldDeleteButton(@NotNull FxRobot robot) {

        //Setup
        VBox listTagValueTextFields = robot.lookup(
            LIST_TAG_VALUE_TEXT_FIELDS_BOX_ID).queryAs(VBox.class);
        Assertions.assertThat(((HBox) listTagValueTextFields.getChildren()
            .get(0)).getChildren().get(3).isVisible()).isEqualTo(false);
        robot.clickOn(ADD_TAG_VALUE_TEXT_FIELD_BUTTON);
        robot.clickOn(ADD_TAG_VALUE_TEXT_FIELD_BUTTON);
        ((ExtendedTextField) ((HBox) listTagValueTextFields.getChildren()
            .get(0)).getChildren().get(0)).addText(LIST_TAG_VALUE_1);
        Assertions.assertThat(((HBox) listTagValueTextFields.getChildren()
            .get(0)).getChildren().get(3).isVisible()).isEqualTo(true);
        ((ExtendedTextField) ((HBox) listTagValueTextFields.getChildren()
            .get(1)).getChildren().get(0)).addText(LIST_TAG_VALUE_2);
        Assertions.assertThat(((HBox) listTagValueTextFields.getChildren()
            .get(1)).getChildren().get(3).isVisible()).isEqualTo(true);
        ((ExtendedTextField) ((HBox) listTagValueTextFields.getChildren()
            .get(2)).getChildren().get(0)).addText(LIST_TAG_VALUE_3);
        Assertions.assertThat(((HBox) listTagValueTextFields.getChildren()
            .get(2)).getChildren().get(3).isVisible()).isEqualTo(true);

        //Delete Button One
        robot.interact(() -> ((Button) ((HBox) listTagValueTextFields
            .getChildren().get(1)).getChildren().get(3)).fire());
        Assertions.assertThat(listTagValueTextFields.getChildren().size())
            .isEqualTo(2);
        Assertions.assertThat(((ExtendedTextField) ((HBox)
            listTagValueTextFields.getChildren().get(0)).getChildren().get(0))
            .getText()).isEqualTo(LIST_TAG_VALUE_1);
        Assertions.assertThat(((HBox) listTagValueTextFields.getChildren()
            .get(0)).getChildren().get(3).isVisible()).isEqualTo(true);
        Assertions.assertThat(((ExtendedTextField) ((HBox)
            listTagValueTextFields.getChildren().get(1)).getChildren().get(0))
            .getText()).isEqualTo(LIST_TAG_VALUE_3);
        Assertions.assertThat(((HBox) listTagValueTextFields.getChildren()
            .get(1)).getChildren().get(3).isVisible()).isEqualTo(true);

        //Delete Button Two
        robot.interact(() -> ((Button) ((HBox) listTagValueTextFields
            .getChildren().get(0)).getChildren().get(3)).fire());
        Assertions.assertThat(listTagValueTextFields.getChildren().size())
            .isEqualTo(1);
        Assertions.assertThat(((ExtendedTextField) ((HBox)
            listTagValueTextFields.getChildren().get(0)).getChildren().get(0))
            .getText()).isEqualTo(LIST_TAG_VALUE_3);
        Assertions.assertThat(((HBox) listTagValueTextFields.getChildren()
            .get(0)).getChildren().get(3).isVisible()).isEqualTo(false);

    }

    /**
     * Tests if the correct errors appear when the address has empty values.
     * @param robot Robot that uses the program
     */
    @Test
    void onSendButtonCurrentEmailEmptyInput(@NotNull FxRobot robot) {
        robot.clickOn(SEND_BUTTON_ID);
        Assertions.assertThat(robot.lookup(TAG_VALUE_TEXT_FIELD_ID)
            .queryAs(ExtendedTextField.class).getPromptText()).isEqualTo(
            TagBoxController.MISSING_TAG_VALUE_ERROR);
        robot.clickOn(SEND_BUTTON_ID);
        Assertions.assertThat(robot.lookup(LIST_TAG_VALUE_TEXT_FIELD_ID)
            .queryAs(ExtendedTextField.class).getPromptText()).isEqualTo(
            TagBoxController.MISSING_TAG_VALUE_ERROR);
    }

    /**
     * Tests if the correct error appears when another address has empty value.
     * @param robot Robot that uses the program
     */
    @Test
    void onSendButtonOtherEmailEmptyInputs(@NotNull FxRobot robot) {
        robot.interact(() -> {
            for (Node node : robot.lookup(TAG_BOXES_ID).queryAs(VBox.class)
                .getChildren()) {
                try {
                    ((ExtendedTextField) ((HBox) node).getChildren().get(1))
                        .setText(TAG_VALUE);
                } catch (ClassCastException exception) {
                    ((ExtendedTextField) ((HBox) ((VBox) ((HBox) node)
                        .getChildren().get(1)).getChildren().get(0))
                        .getChildren().get(0)).setText(LIST_TAG_VALUE_1);
                }
            }
        });
        robot.clickOn(SEND_BUTTON_ID);
        Assertions.assertThat(robot.lookup(ERROR_LABEL_ID).queryAs(Label.class)
            .getText()).isEqualTo(String.format(TagsScreenController
            .MISSING_TAG_VALUE_ERROR, robot.lookup(EMAIL_ADDRESS_COMBO_BOX_ID)
            .queryAs(ComboBox.class).getItems().get(1)));
    }

    /**
     * Tests if the cancel button works correctly.
     * @param robot Robot that uses the program
     */
    @Test
    void onCancelButton(@NotNull FxRobot robot) {

        //Sets Values
        ExtendedTextField tagValueTextField = robot.lookup(
            TAG_VALUE_TEXT_FIELD_ID).queryAs(ExtendedTextField.class);
        robot.interact(() -> {
            tagValueTextField.setText(TAG_VALUE);
            robot.lookup(LIST_TAG_VALUE_TEXT_FIELD_ID)
                .queryAs(ExtendedTextField.class).setText(LIST_TAG_VALUE_1);
            robot.lookup(EMAIL_ADDRESS_COMBO_BOX_ID).queryAs(ComboBox.class)
                .getSelectionModel().select(1);
            tagValueTextField.setText(TAG_VALUE);
            robot.lookup(LIST_TAG_VALUE_TEXT_FIELD_ID)
                .queryAs(ExtendedTextField.class).setText(LIST_TAG_VALUE_2);
        });

        //Checks Create Screen
        robot.clickOn(CANCEL_BUTTON_ID);
        Assertions.assertThat(Screen.getPrimaryStage().getTitle()).isEqualTo(
            Main.CREATE_SCREEN.getWindowTitle());
        Assertions.assertThat(robot.lookup(CreateScreenTestSuite
            .RECIPIENTS_TEXT_FIELD_ID).queryAs(EmailAddressesTextField.class)
            .getText()).isEqualTo(EmailAddress.arrayToString(getEmail()
            .getHeader().getRecipients(Message.RecipientType.TO)));
        Assertions.assertThat(robot.lookup(CreateScreenTestSuite
            .CARBON_COPIES_TEXT_FIELD_ID).queryAs(EmailAddressesTextField.class)
            .getText()).isEqualTo(EmailAddress.arrayToString(getEmail()
            .getHeader().getRecipients(Message.RecipientType.CC)));
        Assertions.assertThat(robot.lookup(CreateScreenTestSuite
            .BLIND_CARBON_COPIES_TEXT_FIELD_ID).queryAs(
            EmailAddressesTextField.class).getText()).isEqualTo(
            EmailAddress.arrayToString(getEmail().getHeader()
            .getRecipients(Message.RecipientType.BCC)));
        Assertions.assertThat(robot.lookup(CreateScreenTestSuite
            .SUBJECT_TEXT_FIELD_ID).queryAs(ExtendedTextField.class).getText())
            .isEqualTo(getEmail().getHeader().getSubject());
        Assertions.assertThat(robot.lookup(CreateScreenTestSuite
            .CONTENTS_TEXT_AREA_ID).queryAs(ExtendedTextArea.class).getText())
            .isEqualTo(getEmail().getBody().getContents().getText()
                .replaceAll(HTMLVoidElement.LINE_BREAK.getStartTag(),
                HTMLVoidElement.LINE_BREAK.getJavaFxStyle())
                .replaceAll("\r", ""));
        Assertions.assertThat(robot.lookup(CreateScreenTestSuite
            .TAG_BOXES_ID).queryAs(VBox.class).getChildren().size())
            .isEqualTo(getEmail().getBody().getContents().getTags().length);

        //Checks Tag Screen
        robot.interact(() -> robot.lookup(CreateScreenTestSuite.SEND_BUTTON_ID)
            .queryAs(Button.class).fire());
        ExtendedTextField newTagValueTextField = robot.lookup(
            TAG_VALUE_TEXT_FIELD_ID).queryAs(ExtendedTextField.class);
        Assertions.assertThat(newTagValueTextField.getText())
            .isEqualTo(TAG_VALUE);
        Assertions.assertThat(robot.lookup(LIST_TAG_VALUE_TEXT_FIELD_ID)
            .queryAs(ExtendedTextField.class).getText())
            .isEqualTo(LIST_TAG_VALUE_1);
        robot.interact(() -> robot.lookup(EMAIL_ADDRESS_COMBO_BOX_ID)
            .queryAs(ComboBox.class).getSelectionModel().select(1));
        Assertions.assertThat(newTagValueTextField.getText())
            .isEqualTo(TAG_VALUE);
        Assertions.assertThat(robot.lookup(
            LIST_TAG_VALUE_TEXT_FIELD_ID).queryAs(ExtendedTextField.class)
            .getText()).isEqualTo(LIST_TAG_VALUE_2);

    }

    /**
     * Runs when a send button test is being run.
     * @param robot Robot that uses the program
     */
    protected void sendButton(@NotNull FxRobot robot) {

        //Setup
        VBox listTagValueTextFieldsBox = robot.lookup(
            LIST_TAG_VALUE_TEXT_FIELDS_BOX_ID).queryAs(VBox.class);

        //First Email
        robot.clickOn(ADD_TAG_VALUE_TEXT_FIELD_BUTTON);
        robot.clickOn(ADD_TAG_VALUE_TEXT_FIELD_BUTTON);
        robot.lookup(TAG_VALUE_TEXT_FIELD_ID).queryAs(ExtendedTextField.class)
            .setText(TAG_VALUE);
        ((ExtendedTextField) ((HBox) listTagValueTextFieldsBox.getChildren()
            .get(0)).getChildren().get(0)).setText(LIST_TAG_VALUE_1);
        ((ExtendedTextField) ((HBox) listTagValueTextFieldsBox.getChildren()
            .get(1)).getChildren().get(0)).setText(LIST_TAG_VALUE_2);
        ((ExtendedTextField) ((HBox) listTagValueTextFieldsBox.getChildren()
            .get(2)).getChildren().get(0)).setText(LIST_TAG_VALUE_3);

        //Second Email
        robot.interact(() -> robot.lookup(EMAIL_ADDRESS_COMBO_BOX_ID)
            .queryAs(ComboBox.class).getSelectionModel().select(1));
        robot.clickOn(ADD_TAG_VALUE_TEXT_FIELD_BUTTON);
        robot.lookup(TAG_VALUE_TEXT_FIELD_ID).queryAs(ExtendedTextField.class)
            .setText(TAG_VALUE);
        ((ExtendedTextField) ((HBox) listTagValueTextFieldsBox.getChildren()
            .get(0)).getChildren().get(0)).setText(LIST_TAG_VALUE_3);
        ((ExtendedTextField) ((HBox) listTagValueTextFieldsBox.getChildren()
            .get(1)).getChildren().get(0)).setText(LIST_TAG_VALUE_2);

        //Third Email
        robot.interact(() -> robot.lookup(EMAIL_ADDRESS_COMBO_BOX_ID)
            .queryAs(ComboBox.class).getSelectionModel().select(2));
        robot.lookup(TAG_VALUE_TEXT_FIELD_ID).queryAs(ExtendedTextField.class)
            .setText(TAG_VALUE);
        ((ExtendedTextField) ((HBox) listTagValueTextFieldsBox.getChildren()
            .get(0)).getChildren().get(0)).setText(LIST_TAG_VALUE_1);

        //Send
        robot.clickOn(SEND_BUTTON_ID);

    }

}
