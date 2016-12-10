package com.speanegames.fairybattles.entities.hero;

import com.speanegames.fairybattles.entities.bullet.Bullet;
import com.speanegames.fairybattles.entities.bullet.BulletFactory;
import com.speanegames.fairybattles.entities.moving.GameEntity;
import com.speanegames.fairybattles.entities.moving.Shooting;

import java.util.ArrayList;
import java.util.List;

public class Hero extends GameEntity implements Shooting {

    private String name;

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

        return bullet;
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
}
