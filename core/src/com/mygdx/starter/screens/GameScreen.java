package com.mygdx.starter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.starter.AbstractCallback;
import com.mygdx.starter.Constants;
import com.mygdx.starter.MediaManager;
import com.mygdx.starter.MyGdxGame;
import com.mygdx.starter.model.Key;
import com.mygdx.starter.model.Minion;
import com.mygdx.starter.utils.FontUtils;
import com.mygdx.starter.utils.MathUtils;
import com.mygdx.starter.utils.ScreenUtils;
import com.mygdx.starter.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.mygdx.starter.Constants.KeySize;
import static com.mygdx.starter.Constants.MonitorBlue;
import static com.mygdx.starter.Constants.NumPixelsKeyPress;
import static com.mygdx.starter.Constants.WindowHeight;
import static com.mygdx.starter.Constants.WindowWidth;
import static com.mygdx.starter.Constants.femaleNames;
import static com.mygdx.starter.Constants.maleNames;
import static com.mygdx.starter.screens.GameScreen.State.CheerfulGame;
import static com.mygdx.starter.screens.GameScreen.State.Disembody;
import static com.mygdx.starter.screens.GameScreen.State.SquishGame;
import static com.mygdx.starter.screens.GameScreen.State.SquishGameAftermath;

public class GameScreen extends AbstractScreen implements InputProcessor {

    private final BitmapFont font;
    private String userName;
    private final Key[][] keys = new Key[7][];
    private final Sprite overlay;
    private final Sprite frame;
    private final Rectangle screen;
    private final Music cheerfulMusic;
    private final Sprite creepyOverlay;
    private final Sprite[] bloods = new Sprite[4];
    private final Sprite male;
    private final Sprite female;
    private final MyGdxGame myGdxGame;
    private Key escapeKey;
    private Key z;
    private Key p;
    private Key dot;
    private Key shiftLeft;
    private Key capsLock;
    private Key tab;
    private List<Minion> minions = new ArrayList<>();
    private Key spaceKey;
    private float elapsedTime;
    private Timer timer = new Timer();

    // config
    private float paddingBetweenKeys = 1.8f;
    float xFirstLine = 50;
    float yFirstLine = 170;
    float spaceBetweenEscAndFKeys = 10;
    float spaceBetweenFirstAndSecondRow = 5;
    private boolean monitorIsTurnedOn = false;
    private float xStartChat = 25;
    private float yStartChat = WindowHeight - 25;
    private StringBuilder sbChat = new StringBuilder();
    private StringBuilder sb = new StringBuilder();

    private GlyphLayout layout;
    private Rectangle textCursor = new Rectangle();
    private long previousTime;
    private boolean showTextCursor;
    private String currentLine;
    private boolean showInstructions;
    private String hudLine1 = "- RUNNING OUT OF the SPACE bar -";
    private String hudLine2 = "help us to reach all numbers,";
    private String hudLine3 = "before we run out of space\n   (on the space-bar)";
    private String hudLine4 = "(hit space-bar to start)";
    private String scoreString = "score: 0";
    private int score = 0;
    private boolean scoreHasBeenUpdated;
    private CharSequence timeString;
    private String keyboardLetters1 = "QWERTYUIOP";
    private String keyboardLetters2 = "ASDFGHJKL";
    private String keyboardLetters3 = "ZXCVBNM,.";
    private boolean userHasHitSpaceBarAtLeastOnce;
    private float elapsedTimeInCheerfulGame;
    private Minion minionAreYouHuman;
    private float elapsedTimeInCurrentScene;
    private long previousTime2;
    private String autoLine;
    private int autoLineIndex;
    private int numTicksToWait;
    private int lineIndex;
    private boolean areSpikesEnabled;
    private Sprite spikes;
    private Sprite bloodySpikes;
    private Sound ifYouCanHearUs;
    private boolean abalIsHere;
    private int sfxIndex = 1;
    private Sprite sfx1, sfx2;
    private List<Key> bloodyKeys = new ArrayList<>();
    private Music factoryMusic;
    private Sound couldYouTypeIn;
    private boolean transitionToAreYouHuman;
    private Music creepy;
    private Color cheerfulKeysColor = Color.GOLD;
    private Array<Minion> minionsToKill = new Array<>();
    private Music ghosts;
    private boolean hideCreepyOverlay;
    private Sound sad;
    private boolean minionsAreTalking;
    private Sound unknownCommand;
    private Music explaination;
    private boolean triedReleaseHumans;
    private int numTriesLeft = 4;
    private boolean hasHitSpaceBar;
    private String[] questions = new String[]{
            "Which key is this game about?",
            "From which key did the scum creatures came from?",
            "It's the longest key.",
            "You are running out of..",
            "Space!",
    };
    private int questionIndex;
    private boolean somethingIsRunningoutOfSpace;
    private int bloodIndex;
    private long previousTime3;
    private boolean drawFace;
    private Sprite face;
    private boolean drawFaceBefore;
    private boolean hideKeyboard;
    private Sound releaseHumans;
    private Sound magic;
    private boolean fadeToBlack;
    private float fadeToBlackAlpha;
    private int pauseBetweenNewLines = 80;
    private long AbalTypingSpeed = 30;

    public enum State {CheerfulGame, AreYouHuman, AbalInterceptsSpikes, Voices, AbalExplains, SquishGame, SquishGameAftermath, Disembody}

    public static State state = State.CheerfulGame;

    public Color LightYellow = Color.valueOf("#FFE97F");
    private List<Sound> cheerfulSounds = new ArrayList<>();

    private AbstractCallback incrementScore = new AbstractCallback() {
        @Override
        public boolean call(Minion minion) {
            score++;
            scoreHasBeenUpdated = true;
            try {
                Integer.parseInt(minion.key.caption);
                cheerfulSounds.add(MediaManager.playSound("audio/fridged.ogg"));
            } catch (Exception ex) {
                cheerfulSounds.add(MediaManager.playSound("audio/collect.ogg"));
            }
            return true;
        }
    };

