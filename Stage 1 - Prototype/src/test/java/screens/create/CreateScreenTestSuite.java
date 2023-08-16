package screens.create;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.mail.Message;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import other.Unicode;
import email.email.Email;
import email.email.body.tag.EmailListTag;
import file.directory.Directory;
import file.html.HTMLElement;
import file.html.HTMLValueElement;
import gui.components.EmailAddressesTextField;
import gui.components.ExtendedTextArea;
import gui.components.ExtendedTextField;
import gui.screens.create.CreateScreenController;
import gui.Main;
import screens.ScreenTestSuite;

/**
 * Test Suite for the Create Screen.
 * @author Jordan Jones
 */
public class CreateScreenTestSuite extends ScreenTestSuite {

    //IDS
    public static final String RECIPIENTS_TEXT_FIELD_ID
        = "#recipientsTextField";
    public static final String CARBON_COPIES_TEXT_FIELD_ID
        = "#carbonCopiesTextField";
    public static final String BLIND_CARBON_COPIES_TEXT_FIELD_ID
        = "#blindCarbonCopiesTextField";
    public static final String SUBJECT_TEXT_FIELD_ID = "#subjectTextField";
    public static final String CONTENTS_TEXT_AREA_ID = "#contentsTextArea";
    protected static final String FONT_FAMILY_COMBO_BOX_ID
        = "#fontFamilyComboBox";
    protected static final String FONT_SIZE_COMBO_BOX_ID = "#fontSizeComboBox";
    protected static final String BOLD_BUTTON_ID = "#boldButton";
    protected static final String ITALIC_BUTTON_ID = "#italicButton";
    protected static final String STRIKETHROUGH_BUTTON_ID
        = "#strikethroughButton";
    protected static final String SUBSCRIPT_BUTTON_ID = "#subscriptButton";
    protected static final String SUPERSCRIPT_BUTTON_ID = "#superscriptButton";
    protected static final String UNDERLINE_BUTTON_ID
        = "#underlineButton";
    protected static final String BULLET_POINT_BUTTON_ID = "#bulletPointButton";
    protected static final String TAG_NAME_TEXT_FIELD = "#tagNameTextField";
    protected static final String TAG_BUTTON_ID = "#tagButton";
    protected static final String TAG_LIST_BUTTON_ID = "#tagListButton";
    public static final String TAG_BOXES_ID = "#tagBoxes";
    protected static final String SELECTED_FILES_BOXES_ID
        = "#selectedFileBoxes";
    protected static final String ATTACHMENT_HYPERLINK = "#filePathHyperLink";
    protected static final String ATTACHMENT_REMOVE_BUTTON
        = "#removeButton";
    public static final String SEND_BUTTON_ID = "#sendButton";
    protected static final String DRAFT_BUTTON_ID = "#draftButton";
    protected static final String CANCEL_BUTTON_ID = "#cancelButton";
    protected static final String ERROR_LABEL_ID = "#errorLabel";

    //TEST VALUES
    protected static final String RECIPIENTS = "jordan.jones090101@gmail.com, "
        + "jordan.joneswork090101@gmail.com, 1910397@swansea.ac.uk";
    protected static final String INVALID_RECIPIENT = "jorm3pwomq3g";
    protected static final String CONTENTS_SUBSTRING = "this is the middle";

    /**
     * Tests if the font family box correctly applies only to the selected text.
     * @param robot Robot that uses the program
     */
    @Test
    void onFontFamilyComboBox(@NotNull FxRobot robot) {
        ExtendedTextArea contentsTextArea =
            robot.lookup(CONTENTS_TEXT_AREA_ID).queryAs(ExtendedTextArea.class);
        ComboBox comboBox = robot.lookup(FONT_FAMILY_COMBO_BOX_ID)
            .queryAs(ComboBox.class);
        robot.interact(() -> {
            contentsTextArea.replaceText(CONTENTS);
            selectText(contentsTextArea);
            comboBox.getSelectionModel().selectFirst();
            checkStyle(contentsTextArea, HTMLValueElement.FONT_FAMILY
                .getJavaFxStyle(comboBox.getValue()));
        });

    }

