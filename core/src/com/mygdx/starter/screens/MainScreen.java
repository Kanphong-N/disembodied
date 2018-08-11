package com.mygdx.starter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.mygdx.starter.Constants;
import com.mygdx.starter.model.Key;
import com.mygdx.starter.model.Minion;
import com.mygdx.starter.utils.FontUtils;
import com.mygdx.starter.utils.MathUtils;
import com.mygdx.starter.utils.StringUtils;

import static com.mygdx.starter.Constants.MonitorBlue;
import static com.mygdx.starter.Constants.NumPixelsKeyPress;
import static com.mygdx.starter.Constants.WindowHeight;
import static com.mygdx.starter.Constants.WindowWidth;

public class MainScreen extends AbstractScreen implements InputProcessor {

    private final BitmapFont font;
    private final String userName;
    private final Key[][] keys = new Key[7][];
    private final Sprite overlay;
    private final Sprite frame;
    private final Rectangle screen;
    private Key escapeKey;
    private Key z;
    private Key p;
    private Key dot;
    private Key shiftLeft;
    private Key capsLock;
    private Key tab;
    private Array<Minion> minions = new Array<>();
    private Key spaceKey;
    private float elapsedTime;

    // config
    private float keySize = 31f;
    private float paddingBetweenKeys = 1.8f;
    float xFirstLine = 50;
    float yFirstLine = 170;
    float spaceBetweenEscAndFKeys = 10;
    float spaceBetweenFirstAndSecondRow = 5;
    private boolean monitorIsTurnedOn = false;
    private float xStartChat = 25;
    private float yStartChat = WindowHeight - 25;
    private StringBuilder sb = new StringBuilder();

    private GlyphLayout layout;
    private Rectangle textCursor = new Rectangle();
    private long previousTime;
    private boolean showTextCursor;
    private String currentLine;

    public MainScreen() {
        super(WindowWidth, Constants.WindowHeight);
        font = new BitmapFont(Gdx.files.internal("fonts/amiga4everpro2.fnt"));
        userName = System.getProperty("user.name");

        textCursor.setWidth(1);
        textCursor.setHeight(12);

        initKeyboard();
        Gdx.input.setInputProcessor(this);

        screen = new Rectangle(5, escapeKey.y + escapeKey.height + 7, 420, 185);

        overlay = new Sprite(new Texture("overlay.png"));
        overlay.setBounds(screen.x, screen.y, screen.width, screen.height);
        overlay.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        frame = new Sprite(new Texture("frame.png"));
        frame.setBounds(screen.x, screen.y, screen.width, screen.height);
        frame.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        spawnMinion("hi\nare you human");
        spawnMinion("");
    }

    private void spawnMinion(String string) {
        Array<Key> keySequence = new Array<>(string.length() + 2);
        keySequence.add(spaceKey);
        for (int i = 0; i < string.length(); i++) {
            Key keyToAdd;
            if (string.charAt(i) == ' ') {
                keyToAdd = spaceKey;
            } else if (string.charAt(i) == '\n') {
                keyToAdd = getKeyByName("Enter");
            } else  {
                keyToAdd = getKeyByName(String.valueOf(string.charAt(i)).toUpperCase());
            }
            keySequence.add(keyToAdd);
        }
        keySequence.add(escapeKey);
        Minion minion = new Minion(keySequence);
        minion.key = spaceKey;
        minion.x = spaceKey.x + MathUtils.randomWithin(10, 20);
        minion.y = spaceKey.y + 10;
        minions.add(minion);
    }

    private void initKeyboard() {
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
        escapeKey = getKeyByName("Escape");

        // special keys
        keys[6] = new Key[6];
        keys[6][0] = new Key("Enter", "<-´", p.x + p.width, p.y - p.height, keySize * 1.3f, keySize * 2);
        keys[6][1] = new Key("L-Shift", "^", escapeKey.x, z.y, keySize * 2, keySize);
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

        spaceKey = getKeyByName("Space");
    }

    public Key getKeyByName(String name) {
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
        //Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1f);
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        update(delta);

        Gdx.gl.glEnable(GL30.GL_BLEND);
        Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);

        if (monitorIsTurnedOn) {
            sr.setColor(MonitorBlue.r, MonitorBlue.g, MonitorBlue.b, 0.3f);
        } else {
            sr.setColor(0.15f, 0.15f, 0.15f, 1f);
        }
        sr.rect(screen.x, screen.y, screen.width, screen.height);

        if (showTextCursor) {
            sr.setColor(Color.WHITE);
            sr.rect(textCursor.x, textCursor.y, textCursor.width, textCursor.height);
        }

