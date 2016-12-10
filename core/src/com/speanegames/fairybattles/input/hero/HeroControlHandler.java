package com.speanegames.fairybattles.input.hero;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.speanegames.fairybattles.config.AppConfig;
import com.speanegames.fairybattles.entities.Hero;
import com.speanegames.fairybattles.entities.moving.Direction;

public class HeroControlHandler {

    private Hero hero;

    public HeroControlHandler(Hero hero) {
        this.hero = hero;
    }

    public void handleInput() {
        final int MOUSE_COLLISION_RADIUS = 30;

        boolean aKeyPressed = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean wKeyPressed = Gdx.input.isKeyPressed(Input.Keys.W);
        boolean sKeyPressed = Gdx.input.isKeyPressed(Input.Keys.S);
        boolean dKeyPressed = Gdx.input.isKeyPressed(Input.Keys.D);

        int mouseX = Gdx.input.getX();
        int mouseY = AppConfig.SCREEN_HEIGHT - Gdx.input.getY();
        int heroX = (int) (hero.getX() + hero.getWidth() / 2);
        int heroY = (int) (hero.getY() + hero.getHeight() / 2);
        int deltaX = mouseX - heroX;
        int deltaY = mouseY - heroY;
        float rotation = MathUtils.atan2(deltaY, deltaX) * 180.0f
                / MathUtils.PI - 90;

        hero.setRotation(rotation);

        if (aKeyPressed) {
            hero.move(Direction.LEFT);
        }

        if (dKeyPressed) {
            hero.move(Direction.RIGHT);
        }

        if (!((Math.abs(mouseX - heroX) < MOUSE_COLLISION_RADIUS) &&
                (Math.abs(mouseY - heroY) < MOUSE_COLLISION_RADIUS))) {

            if (wKeyPressed) {
                hero.move(Direction.FORWARD);
            }

            if (sKeyPressed) {
                hero.move(Direction.BACKWARD);
            }

        }
    }
}
