package com.mygdx.starter.utils;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

/**
 * Created by Christian on 28.02.2018.
 */

public class FontUtils {
    private static GlyphLayout layout = new GlyphLayout();

    public static GlyphLayout getLayout(BitmapFont font, String text) {
        layout.setText(font, text);
        return layout;
    }
}
