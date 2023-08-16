package file.html;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a HTML value element, a HTML element that contains a given value.
 * @author Jordan Jones
 */
public class HTMLValueElement extends HTMLElement {

    //CONSTANTS
    public static final double SIZE_FACTOR = 0.75;
    public static final double TRANSLATE_FACTOR = 0.25;
    public static final HTMLValueElement FONT_FAMILY = new HTMLValueElement(
        "<span style='font-family: %s;'>", "<span style='font-family: [^;]+;'>",
        "</span>", "-fx-font-family: \"%s\";", "-fx-font-family: [^-]*;");
    public static final HTMLValueElement FONT_FAMILY_TWO = new HTMLValueElement(
        "<span style=\"font-family: %s;\">",
        "<span style=\"font-family: [^;]+;\">",
        "</span>", "-fx-font-family: \"%s\";", "-fx-font-family: [^-]*;");
    public static final HTMLValueElement FONT_SIZE = new HTMLValueElement(
        "<span style='font-size: %spx;'>", "<span style='font-size: [^;]+px;'>",
        "</span>", "-fx-font-size: %s;", "-fx-font-size: [^-]*;");
    public static final HTMLValueElement FONT_SIZE_TWO = new HTMLValueElement(
        "<span style=\"font-size: %spx;\">",
        "<span style=\"font-size: [^;]+px;\">",
        "</span>", "-fx-font-size: %s;", "-fx-font-size: [^-]*;");
    public static final HTMLValueElement SUBSCRIPT = new HTMLValueElement(
        "<sub>", "<sub>", "</sub>", "-fx-translate-y: %s;",
        "-fx-translate-y: [^-]*;");
    public static final HTMLValueElement SUPERSCRIPT = new HTMLValueElement(
        "<sup>", "<sup>", "</sup>", "-fx-translate-y: -%s;",
        "-fx-translate-y: -[^-]*;");
    public static final HTMLValueElement[] HTML_ELEMENTS = {FONT_FAMILY,
        SUBSCRIPT, SUPERSCRIPT, FONT_SIZE};

    //Attributes
    private String startTagRegex;
    private String javaFxRegex;

    /**
     * Creates a html tag.
     * @param startTag Start tag
     * @param endTag End tag
     * @param javaFxStyle JavaFx representation of the element
     * @param javaFxRegex JavaFx regex of the element
     */
    public HTMLValueElement(String startTag, String startTagRegex,
        String endTag, String javaFxStyle, String javaFxRegex) {
        super(startTag, endTag, javaFxStyle);
        this.startTagRegex = startTagRegex;
        setJavaFxRegex(javaFxRegex);
    }

    /**
     * Sets the regex of the javaFx style.
     * @param javaFxRegex Regex of the javaFx style
     */
    private void setJavaFxRegex(String javaFxRegex) {
        this.javaFxRegex = javaFxRegex;
    }

    /**
     * Gets the start tag with a given value.
     * @param value Value to insert into the start tag
     * @return Start tag with a given value
     */
    public String getStartTag(@NotNull String value) {
        return String.format(getStartTag(), value.strip());
    }

    /**
     * Gets the start tag regex.
     * @return Start tag regex
     */
    public String getStartTagRegex() {
        return startTagRegex;
    }

    /**
     * Gets the javaFx style with the given value.
     * @param value Value of the javaFx style
     * @return JavaFx style with the given value
     */
    public String getJavaFxStyle(@NotNull Object value) {
        return String.format(getJavaFxStyle(), value.toString().strip());
    }

    /**
     * Gets the java fx style without the value.
     * @return Java fx style without the value
     */
    public String getJavaFxStyleNoValue() {
        return getJavaFxRegex().substring(0, getJavaFxStyle().indexOf(":") + 1);
    }

    /**
     * Gets the javaFx regex.
     * @return javaFx regex
     */
    public String getJavaFxRegex() {
        return javaFxRegex;
    }

    /**
     * Gets the matches string.
     * @return Matches string
     */
    @Override
    public String toMatchesString() {
        return ".*" + javaFxRegex + ".*";
    }

}
