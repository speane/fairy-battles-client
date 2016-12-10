package com.speanegames.fairybattles.entities.bullet;

import com.speanegames.fairybattles.entities.moving.Direction;
import com.speanegames.fairybattles.entities.moving.GameEntity;

public class Bullet extends GameEntity {

    private int maxDistance;
    private int currentDistance;

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
}
