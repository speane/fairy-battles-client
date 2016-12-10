package com.speanegames.fairybattles.input.hero;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.speanegames.fairybattles.entities.hero.Hero;

public class HeroControlHandler {

    private Hero hero;
    private OrthographicCamera camera;

    public HeroControlHandler(Hero hero, OrthographicCamera camera) {
        this.hero = hero;
        this.camera = camera;
    }

    /*public void handleInput() {
        final int MOUSE_COLLISION_RADIUS = 30;

        boolean aKeyPressed = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean wKeyPressed = Gdx.input.isKeyPressed(Input.Keys.W);
        boolean sKeyPressed = Gdx.input.isKeyPressed(Input.Keys.S);
        boolean dKeyPressed = Gdx.input.isKeyPressed(Input.Keys.D);

        Vector3 mouseVector = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouseVector);

        int mouseX = (int) mouseVector.x;
        int mouseY = (int) mouseVector.y;
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
    }*/
}
