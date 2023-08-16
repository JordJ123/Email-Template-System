package file.directory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import file.*;
import file.html.HTMLFile;
import file.text.TextFile;

/**
 * Represents a directory on a file system.
 * @author Jordan Jones
 */
public class Directory extends ExtendedFile {

    /**
     * Creates the directory onto the file system.
     */
    @Override
    public void create() throws DirectoryAlreadyExistsException {
        if (!exists()) {
            mkdirs();
        } else {
            throw new DirectoryAlreadyExistsException(getAbsolutePath());
        }
    }

    /**
     * Creates a directory object.
     * @param directoryPath Path of the directory
     */
    public Directory(String directoryPath) {
        super(directoryPath);
    }

    /**
     * Gets the files found in the directory.
     * @return Files in the directory
     * @throws IOException Error if a file can't be found
     */
    public ExtendedFile[] readFiles() throws IOException  {
        File[] directoryFiles = listFiles();
        assert directoryFiles != null;
        ExtendedFile[] files = new ExtendedFile[directoryFiles.length];
        for (int i = 0; i < directoryFiles.length; i++) {
            files[i] = readFile(directoryFiles[i]);
        }
        return files;
    }

    /**
     * Gets a file from the directory.
     * @param index Index of the file
     * @return File from the directory
     * @throws IOException Thrown with error with finding the file
     */
    public ExtendedFile readFile(int index) throws IOException {
        return readFile(Objects.requireNonNull(listFiles())[index]);
    }

    /**
     * Reads a given file.
     * @param file to be read
     * @return Extended version of a given file
     * @throws IOException Error with the file
     */
    @Contract("_ -> new")
    private @NotNull ExtendedFile readFile(@NotNull File file)
        throws IOException {
        if (file.isDirectory()) {
            return new Directory(file.getAbsolutePath());
        } else {
            String mimeType = getMimeType(file);
            if (HTMLFile.isHTMLMimeType(mimeType)) {
                return new HTMLFile(file.getPath());
            } else if (SoundFile.isSoundMimeType(mimeType)) {
                return new SoundFile(file.getPath());
            } else if (TextFile.isTextMimeType(mimeType)) {
                return new TextFile(file.getPath());
            } else if (VideoFile.isVideoMimeType(mimeType)) {
                return new VideoFile(file.getPath());
            } else {
                return new ExtendedFile(file.getPath());
            }
        }
    }


    /**
     * Deletes the file off of the file system.
     * @throws FileCanNotDeleteException Thrown if file can't be deleted
     * @throws IOException Thrown if error with deleting file
     */
    @Override
    public void remove() throws IOException, FileCanNotDeleteException {
        if (!exists()) {
            throw new DirectoryNotFoundException(
                getAbsolutePath());
        }
        deleteFiles();
        Files.delete(toPath());
    }

    /**
     * Deletes all the files in the directory.
     * @throws FileCanNotDeleteException Thrown if a files can't be deleted
     */
    public void deleteFiles() throws FileCanNotDeleteException {
        deleteFiles(this);
    }

    /**
     * Deletes directories in a directory in the directory.
     * @throws FileCanNotDeleteException Thrown if a files can't be deleted
     */
    public void deleteDirectories() throws FileCanNotDeleteException {
        for (File subFile : Objects.requireNonNull(listFiles())) {
            if (subFile.listFiles() != null) {
                deleteFiles(subFile);
                if (!subFile.delete()) {
                    throw new FileCanNotDeleteException(subFile);
                }
            }
        }
    }

    /**
     * Deletes files in a directory in the directory.
     * @param file File to delete files from
     * @throws FileCanNotDeleteException Thrown if a files can't be deleted
     */
    private void deleteFiles(@NotNull File file)
        throws FileCanNotDeleteException {
        for (File subFile : Objects.requireNonNull(file.listFiles())) {
            if (subFile.listFiles() != null) {
                deleteFiles(subFile);
            }
            if (!subFile.delete()) {
                throw new FileCanNotDeleteException(subFile);
            }
        }
    }

    /**
     * Checks if the directory contains the given file.
     * @param file File to check is contained
     * @return True if the directory contains the file
     * @throws IOException Thrown if error with a file in the directory
     */
    public boolean contains(ExtendedFile file) throws IOException {
        for (ExtendedFile contents : readFiles()) {
           if (contents.equals(file)) {
                return true;
           }
        }
        return false;
    }

}
