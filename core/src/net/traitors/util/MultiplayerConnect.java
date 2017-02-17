package net.traitors.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.Disposable;

import net.traitors.thing.Actor;

import java.util.HashSet;
import java.util.Set;

public class MultiplayerConnect implements Disposable, Actor {

    private static int port = 31415;
    private final Set<Socket> clientSockets = new HashSet<>();
    private Thread serverThread;
    private ServerSocket serverSocket;
    private Socket cliSock;

    public MultiplayerConnect() {

    }

    public void connectToServer(String IP) {
        cliSock = Gdx.net.newClientSocket(Net.Protocol.TCP, IP, port, new SocketHints());
    }

    public void makeServer() {
        serverSocket = Gdx.net.newServerSocket(Net.Protocol.TCP, port, new ServerSocketHints());
        serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    Socket s = serverSocket.accept(new SocketHints());
                    synchronized (clientSockets) {
                        clientSockets.add(s);
                    }
                }
            }
        });
        serverThread.start();
    }

    private void killServer() {
        if (serverThread != null) {
            serverThread.interrupt();
            serverSocket.dispose();
        }
        for (Socket socket : clientSockets) {
            socket.dispose();
        }
    }

    @Override
    public void dispose() {
        killServer();
        if (cliSock != null) {
            cliSock.dispose();
        }
    }

    @Override
    public void act(float delta) {

    }
}
