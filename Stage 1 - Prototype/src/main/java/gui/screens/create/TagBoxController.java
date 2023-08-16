package gui.screens.create;

import email.email.body.tag.EmailTag;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

/**
 * Controller for a tag box on the create screen.
 * @author Jordan Jones
 */
public class TagBoxController {

    //FXML Attributes
    @FXML private HBox tagBox;
    @FXML private Button insertButton;

    //Attributes
    private CreateScreenController createScreenController;
    private EmailTag emailTag;

    /**
     * Adds a tag to the text area.
     */
    @FXML
    private void onTagButton() {
        getCreateScreenController().getContentsTextArea()
            .appendText(getTag().toString());
    }

    /**
     * Deletes the tag.
     */
    @FXML
    private void onDeleteButton() {
        getCreateScreenController().getTags().remove(getTag());
        getCreateScreenController().getTagBoxes().getChildren()
            .remove(getTagBox());
    }

    /**
     * Sets the controller's data.
     * @param createScreenController Controller of the parent screen
     * @param emailTag Tag of the tag button
     */
    public void setController(CreateScreenController createScreenController,
        EmailTag emailTag) {
        setTag(emailTag);
        setTagButton();
        setCreateScreenController(createScreenController);
    }

    /**
     * Sets the tag button.
     */
    private void setTagButton() {
        insertButton.setText(getTag().getName());
    }

    /**
     * Sets the controller of the parent screen.
     * @param createScreenController Controller of the parent screen
     */
    public void setCreateScreenController(
        CreateScreenController createScreenController) {
        this.createScreenController = createScreenController;
    }

    /**
     * Sets the tag related to the button.
     * @param emailTag Tag related to the button
     */
    public void setTag(EmailTag emailTag) {
        this.emailTag = emailTag;
    }

    /**
     * Gets the main pane.
     * @return Main pane
     */
    private HBox getTagBox() {
        return tagBox;
    }

    /**
     * Gets the controller of the parent screen.
     * @return Controller of the parent screen
     */
    private CreateScreenController getCreateScreenController() {
        return createScreenController;
    }

    /**
     * Gets the tag.
     * @return Tag
     */
    private EmailTag getTag() {
        return emailTag;
    }

}
