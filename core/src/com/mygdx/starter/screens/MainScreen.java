package com.mygdx.starter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.starter.Constants;
import com.mygdx.starter.keyboard.Key;
import com.mygdx.starter.utils.FontUtils;

import sun.rmi.runtime.NewThreadAction;

public class MainScreen extends AbstractScreen implements InputProcessor {

    private final BitmapFont font;
    private final String userName;
    private final Key[][] keys = new Key[7][];
    private final Key escape;
    private final Key z;
    private final Key p;
    private final Key dot;
    private final Key shiftLeft;
    private final Key capsLock;
    private final Key tab;
    private float keySize = 30;

    public MainScreen() {
        super(Constants.WindowWidth, Constants.WindowHeight);
        font = new BitmapFont(Gdx.files.internal("fonts/amiga4everpro2.fnt"));
        userName = System.getProperty("user.name");
        sr.setAutoShapeType(true);
        Gdx.input.setInputProcessor(this);

        float xFirstLine = 160;
        float yFirstLine = 170;
        float spaceBetweenEscAndFKeys = 10;
        float spaceBetweenFirstAndSecondRow = 5;

        // F-keys
        keys[0] = new Key[13];
        keys[0][0] = new Key("Escape", "Esc", xFirstLine - keySize - spaceBetweenEscAndFKeys, yFirstLine, keySize, keySize);
        for (int i = 1; i < keys[0].length; i++) {
            String keyName = "F".concat(String.valueOf(i));
            keys[0][i] = new Key(keyName, keyName, keys[0][0].x + spaceBetweenEscAndFKeys + i * keySize, yFirstLine, keySize, keySize);
        }

        // numbers
        keys[1] = new Key[11];
        for (int i = 0; i <= 9; i++) {
            String keyName = String.valueOf(i + 1);
            if (i == 9) {
                keyName = "0";
            }
            keys[1][i] = new Key(keyName, keyName, xFirstLine + i * keySize, keys[0][0].y - keySize - spaceBetweenFirstAndSecondRow, keySize, keySize);
        }
        keys[1][10] = new Key("Delete", "<-",
                keys[1][9].x + keys[1][9].width, keys[1][9].y,
                keySize * 2, keySize);

        initLine("QWERTYUIOP", 2, xFirstLine + keySize / 1.5f, keys[1][0].y - keySize, keySize, keySize);
        initLine("ASDFGHJKL", 3, xFirstLine + keySize * 1.3f, keys[2][0].y - keySize, keySize, keySize);
        initLine("ZXCVBNM,.", 4, xFirstLine + keySize / 1.5f, keys[3][0].y - keySize, keySize, keySize);

        // space-line
        keys[5] = new Key[8];
        float y = keys[4][0].y - keySize;
        keys[5][0] = new Key("L-Ctrl", "Ctrl", keys[0][0].x, y, keySize * 1.3f, keySize);
        keys[5][1] = new Key("SYM", "Win", keys[5][0].x + keys[5][0].width, y, keySize, keySize);
        keys[5][2] = new Key("L-Alt", "Alt", keys[5][1].x + keys[5][1].width, y, keySize, keySize);
        keys[5][3] = new Key("Space", "", keys[5][2].x + keys[5][2].width, y, keySize * 6 - 9, keySize);
        keys[5][4] = new Key("R-Alt", "Alt", keys[5][3].x + keys[5][3].width, y, keySize, keySize);
        keys[5][5] = new Key("Context Menu", "Ctx", keys[5][4].x + keys[5][4].width, y, keySize, keySize);
        keys[5][6] = new Key("Windows Menu", "Win", keys[5][5].x + keys[5][5].width, y, keySize, keySize);
        keys[5][7] = new Key("R-Ctrl", "Ctrl", keys[5][6].x + keys[5][6].width, y, keySize * 1.3f, keySize);

        p = getKeyByName("P");
        z = getKeyByName("Z");
        dot = getKeyByName(".");
        escape = getKeyByName("Escape");

        // special keys
        keys[6] = new Key[6];
        keys[6][0] = new Key("Enter", "<-´", p.x + p.width, p.y - p.height, keySize * 1.3f, keySize * 2);
        keys[6][1] = new Key("L-Shift", "^", escape.x, z.y, keySize * 2, keySize);
        keys[6][2] = new Key("R-Shift", "^", dot.x + dot.width, keys[6][1].y, keySize * 2.3f, keySize);
        shiftLeft = keys[6][1];
        keys[6][3] = new Key("Caps Lock", "Caps Lock", shiftLeft.x, shiftLeft.y + shiftLeft.height, keySize * 2.5f, keySize);
        capsLock = keys[6][3];
        keys[6][4] = new Key("Tab", "Tab", capsLock.x, capsLock.y + capsLock.height, shiftLeft.width, keySize);
        tab = keys[6][4];
        keys[6][5] = new Key("\\", "^", tab.x, tab.y + tab.height, keySize, keySize);

        keys[5][5].isDisabled = true; // ctx
        keys[6][5].isDisabled = true; // circumflex
        capsLock.isDisabled = true;
        keys[5][4].isDisabled = true; // r-alt
        getKeyByName("SYM").isDisabled = true; // windows key
        getKeyByName("Windows Menu").isDisabled = true; // windows key
    }

    private Key getKeyByName(String name) {
        for (Key[] key : keys) {
            for (Key key1 : key) {
                if (key1.name.equals(name)) {
                    return key1;
                }
            }
        }
        return null;
    }

    private void initLine(String line, int index, float xStart, float y, float width, float height) {
        keys[index] = new Key[line.length()];
        for (int i = 0; i < line.length(); i++) {
            String keyName = String.valueOf(line.charAt(i));
            keys[index][i] = new Key(keyName, keyName, xStart + i * keySize, y, width, height);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        update(delta);

        sr.setProjectionMatrix(camera.combined);
        sr.setColor(Color.GRAY);
        sr.begin();
        sr.set(ShapeRenderer.ShapeType.Line);
        for (Key[] keyLine : keys) {
            if (keyLine == null) continue;
            for (Key key : keyLine) {
                if (key == null) continue;
                sr.rect(key.x, key.y, key.width, key.height);
            }
        }
        sr.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (Key[] keyLine : keys) {
            if (keyLine == null) continue;
            for (Key key : keyLine) {
                if (key == null) continue;
                if (key.isDisabled) {
                    font.setColor(Color.DARK_GRAY);
                } else {
                    font.setColor(key.isPressed ? Color.RED : Color.WHITE);
                }
                font.draw(batch, key.caption, key.x + 5, key.y + keySize / 2);
            }
        }

        batch.end();
    }

    private void update(float delta) {
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public boolean keyDown(int keycode) {
        Key key = getKeyByKeyCode(keycode);
        if (key != null) {
            if (!key.isDisabled) {
                key.isPressed = true;
            }
        }
        return false;
    }

    private Key getKeyByKeyCode(int keycode) {
        System.out.println(Input.Keys.toString(keycode));
        return getKeyByName(Input.Keys.toString(keycode));
    }

    @Override
    public boolean keyUp(int keycode) {
        for (Key[] key : keys) {
            for (Key key1 : key) {
                key1.isPressed = false;
            }
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
