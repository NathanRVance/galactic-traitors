package net.traitors.util.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Disposable;

import net.traitors.GameFactory;
import net.traitors.controls.Controls;
import net.traitors.util.save.SaveData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

class ClientSocket implements Disposable, MultiplayerSocket {

    private GameFactory gameFactory;
    private Socket socket;
    private PrintStream outputStream;
    private long playerID = -1;
    private SaveData data;
    private SaveData lastSent;
    private Thread sendThread = new Thread();

    ClientSocket(String ip, int port, GameFactory gameFactory) {
        this.gameFactory = gameFactory;
        socket = Gdx.net.newClientSocket(Net.Protocol.TCP, ip, port, null);
        outputStream = new PrintStream(socket.getOutputStream());
        final BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        try {
            playerID = Integer.parseInt(inputStream.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!Thread.interrupted()) {
                        data = new SaveData(inputStream.readLine());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    long getPlayerID() {
        return playerID;
    }

    @Override
    public void receive() {
        if (data != null) {
            gameFactory.loadSaveData(data);
            data = null;
        }
    }

    @Override
    public void send() {
        final SaveData userInput = Controls.getInputToSend().getSaveData();
        if (userInput.equals(lastSent) || sendThread.isAlive()) return;
        lastSent = userInput;
        sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                outputStream.println(userInput.toString());
            }
        });
        sendThread.start();
    }

    @Override
    public void dispose() {
        socket.dispose();
    }
}
