package screens.create;

import email.email.Email;
import email.email.body.tag.EmailListTag;
import email.email.body.tag.EmailTag;
import file.FileCanNotDeleteException;
import file.directory.Directory;
import file.html.HTMLElement;
import file.html.HTMLValueElement;
import gui.Main;
import gui.components.EmailAddressesTextField;
import gui.components.ExtendedTextArea;
import gui.components.ExtendedTextField;
import gui.fxml.FXMLScreen;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.IndexRange;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import screens.ScreenTestSuite;
import util.BulletPointType;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Test Suite for the Login Screen.
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
    protected static final String ROUND_BULLET_POINT_BUTTON_ID
        = "#roundBulletPointButton";
    protected static final String SQUARE_BULLET_POINT_BUTTON_ID
        = "#squareBulletPointButton";
    protected static final String NUMBER_BULLET_POINT_BUTTON_ID
        = "#numberBulletPointButton";
    protected static final String LETTER_BULLET_POINT_BUTTON_ID
        = "#letterBulletPointButton";
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
    protected static final String ERROR_LABEL_ID = "#errorLabel";
    protected static final String HISTORY_POP_UP = "#historyPopup";

    //TEST VALUES
    protected static final String[] RECIPIENTS = {
        GOOGLE.getEmailAddress().getAddress(),
        OUTLOOK.getEmailAddress().getAddress(),
        OTHER.getEmailAddress().getAddress()
    };
    protected static final String INVALID_RECIPIENT = "jorm3pwomq3g";
    protected static final String CONTENTS_SUBSTRING = "this is the middle";
    protected static final String RECIPIENTS_PROMPT_TEXT
        = "Recipients (Split between a comma [,])";
    protected static final String CARBON_COPIES_PROMPT_TEXT
        = "Carbon Copies";
    protected static final String BLIND_CARBON_COPIES_PROMPT_TEXT
        = "Blind Carbon Copies";
    protected static final String SUBJECT_PROMPT_TEXT = "Subject";

    /**
     * Loads the create page.
     * @param robot Robot that uses the program
     */
    protected void load(@NotNull FxRobot robot) {
        robot.interact(() -> {
            try {
                robot.lookup(ScreenTestSuite.SCREEN_SPACE)
                    .queryAs(Pane.class)
                    .getChildren().add(Main.CREATE_SCREEN.load());
                Main.CREATE_SCREEN.getController().setController(getEmail());
            } catch (IOException | FileCanNotDeleteException e) {
                e.printStackTrace();
            }
        });
        robot.interact(() -> FXMLScreen.getPrimaryStage().toFront());
    }

    /**
     * Tests if the given send button works correctly with tags.
     * @param robot Robot that uses the program
     * @param buttonId Send button that is being clicked
     */
    protected void sendButtonWithTags(@NotNull FxRobot robot, String buttonId) {
        inputValues(robot);
        inputTag(robot);
        inputListTag(robot);
        robot.clickOn(buttonId);
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
     * Inputs values into the fields.
     * @param robot Robot that uses the program
     */
    private void inputValues(@NotNull FxRobot robot) {
        EmailAddressesTextField recipientsTextField = robot.lookup(
            RECIPIENTS_TEXT_FIELD_ID).queryAs(EmailAddressesTextField.class);
        Assertions.assertThat(recipientsTextField.getPromptText())
            .isEqualTo(RECIPIENTS_PROMPT_TEXT);
        inputRecipients(robot, recipientsTextField);
        EmailAddressesTextField carbonCopiesTextField = robot.lookup(
            CARBON_COPIES_TEXT_FIELD_ID).queryAs(EmailAddressesTextField.class);
        Assertions.assertThat(carbonCopiesTextField.getPromptText())
            .isEqualTo(CARBON_COPIES_PROMPT_TEXT);
        inputRecipients(robot, carbonCopiesTextField);
        EmailAddressesTextField blindCarbonCopiesTextField = robot.lookup(
            BLIND_CARBON_COPIES_TEXT_FIELD_ID).queryAs(
                EmailAddressesTextField.class);
        Assertions.assertThat(blindCarbonCopiesTextField.getPromptText())
            .isEqualTo(BLIND_CARBON_COPIES_PROMPT_TEXT);
        inputRecipients(robot, blindCarbonCopiesTextField);
        robot.interact(() -> {
            ExtendedTextField subjectField = robot.lookup(SUBJECT_TEXT_FIELD_ID)
                .queryAs(ExtendedTextField.class);
            Assertions.assertThat(subjectField.getPromptText())
                .isEqualTo(SUBJECT_PROMPT_TEXT);
            subjectField.addText(SUBJECT);
        });
        inputContents(robot);
        attachmentButton(robot);
    }

    /**
     * Inputs the recipients.
     * @param robot Robot that uses the program
     * @param textField Field to input the recipients into
     */
    private void inputRecipients(@NotNull FxRobot robot,
        EmailAddressesTextField textField) {
        robot.interact(() -> {
            if (!textField.isEmpty()) {
                textField.addText(", ");
            }
            textField.addText(RECIPIENTS[0].substring(0,
                RECIPIENTS[0].length() - 2));
        });
        robot.clickOn(robot.lookup(HISTORY_POP_UP).queryAs(VBox.class)
            .getChildren().get(0));
        robot.interact(() -> {
            textField.addText(", ");
            textField.addText(RECIPIENTS[1].substring(0,
                RECIPIENTS[1].length() - 2));
        });
        robot.clickOn(robot.lookup(HISTORY_POP_UP).queryAs(VBox.class)
            .getChildren().get(0));
        robot.interact(() -> {
            textField.addText(", ");
            textField.addText(RECIPIENTS[2].substring(0,
                RECIPIENTS[2].length() - 2));
        });
        robot.clickOn(robot.lookup(HISTORY_POP_UP).queryAs(VBox.class)
            .getChildren().get(0));
    }

    /**
     * Inputs the contents into the contents box.
     * @param robot Robot that uses the program
     */
    private void inputContents(@NotNull FxRobot robot) {
        ExtendedTextArea textArea = robot.lookup(
            CONTENTS_TEXT_AREA_ID).queryAs(ExtendedTextArea.class);
        robot.interact(() -> textArea.replaceText(CONTENTS));
        fontFamilyComboBox(robot, textArea);
        fontSizeComboBox(robot, textArea);
        boldButton(robot, textArea);
        italicButton(robot, textArea);
        strikethroughButton(robot, textArea);
        subscriptButton(robot, textArea);
        superscriptButton(robot, textArea);
        underlineButton(robot, textArea);
        roundBulletPointButton(robot, textArea);
        squareBulletPointButton(robot, textArea);
        numberBulletPointButton(robot, textArea);
        letterBulletPointButton(robot, textArea);
    }

    /**
     * Inputs a tag into the tag box.
     * @param robot Robot that uses the program
     */
    private void inputTag(@NotNull FxRobot robot) {
        ExtendedTextField tagNameTextField = robot.lookup(TAG_NAME_TEXT_FIELD)
            .queryAs(ExtendedTextField.class);
        tagNameTextField.setText(EMAIL_TAG.getName());
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
        Button button = ((Button) ((HBox) children.get(
            children.size() - 1)).getChildren().get(0));
        ExtendedTextArea contentsTextArea = robot.lookup(
            CONTENTS_TEXT_AREA_ID).queryAs(ExtendedTextArea.class);
        robot.interact(() -> {
            contentsTextArea.appendText("\ndbwoufbpwefn3fwf");
            button.fire();
            contentsTextArea.appendText("wobqwfubofbqipfn");
            button.fire();
            contentsTextArea.appendText("wdhoqwh0qwwuhu");
            button.fire();
            contentsTextArea.appendText("wbq9wfhqfhbidqfn");
        });
        Assertions.assertThat(StringUtils.countMatches(
            contentsTextArea.getText(), EMAIL_TAG.toString())).isEqualTo(3);
        tagNameTextField.setText("ForTagDeletingLotsAndLotsOfDeleting");
        robot.clickOn(TAG_BUTTON_ID);
        Assertions.assertThat(((Button) ((HBox) children.get(
            children.size() - 1)).getChildren().get(0)).getText()).isEqualTo(
            "ForTagDeletingLotsAndLostOfDeleting".substring(0, 16));
        Assertions.assertThat(children.size()).isEqualTo(tags + 2);
        robot.interact(() -> ((Button) ((HBox) children
            .get(children.size() - 1)).getChildren().get(1)).fire());

        Assertions.assertThat(children.size()).isEqualTo(tags + 1);
    }

    /**
     * Inputs a list tag into the tag box.
     * @param robot Robot that uses the program
     */
    private void inputListTag(@NotNull FxRobot robot) {
        ExtendedTextField tagNameTextField = robot.lookup(TAG_NAME_TEXT_FIELD)
            .queryAs(ExtendedTextField.class);
        ExtendedTextArea contentsTextArea =
            robot.lookup(CONTENTS_TEXT_AREA_ID).queryAs(ExtendedTextArea.class);
        tagNameTextField.setText(ScreenTestSuite.EMAIL_LIST_TAG.getName().split(
            EmailListTag.getRegex())[0]);
        robot.clickOn(TAG_LIST_BUTTON_ID);
        ObservableList<Node> children
            = robot.lookup(TAG_BOXES_ID).queryAs(VBox.class).getChildren();
        int tags = 0;
        Email email = Main.CREATE_SCREEN.getController().getEmail();
        if (email != null) {
            tags = email.getBody().getContents().getTags().length;
        }
        Assertions.assertThat(children.size()).isEqualTo(tags + 2);
        Assertions.assertThat(((Button) ((HBox) children.get(
            children.size() - 1)).getChildren().get(0)).getText()).contains(
            EmailListTag.LIST_TAG_IDENTIFIER);
        Assertions.assertThat(((EmailListTag) Main.CREATE_SCREEN.getController()
            .getTags().toArray(new EmailTag[0])[(tags + 2) - 1])
            .getBulletPointType()).isEqualTo(
            BulletPointType.ROUND_BULLET_POINT);
        for (BulletPointType bulletPointType
            : BulletPointType.BULLET_POINT_TYPES) {
            int finalTags = tags;
            robot.interact(() -> ((ComboBox<BulletPointType>) ((HBox) robot
                .lookup(TAG_BOXES_ID).queryAs(VBox.class).getChildren()
                .get((finalTags + 2) - 1)).getChildren().get(1))
                .getSelectionModel().select(bulletPointType));
            Assertions.assertThat(((EmailListTag) Main.CREATE_SCREEN
                .getController().getTags().toArray(new EmailTag[0])[
                (tags + 2) - 1])
                .getBulletPointType()).isEqualTo(bulletPointType);
        }
        Button button = ((Button) ((HBox) children.get(
            children.size() - 1)).getChildren().get(0));
        robot.interact(() -> {
            contentsTextArea.appendText("\ndbwoufbpwefn3fwf");
            button.fire();
            contentsTextArea.appendText("wobqwfubofbqipfn");
            button.fire();
            contentsTextArea.appendText("wdhoqwh0qwwuhu");
            button.fire();
            contentsTextArea.appendText("wbq9wfhqfhbidqfn");
        });
        Assertions.assertThat(StringUtils.countMatches(contentsTextArea
                .getText(), ScreenTestSuite.EMAIL_LIST_TAG.toString()))
            .isEqualTo(3);
        tagNameTextField.setText("ForListTagDeletingLotsAndLotsOfDeleting");
        robot.clickOn(TAG_LIST_BUTTON_ID);
        Assertions.assertThat(((Button) ((HBox) children.get(
            children.size() - 1)).getChildren().get(0)).getText()).isEqualTo(
            "ForListTagDeletingLotsAndLotsOfDeleting".substring(0, 16) + "(List)");
        Assertions.assertThat(children.size()).isEqualTo(tags + 3);
        robot.interact(() -> ((Button) ((HBox) children
            .get(children.size() - 1)).getChildren().get(2)).fire());
        Assertions.assertThat(children.size()).isEqualTo(tags + 2);
    }

    /**
     * Selects the wanted text for the text area.
     * @param robot Robot that uses the program
     * @param contentsTextArea Text area to selected text in
     * @param substring Substring to select from the contents text area
     */
    protected void selectText(@NotNull FxRobot robot,
        @NotNull ExtendedTextArea contentsTextArea,
        String substring) {
        int index = contentsTextArea.getText().indexOf(substring);
        int length = index + substring.length();
        robot.interact(() -> contentsTextArea.selectRange(index, length));
        Assertions.assertThat(contentsTextArea.getSelectedText())
            .isEqualTo(substring);
    }

    /**
     * Uses the font family combo box.
     * @param robot Robot that uses the program
     * @param contentsTextArea Contents text area to sets the style for
     */
    protected void fontFamilyComboBox(@NotNull FxRobot robot,
        ExtendedTextArea contentsTextArea) {
        ComboBox<?> comboBox = robot.lookup(FONT_FAMILY_COMBO_BOX_ID)
            .queryAs(ComboBox.class);
        selectText(robot, contentsTextArea, "fontStyle");
        robot.interact(() -> comboBox.getSelectionModel().selectFirst());
        checkStyle(contentsTextArea, HTMLValueElement.FONT_FAMILY
            .getJavaFxStyle(comboBox.getValue()));
    }

    /**
     * Uses the font size combo box.
     * @param robot Robot that uses the program
     * @param contentsTextArea Contents text area to sets the style for
     */
    protected void fontSizeComboBox(@NotNull FxRobot robot,
        ExtendedTextArea contentsTextArea) {
        ComboBox<?> comboBox = robot.lookup(FONT_SIZE_COMBO_BOX_ID)
            .queryAs(ComboBox.class);
        selectText(robot, contentsTextArea, "fontSize");
        robot.interact(() -> comboBox.getSelectionModel().selectFirst());
        checkStyle(contentsTextArea, HTMLValueElement.FONT_SIZE
            .getJavaFxStyle(comboBox.getValue() + ".0"));
    }

    /**
     * Uses the bold button.
     * @param robot Robot that uses the program
     * @param contentsTextArea Contents text area to sets the style for
     */
    protected void boldButton(@NotNull FxRobot robot,
        ExtendedTextArea contentsTextArea) {
        textStyleButton(robot, contentsTextArea, "bold", BOLD_BUTTON_ID,
            HTMLElement.BOLD.getJavaFxStyle());
    }

    /**
     * Uses the italic button.
     * @param robot Robot that uses the program
     * @param contentsTextArea Contents text area to sets the style for
     */
    protected void italicButton(@NotNull FxRobot robot,
        ExtendedTextArea contentsTextArea) {
        textStyleButton(robot, contentsTextArea, "italic", ITALIC_BUTTON_ID,
            HTMLElement.ITALIC.getJavaFxStyle());
    }

    /**
     * Uses the strikethrough button.
     * @param robot Robot that uses the program
     * @param contentsTextArea Contents text area to sets the style for
     */
    protected void strikethroughButton(@NotNull FxRobot robot,
        ExtendedTextArea contentsTextArea) {
        textStyleButton(robot, contentsTextArea, "strikethrough",
            STRIKETHROUGH_BUTTON_ID,
            HTMLElement.STRIKETHROUGH.getJavaFxStyle());
    }

    /**
     * Uses the subscript button.
     * @param robot Robot that uses the program
     * @param contentsTextArea Contents text area to sets the style for
     */
    protected void subscriptButton(@NotNull FxRobot robot,
        ExtendedTextArea contentsTextArea) {
        textStyleButton(robot, contentsTextArea, "subscript",
            SUBSCRIPT_BUTTON_ID, HTMLValueElement.SUBSCRIPT.getJavaFxStyle(
                Main.SMALL_FONT_SIZE * HTMLValueElement.TRANSLATE_FACTOR) + " "
                + HTMLValueElement.FONT_SIZE.getJavaFxStyle(Main.SMALL_FONT_SIZE
                * HTMLValueElement.SIZE_FACTOR));
    }

    /**
     * Uses the superscript button.
     * @param robot Robot that uses the program
     * @param contentsTextArea Contents text area to sets the style for
     */
    protected void superscriptButton(@NotNull FxRobot robot,
        ExtendedTextArea contentsTextArea) {
        textStyleButton(robot, contentsTextArea, "superscript",
            SUPERSCRIPT_BUTTON_ID, HTMLValueElement.SUPERSCRIPT.getJavaFxStyle(
                Main.SMALL_FONT_SIZE * HTMLValueElement.TRANSLATE_FACTOR) + " "
                + HTMLValueElement.FONT_SIZE.getJavaFxStyle(Main.SMALL_FONT_SIZE
                * HTMLValueElement.SIZE_FACTOR));
    }

    /**
     * Uses the underline button.
     * @param robot Robot that uses the program
     * @param contentsTextArea Contents text area to sets the style for
     */
    void underlineButton(@NotNull FxRobot robot,
        ExtendedTextArea contentsTextArea) {
        textStyleButton(robot, contentsTextArea, "underline",
            UNDERLINE_BUTTON_ID, HTMLElement.UNDERLINE.getJavaFxStyle());
    }

    /**
     * Uses the round bullet point button.
     * @param robot Robot that uses the program
     * @param contentsTextArea Contents text area to sets the style for
     */
    void roundBulletPointButton(@NotNull FxRobot robot,
        ExtendedTextArea contentsTextArea) {
        bulletPointButton(robot, contentsTextArea,
            ROUND_BULLET_POINT_BUTTON_ID, SQUARE_BULLET_POINT_BUTTON_ID,
            BulletPointType.ROUND_BULLET_POINT);
    }

    /**
     * Uses the square bullet point button.
     * @param robot Robot that uses the program
     * @param contentsTextArea Contents text area to sets the style for
     */
    void squareBulletPointButton(@NotNull FxRobot robot,
        ExtendedTextArea contentsTextArea) {
        bulletPointButton(robot, contentsTextArea,
            SQUARE_BULLET_POINT_BUTTON_ID, NUMBER_BULLET_POINT_BUTTON_ID,
            BulletPointType.SQUARE_BULLET_POINT);
    }

    /**
     * Uses the number bullet point button.
     * @param robot Robot that uses the program
     * @param contentsTextArea Contents text area to sets the style for
     */
    void numberBulletPointButton(@NotNull FxRobot robot,
        ExtendedTextArea contentsTextArea) {
        bulletPointButton(robot, contentsTextArea,
            NUMBER_BULLET_POINT_BUTTON_ID, LETTER_BULLET_POINT_BUTTON_ID,
            BulletPointType.NUMBER_BULLET_POINT);
    }

    /**
     * Uses the letter bullet point button.
     * @param robot Robot that uses the program
     * @param contentsTextArea Contents text area to sets the style for
     */
    void letterBulletPointButton(@NotNull FxRobot robot,
        ExtendedTextArea contentsTextArea) {
        bulletPointButton(robot, contentsTextArea,
            LETTER_BULLET_POINT_BUTTON_ID, ROUND_BULLET_POINT_BUTTON_ID,
            BulletPointType.LETTER_BULLET_POINT);
    }

    /**
     * Checks if the given text style is correctly applied to the selected text.
     * @param robot Robot that uses the program
     * @param contentsTextArea Contents text area to sets the style for
     * @param substring Substring to check
     * @param buttonId Button that is being clicked
     * @param style Style that is to be checked
     */
    protected void textStyleButton(@NotNull FxRobot robot,
        ExtendedTextArea contentsTextArea, String substring, String buttonId,
        String style) {
        selectText(robot, contentsTextArea, substring);
        robot.clickOn(buttonId);
        checkStyle(contentsTextArea, style);
    }

    /**
     * Runs the bullet point button.
     * @param robot Robot that uses the program
     * @param contentsTextArea Text area to add the bullet points to
     * @param alternateBulletPointId ID of the alternate bullet point button
     * @param bulletPointButtonId ID of the bullet point button to be clicked
     * @param bulletPointType Bullet point type to be checked
     */
    protected void bulletPointButton(@NotNull FxRobot robot,
        @NotNull ExtendedTextArea contentsTextArea, String bulletPointButtonId,
        String alternateBulletPointId, BulletPointType bulletPointType) {

        //Button Set
        robot.interact(() -> contentsTextArea.appendText("\nFirst Line"));
        robot.clickOn(alternateBulletPointId);
        robot.clickOn(bulletPointButtonId);
        int firstLine
            = contentsTextArea.getText().lastIndexOf("\n") + "\n".length();
        Assertions.assertThat(contentsTextArea.getText().substring(firstLine))
            .startsWith(bulletPointType + " ");

        //Enter Key Set
        robot.press(KeyCode.ENTER).release(KeyCode.ENTER);
        robot.interact(() -> contentsTextArea.appendText("Second Line"));
        int secondLine = contentsTextArea.getText().indexOf("\n", firstLine)
            + "\n".length();
        if (bulletPointType == BulletPointType.NUMBER_BULLET_POINT) {
            Assertions.assertThat(contentsTextArea.getText().substring(
                secondLine)).startsWith("2. ");
        } else if (bulletPointType == BulletPointType.LETTER_BULLET_POINT) {
            Assertions.assertThat(contentsTextArea.getText().substring(
                secondLine)).startsWith("b. ");
        } else {
            Assertions.assertThat(contentsTextArea.getText().substring(
                secondLine)).startsWith(bulletPointType + " ");
        }

        //Button Cancel
        robot.press(KeyCode.ENTER).release(KeyCode.ENTER);
        robot.interact(() -> contentsTextArea.appendText("Third Line"));
        int thirdLine = contentsTextArea.getText().indexOf("\n", secondLine)
            + "\n".length();
        robot.clickOn(bulletPointButtonId);
        if (bulletPointType == BulletPointType.NUMBER_BULLET_POINT) {
            Assertions.assertThat(contentsTextArea.getText().substring(
                thirdLine)).doesNotStartWith("3. ");
        } else {
            Assertions.assertThat(contentsTextArea.getText().substring(
                thirdLine)).doesNotStartWith(bulletPointType + " ");
        }

        //Enter Key Cancel
        robot.press(KeyCode.ENTER).release(KeyCode.ENTER);
        robot.interact(() -> contentsTextArea.appendText("Fourth Line"));
        int fourthLine = contentsTextArea.getText().indexOf("\n", thirdLine)
            + "\n".length();
        if (bulletPointType == BulletPointType.NUMBER_BULLET_POINT) {
            Assertions.assertThat(contentsTextArea.getText().substring(
                fourthLine)).doesNotStartWith("4. ");
        } else {
            Assertions.assertThat(contentsTextArea.getText().substring(
                fourthLine)).doesNotStartWith(bulletPointType + " ");
        }

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
                .doesNotContain(style);
        }
        for (int i = indexRange.getStart(); i < indexRange.getEnd(); i++) {
            Assertions.assertThat(contentsTextArea.getStyleOfChar(i))
                .contains(style);
        }
        for (int i = indexRange.getEnd();
            i < contentsTextArea.getText().length(); i++) {
            Assertions.assertThat(contentsTextArea.getStyleOfChar(i))
                .doesNotContain(style);
        }
    }

    /**
     * Clicks the select attachment button.
     * @param robot Robot that uses the program
     */
    protected void attachmentButton(@NotNull FxRobot robot) {
        robot.interact(() -> {
            try {
                Main.CREATE_SCREEN.getController()
                    .addNewFiles(List.of(Objects.requireNonNull(
                        new Directory(ScreenTestSuite.FILES_DIRECTORY)
                            .listFiles())));
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
        robot.clickOn(ATTACHMENT_REMOVE_BUTTON);
        if (email != null) {
            files = email.getBody().getAttachmentNumber();
        }
        Assertions.assertThat(robot.lookup(SELECTED_FILES_BOXES_ID).queryAs(
            VBox.class).getChildren().size()).isEqualTo(files + 2);
//        robot.clickOn(ATTACHMENT_HYPERLINK);
//        Thread.sleep(3000);
    }

}
