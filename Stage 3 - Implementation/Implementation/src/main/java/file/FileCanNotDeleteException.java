package file;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Exception thrown when a file can not be deleted.
 * @author Jordan Jones
 */
public class FileCanNotDeleteException extends Exception {

    //CONSTANTS
    private static final String MESSAGE = "Given file (%s) can't not be "
        + "deleted probably due to the file currently being open";

    /**
     * Creates the exception to be thrown.
     * @param file file that can not be deleted
     */
    public FileCanNotDeleteException(@NotNull File file) {
        super(String.format(MESSAGE, file.getAbsolutePath()));
    }

}
