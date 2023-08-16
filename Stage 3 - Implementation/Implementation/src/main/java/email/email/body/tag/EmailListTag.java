package email.email.body.tag;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import util.BulletPointType;

/**
 * Represents a list tag.
 * @author Jordan Jones
 */
public class EmailListTag extends EmailTag {

    //CONSTANTS
    public static final String LIST_TAG_IDENTIFIER = "(List)";

    //Attributes
    private BulletPointType bulletPointType
        = BulletPointType.BULLET_POINT_TYPES[0];

    /**
     * Creates a list tag.
     * @param name Name of the tag
     */
    public EmailListTag(String name) {
        super(name + LIST_TAG_IDENTIFIER);
    }

    /**
     * Gets the regex of the list tag identifier.
     * @return Regex of the list tag identifier
     */
    @Contract(pure = true)
    public static @NotNull String getRegex() {
        return "\\(List\\)";
    }

    /**
     * Sets the bullet type.
     * @param bulletPointType Bullet type
     */
    public void setBulletPointType(BulletPointType bulletPointType) {
        this.bulletPointType = bulletPointType;
    }

    /**
     * Gets the bullet type.
     * @return Bullet type
     */
    public BulletPointType getBulletPointType() {
        return bulletPointType;
    }

}
