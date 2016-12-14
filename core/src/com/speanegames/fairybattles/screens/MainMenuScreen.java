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
import com.speanegames.fairybattles.networking.NetworkManager;
import com.speanegames.fairybattles.rendering.TextureManager;

public class MainMenuScreen extends ScreenAdapter {

    private FairyBattlesGame game;
    private TextureManager textureManager;
    private NetworkManager networkManager;

    private Skin skin;
    private Stage stage;

    private Label statusLabel;

    public MainMenuScreen(FairyBattlesGame game) {
        this.game = game;
        this.textureManager = game.getTextureManager();
        this.networkManager = game.getNetworkManager();
    }

    @Override
    public void show() {
        skin = new Skin(Gdx.files.internal(AssetConfig.UI_SKIN_FILE_PATH));
        stage = new Stage(new FitViewport(AppConfig.SCREEN_WIDTH, AppConfig.SCREEN_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        initBackground();
        initUI();
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

    public void setStatusMessage(String message) {
        statusLabel.setText(message);
    }

    private void initUI() {
        initTitle();
        initStatusLabel();
        initLoginLabel();
        initConnectToLobbyButton();
        initCreateLobbyButton();
        initSignOutButton();
        initQuitButton();
    }

    private void initTitle() {
        Image labelImage = new Image(textureManager.getTexture("main_menu"));
        labelImage.setPosition(AppConfig.SCREEN_WIDTH / 2 - UIConfig.TITLE_LABEL_WIDTH / 2.3F,
                AppConfig.SCREEN_HEIGHT / 2 + UIConfig.TITLE_LABEL_HEIGHT / 3F, Align.bottomLeft);
        labelImage.setSize(UIConfig.TITLE_LABEL_WIDTH, UIConfig.TITLE_LABEL_HEIGHT);
        stage.addActor(labelImage);
    }

    private void initStatusLabel() {
        statusLabel = new Label("", skin);
        statusLabel.setPosition(
                AppConfig.SCREEN_WIDTH / 2 - UIConfig.TEXT_FIELD_INDENT * 2,
                AppConfig.SCREEN_HEIGHT / 2 - UIConfig.TEXT_FIELD_INDENT * 4, Align.bottomLeft);
        statusLabel.setColor(Color.RED);
        stage.addActor(statusLabel);
    }

    private void initLoginLabel() {
        Label loginLabel = new Label("Player: " + game.getPlayer().getLogin(), skin);
        loginLabel.setPosition(
                UIConfig.TEXT_FIELD_INDENT / 2,
                AppConfig.SCREEN_HEIGHT - UIConfig.TEXT_FIELD_INDENT * 2, Align.bottomLeft
        );
        loginLabel.setColor(Color.SCARLET);
        stage.addActor(loginLabel);
    }

    private void initBackground() {
        Image backgroundImage = new Image(textureManager.getTexture(AssetConfig.MENU_BACKGROUND_IMAGE_NAME));
        backgroundImage.setSize(AppConfig.SCREEN_WIDTH, AppConfig.SCREEN_HEIGHT);
        stage.addActor(backgroundImage);
        stage.addActor(backgroundImage);
    }

    private void initConnectToLobbyButton() {
        TextButton authorizeButton = new TextButton("Connect to lobby", skin);

        authorizeButton.setSize(UIConfig.TEXT_FIELD_WIDTH, UIConfig.TEXT_FIELD_HEIGHT);
        authorizeButton.setPosition(AppConfig.SCREEN_WIDTH / 2,
                AppConfig.SCREEN_HEIGHT / 2, Align.center);

        authorizeButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                game.showConnectToLobbyScreen();
            }
        });

        stage.addActor(authorizeButton);
    }

    private void initCreateLobbyButton() {
        TextButton authorizeButton = new TextButton("Create new lobby", skin);

        authorizeButton.setSize(UIConfig.TEXT_FIELD_WIDTH, UIConfig.TEXT_FIELD_HEIGHT);
        authorizeButton.setPosition(AppConfig.SCREEN_WIDTH / 2,
                AppConfig.SCREEN_HEIGHT / 2 - UIConfig.TEXT_FIELD_INDENT, Align.center);

        authorizeButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                networkManager.createLobbyRequest();
            }
        });

        stage.addActor(authorizeButton);
    }

    private void initSignOutButton() {
        TextButton signOutButton = new TextButton("Sign out", skin);

        signOutButton.setSize(UIConfig.TEXT_FIELD_WIDTH, UIConfig.TEXT_FIELD_HEIGHT);
        signOutButton.setPosition(AppConfig.SCREEN_WIDTH / 2,
                AppConfig.SCREEN_HEIGHT / 2 - UIConfig.TEXT_FIELD_INDENT * 4, Align.center);

        signOutButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                game.signOut();
            }
        });

        stage.addActor(signOutButton);
    }

    private void initQuitButton() {
        TextButton signOutButton = new TextButton("Quit", skin);

        signOutButton.setSize(UIConfig.TEXT_FIELD_WIDTH, UIConfig.TEXT_FIELD_HEIGHT);
        signOutButton.setPosition(AppConfig.SCREEN_WIDTH / 2,
                AppConfig.SCREEN_HEIGHT / 2 - UIConfig.TEXT_FIELD_INDENT * 5, Align.center);

        signOutButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                game.quit();
            }
        });

        stage.addActor(signOutButton);
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
}
