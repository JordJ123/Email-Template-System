package file.serializable;

import java.io.InvalidClassException;

/**
 * Exception to be thrown when a class is invalid due to out of date issues.
 * @author Jordan Jones
 */
public class OutOfDateClassException extends InvalidClassException {

    //CONSTANT
    private static final String MESSAGE
        = "Serializable file uses an out of date version of this class";

    /**
     * Creates the exception to be thrown.
     * @param classname Name of the class that is causing out of date issues
     */
    public OutOfDateClassException(String classname) {
        super(classname, MESSAGE);
    }

}
