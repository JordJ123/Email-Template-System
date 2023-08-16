package file.html;

import file.ExtendedFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * Represents a HTML file.
 * @author Jordan Jones
 */
public class HTMLFile extends ExtendedFile {

    //CONSTANTS
    private static final String[] MIME_TYPES = {"text/html"};
    private static final String FILE_ERROR = "File is not a html file";

    /**
     * Creates a html file object.
     * @param filePath Path of the file object
     * @throws IOException Thrown if error with creating a file
     */
    public HTMLFile(String filePath) throws IOException {
        super(filePath);
        checkIfExists();
    }

    /**
     * Checks if the mime type is a text mime type.
     * @param mimeType Mime type to be checked
     * @return True if the mime type is a text mime type
     */
    public static boolean isHTMLMimeType(String mimeType) {
        for (String possibleType : MIME_TYPES) {
            if (mimeType.equals(possibleType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the contents of the html file.
     * @return Contents of the html file
     * @throws FileNotFoundException Error if the file can't be found
     */
    public Scanner getContents() throws FileNotFoundException {
        return new Scanner(this);
    }

    /**
     * Checks if the file exists, and if not, creates it.
     * @throws IllegalArgumentException Thrown if file exists and is not HTML
     * @throws IOException Thrown if error with creating a file
     */
    protected void checkIfExists() throws IOException {
        if (exists()) {
            if (!isHTMLMimeType(getMimeType(this))) {
                throw new IllegalArgumentException(FILE_ERROR);
            }
        } else {
            createNewFile();
        }
    }

}