    /**
     * Tests if the font size box correctly applies only to the selected text.
     * @param robot Robot that uses the program
     */
    @Test
    void onFontSizeComboBox(@NotNull FxRobot robot) {
        ExtendedTextArea contentsTextArea =
            robot.lookup(CONTENTS_TEXT_AREA_ID).queryAs(ExtendedTextArea.class);
        ComboBox comboBox = robot.lookup(FONT_SIZE_COMBO_BOX_ID)
            .queryAs(ComboBox.class);
        robot.interact(() -> {
            contentsTextArea.replaceText(CONTENTS);
            selectText(contentsTextArea);
            comboBox.getSelectionModel().selectFirst();
            checkStyle(contentsTextArea, HTMLValueElement.FONT_SIZE
                .getJavaFxStyle(comboBox.getValue() + ".0"));
        });
    }

    /**
     * Tests if the bold button correctly applies only to the selected text.
     * @param robot Robot that uses the program
     */
    @Test
    void onBoldButton(@NotNull FxRobot robot) {
        textStyleButton(robot, BOLD_BUTTON_ID,
            HTMLElement.BOLD.getJavaFxStyle());
    }

    /**
     * Tests if the italic button correctly applies only to the selected text.
     * @param robot Robot that uses the program
     */
    @Test
    void onItalicButton(@NotNull FxRobot robot) {
        textStyleButton(robot, ITALIC_BUTTON_ID,
            HTMLElement.ITALIC.getJavaFxStyle());
    }

    /**
     * Tests if the strikethrough button applies only to the selected text.
     * @param robot Robot that uses the program
     */
    @Test
    void onStrikethroughButton(@NotNull FxRobot robot) {
        textStyleButton(robot, STRIKETHROUGH_BUTTON_ID,
            HTMLElement.STRIKETHROUGH.getJavaFxStyle());
    }

    /**
     * Tests if the subscript button applies only to the selected text.
     * @param robot Robot that uses the program
     */
    @Test
    void onSubscriptButton(@NotNull FxRobot robot) {
        textStyleButton(robot, SUBSCRIPT_BUTTON_ID,
            HTMLValueElement.SUBSCRIPT.getJavaFxStyle(ExtendedTextArea
                .DEFAULT_FONT_SIZE * HTMLValueElement.TRANSLATE_FACTOR) + " "
                + HTMLValueElement.FONT_SIZE.getJavaFxStyle(ExtendedTextArea
                .DEFAULT_FONT_SIZE * HTMLValueElement.SIZE_FACTOR));
    }

    /**
     * Tests if the subscript button applies only to the selected text.
     * @param robot Robot that uses the program
     */
    @Test
    void onSuperscriptButton(@NotNull FxRobot robot) {
        textStyleButton(robot, SUPERSCRIPT_BUTTON_ID,
            HTMLValueElement.SUPERSCRIPT.getJavaFxStyle(ExtendedTextArea
                .DEFAULT_FONT_SIZE * HTMLValueElement.TRANSLATE_FACTOR) + " "
                + HTMLValueElement.FONT_SIZE.getJavaFxStyle(ExtendedTextArea
                .DEFAULT_FONT_SIZE * HTMLValueElement.SIZE_FACTOR));
    }

