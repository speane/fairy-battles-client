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
import com.speanegames.fairybattles.entities.score.PlayerScore;
import com.speanegames.fairybattles.networking.NetworkManager;
import com.speanegames.fairybattles.rendering.TextureManager;

public class BattleFinishedScreen extends ScreenAdapter {

    private String team;
    private int position;

    private FairyBattlesGame game;
    private TextureManager textureManager;
    private NetworkManager networkManager;

    private Skin skin;
    private Stage stage;

    private Player player;

    private PlayerScore[] sunTeamScores;
    private PlayerScore[] moonTeamScores;

    private Label[] moonTeamLoginLabels;
    private Label[] sunTeamLoginLabels;
    private Label[] moonTeamScoreLabels;
    private Label[] sunTeamScoreLabels;

    public BattleFinishedScreen(FairyBattlesGame game,
                                PlayerScore[] sunTeamScores,
                                PlayerScore[] moonTeamScores,
                                String team,
                                int position) {

        this.team = team;
        this.position = position;
        this.player = game.getPlayer();
        this.sunTeamScores = sunTeamScores;
        this.moonTeamScores = moonTeamScores;
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
        initLeaveLobbyButton();
        initTeamScoreLabels();
    }

    private void initTitleLabel() {
        // TODO draw label

        Image labelImage = new Image(textureManager.getTexture("battle_lobby_label"));
        labelImage.setPosition(AppConfig.SCREEN_WIDTH / 2 - UIConfig.TITLE_LABEL_WIDTH / 2.3F,
                AppConfig.SCREEN_HEIGHT / 2 + UIConfig.TITLE_LABEL_HEIGHT / 3F, Align.bottomLeft);
        labelImage.setSize(UIConfig.TITLE_LABEL_WIDTH, UIConfig.TITLE_LABEL_HEIGHT);
        stage.addActor(labelImage);
    }

    private void initLeaveLobbyButton() {
        TextButton leaveLobbyButton = new TextButton("Back to lobby", skin);

        leaveLobbyButton.setSize(UIConfig.TEXT_FIELD_WIDTH, UIConfig.TEXT_FIELD_HEIGHT);
        leaveLobbyButton.setPosition(AppConfig.SCREEN_WIDTH / 2,
                AppConfig.SCREEN_HEIGHT / 2 - UIConfig.TEXT_FIELD_INDENT * 3, Align.center);

        leaveLobbyButton.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                game.backToLobbyScreen();
            }
        });

        stage.addActor(leaveLobbyButton);
    }


    private void initTeamScoreLabels() {
        sunTeamLoginLabels = new Label[AppConfig.MAX_TEAM_PLAYERS_AMOUNT];
        sunTeamScoreLabels = new Label[AppConfig.MAX_TEAM_PLAYERS_AMOUNT];

        moonTeamLoginLabels = new Label[AppConfig.MAX_TEAM_PLAYERS_AMOUNT];
        moonTeamScoreLabels = new Label[AppConfig.MAX_TEAM_PLAYERS_AMOUNT];

        Color sunColor;
        Color moonColor;

        if (team.equals("SUN")) {
            sunColor = Color.GREEN;
            moonColor = Color.RED;
        } else {
            sunColor = Color.RED;
            moonColor = Color.GREEN;
        }

        for (int i = 0; i < AppConfig.MAX_TEAM_PLAYERS_AMOUNT; i++) {
            String loginLabelText;
            String scoreLabelText;
            if (sunTeamScores[i] != null) {
                loginLabelText = sunTeamScores[i].getPlayer().getLogin();
                scoreLabelText = "" + sunTeamScores[i].getScore();
            } else {
                loginLabelText = "[EMPTY SLOT]";
                scoreLabelText = "";
            }
            sunTeamLoginLabels[i] = new Label(loginLabelText, skin);
            sunTeamLoginLabels[i].setPosition(
                    UIConfig.TEXT_FIELD_INDENT * 2,
                    AppConfig.SCREEN_HEIGHT / 2
                            - i * UIConfig.TEXT_FIELD_INDENT / 2,
                    Align.bottomLeft);
            sunTeamLoginLabels[i].setColor(sunColor);
            stage.addActor(sunTeamLoginLabels[i]);

            sunTeamScoreLabels[i] = new Label(scoreLabelText, skin);
            sunTeamScoreLabels[i].setPosition(
                    UIConfig.TEXT_FIELD_INDENT * 2 + 170,
                    AppConfig.SCREEN_HEIGHT / 2
                            - i * UIConfig.TEXT_FIELD_INDENT / 2,
                    Align.bottomLeft);
            sunTeamScoreLabels[i].setColor(sunColor);
            stage.addActor(sunTeamScoreLabels[i]);

            if (moonTeamScores[i] != null) {
                loginLabelText = moonTeamScores[i].getPlayer().getLogin();
                scoreLabelText = "" + moonTeamScores[i].getScore();
            } else {
                loginLabelText = "[EMPTY SLOT]";
                scoreLabelText = "";
            }

            moonTeamLoginLabels[i] = new Label(loginLabelText, skin);
            moonTeamLoginLabels[i].setPosition(
                    AppConfig.SCREEN_WIDTH / 2 + UIConfig.TEXT_FIELD_INDENT * 6,
                    AppConfig.SCREEN_HEIGHT / 2
                            - i * UIConfig.TEXT_FIELD_INDENT / 2,
                    Align.bottomLeft);
            moonTeamLoginLabels[i].setColor(moonColor);
            stage.addActor(moonTeamLoginLabels[i]);

            moonTeamScoreLabels[i] = new Label(scoreLabelText, skin);
            moonTeamScoreLabels[i].setPosition(
                    AppConfig.SCREEN_WIDTH / 2 + UIConfig.TEXT_FIELD_INDENT * 6 + 170,
                    AppConfig.SCREEN_HEIGHT / 2
                            - i * UIConfig.TEXT_FIELD_INDENT / 2,
                    Align.bottomLeft);
            moonTeamScoreLabels[i].setColor(moonColor);
            stage.addActor(moonTeamScoreLabels[i]);
        }

        if (team.equals("SUN")) {
            sunTeamLoginLabels[position].setColor(Color.BLUE);
        } else {
            moonTeamLoginLabels[position].setColor(Color.BLUE);
        }
    }
}