        for (Key[] keyLine : keys) {
            if (keyLine == null) continue;
            for (Key key : keyLine) {
                if (key == null) continue;
                sr.setColor(Color.DARK_GRAY);
                sr.set(ShapeRenderer.ShapeType.Filled);
                sr.rect(key.x, key.y, key.width - paddingBetweenKeys, key.height - (key.isPressed ? NumPixelsKeyPress : 0) - paddingBetweenKeys);

                sr.setColor(Color.GRAY);
                sr.set(ShapeRenderer.ShapeType.Line);
                sr.rect(key.x, key.y, key.width - paddingBetweenKeys, key.height - (key.isPressed ? NumPixelsKeyPress : 0) - paddingBetweenKeys);

                sr.set(ShapeRenderer.ShapeType.Filled);
                if (!key.isPressed) {
                    sr.rect(key.x, key.y, key.width - paddingBetweenKeys, 3);
                }
            }
        }

        for (Minion minion : minions) {
            minion.render(sr);
        }

        sr.end();
        Gdx.gl.glDisable(GL30.GL_BLEND);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        font.setColor(Color.WHITE);
        font.draw(batch, sb, xStartChat, yStartChat);

        overlay.setX(MathUtils.oscilliate(elapsedTime, 4f, 5.5f, 1f));
        overlay.draw(batch, MathUtils.oscilliate(elapsedTime, 0.2f, 0.5f, 8f));

        for (Key[] keyLine : keys) {
            if (keyLine == null) continue;
            for (Key key : keyLine) {
                if (key == null) continue;
                if (key.isDisabled) {
                    font.setColor(Color.GRAY);
                } else {
                    font.setColor(Color.WHITE);
                }
                font.draw(batch, key.caption, key.x + 5, key.y + keySize * 0.75f - (key.isPressed ? NumPixelsKeyPress : 0));
            }
        }
        frame.draw(batch);
        for (Minion minion : minions) {
            minion.render(batch);
        }
        batch.end();
    }

    private void update(float delta) {
        elapsedTime += delta;

        for (Minion minion : minions) {
            minion.update(delta);
        }

        // fires every second
        if (System.currentTimeMillis() > previousTime + 1000) {
            previousTime = System.currentTimeMillis();
            showTextCursor = !showTextCursor;
            System.out.println("'" + currentLine + "'");
            monitorIsTurnedOn = !monitorIsTurnedOn;
        }

        currentLine = sb.toString();
        int lastIndex = currentLine.lastIndexOf("\n");
        if (lastIndex > -1) {
            currentLine = sb.toString().substring(lastIndex + 1);
        }
        layout = FontUtils.getLayout(font, currentLine);
        textCursor.setX(xStartChat + layout.width);
        layout = FontUtils.getLayout(font, sb.toString());
        textCursor.setY(yStartChat - layout.height - 2);

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

                switch (keycode) {
                    case Input.Keys.ENTER:
                        sb.append("\n");
                        if (StringUtils.numOccurrences(sb.toString(), '\n') > 17) {
                            sb.delete(0, sb.indexOf("\n") + 1);
                        }
                        break;
                    case Input.Keys.DEL:
                        if (sb.toString().charAt(sb.length() - 1) != '\n') {
                            sb.setLength(Math.max(0, sb.length() - 1));
                        }
                        break;
                    case Input.Keys.SPACE:
                        sb.append(" ");
                        break;
                    case Input.Keys.SHIFT_LEFT:
                    case Input.Keys.SHIFT_RIGHT:
                    case Input.Keys.CONTROL_LEFT:
                    case Input.Keys.CONTROL_RIGHT:
                    case Input.Keys.ALT_LEFT:
                    case Input.Keys.ALT_RIGHT:
                    case Input.Keys.ESCAPE:
                        // ignore
                        break;
                    default:
                        String keyToAppend = Input.Keys.toString(keycode);
                        if (shiftLeft.isPressed) {
                            keyToAppend = keyToAppend.toUpperCase();
                        } else {
                            keyToAppend = keyToAppend.toLowerCase();
                        }
                        sb.append(keyToAppend);
                        break;
                }
            }
        }
        return false;
    }

    public Key getKeyByKeyCode(int keycode) {
        System.out.println(Input.Keys.toString(keycode));
        return getKeyByName(Input.Keys.toString(keycode));
    }

    @Override
    public boolean keyUp(int keycode) {
        Key key = getKeyByKeyCode(keycode);
        if (key != null) {
            if (!key.isDisabled) {
                key.isPressed = false;
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
