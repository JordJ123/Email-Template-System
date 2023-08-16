package gui.components;

import gui.fxml.FXMLComponent;
import gui.fxml.ExtendedScene;
import gui.fxml.FXMLScreen;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Represents a popup component.
 * @author Jordan Jones
 */
public class PopUp {

    //Attributes
    private Stage stage;

    /**
     * Creates a popup component.
     * @param contents FXML components that the popup will contain
     * @throws IOException Thrown if error with the fxml
     */
    public PopUp(@NotNull FXMLComponent<?> contents) throws IOException {
        setStage(new Stage());
        getStage().initModality(Modality.APPLICATION_MODAL);
        getStage().initOwner(FXMLScreen.getPrimaryStage());
        getStage().setScene(new ExtendedScene(contents.load()));
    }

    /**
     * Sets the stage.
     * @param stage Stage
     */
    private void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Gets the stage.
     * @return Stage
     */
    private Stage getStage() {
        return stage;
    }

    /**
     * Displays the popup.
     */
    public void display() {
        getStage().show();
    }

    /**
     * Closes the popup.
     */
    public void close() {
        getStage().close();
    }

}
