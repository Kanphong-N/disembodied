package com.mygdx.starter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.starter.utils.MathUtils;

/**
 * Created by Christian on 16.02.2018.
 */

public class MediaManager {

    public static AssetManager assetManager = new AssetManager();
    public static TextureAtlas atlas = new TextureAtlas("textures.atlas");
    private static Music music;
    private static String[] musicFiles;
    private static short currentMusicFile;

    /**
     * Either continues the music playback of a previously paused music file or proceeds to the next music file depending on the proceedToNext flag.
     * If there was no previous music file, a new music file is chosen randomly.
     *
     * @param proceedToNext If set to true, a new music file is chosen randomly. If set to false, the previously paused file is being resumed.
     */
    public static void playMusic(boolean proceedToNext) {

        // remember all available music files
        if (musicFiles == null) {
            FileHandle dirHandle = Gdx.files.internal("music");
            FileHandle[] fileList = dirHandle.list();
            musicFiles = new String[fileList.length];
            for (int i = 0; i < musicFiles.length; i++) {
                musicFiles[i] = fileList[i].path();
                Gdx.app.log(MediaManager.class.getName(), "Found '" + fileList[i].path() + "'");
            }
        }

        if (musicFiles.length == 0) {
            Gdx.app.error(MediaManager.class.getName(), "Could not find any music files!");
        } else {
            // select a music file to play
            if (currentMusicFile == -1 || (music == null && !proceedToNext)) {
                // if there is no previous music file, choose a new one randomly
                currentMusicFile = (short) MathUtils.randomWithin(0, musicFiles.length - 1);
                music = loadMusicAsset(musicFiles[currentMusicFile]);
            } else if (proceedToNext) {
                // switch to the next music file randomly
                int previousMusicFile = currentMusicFile;
                do {
                    currentMusicFile = (short) MathUtils.randomWithin(0, musicFiles.length - 1);
                } while (previousMusicFile == currentMusicFile);
                if (assetManager.isLoaded(musicFiles[previousMusicFile])) {
                    assetManager.unload(musicFiles[previousMusicFile]); // free the resources of the previous music file
                }
                if (music != null) {
                    //music.dispose();
                    music = null;
                }
                music = loadMusicAsset(musicFiles[currentMusicFile]);
            } else {
                // resume previously paused music file
            }

            // play the selected music file
            music.play();
            music.setOnCompletionListener(new Music.OnCompletionListener() {
                @Override
                public void onCompletion(Music music) {
                    playMusic(true);
                }
            });
        }
    }

    private static Music loadMusicAsset(String path) {
        assetManager.load(path, Music.class);
        assetManager.finishLoading();
        Gdx.app.log(MediaManager.class.getName(), "Loaded '" + path + "'");
        return assetManager.get(path);
    }

    public static void pauseMusic() {
        if (music != null) {
            music.pause();
        }
    }

    public static void stopMusic() {
        if (music != null) {
            music.stop();
        }
    }

    public static Sound playSoundRandomPitch(String assetPath) {
        return playSound(assetPath, 0.5f + MathUtils.randomWithin(0f, 1.5f), false, 1f);
    }

    public static Sound playSound(String assetPath, boolean looping) {
        return playSound(assetPath, 1f, looping, 1f);
    }

    public static Sound playSound(String assetPath) {
        return playSound(assetPath, 1f, false, 1f);
    }

    public static Sound playSound(String assetPath, float pitch, boolean looping, float volume) {
        if (!assetManager.isLoaded(assetPath)) {
            assetManager.load(assetPath, Sound.class);
            assetManager.finishLoading();
        }
        Sound sound = assetManager.get(assetPath, Sound.class);
        long soundId = sound.play();
        sound.setLooping(soundId, looping);
        sound.setPitch(soundId, pitch);
        sound.setVolume(soundId, volume);
        return sound;
    }

    public static Music playMusic(String assetPath, boolean looping) {
        if (!assetManager.isLoaded(assetPath)) {
            assetManager.load(assetPath, Music.class);
            assetManager.finishLoading();
        }
        music = assetManager.get(assetPath, Music.class);
        music.setLooping(looping);
        music.play();
        return music;
    }
}
