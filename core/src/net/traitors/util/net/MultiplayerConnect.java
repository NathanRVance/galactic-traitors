package net.traitors.util.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.Socket;

import net.traitors.GameScreen;
import net.traitors.controls.Controls;
import net.traitors.util.save.SaveData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class MultiplayerConnect {

    private static final float tickRate = 64;
    private static int port = 31415;
    private static float timeSinceLastTick = 0;
    private static MultiServerSocket serverSocket;
    private static Socket cliSock;
    private static ObjectOutputStream outputStream;
    private static int playerID = 0;
    private static Controls.UserInput lastUserInput = new Controls.UserInput();
    private static String connectIP;
    private static boolean makeServer = false;

    public static void start() {
        if (connectIP != null) {
            System.out.println("Connecting");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    cliSock = Gdx.net.newClientSocket(Net.Protocol.TCP, connectIP, port, null);
                    try {
                        outputStream = new ObjectOutputStream(cliSock.getOutputStream());
                        final ObjectInputStream input = new ObjectInputStream(cliSock.getInputStream());
                        //First get our playerID
                        int playerID = input.readInt();
                        //Then do inputs
                        while (!Thread.interrupted()) {
                            SaveData saveData = new SaveData();
                            saveData.loadData((String) input.readObject());
                            GameScreen.getStuff().loadSaveData(saveData);
                            MultiplayerConnect.playerID = playerID;
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        if (makeServer) {
            System.out.println("Serving");
            serverSocket = new MultiServerSocket(port);
        }
    }

    public static void connectToServer(final String IP) {
        connectIP = IP;
    }

    public static int getPlayerID() {
        return playerID;
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

    public static void tick(float delta) {
        if (serverSocket != null) {
            timeSinceLastTick += delta;
            if (timeSinceLastTick > 1 / tickRate) {
                timeSinceLastTick = 0;
                serverTick();
            }
        }
        if (outputStream != null) {
            clientTick();
        }
    }

    private static void serverTick() {
        if(GameScreen.getStuff().clean()) {
            serverSocket.pushData(GameScreen.getStuff().getSaveData().toString());
        }
        List<Controls.UserInput> inputs = serverSocket.getInputs();
        GameScreen.getStuff().updateInputs(inputs);
    }

    private static void clientTick() {
        final Controls.UserInput input = Controls.getUserInput();
        if (!input.equals(lastUserInput)) { //Need to send again
            lastUserInput = input;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        outputStream.writeObject(input.getSaveData().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
