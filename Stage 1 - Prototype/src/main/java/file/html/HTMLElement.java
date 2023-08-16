package file.html;

/**
 * Represents a HTML element.
 * @author Jordan Jones
 */
public class HTMLElement {

    //CONSTANTS
    public static final HTMLElement BOLD = new HTMLElement(
        "<b>", "</b>", "-fx-font-weight: bold;");
    public static final HTMLElement ITALIC = new HTMLElement(
        "<i>", "</i>", "-fx-font-style: italic;");
    public static final HTMLElement STRIKETHROUGH = new HTMLElement(
        "<del>", "</del>", "-fx-strikethrough: true;");
    public static final HTMLElement UNDERLINE = new HTMLElement(
        "<u>", "</u>", "-fx-underline: true;");
    public static final HTMLElement[] HTML_ELEMENTS = {BOLD, ITALIC,
        STRIKETHROUGH, UNDERLINE};

    //Attributes
    private String startTag;
    private String endTag;
    private String javaFxStyle;

    /**
     * Creates a html tag.
     * @param startTag Start tag
     * @param endTag End tag
     * @param javaFxStyle JavaFx representation of the element
     */
    public HTMLElement(String startTag, String endTag, String javaFxStyle) {
        setStartTag(startTag);
        setEndTag(endTag);
        setJavaFxStyle(javaFxStyle);
    }

    /**
     * Sets the start tag.
     * @param startTag start tag
     */
    private void setStartTag(String startTag) {
        this.startTag = startTag;
    }

    /**
     * Sets the end tag.
     * @param endTag End tag
     */
    private void setEndTag(String endTag) {
        this.endTag = endTag;
    }

    /**
     * Sets the javafx style.
     * @param javaFxStyle JavaFx style
     */
    private void setJavaFxStyle(String javaFxStyle) {
        this.javaFxStyle = javaFxStyle;
    }

    /**
     * Gets the start tag.
     * @return Start tag
     */
    public String getStartTag() {
        return startTag;
    }

    /**
     * Gets the end tag.
     * @return End tag
     */
    public String getEndTag() {
        return endTag;
    }

    /**
     * Gets the javafx style.
     * @return Javafx style
     */
    public String getJavaFxStyle() {
        return javaFxStyle;
    }

    /**
     * Gets the matches string.
     * @return Matches string
     */
    public String toMatchesString() {
        return ".*" + javaFxStyle + ".*";
    }

}
