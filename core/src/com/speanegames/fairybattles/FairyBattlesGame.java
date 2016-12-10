package com.speanegames.fairybattles;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.speanegames.fairybattles.screens.AppLoadingScreen;

public class FairyBattlesGame extends Game {

	private AssetManager assetManager;

	@Override
	public void create() {
        initAssetManager();
        setScreen(new AppLoadingScreen(this));
	}

	public AssetManager getAssetManager() {
		return assetManager;
	}

    private void initAssetManager() {
        assetManager = new AssetManager();
        assetManager.setLoader(TiledMap.class,
                new TmxMapLoader(new InternalFileHandleResolver()));
    }
}