    /**
     * Tests if all the bullet point functionality works correctly.
     * @param robot Robot that uses the program
     */
    @Test
    void onBulletPointButton(@NotNull FxRobot robot) {

        //Button Set
        ExtendedTextArea contentsTextArea =
            robot.lookup(CONTENTS_TEXT_AREA_ID).queryAs(ExtendedTextArea.class);
        robot.interact(() -> {
            contentsTextArea.replaceText(CONTENTS);
            selectText(contentsTextArea);
        });
        robot.clickOn(BULLET_POINT_BUTTON_ID);
        Assertions.assertThat(contentsTextArea.getText()).startsWith(
            Unicode.BULLET_POINT + " ");

        //Enter Key Set
        robot.press(KeyCode.ENTER);
        Assertions.assertThat(contentsTextArea.getText().substring(
                contentsTextArea.getText().indexOf("\n") + "\n".length()))
            .startsWith(Unicode.BULLET_POINT + " ");

        //Button Cancel
        robot.press(KeyCode.ENTER);
        robot.clickOn(BULLET_POINT_BUTTON_ID);
        Assertions.assertThat(contentsTextArea.getText().substring(
                contentsTextArea.getText().indexOf("\n") + "\n".length()))
            .doesNotStartWith(Unicode.BULLET_POINT + " ");

        //Enter Key Cancel
        robot.press(KeyCode.ENTER);
        Assertions.assertThat(contentsTextArea.getText().substring(
                contentsTextArea.getText().indexOf("\n") + "\n".length()))
            .doesNotStartWith(Unicode.BULLET_POINT + " ");

    }

    /**
     * Tests if the underline button applies only to the selected text.
     * @param robot Robot that uses the program
     */
    @Test
    void onUnderlineButton(@NotNull FxRobot robot) {
        textStyleButton(robot, UNDERLINE_BUTTON_ID,
            HTMLElement.UNDERLINE.getJavaFxStyle());
    }

