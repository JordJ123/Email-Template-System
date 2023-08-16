package gui.screens;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;

/**
 * Extended version of the javafx scene class.
 * @author Jordan Jones
 */
public class ExtendedScene extends Scene {

    /**
     * Creates a scene with extended features.
     * @param parent Parent node
     */
    public ExtendedScene(Parent parent) {
        super(parent);
    }

    /**
     * Adds a shortcut to the scene.
     * @param keyCombination Key combination to activate shortcut
     * @param runnable Code to run when the shortcut is activated
     */
    public void addShortcut(KeyCombination keyCombination, Runnable runnable) {
        getAccelerators().put(keyCombination, runnable);
    }

}
