package com.mygdx.starter.utils;

import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class MathUtils {

    private static final Random random = new Random();

    public static boolean within(float v, float min, float max) {
        return v >= min && v <= max;
    }

    public static float oscilliate(float x, float min, float max, float period) {
        return max - (float) (Math.sin(x * 2f * Math.PI / period) * ((max - min) / 2f) + ((max - min) / 2f));
    }

    public static int randomWithin(int min, int max) {
        return random.nextInt(max + 1 - min) + min;
    }

    public static float randomWithin(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    public static void main(String[] args) {
        for (int i = 8; i >= 1; i--) {
            System.out.println(0.4f + (1 - (i / 8f)) * 0.9f + MathUtils.randomWithin(-0.1f, 0.1f));
        }
    }

    public static boolean overlaps(Rectangle r1, Rectangle r2) {
        return r1.x < r2.x + r2.width && r1.x + r1.width > r2.x && r1.y < r2.y + r2.height && r1.y + r1.height > r2.y;
    }

    public static float interpolate(float x, float x0, float x1, float y0, float y1) {
        return (y0 * (x1 - x) + y1 * (x - x0)) / (x1 - x0);
    }
}
