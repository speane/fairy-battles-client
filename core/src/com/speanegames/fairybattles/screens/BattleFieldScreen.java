package com.speanegames.fairybattles.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.speanegames.fairybattles.FairyBattlesGame;
import com.speanegames.fairybattles.collision.CollisionDetector;
import com.speanegames.fairybattles.config.AppConfig;
import com.speanegames.fairybattles.config.AssetConfig;
import com.speanegames.fairybattles.config.BattleFieldUIConfig;
import com.speanegames.fairybattles.entities.bullet.Bullet;
import com.speanegames.fairybattles.entities.fortress.Fortress;
import com.speanegames.fairybattles.entities.fortress.FortressFactory;
import com.speanegames.fairybattles.entities.hero.Hero;
import com.speanegames.fairybattles.entities.hero.HeroFactory;
import com.speanegames.fairybattles.entities.moving.Direction;
import com.speanegames.fairybattles.networking.NetworkManager;
import com.speanegames.fairybattles.rendering.RendererImpl;
import com.speanegames.fairybattles.rendering.TextureManager;

import java.util.Iterator;

public class BattleFieldScreen extends ScreenAdapter {

    private final FairyBattlesGame game;

    private Batch batch;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private ShapeRenderer shapeRenderer;

    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;

    private RendererImpl renderer;
    private HeroFactory heroFactory;
    private FortressFactory fortressFactory;
    private CollisionDetector collisionDetector;
    private NetworkManager networkManager;

    private Hero hero;
    private Fortress fortress;
    private Fortress enemyFortress;

    private TiledMap battleFieldMap;
    private TiledMapTileLayer indestructibleTiledMapLayer;

    private float MAP_WIDTH;
    private float MAP_HEIGHT;

    public BattleFieldScreen(FairyBattlesGame game,
                             TextureManager textureManager) {

        this.game = game;
        heroFactory = new HeroFactory(textureManager);
        fortressFactory = new FortressFactory(textureManager);
        collisionDetector = new CollisionDetector();
    }

    @Override
    public void show() {
        battleFieldMap = game.getAssetManager().get(
                AssetConfig.BATTLE_FIELD_TILED_MAP_PATH,
                TiledMap.class);

        indestructibleTiledMapLayer =
                (TiledMapTileLayer) battleFieldMap.getLayers().get(
                        AssetConfig.INDESTRUCTIBLE_MAP_LAYER_NAME);

        initLevelSize();

        batch = new SpriteBatch();

        renderer = new RendererImpl(batch);
        shapeRenderer = new ShapeRenderer();

        initEntities();
        initCamera();
        initInputProcessor();
        initNetwork();
    }

    @Override
    public void render(float delta) {
        handleInput();
        updateBullets();
        checkCollisions();
        updateCamera();
        draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    private void initCamera() {
        camera = new OrthographicCamera();
        orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(battleFieldMap, batch);
        orthogonalTiledMapRenderer.setView(camera);

        updateCamera();

        viewport = new FitViewport(AppConfig.SCREEN_WIDTH,
                AppConfig.SCREEN_HEIGHT, camera);
        viewport.apply(true);
    }

    private void updateCamera() {
        float cameraX;
        float cameraY;
        if ((hero.getX() + AppConfig.SCREEN_WIDTH / 2) > MAP_WIDTH) {
            cameraX = MAP_WIDTH - AppConfig.SCREEN_WIDTH / 2;
        }
        else if ((hero.getX() - AppConfig.SCREEN_WIDTH / 2) < 0) {
            cameraX = AppConfig.SCREEN_WIDTH / 2;
        }
        else {
            cameraX = hero.getX();
        }

        if ((hero.getY() + AppConfig.SCREEN_HEIGHT / 2) > MAP_HEIGHT) {
            cameraY = MAP_HEIGHT - AppConfig.SCREEN_HEIGHT / 2;
        }
        else if ((hero.getY() - AppConfig.SCREEN_HEIGHT / 2) < 0) {
            cameraY = AppConfig.SCREEN_HEIGHT / 2;
        }
        else {
            cameraY = hero.getY();
        }

        camera.position.set(cameraX, cameraY, camera.position.z);
        camera.update();
        orthogonalTiledMapRenderer.setView(camera);
    }

    private void initEntities() {
        hero = heroFactory.createHero("WATER", 600, 200, 0);
        fortress = fortressFactory.createFortress("MOON", (int) (MAP_WIDTH / 2),
                (int) (indestructibleTiledMapLayer.getTileHeight() * 4), 180);
        enemyFortress = fortressFactory.createFortress(
                "SUN", (int) (MAP_WIDTH / 2),
                (int) (MAP_HEIGHT - indestructibleTiledMapLayer.getTileHeight() * 4), 0);

        renderer.subscribe(enemyFortress);
        renderer.subscribe(fortress);
        renderer.subscribe(hero);
    }

    private void draw() {
        clearScreen();
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);



        orthogonalTiledMapRenderer.render();

        batch.begin();

        renderer.draw(hero);
        renderer.draw(fortress);
        renderer.draw(enemyFortress);

        for (Bullet bullet : hero.getBullets()) {
            renderer.draw(bullet);
        }

        batch.end();

        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.GREEN);

