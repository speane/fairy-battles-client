package com.speanegames.fairybattles.entities.fortress;

import com.speanegames.fairybattles.entities.moving.GameEntity;

public class Fortress extends GameEntity {

    private int maxHealth;
    private int currentHealth;

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }
}
