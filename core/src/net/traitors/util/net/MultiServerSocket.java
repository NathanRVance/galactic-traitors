package net.traitors.util.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Disposable;

import net.traitors.GameFactory;
import net.traitors.GameScreen;
import net.traitors.controls.Controls;
import net.traitors.util.save.SaveData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class MultiServerSocket implements Disposable, MultiplayerSocket {

    private final List<Socket> sockets = Collections.synchronizedList(new ArrayList<Socket>());
    private List<PrintStream> outputs = Collections.synchronizedList(new ArrayList<PrintStream>());

    private ServerSocket serverSocket;
    private Thread serverThread;

    private GameFactory gameFactory;

    MultiServerSocket(int port, final GameScreen gameScreen, GameFactory gameFactory) {
        this.gameFactory = gameFactory;
        serverSocket = Gdx.net.newServerSocket(Net.Protocol.TCP, port, null);
        serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    final Socket s = serverSocket.accept(null);
                    synchronized (sockets) {
                        sockets.add(s);
                        PrintStream p = new PrintStream(s.getOutputStream());
                        outputs.add(p);
                        //Tell the client what ID its player is
                        p.println(gameScreen.addPlayer(false));
                        System.out.println("Told it");
                        final BufferedReader inputStream = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    while (!Thread.interrupted()) {
                                        Controls.UserInput input = new Controls.UserInput(-1);
                                        input.loadSaveData(new SaveData(inputStream.readLine()));
                                        Controls.setInput(input.ID, input);
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

    @Override
    public void send() {
        final String data = gameFactory.getSaveData().toString();
        synchronized (sockets) {
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
    }

    @Override
    public void receive() {
        //Already handled in background thread
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
