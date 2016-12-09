package com.speanegames.fairybattles;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.speanegames.fairybattles.config.AssetConfig;
import com.speanegames.fairybattles.rendering.TextureManager;
import com.speanegames.fairybattles.rendering.TextureManagerImpl;
import com.speanegames.fairybattles.screens.BattleFieldScreen;

public class FairyBattlesGame extends Game {

	private AssetManager assetManager;

	@Override
	public void create() {
		assetManager = new AssetManager();
		assetManager.load(AssetConfig.TEXTURE_ATLAS_PATH, TextureAtlas.class);

		while (!assetManager.update()) {
			assetManager.getProgress();
		}

		TextureAtlas atlas =
				assetManager.get(AssetConfig.TEXTURE_ATLAS_PATH);
		TextureManager textureManager = new TextureManagerImpl(atlas);

		setScreen(new BattleFieldScreen(this, textureManager));
	}

	public AssetManager getAssetManager() {
		return assetManager;
	}
}