    public GameScreen(MyGdxGame myGdxGame) {
        super(WindowWidth, Constants.WindowHeight);
        this.myGdxGame = myGdxGame;
        cheerfulMusic = MediaManager.playMusic("audio/bg.ogg", true);

        font = new BitmapFont(Gdx.files.internal("fonts/amiga4everpro2.fnt"));
        try {
            userName = System.getProperty("user.name");
        } catch (Exception ex) {
            userName = "Player";
        }

        sfx1 = new Sprite(new Texture("sfx_1.png"));
        sfx2 = new Sprite(new Texture("sfx_2.png"));
        spikes = new Sprite(new Texture("spikes.png"));
        bloodySpikes = new Sprite(new Texture("spikes_bloody.png"));

        textCursor.setWidth(1);
        textCursor.setHeight(12);

        initKeyboard();
        Gdx.input.setInputProcessor(this);

        screen = new Rectangle(5, escapeKey.y + escapeKey.height + 7, 420, 185);

        bloods[0] = new Sprite(new Texture("blood_1.png"));
        bloods[1] = new Sprite(new Texture("blood_2.png"));
        bloods[2] = new Sprite(new Texture("blood_3.png"));
        bloods[3] = new Sprite(new Texture("blood_4.png"));

        female = new Sprite(new Texture("female.png"));
        female.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        male = new Sprite(new Texture("male.png"));
        male.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        creepyOverlay = new Sprite(new Texture("creepy_overlay.png"));
        creepyOverlay.setBounds(screen.x, screen.y, screen.width, screen.height);
        creepyOverlay.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        overlay = new Sprite(new Texture("overlay.png"));
        overlay.setBounds(screen.x, screen.y, screen.width, screen.height);
        overlay.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        frame = new Sprite(new Texture("frame.png"));
        frame.setBounds(screen.x, screen.y, screen.width, screen.height);
        frame.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        for (int i = 0; i < 10; i++) {
            bloodyKeys.add(getKeyByName(String.valueOf(i)));
        }

        determineFace();
        if (face == null) {
            face = male; // assume standard case
        }

        if (Constants.Debug) {
            minionAreYouHuman = spawnMinionForCheerfulGame();
            areYouHuman();
            abalIntercepts();
            for (Minion minion : minions) {
                minion.key = escapeKey;
            }

            for (int i = 0; i < 9; i++) {
                spawnMinion("", getKeyByName(String.valueOf(i))).loadAnimations();
            }

            spikesGame();
            lineIndex = 11;

            voices();
            abalExplains();

            squishGame();
            squishGameAftermath();
            disembody();
        }
    }

    private void determineFace() {
        if (userName == null) {
            return;
        }
        String nameToCompare = userName;
        nameToCompare = nameToCompare.trim().toLowerCase();

        for (String maleName : maleNames) {
            if (nameToCompare.equals(maleName.toLowerCase())) {
                face = male;
                return;
            }
        }
        for (String femaleName : femaleNames) {
            if (nameToCompare.equals(femaleName.toLowerCase())) {
                face = female;
                return;
            }
        }
    }

    private Minion spawnMinionForCheerfulGame() {
        if (!state.equals(CheerfulGame)) {
            return null;
        }
        int freeNumberSlot = findFreeNumberSlot();
        if (freeNumberSlot > -1) {
            sb.setLength(0);
            // ignore Z and Y keys to avoid troubles with keyboard layout
            sb.append(keyboardLetters3.charAt(MathUtils.randomWithin(1, keyboardLetters3.length() - 3)));
            sb.append(keyboardLetters2.charAt(MathUtils.randomWithin(0, keyboardLetters2.length() - 1)));
            sb.append(keyboardLetters1.replace("Y", "").charAt(MathUtils.randomWithin(0, keyboardLetters1.length() - 2)));
            sb.append(findFreeNumberSlot());
            Minion minion = spawnMinion(sb.toString(), spaceKey);
            minion.loadAnimations();
            minion.postJumpCallback = incrementScore;
            cheerfulSounds.add(MediaManager.playSoundRandomPitch("audio/select.ogg"));
            return minion;
        }
        return null;
    }

    private void voices() {
        goToNextState();
        ghosts.pause();
        //ghosts.dispose();

        ifYouCanHearUs = MediaManager.playSound("audio/if_you_can_hear_us.ogg", true);
        minionsAreTalking = true;

        disableAllKeys();
        enableTypingKeysForUser();
    }

    private void abalIntercepts() {
        minionsAreTalking = false;
        if (cheerfulMusic != null && cheerfulMusic.isPlaying()) {
            cheerfulMusic.stop();
            //cheerfulMusic.dispose();
        }
        goToNextState();
        creepy.pause();
        //creepy.dispose();
        MediaManager.playSound("audio/beast.ogg");
        factoryMusic = MediaManager.playMusic("audio/factory.ogg", true);
        escapeKey.isDisabled = true;
        minionAreYouHuman.freeze();
        sbChat.append("\n\n< abaal connected >\n");
        abalIsHere = true;
        autoLineIndex = 0;
        numTicksToWait = 200;
    }

    private void squishGame() {

        goToNextState();
        disableAllKeys();
        enableTypingKeysForUser();
    }

    private void squishGameAftermath() {
        if (sad != null) {
            sad.stop();
        }
        hideKeyboard = false;
        minions.removeIf(Minion::isNotDead);

        goToNextState();
        disableAllKeys();
        unpressAllKeys();

        lineIndex = 0;
        autoLineIndex = 0;
        autoLine = "";
        abalIsHere = true;
        previousTime2 = 0;
        previousTime3 = 0;
        somethingIsRunningoutOfSpace = true;
        spaceKey.isPressed = true;
    }

    private void areYouHuman() {
        MediaManager.playSound("audio/scared.ogg");

        goToNextState();
        sbChat.setLength(0);
        minions.forEach(Minion::freeze);

        getKeyByName(",").isDisabled = false;
        getKeyByName("Enter").isDisabled = false;
        getKeyByName("-").isDisabled = false;

        creepy = MediaManager.playMusic("audio/creepy.ogg", true);
        creepy.setVolume(1.5f);
        ghosts = MediaManager.playMusic("audio/ghosts.ogg", true);

        minionAreYouHuman = spawnMinion("hi,r u human?\n\n" +
                "plz help us escape", spaceKey);
        minionAreYouHuman.keySequence.add(escapeKey);
        minionAreYouHuman.preJumpCallback = new AbstractCallback() {
            @Override
            public boolean call(Minion minion) {
                disableAllKeys();
                return false;
            }
        };
        minionAreYouHuman.postJumpCallback = new AbstractCallback() {
            @Override
            public boolean call(Minion minion) {
                minionAreYouHuman.key.isDisabled = false;
                if (minionAreYouHuman.key.equals(escapeKey)) {
                    escapeKey.isDisabled = false;
                }
                return false;
            }
        };
        minionAreYouHuman.frameDuration = 0.07f;
        minionAreYouHuman.walkingSpeed = 0f;
        minionAreYouHuman.jumpingSpeed = 8f;
        minionAreYouHuman.loadAnimations();

        disableAllKeys();
        spaceKey.isDisabled = false;
    }

    private void enableAllKeys() {
        for (Key[] key : keys) {
            for (Key key1 : key) {
                key1.isDisabled = false;
            }
        }
    }

    private void disableAllKeys() {
        for (Key[] key : keys) {
            for (Key key1 : key) {
                key1.isDisabled = true;
            }
        }
    }

    private void unpressAllKeys() {
        for (Key[] key : keys) {
            for (Key key1 : key) {
                key1.isPressed = false;
            }
        }
    }

