package com.speanegames.fairybattles.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.speanegames.fairybattles.FairyBattlesGame;
import com.speanegames.fairybattles.config.AppConfig;
import com.speanegames.fairybattles.config.AssetConfig;
import com.speanegames.fairybattles.config.UIConfig;
import com.speanegames.fairybattles.entities.player.Player;
import com.speanegames.fairybattles.networking.NetworkManager;
import com.speanegames.fairybattles.rendering.TextureManager;

public class LobbyScreen extends ScreenAdapter {

    private String lobbyId;
    private boolean isOwner;
    private String team;
    private int position;

    private FairyBattlesGame game;
    private TextureManager textureManager;
    private NetworkManager networkManager;

    private Skin skin;
    private Stage stage;

    private Player player;

    private Player[] moonPlayers;
    private Player[] sunPlayers;

    private Label[] moonTeamLabels;
    private Label[] sunTeamLabels;

    public LobbyScreen(FairyBattlesGame game, String lobbyId, boolean isOwner) {
        this.player = game.getPlayer();
        this.lobbyId = lobbyId;
        this.isOwner = isOwner;
        this.game = game;
        this.textureManager = game.getTextureManager();
        this.networkManager = game.getNetworkManager();
        moonPlayers = new Player[AppConfig.MAX_TEAM_PLAYERS_AMOUNT];
        sunPlayers = new Player[AppConfig.MAX_TEAM_PLAYERS_AMOUNT];
    }

