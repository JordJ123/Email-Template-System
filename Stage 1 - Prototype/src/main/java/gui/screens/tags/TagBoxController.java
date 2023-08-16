package gui.screens.tags;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;
import email.email.body.tag.EmailTag;
import gui.components.ExtendedTextField;

/**
 * Controller that represents a tag box.
 * @author Jordan Jones
 */
public class TagBoxController {

    //CONSTANT
    public static final String MISSING_TAG_VALUE_ERROR
        = "Please enter a value for this tag";

    //FXML Attributes
    @FXML private Label tagNameLabel;
    @FXML private ExtendedTextField tagValueTextField;

    /**
     * Sets the controller's data.
     * @param emailTag Tag of the tag box
     * @param tagValue Value of the tag
     */
    public void setController(@NotNull EmailTag emailTag, String tagValue) {
        setTagNameLabel(emailTag.getName());
        setTagValueTextField(tagValue);
    }

    /**
     * Sets the tag name of the tag box.
     * @param tagName Tag name of the tag box
     */
    private void setTagNameLabel(String tagName) {
        tagNameLabel.setText(tagName);
    }

    /**
     * Sets the tag value of the tag box.
     * @param tagValue Tag value of the tag box
     */
    public void setTagValueTextField(String tagValue) {
        tagValueTextField.setText(tagValue);
    }

    /**
     * Gets the tag value text field.
     * @return Tag value text field
     */
    private ExtendedTextField getTagValueTextField() {
        return tagValueTextField;
    }

    /**
     * Gets the tag value of the tag box.
     * @return Tag value of the tag box
     */
    public String getTagValue() {
        return getTagValueTextField().getText();
    }

    /**
     * Checks if the tag box controller has a tag value.
     * @return True if the box does have a tag value
     */
    public boolean hasTagValue() {
        return !getTagValueTextField().isEmpty();
    }

    /**
     * Displays an error for the tag input.
     */
    public void displayTagError() {
        getTagValueTextField().displayError(MISSING_TAG_VALUE_ERROR);
    }

}
