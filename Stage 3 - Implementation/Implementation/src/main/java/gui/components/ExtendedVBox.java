package gui.components;

import javafx.scene.layout.VBox;

/**
 * Extends the VBox.
 * @author Jordan Jones
 */
public class ExtendedVBox extends VBox {

    /**
     * Run a click effect.
     */
    public void clickEffect() {
        ComponentHandler.clickEffect(this);
    }

}
