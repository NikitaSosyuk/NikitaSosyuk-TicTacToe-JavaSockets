package game.controllers;

import game.Main;
import game.connectBetweenServerAndJavaFX.Singleton;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class GameController implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private ImageView imageViewOne;
    @FXML
    private ImageView imageViewTwo;
    @FXML
    private Label labelWarrior;
    @FXML
    private Label figureMe;
    @FXML
    private Label figureWarrior;
    @FXML
    private ComboBox<String> emotionBox;
    @FXML
    private Text scoreMe;
    @FXML
    private Text scoreWarrior;
    @FXML
    private Text figureOrEmotion;
    @FXML
    private Text myMoveOrNote;
    @FXML
    private StackPane stackPane1;
    @FXML
    private StackPane stackPane2;
    @FXML
    private StackPane stackPane3;
    @FXML
    private StackPane stackPane4;
    @FXML
    private StackPane stackPane5;
    @FXML
    private StackPane stackPane6;
    @FXML
    private StackPane stackPane7;
    @FXML
    private StackPane stackPane8;
    @FXML
    private StackPane stackPane9;

    private Scanner scanner;
    private Socket socket;
    private PrintWriter printWriter;
    private int warrior;
    private int me;
    private StackPane[] array = new StackPane[9];
    private boolean myMove = true;
    private boolean myMoveEmotion = false;
    private int figure = Integer.MIN_VALUE;
    private boolean[] wasMove = new boolean[9];

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        socket = Singleton.getInstance(null).socket;
        try {
            scanner = new Scanner(socket.getInputStream());
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            addStackPaneToArray();

            ServerDataOfUsersService serverListener = new ServerDataOfUsersService();
            serverListener.start();
            serverListener.setOnSucceeded(t -> {
                System.out.println((String) t.getSource().getValue());
                String response = (String) t.getSource().getValue();
                String[] data = response.split(",");
                labelWarrior.setText(data[3]);
                labelWarrior.setTextAlignment(TextAlignment.CENTER);
                if (data[2].equals("1")) {
                    setFoxImage(imageViewOne, "default");
                    me = 1;
                } else {
                    setDogImage(imageViewOne, "default");
                    me = 2;
                }
                if (data[4].equals("1")) {
                    setFoxImage(imageViewTwo, "default");
                    warrior = 1;
                } else {
                    setDogImage(imageViewTwo, "default");
                    warrior = 2;
                }
                if (data[5].equals("X")) {
                    figureMe.setText(" X");
                    figure = 1;
                    myMove = true;
                    myMoveEmotion = true;
                    figureWarrior.setText("〇️");
                    figureOrEmotion.setText("Move or Emotion");
                    myMoveOrNote.setText("Your move");
                } else {
                    figureMe.setText("〇️");
                    myMove = false;
                    figure = 2;
                    figureWarrior.setText(" X");
                    figureOrEmotion.setText("Wait");
                    myMoveOrNote.setText("Warrior's move");
                }
            });

            GameDataService gameListener = new GameDataService();
            gameListener.start();
            gameListener.setOnSucceeded(t -> {
                System.out.println((String) t.getSource().getValue());
                String result = (String) t.getSource().getValue();
                String[] split = result.split(",");
                if (split[0].equals("Win")) {
                    myMove = false;
                    if (split[1].equals("nobody")) {
                        System.out.println("nobody");
                        scoreMe.setText("1");
                        scoreWarrior.setText("1");
                        nextGame("game-nobody");
                    } else {
                        if (split[1].equals("" + figure)) {
                            scoreMe.setText("1");
                            scoreWarrior.setText("0");
                            nextGame("game-win");
                        } else {
                            scoreMe.setText("0");
                            scoreWarrior.setText("1");
                            nextGame("game-lose");
                        }
                    }
                    gameListener.cancel();
                } else {
                    if (split[0].equals("Emotion")) {
                        switch (warrior) {
                            case 1 -> setFoxImage(imageViewTwo, split[1]);
                            case 2 -> setDogImage(imageViewTwo, split[1]);
                        }
                        myMoveEmotion = true;
                        if (myMove) {
                            moveText();
                        } else {
                            figureOrEmotion.setText("Emotion");
                            myMoveOrNote.setText("Show your Emotion");
                        }
                    } else {
                        System.out.println("drawing");
                        int figure2 = Integer.parseInt(split[1]);
                        int where = Integer.parseInt(split[2]);
                        drawFigure(array[where], figure2, where);
                        myMove = true;
                        myMoveEmotion = true;
                        moveText();
                    }
                    gameListener.restart();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void uploadEmotion() {
        if ((myMove || myMoveEmotion) && !emotionBox.getValue().equals("Choose")) {
            switch (me) {
                case 1 -> setFoxImage(imageViewOne, emotionBox.getValue());
                case 2 -> setDogImage(imageViewOne, emotionBox.getValue());
            }
            figureOrEmotion.setText("Wait");
            myMoveOrNote.setText("Warrior's move");
            printWriter.println("Emotion," + emotionBox.getValue());
            System.out.println("Emotion," + emotionBox.getValue());
        }
    }

    private void setFoxImage(ImageView image, String which) {
        File file = new File("src/pictures/fox-" + which + ".png");
        Image imageOne = new Image(file.toURI().toString());
        image.setImage(imageOne);
    }

    private void setDogImage(ImageView image, String which) {
        File file = new File("src/pictures/dog-" + which + ".png");
        Image imageOne = new Image(file.toURI().toString());
        image.setImage(imageOne);
    }

    private void drawFigure(StackPane stackPane, int figure, int which) {
        ImageView imageView = null;
        if (figure == 2) {
            File file = new File("src/pictures/circle.png");
            Image image = new Image(file.toURI().toString());
            imageView = new ImageView(image);
        }
        if (figure == 1) {
            File file = new File("src/pictures/cross.png");
            Image image = new Image(file.toURI().toString());
            imageView = new ImageView(image);
        }
        stackPane.getChildren().add(imageView);
        wasMove[which] = true;
        System.out.println("Drew");
    }

    private void addStackPaneToArray() {
        array[0] = stackPane1;
        array[1] = stackPane2;
        array[2] = stackPane3;
        array[3] = stackPane4;
        array[4] = stackPane5;
        array[5] = stackPane6;
        array[6] = stackPane7;
        array[7] = stackPane8;
        array[8] = stackPane9;
        stackPane1.setOnMouseClicked(t -> {
            if (myMove &&!wasMove[0] && myMoveEmotion) {
                drawFigure(stackPane1, figure, 0);
                figureOrEmotion.setText("Wait");
                myMoveOrNote.setText("Warrior's move");
                printWriter.println("Motion," + figure + "," + 0);
                myMove = false;
                myMoveEmotion = false;
            }
        });
        stackPane2.setOnMouseClicked(t -> {
            if (myMove &&!wasMove[1] && myMoveEmotion) {
                figureOrEmotion.setText("Wait");
                myMoveOrNote.setText("Warrior's move");
                drawFigure(stackPane2, figure, 1);
                printWriter.println("Motion," + figure + "," + 1);
                myMove = false;
                myMoveEmotion = false;
            }
        });
        stackPane3.setOnMouseClicked(t -> {
            if (myMove &&!wasMove[2] && myMoveEmotion) {
                figureOrEmotion.setText("Wait");
                myMoveOrNote.setText("Warrior's move");
                drawFigure(stackPane3, figure, 2);
                printWriter.println("Motion," + figure + "," + 2);
                myMove = false;
                myMoveEmotion = false;
            }
        });
        stackPane4.setOnMouseClicked(t -> {
            if (myMove &&!wasMove[3] && myMoveEmotion) {
                figureOrEmotion.setText("Wait");
                myMoveOrNote.setText("Warrior's move");
                drawFigure(stackPane4, figure, 3);
                printWriter.println("Motion," + figure + "," + 3);
                myMove = false;
                myMoveEmotion = false;
            }
        });
        stackPane5.setOnMouseClicked(t -> {
            if (myMove &&!wasMove[4] && myMoveEmotion) {
                figureOrEmotion.setText("Wait");
                myMoveOrNote.setText("Warrior's move");
                drawFigure(stackPane5, figure, 4);
                printWriter.println("Motion," + figure + "," + 4);
                myMove = false;
                myMoveEmotion = false;
            }
        });
        stackPane6.setOnMouseClicked(t -> {
            if (myMove &&!wasMove[5] && myMoveEmotion) {
                figureOrEmotion.setText("Wait");
                myMoveOrNote.setText("Warrior's move");
                drawFigure(stackPane6, figure, 5);
                printWriter.println("Motion," + figure + "," + 5);
                myMove = false;
                myMoveEmotion = false;
            }
        });
        stackPane7.setOnMouseClicked(t -> {
            if (myMove &&!wasMove[6] && myMoveEmotion) {
                figureOrEmotion.setText("Wait");
                myMoveOrNote.setText("Warrior's move");
                drawFigure(stackPane7, figure, 6);
                printWriter.println("Motion," + figure + "," + 6);
                myMove = false;
                myMoveEmotion = false;
            }
        });
        stackPane8.setOnMouseClicked(t -> {
            if (myMove &&!wasMove[7] && myMoveEmotion) {
                figureOrEmotion.setText("Wait");
                myMoveOrNote.setText("Warrior's move");
                drawFigure(stackPane8, figure, 7);
                printWriter.println("Motion," + figure + "," + 7);
                myMove = false;
                myMoveEmotion = false;
            }
        });
        stackPane9.setOnMouseClicked(t -> {
            if (myMove &&!wasMove[8] && myMoveEmotion) {
                figureOrEmotion.setText("Wait");
                myMoveOrNote.setText("Warrior's move");
                drawFigure(stackPane9, figure, 8);
                printWriter.println("Motion," + figure + "," + 8);
                myMove = false;
                myMoveEmotion = false;
            }
        });

    }

    private void nextGame(String where) {
        try {
            Singleton.getInstance(null).socket.close();
            Singleton.setNull();
            Main.loadScene(FXMLLoader.load(getClass().getResource("../fxmlScreens/" + where + ".fxml")), root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void moveText() {
        figureOrEmotion.setText("Figure Or Emotion");
        myMoveOrNote.setText("Make move");
    }

    private class ServerDataOfUsersService extends Service<String> {
        @Override
        protected Task<String> createTask() {
            return new Task<>() {
                @Override
                protected String call() {
                    while (!socket.isInputShutdown()) {
                        if (scanner.hasNextLine()) {
                            String response = scanner.nextLine();
                            if (response.startsWith("DataUser")) {
                                return response;
                            }
                        }
                    }
                    return null;
                }
            };
        }
    }

    private class GameDataService extends Service<String> {

        @Override
        protected Task<String> createTask() {
            return new Task<>() {
                @Override
                protected String call() {
                    while (!socket.isInputShutdown()) {
                        if (scanner.hasNextLine()) {
                            String response = scanner.nextLine();
                            if (response.startsWith("Emotion,") || response.startsWith("Motion,") || response.startsWith("Win,")) {
                                System.out.println(response);
                                return response;
                            }
                        }
                    }
                    return null;
                }
            };
        }
    }
}
