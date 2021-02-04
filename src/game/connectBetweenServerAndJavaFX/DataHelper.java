package game.connectBetweenServerAndJavaFX;

public class DataHelper {

    public static boolean newGame = true;

    public static int port = 0;

    public static int figure = 0;

    public static String name = null;

    public static void setData(String responseName, int responsePort, int figureWasChoose) {
        name = responseName;
        port = responsePort;
        figure = figureWasChoose;
        newGame = false;
    }
}
