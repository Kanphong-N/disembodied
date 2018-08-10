package com.mygdx.starter.utils;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by Christian on 16.02.2018.
 */

public class SpriteUtils {

    private final float windowWidth;
    private final float windowHeight;

    public SpriteUtils(Viewport viewport) {
        this.windowWidth = viewport.getWorldWidth();
        this.windowHeight = viewport.getWorldHeight();
    }

    public void centerH(Sprite sprite) {
        sprite.setX(windowWidth / 2f - sprite.getWidth() / 2f);
    }

    public void centerV(Sprite sprite) {
        sprite.setY(windowHeight / 2f - sprite.getHeight() / 2f);
    }

    public void center(Sprite sprite) {
        centerH(sprite);
        centerV(sprite);
    }

    public void offsetFromTop(Sprite sprite, int offsetY) {
        sprite.setY(windowHeight - sprite.getHeight() - offsetY);
    }

    public void offsetFromBottom(Sprite sprite, int offsetY) {
        sprite.setY(offsetY);
    }

    public void offsetFromRight(Sprite sprite, int offsetX) {
        sprite.setX(windowWidth - sprite.getWidth() - offsetX);
    }
}
