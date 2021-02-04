package game.controllers;

import game.Main;
import game.connectBetweenServerAndJavaFX.DataHelper;
import game.connectBetweenServerAndJavaFX.Singleton;
import game.server.ServerMain;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public class StartController implements Initializable {

    //fox - 1 dog - 2
    private int chooseCharacter = Integer.MAX_VALUE;
    private Integer port = null;
    private boolean portIsHidden = true;

    @FXML
    private ImageView imageViewOne;
    @FXML
    private ImageView imageViewTwo;
    @FXML
    private GridPane root;
    @FXML
    public TextField portField;
    @FXML
    public TextField username;

    @FXML
    public void playerButtonAction() {
        if (portIsHidden) {
            portField.setVisible(true);
            portIsHidden = false;
        } else {
            String portText = portField.getText();
            if (portText != null && !portText.equals("")) {
                try {
                    port = Integer.parseInt(portText);
                    if (alertUsername() && alertCharacter()) {
                        String response = username.getText() + "," + chooseCharacter;
                        System.out.println(response);
                        Socket socket = new Socket("localhost", port);
                        Singleton.getInstance(socket);
                        new PrintWriter(socket.getOutputStream(), true).println(response);
                        DataHelper.setData(username.getText(), port, chooseCharacter);
                        Main.loadScene(FXMLLoader.load(getClass().getResource("../fxmlScreens/waitScreen.fxml")), root);
                    }
                } catch (NumberFormatException | UnknownHostException | ConnectException e) {
                    alertPort();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                alertPort();
            }
        }

    }

    @FXML
    public void dogHasChosen() {
        if (chooseCharacter == 1) {
            File file = new File("src/pictures/fox-eye-start.png");
            Image imageOne = new Image(file.toURI().toString());
            imageViewOne.setImage(imageOne);
            File fileTwo = new File("src/pictures/dog-eye-start-selected.png");
            Image imageTwo = new Image(fileTwo.toURI().toString());
            imageViewTwo.setImage(imageTwo);
            chooseCharacter = 2;
        } else {
            if (chooseCharacter == 2) {
                chooseCharacter = Integer.MAX_VALUE;
                File fileTwo = new File("src/pictures/dog-eye-start.png");
                Image imageTwo = new Image(fileTwo.toURI().toString());
                imageViewTwo.setImage(imageTwo);
            } else {
                File fileTwo = new File("src/pictures/dog-eye-start-selected.png");
                Image imageTwo = new Image(fileTwo.toURI().toString());
                imageViewTwo.setImage(imageTwo);
                chooseCharacter = 2;
            }
        }
    }

    @FXML
    public void foxHasChosen() {
        if (chooseCharacter == 2) {
            File fileTwo = new File("src/pictures/dog-eye-start.png");
            Image imageTwo = new Image(fileTwo.toURI().toString());
            imageViewTwo.setImage(imageTwo);
            File file = new File("src/pictures/fox-eye-start-selected.png");
            Image imageOne = new Image(file.toURI().toString());
            imageViewOne.setImage(imageOne);
            chooseCharacter = 1;
        } else {
            if (chooseCharacter == 1) {
                chooseCharacter = Integer.MAX_VALUE;
                File file = new File("src/pictures/fox-eye-start.png");
                Image imageOne = new Image(file.toURI().toString());
                imageViewOne.setImage(imageOne);
            } else {
                File file = new File("src/pictures/fox-eye-start-selected.png");
                Image imageOne = new Image(file.toURI().toString());
                imageViewOne.setImage(imageOne);
                chooseCharacter = 1;
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File file = new File("src/pictures/fox-eye-start.png");
        Image imageOne = new Image(file.toURI().toString());
        imageViewOne.setImage(imageOne);
        File fileTwo = new File("src/pictures/dog-eye-start.png");
        Image imageTwo = new Image(fileTwo.toURI().toString());
        imageViewTwo.setImage(imageTwo);
        portField.setVisible(false);
        if (!DataHelper.newGame) {
            username.setText(DataHelper.name);
            portField.setText(String.valueOf(DataHelper.port));
            switch (DataHelper.figure) {
                case 1 -> foxHasChosen();
                case 2 -> dogHasChosen();
            }
        }
    }

    //HELPERS
    private boolean alertUsername() {
        if (username.getText() == null || username.getText().equals("") || username.getText().contains(",")) {
            username.setText(null);
            username.setPromptText("Please enter correct username");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setContentText("Please enter correct username. Check that you didn't use ','");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private boolean alertCharacter() {
        if (chooseCharacter == Integer.MAX_VALUE) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setContentText("Please choose character");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private void alertPort() {
        portField.setText(null);
        portField.setPromptText("Please enter correct port");
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setContentText("Please enter correct host / Check entered port");
        alert.showAndWait();
    }
}
