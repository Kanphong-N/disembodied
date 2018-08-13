package com.mygdx.starter.utils;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import static com.mygdx.starter.Constants.WindowWidth;

public class ScreenUtils {
    public static float centerTextOnScreen(BitmapFont font, String text) {
        return WindowWidth / 2 - FontUtils.getLayout(font, text).width / 2;
    }
}
