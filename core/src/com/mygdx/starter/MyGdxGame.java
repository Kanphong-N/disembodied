package com.mygdx.starter;

import com.badlogic.gdx.Game;
import com.mygdx.starter.screens.MainScreen;

public class MyGdxGame extends Game {

    @Override
    public void create() {
        MainScreen screen = new MainScreen();
        setScreen(screen);
    }

}
