package file.serializable;

import java.io.FileNotFoundException;

/**
 * Exception when a serializable file can not be found when needed.
 * @author Jordan Jones
 */
public class SerializableFileNotFoundException extends FileNotFoundException {

    //CONSTANTS
    private static final String MESSAGE
        = "%s (The system cannot find the file specified)";

    /**
     * Creates a serializable file not found exception.
     * @param filePath File path of the file that is trying to be found
     */
    public SerializableFileNotFoundException(String filePath) {
        super(String.format(MESSAGE, filePath));
    }

}
