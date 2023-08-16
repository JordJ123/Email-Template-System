package gui.components;

import javafx.scene.control.Label;

/**
 * Extends the javafx label class.
 * @author Jordan Jones
 */
public class ExtendedLabel extends Label {

    /**
     * Run a click effect.
     */
    public void clickEffect() {
        ComponentHandler.clickEffect(this);
    }

}
