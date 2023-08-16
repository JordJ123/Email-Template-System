package gui.screens;

import java.io.IOException;
import java.util.Objects;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a screen that can be created from a fxml file.
 * @param <E> Type of the controller that it stores
 * @author Jordan Jones
 */
public class Screen<E> {

    //CONSTANTS
    private static final String FXML_FILE = "%s.fxml";

    //ERRORS
    private static final String FILE_PATH_ERROR =
        "FXML file with the file path %s does not exist";
    private static final String ICON_ERROR =
        "Image file with the file name %s does not exist";

    //Static Attributes
    private static Stage primaryStage;

    //Attributes
    private String fxmlName;
    private String windowTitle;
    private E controller;

    /**
     * Creates a screen object from a fxml file.
     * @param fxmlName Identifier of the screen used for file paths
     * @param windowTitle Title of the window containing the screen
     */
    public Screen(String fxmlName, String windowTitle) {
        setFxmlName(fxmlName);
        setWindowTitle(windowTitle);
    }

    /**
     * Sets the primary stage window.
     * @param primaryStage Primary stage window
     * @param iconFileName File name for the window icon (null for default)
     * @throws IllegalArgumentException Thrown if file path does not exist
     */
    public static void setPrimaryStage(@NotNull Stage primaryStage,
        String iconFileName) {
        Screen.primaryStage = primaryStage;
        primaryStage.setResizable(false);
        if (iconFileName != null) {
            try {
                Image image = new Image(Objects.requireNonNull(iconFileName));
                primaryStage.getIcons().add(image);
            } catch (NullPointerException nullPointerException) {
                throw new IllegalArgumentException(
                    String.format(ICON_ERROR, iconFileName));
            }
        }
    }

    /**
     * Gets the primary stage window.
     * @return Primary stage window
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Sets the fxml name.
     * @param fxmlName Fxml name
     */
    private void setFxmlName(String fxmlName) {
        this.fxmlName = fxmlName;
    }

    /**
     * Sets the window title.
     * @param windowTitle Window title
     */
    private void setWindowTitle(String windowTitle) {
        this.windowTitle = windowTitle;
    }

    /**
     * Sets the controller.
     * @param controller Controller
     */
    private void setController(E controller) {
        this.controller = controller;
    }

    /**
     * Gets the fxml name.
     * @return Fxml name
     */
    private String getFxmlName() {
        return fxmlName;
    }

    /**
     * Gets the window title.
     * @return Window title
     */
    public String getWindowTitle() {
        return windowTitle;
    }

    /**
     * Gets the controller of the screen.
     * @return Controller of the screen
     */
    public E getController() {
        return controller;
    }

    /**
     * Loads the screen onto the window.
     * @throws FXMLLoadException Thrown if error with the fxml file
     * @throws IOException Thrown if error with the fxml file
     */
    public void load() throws IOException {
        String fullPath = String.format(FXML_FILE, getFxmlName());
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fullPath));
        try {
            getPrimaryStage().setScene(new ExtendedScene(loader.load()));
        } catch (LoadException loadError) {
            throw new FXMLLoadException(fullPath, loadError.getMessage());
        }
        setController(loader.getController());
        getPrimaryStage().setTitle(getWindowTitle());
        getPrimaryStage().show();
    }

}
