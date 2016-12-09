package com.speanegames.fairybattles.entities;

import com.speanegames.fairybattles.rendering.TextureManager;

public class HeroFactory {

    private TextureManager textureManager;

    public HeroFactory(TextureManager textureManager) {
        this.textureManager = textureManager;
    }

    public Hero createHero(String name, float x, float y, float rotation) {
        Hero hero = new Hero();
        hero.setPosition(100, 100);
        hero.setSize(300, 300);
        hero.setRotation(rotation);
        hero.setTexture(textureManager.getTexture("player_tank_body"));

        return hero;
    }
}
