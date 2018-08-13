package com.mygdx.starter.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.starter.Constants;
import com.mygdx.starter.AbstractCallback;
import com.mygdx.starter.MediaManager;
import com.mygdx.starter.MyShapeRenderer;
import com.mygdx.starter.screens.GameScreen;
import com.mygdx.starter.utils.MathUtils;

import static com.mygdx.starter.Constants.NumPixelsKeyPress;
import static com.mygdx.starter.model.Minion.State.Dead;
import static com.mygdx.starter.model.Minion.State.Frozen;
import static com.mygdx.starter.model.Minion.State.Idle;
import static com.mygdx.starter.model.Minion.State.Jumping;
import static com.mygdx.starter.model.Minion.State.Moving;
import static com.mygdx.starter.model.Minion.State.Spawning;

public class Minion {

    private final float targetWalkingSpeed;
    // config
    public float frameDuration = MathUtils.randomWithin(0.15f, 0.25f); // how fast the animation should be
    public Array<Key> keySequence;

    // animation
    private Animation[] animations;

    public float x, y;
    private State state;
    private float elapsedTime;
    public Key key;
    private float spriteSize = 32;
    private float initialSpriteSize = 32;
    private boolean standsOnPressedKey;
    public float walkingSpeed = MathUtils.randomWithin(0.1f, 0.2f);
    public float jumpingSpeed = MathUtils.randomWithin(2f, 3f);

    // temp vars needed for jumping animation
    private double deltaX;
    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private boolean hasReachedGoal;
    public int keyIndex = 0;
    public AbstractCallback preJumpCallback;
    public AbstractCallback postJumpCallback;
    private State stateBeforeFrozen;
    private boolean isInflating;

    public float getCenterX() {
        return x + spriteSize / 2;
    }

    public float getCenterY() {
        return y + spriteSize / 2;
    }

    public void freeze() {
        stateBeforeFrozen = state;
        state = Frozen;
    }

    public Key getNextKey() {
        if (keyIndex + 1 > keySequence.size - 1) {
            return null;
        }
        return keySequence.get(keyIndex + 1);
    }

    public void unfreeze() {
        if (!state.equals(Dead)) {
            state = stateBeforeFrozen;
        }
    }

    public void kill() {
        state = Dead;
        isInflating = false;
        spriteSize = initialSpriteSize;
    }

    public void inflate() {
        this.isInflating = true;
    }

    public boolean isNotDead() {
        return !state.equals(Dead);
    }

    public enum State {
        Spawning, Moving, Jumping, Idle, Frozen, Dead
    }

    public Constants.Directions horizontalMovingDirection;
    public Constants.Directions verticalMovingDirection;

    public Minion(Array<Key> keySequence) {
        this.keySequence = keySequence;
        state = State.Spawning;
        horizontalMovingDirection = MathUtils.randomWithin(1, 10) > 5 ? Constants.Directions.West : Constants.Directions.East;
        verticalMovingDirection = MathUtils.randomWithin(1, 10) > 5 ? Constants.Directions.North : Constants.Directions.South;
        targetWalkingSpeed = walkingSpeed;
        walkingSpeed *= 10;
    }

    public void loadAnimations() {
        this.animations = new Animation[State.values().length];
        int i = 0;
        for (State type : State.values()) {
            String id = "minion_" + type.name().toLowerCase();
            animations[i++] = new Animation(frameDuration, MediaManager.atlas.findRegions(id), Animation.PlayMode.LOOP);
        }
    }

