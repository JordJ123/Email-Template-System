package gui.screens.select;

import email.EmailAccount;
import file.directory.Directory;
import file.text.TextFile;
import gui.Main;
import gui.screens.Screen;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

/**
 * Controller for the Select Account Screen.
 * @author Jordan Jones
 */
public class SelectScreenController {

    //FXML Attributes
    @FXML private VBox emailAccountBoxes;

    /**
     * Code to run when the screen first loads.
     * @throws IOException Thrown if error with account files or fxml
     */
    @FXML
    private void initialize() throws IOException {
        for (File file : Objects.requireNonNull(new Directory(
            EmailAccount.ACCOUNTS_PATH).listFiles())) {
            Scanner scanner = new Scanner(new TextFile(String.format(
                EmailAccount.LOGIN_PATH, file.getName())));
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "EmailAccountBox.fxml"));
            HBox emailAccountBox = fxmlLoader.load();
            ((EmailAccountBoxController) fxmlLoader.getController())
                .setController(getEmailAccountBoxes(),
                    Integer.parseInt(file.getName()), scanner.next());
            getEmailAccountBoxes().getChildren().add(emailAccountBox);
        }
        Screen.getPrimaryStage().sizeToScene();
    }

    /**
     * Adds an account by going to the login screen.
     * @throws IOException Thrown if error with the login screen fxml
     */
    @FXML
    private void onAddButton() throws IOException {
        Main.LOGIN_SCREEN.load();
    }

    /**
     * Gets the email account boxes.
     * @return Email account boxes
     */
    private VBox getEmailAccountBoxes() {
        return emailAccountBoxes;
    }

}