    @Override
    public void show() {
        skin = new Skin(Gdx.files.internal(AssetConfig.UI_SKIN_FILE_PATH));
        stage = new Stage(new FitViewport(AppConfig.SCREEN_WIDTH, AppConfig.SCREEN_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        initBackground();
        initUI();
        if (team != null) {
            joinTeam(team, position);
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        clearScreen();
        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void joinTeam(String team, int position) {
        this.team = team;
        this.position = position;
        if (team.equals("SUN")) {
            sunPlayers[position] = player;
            for (int i = 0; i < AppConfig.MAX_TEAM_PLAYERS_AMOUNT; i++) {
                sunTeamLabels[i].setColor(Color.GREEN);
                moonTeamLabels[i].setColor(Color.RED);
            }
            sunTeamLabels[position].setText(player.getLogin());
            sunTeamLabels[position].setColor(Color.BLUE);
        } else {
            moonPlayers[position] = player;
            for (int i = 0; i < AppConfig.MAX_TEAM_PLAYERS_AMOUNT; i++) {
                moonTeamLabels[i].setColor(Color.GREEN);
                sunTeamLabels[i].setColor(Color.RED);
            }
            moonTeamLabels[position].setText(player.getLogin());
            moonTeamLabels[position].setColor(Color.BLUE);
        }
    }

    public void playerJoinedTeam(Player player, String team, int position) {
        if (team.equals("SUN")) {
            sunPlayers[position] = player;
            sunTeamLabels[position].setText(player.getLogin());
        } else {
            moonPlayers[position] = player;
            moonTeamLabels[position].setText(player.getLogin());
        }
    }

    public void cleanLobbySlot(String team, int position) {
        if (team.equals("SUN")) {
            sunTeamLabels[position].setText("[EMPTY SLOT]");
            sunPlayers[position] = null;
        } else {
            moonTeamLabels[position].setText("[EMPTY SLOT]");
            moonPlayers[position] = null;
        }
    }

    private void initBackground() {
        Image backgroundImage = new Image(textureManager.getTexture(AssetConfig.MENU_BACKGROUND_IMAGE_NAME));
        backgroundImage.setSize(AppConfig.SCREEN_WIDTH, AppConfig.SCREEN_HEIGHT);
        stage.addActor(backgroundImage);
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void initUI() {
        initTitleLabel();

        if (isOwner) {
            initDissolveButton();
            initStartButton();
        } else {
            initLeaveLobbyButton();
        }

        initLobbyIdLabel();
        initLoginLabel();
        initJoinSunTeamButton();
        initJoinMoonTeamButton();
        initTeamLabels();
    }

    private void initLobbyIdLabel() {
        Label lobbyIdLabel = new Label("Lobby ID: " + lobbyId, skin);
        lobbyIdLabel.setPosition(
                AppConfig.SCREEN_WIDTH / 2 - UIConfig.TEXT_FIELD_INDENT * 2,
                AppConfig.SCREEN_HEIGHT / 2 + UIConfig.TEXT_FIELD_INDENT * 2, Align.bottomLeft);
        lobbyIdLabel.setColor(Color.WHITE);
        stage.addActor(lobbyIdLabel);
    }

    private void initLoginLabel() {
        Label loginLabel = new Label("Player: " + game.getPlayer().getLogin(), skin);
        loginLabel.setPosition(
                UIConfig.TEXT_FIELD_INDENT / 2,
                AppConfig.SCREEN_HEIGHT - UIConfig.TEXT_FIELD_INDENT * 2, Align.bottomLeft
        );
        loginLabel.setColor(Color.WHITE);
        stage.addActor(loginLabel);
    }

    private void initTitleLabel() {
        Image labelImage = new Image(textureManager.getTexture("battle_lobby_label"));
        labelImage.setPosition(AppConfig.SCREEN_WIDTH / 2 - UIConfig.TITLE_LABEL_WIDTH / 2.3F,
                AppConfig.SCREEN_HEIGHT / 2 + UIConfig.TITLE_LABEL_HEIGHT / 3F, Align.bottomLeft);
        labelImage.setSize(UIConfig.TITLE_LABEL_WIDTH, UIConfig.TITLE_LABEL_HEIGHT);
        stage.addActor(labelImage);
    }

    private void initDissolveButton() {
        TextButton dissolveButton = new TextButton("Dissolve lobby", skin);

        dissolveButton.setSize(UIConfig.TEXT_FIELD_WIDTH, UIConfig.TEXT_FIELD_HEIGHT);
        dissolveButton.setPosition(AppConfig.SCREEN_WIDTH / 2,
                AppConfig.SCREEN_HEIGHT / 2 - UIConfig.TEXT_FIELD_INDENT * 3, Align.center);

        dissolveButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                networkManager.dissolveLobbyRequest();
            }
        });

        stage.addActor(dissolveButton);
    }

    private void initStartButton() {
        TextButton startButton = new TextButton("Start battle", skin);

        startButton.setSize(UIConfig.TEXT_FIELD_WIDTH, UIConfig.TEXT_FIELD_HEIGHT);
        startButton.setPosition(AppConfig.SCREEN_WIDTH / 2,
                AppConfig.SCREEN_HEIGHT / 2 - UIConfig.TEXT_FIELD_INDENT * 2, Align.center);

        startButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                if (team != null) {
                    networkManager.startBattleRequest();
                }
            }
        });

