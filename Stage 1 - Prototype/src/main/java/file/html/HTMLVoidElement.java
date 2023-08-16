package file.html;

/**
 * Represents a HTML void element, a HTML element with a single tag.
 * @author Jordan Jones
 */
public class HTMLVoidElement extends HTMLElement {

    //CONSTANTS
    public static final HTMLVoidElement LINE_BREAK = new HTMLVoidElement(
        "<br>", "\r\n");
    public static final HTMLVoidElement[] HTML_ELEMENTS = {LINE_BREAK};

    /**
     * Creates a html element.
     * @param tag Tag
     * @param javaFxStyle JavaFx representation of the element
     */
    public HTMLVoidElement(String tag, String javaFxStyle) {
        super(tag, tag, javaFxStyle);
    }

}
