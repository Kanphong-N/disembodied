package com.mygdx.starter;

import com.badlogic.gdx.Game;
import com.mygdx.starter.screens.EmotionalScreen;
import com.mygdx.starter.screens.GameScreen;

public class MyGdxGame extends Game {

    private GameScreen gameScreen;

    @Override
    public void create() {
        gameScreen  = new GameScreen(this);
        setScreen(gameScreen);
    }

    public void showEmotionalScreen() {
        gameScreen.dispose();
        setScreen(new EmotionalScreen());
    }
}