        renderFortressHealthBar(fortress);
        renderFortressHealthBar(enemyFortress);

        renderHeroHealthBar(hero);


        shapeRenderer.end();
    }

    private void renderHeroHealthBar(Hero hero) {
        shapeRenderer.setColor(Color.BLACK);

        shapeRenderer.rect(
                hero.getX() + hero.getWidth() / 2
                        - BattleFieldUIConfig.HERO_HEALTH_BAR_WIDTH / 2 - 1,
                hero.getY() + hero.getHeight()
                        + BattleFieldUIConfig.HERO_HEALTH_BAR_VERTICAL_INDENT - 1,
                BattleFieldUIConfig.HERO_HEALTH_BAR_WIDTH + 2,
                BattleFieldUIConfig.HERO_HEALTH_BAR_HEIGHT + 2);


        float ratio = ((float) hero.getCurrentHealth()
                / hero.getMaxHealth());

        shapeRenderer.setColor(getHealthColor(ratio * 100));

        shapeRenderer.rect(
                hero.getX() + hero.getWidth() / 2
                        - BattleFieldUIConfig.HERO_HEALTH_BAR_WIDTH / 2,
                hero.getY() + hero.getHeight()
                        + BattleFieldUIConfig.HERO_HEALTH_BAR_VERTICAL_INDENT,
                BattleFieldUIConfig.HERO_HEALTH_BAR_WIDTH
                        * ((float) hero.getCurrentHealth()
                                / hero.getMaxHealth()),
                BattleFieldUIConfig.HERO_HEALTH_BAR_HEIGHT);
    }

    private void renderFortressHealthBar(Fortress fortress) {
        shapeRenderer.setColor(Color.BLACK);

        shapeRenderer.rect(
                fortress.getX() + fortress.getWidth() / 2
                        - BattleFieldUIConfig.FORTRESS_HEALTH_BAR_WIDTH / 2 - 1,
                fortress.getY() + fortress.getHeight()
                        + BattleFieldUIConfig.FORTRESS_HEALTH_BAR_VERTICAL_INDENT - 1,
                BattleFieldUIConfig.FORTRESS_HEALTH_BAR_WIDTH + 2,
                BattleFieldUIConfig.FORTRESS_HEALTH_BAR_HEIGHT + 2);


        float healthPercent = (float) fortress.getCurrentHealth()
                / fortress.getMaxHealth();

        shapeRenderer.setColor(getHealthColor(healthPercent * 100));

        shapeRenderer.rect(
                fortress.getX() + fortress.getWidth() / 2
                        - BattleFieldUIConfig.FORTRESS_HEALTH_BAR_WIDTH / 2,
                fortress.getY() + fortress.getHeight()
                        + BattleFieldUIConfig.FORTRESS_HEALTH_BAR_VERTICAL_INDENT,
                BattleFieldUIConfig.FORTRESS_HEALTH_BAR_WIDTH * healthPercent,
                BattleFieldUIConfig.FORTRESS_HEALTH_BAR_HEIGHT);
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.WHITE.r, Color.WHITE.g, Color.WHITE.b, Color.WHITE.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public void handleInput() {
        final int MOUSE_COLLISION_RADIUS = 30;

        boolean moved = false;

        float oldX = hero.getX();
        float oldY = hero.getY();

        boolean aKeyPressed = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean wKeyPressed = Gdx.input.isKeyPressed(Input.Keys.W);
        boolean sKeyPressed = Gdx.input.isKeyPressed(Input.Keys.S);
        boolean dKeyPressed = Gdx.input.isKeyPressed(Input.Keys.D);

        Vector3 mouseVector = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouseVector);

        int mouseX = (int) mouseVector.x;
        int mouseY = (int) mouseVector.y;
        int heroX = (int) (hero.getX() + hero.getWidth() / 2);
        int heroY = (int) (hero.getY() + hero.getHeight() / 2);
        int deltaX = mouseX - heroX;
        int deltaY = mouseY - heroY;
        float rotation = MathUtils.atan2(deltaY, deltaX) * 180.0f
                / MathUtils.PI - 90;


        hero.setRotation(rotation);

        if (aKeyPressed) {
            hero.move(Direction.LEFT);
            moved = true;
        }

        if (dKeyPressed) {
            hero.move(Direction.RIGHT);
            moved = true;
        }

        if (!((Math.abs(mouseX - heroX) < MOUSE_COLLISION_RADIUS) &&
                (Math.abs(mouseY - heroY) < MOUSE_COLLISION_RADIUS))) {

            if (wKeyPressed) {
                hero.move(Direction.FORWARD);
                moved = true;
            }

            if (sKeyPressed) {
                hero.move(Direction.BACKWARD);
                moved = true;
            }
        }

        if (moved) {
            if (collisionDetector.collidesWithLayer(indestructibleTiledMapLayer,
                    hero.getCollisionModel())
                    || collisionDetector.isCollision(hero, fortress)
                    || collisionDetector.isCollision(hero, enemyFortress)) {

                hero.setPosition(oldX, oldY);
            }
        }
    }

    private void initInputProcessor() {
        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    hero.shoot();
                    return true;
                }

                return false;
            }
        });
    }

    private void initLevelSize() {
        TiledMapTileLayer tiledMapTileLayer =
                (TiledMapTileLayer) battleFieldMap.getLayers().get(
                        AssetConfig.BACKGROUND_MAP_LAYER_NAME);

        MAP_WIDTH = tiledMapTileLayer.getWidth() * tiledMapTileLayer.getTileWidth();
        MAP_HEIGHT = tiledMapTileLayer.getHeight() * tiledMapTileLayer.getTileHeight();
    }

    private void updateBullets() {
        for (Bullet bullet : hero.getBullets()) {
            bullet.move();
        }
    }

    private void initNetwork() {
        networkManager = new NetworkManager();
        networkManager.start();
        networkManager.connectToRoom();
    }

    private void checkCollisions() {
        Iterator<Bullet> bulletIterator = hero.getBullets().iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.move();

            if (bullet.isFinished()) {
                bulletIterator.remove();
            } else if (collisionDetector.collidesWithLayer(indestructibleTiledMapLayer,
                    bullet.getCollisionModel())) {

                bulletIterator.remove();
            } else if (collisionDetector.isCollision(bullet, enemyFortress)) {
                int enemyFortressNewHealth = enemyFortress.getCurrentHealth() - bullet.getHero().getDamage();
                enemyFortress.setCurrentHealth(enemyFortressNewHealth > 0 ? enemyFortressNewHealth : 0);
                bulletIterator.remove();
            }
        }
    }

    private Color getHealthColor(float healthPercents) {
        if (healthPercents > BattleFieldUIConfig.GREEN_COLOR_HEALTH_PERCENT) {
            return Color.GREEN;
        } else if (healthPercents > BattleFieldUIConfig.YELLOW_COLOR_HEALTH_PERCENT) {
            return Color.YELLOW;
        } else {
            return Color.RED;
        }
    }
}
