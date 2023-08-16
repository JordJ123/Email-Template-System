package gui.screens;

import javafx.fxml.LoadException;

/**
 * Exception when there is an error loading a fxml file.
 * @author Jordan Jones
 */
public class FXMLLoadException extends LoadException {

    //CONSTANTS
    private static final String MESSAGE =
        "Could not parse the FXML file with the file path %s due to the given "
            + "error \n(%s)";

    /**
     * Creates exception to be thrown.
     * @param filePath File path of the fxml file
     * @param cause Exception that caused the error
     */
    public FXMLLoadException(String filePath, String cause) {
        super(String.format(MESSAGE, filePath, cause));
    }

}
