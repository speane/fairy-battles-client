package com.speanegames.fairybattles.entities.hero;

import com.speanegames.fairybattles.entities.bullet.Bullet;
import com.speanegames.fairybattles.entities.bullet.BulletFactory;
import com.speanegames.fairybattles.entities.moving.GameEntity;
import com.speanegames.fairybattles.entities.moving.Shooting;

import java.util.ArrayList;
import java.util.List;

public class Hero extends GameEntity implements Shooting {

    private String name;
    private int damage;
    private int maxHealth;
    private int currentHealth;

    private boolean killed;

    private float respawnTime;
    private float timeAfterDeath;

    private float reloadTime;
    private float loadTime;

    private BulletFactory bulletFactory;

    private List<Bullet> bullets;

    public Hero() {
        bullets = new ArrayList<Bullet>();
    }

    @Override
    public Bullet shoot() {
        Bullet bullet = bulletFactory.create(
                getX() + getWidth() / 2
                - bulletFactory.getBulletInfo().getWidth() / 2,
                getY() + getHeight() / 2
                - bulletFactory.getBulletInfo().getHeight() / 2,
                getRotation());

        bullets.add(bullet);

        reload();

        return bullet;
    }

    public boolean isAlive() {
        return currentHealth > 0;
    }

    public void kill() {
        killed = true;
        timeAfterDeath = 0;
    }

    public boolean isRespawned() {
        return timeAfterDeath >= respawnTime;
    }

    public float getRespawnTime() {
        return respawnTime;
    }

    public void setRespawnTime(float respawnTime) {
        this.respawnTime = respawnTime;
    }

    public float getTimeAfterDeath() {
        return timeAfterDeath;
    }

    public void setTimeAfterDeath(float timeAfterDeath) {
        this.timeAfterDeath = timeAfterDeath;
    }

    public void reload() {
        this.loadTime = 0;
    }

    @Override
    public boolean isLoaded() {
        return loadTime >= reloadTime;
    }

    public void setLoadTime(float loadTime) {
        this.loadTime = loadTime;
    }

    public float getLoadTime() {
        return loadTime;
    }

    public void addLoadTime(float delta) {
        loadTime += delta;
    }

    public BulletFactory getBulletFactory() {
        return bulletFactory;
    }

    public void setBulletFactory(BulletFactory bulletFactory) {
        this.bulletFactory = bulletFactory;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public void setBullets(List<Bullet> bullets) {
        this.bullets = bullets;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

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

    public float getReloadTime() {
        return reloadTime;
    }

    public void setReloadTime(float reloadTime) {
        this.reloadTime = reloadTime;
    }

    public boolean isKilled() {
        return killed;
    }

    public void setKilled(boolean killed) {
        this.killed = killed;
    }
}
