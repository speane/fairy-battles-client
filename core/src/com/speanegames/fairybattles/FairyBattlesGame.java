package com.speanegames.fairybattles;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.speanegames.fairybattles.entities.player.Player;
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
	private LobbyScreen lobbyScreen;
    private MainMenuScreen mainMenuScreen;

    private Player player;

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

    public void setCreateLobbyMessage(String message) {
        mainMenuScreen.setStatusMessage(message);
    }

	public void showConnectToLobbyScreen() {
        ConnectToLobbyScreen connectToLobbyScreen = new ConnectToLobbyScreen(this);
        this.connectToLobbyScreen = connectToLobbyScreen;
        setScreen(connectToLobbyScreen);
	}

    public void connectToLobby(String lobbyId) {
        LobbyScreen lobbyScreen = new LobbyScreen(this, lobbyId, false);
        this.lobbyScreen = lobbyScreen;
        setScreen(lobbyScreen);
    }

	public void showMainMenuScreen() {
		MainMenuScreen mainMenuScreen = new MainMenuScreen(this);
		setScreen(mainMenuScreen);
	}

    public void showSignInScreen() {
        SignInScreen signInScreen = new SignInScreen(this);
        this.signInScreen = signInScreen;
        setScreen(signInScreen);
    }

	public void showLobbyOwnerScreen(String lobbyId) {
		LobbyScreen lobbyScreen = new LobbyScreen(this, lobbyId, true);
		this.lobbyScreen = lobbyScreen;
		setScreen(lobbyScreen);
	}

    public void joinTeam(String team, int position) {
        lobbyScreen.joinTeam(team, position);
    }

    public void lobbyDissolved() {
        showMainMenuScreen();
    }

    public void leaveLobbyRequest() {
        networkManager.leaveLobbyRequest();
    }

    public void leaveLobby() {
        showMainMenuScreen();
    }

    public void playerJoinedTeam(String login, String team, int position) {
        Player player = new Player();
        player.setLogin(login);
        lobbyScreen.playerJoinedTeam(player, team, position);
    }

    public void cleanLobbySlot(String team, int position) {
        lobbyScreen.cleanLobbySlot(team, position);
    }

    public void signIn(String login) {
        Player player = new Player();
        player.setLogin(login);
        this.player = player;
        showMainMenuScreen();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void startBattle(String team, int position) {
        BattleFieldScreen screen = new BattleFieldScreen(this, team, position);
        setScreen(screen);
    }
}
