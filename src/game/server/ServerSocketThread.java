package game.server;

import javafx.concurrent.Task;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class ServerSocketThread implements Runnable {

    private Scanner scannerFirst;
    private PrintWriter writerFirst;
    private Scanner scannerSecond;
    private PrintWriter writerSecond;
    private int[] field = new int[9];
    private int countOfMotion = 0;
    private Socket first;
    private Socket second;

    public ServerSocketThread(Socket clientFirst, Socket clientSecond) throws IOException {
        first = clientFirst;
        second = clientSecond;
        scannerFirst = new Scanner(clientFirst.getInputStream());
        writerFirst = new PrintWriter(clientFirst.getOutputStream(), true);

        scannerSecond = new Scanner(clientSecond.getInputStream());
        writerSecond = new PrintWriter(clientSecond.getOutputStream(), true);

    }

    @Override
    public void run() {
        String dataFirst = "";
        String dataSecond = "";
        boolean flagFirst = false;
        boolean flagSecond = false;
        while (!flagFirst && !flagSecond) {
            if (scannerFirst.hasNextLine()) {
                dataFirst = scannerFirst.nextLine();
                flagFirst = true;
            }
            if (scannerSecond.hasNextLine()) {
                dataSecond = scannerSecond.nextLine();
                flagSecond = true;
            }
        }
        writerFirst.println("next");
        writerSecond.println("next");
        flagFirst = false;
        flagSecond = false;
        while (!flagFirst && !flagSecond) {
            if (scannerFirst.hasNextLine()) {
                if (scannerFirst.nextLine().equals("Move to gameScreen")) {
                    writerFirst.println("DataUser," + dataFirst + "," + dataSecond + ",X");
                    flagFirst = true;
                }
            }
            if (scannerSecond.hasNextLine()) {
                if (scannerSecond.nextLine().equals("Move to gameScreen")) {
                    writerSecond.println("DataUser," + dataSecond + "," + dataFirst + ",〇️");
                    flagSecond = true;
                }
            }
        }

        while (!first.isInputShutdown() && !second.isInputShutdown()) {
            //жду какой-то ответ от первого
            if (scannerFirst.hasNextLine()) {
                String response = scannerFirst.nextLine();
                //если это ход, то выполняется метод ответсвенный за игру и в случае конца игры - выхожу из цикла
                if (response.startsWith("Motion,")) {
                    if (motionAction(response, writerSecond, writerFirst)) break;
                    System.out.println("send to second from first - " + response);
                }
                //если это эмоция (чат) - то просто перенаправляю второму
                if (response.startsWith("Emotion,")) {
                    writerSecond.println(response);
                    System.out.println("send to second from first - " + response);
                }
            }
            //проверка на то, что цикл идет дальше
            System.out.println("check2");

            //жду какой-то ответ от второго
            if (scannerSecond.hasNextLine()) {

                String response = scannerSecond.nextLine();
                //если это эмоция (чат) - то просто перенаправляю первому
                if (response.startsWith("Emotion,")) {
                    writerFirst.println(response);
                    System.out.println("send to first from second - " + response);
                }
                //если это ход, то выполняется метод ответсвенный за игру и в случае конца игры - выхожу из цикла
                if (response.startsWith("Motion,")) {
                    if (motionAction(response, writerFirst, writerSecond)) break;
                    System.out.println("send to first from second - " + response);
                }
            }
        }
    }

    private boolean motionAction(String response, PrintWriter writerOne, PrintWriter writerTwo) {
        String[] responseArray = response.split(",");
        int figure = Integer.parseInt(responseArray[1]);
        int where = Integer.parseInt(responseArray[2]);
        field[where] = figure;
        countOfMotion++;
        int result = checkWinner();
        System.out.println("result = " + result);
        if (result != 0) {
            writerOne.println(response);
            if (result == -1) {
                writerTwo.println("Win,nobody");
                writerOne.println("Win,nobody");
            }
            writerOne.println("Win," + result);
            writerTwo.println("Win," + result);
            return true;
        } else {
            writerOne.println(response);
        }
        return false;
    }

    // 1 - X 2 - O -1 ничья 0 - ничего
    private int checkWinner() {
        for (int i = 0; i < 9; i += 3) {
            if (field[i] != 0 && (field[i] == field[i + 1]) && (field[i] == field[i + 2])) {
                return field[i];
            }
        }
        for (int i = 0; i < 3; i++) {
            if (field[i] != 0 && (field[i] == field[i + 3]) && (field[i] == field[i + 6])) {
                return field[i];
            }
        }
        if (field[0] != 0 && (field[0] == field[4]) && (field[0] == field[8])) {
            return field[0];
        }
        if (field[2] != 0 && (field[2] == field[4]) && (field[2] == field[6])) {
            return field[0];
        }
        if (countOfMotion == 9) {
            return -1;
        }
        return 0;
    }
}


