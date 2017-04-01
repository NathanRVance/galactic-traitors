package net.traitors.controls;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;

import net.traitors.util.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputMultiprocessor implements com.badlogic.gdx.InputProcessor {

    private Camera camera;
    private List<InputProcessor> inputs = new ArrayList<>();
    private List<MouseoverCallback> callbacks = new ArrayList<>();
    private Map<MouseoverCallback, Boolean> wasIn = new HashMap<>();
    private List<MouseoverCallback> asyncRemove = new ArrayList<>();

    private int currentX = 0;
    private int currentY = 0;

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
        callbacks.add(callback);
        wasIn.put(callback, false);
    }

    public synchronized void removeCallback(MouseoverCallback callback) {
        asyncRemove.add(callback);
    }

    public void bump() {
        mouseMoved(currentX, currentY);
    }

    private void resolveRemove() {
        for (MouseoverCallback callback : asyncRemove) {
            callbacks.remove(callback);
            wasIn.remove(callback);
        }
        asyncRemove.clear();
    }

    @Override
    public synchronized boolean keyDown(int keycode) {
        for (InputProcessor processor : inputs) {
            if (processor.keyDown(keycode))
                return true;
        }
        return false;
    }

    @Override
    public synchronized boolean keyUp(int keycode) {
        for (InputProcessor processor : inputs) {
            if (processor.keyUp(keycode))
                return true;
        }
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
        resolveRemove();
        for (InputProcessor processor : inputs) {
            if (processor.touchDown(screenX, screenY, pointer, button))
                return true;
        }

        Point p = getTouchInWorld(screenX, screenY);
        for (MouseoverCallback callback : callbacks) {
            if (callback.contains(p))
                callback.mouseDown();
        }

        return false;
    }

    @Override
    public synchronized boolean touchUp(int screenX, int screenY, int pointer, int button) {
        resolveRemove();
        for (InputProcessor processor : inputs) {
            if (processor.touchUp(screenX, screenY, pointer, button))
                return true;
        }

        Point p = getTouchInWorld(screenX, screenY);
        for (MouseoverCallback callback : callbacks) {
            if (callback.contains(p))
                callback.mouseUp();
        }

        return false;
    }

    @Override
    public synchronized boolean touchDragged(int screenX, int screenY, int pointer) {
        for (InputProcessor processor : inputs) {
            if (processor.touchDragged(screenX, screenY, pointer))
                return true;
        }
        return false;
    }

    @Override
    public synchronized boolean mouseMoved(int screenX, int screenY) {
        currentX = screenX;
        currentY = screenY;
        resolveRemove();
        for (InputProcessor processor : inputs) {
            if (processor.mouseMoved(screenX, screenY))
                return true;
        }

        Point p = getTouchInWorld(screenX, screenY);
        for (MouseoverCallback callback : callbacks) {
            if (callback.contains(p)) {
                if (!wasIn.get(callback)) {
                    wasIn.put(callback, true);
                    callback.mouseEnter();
                }
            } else {
                if (wasIn.get(callback)) {
                    wasIn.put(callback, false);
                    callback.mouseExit();
                }
            }
        }

        return false;
    }

    @Override
    public synchronized boolean scrolled(int amount) {
        for (InputProcessor processor : inputs) {
            if (processor.scrolled(amount))
                return true;
        }
        return false;
    }

    /**
     * @param screenX x coordinate, origin is upper left
     * @param screenY y coordinate, origin is upper left
     * @return translated point in world coordinates
     */
    private Point getTouchInWorld(int screenX, int screenY) {
        return new Point(screenX, screenY).unproject(camera);
    }
}
