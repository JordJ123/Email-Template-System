package gui.fxml.create;

import file.ExtendedFile;
import gui.fxml.FXMLController;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * Controller for the selected file box.
 * @author Jordan Jones
 */
public class SelectedFileBoxController extends FXMLController {

    //@FXML Attributes
    @FXML private HBox selectedFileBox;
    @FXML private Hyperlink filePathHyperLink;

    //Attributes
    private CreateScreenController createScreenController;
    private ExtendedFile file;

    /**
     * Opens the file explorer and focuses on the selected file.
     * @throws IOException Thrown if it can't find file
     */
    @FXML
    private void onFilePathHyperlink() throws IOException {
        Runtime.getRuntime().exec("explorer /select, "
            + getFile().getAbsolutePath());
    }

    /**
     * Removes the selected file.
     */
    @FXML
    private void onRemoveButton() {
        getCreateScreenController().getSelectedFiles().remove(getFile());
        getCreateScreenController().getSelectedFileBoxes().getChildren().remove(
            getSelectedFileBox());
    }

    /**
     * Sets the controller's data.
     * @param createScreenController Controller of the parent screen
     * @param file File the box is linked to
     */
    public void setController(CreateScreenController createScreenController,
        ExtendedFile file) {
        setFile(file);
        setFilePathHyperLink();
        setCreateScreenController(createScreenController);

    }

    /**
     * Sets the file path hyperlink text.
     */
    private void setFilePathHyperLink() {
        filePathHyperLink.setText(getFile().getName());
    }

    /**
     * Sets the controller of the parent screen.
     * @param createScreenController Controller of the parent screen
     */
    private void setCreateScreenController(
        CreateScreenController createScreenController) {
        this.createScreenController = createScreenController;
    }

    /**
     * Sets the file.
     * @param file File the box is linked to
     */
    private void setFile(@NotNull ExtendedFile file) {
        this.file = file;
    }

    /**
     * Gets the main pane.
     * @return Main pane
     */
    private HBox getSelectedFileBox() {
        return selectedFileBox;
    }

    /**
     * Gets the controller of the parent screen.
     * @return Controller of the parent screen
     */
    private CreateScreenController getCreateScreenController() {
        return createScreenController;
    }

    /**
     * Gets the selected file.
     * @return Selected file
     */
    private File getFile() {
        return file;
    }

}
