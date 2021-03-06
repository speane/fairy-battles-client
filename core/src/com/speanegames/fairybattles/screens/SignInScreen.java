package com.speanegames.fairybattles.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.speanegames.fairybattles.FairyBattlesGame;
import com.speanegames.fairybattles.config.AppConfig;
import com.speanegames.fairybattles.config.AssetConfig;
import com.speanegames.fairybattles.config.UIConfig;
import com.speanegames.fairybattles.networking.NetworkManager;
import com.speanegames.fairybattles.rendering.TextureManager;

public class SignInScreen extends ScreenAdapter {

    private TextureManager textureManager;
    private NetworkManager networkManager;

    private Skin skin;
    private Stage stage;

    private TextField loginTextField;
    private TextField passwordTextField;
    private Label statusLabel;

    private FairyBattlesGame game;

    public SignInScreen(FairyBattlesGame game) {
        this.game = game;
        networkManager = game.getNetworkManager();
        textureManager = game.getTextureManager();
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

    private void initStatusLabel() {
        statusLabel = new Label("", skin);
        statusLabel.setPosition(
                AppConfig.SCREEN_WIDTH / 2 - UIConfig.TEXT_FIELD_INDENT * 2,
                AppConfig.SCREEN_HEIGHT / 2 - UIConfig.TEXT_FIELD_INDENT * 4, Align.bottomLeft);
        statusLabel.setColor(Color.RED);
        stage.addActor(statusLabel);
    }

    private void initTitleLabel() {
        Image labelImage = new Image(textureManager.getTexture("sign_in_label"));
        labelImage.setPosition(AppConfig.SCREEN_WIDTH / 2 - UIConfig.TITLE_LABEL_WIDTH / 2.3F,
                AppConfig.SCREEN_HEIGHT / 2 + UIConfig.TITLE_LABEL_HEIGHT / 3F, Align.bottomLeft);
        labelImage.setSize(UIConfig.TITLE_LABEL_WIDTH, UIConfig.TITLE_LABEL_HEIGHT);
        stage.addActor(labelImage);
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
        addLoginInputField();
        addPasswordInputField();
        addAuthorizeButton();
        initSignUpButton();
        initQuitButton();
        initStatusLabel();
    }

    private void initQuitButton() {
        TextButton signOutButton = new TextButton("Quit", skin);

        signOutButton.setSize(UIConfig.TEXT_FIELD_WIDTH, UIConfig.TEXT_FIELD_HEIGHT);
        signOutButton.setPosition(AppConfig.SCREEN_WIDTH / 2,
                AppConfig.SCREEN_HEIGHT / 2 - UIConfig.TEXT_FIELD_INDENT * 6, Align.center);

        signOutButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                game.quit();
            }
        });

        stage.addActor(signOutButton);
    }

    private void addLoginInputField() {
        loginTextField = new TextField("", skin);

        loginTextField.setMessageText("Login");
        loginTextField.setSize(UIConfig.TEXT_FIELD_WIDTH, UIConfig.TEXT_FIELD_HEIGHT);
        loginTextField.setPosition(AppConfig.SCREEN_WIDTH / 2, AppConfig.SCREEN_HEIGHT / 2 + UIConfig.TEXT_FIELD_INDENT, Align.center);

        stage.addActor(loginTextField);
    }

    private void addPasswordInputField() {
        passwordTextField = new TextField("", skin);

        passwordTextField.setMessageText("Password");
        passwordTextField.setPasswordMode(true);
        passwordTextField.setPasswordCharacter('*');
        passwordTextField.setSize(UIConfig.TEXT_FIELD_WIDTH, UIConfig.TEXT_FIELD_HEIGHT);
        passwordTextField.setPosition(AppConfig.SCREEN_WIDTH / 2, AppConfig.SCREEN_HEIGHT / 2, Align.center);

        stage.addActor(passwordTextField);
    }

    private void addAuthorizeButton() {
        TextButton authorizeButton = new TextButton("Sign in", skin);

        authorizeButton.setSize(UIConfig.TEXT_FIELD_WIDTH, UIConfig.TEXT_FIELD_HEIGHT);
        authorizeButton.setPosition(AppConfig.SCREEN_WIDTH / 2,
                AppConfig.SCREEN_HEIGHT / 2 - UIConfig.TEXT_FIELD_INDENT * 2, Align.center);

        authorizeButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                networkManager.signInRequest(loginTextField.getText(), passwordTextField.getText());
            }
        });

        stage.addActor(authorizeButton);
    }

    private void initSignUpButton() {
        TextButton signUpButton = new TextButton("Sign up", skin);

        signUpButton.setSize(UIConfig.TEXT_FIELD_WIDTH, UIConfig.TEXT_FIELD_HEIGHT);
        signUpButton.setPosition(AppConfig.SCREEN_WIDTH / 2,
                AppConfig.SCREEN_HEIGHT / 2 - UIConfig.TEXT_FIELD_INDENT * 3, Align.center);

        signUpButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                game.showSignUpScreen();
            }
        });

        stage.addActor(signUpButton);
    }

    public void setSignInErrorMessage(String message) {
        statusLabel.setText(message);
    }
}
