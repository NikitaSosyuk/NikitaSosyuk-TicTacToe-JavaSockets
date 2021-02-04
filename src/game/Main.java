package game;

import game.connectBetweenServerAndJavaFX.DataHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Random;

public class Main extends Application {

    Random r = new Random();

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("fxmlScreens/startScreen.fxml"));
        primaryStage.setTitle("Nktlckr" + r.nextInt());
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void loadScene(Parent next, Parent now) {
        Scene scene = new Scene(next, 1280, 720);
        Stage stage = (Stage) now.getScene().getWindow();
        stage.setScene(scene);
    }
}