    /**
     * Tests if the tag button correctly creates a tag box.
     * @param robot Robot that uses the program
     */
    @Test
    void onTagButton(@NotNull FxRobot robot) {
        robot.lookup(TAG_NAME_TEXT_FIELD).queryAs(ExtendedTextField.class)
            .setText(EMAIL_TAG.getName());
        robot.clickOn(TAG_BUTTON_ID);
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
     * Tests if the tag button causes an error if a tag already has the name.
     * @param robot Robot that uses the program
     */
    @Test
    void onTagButtonExistingTag(@NotNull FxRobot robot) {
        ExtendedTextField tagNameTextField = robot.lookup(TAG_NAME_TEXT_FIELD)
            .queryAs(ExtendedTextField.class);
        tagNameTextField.setText(EMAIL_TAG.getName());
        robot.clickOn(TAG_LIST_BUTTON_ID);
        tagNameTextField.setText(EMAIL_TAG.getName());
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
     * Tests if the tag button causes an error if the name is empty.
     * @param robot Robot that uses the program
     */
    @Test
    void onTagButtonInvalidTag(@NotNull FxRobot robot) {
        robot.clickOn(TAG_BUTTON_ID);
        Assertions.assertThat(robot.lookup(ERROR_LABEL_ID).queryAs(Label.class))
            .hasText(CreateScreenController.WHITESPACE_TAG_ERROR);
    }

    /**
     * Tests if the tag box insert button inserts the tag into the text area.
     * @param robot Robot that uses the program
     */
    @Test
    void onTagBoxInsertButton(@NotNull FxRobot robot) {
        ExtendedTextField tagNameTextField = robot.lookup(TAG_NAME_TEXT_FIELD)
            .queryAs(ExtendedTextField.class);
        ExtendedTextArea contentsTextArea =
            robot.lookup(CONTENTS_TEXT_AREA_ID).queryAs(ExtendedTextArea.class);
        tagNameTextField.setText(EMAIL_TAG.getName());
        robot.clickOn(TAG_BUTTON_ID);
        ObservableList<Node> children
            = robot.lookup(TAG_BOXES_ID).queryAs(VBox.class).getChildren();
        Button button = ((Button) ((HBox) children.get(
            children.size() - 1)).getChildren().get(0));
        robot.interact(() -> {
            contentsTextArea.appendText("dbwoufbpwefn3fwf");
            button.fire();
            contentsTextArea.appendText("wobqwfubofbqipfn");
            button.fire();
            contentsTextArea.appendText("wdhoqwh0qw\nwuhu");
            button.fire();
            contentsTextArea.appendText("wbq9wfhqfhbidqfn");
        });
        Assertions.assertThat(StringUtils.countMatches(
            contentsTextArea.getText(), EMAIL_TAG.toString())).isEqualTo(3);
    }

    /**
     * Tests if the tag box delete button deletes the tag box.
     * @param robot Robot that uses the program
     */
    @Test
    void onTagBoxDeleteButton(@NotNull FxRobot robot) {
        //Setup
        ExtendedTextField tagNameTextField = robot.lookup(TAG_NAME_TEXT_FIELD)
            .queryAs(ExtendedTextField.class);
        ObservableList<Node> children
            = robot.lookup(TAG_BOXES_ID).queryAs(VBox.class).getChildren();
        tagNameTextField.setText(EMAIL_TAG.getName());
        robot.clickOn(TAG_BUTTON_ID);
        tagNameTextField.setText(EMAIL_LIST_TAG.getName().split(
            EmailListTag.getRegex())[0]);
        robot.clickOn(TAG_LIST_BUTTON_ID);
        Assertions.assertThat(((Button) ((HBox) children.get(
            children.size() - 2)).getChildren().get(0)).getText()).isEqualTo(
            EMAIL_TAG.getName());
        int tags = 0;
        Email email = Main.CREATE_SCREEN.getController().getEmail();
        if (email != null) {
            tags = email.getBody().getContents().getTags().length;
        }
        Assertions.assertThat(children.size()).isEqualTo(tags + 2);

        //Delete
        robot.interact(() -> ((Button) ((HBox) children
            .get(children.size() - 2)).getChildren().get(1)).fire());
        Assertions.assertThat(((Button) ((HBox) children.get(
            children.size() - 1)).getChildren().get(0)).getText()).isEqualTo(
            EMAIL_LIST_TAG.getName());
        Assertions.assertThat(children.size()).isEqualTo(tags + 1);

    }

    /**
     * Tests if the tag list button correctly creates a tag list box.
     * @param robot Robot that uses the program
     */
    @Test
    void onTagListButton(@NotNull FxRobot robot) {
        robot.lookup(TAG_NAME_TEXT_FIELD).queryAs(ExtendedTextField.class)
            .setText(EMAIL_TAG.getName());
        robot.clickOn(TAG_LIST_BUTTON_ID);
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
     * Tests if the tag list button causes an error if the name is in use.
     * @param robot Robot that uses the program
     */
    @Test
    void onTagListButtonExistingTag(@NotNull FxRobot robot) {
        ExtendedTextField tagNameTextField = robot.lookup(TAG_NAME_TEXT_FIELD)
            .queryAs(ExtendedTextField.class);
        tagNameTextField.setText(EMAIL_TAG.getName());
        robot.clickOn(TAG_BUTTON_ID);
        tagNameTextField.setText(EMAIL_TAG.getName());
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
     * Tests if the tag list button causes an error if the name is empty.
     * @param robot Robot that uses the program
     */
    @Test
    void onTagListButtonInvalidTag(@NotNull FxRobot robot) {
        robot.clickOn(TAG_LIST_BUTTON_ID);
        Assertions.assertThat(robot.lookup(ERROR_LABEL_ID).queryAs(Label.class))
            .hasText(CreateScreenController.WHITESPACE_TAG_ERROR);
    }

    /**
     * Tests if the tag list box insert button inserts the list into the area.
     * @param robot Robot that uses the program
     */
    @Test
    void onTagListBoxInsertButton(@NotNull FxRobot robot) {
        ExtendedTextField tagNameTextField = robot.lookup(TAG_NAME_TEXT_FIELD)
            .queryAs(ExtendedTextField.class);
        ExtendedTextArea contentsTextArea =
            robot.lookup(CONTENTS_TEXT_AREA_ID).queryAs(ExtendedTextArea.class);
        tagNameTextField.setText(EMAIL_LIST_TAG.getName().split(
            EmailListTag.getRegex())[0]);
        robot.clickOn(TAG_LIST_BUTTON_ID);
        ObservableList<Node> children
            = robot.lookup(TAG_BOXES_ID).queryAs(VBox.class).getChildren();
        Button button = ((Button) ((HBox) children.get(
            children.size() - 1)).getChildren().get(0));
        robot.interact(() -> {
            contentsTextArea.appendText("dbwoufbpwefn3fwf");
            button.fire();
            contentsTextArea.appendText("wobqwfubofbqipfn");
            button.fire();
            contentsTextArea.appendText("wdhoqwh0qw\nwuhu");
            button.fire();
            contentsTextArea.appendText("wbq9wfhqfhbidqfn");
        });
        Assertions.assertThat(StringUtils.countMatches(contentsTextArea
            .getText(), EMAIL_LIST_TAG.toString())).isEqualTo(3);
    }

    /**
     * Tests if the tag list box delete button deletes the tag list box.
     * @param robot Robot that uses the program
     */
    @Test
    void onTagListBoxDeleteButton(@NotNull FxRobot robot) {

        //Setup
        ExtendedTextField tagNameTextField = robot.lookup(TAG_NAME_TEXT_FIELD)
            .queryAs(ExtendedTextField.class);
        ObservableList<Node> children
            = robot.lookup(TAG_BOXES_ID).queryAs(VBox.class).getChildren();
        tagNameTextField.setText(EMAIL_LIST_TAG.getName().split(
            EmailListTag.getRegex())[0]);
        robot.clickOn(TAG_LIST_BUTTON_ID);
        tagNameTextField.setText(EMAIL_TAG.getName());
        robot.clickOn(TAG_BUTTON_ID);
        Assertions.assertThat(((Button) ((HBox) children.get(
            children.size() - 2)).getChildren().get(0)).getText()).isEqualTo(
                EMAIL_LIST_TAG.getName());
        int tags = 0;
        Email email = Main.CREATE_SCREEN.getController().getEmail();
        if (email != null) {
            tags = email.getBody().getContents().getTags().length;
        }
        Assertions.assertThat(children.size()).isEqualTo(tags + 2);

        //Delete
        robot.interact(() -> ((Button) ((HBox) children
            .get(children.size() - 2)).getChildren().get(1)).fire());
        Assertions.assertThat(((Button) ((HBox) children.get(
            children.size() - 1)).getChildren().get(0)).getText()).isEqualTo(
            EMAIL_TAG.getName());
        Assertions.assertThat(children.size()).isEqualTo(tags + 1);

    }

    /**
     * Tests the select attachment button functionality.
     * @param robot Robot that uses the program
     */
    @Test
    void onSelectAttachmentButton(@NotNull FxRobot robot) {
        robot.interact(() -> {
            try {
                Main.CREATE_SCREEN.getController()
                    .addNewFiles(List.of(Objects.requireNonNull(
                        new Directory(FILES_DIRECTORY).listFiles())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        int files = 0;
        Email email = Main.CREATE_SCREEN.getController().getEmail();
        if (email != null) {
            files = email.getBody().getAttachmentNumber();
        }
        Assertions.assertThat(robot.lookup(SELECTED_FILES_BOXES_ID).queryAs(
            VBox.class).getChildren().size()).isEqualTo(files + 3);
    }

    /**
     * Tests if the attachment hyperlink works correctly.
     * @param robot Robot that uses the program
     */
    @Test
    void onAttachmentHyperlink(@NotNull FxRobot robot) {
        onSelectAttachmentButton(robot);
        robot.clickOn(ATTACHMENT_HYPERLINK);
    }

    /**
     * Tests if the remove attachment button works correctly.
     * @param robot Robot that uses the program
     */
    @Test
    void onAttachmentRemoveButton(@NotNull FxRobot robot) {
        onSelectAttachmentButton(robot);
        robot.clickOn(ATTACHMENT_REMOVE_BUTTON);
        int files = 0;
        Email email = Main.CREATE_SCREEN.getController().getEmail();
        if (email != null) {
            files = email.getBody().getAttachmentNumber();
        }
        Assertions.assertThat(robot.lookup(SELECTED_FILES_BOXES_ID).queryAs(
            VBox.class).getChildren().size()).isEqualTo(files + 2);
    }

    /**
     * Tests if the send button correctly shows the invalid recipient error.
     * @param robot Robot that uses the program
     */
    @Test
    void onSendButtonInvalidRecipient(@NotNull FxRobot robot) {
        robot.interact(() -> robot.lookup(RECIPIENTS_TEXT_FIELD_ID)
            .queryAs(EmailAddressesTextField.class).setText(INVALID_RECIPIENT));
        robot.clickOn(SEND_BUTTON_ID);
        Assertions.assertThat(robot.lookup(ERROR_LABEL_ID).queryAs(Label.class))
            .hasText(String.format(CreateScreenController.INVALID_ADDRESS_ERROR,
                INVALID_RECIPIENT, Message.RecipientType.TO
                    .toString().toUpperCase()));
    }

    /**
     * Tests if the send button correctly shows the invalid carbon copy error.
     * @param robot Robot that uses the program
     */
    @Test
    void onSendButtonInvalidCarbonCopy(@NotNull FxRobot robot) {
        robot.interact(() -> robot.lookup(CARBON_COPIES_TEXT_FIELD_ID)
            .queryAs(EmailAddressesTextField.class).setText(INVALID_RECIPIENT));
        robot.clickOn(SEND_BUTTON_ID);
        Assertions.assertThat(robot.lookup(ERROR_LABEL_ID).queryAs(Label.class))
            .hasText(String.format(CreateScreenController.INVALID_ADDRESS_ERROR,
                INVALID_RECIPIENT, Message.RecipientType.CC
                    .toString().toUpperCase()));
    }

    /**
     * Tests if the send button correctly shows the invalid blind copy error.
     * @param robot Robot that uses the program
     */
    @Test
    void onSendButtonInvalidBlindCarbonCopy(@NotNull FxRobot robot) {
        robot.interact(() -> robot.lookup(BLIND_CARBON_COPIES_TEXT_FIELD_ID)
            .queryAs(EmailAddressesTextField.class).setText(INVALID_RECIPIENT));
        robot.clickOn(SEND_BUTTON_ID);
        Assertions.assertThat(robot.lookup(ERROR_LABEL_ID).queryAs(Label.class))
            .hasText(String.format(CreateScreenController.INVALID_ADDRESS_ERROR,
                INVALID_RECIPIENT, Message.RecipientType.BCC
                    .toString().toUpperCase()));
    }

    /**
     * Tests if the draft button correctly shows the invalid recipient error.
     * @param robot Robot that uses the program
     */
    @Test
    void onDraftButtonInvalidRecipient(@NotNull FxRobot robot) {
        robot.interact(() -> robot.lookup(RECIPIENTS_TEXT_FIELD_ID)
            .queryAs(EmailAddressesTextField.class).setText(INVALID_RECIPIENT));
        robot.clickOn(DRAFT_BUTTON_ID);
        Assertions.assertThat(robot.lookup(ERROR_LABEL_ID).queryAs(Label.class))
            .hasText(String.format(CreateScreenController.INVALID_ADDRESS_ERROR,
                INVALID_RECIPIENT, Message.RecipientType.TO
                    .toString().toUpperCase()));
    }

    /**
     * Tests if the draft button correctly shows the invalid carbon copy error.
     * @param robot Robot that uses the program
     */
    @Test
    void onDraftButtonInvalidCarbonCopy(@NotNull FxRobot robot) {
        robot.interact(() -> robot.lookup(CARBON_COPIES_TEXT_FIELD_ID)
            .queryAs(EmailAddressesTextField.class).setText(INVALID_RECIPIENT));
        robot.clickOn(DRAFT_BUTTON_ID);
        Assertions.assertThat(robot.lookup(ERROR_LABEL_ID).queryAs(Label.class))
            .hasText(String.format(CreateScreenController.INVALID_ADDRESS_ERROR,
                INVALID_RECIPIENT, Message.RecipientType.CC
                    .toString().toUpperCase()));
    }

    /**
     * Tests if the draft button correctly shows the invalid blind copy error.
     * @param robot Robot that uses the program
     */
    @Test
    void onDraftButtonInvalidBlindCarbonCopy(@NotNull FxRobot robot) {
        robot.interact(() -> robot.lookup(BLIND_CARBON_COPIES_TEXT_FIELD_ID)
            .queryAs(EmailAddressesTextField.class).setText(INVALID_RECIPIENT));
        robot.clickOn(DRAFT_BUTTON_ID);
        Assertions.assertThat(robot.lookup(ERROR_LABEL_ID).queryAs(Label.class))
            .hasText(String.format(CreateScreenController.INVALID_ADDRESS_ERROR,
                INVALID_RECIPIENT, Message.RecipientType.BCC
                    .toString().toUpperCase()));
    }

    /**
     * Checks if the given text style is correctly applied to the selected text.
     * @param robot Robot that uses the program
     * @param buttonId Button that is being clicked
     * @param style Style that is to be checked
     */
    protected void textStyleButton(@NotNull FxRobot robot, String buttonId,
        String style) {
        ExtendedTextArea contentsTextArea =
            robot.lookup(CONTENTS_TEXT_AREA_ID).queryAs(ExtendedTextArea.class);
        robot.interact(() -> {
            contentsTextArea.replaceText(CONTENTS);
            selectText(contentsTextArea);
        });
        robot.clickOn(buttonId);
        checkStyle(contentsTextArea, style);
    }

    /**
     * Selects the wanted text for the text area.
     * @param contentsTextArea Text area to selected text in
     */
    protected void selectText(@NotNull ExtendedTextArea contentsTextArea) {
        int index = contentsTextArea.getText().indexOf(CONTENTS_SUBSTRING);
        int length = index + CONTENTS_SUBSTRING.length();
        contentsTextArea.selectRange(index, length);
        Assertions.assertThat(contentsTextArea.getSelectedText())
            .isEqualTo(CONTENTS_SUBSTRING);
    }

    /**
     * Checks the given style in the text area.
     * @param contentsTextArea Text area to check the style in
     * @param style Style to check
     */
    protected void checkStyle(@NotNull ExtendedTextArea contentsTextArea,
        String style) {
        IndexRange indexRange = contentsTextArea.getSelection();
        for (int i = 0; i < indexRange.getStart(); i++) {
            Assertions.assertThat(contentsTextArea.getStyleOfChar(i))
                .isEqualTo("");
        }
        for (int i = indexRange.getStart(); i < indexRange.getEnd(); i++) {
            Assertions.assertThat(contentsTextArea.getStyleOfChar(i))
                .isEqualTo(style);
        }
        for (int i = indexRange.getEnd();
             i < contentsTextArea.getText().length(); i++) {
            Assertions.assertThat(contentsTextArea.getStyleOfChar(i))
                .isEqualTo("");
        }
    }

    /**
     * Prints the results of the error label to check for an error.
     * @param robot Robot that uses the program
     */
    protected void checkError(@NotNull FxRobot robot) {
        System.out.println(robot.lookup(ERROR_LABEL_ID).queryAs(Label.class)
            .getText());
    }

}
