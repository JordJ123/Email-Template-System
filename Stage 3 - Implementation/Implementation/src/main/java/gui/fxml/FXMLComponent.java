package gui.fxml;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

/**
 * Represents a component fxml.
 * @param <E> Type of the controller that it stores
 * @author Jordan Jones
 */
public class FXMLComponent<E> extends FXML<E> {

    /**
     * Creates a component object from a fxml file.
     * @param fxmlName Identifier of the screen used for file paths
     */
    public FXMLComponent(String fxmlName) {
        super(fxmlName);
    }

    /**
     * Gets the controller.
     * @return Controller
     */
    public E getController() {
        return super.getController();
    }

    /**
     * Loads the component.
     * @throws IOException Thrown if error with the fxml file
     * @return Base node of the component
     */
    public Parent load() throws IOException {
        String fullPath = String.format(FXML_FILE, getFxmlName());
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fullPath));
        Parent parent = loader.load();
        setController(loader.getController());
        return parent;
    }

}
