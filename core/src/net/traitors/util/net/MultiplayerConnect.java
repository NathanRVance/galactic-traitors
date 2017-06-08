package net.traitors.util.net;

import net.traitors.GameFactory;
import net.traitors.GameScreen;

public class MultiplayerConnect {

    private static final int port = 31415;
    private static String connectIP;

    public static MultiplayerSocket start(GameScreen gameScreen, GameFactory gameFactory) {
        if (connectIP != null) {
            System.out.println("Connecting");
            ClientSocket cliSock = new ClientSocket(connectIP, port, gameFactory);
            gameFactory.setPlayerID(cliSock.getPlayerID());
            return cliSock;
        } else {
            System.out.println("Serving");
            return new MultiServerSocket(port, gameScreen, gameFactory);
        }
    }

    public static void connectToServer(final String IP) {
        connectIP = IP;
    }
}
