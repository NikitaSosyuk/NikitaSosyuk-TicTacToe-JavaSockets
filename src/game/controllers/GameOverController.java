package game.controllers;

import game.Main;
import game.connectBetweenServerAndJavaFX.Singleton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GameOverController implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    public void newGame() {
        try {
            Singleton.setNull();
            Main.loadScene(FXMLLoader.load(getClass().getResource("../fxmlScreens/startScreen.fxml")), root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

}
