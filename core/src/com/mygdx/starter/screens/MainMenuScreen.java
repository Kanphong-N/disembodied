package com.mygdx.starter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.starter.Constants;
import com.mygdx.starter.MediaManager;
import com.mygdx.starter.utils.FontUtils;

public class MainMenuScreen extends AbstractScreen {

    public MainMenuScreen() {
        super(Constants.WindowWidth, Constants.WindowHeight);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        update(delta);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        FontUtils.BlackCherry.draw(batch, "Hello World", 100, 100);
        batch.end();
    }

    private void update(float delta) {
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
