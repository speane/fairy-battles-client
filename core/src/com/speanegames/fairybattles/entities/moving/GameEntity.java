package com.speanegames.fairybattles.entities.moving;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.speanegames.fairybattles.rendering.Drawable;

public class GameEntity implements Movable, Rotatable, Drawable {

    private Vector2 position;

    private float moveSpeed = 3;
    private float rotation;
    private float width;
    private float height;
    private TextureRegion texture;

    public GameEntity() {
        position = new Vector2();
    }

    @Override
    public TextureRegion getTexture() {
        return texture;
    }

    public void setTexture(TextureRegion texture) {
        this.texture = texture;
    }

    @Override
    public void move(Direction direction) {
        position.add(getMoveVector(direction));
    }

    @Override
    public void rotate(Direction direction) {

    }

    @Override
    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public void setX(float x) {
        position.x = x;
    }

    public void setY(float y) {
        position.y = y;
    }

    public void setPosition(float x, float y) {
        position.x = x;
        position.y = y;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public Rectangle getCollisionModel() {
        return new Rectangle(getX() , getY(), getWidth(), getHeight());
    }

    private Vector2 getMoveVector(Direction direction) {
        float deltaX = 0;
        float deltaY = 0;

        switch (direction) {
            case FORWARD:
                deltaX = -moveSpeed * MathUtils.sinDeg(rotation);
                deltaY = moveSpeed * MathUtils.cosDeg(rotation);
                break;
            case BACKWARD:
                deltaX = moveSpeed * MathUtils.sinDeg(rotation);
                deltaY = -moveSpeed * MathUtils.cosDeg(rotation);
                break;
            case LEFT:
                deltaX = -moveSpeed * MathUtils.cosDeg(rotation);
                deltaY = -moveSpeed * MathUtils.sinDeg(rotation);
                break;
            case RIGHT:
                deltaX = moveSpeed * MathUtils.cosDeg(rotation);
                deltaY = moveSpeed * MathUtils.sinDeg(rotation);
                break;
            default:
                break;
        }

        return new Vector2(deltaX, deltaY);
    }
}
