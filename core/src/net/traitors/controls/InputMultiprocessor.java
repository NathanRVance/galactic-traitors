package net.traitors.controls;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;

import net.traitors.util.Point;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InputMultiprocessor implements com.badlogic.gdx.InputProcessor {

    private Camera camera;
    private List<InputProcessor> inputs = new ArrayList<>();
    private List<MouseoverCallback> callbacks = new ArrayList<>();
    private Set<MouseoverCallback> wasIn = new HashSet<>();
    private List<MouseoverCallback> asyncRemove = new ArrayList<>();
    private List<MouseoverCallback> asyncAdd = new ArrayList<>();
    //These are locations on screen
    private List<Point> points = new ArrayList<>();
    private List<MouseoverCallback> wantsDragCallback = new ArrayList<>();

    public InputMultiprocessor(Camera camera) {
        this.camera = camera;
    }

    public synchronized void addProcessor(InputProcessor processor) {
        inputs.add(processor);
    }

    public synchronized void removeProcessor(InputProcessor processor) {
        inputs.remove(processor);
    }

    public synchronized void addCallback(MouseoverCallback callback) {
        asyncAdd.add(callback);
    }

    public synchronized void removeCallback(MouseoverCallback callback) {
        asyncRemove.add(callback);
    }

    public void bump() {
        resolveAsync();
    }

    private void resolveAsync() {
        for (MouseoverCallback callback : asyncRemove) {
            callbacks.remove(callback);
            wasIn.remove(callback);
        }
        asyncRemove.clear();

        for (MouseoverCallback callback : asyncAdd) {
            callbacks.add(callback);
        }
        asyncAdd.clear();
    }

    List<Point> getWorldTouches() {
        List<Point> ret = new ArrayList<>();
        for (Point point : points) {
            if (point != null) ret.add(point.unproject(camera));
        }
        return ret;
    }

    @Override
    public synchronized boolean keyDown(int keycode) {
        for (InputProcessor processor : inputs) {
            if (processor.keyDown(keycode))
                return true;
        }
        Controls.keyPressed(keycode);
        return false;
    }

    @Override
    public synchronized boolean keyUp(int keycode) {
        for (InputProcessor processor : inputs) {
            if (processor.keyUp(keycode))
                return true;
        }
        Controls.keyReleased(keycode);
        return false;
    }

    @Override
    public synchronized boolean keyTyped(char character) {
        for (InputProcessor processor : inputs) {
            if (processor.keyTyped(character))
                return true;
        }
        return false;
    }

    @Override
    public synchronized boolean touchDown(int screenX, int screenY, int pointer, int button) {
        resolveAsync();
        for (InputProcessor processor : inputs) {
            if (processor.touchDown(screenX, screenY, pointer, button))
                return true;
        }

        Point screenP = new Point(screenX, screenY);
        for (MouseoverCallback callback : callbacks) {
            Point layerP = callback.getLayer().screenToLayerCoords(screenP);
            if (callback.contains(layerP)) {
                if (callback.mouseDown(layerP)) {
                    incListSize(wantsDragCallback, pointer);
                    wantsDragCallback.set(pointer, callback);
                    return true;
                }
            }
        }

        //Not consumed, so add to points
        incListSize(points, pointer);
        points.set(pointer, new Point(screenX, screenY));

        return false;
    }

    @Override
    public synchronized boolean touchUp(int screenX, int screenY, int pointer, int button) {
        //Whether or not consumed, delete from points
        incListSize(points, pointer);
        points.set(pointer, null);
        incListSize(wantsDragCallback, pointer);
        MouseoverCallback wantsMouseUp = wantsDragCallback.get(pointer);
        wantsDragCallback.set(pointer, null);

        resolveAsync();
        for (InputProcessor processor : inputs) {
            if (processor.touchUp(screenX, screenY, pointer, button))
                return true;
        }

        Point screenP = new Point(screenX, screenY);
        if(wantsMouseUp != null && wantsMouseUp.mouseUp()) return true;
        for (MouseoverCallback callback : callbacks) {
            if (callback.contains(callback.getLayer().screenToLayerCoords(screenP)))
                if (callback.mouseUp()) return true;
        }

        return false;
    }

    @Override
    public synchronized boolean touchDragged(int screenX, int screenY, int pointer) {
        //We're tracking it, and it moved!
        mouseEnterExit(new Point(screenX, screenY));
        if (points.size() > pointer && points.get(pointer) != null) {
            points.set(pointer, new Point(screenX, screenY));
        }

        if (wantsDragCallback.size() > pointer && wantsDragCallback.get(pointer) != null) {
            MouseoverCallback callback = wantsDragCallback.get(pointer);
            if (callback.mouseDragged(callback.getLayer().screenToLayerCoords(new Point(screenX, screenY)))) {
                return true;
            }
        }

        for (InputProcessor processor : inputs) {
            if (processor.touchDragged(screenX, screenY, pointer))
                return true;
        }
        return false;
    }

    @Override
    public synchronized boolean mouseMoved(int screenX, int screenY) {
        resolveAsync();
        for (InputProcessor processor : inputs) {
            if (processor.mouseMoved(screenX, screenY))
                return true;
        }
        mouseEnterExit(new Point(screenX, screenY));

        return false;
    }

    private void mouseEnterExit(Point screenLoc) {
        for (MouseoverCallback callback : callbacks) {
            if (callback.contains(callback.getLayer().screenToLayerCoords(screenLoc))) {
                if (!wasIn.contains(callback)) {
                    wasIn.add(callback);
                    callback.mouseEnter();
                }
            } else {
                if (wasIn.contains(callback)) {
                    wasIn.remove(callback);
                    callback.mouseExit();
                }
            }
        }
    }

    @Override
    public synchronized boolean scrolled(int amount) {
        for (InputProcessor processor : inputs) {
            if (processor.scrolled(amount))
                return true;
        }
        return false;
    }

    private void incListSize(List<?> list, int lastIndex) {
        for (int i = list.size(); i <= lastIndex; i++) {
            list.add(null);
        }
    }
}
