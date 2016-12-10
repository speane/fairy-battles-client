package com.speanegames.fairybattles.entities.bullet;

import com.speanegames.fairybattles.entities.hero.Hero;
import com.speanegames.fairybattles.entities.moving.Direction;
import com.speanegames.fairybattles.entities.moving.GameEntity;

public class Bullet extends GameEntity {

    private int maxDistance;
    private int currentDistance;

    private Hero hero;

    public Bullet(Hero hero) {
        super();
        this.hero = hero;
    }

    public void move() {
        super.move(Direction.FORWARD);
        currentDistance += getMoveSpeed();
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    public boolean isFinished() {
        return currentDistance >= maxDistance;
    }

    public int getCurrentDistance() {
        return currentDistance;
    }

    public void setCurrentDistance(int currentDistance) {
        this.currentDistance = currentDistance;
    }

    public Hero getHero() {
        return hero;
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }
}
