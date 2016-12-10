package com.speanegames.fairybattles.entities.hero;

import com.speanegames.fairybattles.entities.bullet.BulletInfo;

public class HeroInfo {

    private String name;
    private String textureName;
    private Integer moveSpeed;
    private Integer width;
    private Integer height;
    private BulletInfo bulletInfo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTextureName() {
        return textureName;
    }

    public void setTextureName(String textureName) {
        this.textureName = textureName;
    }

    public Integer getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(Integer moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public BulletInfo getBulletInfo() {
        return bulletInfo;
    }

    public void setBulletInfo(BulletInfo bulletInfo) {
        this.bulletInfo = bulletInfo;
    }
}
