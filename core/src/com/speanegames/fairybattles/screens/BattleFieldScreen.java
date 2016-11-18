package com.speanegames.fairybattles.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.speanegames.fairybattles.FairyBattlesGame;
import com.speanegames.fairybattles.entities.Hero;
import com.speanegames.fairybattles.rendering.Renderer;

public class BattleFieldScreen extends ScreenAdapter {

    private final FairyBattlesGame game;
    private final Renderer renderer;

    private Hero hero;

    public BattleFieldScreen(FairyBattlesGame game) {
        this.game = game;
        this.renderer = new Renderer();
        this.hero = new Hero();
    }

    @Override
    public void render(float delta) {
        renderer.renderWorld();

    }
}
