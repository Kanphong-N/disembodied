package com.mygdx.starter.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.starter.Constants;
import com.mygdx.starter.MyGdxGame;

import static com.mygdx.starter.Constants.WindowHeight;
import static com.mygdx.starter.Constants.WindowWidth;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        float scale = 1f;
        config.x = 1920/2 - WindowWidth / 2;
        config.y = 1080/2 - WindowHeight / 2;
        config.width = (int) (WindowWidth * scale);
        config.height = (int) (WindowHeight * scale);
        config.vSyncEnabled = true;
        config.title = Constants.GameTitle;
        //config.fullscreen = true;
        new LwjglApplication(new MyGdxGame(), config);
    }
}
