package com.mygdx.starter.keyboard;

import com.badlogic.gdx.math.Rectangle;

public class Key {
    public final float width;
    public final float height;
    public final Rectangle rectangle;
    public String name;
    public String caption;
    public boolean isPressed;
    public boolean isDisabled;
    public float x, y;

    public Key(String name, String caption, float x, float y, float width, float height) {
        this.name = name;
        this.caption = caption;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isPressed = false;
        this.rectangle = new Rectangle(x, y, width, height);
    }
}