        stage.addActor(startButton);
    }

    private void initLeaveLobbyButton() {
        TextButton leaveLobbyButton = new TextButton("Leave lobby", skin);

        leaveLobbyButton.setSize(UIConfig.TEXT_FIELD_WIDTH, UIConfig.TEXT_FIELD_HEIGHT);
        leaveLobbyButton.setPosition(AppConfig.SCREEN_WIDTH / 2,
                AppConfig.SCREEN_HEIGHT / 2 - UIConfig.TEXT_FIELD_INDENT * 3, Align.center);

        leaveLobbyButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                game.leaveLobbyRequest();
            }
        });

        stage.addActor(leaveLobbyButton);
    }

    private void initJoinSunTeamButton() {
        TextButton joinSunTeamButton = new TextButton("Join SUN team", skin);

        joinSunTeamButton.setSize(UIConfig.TEXT_FIELD_WIDTH, UIConfig.TEXT_FIELD_HEIGHT);
        joinSunTeamButton.setPosition(UIConfig.TEXT_FIELD_INDENT * 3,
                AppConfig.SCREEN_HEIGHT / 2 - UIConfig.TEXT_FIELD_INDENT * 2.5f, Align.center);

        joinSunTeamButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                networkManager.joinTeamRequest("SUN");
            }
        });

        stage.addActor(joinSunTeamButton);
    }

    private void initJoinMoonTeamButton() {
        TextButton joinSunTeamButton = new TextButton("Join MOON team", skin);

        joinSunTeamButton.setSize(UIConfig.TEXT_FIELD_WIDTH, UIConfig.TEXT_FIELD_HEIGHT);
        joinSunTeamButton.setPosition(AppConfig.SCREEN_WIDTH / 2 + UIConfig.TEXT_FIELD_INDENT * 7,
                AppConfig.SCREEN_HEIGHT / 2 - UIConfig.TEXT_FIELD_INDENT * 2.5f, Align.center);

        joinSunTeamButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                networkManager.joinTeamRequest("MOON");
            }
        });

        stage.addActor(joinSunTeamButton);
    }

    private void initTeamLabels() {
        moonTeamLabels = new Label[AppConfig.MAX_TEAM_PLAYERS_AMOUNT];
        sunTeamLabels = new Label[AppConfig.MAX_TEAM_PLAYERS_AMOUNT];

        Color sunTeamColor;
        Color moonTeamColor;

        if (team != null) {
            if (team.equals("SUN")) {
                sunTeamColor = Color.GREEN;
                moonTeamColor = Color.RED;
            } else {
                sunTeamColor = Color.RED;
                moonTeamColor = Color.GREEN;
            }
        } else {
            sunTeamColor = moonTeamColor = Color.WHITE;
        }

        for (int i = 0; i < AppConfig.MAX_TEAM_PLAYERS_AMOUNT; i++) {

            if (sunPlayers[i] != null) {
                sunTeamLabels[i] = new Label(sunPlayers[i].getLogin(), skin);
            } else {
                sunTeamLabels[i] = new Label("[EMPTY SLOT]", skin);
            }

            sunTeamLabels[i].setColor(sunTeamColor);
            sunTeamLabels[i].setPosition(
                    UIConfig.TEXT_FIELD_INDENT * 2,
                    AppConfig.SCREEN_HEIGHT / 2
                            - i * UIConfig.TEXT_FIELD_INDENT / 2,
                    Align.bottomLeft);

            stage.addActor(sunTeamLabels[i]);

            if (moonPlayers[i] != null) {
                moonTeamLabels[i] = new Label(moonPlayers[i].getLogin(), skin);
            } else {
                moonTeamLabels[i] = new Label("[EMPTY SLOT]", skin);
            }

            moonTeamLabels[i].setColor(moonTeamColor);
            moonTeamLabels[i].setPosition(
                    AppConfig.SCREEN_WIDTH / 2 + UIConfig.TEXT_FIELD_INDENT * 6,
                    AppConfig.SCREEN_HEIGHT / 2
                            - i * UIConfig.TEXT_FIELD_INDENT / 2,
                    Align.bottomLeft);

            stage.addActor(moonTeamLabels[i]);
        }

        if (team != null) {
            if (team.equals("SUN")) {
                sunTeamLabels[position].setColor(Color.BLUE);
            } else {
                moonTeamLabels[position].setColor(Color.BLUE);
            }
        }
    }

    public Player[] getMoonPlayers() {
        return moonPlayers;
    }

    public void setMoonPlayers(Player[] moonPlayers) {
        this.moonPlayers = moonPlayers;
    }

    public Player[] getSunPlayers() {
        return sunPlayers;
    }

    public void setSunPlayers(Player[] sunPlayers) {
        this.sunPlayers = sunPlayers;
    }

    public boolean isLobbyOwner() {
        return isOwner;
    }
}
