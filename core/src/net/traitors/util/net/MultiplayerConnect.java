package net.traitors.util.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.Socket;

import net.traitors.GameScreen;
import net.traitors.controls.Controls;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class MultiplayerConnect {

    private static final float tickRate = 8;
    private static int port = 31415;
    private static float timeSinceLastTick = 0;
    private static MultiServerSocket serverSocket;
    private static Socket cliSock;
    private static PrintStream outputStream;
    private static Controls.UserInput lastUserInput = new Controls.UserInput(-1);
    private static String connectIP;
    private static boolean makeServer = false;
    private static GameScreen gameScreen;

    public static void start(final GameScreen gameScreen) {
        MultiplayerConnect.gameScreen = gameScreen;
        if (connectIP != null) {
            System.out.println("Connecting");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    cliSock = Gdx.net.newClientSocket(Net.Protocol.TCP, connectIP, port, null);
                    try {
                        outputStream = new PrintStream(cliSock.getOutputStream());
                        final BufferedReader inputStream = new BufferedReader(new InputStreamReader(cliSock.getInputStream()));
                        //First get our playerID
                        int playerID = Integer.parseInt(inputStream.readLine());
                        //Then do inputs
                        while (!Thread.interrupted()) {
                            System.out.println("Listening for game state");
                            //GameScreen.getWorldLayer().loadSaveData(new SaveData(inputStream.readLine()));
                            gameScreen.setPlayerID(playerID);
                            System.out.println("Received game state");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        if (makeServer) {
            System.out.println("Serving");
            serverSocket = new MultiServerSocket(port, gameScreen);
        }
    }

    public void connectToServer(final String IP) {
        connectIP = IP;
    }

    public static void makeServer() {
        makeServer = true;
    }

    public static void dispose() {
        if (serverSocket != null) {
            serverSocket.dispose();
        }
        if (cliSock != null) {
            cliSock.dispose();
        }
    }

    public static boolean isServer() {
        return serverSocket != null;
    }

    public static boolean isClient() {
        return outputStream != null;
    }

    public static void tick(float delta) {
        if (isServer()) {
            timeSinceLastTick += delta;
            if (timeSinceLastTick > 1 / tickRate) {
                timeSinceLastTick = 0;
                serverTick();
            }
        }
        if (isClient()) {
            clientTick();
        }
    }

    private static void serverTick() {
        if (gameScreen.isClean()) {
            //serverSocket.pushData(GameScreen.getWorldLayer().getSaveData().toString());
        }
        serverSocket.getInputs();
    }

    private static void clientTick() {
        final Controls.UserInput input = Controls.getUserInput();
        if (!input.equals(lastUserInput)) { //Need to send again
            lastUserInput = input;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    outputStream.println(input.getSaveData().toString());
                    System.out.println("Sent user input");
                }
            }).start();
        }
    }
}