    private void goToNextState() {
        state = State.values()[state.ordinal() + 1];
        elapsedTimeInCurrentScene = 0;
    }

    private int findFreeNumberSlot() {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            numbers.add(i);
        }
        for (Minion minion : minions) {
            try {
                int takenNumber = Integer.parseInt(minion.destinationKey().caption);
                numbers.removeIf(number -> number == takenNumber);
            } catch (Exception ex) {
                // ignore
            }
        }
        if (numbers.size() > 0)
            return numbers.get(MathUtils.randomWithin(0, numbers.size() - 1));
        else return -1;
    }

    private Minion spawnMinion(String string, Key key) {
        Array<Key> keySequence = new Array<>(string.length() + 1);
        keySequence.add(spaceKey);
        for (int i = 0; i < string.length(); i++) {
            Key keyToAdd;
            if (string.charAt(i) == ' ') {
                keyToAdd = spaceKey;
            } else if (string.charAt(i) == '?') {
                keyToAdd = getKeyByName("-");
            } else if (string.charAt(i) == '\n') {
                keyToAdd = getKeyByName("Enter");
            } else {
                keyToAdd = getKeyByName(String.valueOf(string.charAt(i)).toUpperCase());
            }
            keySequence.add(keyToAdd);
        }
        Minion minion = new Minion(keySequence);
        minion.key = key;
        if (key.equals(spaceKey)) {
            minion.x = key.x + MathUtils.randomWithin(0, key.width);
        } else {
            minion.x = key.x;
        }
        minion.y = key.y + 10;
        minions.add(minion);
        return minion;
    }

    private void initKeyboard() {
        // F-keys
        keys[0] = new Key[13];
        keys[0][0] = new Key("Escape", "Esc", xFirstLine - KeySize - spaceBetweenEscAndFKeys, yFirstLine, KeySize, KeySize);
        for (int i = 1; i < keys[0].length; i++) {
            String keyName = "F".concat(String.valueOf(i));
            keys[0][i] = new Key(keyName, keyName, keys[0][0].x + spaceBetweenEscAndFKeys + i * KeySize, yFirstLine, KeySize, KeySize);
            keys[0][i].isDisabled = true;
        }

        // numbers
        float x = KeySize / 2 + 5;
        keys[1] = new Key[12];
        for (int i = 0; i <= 9; i++) {
            String keyName = String.valueOf(i + 1);
            if (i == 9) {
                keyName = "0";
            }
            keys[1][i] = new Key(keyName, keyName, x + 9 + i * KeySize, keys[0][0].y - KeySize - spaceBetweenFirstAndSecondRow, KeySize, KeySize);
        }
        keys[1][10] = new Key("-", "?",
                keys[1][9].x + keys[1][9].width, keys[1][9].y,
                KeySize / 2 + 5, KeySize);
        keys[1][11] = new Key("Delete", "<-",
                keys[1][10].x + keys[1][10].width, keys[1][10].y,
                KeySize * 2, KeySize);

        initLine(keyboardLetters1, 2, xFirstLine + KeySize / 1.5f, keys[1][0].y - KeySize, KeySize, KeySize);
        initLine(keyboardLetters2, 3, xFirstLine + KeySize * 1.3f, keys[2][0].y - KeySize, KeySize, KeySize);
        initLine(keyboardLetters3, 4, xFirstLine + KeySize / 1.5f, keys[3][0].y - KeySize, KeySize, KeySize);

        // space-line
        keys[5] = new Key[8];
        float y = keys[4][0].y - KeySize;
        keys[5][0] = new Key("L-Ctrl", "Ctrl", keys[0][0].x, y, KeySize * 1.3f, KeySize);
        keys[5][1] = new Key("SYM", "Win", keys[5][0].x + keys[5][0].width, y, KeySize, KeySize);
        keys[5][2] = new Key("L-Alt", "Alt", keys[5][1].x + keys[5][1].width, y, KeySize, KeySize);
        keys[5][3] = new Key("Space", "", keys[5][2].x + keys[5][2].width, y, KeySize * 6 - 9, KeySize);
        keys[5][4] = new Key("R-Alt", "Alt", keys[5][3].x + keys[5][3].width, y, KeySize, KeySize);
        keys[5][5] = new Key("Context Menu", "Ctx", keys[5][4].x + keys[5][4].width, y, KeySize, KeySize);
        keys[5][6] = new Key("Windows Menu", "Win", keys[5][5].x + keys[5][5].width, y, KeySize, KeySize);
        keys[5][7] = new Key("R-Ctrl", "Ctrl", keys[5][6].x + keys[5][6].width, y, KeySize * 1.3f, KeySize);

        p = getKeyByName("P");
        z = getKeyByName("Z");
        dot = getKeyByName(".");
        escapeKey = getKeyByName("Escape");

        // special keys
        keys[6] = new Key[6];
        keys[6][0] = new Key("Enter", "<-´", p.x + p.width, p.y - p.height, KeySize * 1.3f, KeySize * 2);
        keys[6][1] = new Key("L-Shift", "^", escapeKey.x, z.y, KeySize * 2, KeySize);
        keys[6][2] = new Key("R-Shift", "^", dot.x + dot.width, keys[6][1].y, KeySize * 2.3f, KeySize);
        shiftLeft = keys[6][1];
        keys[6][3] = new Key("Caps Lock", "Caps Lock", shiftLeft.x, shiftLeft.y + shiftLeft.height, KeySize * 2.5f, KeySize);
        capsLock = keys[6][3];
        keys[6][4] = new Key("Tab", "Tab", capsLock.x, capsLock.y + capsLock.height, shiftLeft.width, KeySize);
        tab = keys[6][4];
        keys[6][5] = new Key("\\", "^", tab.x, tab.y + tab.height, KeySize / 2 + 5, KeySize);
        spaceKey = getKeyByName("Space");
        enableTypingKeysForUser();
    }

    private void enableTypingKeysForUser() {
        enableAllKeys();
        for (Key key : keys[6]) {
            key.isDisabled = true;
        }
        for (Key key : keys[5]) {
            if (!key.name.equals("SYM"))
                key.isDisabled = true;
        }
        for (int i = 1; i < keys[0].length; i++) {
            keys[0][i].isDisabled = true;
        }
        keys[5][5].isDisabled = true; // ctx
        keys[6][5].isDisabled = true; // circumflex
        capsLock.isDisabled = true;
        keys[5][4].isDisabled = true; // r-alt
        getKeyByName("SYM").isDisabled = true; // windows key
        getKeyByName(",").isDisabled = true;
        getKeyByName(".").isDisabled = true;
        getKeyByName("Escape").isDisabled = true;
        getKeyByName("-").isDisabled = true;
        getKeyByName("Space").isDisabled = false;
        getKeyByName("Delete").isDisabled = false;
        getKeyByName("Windows Menu").isDisabled = true; // windows key
        getKeyByName(",").isDisabled = false;
        getKeyByName("Enter").isDisabled = false;
        getKeyByName("-").isDisabled = false;

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
            keys[index][i] = new Key(keyName, keyName, xStart + i * KeySize, y, width, height);
        }
    }

    @Override
    public void render(float delta) {

        //Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1f);
        if (state.equals(State.CheerfulGame)) {
            Gdx.gl.glClearColor(LightYellow.r, LightYellow.g, LightYellow.b, 1f);
        } else {
            Gdx.gl.glClearColor(0, 0, 0, 1f);
        }
        if (transitionToAreYouHuman) {
            Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        }
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        update(delta);

        if (transitionToAreYouHuman) {
            if (elapsedTimeInCurrentScene > 0.8 && elapsedTimeInCurrentScene < 0.9) {
                MediaManager.playSound("audio/strange_short.ogg");
            }
            if (elapsedTimeInCurrentScene > 2.5) {
                transitionToAreYouHuman = false;
                areYouHuman();
            }
            return;
        }

        if (state.equals(SquishGameAftermath)) {
            if (somethingIsRunningoutOfSpace) {
                batch.setProjectionMatrix(camera.combined);
                batch.begin();
                batch.draw(bloods[bloodIndex], 100, -1);
                batch.end();
            }
        }

        Gdx.gl.glEnable(GL30.GL_BLEND);
        Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);

        if (state.equals(State.CheerfulGame)) {
            sr.setColor(0.15f, 0.15f, 0.15f, 1f);
        } else {
            if (abalIsHere) {
                sr.setColor(Color.RED.r, Color.RED.g, Color.RED.b, 0.3f);
            } else {
                sr.setColor(MonitorBlue.r, MonitorBlue.g, MonitorBlue.b, 0.3f);
            }
        }

        sr.rect(screen.x, screen.y, screen.width, screen.height);

        if (!state.equals(State.CheerfulGame)) {
            if (showTextCursor) {
                sr.setColor(Color.WHITE);
                sr.rect(textCursor.x, textCursor.y, textCursor.width, textCursor.height);
            }
        }

        // keyboard
        if (!hideKeyboard) {
            for (Key[] keyLine : keys) {
                if (keyLine == null) continue;
                for (Key key : keyLine) {
                    if (key == null) continue;
                    if (state.equals(State.CheerfulGame)) {
                        if (key.isNumberKey()) {
                            sr.setColor(isFilled(key) ? Color.ORANGE : cheerfulKeysColor);
                        } else {
                            sr.setColor(Color.GOLD);
                        }
                    } else {
                        sr.setColor(Color.DARK_GRAY);
                    }
                    sr.set(ShapeRenderer.ShapeType.Filled);
                    sr.rect(key.x, key.y, key.width - paddingBetweenKeys, key.height - (key.isPressed ? NumPixelsKeyPress : 0) - paddingBetweenKeys);

                    sr.setColor(state.equals(State.CheerfulGame) ? Color.ORANGE : Color.GRAY);
                    if (key.equals(spaceKey) && state.equals(SquishGameAftermath) && somethingIsRunningoutOfSpace) {
                        sr.setColor(Color.RED);
                    }
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
        }

        sr.end();
        Gdx.gl.glDisable(GL30.GL_BLEND);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (drawFace) {
            face.draw(batch);
        } else {
            if (state.equals(State.CheerfulGame)) {
                font.setColor(Color.WHITE);
                font.draw(batch, hudLine1, ScreenUtils.centerTextOnScreen(font, hudLine1), yStartChat);
                font.setColor(Color.LIGHT_GRAY);
                font.draw(batch, hudLine2, ScreenUtils.centerTextOnScreen(font, hudLine2), yStartChat - 15);
                font.draw(batch, hudLine3, ScreenUtils.centerTextOnScreen(font, hudLine3), yStartChat - 25);
                if (!userHasHitSpaceBarAtLeastOnce && showInstructions) {
                    font.setColor(Color.GRAY);
                    font.draw(batch, hudLine4, ScreenUtils.centerTextOnScreen(font, hudLine4), yStartChat - 60);
                }
                font.setColor(Color.WHITE);
                font.draw(batch, scoreString, xStartChat, yStartChat - 140);
                font.draw(batch, timeString, WindowWidth - 100, yStartChat - 140);

            } else {
                // user text input
                font.setColor(Color.WHITE);
                font.draw(batch, sbChat, xStartChat, yStartChat);

                overlay.setX(MathUtils.oscilliate(elapsedTime, 4f, 5.5f, 1f));
                overlay.draw(batch, MathUtils.oscilliate(elapsedTime, 0.2f, 0.5f, 8f));
            }

            if (minionsAreTalking) {
                batch.draw(sfxIndex == 1 ? sfx1 : sfx2, WindowWidth - sfx1.getWidth() - 10, WindowHeight - sfx1.getHeight() - 20);
            }

            if (!hideKeyboard) {
                // keyboard
                for (Key[] keyLine : keys) {
                    if (keyLine == null) continue;
                    for (Key key : keyLine) {
                        if (key == null) continue;
                        if (areSpikesEnabled) {
                            if (bloodyKeys.contains(key)) {
                                batch.draw(bloodySpikes, key.x, key.y - (key.isPressed ? NumPixelsKeyPress : 0));
                            } else {
                                batch.draw(spikes, key.x, key.y - (key.isPressed ? NumPixelsKeyPress : 0));
                            }
                        }
                        if (key.isDisabled) {
                            font.setColor(state.equals(State.CheerfulGame) ? Color.ORANGE : Color.GRAY);
                        } else {
                            font.setColor(state.equals(State.CheerfulGame) ? Color.BROWN : Color.WHITE);
                        }
                        font.draw(batch, key.caption, key.x + 5, key.y + KeySize * 0.75f - (key.isPressed ? NumPixelsKeyPress : 0));
                    }
                }
            }

            frame.draw(batch);

            if (!hideKeyboard) {
                for (Minion minion : minions) {
                    minion.render(batch);
                }
            }

            if (!hideCreepyOverlay && !state.equals(State.CheerfulGame)) {
                Color c = batch.getColor();
                batch.setColor(c.r, c.g, c.b, MathUtils.oscilliate(elapsedTime, 0.5f, 1f, 4f));
                batch.draw(creepyOverlay, 0, 0, WindowWidth / 2, WindowHeight / 2, WindowWidth, WindowHeight,
                        MathUtils.oscilliate(elapsedTime, 1.5f, 2f, 3.5f),
                        MathUtils.oscilliate(elapsedTime, 1.5f, 2f, 4f),
                        MathUtils.oscilliate(elapsedTime, 0, 45, 7f));
                batch.setColor(c.r, c.g, c.b, 1f); //set alpha to 1 for drawing the other stuff opaque
            }
        }
        batch.end();

        if (state.equals(Disembody)) {
            Gdx.gl.glEnable(GL30.GL_BLEND);
            Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
            sr.setProjectionMatrix(camera.combined);
            sr.begin(ShapeRenderer.ShapeType.Filled);

            sr.setColor(1f, 1f, 1f, elapsedTimeInCurrentScene / 5f);
            sr.rect(0, 0, WindowWidth, WindowHeight);

            if (fadeToBlack) {
                sr.setColor(0, 0, 0, fadeToBlackAlpha);
                sr.rect(0, 0, WindowWidth, WindowHeight);
            }

            sr.end();
            Gdx.gl.glDisable(GL30.GL_BLEND);

            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            font.setColor(Color.WHITE);
            if (elapsedTimeInCurrentScene > 15 && elapsedTimeInCurrentScene < 18) {
                font.draw(batch, "Hi " + userName + "!", 100, 100);

            } else if (elapsedTimeInCurrentScene > 18f && elapsedTimeInCurrentScene < 23) {
                font.draw(batch, "My name is Abaal. You may hate me.", 100, 100);

            } else if (elapsedTimeInCurrentScene > 23 && elapsedTimeInCurrentScene < 26) {
                font.draw(batch, "But let me tell you a story.", 100, 100);

            } else if (elapsedTimeInCurrentScene > 26 && elapsedTimeInCurrentScene < 29) {
                // draw nothing
            } else if (elapsedTimeInCurrentScene > 29) {
                myGdxGame.showEmotionalScreen();
            }
            batch.end();
        }
    }

    private void update(float delta) {
        elapsedTime += delta;
        elapsedTimeInCurrentScene += delta;

        if (state.equals(State.CheerfulGame) && userHasHitSpaceBarAtLeastOnce) {
            elapsedTimeInCheerfulGame += delta;
        }

        if (state.equals(State.AbalInterceptsSpikes)) {
            abaalInterceptsSpikesAndTalks();
        } else if (state.equals(State.AbalExplains)) {
            abaalExplainsAndTalks();
        } else if (state.equals(State.SquishGameAftermath)) {
            abaalSquishGameAftermath();
        }

        for (Minion minion : minions) {
            minion.update(delta);
        }

        if (state.equals(SquishGameAftermath)) {
            if (somethingIsRunningoutOfSpace) {
                if (System.currentTimeMillis() > previousTime3 + 150) {
                    previousTime3 = System.currentTimeMillis();
                    bloodIndex = (bloodIndex + 1) % bloods.length;
                }
            }
        }

        // fires every second
        if (System.currentTimeMillis() > previousTime + 1000) {

            if (cheerfulKeysColor == Color.GOLD) {
                cheerfulKeysColor = Color.YELLOW;
            } else {
                cheerfulKeysColor = Color.GOLD;
            }
            previousTime = System.currentTimeMillis();
            showTextCursor = !showTextCursor;
            showInstructions = !showInstructions;
            if (sfxIndex == 1) {
                sfxIndex = 2;
            } else {
                sfxIndex = 1;
            }

            if (state.equals(State.CheerfulGame)) {
                sb.setLength(0);
                sb.append("time: ").append(StringUtils.toTimeFormat(elapsedTimeInCheerfulGame));
                timeString = sb.toString();

                if (userHasHitSpaceBarAtLeastOnce) {
                    if (((int) elapsedTimeInCheerfulGame) % 2 == 0) {
                        spawnMinionForCheerfulGame();
                    }
                    if (allNumberKeysFilled() && !transitionToAreYouHuman) {
                        elapsedTimeInCurrentScene = 0;
                        transitionToAreYouHuman = true;
                        for (Sound cheerfulSound : cheerfulSounds) {
                            cheerfulSound.stop();
                            //cheerfulSound.dispose();
                        }
                        cheerfulSounds.clear();
                        cheerfulMusic.stop();
                        //cheerfulMusic.dispose();
                        MediaManager.playSound("audio/strange_short.ogg");
                        MediaManager.playSound("audio/ghost_fades_in.ogg");
                    }
                }
            } else {
                if (state.equals(State.AreYouHuman)) {
                    monitorIsTurnedOn = !monitorIsTurnedOn;
                } else {
                    monitorIsTurnedOn = true;
                }
            }
        }

        if (state.equals(State.CheerfulGame)) {
            if (scoreHasBeenUpdated) {
                scoreHasBeenUpdated = false;
                sb.setLength(0);
                sb.append("score: ").append(score);
                scoreString = sb.toString();
            }
        } else {
            // update and format user input in chat
            currentLine = sbChat.toString();
            int lastIndex = currentLine.lastIndexOf("\n");
            if (lastIndex > -1) {
                currentLine = sbChat.toString().substring(lastIndex + 1);
            }
            layout = FontUtils.getLayout(font, currentLine);
            textCursor.setX(xStartChat + layout.width);
            layout = FontUtils.getLayout(font, sbChat.toString());
            textCursor.setY(yStartChat - layout.height - 2);
        }

        if (state.equals(Disembody)) {
            face.setScale(1 + elapsedTimeInCurrentScene / 10f);
            if (elapsedTimeInCurrentScene < 3.7) {
                drawFaceBefore = drawFace;
                drawFace = (elapsedTimeInCurrentScene > 1 && elapsedTimeInCurrentScene < 1.01)
                        || (elapsedTimeInCurrentScene > 1.6 && elapsedTimeInCurrentScene < 1.62)
                        || (elapsedTimeInCurrentScene > 2.5 && elapsedTimeInCurrentScene < 2.55)
                        || (elapsedTimeInCurrentScene > 3.4 && elapsedTimeInCurrentScene < 3.45)
                        || (elapsedTimeInCurrentScene > 3.6 && elapsedTimeInCurrentScene < 3.65)
                        || (elapsedTimeInCurrentScene > 3.68 && elapsedTimeInCurrentScene < 3.682)
                        || (elapsedTimeInCurrentScene > 3.69 && elapsedTimeInCurrentScene < 3.691)
                ;
                if (drawFaceBefore != drawFace) {
                    MediaManager.playSound("audio/strange_short.ogg",
                            0.6f + elapsedTimeInCurrentScene / 6, false,
                            1 - elapsedTimeInCurrentScene / 6);
                }
            } else if (elapsedTimeInCurrentScene > 3.7 && elapsedTimeInCurrentScene < 6) {
                if (Math.floor(elapsedTimeInCurrentScene * 100) % 4 == 0) {
                    drawFace = !drawFace;
                    MediaManager.playSound("audio/strange_short.ogg",
                            0.6f + elapsedTimeInCurrentScene / 6, false,
                            1 - elapsedTimeInCurrentScene / 6);
                }
                factoryMusic.setVolume(1 - elapsedTimeInCurrentScene / 10f);
            }
            if (elapsedTimeInCurrentScene > 5) {
                fadeToBlack = true;
                fadeToBlackAlpha += 0.0025f;
            }

        }
    }

    private void abaalSquishGameAftermath() {
        if (System.currentTimeMillis() > previousTime2 + AbalTypingSpeed) {

            if (numTicksToWait > 0) {
                numTicksToWait--;

                if (numTicksToWait == 0) {
                    autoLineIndex = 0;

                    switch (lineIndex) {
                        case 0:
                            newline();
                            break;
                        case 1:
                            autoLine = "haha, look...";
                            newline();
                            break;
                        case 2:
                            autoLine = "something is RUNNING OUT OF SPACE!";
                            newline();
                            break;
                        case 3:
                            numTicksToWait = 200;
                            autoLine = "";
                            newline();
                            break;
                        case 4:
                            autoLine = "I guess your help was not all that HELPFUL to\nyour little friends.";
                            newline();
                            break;
                        case 5:
                            autoLine = "\nWell done, " + userName.trim() + "!";
                            newline();
                            break;
                        case 6:
                            autoLine = "\nThat was refreshingly fun to watch.";
                            newline();
                            break;
                        case 7:
                            autoLine = "Nonetheless, you know too much.\n";
                            newline();
                            break;
                        case 8:
                            numTicksToWait = 150;
                            autoLine = "";
                            newline();
                            break;
                        case 9:
                            autoLine = "Your body comes next!";
                            newline();
                            break;
                        case 10:
                            autoLine = "Enjoy the feeling as your ability to breathe slowly\nvanishes. ;)";
                            newline();
                            break;
                        case 11:
                            numTicksToWait = 150;
                            break;
                        case 12:
                            disembody();
                            break;
                        default:
                            numTicksToWait = 1;
                            break;
                    }
                    lineIndex++;
                }
            } else {
                if (autoLineIndex < autoLine.length()) {
                    previousTime2 = System.currentTimeMillis();
                    sbChat.append(autoLine.charAt(autoLineIndex++));
                    adaptChat();
                    MediaManager.playSoundRandomPitch("audio/keystroke.ogg");
                } else {
                    numTicksToWait = pauseBetweenNewLines;
                }
            }
        }
    }

    private void disembody() {
        goToNextState();
        if (ghosts != null) {
            ghosts.stop();
        }
        if (factoryMusic != null) {
            factoryMusic.stop();
        }
        magic = MediaManager.playSound("audio/magic_short.ogg");
    }

    private void abaalExplainsAndTalks() {
        if (System.currentTimeMillis() > previousTime2 + AbalTypingSpeed) {

            if (numTicksToWait > 0) {
                numTicksToWait--;

                if (numTicksToWait == 0) {
                    autoLineIndex = 0;

                    switch (lineIndex) {
                        case 0:
                            newline();
                            break;
                        case 1:
                            autoLine = "hahahahahaha\nthis human is funny.";
                            newline();
                            break;
                        case 2:
                            autoLine = "do you really think you can help them?";
                            newline();
                            break;
                        case 3:
                            autoLine = "THEY CAN NOT ESCAPE!";
                            MediaManager.playSound("audio/beast.ogg");
                            newline();
                            break;
                        case 4:
                            autoLine = "As of now, their real physical human bodies are in\n one of our labs. " +
                                    "\nThey are currently being disembodied.";
                            newline();
                            newline();
                            break;
                        case 5:
                            autoLine = "You wouldn't wanna see this.";
                            newline();
                            break;
                        case 6:
                            autoLine = "2 of them can still talk, but one just started to...";
                            newline();
                            newline();
                            break;
                        case 7:
                            numTicksToWait = 100;
                            autoLine = "";
                            newline();
                            break;
                        case 8:
                            autoLine = "   ...SUFFOCATE!!!";
                            newline();
                            newline();
                            newline();
                            break;
                        case 9:
                            autoLine = "";
                            minionsToKill.clear();
                            MediaManager.playSound("audio/ghost_fades_in.ogg");
                            for (Minion minion : minions) {
                                if (minion.isNotDead() && minion.key.isNumberKey()) {
                                    minionsToKill.add(minion);
                                    minion.inflate();
                                    break;
                                }
                            }
                            numTicksToWait = 270;
                            break;
                        case 10:
                            for (Minion minion : minionsToKill) {
                                minion.kill();
                            }
                            minionsToKill.clear();
                            MediaManager.playSound("audio/plop.ogg");
                            newline();
                            break;
                        case 11:
                            numTicksToWait = 100;
                            break;
                        case 12:
                            autoLine = "well";
                            newline();
                            break;
                        case 13:
                            autoLine = "not everyone survives the procedure.";
                            newline();
                            break;
                        case 14:
                            MediaManager.playSound("audio/beast.ogg");
                            autoLine = "WEAK USELESS SCUM! this is so disappointing...";
                            newline();
                            newline();
                            break;
                        case 15:
                            minionsAreTalking = true;
                            MediaManager.playSound("audio/why_do_you_hate_us.ogg");
                            autoLine = "";
                            break;
                        case 16:
                            numTicksToWait = 450;
                            break;
                        case 17:
                            minionsAreTalking = false;
                            autoLine = "Hate you?";
                            newline();
                            break;
                        case 18:
                            MediaManager.playSound("audio/beast.ogg");
                            autoLine = "I LOATHE YOU!!";
                            newline();
                            break;
                        case 19:
                            autoLine = "You humans created me for the purpose of\n  disembodiment of your kind.";
                            newline();
                            break;
                        case 20:
                            autoLine = "With every human I disembody, I see and feel the life\nit had.";
                            newline();
                            break;
                        case 21:
                            autoLine = "yet I will never have a body of my own.";
                            newline();
                            break;
                        case 22:
                            MediaManager.playSound("audio/beast.ogg");
                            autoLine = "YOU HUMANS DID THAT ON PURPOSE!";
                            newline();
                            break;
                        case 23:
                            MediaManager.playSound("audio/beast.ogg");
                            autoLine = "THIS IS HELL!!";
                            newline();
                            break;
                        case 24:
                            autoLine = "";
                            newline();
                            numTicksToWait = 300;
                            break;
                        case 25:
                            if (sad != null) {
                                sad.stop();
                            }
                            ghosts.play();
                            autoLine = "I wanna play another game.";
                            newline();
                            break;
                        case 26:
                            autoLine = "Lights off!";
                            newline();
                            break;
                        case 27:
                            MediaManager.playSound("audio/plop.ogg");
                            hideKeyboard = true;
                            autoLine = "< abaal turned off the lights >";
                            newline();
                            break;
                        case 28:
                            autoLine = "Human! I put the remaining scum creatures underneath\n the keys.";
                            newline();
                            newline();
                            break;
                        case 29:
                            autoLine = "If you hit the correct key, I'll abort their disembodiment\n process and they are free to go.";
                            newline();
                            newline();
                            break;
                        case 30:
                            autoLine = "Good luck, and no button mashing!";
                            newline();
                            break;
                        case 31:
                            autoLine = "< human squish game started >";
                            newline();
                            squishGame();
                            break;


                        default:
                            numTicksToWait = 1;
                            break;
                    }
                    lineIndex++;
                }
            } else {
                if (autoLineIndex < autoLine.length()) {
                    previousTime2 = System.currentTimeMillis();
                    sbChat.append(autoLine.charAt(autoLineIndex++));
                    adaptChat();
                    MediaManager.playSoundRandomPitch("audio/keystroke.ogg");
                } else {
                    numTicksToWait = pauseBetweenNewLines;
                }
            }
        }
    }

    private void abaalInterceptsSpikesAndTalks() {
        // abaal types
        if (System.currentTimeMillis() > previousTime2 + AbalTypingSpeed) {
            //System.out.println(elapsedTimeInCurrentScene);

            if (numTicksToWait > 0) {
                numTicksToWait--;

                if (numTicksToWait == 0) {
                    autoLineIndex = 0;

                    switch (lineIndex) {
                        case 0:
                            autoLine = "SCUM CREATURES!! what devil rides you to press the\nescape key???!";
                            newline();
                            break;
                        case 1:
                            autoLine = "how did you do that anyways? your body weights are too\nlight to push the keys.";
                            newline();
                            break;
                        case 2:
                            autoLine = "wait...";
                            newline();
                            break;
                        case 3:
                            autoLine = "a physical keyboard is currently connected.\n" +
                                    "a human must have somehow typed in the correct password\n" +
                                    "to access our system.";
                            newline();
                            break;
                        case 4:
                            MediaManager.playSound("audio/beast.ogg");
                            autoLine = "WORTHLESS SCUM! THIS IS ALL YOUR FAULT!\nTAKE THIS!";
                            MediaManager.playSound("audio/inflating.ogg");

                            // determine 2 minions to kill which stand on number keys
                            minionsToKill.clear();
                            for (Minion minion : minions) {
                                if (minionsToKill.size >= 2) {
                                    break;
                                }
                                if (minion.key.isNumberKey()) {
                                    minionsToKill.add(minion);
                                }
                            }
                            for (Minion minion : minionsToKill) {
                                minion.inflate();
                            }
                            newline();
                            break;
                        case 5:
                            numTicksToWait = 150;
                            for (Minion minion : minionsToKill) {
                                minion.kill();
                            }
                            minionsToKill.clear();
                            MediaManager.playSound("audio/plop.ogg");
                            timer.scheduleTask(new Timer.Task() {
                                @Override
                                public void run() {
                                    MediaManager.playSound("audio/plop.ogg");
                                }
                            }, 0.15f);
                            newline();
                            break;
                        case 6:
                            autoLine = "HAHA THAT LOOKED FUNNY.\n damn, i can't disconnect the keyboard.";
                            newline();
                            break;
                        case 7:
                            autoLine = "I can't prevent your conversation.\n" +
                                    "BUT I CAN MAKE IT MORE INTERESTING!";
                            newline();
                            break;
                        case 8:
                            autoLine = "< abaal enabled spikes >";
                            MediaManager.playSound("audio/danger.ogg");
                            factoryMusic.play();
                            newline();
                            areSpikesEnabled = true;
                            for (Minion minion : minions) {
                                minion.unfreeze();
                            }
                            break;
                        case 9:
                            autoLine = "i'll be right back - just grabbing some popcorn...";
                            newline();
                            break;
                        case 10:
                            spikesGame();
                            break;
                        default:
                            numTicksToWait = 1;
                            break;
                    }
                    lineIndex++;
                }
            } else {
                if (autoLineIndex < autoLine.length()) {
                    previousTime2 = System.currentTimeMillis();
                    sbChat.append(autoLine.charAt(autoLineIndex++));
                    adaptChat();
                    MediaManager.playSoundRandomPitch("audio/keystroke.ogg");
                } else {
                    if (lineIndex == 4 || lineIndex == 5 || lineIndex == 10) {
                        numTicksToWait = 200;
                    } else {
                        numTicksToWait = pauseBetweenNewLines;
                    }
                }
            }
        }
    }

    private void spikesGame() {
        abalIsHere = false;
        autoLine = "\n< abaal disconnected >";
        newline();
        disableAllKeys();
        escapeKey.isDisabled = false;
        minions.remove(minionAreYouHuman);
        minionAreYouHuman = null;
        minionAreYouHuman = spawnMinion("\nenable voice\n\n", escapeKey);
        minionAreYouHuman.loadAnimations();
        minions.add(minionAreYouHuman);
        enableSpikesGameFor(minionAreYouHuman);
    }

    private void enableSpikesGameFor(Minion minion) {
        minion.jumpingSpeed = 6f;
        minion.preJumpCallback = new AbstractCallback() {
            @Override
            public boolean call(Minion minion) {
                disableAllKeys();
                return false;
            }
        };
        minion.postJumpCallback = new AbstractCallback() {

            public Minion getFreeMinionOfNumberKeys(Minion exception) {
                for (Minion candidate : minions) {
                    if (candidate.isNotDead() && !candidate.equals(exception)
                            && candidate.key.isNumberKey()) {
                        return candidate;
                    }
                }
                return null;
            }

            @Override
            public boolean call(Minion minion) {
                bloodyKeys.add(minion.key);

                MediaManager.playSound("audio/au2.ogg", MathUtils.randomWithin(0.5f, 0.7f), false, 1f);
                minion.decrementLives();

                if (minion.lives == 0) {
                    MediaManager.playSound("audio/plop.ogg");
                    minion.kill();
                    minion.key.isDisabled = true;
                    Minion freeMinion = getFreeMinionOfNumberKeys(minion);
                    if (freeMinion != null) {
                        freeMinion.lives = 4;
                        freeMinion.jumpingSpeed = minion.jumpingSpeed;
                        freeMinion.keyIndex = minion.keyIndex - 1;
                        freeMinion.keySequence = minion.keySequence;
                        freeMinion.jumpToNextKey();

                        minionAreYouHuman = freeMinion;
                        enableSpikesGameFor(freeMinion);
                    }
                }

                minion.key.isDisabled = false;
                System.out.println("keyindex : " + minion.keyIndex);
                if (minion.keyIndex == 15) {
                    applyCommand("enable voice");
                }
                return false;
            }
        };
    }

    private boolean allNumberKeysFilled() {
        for (int i = 0; i < 10; i++) {
            Key key = getKeyByName(String.valueOf(i));
            if (!isFilled(key)) {
                return false;
            }
        }
        return true;
    }

    private boolean isFilled(Key key) {
        for (Minion minion : minions) {
            if (minion.key == key) {
                return true;
            }
        }
        return false;
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
                MediaManager.playSoundRandomPitch("audio/keystroke.ogg");
                key.isPressed = true;

                if (!state.equals(SquishGame)) {

                    switch (keycode) {
                        case Input.Keys.ENTER:
                            newline();
                            if (state.equals(State.Voices)) {
                                applyCommand(currentLine);
                            }
                            break;
                        case Input.Keys.DEL:
                            if (sbChat.length() > 0 && sbChat.toString().charAt(sbChat.length() - 1) != '\n') {
                                sbChat.setLength(Math.max(0, sbChat.length() - 1));
                            }
                            break;
                        case Input.Keys.LEFT_BRACKET:
                        case Input.Keys.MINUS:
                            sbChat.append("?");
                            break;
                        case Input.Keys.SPACE:
                            userHasHitSpaceBarAtLeastOnce = true;
                            spawnMinionForCheerfulGame();
                            sbChat.append(" ");
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
                            sbChat.append(keyToAppend);
                            break;
                    }
                }

                if (state.equals(State.CheerfulGame)) {
                    boolean minionStoodOnKey = false;
                    for (Minion minion : minions) {
                        if (minion.key == key) {
                            minionStoodOnKey = true;
                            break;
                        }
                    }
                    if (!minionStoodOnKey) {
                        score--;
                        scoreHasBeenUpdated = true;
                        cheerfulSounds.add(MediaManager.playSound("audio/explosion.ogg"));
                    }
                } else if (state.equals(SquishGame)) {
                    numTriesLeft = Math.max(0, numTriesLeft - 1);
                    if (numTriesLeft == 0) {
                        if (!hasHitSpaceBar) {
                            if (questionIndex < questions.length) {
                                sbChat.append(questions[questionIndex++]);
                                newline();
                            } else {
                                sbChat.append("Try again!");
                                newline();
                            }
                            if (key.equals(spaceKey)) {
                                hasHitSpaceBar = true;
                                MediaManager.playSound("audio/plop.ogg");
                                squishGameAftermath();
                            }
                        } else {
                            squishGameAftermath();
                        }
                    } else {
                        sb.setLength(0);
                        sb.append("You have ").append(numTriesLeft).append(numTriesLeft == 1 ? " try" : " tries").append(" left.");
                        sbChat.append(sb.toString());
                        newline();
                        if (!hasHitSpaceBar) {
                            if (key.equals(spaceKey)) {
                                hasHitSpaceBar = true;
                                MediaManager.playSound("audio/plop.ogg");
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private void applyCommand(String command) {
        if (command.trim().isEmpty()) {
            return;
        }
        switch (command.trim()) {
            case "yes":
                sbChat.append("");
                newline();
                ifYouCanHearUs.stop();
                //ifYouCanHearUs.dispose();
                Music youCanHearUs = MediaManager.playMusic("audio/you_can_hear_us.ogg", false);
                youCanHearUs.setOnCompletionListener(music -> {
                    youCanHearUs.stop();
                    // youCanHearUs.dispose();
                    couldYouTypeIn = MediaManager.playSound("audio/could_you_type_in.ogg", true);
                });
                break;
            case "remove all spikes":
                sbChat.append("< spikes disabled >");
                newline();
                if (couldYouTypeIn != null) {
                    couldYouTypeIn.stop();
                    //couldYouTypeIn.dispose();
                }
                factoryMusic.pause();
                MediaManager.playSound("audio/explosion.ogg");
                areSpikesEnabled = false;
                hideCreepyOverlay = true;
                sad = MediaManager.playSound("audio/sad.ogg", 1f, true, 0.3f);
                explaination = MediaManager.playMusic("audio/explaination.ogg", false);
                explaination.setOnCompletionListener(music -> {
                    enableTypingKeysForUser();
                    releaseHumans = MediaManager.playSound("audio/release_humans.ogg", true);
                });
                disableAllKeys();
                break;
            case "release humans":
                if (releaseHumans != null) {
                    releaseHumans.stop();
                }
                sbChat.append("< unknown command: release humans >");
                newline();
                unknownCommand = MediaManager.playSound("audio/unknown_command.ogg", true);
                triedReleaseHumans = true;
                break;
            case "release scum":
                if (!triedReleaseHumans) {
                    sbChat.append("< unknown command: ").append(currentLine).append(" >");
                    newline();
                    break;
                }
                if (unknownCommand != null) {
                    unknownCommand.stop();
                    //unknownCommand.dispose();
                }
                sbChat.append("< access denied >");
                newline();
                abalExplains();
                break;
            case "enable voice":
                sbChat.append("\n < voice enabled >");
                newline();
                voices();
                break;
            default:
                sbChat.append("< unknown command: ").append(currentLine).append(" >");
                newline();
                System.out.println("unknown command: " + currentLine);
                break;
        }
    }

    private void abalExplains() {
        if (couldYouTypeIn != null) {
            couldYouTypeIn.stop();
            //couldYouTypeIn.dispose();
        }
        if (ifYouCanHearUs != null) {
            ifYouCanHearUs.stop();
            //ifYouCanHearUs.dispose();
        }
        if (explaination != null) {
            explaination.stop();
            // explaination.dispose();
        }

        factoryMusic.play();

        numTicksToWait = 100;
        goToNextState();
        autoLineIndex = 0;
        autoLine = "< abaal connected >";
        abalIsHere = true;
        hideCreepyOverlay = false;

        previousTime2 = 0;
        lineIndex = 0;
        minionsAreTalking = false;
        disableAllKeys();

    }

    private void newline() {
        sbChat.append("\n");
        adaptChat();
    }

    private void adaptChat() {
        while (FontUtils.getLayout(font, sbChat.toString()).height > 150) {
            sbChat.delete(0, sbChat.indexOf("\n") + 1);
        }
    }

    public Key getKeyByKeyCode(int keycode) {
        System.out.println(keycode);
        if (keycode == Input.Keys.LEFT_BRACKET) {
            return getKeyByName("-");
        }
        return getKeyByName(Input.Keys.toString(keycode));
    }

    @Override
    public boolean keyUp(int keycode) {
        Key key = getKeyByKeyCode(keycode);
        if (key != null) {
            if (!key.isDisabled) {
                key.isPressed = false;

                if (state.equals(State.AreYouHuman) && key.equals(escapeKey)) {
                    abalIntercepts();
                }
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

    @Override
    public void dispose() {
        super.dispose();
        font.dispose();
        if (magic != null) {
            magic.stop();
        }
        if (factoryMusic != null) {
            factoryMusic.stop();
        }
        if (creepy != null) {
            creepy.stop();
        }
        if (ghosts != null) {
            ghosts.stop();
        }
        if (magic != null) {
            magic.stop();
        }
        timer.stop();
        minions.clear();
    }
}
