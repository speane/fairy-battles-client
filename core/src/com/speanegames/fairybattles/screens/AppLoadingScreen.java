package com.speanegames.fairybattles.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.speanegames.fairybattles.FairyBattlesGame;
import com.speanegames.fairybattles.config.AppConfig;
import com.speanegames.fairybattles.config.AssetConfig;
import com.speanegames.fairybattles.networking.NetworkManager;
import com.speanegames.fairybattles.rendering.TextureManagerImpl;

public class AppLoadingScreen extends ScreenAdapter {

    private static final float PROGRESS_BAR_WIDTH_RATIO = 3;
    private static final float PROGRESS_BAR_HEIGHT_RATIO = 10;
    private static float PROGRESS_BAR_WIDTH;
    private static float PROGRESS_BAR_HEIGHT;

    private Viewport viewport;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;

    private float progress;

    private final FairyBattlesGame game;

    public AppLoadingScreen(FairyBattlesGame game) {
        this.game = game;
        PROGRESS_BAR_WIDTH = AppConfig.SCREEN_WIDTH / PROGRESS_BAR_WIDTH_RATIO;
        PROGRESS_BAR_HEIGHT = AppConfig.SCREEN_HEIGHT / PROGRESS_BAR_HEIGHT_RATIO;
    }

    @Override
    public void show() {
        initCamera();
        initViewport();
        initShapeRenderer();
        loadResourcesAsync();
    }

    @Override
    public void render(float delta) {
        update();
        clearScreen();
        draw();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    private void initCamera() {
        camera = new OrthographicCamera();
        camera.position.set(AppConfig.SCREEN_WIDTH / 2,
                AppConfig.SCREEN_HEIGHT / 2, 0);
        camera.update();
    }

    private void initViewport() {
        viewport = new FitViewport(AppConfig.SCREEN_WIDTH,
                AppConfig.SCREEN_HEIGHT, camera);
    }

    private void initShapeRenderer() {
        shapeRenderer = new ShapeRenderer();
    }

    private void loadResourcesAsync() {
        AssetManager assetManager = game.getAssetManager();
        assetManager.load(AssetConfig.TEXTURE_ATLAS_PATH, TextureAtlas.class);
        assetManager.load(AssetConfig.BATTLE_FIELD_TILED_MAP_PATH, TiledMap.class);
    }

    private void update() {
        if (game.getAssetManager().update()) {

            TextureAtlas atlas = game.getAssetManager().get(
                    AssetConfig.TEXTURE_ATLAS_PATH, TextureAtlas.class);

            game.setTextureManager(new TextureManagerImpl(atlas));
            NetworkManager networkManager = new NetworkManager(game);
            networkManager.start();
            game.setNetworkManager(networkManager);

            game.showSignInScreen();
        } else {
            progress = game.getAssetManager().getProgress();
        }
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void draw() {
        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(
                (AppConfig.SCREEN_WIDTH - PROGRESS_BAR_WIDTH) / 2,
                (AppConfig.SCREEN_HEIGHT - PROGRESS_BAR_HEIGHT) / 2,
                PROGRESS_BAR_WIDTH * progress, PROGRESS_BAR_HEIGHT);
        shapeRenderer.end();
    }
}
