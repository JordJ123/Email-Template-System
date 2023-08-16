package file;

import java.io.IOException;

/**
 * Represents a video file.
 * @author Jordan Jones
 */
public class VideoFile extends ExtendedFile {

    //CONSTANTS
    private static final String[] MIME_TYPES = {"video/mp4"};
    private static final String FILE_ERROR = "File is not a video file";

    /**
     * Creates an extended file object.
     * @param filePath Path of the file object
     */
    public VideoFile(String filePath) {
        super(filePath);
    }

    /**
     * Checks if the mime type is a video mime type.
     * @param mimeType Mime type to be checked
     * @return True if the mime type is a video mime type
     */
    public static boolean isVideoMimeType(String mimeType) {
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
            if (!isVideoMimeType(getMimeType(this))) {
                throw new IllegalArgumentException(FILE_ERROR);
            }
        } else {
            createNewFile();
        }
    }

}
