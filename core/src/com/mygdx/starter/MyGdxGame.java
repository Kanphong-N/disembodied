package com.mygdx.starter;

import com.badlogic.gdx.Game;
import com.mygdx.starter.screens.MainMenuScreen;

public class MyGdxGame extends Game {

    @Override
    public void create() {
        MainMenuScreen screen = new MainMenuScreen();
        setScreen(screen);
    }

}
