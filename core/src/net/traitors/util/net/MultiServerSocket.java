package net.traitors.util.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Disposable;

import net.traitors.GameScreen;
import net.traitors.controls.Controls;
import net.traitors.util.save.SaveData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MultiServerSocket implements Disposable {

    private final List<Socket> sockets = Collections.synchronizedList(new ArrayList<Socket>());
    private List<PrintStream> outputs = Collections.synchronizedList(new ArrayList<PrintStream>());
    private Map<Socket, Controls.UserInput> incomingData = Collections.synchronizedMap(new HashMap<Socket, Controls.UserInput>());

    private ServerSocket serverSocket;
    private Thread serverThread;

    MultiServerSocket(int port, final GameScreen gameScreen) {
        serverSocket = Gdx.net.newServerSocket(Net.Protocol.TCP, port, null);
        serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    final Socket s = serverSocket.accept(null);
                    synchronized (sockets) {
                        sockets.add(s);
                        outputs.add(new PrintStream(s.getOutputStream()));
                        gameScreen.addPlayer();
                        //Tell the client what ID its player is
                        outputs.get(outputs.size() - 1).println(outputs.size());
                        System.out.println("Told it");
                        final BufferedReader inputStream = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        incomingData.put(s, null);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    while (!Thread.interrupted()) {
                                        Controls.UserInput input = new Controls.UserInput(-1);
                                        input.loadSaveData(new SaveData(inputStream.readLine()));
                                        incomingData.put(s, input);
                                        System.out.println("Received user input");
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
            }
        });
        serverThread.start();
    }

    void pushData(final String data) {
        for (final PrintStream out : outputs) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    out.println(data);
                    System.out.println("Sent game state");
                }
            }).start();
        }
    }

    void getInputs() {
        synchronized (sockets) {
            for (Socket socket : sockets) {
                Controls.UserInput input = incomingData.get(socket);
                Controls.setInput(input.ID, input);
            }
        }
    }

    @Override
    public void dispose() {
        serverThread.interrupt();
        serverSocket.dispose();
        for (Socket socket : sockets) {
            socket.dispose();
        }
    }
}
