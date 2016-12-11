package com.speanegames.fairybattles;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.speanegames.fairybattles.networking.NetworkManager;
import com.speanegames.fairybattles.rendering.TextureManager;
import com.speanegames.fairybattles.screens.*;

public class FairyBattlesGame extends Game {

	private AssetManager assetManager;
	private TextureManager textureManager;
	private NetworkManager networkManager;
	private boolean waitingResponse;
	private SignInScreen signInScreen;
	private ConnectToLobbyScreen connectToLobbyScreen;
    private ConnectedLobbyScreen connectedLobbyScreen;

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

	public TextureManager getTextureManager() {
		return textureManager;
	}

	public void setTextureManager(TextureManager textureManager) {
		this.textureManager = textureManager;
	}

	public NetworkManager getNetworkManager() {
		return networkManager;
	}

	public void setNetworkManager(NetworkManager networkManager) {
		this.networkManager = networkManager;
	}

	public boolean isWaitingResponse() {
		return waitingResponse;
	}

	public void setWaitingResponse(boolean waitingResponse) {
		this.waitingResponse = waitingResponse;
	}

	public void setSignInMessage(String message) {
		signInScreen.setSignInErrorMessage(message);
	}

	public void setConnectToLobbyMessage(String message) {

	}

	public void showConnectToLobbyScreen() {
		ConnectToLobbyScreen connectToLobbyScreen = new ConnectToLobbyScreen(this);
		this.connectToLobbyScreen = connectToLobbyScreen;
		setScreen(connectToLobbyScreen);
	}

	public void showMainMenuScreen() {
		MainMenuScreen mainMenuScreen = new MainMenuScreen(this);
		setScreen(mainMenuScreen);
	}

	public void showConnectedLobbyScreen() {
        ConnectedLobbyScreen connectedLobbyScreen = new ConnectedLobbyScreen(this);
        this.connectedLobbyScreen = connectedLobbyScreen;
        setScreen(connectedLobbyScreen);
	}

    public void showSignInScreen() {
        SignInScreen signInScreen = new SignInScreen(this);
        this.signInScreen = signInScreen;
        setScreen(signInScreen);
    }
}
