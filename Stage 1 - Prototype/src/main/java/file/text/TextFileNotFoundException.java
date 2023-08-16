package file.text;

import java.io.FileNotFoundException;

/**
 * Exception when a text file can not be found when needed.
 * @author Jordan Jones
 */
public class TextFileNotFoundException extends FileNotFoundException {

    //CONSTANTS
    private static final String MESSAGE
        = "%s (The system cannot find the file specified)";

    /**
     * Creates a text file not found exception.
     * @param filePath File path of the file that is trying to be found
     */
    public TextFileNotFoundException(String filePath) {
        super(String.format(MESSAGE, filePath));
    }

}
