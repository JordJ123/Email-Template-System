package file.directory;

import java.io.FileNotFoundException;

/**
 * Exception that is thrown when a directory can not be found.
 * @author Jordan Jones
 */
public class DirectoryNotFoundException extends FileNotFoundException {

    //CONSTANTS
    private static final String MESSAGE = "Can not find the directory %s";

    /**
     * Creates exception to be thrown if the given directory does not exist.
     * @param filePath File path of the directory attempted to be found
     */
    public DirectoryNotFoundException(String filePath) {
        super(String.format(MESSAGE, filePath));
    }

}
