package gui.components;

import javafx.scene.image.Image;

/**
 * Class that represents a javafx image with extra methods.
 * @author Jordan Jones
 */
public class ExtendedImage extends Image {

    //Attributes
    private String imagePath;

    /**
     * Creates an extended image.
     * @param imagePath File path of the image
     */
    public ExtendedImage(String imagePath) {
        super(imagePath);
        setImagePath(imagePath);
    }

    /**
     * Saves the image path of the image.
     * @param imagePath Image path of the image
     */
    private void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Gets the image path of the image.
     * @return Image path of the image
     */
    public String getImagePath() {
        return imagePath;
    }

}
