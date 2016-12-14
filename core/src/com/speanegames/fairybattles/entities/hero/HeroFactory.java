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
        hero.setMaxHealth(heroInfo.getMaxHealth());
        hero.setCurrentHealth(hero.getMaxHealth());
        hero.setDamage(heroInfo.getDamage());
        hero.setReloadTime(heroInfo.getReloadTime());
        hero.setLoadTime(heroInfo.getReloadTime());
        hero.setRespawnTime(heroInfo.getRespawnTime());
        hero.setTimeAfterDeath(heroInfo.getRespawnTime());

        hero.setBulletFactory(
                new BulletFactory(heroInfo.getBulletInfo(), textureManager, hero));

        return hero;
    }

    private void initHeroInfoHashMap() {
        heroInfoMap = new HashMap<String, HeroInfo>();

        HeroInfo waterHeroInfo = new HeroInfo();
        waterHeroInfo.setName("WATER");
        waterHeroInfo.setWidth(64);
        waterHeroInfo.setHeight(64);
        waterHeroInfo.setDamage(100);
        waterHeroInfo.setMaxHealth(200);
        waterHeroInfo.setMoveSpeed(3);
        waterHeroInfo.setTextureName("water_hero");
        waterHeroInfo.setReloadTime(1000f);
        waterHeroInfo.setRespawnTime(2000f);

        BulletInfo waterBulletInfo = new BulletInfo();
        waterBulletInfo.setWidth(16);
        waterBulletInfo.setHeight(16);
        waterBulletInfo.setMoveSpeed(5);
        waterBulletInfo.setMaxDistance(300);
        waterBulletInfo.setTextureName("water_hero_bullet");

        waterHeroInfo.setBulletInfo(waterBulletInfo);

        heroInfoMap.put(waterHeroInfo.getName(), waterHeroInfo);

        HeroInfo fireHero = new HeroInfo();
        fireHero.setName("FIRE");
        fireHero.setWidth(64);
        fireHero.setHeight(64);
        fireHero.setDamage(100);
        fireHero.setMaxHealth(200);
        fireHero.setMoveSpeed(3);
        fireHero.setTextureName("fire_hero");
        fireHero.setReloadTime(1000f);
        fireHero.setRespawnTime(2000f);

        BulletInfo fireBulletInfo = new BulletInfo();
        fireBulletInfo.setWidth(16);
        fireBulletInfo.setHeight(16);
        fireBulletInfo.setMoveSpeed(5);
        fireBulletInfo.setMaxDistance(300);
        fireBulletInfo.setTextureName("fire_hero_bullet");

        fireHero.setBulletInfo(fireBulletInfo);

        heroInfoMap.put(fireHero.getName(), fireHero);
    }
}
