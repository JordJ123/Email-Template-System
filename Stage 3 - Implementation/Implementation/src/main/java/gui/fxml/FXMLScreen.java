package gui.fxml;

import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * Represents a screen fxml file.
 * @param <E> Type of the controller that it stores
 * @author Jordan Jones
 */
public class FXMLScreen<E> extends FXML<E> {

    //ERRORS
    private static final String ICON_ERROR =
        "Image file with the file name %s does not exist";

    //Static Attributes
    private static Stage primaryStage;
    private static boolean isClosed;

    //Attributes
    private String windowTitle;

    /**
     * Creates a screen object from a fxml file.
     * @param fxmlName Identifier of the screen used for file paths
     * @param windowTitle Title of the window containing the screen
     */
    public FXMLScreen(String fxmlName, String windowTitle) {
        super(fxmlName);
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
        FXMLScreen.primaryStage = primaryStage;
        if (iconFileName != null) {
            try {
                Image image = new Image(Objects.requireNonNull(iconFileName));
                primaryStage.getIcons().add(image);
            } catch (NullPointerException nullPointerException) {
                throw new IllegalArgumentException(
                    String.format(ICON_ERROR, iconFileName));
            }
        }
        double height = Screen.getPrimary().getVisualBounds().getHeight();
        primaryStage.maximizedProperty().addListener((ov, t, t1) -> {
            if (!t1) {
                primaryStage.sizeToScene();
                if (primaryStage.getHeight() > height) {
                    primaryStage.setHeight(height);
                }
                if (primaryStage.getY() < 0) {
                    primaryStage.setY(0);
                }
            }
        });
        primaryStage.requestFocus();
        primaryStage.setOnCloseRequest(windowEvent -> setIsClosed(true));
    }

    /**
     * Sets if the window has been closed.
     * @param isClosed True if the window has been closed
     */
    private static void setIsClosed(boolean isClosed) {
        FXMLScreen.isClosed = isClosed;
    }

    /**
     * Gets the primary stage window.
     * @return Primary stage window
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Gets if the window has been closed.
     * @return True if the window has been closed
     */
    public static boolean getIsClosed() {
        return isClosed;
    }

    /**
     * Sets the window title.
     * @param windowTitle Window title
     */
    private void setWindowTitle(String windowTitle) {
        this.windowTitle = windowTitle;
    }

    /**
     * Gets the window title.
     * @return Window title
     */
    public String getWindowTitle() {
        return windowTitle;
    }

    /**
     * Gets the controller.
     * @return Controller
     */
    public E getController() {
        return super.getController();
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
            Throwable error = loadError.getCause();
            if (error.getClass() == InvocationTargetException.class) {
                error = error.getCause();
            }
            throw new FXMLLoadException(fullPath, error.toString());
        }
        setController(loader.getController());
        getPrimaryStage().setTitle(getWindowTitle());
        getPrimaryStage().show();
    }

}
