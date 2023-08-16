package gui.components;

import file.html.HTMLValueElement;
import file.html.HTMLVoidElement;
import javafx.beans.NamedArg;
import javafx.scene.control.IndexRange;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.apache.commons.lang3.StringUtils;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.NavigationActions;
import file.html.HTMLElement;
import gui.screens.ExtendedScene;
import org.jetbrains.annotations.NotNull;
import other.Unicode;

/**
 * Extended version of a javafx text area.
 * @author Jordan Jones
 */
public class ExtendedTextArea extends InlineCssTextArea {

    //CONSTANTS
    public static final int DEFAULT_FONT_SIZE = 14;

    //Attributes
    private int characterLimit = Integer.MAX_VALUE;

    /**
     * Creates an extended text field.
     * @param characterLimit Limit of characters that can be typed in
     */
    public ExtendedTextArea(@NamedArg("characterLimit") int characterLimit) {
        setCharacterLimit(characterLimit);
    }

    /**
     * Sets the shortcuts of the extended text area.
     */
    public void setShortcuts() {
        ExtendedScene scene = (ExtendedScene) getScene();
        scene.addShortcut(new KeyCodeCombination(KeyCode.B,
            KeyCombination.CONTROL_DOWN), this::boldSelectedText);
        scene.addShortcut(new KeyCodeCombination(KeyCode.I,
            KeyCombination.CONTROL_DOWN), this::italicizeSelectedText);
        scene.addShortcut(new KeyCodeCombination(KeyCode.S,
            KeyCombination.ALT_DOWN), this::strikethroughSelectedText);
        scene.addShortcut(new KeyCodeCombination(KeyCode.U,
            KeyCombination.CONTROL_DOWN), this::underlineSelectedText);
        setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (KeyCode.ENTER.equals(code)) {
                moveTo(getCaretPosition() - 1);
                selectParagraph();
                if (StringUtils.startsWith(getSelectedText(),
                    Unicode.BULLET_POINT + " ")) {
                    moveTo(getCaretPosition() + 1);
                    lineStart(NavigationActions.SelectionPolicy.CLEAR);
                    insertText(getCaretPosition(), Unicode.BULLET_POINT + " ");
                } else {
                    moveTo(getCaretPosition() + 1);
                }
                deselect();
            }
        });
    }

    /**
     * Sets the character limit of the text field.
     * @param characterLimit Character limit of the text field
     */
    private void setCharacterLimit(int characterLimit) {
        this.characterLimit = characterLimit;
        textProperty().addListener((observable, oldValue, newValue) -> {
            if (getText().length() > getCharacterLimit()) {
                appendText(getText(0, getCharacterLimit()));
            }
        });
    }

    /**
     * Gets the character limit of the text field.
     * @return Character limit of the text field
     */
    private int getCharacterLimit() {
        return characterLimit;
    }

    /**
     * Checks if the text area is empty.
     * @return True if the text area is empty
     */
    public boolean isEmpty() {
        return getText().equals("");
    }

    /**
     * Converts the text into html.
     * @return Text as html
     */
    public String toHTML() {
        for (HTMLElement htmlElement : HTMLElement.HTML_ELEMENTS) {
            toHTMLWithElements(htmlElement);
        }
        for (HTMLValueElement htmlElement : HTMLValueElement.HTML_ELEMENTS) {
            toHTMLWithValueElements(htmlElement);
        }
        toHTMLWithVoidElements();
        return getText();
    }

    /**
     * Adds html elements of the given element type.
     * @param htmlElement Element to add
     */
    private void toHTMLWithElements(HTMLElement htmlElement) {
        for (int i = 0; i < getText().length(); i++) {
            String characterStyle = getStyleOfChar(i);
            if (characterStyle.contains(htmlElement.getJavaFxStyle())) {
                insert(i, htmlElement.getStartTag(), characterStyle);
                for (int j = i + htmlElement.getStartTag().length();
                     j < getText().length(); j++) {
                    if (!getStyleOfChar(j).contains(
                        htmlElement.getJavaFxStyle())) {
                        insert(j, htmlElement.getEndTag(), characterStyle);
                        i = j + htmlElement.getEndTag().length() - 1;
                        break;
                    }
                    if (j == getText().length() - 1) {
                        insert(getText().length(), htmlElement.getEndTag(),
                            characterStyle);
                        i = getText().length();
                        j = getText().length();
                    }
                }
            }
        }
    }

    /**
     * Adds html elements of the given element type.
     * @param htmlElement Element to add
     */
    private void toHTMLWithValueElements(HTMLValueElement htmlElement) {
        for (int i = 0; i < getText().length(); i++) {
            String characterStyle = getStyleOfChar(i);
            if (characterStyle.matches(htmlElement.toMatchesString())) {
                int index = characterStyle.indexOf(
                    htmlElement.getJavaFxStyleNoValue())
                    + htmlElement.getJavaFxStyleNoValue().length();
                String value = characterStyle.substring(index,
                    characterStyle.indexOf(";", index));
                insert(i, htmlElement.getStartTag(value), characterStyle);
                for (int j = i + htmlElement.getStartTag().length();
                     j < getText().length(); j++) {
                    if (!getStyleOfChar(j).matches(
                        htmlElement.toMatchesString())) {
                        insert(j, htmlElement.getEndTag(), characterStyle);
                        i = j + htmlElement.getEndTag().length() - 1;
                        break;
                    }
                    if (j == getText().length() - 1) {
                        insert(getText().length(), htmlElement.getEndTag(),
                            characterStyle);
                        i = getText().length();
                        j = getText().length();
                    }
                }
            }
        }
    }

    /**
     * Adds all html void elements.
     */
    private void toHTMLWithVoidElements() {
        String string = getText();
        for (HTMLVoidElement htmlElement : HTMLVoidElement.HTML_ELEMENTS) {
            string = string.replaceAll(htmlElement.getJavaFxStyle(),
                htmlElement.getStartTag());
        }
        replaceText(string);
    }

    /**
     * Adds html text to the current text in the text area.
     * @param htmlText HTML text to add to the current text in the text area
     */
    public void addHTMLText(String htmlText) {
        appendText(htmlText);
        for (HTMLElement htmlElement : HTMLElement.HTML_ELEMENTS) {
            addHTMLElements(htmlElement);
        }
        for (HTMLValueElement htmlElement : HTMLValueElement.HTML_ELEMENTS) {
            addHTMLValueElements(htmlElement);
        }
        for (HTMLVoidElement htmlElement : HTMLVoidElement.HTML_ELEMENTS) {
            addHTMLVoidElements(htmlElement);
        }
    }

    /**
     * Add HTML elements to the text with the given html element type.
     * @param htmlElement HTML element type to add to the text
     */
    private void addHTMLElements(@NotNull HTMLElement htmlElement) {
        int index;
        do {
            index = getText().indexOf(htmlElement.getStartTag());
            if (index != -1) {
                int startIndex = index + htmlElement.getStartTag().length();
                int endIndex = getText().indexOf(htmlElement.getEndTag());
                for (int i = startIndex; i < endIndex; i++) {
                    setStyle(i, i + 1,
                        getStyleOfChar(i) + htmlElement.getJavaFxStyle());
                }
                replace(index, startIndex, "", "");
                replace(endIndex - htmlElement.getStartTag().length(),
                    (endIndex - htmlElement.getStartTag().length())
                        + htmlElement.getEndTag().length(), "", "");
            }
        } while (index != -1);
    }

    /**
     * Add HTML value elements to the text with the given html element type.
     * @param htmlElement HTML value element type to add to the text
     */
    private void addHTMLValueElements(@NotNull HTMLValueElement htmlElement) {
        int index;
        do {
            index = getText().indexOf(htmlElement.getStartTag());
            if (index != -1) {
                int startIndex = index + htmlElement.getStartTag().length();
                int endIndex = getText().indexOf(htmlElement.getEndTag());
                for (int i = startIndex; i < endIndex; i++) {
                    if (htmlElement == HTMLValueElement.SUBSCRIPT) {
                        subscriptText(i, i + 1);
                    } else if (htmlElement == HTMLValueElement.SUPERSCRIPT) {
                        superscriptText(i, i + 1);
                    } else {
                        String value = getText().substring(
                            getText().indexOf(":", startIndex),
                            getText().indexOf(";", startIndex));
                        setStyle(i, i + 1, getStyleOfChar(i)
                            + htmlElement.getJavaFxStyle(value));
                    }
                }
                replace(index, startIndex, "", "");
                replace(endIndex - htmlElement.getStartTag().length(),
                    (endIndex - htmlElement.getStartTag().length())
                        + htmlElement.getEndTag().length(), "", "");
            }
        } while (index != -1);
    }

    /**
     * Add HTML void elements to the text with the given html element type.
     * @param htmlElement HTML void element type to add to the text
     */
    private void addHTMLVoidElements(@NotNull HTMLVoidElement htmlElement) {
        int index;
        do {
            index = getText().indexOf(htmlElement.getStartTag());
            if (index != -1) {
                int endIndex = index + htmlElement.getStartTag().length();
                replace(index, endIndex, htmlElement.getJavaFxStyle(), "");
            }
        } while (index != -1);
    }

    /**
     * Adds a bullet point.
     */
    public void addBulletPoint() {
        IndexRange indexRange = getSelection();
        int unicodeLength = (Unicode.BULLET_POINT + " ").length();
        selectLine();
        if (!StringUtils.startsWith(getSelectedText(),
            Unicode.BULLET_POINT + " ")) {
            deselect();
            lineStart(NavigationActions.SelectionPolicy.CLEAR);
            insertText(getCaretPosition(), Unicode.BULLET_POINT + " ");
            lineEnd(NavigationActions.SelectionPolicy.CLEAR);
            selectRange(indexRange.getStart() + unicodeLength,
                indexRange.getEnd() + unicodeLength);
        } else {
            replace(getSelection().getStart(), getSelection().getStart()
                + unicodeLength, "", "");
            selectRange(indexRange.getStart() - unicodeLength,
                indexRange.getEnd() - unicodeLength);
        }
        requestFocus();
    }

    /**
     * Changes the font family of the selected text.
     * @param fontFamily Font family to set the selected text to
     */
    public void fontFamilyChangeSelectedText(String fontFamily) {
        for (int i = getSelection().getStart();
            i < getSelection().getEnd(); i++) {
            String characterStyle = getStyleOfChar(i);
            if (characterStyle.matches(
                HTMLValueElement.FONT_FAMILY.toMatchesString())) {
                setStyle(i, i + 1, characterStyle.replaceAll(
                    HTMLValueElement.FONT_FAMILY.getJavaFxRegex(),
                    HTMLValueElement.FONT_FAMILY.getJavaFxStyle(fontFamily)));
            } else {
                setStyle(i, i + 1, HTMLValueElement.FONT_FAMILY
                    .getJavaFxStyle(fontFamily) + characterStyle);
            }
        }
    }

    /**
     * Changes the font size of the selected text.
     * @param fontSize Font size to set the selected text to
     */
    public void fontSizeChangeSelectedText(double fontSize) {
        for (int i = getSelection().getStart();
            i < getSelection().getEnd(); i++) {
            double newFontSize = fontSize;
            String characterStyle = getStyleOfChar(i);
            characterStyle = characterStyle.replaceAll(
                HTMLValueElement.FONT_SIZE.getJavaFxRegex(), "");
            if (characterStyle.matches(
                HTMLValueElement.SUBSCRIPT.toMatchesString())
                || characterStyle.matches(
                HTMLValueElement.SUPERSCRIPT.toMatchesString())) {
                newFontSize = fontSize * HTMLValueElement.SIZE_FACTOR;
            }
            setStyle(i, i + 1, characterStyle + HTMLValueElement.FONT_SIZE
                .getJavaFxStyle(newFontSize));
        }
    }

    /**
     * Bolds the selected text.
     */
    public void boldSelectedText() {
        textStyleSelectedText(HTMLElement.BOLD);
    }

    /**
     * Italicizes the selected text.
     */
    public void italicizeSelectedText() {
        textStyleSelectedText(HTMLElement.ITALIC);
    }

    /**
     * Strikethrough the selected text.
     */
    public void strikethroughSelectedText() {
        textStyleSelectedText(HTMLElement.STRIKETHROUGH);
    }

    /**
     * Subscript the selected text.
     */
    public void subscriptSelectedText() {
        subscriptText(getSelection().getStart(), getSelection().getEnd());
    }

    /**
     * Subscript the given index range.
     * @param start Start of the end index range
     * @param end End of the index range
     */
    private void subscriptText(int start, int end) {
        subsuperscriptText(start, end, HTMLValueElement.SUBSCRIPT,
            HTMLValueElement.SUPERSCRIPT);
    }

    /**
     * Superscript the selected text.
     */
    public void superscriptSelectedText() {
        superscriptText(getSelection().getStart(), getSelection().getEnd());
    }

    /**
     * Superscripts the given index range.
     * @param start Start of the end index range
     * @param end End of the index range
     */
    private void superscriptText(int start, int end) {
        subsuperscriptText(start, end, HTMLValueElement.SUPERSCRIPT,
            HTMLValueElement.SUBSCRIPT);
    }

    /**
     * Subscript or superscripts the given index range.
     * @param start Start of the end index range
     * @param end End of the index range
     * @param applyElement Element to apply
     * @param removeElement Element to remove
     */
    private void subsuperscriptText(int start, int end,
        HTMLValueElement applyElement, HTMLValueElement removeElement) {
        boolean isAdd = applyTextStyle(start, end, applyElement);
        for (int i = start; i < end; i++) {
            String characterStyle = getStyleOfChar(i);
            int index = characterStyle.indexOf("-fx-font-size: ");
            double fontsize = DEFAULT_FONT_SIZE;
            if (index != -1) {
                fontsize = Double.parseDouble(characterStyle.substring(
                    index + "-fx-font-size: ".length()).split(";")[0]);
            }
            if (isAdd) {
                if (!characterStyle.matches(applyElement.toMatchesString())) {
                    if (!characterStyle.matches(
                        removeElement.toMatchesString())) {
                        if (characterStyle.contains(HTMLValueElement
                            .FONT_SIZE.getJavaFxStyleNoValue())) {
                            characterStyle = characterStyle.replaceAll(
                                HTMLValueElement.FONT_SIZE.getJavaFxRegex(),
                                HTMLValueElement.FONT_SIZE.getJavaFxStyle(
                                    fontsize * HTMLValueElement.SIZE_FACTOR));
                        } else {
                            characterStyle += HTMLValueElement.FONT_SIZE
                                .getJavaFxStyle(
                                    fontsize * HTMLValueElement.SIZE_FACTOR);
                        }
                        setStyle(i, i + 1, applyElement.getJavaFxStyle(
                            fontsize * HTMLValueElement.TRANSLATE_FACTOR)
                            + " " + characterStyle);
                    } else {
                        int newIndex = characterStyle.indexOf(
                            applyElement.getJavaFxStyleNoValue());
                        double translate = Double.parseDouble(
                            characterStyle.substring(newIndex + applyElement
                                .getJavaFxStyleNoValue().length())
                                .split(";")[0]);
                        int factor = 1;
                        if (applyElement == HTMLValueElement.SUPERSCRIPT) {
                            factor = -1;
                        }
                        characterStyle = characterStyle.replaceAll(
                            removeElement.getJavaFxRegex(),
                            applyElement.getJavaFxStyle(translate * factor));
                        setStyle(i, i + 1, characterStyle);
                    }
                }
            } else {
                characterStyle = characterStyle.replaceAll(
                    applyElement.getJavaFxRegex(), "");
                characterStyle = characterStyle.replaceAll(
                    HTMLValueElement.FONT_SIZE.getJavaFxRegex(),
                    HTMLValueElement.FONT_SIZE.getJavaFxStyle(
                        fontsize / HTMLValueElement.SIZE_FACTOR));
                setStyle(i, i + 1, characterStyle);
            }
        }
    }

    /**
     * Strikethrough the selected text.
     */
    public void underlineSelectedText() {
        textStyleSelectedText(HTMLElement.UNDERLINE);
    }

    /**
     * Sets the style type of the selected text.
     * @param htmlElement Tag to get the style from
     */
    private void textStyleSelectedText(HTMLElement htmlElement) {
        int start = getSelection().getStart();
        int end = getSelection().getEnd();
        if (applyTextStyle(start, end, htmlElement)) {
            for (int i = start; i < end; i++) {
                if (!getStyleOfChar(i).equals("")) {
                    setStyle(i, i + 1, htmlElement.getJavaFxStyle()
                        + " " + getStyleOfChar(i));
                } else {
                    setStyle(i, i + 1, htmlElement.getJavaFxStyle());
                }
            }
        } else {
            for (int i = start; i < end; i++) {
                setStyle(i, i + 1, getStyleOfChar(i)
                    .replaceAll(htmlElement.getJavaFxStyle(), ""));
            }
        }
    }

    /**
     * Checks if the text style should be applied or removed.
     * @param start Start of the index range
     * @param end End of the index range
     * @param htmlElement Html element to check if it needs to be applied
     * @return True if the text style should be applied
     */
    private boolean applyTextStyle(int start, int end,
        HTMLElement htmlElement) {
        boolean isAdd = false;
        for (int i = start; i < end; i++) {
            if (!getStyleOfChar(i).matches(htmlElement.toMatchesString())) {
                isAdd = true;
                break;
            }
        }
        return isAdd;
    }

}
