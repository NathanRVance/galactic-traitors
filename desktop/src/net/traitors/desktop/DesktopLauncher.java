package net.traitors.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.traitors.GalacticTraitors;
import net.traitors.util.net.MultiplayerConnect;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        new LwjglApplication(new GalacticTraitors(), config);
        MultiplayerConnect.makeServer();
    }
}
