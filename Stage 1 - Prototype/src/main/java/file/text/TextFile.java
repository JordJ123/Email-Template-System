package file.text;

import file.ExtendedFile;
import file.directory.DirectoryNotFoundException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Represents a text file.
 * @author Jordan Jones
 */
public class TextFile extends ExtendedFile {

    //CONSTANTS
    private static final String[] MIME_TYPES = {"text/plain"};
    private static final String FILE_ERROR = "File is not a text file";

    /**
     * Creates a text file object.
     * @param filePath Path of the text file
     */
    public TextFile(String filePath) {
        super(filePath);
    }

    /**
     * Checks if the mime type is a text mime type.
     * @param mimeType Mime type to be checked
     * @return True if the mime type is a text mime type
     */
    public static boolean isTextMimeType(String mimeType) {
        for (String possibleType : MIME_TYPES) {
            if (mimeType.equals(possibleType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the contents of the text file.
     * @param text Text to be added to the text file
     * @throws DirectoryNotFoundException Thrown if directory does not exist
     * @throws FileNotFoundException Error if the file can't be found
     */
    public void setContents(String text) throws FileNotFoundException {
        try {
            PrintWriter textFile = new PrintWriter(this);
            textFile.print(text);
            textFile.close();
        } catch (FileNotFoundException fileNotFoundException) {
            throw new DirectoryNotFoundException(
                getParentFile().getAbsolutePath());
        }
    }

    /**
     * Gets the contents of the text file.
     * @return Contents of the text file
     * @throws FileNotFoundException Error if the file can't be found
     */
    public Scanner getContents() throws FileNotFoundException {
        return new Scanner(this);
    }

    /**
     * Deletes the text file off of the file system.
     * @throws IOException Thrown if error with deleting file
     */
    @Override
    public void remove() throws IOException {
        try {
            super.remove();
        } catch (FileNotFoundException fileNotFoundException) {
            throw new TextFileNotFoundException(getAbsolutePath());
        }
    }

    /**
     * Checks if the file exists and if not, creates it.
     * @throws IllegalArgumentException Thrown if file exists and is not HTML
     * @throws IOException Thrown if error with creating a file
     */
    protected void checkIfExists() throws IOException {
        if (exists()) {
            if (!isTextMimeType(getMimeType(this))) {
                throw new IllegalArgumentException(FILE_ERROR);
            }
        } else {
            createNewFile();
        }
    }

}
