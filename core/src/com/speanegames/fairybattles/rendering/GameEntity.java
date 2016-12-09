package com.speanegames.fairybattles.rendering;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.speanegames.fairybattles.entities.moving.Direction;
import com.speanegames.fairybattles.entities.moving.Movable;
import com.speanegames.fairybattles.entities.moving.Rotatable;

public class GameEntity implements Movable, Rotatable, Drawable {

    private Vector2 position;
    private Vector2 moveVector;
    private float rotationSpeed;
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
        switch (direction) {
            case FORWARD:
                position.add(moveVector);
                break;
            case BACKWARD:
                position.sub(moveVector);
                break;
            default:
                break;
        }
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
}
