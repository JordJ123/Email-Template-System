package file.serializable;


import file.ExtendedFile;
import file.directory.DirectoryNotFoundException;

import java.io.*;

/**
 * Represents a serializable file.
 * @param <T> Type of object to be stored in the text file
 * @author Jordan Jones
 */
public class SerializableFile<T> extends ExtendedFile {

    /**
     * Creates an serializable file object.
     * @param filePath Path of the file object
     */
    public SerializableFile(String filePath) {
        super(filePath);
    }

    /**
     * Serializes the given object into the file.
     * @param object Object to be serialized
     * @throws DirectoryNotFoundException Thrown if directory does not exist
     * @throws IOException Thrown if error serialising the file
     */
    public void serialize(T object) throws IOException {
        FileOutputStream fileOutput;
        try {
            fileOutput = new FileOutputStream(getAbsolutePath());
        } catch (FileNotFoundException fileNotFoundException) {
            throw new DirectoryNotFoundException(
                getParentFile().getAbsolutePath());
        }
        ObjectOutputStream objOutput = new ObjectOutputStream(fileOutput);
        objOutput.writeObject(object);
        objOutput.close();
        fileOutput.close();
    }

    /**
     * Deserializes the given object from the file.
     * @return Object from the file (null if file doesn't exist)
     * @throws ClassNotFoundException Thrown if file is for a non-local class
     * @throws IOException Thrown if error deserializing the file
     * @throws OutOfDateClassException Thrown if the file is for an oud class
     * @throws SerializableFileNotFoundException Thrown if file does not exist
     */
    public T deserialize() throws ClassNotFoundException, IOException {
        try {
            FileInputStream fileInput = new FileInputStream(getAbsolutePath());
            ObjectInputStream objInput = new ObjectInputStream(fileInput);
            Object object = objInput.readObject();
            objInput.close();
            fileInput.close();
            return (T) object;
        } catch (FileNotFoundException fileNotFoundException) {
            throw new SerializableFileNotFoundException(getAbsolutePath());
        } catch (InvalidClassException invalidClassException) {
            throw new OutOfDateClassException(invalidClassException.classname);
        }
    }

}
