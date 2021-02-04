package game.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class ServerMain {

    public static int PORT = 0;

    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        generatePort();
        while (true) {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("PORT is " + PORT);
                System.out.println("Server's waiting first client...");
                Socket clientFirst = serverSocket.accept();
                System.out.println("Server has first client");
                System.out.println("Server's waiting second client");
                Socket clientSecond = serverSocket.accept();
                System.out.println("Server has second client");
                new Thread(new ServerSocketThread(clientFirst, clientSecond)).start();
            } catch (IOException e) {
                main(args);
            }
        }
    }

    private static void generatePort() {
        PORT = RANDOM.nextInt(30000);
    }

}
