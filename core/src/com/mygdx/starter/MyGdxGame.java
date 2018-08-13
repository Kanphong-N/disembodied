package com.mygdx.starter;

import com.badlogic.gdx.Game;
import com.mygdx.starter.screens.CreditsScreen;
import com.mygdx.starter.screens.EmotionalScreen;
import com.mygdx.starter.screens.GameScreen;

public class MyGdxGame extends Game {

    private GameScreen gameScreen;
    private EmotionalScreen emotionalScreen;

    @Override
    public void create() {
        gameScreen = new GameScreen(this);
        setScreen(gameScreen);
    }

    public void showEmotionalScreen() {
        if (gameScreen != null) {
            gameScreen.dispose();
        }
        emotionalScreen = new EmotionalScreen(this);
        setScreen(emotionalScreen);
    }

    public void showCreditsScreen() {
        if (emotionalScreen != null) {
            emotionalScreen.dispose();
        }
        setScreen(new CreditsScreen());
    }
}
