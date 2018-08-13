package com.mygdx.starter;

import com.badlogic.gdx.Game;
import com.mygdx.starter.screens.EmotionalScreen;
import com.mygdx.starter.screens.GameScreen;

public class MyGdxGame extends Game {

    @Override
    public void create() {
        setScreen(new EmotionalScreen());
        //setScreen(new GameScreen());
    }

}
