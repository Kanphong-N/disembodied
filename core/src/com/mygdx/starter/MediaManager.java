package com.mygdx.starter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

/**
 * Created by Christian on 16.02.2018.
 */

public class MediaManager {

    public static AssetManager assetManager = new AssetManager();
    public static TextureAtlas atlas = new TextureAtlas("textures.atlas");

    public static void playMusic(String assetPath, boolean loop) {
        if (!assetManager.isLoaded(assetPath)) {
            assetManager.load(assetPath, Music.class);
            assetManager.finishLoading();
        }
        Music music = assetManager.get(assetPath, Music.class);
        music.setLooping(loop);
        music.play();
    }

}
