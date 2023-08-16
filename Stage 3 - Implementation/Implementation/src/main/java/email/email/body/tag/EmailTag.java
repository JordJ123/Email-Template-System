package email.email.body.tag;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents an email tag.
 * @author Jordan Jones
 */
public class EmailTag implements Serializable {

    //CONSTANTS
    public static final String TAG_START = "<!";
    public static final String TAG_END = "!>";

    //Attributes
    private String name;

    /**
     * Creates a tag.
     * @param name Name of the tag
     */
    public EmailTag(String name) {
        setName(name);
    }

    /**
     * Gets the regex of a tag.
     * @return Regex of a tag
     */
    @Contract(pure = true)
    public static @NotNull String getRegex() {
        return TAG_START + "[^" + TAG_START + "]+" + TAG_END;
    }

    /**
     * Sets the string of the tag.
     * @param name String of the tag
     */
    private void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the string of the tag.
     * @return String of the tag
     */
    public String getName() {
        return name;
    }

    /**
     * Checks if the given object is the same as this tag.
     * @param o object to compare
     * @return True if the objects are the same
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || (o.getClass() != EmailTag.class
            && o.getClass() != EmailListTag.class)) {
            return false;
        }
        EmailTag emailTag = (EmailTag) o;
        return getName().split("\\(List\\)")[0].equals(
            emailTag.getName().split("\\(List\\)")[0]);
    }

    /**
     * Gets the hash code of the tag.
     * @return Hash code of the tag
     */
    @Override
    public int hashCode() {
        return Objects.hash(getName().split("\\(List\\)")[0]);
    }

    /**
     * Returns the tag in a tag string format.
     * @return Tag in a tag string format
     */
    @Override
    public String toString() {
        return TAG_START + getName() + TAG_END;
    }

}
