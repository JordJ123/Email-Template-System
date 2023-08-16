package gui.components;

import file.html.HTMLValueElement;
import file.html.HTMLVoidElement;
import gui.Main;
import javafx.beans.NamedArg;
import javafx.scene.control.IndexRange;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.apache.commons.lang3.StringUtils;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.NavigationActions;
import file.html.HTMLElement;
import gui.fxml.ExtendedScene;
import org.jetbrains.annotations.NotNull;
import util.Alphabet;
import util.BulletPointType;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extended version of a javafx text area.
 * @author Jordan Jones
 */
public class ExtendedTextArea extends InlineCssTextArea {

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
                setEnterShortcut();
            }
        });
    }

    /**
     * Sets the enter shortcut.
     */
    private void setEnterShortcut() {
        moveTo(getCaretPosition() - 1);
        selectParagraph();
        for (BulletPointType bpt : BulletPointType.BULLET_POINT_TYPES) {
            if (bpt.isBulletPointType(getSelectedText())) {
                if (bpt == BulletPointType.NUMBER_BULLET_POINT) {
                    int number = Integer.parseInt(getSelectedText()
                        .substring(0, getSelectedText().indexOf(".")));
                    number++;
                    moveTo(getCaretPosition() + 1);
                    lineStart(NavigationActions.SelectionPolicy.CLEAR);
                    insertText(getCaretPosition(), number + ". ");
                    int position = getCaretPosition();
                    int index;
                    do {
                        index = getText().indexOf("\n", getCaretPosition());
                        if (index > -1) {
                            selectRange(index + 1, index + 1);
                            selectLine();
                            if (StringUtils.startsWith(
                                getSelectedText(), number + ". ")) {
                                int length = (number + ". ").length();
                                number++;
                                replace(getSelection().getStart(),
                                    getSelection().getStart() + length,
                                    number + ". ", "");
                            }
                            deselect();
                        }
                    } while (index > -1);
                    selectRange(position, position);
                } else if (bpt == BulletPointType.LETTER_BULLET_POINT) {
                    String letter = getSelectedText()
                        .substring(0, getSelectedText().indexOf("."));
                    Alphabet alphabet = new Alphabet(false);
                    int number = alphabet.index(letter);
                    number++;
                    moveTo(getCaretPosition() + 1);
                    lineStart(NavigationActions.SelectionPolicy.CLEAR);
                    insertText(getCaretPosition(),
                        alphabet.letter(number) + ". ");
                    int position = getCaretPosition();
                    int index;
                    do {
                        index = getText().indexOf("\n", getCaretPosition());
                        if (index > -1) {
                            selectRange(index + 1, index + 1);
                            selectLine();
                            if (StringUtils.startsWith(
                                getSelectedText(),
                                alphabet.letter(number) + ". ")) {
                                int length = (alphabet.letter(number)
                                    + ". ").length();
                                number++;
                                replace(getSelection().getStart(),
                                    getSelection().getStart() + length,
                                    alphabet.letter(number) + ". ", "");
                            }
                            deselect();
                        }
                    } while (index > -1);
                    selectRange(position, position);
                } else {
                    moveTo(getCaretPosition() + 1);
                    lineStart(NavigationActions.SelectionPolicy.CLEAR);
                    insertText(getCaretPosition(), bpt + " ");
                }
                deselect();
                return;
            }
        }
        moveTo(getCaretPosition() + 1);
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
    public void addHTMLText(@NotNull String htmlText) {
        htmlText = htmlText.replaceAll("<meta [^>]*>", "");
        htmlText = htmlText.replaceAll("&quot;", "\"");
        appendText(htmlText);
        for (HTMLElement htmlElement : HTMLElement.HTML_ELEMENTS) {
            addHTMLElements(htmlElement);
        }
        for (HTMLValueElement htmlElement : HTMLValueElement.HTML_ELEMENTS) {
            addHTMLValueElements(htmlElement);
        }
        addHTMLValueElements(HTMLValueElement.FONT_FAMILY_TWO);
        addHTMLValueElements(HTMLValueElement.FONT_SIZE_TWO);
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
        Pattern pattern = Pattern.compile(htmlElement.getStartTagRegex());
        Matcher matcher = pattern.matcher(getText());
        while (matcher.find()) {
            int index = matcher.start();
            String text = matcher.group();
            int startIndex = index + text.length();
            int endIndex = getText().indexOf(htmlElement.getEndTag(),
                startIndex);
            for (int i = startIndex; i < endIndex; i++) {
                if (htmlElement == HTMLValueElement.SUBSCRIPT) {
                    subscriptText(i, i + 1);
                } else if (htmlElement == HTMLValueElement.SUPERSCRIPT) {
                    superscriptText(i, i + 1);
                } else if (htmlElement == HTMLValueElement.FONT_SIZE) {
                    String value = htmlElement.getJavaFxStyle(text.substring(
                        text.indexOf(":") + 1, text.indexOf("px;")));
                    if (!getStyleOfChar(i).contains(value)) {
                        setStyle(i, i + 1, getStyleOfChar(i) + value);
                    }
                } else if (htmlElement == HTMLValueElement.FONT_FAMILY
                    || htmlElement == HTMLValueElement.FONT_FAMILY_TWO) {
                    String value = text.substring(
                        text.indexOf(":") + 1, text.indexOf(";"));
                    value = value.replaceAll("\"", "");
                    setStyle(i, i + 1, getStyleOfChar(i)
                        + htmlElement.getJavaFxStyle(value));
                } else {
                    String value = text.substring(
                        text.indexOf(":") + 1, text.indexOf(";"));
                    setStyle(i, i + 1, getStyleOfChar(i)
                        + htmlElement.getJavaFxStyle(value));
                }
            }
            System.out.println(index + " " + startIndex + " " + endIndex);
            replace(index, startIndex, "", "");
            replace(endIndex - text.length(), (endIndex - text.length())
                + htmlElement.getEndTag().length(), "", "");
            matcher = pattern.matcher(getText());
        }
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
     * @param bulletPointType Type of bullet point to add
     */
    public void addBulletPointType(BulletPointType bulletPointType) {
        ArrayList<Integer> lineIndexes = lineIndexes();
        boolean isAdd = isAddBulletPoint(lineIndexes, bulletPointType);
        removeBulletPoints(lineIndexes);
        if (isAdd) {
            addBulletPoints(lineIndexes, bulletPointType);
        }
        lineEnd(NavigationActions.SelectionPolicy.CLEAR);
        requestFocus();
    }

    /**
     * Replaces occurrences of a given text with other text in the text area.
     * @param text Text to replace
     * @param replaceText Text to replace with
     */
    public void replaceOccurrences(String text, String replaceText) {
        int index;
        do {
            index = getText().indexOf(text);
            if (index != -1) {
                replaceText(index, index + text.length(), replaceText);
            }
        } while (index != -1);
    }

    /**
     * Finds the starting index for every line.
     * @return Starting index for every line
     */
    private @NotNull ArrayList<Integer> lineIndexes() {
        ArrayList<Integer> lineIndexes = new ArrayList<>();
        IndexRange indexRange = getSelection();
        int currentIndex = indexRange.getStart();
        while (true) {
            int index = getText().indexOf("\n", currentIndex);
            if (index != -1 && index < indexRange.getEnd()
                && !lineIndexes.contains(index + 1)) {
                lineIndexes.add(index + 1);
                currentIndex = index + 1;
            } else {
                break;
            }
        }
        selectRange(indexRange.getStart(), indexRange.getStart());
        selectLine();
        lineIndexes.add(0, getSelection().getStart());
        return lineIndexes;
    }

    /**
     * Checks if the bullet point needs to be added or be removed.
     * @param lineIndexes Indexes of the lines
     * @param bulletPointType Type of bullet point to add
     * @return True if adding bullet points
     */
    private boolean isAddBulletPoint(@NotNull ArrayList<Integer> lineIndexes,
        BulletPointType bulletPointType) {
        boolean isAdd = false;
        for (Integer lineIndex : lineIndexes) {
            selectRange(lineIndex, lineIndex);
            selectLine();
            if (!bulletPointType.isBulletPointType(getSelectedText())) {
                isAdd = true;
                break;
            }
        }
        return isAdd;
    }

    /**
     * Removes bullet points that don't match the bullet point type to be added.
     * @param lineIndexes Indexes of the lines
     */
    private void removeBulletPoints(
        @NotNull ArrayList<Integer> lineIndexes) {
        for (int i = 0; i < lineIndexes.size(); i++) {
            selectRange(lineIndexes.get(i), lineIndexes.get(i));
            selectLine();
            for (BulletPointType bpt : BulletPointType.BULLET_POINT_TYPES) {
                if (bpt.isBulletPointType(getSelectedText())) {
                    int length = getSelectedText().substring(0,
                        getSelectedText().indexOf(" ") + 1).length();
                    replace(getSelection().getStart(),
                        getSelection().getStart() + length, "", "");
                    for (int j = i + 1; j < lineIndexes.size(); j++) {
                        int value = lineIndexes.get(j);
                        lineIndexes.set(j, value - length);
                    }
                    break;
                }
            }
        }
    }

    /**
     * Adds/Deletes bullet points to the text area.
     * @param lineIndexes Indexes of the lines
     * @param bulletPointType Type of bullet point to add
     */
    private void addBulletPoints(ArrayList<Integer> lineIndexes,
        BulletPointType bulletPointType) {
        if (bulletPointType == BulletPointType.NUMBER_BULLET_POINT) {
            int extra = 0;
            int number = 1;
            for (Integer lineIndex : lineIndexes) {
                selectRange(lineIndex + extra, lineIndex + extra);
                selectLine();
                lineStart(NavigationActions.SelectionPolicy.CLEAR);
                insertText(getCaretPosition(), number + ". ");
                number++;
                extra += (number + ". ").length();
            }
        } else if (bulletPointType == BulletPointType.LETTER_BULLET_POINT) {
            int extra = 0;
            int number = 0;
            Alphabet alphabet = new Alphabet(false);
            for (Integer lineIndex : lineIndexes) {
                selectRange(lineIndex + extra, lineIndex + extra);
                selectLine();
                lineStart(NavigationActions.SelectionPolicy.CLEAR);
                insertText(getCaretPosition(), alphabet.letter(number)
                    + ". ");
                number++;
                extra += (number + ". ").length();
            }
        } else {
            int loops = 0;
            int bulletPointLength = (bulletPointType + " ").length();
            for (Integer lineIndex : lineIndexes) {
                int extra = loops * bulletPointLength;
                selectRange(lineIndex + extra, lineIndex + extra);
                selectLine();
                lineStart(NavigationActions.SelectionPolicy.CLEAR);
                insertText(getCaretPosition(), bulletPointType + " ");
                loops++;
            }
        }
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
            double fontsize = Main.SMALL_FONT_SIZE;
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
                        double translate = Math.abs(Double.parseDouble(
                            characterStyle.substring(newIndex + applyElement
                                .getJavaFxStyleNoValue().length())
                                .split(";")[0]));
                        characterStyle = characterStyle.replaceAll(
                            removeElement.getJavaFxRegex(),
                            applyElement.getJavaFxStyle(translate));
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
