package file;

import file.directory.Directory;
import org.apache.tika.Tika;
import org.jboss.resource.adapter.jdbc.remote.SerializableInputStream;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.Objects;

/**
 * Represents a file with extra methods.
 * @author Jordan Jones
 */
public class ExtendedFile extends File {

    //CONSTANTS
    private static final int BUFFER_SIZE_BYTES = 4096;
    public static final String SAVES_PATH = "saves/";

    //Attributes
    private SerializableInputStream inputStream;

    /**
     * Creates an extended file object.
     * @param filePath Path of the file object
     */
    public ExtendedFile(String filePath) {
        super(filePath);
    }

    /**
     * Creates an extended file object with a given input stream.
     * @param filePath Path of the file object
     * @param inputStream Input stream for the file's data
     */
    public ExtendedFile(String filePath, SerializableInputStream inputStream) {
        super(filePath);
        setInputStream(inputStream);
    }

    /**
     * Gets the file type of the given file.
     * @param file File to be checked
     * @return File type
     * @throws IOException Error if file does not actually exist
     */
    protected static String getMimeType(File file) throws IOException {
        return new Tika().detect(file);
    }

    /**
     * Sets the input stream.
     * @param inputStream Input stream
     */
    private void setInputStream(SerializableInputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * Gets the input stream.
     * @return Input stream
     */
    private SerializableInputStream getInputStream() {
        return inputStream;
    }

    /**
     * Gets the directory of the file.
     * @return Directory the file is found in
     */
    @Contract(" -> new")
    public @NotNull Directory getDirectory() {
        return new Directory(getPath().substring(0,
            getPath().lastIndexOf("\\")));
    }

    /**
     * Checks if the two objects are equal.
     * @param obj Object to check is equal
     * @return True if the objects are the same or contain the same file path
     */
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            if (obj.getClass() == ExtendedFile.class) {
                return getPath().equals(((ExtendedFile) obj).getPath());
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the hash code of the extended file.
     * @return Hash code of the extended file
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(getPath());
    }

    /**
     * Creates the file on the file system.
     * @throws FileAlreadyExistsException Thrown if file already exists
     * @throws IOException Thrown if error with creating the file
     */
    public void create() throws IOException {
        if (!exists()) {
            super.createNewFile();
            if (getInputStream() != null) {
                FileOutputStream output = new FileOutputStream(getPath());
                byte[] buffer = new byte[BUFFER_SIZE_BYTES];
                int byteRead;
                while ((byteRead = getInputStream().read(
                    buffer, 0, BUFFER_SIZE_BYTES)) != -1) {
                    output.write(buffer, 0, byteRead);
                }
                output.close();
            }
        } else {
            throw new FileAlreadyExistsException(getAbsolutePath());
        }
    }

    /**
     * Deletes the file off of the file system.
     * @throws FileNotFoundException Thrown if file does not exist
     * @throws IOException Thrown if error with deleting file
     */
    public void remove() throws IOException {
        if (!exists()) {
            throw new FileNotFoundException(
                getAbsolutePath());
        }
        Files.delete(toPath());
    }

}