    public void update(float delta) {
        elapsedTime += delta;

        // handle y-change when the user presses the key that the minion currently stands on
        if (key != null && key.isPressed && !standsOnPressedKey) {
            standsOnPressedKey = true;
            y -= NumPixelsKeyPress;
        }
        if (key != null && !key.isPressed && standsOnPressedKey) {
            standsOnPressedKey = false;
            y += NumPixelsKeyPress;

            if (!state.equals(Frozen)) {
                jumpToNextKey();
            }
        }

        if (isInflating) {
            spriteSize += 0.3f;
        }

        switch (state) {
            case Jumping:
                x += deltaX;
                y = MathUtils.interpolate(x, startX, endX, startY, endY);
                hasReachedGoal = (horizontalMovingDirection.equals(Constants.Directions.East) && x >= endX)
                        || (horizontalMovingDirection.equals(Constants.Directions.West) && x <= endX);
                if (hasReachedGoal) {
                    key = keySequence.get(++keyIndex);
                    state = Idle;
                    if (postJumpCallback != null) {
                        postJumpCallback.call(this);
                    }
                }
                break;
            case Spawning:
            case Moving:
                if (isInflating)
                    break;
                walkingSpeed -= 0.04f;
                if (walkingSpeed < targetWalkingSpeed) {
                    walkingSpeed = targetWalkingSpeed;
                }

                x += walkingSpeed * (horizontalMovingDirection.equals(Constants.Directions.West) ? -1 : 1);
                y += walkingSpeed * (verticalMovingDirection.equals(Constants.Directions.South) ? -1 : 1);

                // bounce of the current key the minion is standing on
                if (key != null) {

                    // horizontal movement
                    if (horizontalMovingDirection.equals(Constants.Directions.West) && x < key.x) {
                        x = key.x;
                        horizontalMovingDirection = Constants.Directions.East;
                    } else if (horizontalMovingDirection.equals(Constants.Directions.East) && x > key.x + key.width - spriteSize) {
                        x = key.x + key.width - spriteSize;
                        horizontalMovingDirection = Constants.Directions.West;
                    }

                    // vertical movement
                    float bottomBorder = key.y + (key.isPressed ? 0 : NumPixelsKeyPress);
                    float topBorder = key.y + key.height - 10 + (key.isPressed ? 0 : NumPixelsKeyPress);
                    if (verticalMovingDirection.equals(Constants.Directions.South) && y < bottomBorder) {
                        y = bottomBorder;
                        verticalMovingDirection = Constants.Directions.North;
                    } else if (verticalMovingDirection.equals(Constants.Directions.North) && y > topBorder) {
                        y = topBorder;
                        verticalMovingDirection = Constants.Directions.South;
                    }
                }
                break;
            default:
                break;
        }
    }

    private void jumpToNextKey() {
        if (keyIndex + 1 < keySequence.size) {
            if (preJumpCallback != null) {
                preJumpCallback.call(this);
            }

            state = Jumping;
            key = null;

            startX = x;
            startY = y;
            endX = keySequence.get(keyIndex + 1).x;
            endY = keySequence.get(keyIndex + 1).getCenterY();
            float totalDeltaX = endX - startX;
            float totalDeltaY = endY - startY;
            double totalDistance = Math.sqrt(totalDeltaX * totalDeltaX + totalDeltaY * totalDeltaY);
            deltaX = jumpingSpeed / totalDistance * totalDeltaX;
            horizontalMovingDirection = Math.signum(endX - startX) > 0 ? Constants.Directions.East : Constants.Directions.West;
        } else {
            System.out.println("Already reached last key of sequence");
        }
    }

    public void render(MyShapeRenderer sr) {

        // shadow
        sr.setColor(0.2f, 0.2f, 0.2f, 0.5f);
        sr.ellipse(x, y, spriteSize - 3, 5);

        if (hasKeysLeftToVisit()) {
            Key nextKey = keySequence.get(keyIndex + 1);
            if (nextKey != null) {
                if (GameScreen.state.equals(GameScreen.State.CheerfulGame)) {
                    sr.line(getCenterX(), getCenterY(), nextKey.getCenterX(), nextKey.getCenterY(), Color.BROWN, Color.GOLD);
                } else if (GameScreen.state.equals(GameScreen.State.AbalInterceptsSpikes)) {
                    sr.line(getCenterX(), getCenterY(), nextKey.getCenterX(), nextKey.getCenterY(), Color.RED, Color.DARK_GRAY);
                }
            }
        }
    }

    private boolean hasKeysLeftToVisit() {
        return keyIndex < keySequence.size - 1;
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame;
        currentFrame = getCurrentFrameFor(state, true);
        if (currentFrame != null) {
            if (isInflating) {
                batch.draw(currentFrame, x - (spriteSize - initialSpriteSize) / 2, y - (spriteSize - initialSpriteSize) / 2, spriteSize, spriteSize);
            } else {
                batch.draw(currentFrame, x, y);
            }
        }
    }

    private TextureRegion getCurrentFrameFor(State state, boolean loop) {
        // use same animation for moving and idle states for now
        if (state.equals(Idle) || state.equals(Spawning)) {
            state = Moving;
        }
        return (TextureRegion) animations[state.ordinal()].getKeyFrame(elapsedTime, loop);
    }

    public Key destinationKey() {
        return keySequence.get(keySequence.size - 1);
    }
}

