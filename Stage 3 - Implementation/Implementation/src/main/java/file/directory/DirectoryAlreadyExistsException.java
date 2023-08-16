package file.directory;

import java.nio.file.FileAlreadyExistsException;

/**
 * Exception that is thrown when a directory already exists.
 * @author Jordan Jones
 */
public class DirectoryAlreadyExistsException
    extends FileAlreadyExistsException {

    //CONSTANTS
    private static final String MESSAGE
        = "Directory already exists with the path %s";

    /**
     * Creates exception to be thrown if the given directory already exists.
     * @param filePath File path of the directory attempted to be found
     */
    public DirectoryAlreadyExistsException(String filePath) {
        super(String.format(MESSAGE, filePath));
    }

}
