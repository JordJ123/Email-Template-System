package gui.screens.tags;


import java.io.IOException;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import email.email.body.tag.EmailListTag;
import gui.components.ExtendedTextField;
import gui.screens.Screen;

/**
 * Controller that represents a list tag box.
 * @author Jordan Jones
 */
public class ListTagBoxController {

    //CONSTANT
    private static final String MISSING_TAG_VALUE_ERROR
        = "Please enter a value for this tag";

    //FXML Attributes
    @FXML private Label listTagNameLabel;
    @FXML private VBox listTagValueTextFieldsBox;

    /**
     * Adds a text field tag box.
     * @throws IOException Thrown if error with list tag box fxml file
     */
    @FXML
    private void onAddTagValueTextFieldButton() throws IOException {
        addTagValueTextField("");
    }

    /**
     * Sets the controller's data.
     * @param emailListTag List tag of the tag box
     * @param tagValues Values of the tag
     * @throws IOException Thrown if error with list tag box fxml file
     */
    public void setController(@NotNull EmailListTag emailListTag,
        String[] tagValues) throws IOException {
        setListTagNameLabel(emailListTag.getName());
        setListTagValueTextFieldsBox(tagValues);
    }

    /**
     * Sets the tag name of the tag box.
     * @param tagName Tag name of the tag box
     */
    private void setListTagNameLabel(String tagName) {
        listTagNameLabel.setText(tagName);
    }

    /**
     * Sets the tag values of the tag box.
     * @param tagValues Tag values of the tag box
     * @throws IOException Thrown if error with list tag box fxml file
     */
    public void setListTagValueTextFieldsBox(String @NotNull [] tagValues)
        throws IOException {
        getListTagValueTextFieldsBox().getChildren().clear();
        for (String tagValue : tagValues) {
            addTagValueTextField(tagValue);
        }
        if (getListTagValueTextFieldsBox().getChildren().size() <= 1) {
            ((HBox) (getListTagValueTextFieldsBox().getChildren().get(0)))
                .getChildren().get(3).setVisible(false);
        }
    }

    /**
     * Gets the tag value text fields.
     * @return Tag value text field
     */
    public VBox getListTagValueTextFieldsBox() {
        return listTagValueTextFieldsBox;
    }

    /**
     * Gets the tag values of the tag box.
     * @return Tag values of the tag box
     */
    public String[] getTagValues() {
        ArrayList<String> tagValues = new ArrayList<>();
        for (Node node : getListTagValueTextFieldsBox().getChildren()) {
            tagValues.add(((ExtendedTextField)
                ((HBox) node).getChildren().get(0)).getText());
        }
        return tagValues.toArray(new String[0]);
    }

    /**
     * Checks if the tag box controller has a tag value for every field.
     * @return True if every field does have a tag value
     */
    public boolean hasTagValues() {
        for (String tagValue : getTagValues()) {
            if (tagValue.equals("")) {
                return false;
            }
        }
        return true;
    }

    /**
     * Displays an error for the tag input.
     */
    public void displayTagError() {
        for (Node hBox : getListTagValueTextFieldsBox().getChildren()) {
            ExtendedTextField extendedTextField
                = ((ExtendedTextField) ((HBox) hBox).getChildren().get(0));
            if (extendedTextField.isEmpty()) {
                extendedTextField.displayError(MISSING_TAG_VALUE_ERROR);
            }
        }
    }

    /**
     * Adds a tag value text field.
     * @param tagValue Tag value
     * @throws IOException Thrown if error with list tag box fxml file
     */
    private void addTagValueTextField(String tagValue) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
            getClass().getResource("ListTagBoxTextField.fxml"));
        HBox hBox = fxmlLoader.load();
        if (getListTagValueTextFieldsBox().getChildren().size() == 1) {
            ((HBox) (getListTagValueTextFieldsBox().getChildren().get(0)))
                .getChildren().get(3).setVisible(true);
        }
        if (getListTagValueTextFieldsBox().getChildren().size() >= 1) {
            ((HBox) (getListTagValueTextFieldsBox().getChildren().get(
                getListTagValueTextFieldsBox().getChildren().size() - 1)))
                .getChildren().get(2).setVisible(true);
        }
        getListTagValueTextFieldsBox().getChildren().add(hBox);
        ((ListTagBoxTextFieldController) fxmlLoader.getController())
            .setController(this, tagValue);
        Screen.getPrimaryStage().sizeToScene();
    }

}
