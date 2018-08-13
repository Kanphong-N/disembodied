package com.mygdx.starter;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by Christian on 16.02.2018.
 */

public class Constants {

    public static final String GameTitle = "Disembodied";
    public static final int WindowWidth = 1280 / 2 - 210;
    public static final int WindowHeight = 400;

    public enum Colors {Neutral, Blue, Red}

    public enum Directions {North, East, South, West}

    public static float FadingSpeed = 0.02f; // amount of change for alpha values in each tick

    public static float NumPixelsKeyPress = 3f;
    public static Color MonitorBlue = Color.valueOf("#0094FF");
    public static float KeySize = 31f;



}

