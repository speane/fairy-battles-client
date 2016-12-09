package com.speanegames.fairybattles.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.speanegames.fairybattles.FairyBattlesGame;
import com.speanegames.fairybattles.entities.Hero;
import com.speanegames.fairybattles.entities.HeroFactory;
import com.speanegames.fairybattles.rendering.RendererImpl;
import com.speanegames.fairybattles.rendering.TextureManager;

public class BattleFieldScreen extends ScreenAdapter {

    private final FairyBattlesGame game;
    private final RendererImpl renderer;
    private final HeroFactory heroFactory;

    private Hero hero;

    public BattleFieldScreen(FairyBattlesGame game,
                             TextureManager textureManager) {

        this.game = game;
        renderer = new RendererImpl(new SpriteBatch());
        heroFactory = new HeroFactory(textureManager);

        initEntities();
    }

    @Override
    public void render(float delta) {
        renderer.renderAll();
    }

    private void initEntities() {
        hero = heroFactory.createHero("player_tank_body", 200, 200, 67);
        renderer.subscribe(hero);
    }
}
