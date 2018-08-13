package com.mygdx.starter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.starter.Constants;
import com.mygdx.starter.MediaManager;
import com.mygdx.starter.MyGdxGame;
import com.mygdx.starter.utils.FontUtils;

import static com.mygdx.starter.Constants.WindowHeight;
import static com.mygdx.starter.Constants.WindowWidth;

public class EmotionalScreen extends AbstractScreen implements InputProcessor {

    private final Music music;
    private final BitmapFont font;
    private final Music rain;
    private final MyGdxGame myGdxGame;
    private final Sprite earth;
    private final Sprite over;
    private final Sprite waste;
    private float elapsedTime;
    private GlyphLayout layout;
    private String phrase = "";
    private String oldPhrase = "";
    private float fontAlpha = 1f;
    private float fadeSpeed = 0.0025f;
    private int sceneIndex;
    private boolean alreadyStartedAgain = false;

    public EmotionalScreen(MyGdxGame myGdxGame) {
        super(WindowWidth, Constants.WindowHeight);
        this.myGdxGame = myGdxGame;
        music = MediaManager.playMusic("audio/emotional.ogg", true);
        rain = MediaManager.playMusic("audio/rain.ogg", true);
        rain.setVolume(0.5f);
        Gdx.input.setInputProcessor(this);
        font = new BitmapFont(Gdx.files.internal("fonts/amiga4everpro2.fnt"));

        earth = new Sprite(new Texture("earth.png"));
        earth.setPosition(WindowWidth / 2 - earth.getWidth() / 2, WindowHeight / 2 - earth.getHeight() / 2 + 50);

        over = new Sprite(new Texture("over.png"));
        over.setPosition(WindowWidth / 2 - earth.getWidth() / 2, WindowHeight / 2 - earth.getHeight() / 2 + 50);

        waste = new Sprite(new Texture("waste.png"));
        waste.setPosition(WindowWidth / 2 - earth.getWidth() / 2, WindowHeight / 2 - earth.getHeight() / 2 + 50);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        update(delta);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        //System.out.println(elapsedTime);

        if (!alreadyStartedAgain) {
            if (elapsedTime < 9) {
                earth.draw(batch, fontAlpha);
            } else if (elapsedTime > 22.8 && elapsedTime < 32) {
                waste.draw(batch, fontAlpha);
            } else if (elapsedTime > 52.623 && elapsedTime < 62) {
                over.draw(batch, fontAlpha);
            }
        }

        font.setColor(1f, 1f, 1f, fontAlpha);
        font.draw(batch, phrase, WindowWidth / 2f - layout.width / 2f, WindowHeight / 2f - layout.height / 2f);

        batch.end();
    }

    private String[][] group1 = new String[][]{
            new String[]{"Blue", "Blue Planet", "Blue Planet Earth"},
            new String[]{},
            new String[]{"Runs", "Runs out", "Runs out of", "Runs out of Space"},
            new String[]{},
            new String[]{"Waste.", "Waste. Cities.", "Waste. Cities. Overpopulation."},
            new String[]{},
            new String[]{"To", "To solve", "To solve this..."},
            new String[]{},
            new String[]{"...they", "...they invented", "...they invented human", "...they invented human disembodiment."},
    };

    private String[][] group2 = new String[][]{
            new String[]{"The", "The body", "The body vanishes"},
            new String[]{},
            new String[]{"while", "while the", "while the soul", "while the soul lives."},
            new String[]{},
            new String[]{"Incredible", "Incredible technology", "Incredible technology indeed."},
            new String[]{},
            new String[]{"I'm", "I'm mouth", "I'm mouth-less"},
            new String[]{},
            new String[]{"and", "and I", "and I must", "and I must scream"},
    };

    private String[][] currGroup = group1;

