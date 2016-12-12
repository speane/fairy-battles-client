package com.speanegames.fairybattles.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
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
import com.speanegames.fairybattles.entities.player.Player;
import com.speanegames.fairybattles.networking.NetworkManager;
import com.speanegames.fairybattles.rendering.RendererImpl;
import com.speanegames.fairybattles.rendering.TextureManager;

import java.util.Iterator;

public class BattleFieldScreen extends ScreenAdapter {

    private final FairyBattlesGame game;

    private Player player;
    private Player[] sunTeam;
    private Player[] moonTeam;

    private Hero[] sunHeroes;
    private Hero[] moonHeroes;

    private String team;
    private int position;

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
    private TextureManager textureManager;

    private Hero hero;

    private Hero[] allies;
    private Hero[] enemies;

    private Fortress allyFortress;
    private Fortress enemyFortress;

    private Fortress sunFortress;
    private Fortress moonFortress;

    private TiledMap battleFieldMap;
    private TiledMapTileLayer indestructibleTiledMapLayer;

    private float mapWidth;
    private float mapHeight;

    public BattleFieldScreen(FairyBattlesGame game, String team, int position) {
        this.sunTeam = game.getSunTeam();
        this.moonTeam = game.getMoonTeam();
        this.team = team;
        this.position = position;
        this.game = game;
        this.player = game.getPlayer();
        this.textureManager = game.getTextureManager();
        this.networkManager = game.getNetworkManager();
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
    }

    @Override
    public void render(float delta) {
        handleInput();
        updateBullets();
        checkCollisions();
        updateCamera();
        draw();

        networkManager.moveHeroRequest(hero.getX(), hero.getY(), hero.getRotation());
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public void moveHero(String team, int position, float x, float y, float rotation) {
        if (team.equals("SUN")) {
            sunHeroes[position].setPosition(x, y);
            sunHeroes[position].setRotation(rotation);
        } else {
            moonHeroes[position].setPosition(x, y);
            moonHeroes[position].setRotation(rotation);
        }
    }

    public void shootHero(String team, int position) {
        // TODO shoot hero
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
        if ((hero.getX() + AppConfig.SCREEN_WIDTH / 2) > mapWidth) {
            cameraX = mapWidth - AppConfig.SCREEN_WIDTH / 2;
        }
        else if ((hero.getX() - AppConfig.SCREEN_WIDTH / 2) < 0) {
            cameraX = AppConfig.SCREEN_WIDTH / 2;
        }
        else {
            cameraX = hero.getX();
        }

        if ((hero.getY() + AppConfig.SCREEN_HEIGHT / 2) > mapHeight) {
            cameraY = mapHeight - AppConfig.SCREEN_HEIGHT / 2;
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
        sunHeroes = new Hero[AppConfig.MAX_TEAM_PLAYERS_AMOUNT];
        moonHeroes = new Hero[AppConfig.MAX_TEAM_PLAYERS_AMOUNT];
        for (int i = 0; i < AppConfig.MAX_TEAM_PLAYERS_AMOUNT; i++) {
            if (sunTeam[i] != null) {
                System.out.println("SUN: " + i);
                sunHeroes[i] = heroFactory.createHero("WATER", mapWidth / 6 * (i + 2), 300, 0);
            }

            if (moonTeam[i] != null) {
                System.out.println("MOON: " + i);
                moonHeroes[i] = heroFactory.createHero("WATER", mapWidth / 6 * (i + 2), mapHeight - 300, 180);
            }
        }

        sunFortress = fortressFactory.createFortress(
                "SUN", (int) (mapWidth / 2),
                (int) (indestructibleTiledMapLayer.getTileHeight() * 4), 180);

        moonFortress = fortressFactory.createFortress("MOON", (int) (mapWidth / 2),
                (int) (mapHeight - indestructibleTiledMapLayer.getTileHeight() * 4), 0);


        if (team.equals("SUN")) {
            hero = sunHeroes[position];
            allies = sunHeroes;
            enemies = moonHeroes;
            allyFortress = sunFortress;
            enemyFortress = moonFortress;
        } else {
            hero = moonHeroes[position];
            allies = moonHeroes;
            enemies = sunHeroes;
            allyFortress = moonFortress;
            enemyFortress = sunFortress;
        }
    }

    private void draw() {
        clearScreen();
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);



        orthogonalTiledMapRenderer.render();

        batch.begin();

        renderer.draw(hero);
        renderer.draw(sunFortress);
        renderer.draw(moonFortress);

        for (int i = 0; i < AppConfig.MAX_TEAM_PLAYERS_AMOUNT; i++) {
            if (sunHeroes[i] != null) {
                renderer.draw(sunHeroes[i]);
            }

            if (moonHeroes[i] != null) {
                renderer.draw(moonHeroes[i]);
            }
        }

        for (Bullet bullet : hero.getBullets()) {
            renderer.draw(bullet);
        }

        batch.end();

        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.GREEN);

        renderFortressHealthBar(sunFortress);
        renderFortressHealthBar(moonFortress);

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
                    || collisionDetector.isCollision(hero, sunFortress)
                    || collisionDetector.isCollision(hero, moonFortress)) {

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

        mapWidth = tiledMapTileLayer.getWidth() * tiledMapTileLayer.getTileWidth();
        mapHeight = tiledMapTileLayer.getHeight() * tiledMapTileLayer.getTileHeight();
    }

    private void updateBullets() {
        for (Bullet bullet : hero.getBullets()) {
            bullet.move();
        }
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
