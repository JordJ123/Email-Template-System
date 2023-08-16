package util;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Represents a bullet point type.
 * @author Jordan Jones
 */
public class BulletPointType implements Serializable {

    //CONSTANTS
    public static final BulletPointType ROUND_BULLET_POINT
        = new BulletPointType("\u2022", "\u2022");
    public static final BulletPointType SQUARE_BULLET_POINT
        = new BulletPointType("\u25AA", "\u25AA");
    public static final BulletPointType NUMBER_BULLET_POINT
        = new BulletPointType("1.", "[0-9]+\\.");
    public static final BulletPointType LETTER_BULLET_POINT
        = new BulletPointType("a.", "[a-z]+\\.");
    public static final BulletPointType[] BULLET_POINT_TYPES = {
        ROUND_BULLET_POINT, SQUARE_BULLET_POINT, NUMBER_BULLET_POINT,
        LETTER_BULLET_POINT};

    //Attributes
    private String representation;
    private String regex;

    /**
     * Creates a bullet point type.
     * @param representation String representation of the bullet point type
     * @param regex Regex of the bullet point
     */
    public BulletPointType(String representation, String regex) {
        setRepresentation(representation);
        setRegex(regex);
    }

    /**
     * Sets the bullet point type.
     * @param representation String representation of the bullet point type
     */
    private void setRepresentation(String representation) {
        this.representation = representation;
    }

    /**
     * Gets the regex of the bullet point.
     * @param regex Regex of the bullet point
     */
    private void setRegex(String regex) {
        this.regex = regex;
    }

    /**
     * Gets the string representation.
     * @return String representation
     */
    private String getRepresentation() {
        return representation;
    }

    /**
     * Gets the regex of the bullet point.
     * @return Regex of the bullet point
     */
    private String getRegex() {
        return regex;
    }

    /**
     * Converts object to a string.
     * @return String format of an object
     */
    @Override
    public String toString() {
        return getRepresentation();
    }

    /**
     * Checks if the line starts with the bullet point type.
     * @param line Line to check
     * @return True if the line starts with the bullet point type
     */
    public boolean isBulletPointType(@NotNull String line) {
        return line.matches(getRegex() + " .*");
    }

}
