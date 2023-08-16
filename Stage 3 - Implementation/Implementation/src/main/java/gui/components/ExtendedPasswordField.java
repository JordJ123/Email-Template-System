package gui.components;

import javafx.beans.NamedArg;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.PasswordField;

/**
 * Extended version of a javafx text field.
 * @author Jordan Jones
 */
public class ExtendedPasswordField extends PasswordField {

    //Attributes
    private String basePromptText = null;
    private int characterLimit;

    /**
     * Creates an extended text field.
     * @param characterLimit Limit of characters that can be typed in
     */
    public ExtendedPasswordField(
        @NamedArg("characterLimit") int characterLimit) {
        setCharacterLimit(characterLimit);
        textProperty().addListener((observable, oldValue, newValue) -> {
            if (getText().length() > getCharacterLimit()) {
                setText(getText(0, getCharacterLimit()));
            }
        });
    }

    /**
     * Sets the character limit of the text field (less than 0 sets it to max).
     * @param characterLimit Character limit of the text field
     */
    private void setCharacterLimit(int characterLimit) {
        if (characterLimit > 0) {
            this.characterLimit = characterLimit;
        } else {
            this.characterLimit = Integer.MAX_VALUE;
        }
    }

    /**
     * Gets the character limit of the text field.
     * @return Character limit of the text field
     */
    private int getCharacterLimit() {
        return characterLimit;
    }

    /**
     * Checks if the text field is empty.
     * @return True if the text field is empty
     */
    public boolean isEmpty() {
        return getText().equals("");
    }

     /**
     * Displays a prompt error for the text field.
     * @param errorText Error text for the text field
     */
    public void displayError(String errorText) {
        if (basePromptText == null) {
            basePromptText = getPromptText();
        }
        ChangeListener changeListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue,
                Object newValue) {
                setPromptText(basePromptText);
                styleProperty().setValue("");
                textProperty().removeListener(this);
            }
        };
        textProperty().addListener(changeListener);
        setStyle("-fx-prompt-text-fill:red;");
        setPromptText(errorText);
    }

}
