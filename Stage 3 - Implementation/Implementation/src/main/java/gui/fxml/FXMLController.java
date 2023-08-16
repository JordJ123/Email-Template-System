package gui.fxml;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Methods that can be used for any controller.
 */
public class FXMLController {

    /**
     * Code that runs when a mouse hovers over a given component.
     */
    @FXML
    protected void onHoverEnter() {
        FXMLScreen.getPrimaryStage().getScene().setCursor(Cursor.HAND);
    }

    /**
     * Code that runs when a mouse stops hovering over a given component.
     */
    @FXML
    protected void onHoverExit() {
        FXMLScreen.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
    }

}
