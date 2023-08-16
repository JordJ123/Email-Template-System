package file;

import gui.sound.Sound;

import java.io.IOException;

/**
 * Represents a sound file.
 * @author Jordan Jones
 */
public class SoundFile extends ExtendedFile {

    //CONSTANTS
    private static final String[] MIME_TYPES = {"audio/mpeg"};
    private static final String FILE_ERROR = "File is not a sound file";

    /**
     * Creates a sound file object.
     * @param filePath Path of the file object
     * @throws IOException Thrown if error with creating a file
     */
    public SoundFile(String filePath) throws IOException {
        super(filePath);
        checkIfExists();
    }

    /**
     * Checks if the mime type is a text mime type.
     * @param mimeType Mime type to be checked
     * @return True if the mime type is a text mime type
     */
    public static boolean isSoundMimeType(String mimeType) {
        for (String possibleType : MIME_TYPES) {
            if (mimeType.equals(possibleType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the file exists, and if not, creates it.
     * @throws IllegalArgumentException Thrown if file exists and is not HTML
     * @throws IOException Thrown if error with creating a file
     */
    protected void checkIfExists() throws IOException {
        if (exists()) {
            if (!isSoundMimeType(getMimeType(this))) {
                throw new IllegalArgumentException(FILE_ERROR);
            }
        } else {
            createNewFile();
        }
    }

    /**
     * Converts sound file to a sound object.
     * @return Sound object
     */
    public Sound toSound() {
        return new Sound(getAbsolutePath());
    }

}
