package com.mygdx.starter.utils;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import java.util.Locale;


/**
 * Created by Christian on 20.05.2017.
 */
public class StringUtils {

    static GlyphLayout layout = new GlyphLayout();

    public static GlyphLayout getFontBounds(BitmapFont font, String text) {
        layout.setText(font, text);
        return layout;
    }

    public static int numOccurrences(String haystack, char needle)
    {
        int count = 0;
        for (int i=0; i < haystack.length(); i++)
        {
            if (haystack.charAt(i) == needle)
            {
                count++;
            }
        }
        return count;
    }

    public static String toTimeFormat(float elapsedTime) {
        float millis = elapsedTime * 1000;
        int minutes = (int) (millis / (1000 * 60));
        int seconds = (int) ((millis / 1000) % 60);
        int seconds10 = (int) ((millis / 100) % 10);
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds, seconds10);
    }
}
