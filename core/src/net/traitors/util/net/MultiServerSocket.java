package net.traitors.util.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Disposable;

import net.traitors.GameScreen;
import net.traitors.controls.Controls;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MultiServerSocket implements Disposable {

    private final List<Socket> sockets = Collections.synchronizedList(new ArrayList<Socket>());
    private List<ObjectOutputStream> outputs = Collections.synchronizedList(new ArrayList<ObjectOutputStream>());
    private Map<Socket, Controls.UserInput> incomingData = Collections.synchronizedMap(new HashMap<Socket, Controls.UserInput>());

    private ServerSocket serverSocket;
    private Thread serverThread;

    MultiServerSocket(int port) {
        serverSocket = Gdx.net.newServerSocket(Net.Protocol.TCP, port, null);
        serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    final Socket s = serverSocket.accept(null);
                    synchronized (sockets) {
                        sockets.add(s);
                        try {
                            outputs.add(new ObjectOutputStream(s.getOutputStream()));
                            GameScreen.getStuff().addPlayerAsync();
                            //Tell the client what ID its player is
                            outputs.get(outputs.size() - 1).writeInt(outputs.size());
                            System.out.println("Told it");
                            final ObjectInputStream inputStream = new ObjectInputStream(s.getInputStream());
                            incomingData.put(s, null);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        while (!Thread.interrupted()) {
                                            incomingData.put(s, (Controls.UserInput) inputStream.readObject());
                                        }
                                    } catch (IOException | ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        serverThread.start();
    }

    void pushData(final Serializable data) {
        for (final ObjectOutputStream oos : outputs) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        oos.writeObject(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    List<Controls.UserInput> getInputs() {
        List<Controls.UserInput> ret = new ArrayList<>(sockets.size());
        synchronized (sockets) {
            for (Socket socket : sockets) {
                ret.add(incomingData.get(socket));
            }
        }
        return ret;
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
