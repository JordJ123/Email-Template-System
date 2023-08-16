package gui.fxml;

/**
 * Represents an fxml file.
 * @param <E> Type of the controller that it stores
 * @author Jordan Jones
 */
public abstract class FXML<E> {

    //CONSTANTS
    protected static final String FXML_FILE = "%s.fxml";

    //Attributes
    private String fxmlName;
    private E controller;

    /**
     * Creates a screen object from a fxml file.
     * @param fxmlName Identifier of the screen used for file paths
     */
    public FXML(String fxmlName) {
        setFxmlName(fxmlName);
    }

    /**
     * Sets the fxml name.
     * @param fxmlName Fxml name
     */
    private void setFxmlName(String fxmlName) {
        this.fxmlName = fxmlName;
    }

    /**
     * Sets the controller.
     * @param controller Controller
     */
    public void setController(E controller) {
        this.controller = controller;
    }

    /**
     * Gets the fxml name.
     * @return Fxml name
     */
    protected String getFxmlName() {
        return fxmlName;
    }

    /**
     * Gets the controller.
     * @return Controller
     */
    public E getController() {
        return controller;
    }

}
