package gui.screens.tags;

import gui.components.ExtendedTextField;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;

/**
 * Controller for the text field in a list tag box.
 * @author Jordan Jones
 */
public class ListTagBoxTextFieldController {

    //FXML Attributes
    @FXML private HBox hBox;
    @FXML private ExtendedTextField listTagValueTextField;
    @FXML private Button upButton;
    @FXML private Button downButton;

    //Attributes
    private ObservableList<Node> listTagBoxControllerTextFields;

    /**
     * Moves the tag value up the list.
     */
    @FXML
    private void onUpButton() {
        int index = getListTagBoxControllerTextFields().indexOf(getHBox()) - 1;
        getListTagBoxControllerTextFields().remove(getHBox());
        getListTagBoxControllerTextFields().add(index, getHBox());
        getDownButton().setVisible(true);
        if (index == 0) {
            getUpButton().setVisible(false);
            ((HBox) getListTagBoxControllerTextFields().get(1))
                .getChildren().get(1).setVisible(true);
            if (getListTagBoxControllerTextFields().size() == 2) {
                ((HBox) getListTagBoxControllerTextFields().get(1))
                    .getChildren().get(2).setVisible(false);
            }
        }
        if (index == getListTagBoxControllerTextFields().size() - 2) {
            ((HBox) getListTagBoxControllerTextFields().get(
                getListTagBoxControllerTextFields().size() - 1))
                .getChildren().get(2).setVisible(false);
        }
    }

    /**
     * Moves the tag value down the list.
     */
    @FXML
    private void onDownButton() {
        int index = getListTagBoxControllerTextFields().indexOf(getHBox()) + 1;
        getListTagBoxControllerTextFields().remove(getHBox());
        getListTagBoxControllerTextFields().add(index, getHBox());
        getUpButton().setVisible(true);
        if (index == getListTagBoxControllerTextFields().size() - 1) {
            getDownButton().setVisible(false);
            ((HBox) getListTagBoxControllerTextFields().get(index - 1))
                .getChildren().get(2).setVisible(true);
            if (getListTagBoxControllerTextFields().size() == 2) {
                ((HBox) getListTagBoxControllerTextFields().get(index - 1))
                    .getChildren().get(1).setVisible(false);
            }
        }
        if (index == 1) {
            ((HBox) getListTagBoxControllerTextFields().get(0))
                .getChildren().get(1).setVisible(false);
        }
    }

    /**
     * Removes the tag value from the list.
     */
    @FXML
    private void onRemoveButton() {
        int index = getListTagBoxControllerTextFields().indexOf(getHBox());
        getListTagBoxControllerTextFields().remove(getHBox());
        if (index == 0) {
            ((HBox) getListTagBoxControllerTextFields().get(0)).getChildren()
                .get(1).setVisible(false);
            if (getListTagBoxControllerTextFields().size() > 1) {
                ((HBox) getListTagBoxControllerTextFields().get(0))
                    .getChildren().get(2).setVisible(true);
            }
        } else if (index == getListTagBoxControllerTextFields().size()) {
            ((HBox) getListTagBoxControllerTextFields().get(
                getListTagBoxControllerTextFields().size() - 1)).getChildren()
                .get(2).setVisible(false);
            if (getListTagBoxControllerTextFields().size() > 1) {
                ((HBox) getListTagBoxControllerTextFields().get(
                    getListTagBoxControllerTextFields().size() - 1))
                    .getChildren().get(1).setVisible(true);
            }
        }
        if (getListTagBoxControllerTextFields().size() == 1) {
            ((HBox) getListTagBoxControllerTextFields().get(0)).getChildren()
                .get(3).setVisible(false);
        }
    }

    /**
     * Sets the controller of the screen.
     * @param listTagBoxController Controller of the parent screen
     * @param tagValue Value of the list tag
     */
    public void setController(
        @NotNull ListTagBoxController listTagBoxController, String tagValue) {
        setListTagValueTextField(tagValue);
        setListTagBoxControllerTextFields(listTagBoxController);
        setUpButton();
        setDownButton();
    }

    /**
     * Sets the tag value in the extended text field.
     * @param tagValue Tag value to set in the extended text field
     */
    private void setListTagValueTextField(String tagValue) {
        this.listTagValueTextField.setText(tagValue);
    }

    /**
     * Sets the list tag box controller text fields.
     * @param listTagBoxController Controllers of the parent screen
     */
    private void setListTagBoxControllerTextFields(
        @NotNull ListTagBoxController listTagBoxController) {
        this.listTagBoxControllerTextFields = listTagBoxController
            .getListTagValueTextFieldsBox().getChildren();
    }

    /**
     * Sets if the up button should be visible or not.
     */
    private void setUpButton() {
        int index = getListTagBoxControllerTextFields().indexOf(getHBox());
        if (index == 0) {
            upButton.setVisible(false);
        }
    }

    /**
     * Sets if the down button should be visible or not.
     */
    private void setDownButton() {
        int index = getListTagBoxControllerTextFields().indexOf(getHBox());
        if (index == getListTagBoxControllerTextFields().size() - 1) {
            downButton.setVisible(false);
        }
    }

    /**
     * Gets the HBox.
     * @return HBox
     */
    private HBox getHBox() {
        return hBox;
    }

    /**
     * Gets the up button.
     * @return Up Button
     */
    private Button getUpButton() {
        return upButton;
    }

    /**
     * Gets the down button.
     * @return Down Button
     */
    private Button getDownButton() {
        return downButton;
    }

    /**
     * Gets the list tag box controller text fields.
     * @return List tag box controller text fields
     */
    private ObservableList<Node> getListTagBoxControllerTextFields() {
        return listTagBoxControllerTextFields;
    }

}
