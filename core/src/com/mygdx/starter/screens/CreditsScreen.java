package com.mygdx.starter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.starter.Constants;
import com.mygdx.starter.MediaManager;
import com.mygdx.starter.utils.FontUtils;
import com.mygdx.starter.utils.MathUtils;

import static com.mygdx.starter.Constants.WindowHeight;
import static com.mygdx.starter.Constants.WindowWidth;

public class CreditsScreen extends AbstractScreen implements InputProcessor {

    private Music music;
    private final BitmapFont font;
    private final Sprite title;
    private final Sprite heart;
    private Sound musicSound;
    private float fontAlpha = 1f;
    private float fadeSpeed = 0.3f;
    private float elapsedTime;
    private float y = -80;
    private CharSequence phrase = "Thank you for playing" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "Disembodied has been made with    in 3 days" +
            "\n" +
            "\n" +
            "for the Ludum Dare 42 game development competition." +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "The game, music, graphics and voices have been made\n\nby myself within that time." +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "Tools used include:" +
            "\n" +
            "\n" +
            " - the libGDX cross-platform game dev framework" +
            "\n" +
            "\n" +
            " - Android Studio" +
            "\n" +
            "\n" +
            " - Audacity" +
            "\n" +
            "\n" +
            " - Paint.NET" +
            "\n" +
            "\n" +
            " - GDX Texture Packer" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "Equipment used include:" +
            "\n" +
            "\n" +
            " - Rode NT-USB microphone" +
            "\n" +
            "\n" +
            " - my ASUS Zenbook" +
            "\n" +
            "\n" +
            " - a Kawai digital piano" +
            "\n" +
            "\n" +
            " - and my coffee cup :)" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "Crafted with <3 in The Refactory in Vienna, 2018" +
            "\n" +
            "\n" +
            "http://therefactory.bplaced.net";
    private float scale;
    private GlyphLayout layoutw;

    public CreditsScreen() {
        super(WindowWidth, Constants.WindowHeight);

        try {
            music = MediaManager.playMusic("audio/credits2.ogg", false);
        } catch (Exception ex) {
            musicSound  = MediaManager.playSound("audio/credits2.ogg", false);
        }

        Gdx.input.setInputProcessor(this);
        font = new BitmapFont(Gdx.files.internal("fonts/amiga4everpro2.fnt"));

        title = new Sprite(new Texture("title.png"));
        title.setScale(0.5f);
        title.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        heart = new Sprite(new Texture("heart.png"));
        heart.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        update(delta);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.setColor(1f, 1f, 1f, fontAlpha);

        if (elapsedTime >= 60 + 10) {
            Gdx.app.exit();
        } else if (elapsedTime >= 49.226) {
            layoutw = FontUtils.getLayout(font, "- The End -");
            font.draw(batch, "- The End -", WindowWidth / 2 - layoutw.width / 2, WindowHeight / 2 - layoutw.height / 2);
        } else {

            title.draw(batch);

            scale = MathUtils.oscilliate(elapsedTime, .9f, 1f, 1.4f);
            batch.draw(heart,
                    heart.getX() + 223, y - 26,
                    heart.getWidth() / 2,
                    heart.getHeight() / 2,
                    heart.getWidth() / 2 * scale,
                    heart.getHeight() / 2 * scale,
                    scale,
                    scale,
                    0);

            font.draw(batch, phrase, 10, y + 100);
        }

        batch.end();
    }

    private void update(float delta) {
        elapsedTime += delta;

        y += fadeSpeed;

        title.setY(y);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public boolean keyDown(int keycode) {

        return false;
    }


    @Override
    public boolean keyUp(int keycode) {

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
