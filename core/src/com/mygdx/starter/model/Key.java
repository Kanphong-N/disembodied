package com.mygdx.starter.model;

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
    private float centerX;

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

    public float getCenterX() {
        return x + width / 2;
    }

    public float getCenterY() {
        return y + height / 2;
    }
}
