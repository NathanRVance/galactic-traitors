package net.traitors.util;

import com.badlogic.gdx.graphics.Pixmap;

public class PixmapRotateRec extends Pixmap {


    public PixmapRotateRec(int width, int height, Format format) {
        super(width, height, format);
    }

    public void fillQuadrahedron(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
        fillTriangle(x1, y1, x2, y2, x3, y3);
        fillTriangle(x1, y1, x3, y3, x4, y4);
    }
}
