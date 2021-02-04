package game.connectBetweenServerAndJavaFX;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public final class Singleton {

    private static Singleton server;

    public Socket socket;

    private Singleton(Socket socket) {
        this.socket = socket;
    }

    public static Singleton getInstance(Socket socket){
        if (server == null) {
            server = new Singleton(socket);
        }
        return server;
    }
     public static void setNull() {
         server = null;
     }
}
