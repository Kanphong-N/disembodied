package com.mygdx.starter.utils;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;


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
}
