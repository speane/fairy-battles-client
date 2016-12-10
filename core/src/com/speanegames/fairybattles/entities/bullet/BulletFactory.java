package com.speanegames.fairybattles.entities.bullet;

import com.speanegames.fairybattles.entities.hero.Hero;
import com.speanegames.fairybattles.rendering.TextureManager;

public class BulletFactory {

    private BulletInfo bulletInfo;

    private Hero hero;

    private TextureManager textureManager;

    public BulletFactory(BulletInfo bulletInfo, TextureManager textureManager, Hero hero) {
        this.bulletInfo = bulletInfo;
        this.textureManager = textureManager;
        this.hero = hero;
    }

    public BulletInfo getBulletInfo() {
        return bulletInfo;
    }

    public void setBulletInfo(BulletInfo bulletInfo) {
        this.bulletInfo = bulletInfo;
    }

    public Bullet create(float x, float y, float rotation) {
        Bullet bullet = new Bullet(hero);

        bullet.setPosition(x, y);
        bullet.setRotation(rotation);

        bullet.setMaxDistance(bulletInfo.getMaxDistance());
        bullet.setSize(bulletInfo.getWidth(), bulletInfo.getHeight());
        bullet.setMoveSpeed(bulletInfo.getMoveSpeed());
        bullet.setTexture(textureManager.getTexture(bulletInfo.getTextureName()));

        return bullet;
    }
}