    private void update(float delta) {
        elapsedTime += delta;

        oldPhrase = phrase;

        if (sceneIndex == 0) {
            if (!phrase.equals(currGroup[sceneIndex][0]) && elapsedTime <= 1.376) {
                phrase = currGroup[sceneIndex][0];
            } else if (!phrase.equals(currGroup[sceneIndex][1]) && elapsedTime > 1.376) {
                phrase = currGroup[sceneIndex][1];
            } else if (!phrase.equals(currGroup[sceneIndex][2]) && elapsedTime > 2.773) {
                phrase = currGroup[sceneIndex][2];
                sceneIndex++;
            }
        } else if (sceneIndex == 1) {
            fontAlpha -= fadeSpeed;
            if (elapsedTime > 10) {
                sceneIndex++;
                phrase = "";
                fontAlpha = 1f;
            }
        } else if (sceneIndex == 2) {
            if (!phrase.equals(currGroup[sceneIndex][0]) && elapsedTime > 10.8 && elapsedTime < 12.113) {
                phrase = currGroup[sceneIndex][0];
            } else if (!phrase.equals(currGroup[sceneIndex][1]) && elapsedTime > 12.113 && elapsedTime < 13.437) {
                phrase = currGroup[sceneIndex][1];
            } else if (!phrase.equals(currGroup[sceneIndex][2]) && elapsedTime > 13.437 && elapsedTime < 14.897) {
                phrase = currGroup[sceneIndex][2];
            } else if (!phrase.equals(currGroup[sceneIndex][3]) && elapsedTime > 14.897) {
                phrase = currGroup[sceneIndex][3];
                sceneIndex++;
            }
        } else if (sceneIndex == 3) {
            fontAlpha -= fadeSpeed;
            if (elapsedTime > 22) {
                sceneIndex++;
                phrase = "";
                fontAlpha = 1f;
            }
        } else if (sceneIndex == 4) {
            if (!phrase.equals(currGroup[sceneIndex][0]) && elapsedTime > 22.8 && elapsedTime < 24.24) {
                phrase = currGroup[sceneIndex][0];
            } else if (!phrase.equals(currGroup[sceneIndex][1]) && elapsedTime > 24.24 && elapsedTime < 25.655) {
                phrase = currGroup[sceneIndex][1];
            } else if (!phrase.equals(currGroup[sceneIndex][2]) && elapsedTime > 25.655) {
                phrase = currGroup[sceneIndex][2];
                sceneIndex++;
            }
        } else if (sceneIndex == 5) {
            fontAlpha -= fadeSpeed;
            if (elapsedTime > 36) {
                sceneIndex++;
                phrase = "";
                fontAlpha = 1f;
            }
        } else if (sceneIndex == 6) {
            if (!phrase.equals(currGroup[sceneIndex][0]) && elapsedTime > 36.653 && elapsedTime < 38.117) {
                phrase = currGroup[sceneIndex][0];
            } else if (!phrase.equals(currGroup[sceneIndex][1]) && elapsedTime > 38.117 && elapsedTime < 39.556) {
                phrase = currGroup[sceneIndex][1];
            } else if (!phrase.equals(currGroup[sceneIndex][2]) && elapsedTime > 39.556) {
                phrase = currGroup[sceneIndex][2];
                sceneIndex++;
            }
        } else if (sceneIndex == 7) {
            fontAlpha -= fadeSpeed;
            if (elapsedTime > 52) {
                sceneIndex++;
                phrase = "";
                fontAlpha = 1f;
            }
        } else if (sceneIndex == 8) {
            if (!phrase.equals(currGroup[sceneIndex][0]) && elapsedTime > 52.623 && elapsedTime < 53.54) {
                phrase = currGroup[sceneIndex][0];
            } else if (!phrase.equals(currGroup[sceneIndex][1]) && elapsedTime > 53.54 && elapsedTime < 54.552) {
                phrase = currGroup[sceneIndex][1];
            } else if (!phrase.equals(currGroup[sceneIndex][2]) && elapsedTime > 54.552 && elapsedTime < 56) {
                phrase = currGroup[sceneIndex][2];
            } else if (!phrase.equals(currGroup[sceneIndex][3]) && elapsedTime > 56) {
                phrase = currGroup[sceneIndex][3];
                sceneIndex++;
            }
        } else if (sceneIndex == 9) {
            fontAlpha -= fadeSpeed;
            if (alreadyStartedAgain) {
                elapsedTime = 0;
                sceneIndex++;
            } else {
                if (elapsedTime > 60 + 11.596) {
                    currGroup = group2;
                    if (!alreadyStartedAgain) {
                        alreadyStartedAgain = true;
                        startAgain();
                    }
                    phrase = "";
                    fontAlpha = 1f;
                }
            }
        } else if (sceneIndex == 10) {
            int waitTime = 15;
            fontAlpha -= fadeSpeed;

            music.setVolume(Math.max(0, 1f - elapsedTime / waitTime));
            rain.setVolume(Math.max(0, 0.5f - elapsedTime / waitTime));

            if (elapsedTime >= waitTime) {
                music.setVolume(0f);
                rain.setVolume(0f);
                //music.stop();
                //rain.stop();
                myGdxGame.showCreditsScreen();
            }
        }

        if (!oldPhrase.equals(phrase)) {
            layout = FontUtils.getLayout(font, phrase);
        }
    }

    private void startAgain() {
        phrase = "";
        fontAlpha = 1f;
        elapsedTime = 0;
        sceneIndex = 0;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public boolean keyDown(int keycode) {

        return false;
    }


    @Override
    public boolean keyUp(int keycode) {

        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
