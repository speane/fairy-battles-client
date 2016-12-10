package com.speanegames.fairybattles.entities.hero;

import com.speanegames.fairybattles.entities.bullet.BulletFactory;
import com.speanegames.fairybattles.entities.bullet.BulletInfo;
import com.speanegames.fairybattles.rendering.TextureManager;

import java.util.HashMap;

public class HeroFactory {

    private TextureManager textureManager;

    private HashMap<String, HeroInfo> heroInfoMap;

    public HeroFactory(TextureManager textureManager) {
        this.textureManager = textureManager;
        initHeroInfoHashMap();
    }

    public Hero createHero(String name, float x, float y, float rotation) {
        HeroInfo heroInfo = heroInfoMap.get(name);

        Hero hero = new Hero();

        hero.setName(name);
        hero.setPosition(x - heroInfo.getWidth() / 2, y - heroInfo.getHeight() / 2);
        hero.setRotation(rotation);

        hero.setSize(heroInfo.getWidth(), heroInfo.getHeight());
        hero.setMoveSpeed(heroInfo.getMoveSpeed());
        hero.setTexture(textureManager.getTexture(heroInfo.getTextureName()));

        hero.setBulletFactory(
                new BulletFactory(heroInfo.getBulletInfo(), textureManager));

        return hero;
    }

    private void initHeroInfoHashMap() {
        heroInfoMap = new HashMap<String, HeroInfo>();

        HeroInfo waterHeroInfo = new HeroInfo();
        waterHeroInfo.setName("WATER");
        waterHeroInfo.setWidth(64);
        waterHeroInfo.setHeight(64);
        waterHeroInfo.setMoveSpeed(3);
        waterHeroInfo.setTextureName("water_hero");

        BulletInfo bulletInfo = new BulletInfo();
        bulletInfo.setWidth(16);
        bulletInfo.setHeight(16);
        bulletInfo.setMoveSpeed(5);
        bulletInfo.setMaxDistance(300);
        bulletInfo.setTextureName("water_hero_bullet");

        waterHeroInfo.setBulletInfo(bulletInfo);

        heroInfoMap.put(waterHeroInfo.getName(), waterHeroInfo);
    }
}
