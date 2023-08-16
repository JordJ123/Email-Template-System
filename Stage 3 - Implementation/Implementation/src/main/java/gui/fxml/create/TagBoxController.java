package gui.fxml.create;

import email.email.body.tag.EmailListTag;
import email.email.body.tag.EmailTag;
import gui.components.ExtendedTextArea;
import gui.fxml.FXMLController;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import util.BulletPointType;

/**
 * Controller for a tag box on the create screen.
 * @author Jordan Jones
 */
public class TagBoxController extends FXMLController {

    //FXML Attributes
    @FXML private HBox tagBox;
    @FXML private Button insertButton;
    @FXML private ComboBox<BulletPointType> bulletPointBox;

    //Attributes
    private CreateScreenController createScreenController;
    private EmailTag emailTag;

    /**
     * Adds a tag to the text area.
     */
    @FXML
    private void onTagButton() {
        ExtendedTextArea extendedTextArea = getCreateScreenController()
            .getContentsTextArea();
        extendedTextArea.insertText(extendedTextArea.getCaretPosition(),
            getTag().toString());
    }

    /**
     * Sets the email list tag bullet point.
     */
    @FXML
    private void onBulletPointBox() {
        ((EmailListTag) getTag()).setBulletPointType(getBulletPointBox()
            .getValue());
    }

    /**
     * Deletes the tag.
     */
    @FXML
    private void onDeleteButton() {
        getCreateScreenController().getTags().remove(getTag());
        getCreateScreenController().getTagBoxes().getChildren()
            .remove(getTagBox());
        getCreateScreenController().getContentsTextArea().replaceOccurrences(
            getTag().toString(), "");
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
        setBulletPointBox();
        setCreateScreenController(createScreenController);
    }

    /**
     * Sets the tag button.
     */
    private void setTagButton() {
        insertButton.setText(getTag().getName());
    }

    /**
     * Sets the bullet point box.
     */
    private void setBulletPointBox() {
        if (getTag().getClass() == EmailListTag.class) {
            bulletPointBox.setButtonCell(new ListCell<>() {
                @Override
                public void updateItem(BulletPointType item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setText(item + "  ");
                        setAlignment(Pos.CENTER);
                        setPadding(new Insets(5, 0, 5, 0));
                        setStyle("-fx-font-size: 18");
                    }
                }
            });
            bulletPointBox.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(BulletPointType item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setText(item.toString());
                        setAlignment(Pos.CENTER);
                        setPadding(new Insets(5, 0, 5, 0));
                        setStyle("-fx-font-size: 18");
                    }
                }
            });
            bulletPointBox.getItems().addAll(
                BulletPointType.BULLET_POINT_TYPES);
            bulletPointBox.getSelectionModel().selectFirst();
        } else {
            getTagBox().getChildren().remove(bulletPointBox);
        }
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
     * Gets the bullet point box.
     * @return Bullet point box
     */
    private ComboBox<BulletPointType> getBulletPointBox() {
        return bulletPointBox;
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
