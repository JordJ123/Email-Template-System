package gui.components;

import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

/**
 * Handles components.
 * @author Jordan Jones
 */
public final class ComponentHandler {

    //CONSTANT
    public static final double CLICK_EFFECT_DURATION = 0.15;
    public static final double CLICK_EFFECT_AFTERMATH = 0.05;

    /**
     * Utility class.
     */
    private ComponentHandler() { }

    /**
     * Applies the click effect to node.
     * @param node Node to apply the click effect to
     */
    public static void clickEffect(@NotNull Node node) {
        String originalStyle = node.getStyle();
        String clickColour = "-fx-background-color: #2B6799;";
        if (!originalStyle.contains(clickColour)) {
            node.setStyle(originalStyle + clickColour);
            PauseTransition transition
                = new PauseTransition(Duration.seconds(CLICK_EFFECT_DURATION));
            transition.setOnFinished(event -> node.setStyle(originalStyle));
            transition.playFromStart();
        }
    }

}
