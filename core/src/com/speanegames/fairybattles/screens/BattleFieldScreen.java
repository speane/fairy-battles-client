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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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
import com.speanegames.fairybattles.entities.score.PlayerScore;
import com.speanegames.fairybattles.networking.NetworkManager;
import com.speanegames.fairybattles.rendering.RendererImpl;
import com.speanegames.fairybattles.rendering.TextureManager;

import java.util.Iterator;

public class BattleFieldScreen extends ScreenAdapter {

    private final FairyBattlesGame game;

    private Player player;
    private Player[] sunTeam;
    private Player[] moonTeam;

    private PlayerScore playerScore;
    private PlayerScore[] sunTeamScores;
    private PlayerScore[] moonTeamScores;

    private Hero[] sunHeroes;
    private Hero[] moonHeroes;

    private boolean isEnd;
    private String victoryTeam;
    private boolean isVictory;

    private Label healthBarLabel;
    private Label shootLoadBarLabel;

    private String team;
    private int position;

    private Batch batch;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private ShapeRenderer shapeRenderer;
    private Stage stage;
    private Skin skin;

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

    private boolean isHost;

    private Fortress allyFortress;
    private Fortress enemyFortress;

    private Fortress sunFortress;
    private Fortress moonFortress;

    private TiledMap battleFieldMap;
    private TiledMapTileLayer indestructibleTiledMapLayer;

    private float mapWidth;
    private float mapHeight;

    public BattleFieldScreen(FairyBattlesGame game, String team, int position, boolean isHost) {
        this.isHost = isHost;
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
        skin = new Skin(Gdx.files.internal(AssetConfig.UI_SKIN_FILE_PATH));

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
        initScores();
        initCamera();
        initUI();
        initInputProcessor();
    }

    @Override
    public void render(float delta) {
        if (!isEnd) {
            handleInput();
            updateBullets();
            checkCollisions();
            updateCamera();
            updateUI();
            hero.setLoadTime(hero.getLoadTime() + delta * 1000);
            if (hero.isAlive() && !isEnd) {
                networkManager.moveHeroRequest(hero.getX(), hero.getY(), hero.getRotation());
            } else {
                if (hero.isKilled()) {
                    hero.setTimeAfterDeath(hero.getTimeAfterDeath() + delta * 1000);
                    if (hero.isRespawned()) {
                        networkManager.respawn(team, position);
                    }
                }
            }
        }
        draw();
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public void moveHero(String team, int position, float x, float y, float rotation) {
        if (team.equals("SUN")) {
            if (sunHeroes[position] != null) {
                sunHeroes[position].setPosition(x, y);
                sunHeroes[position].setRotation(rotation);
            }
        } else {
            if (moonHeroes[position] != null) {
                moonHeroes[position].setPosition(x, y);
                moonHeroes[position].setRotation(rotation);
            }
        }
    }

    public void shootHero(String team, int position) {
        if (team.equals("SUN")) {
            sunHeroes[position].shoot();
        } else {
            moonHeroes[position].shoot();
        }
    }

    public void hitHero(String shooterTeam, int shooterPosition, int targetPosition) {
        Hero shooter;
        Hero target;
        if (shooterTeam.equals("SUN")) {
            shooter = sunHeroes[shooterPosition];
            target = moonHeroes[targetPosition];
        } else {
            shooter = moonHeroes[shooterPosition];
            target = sunHeroes[targetPosition];
        }
        target.setCurrentHealth(target.getCurrentHealth() - shooter.getDamage());
    }

    public void hitFortress(String shooterTeam, int shooterPosition) {
        if (shooterTeam.equals("SUN")) {
            Hero shooter = sunHeroes[shooterPosition];
            Fortress target = moonFortress;
            target.setCurrentHealth(target.getCurrentHealth() - shooter.getDamage());
            if (target.getCurrentHealth() <= 0) {
                networkManager.destroyFortress("MOON");
            }
            sunTeamScores[shooterPosition].setScore(sunTeamScores[shooterPosition].getScore() + 30);
        } else {
            Hero shooter = moonHeroes[shooterPosition];
            Fortress target = sunFortress;
            target.setCurrentHealth(target.getCurrentHealth() - shooter.getDamage());
            if (target.getCurrentHealth() <= 0) {
                networkManager.destroyFortress("SUN");
            }
            moonTeamScores[shooterPosition].setScore(moonTeamScores[shooterPosition].getScore() + 30);
        }
    }

    public void killHero(String killerTeam, int killerPosition, String targetTeam, int targetPosition) {
        System.out.println("KILL HERO RESPONSE: " + killerTeam + " " + killerPosition + targetTeam + targetPosition);


        Hero killer;
        Hero target;
        if (killerTeam.equals("SUN")) {
            killer = sunHeroes[killerPosition];
            target = moonHeroes[targetPosition];
            sunTeamScores[killerPosition].setScore(sunTeamScores[killerPosition].getScore() + 100);
        } else {
            killer = moonHeroes[killerPosition];
            target = sunHeroes[targetPosition];
            moonTeamScores[killerPosition].setScore(moonTeamScores[killerPosition].getScore() + 100);
        }

        target.kill();
    }

    public void respawnHero(String team, int position) {
        Hero respawnedHero;
        if (team.equals("SUN")) {
            respawnedHero = sunHeroes[position];
            respawnedHero.setPosition(mapWidth / 6 * (position + 2), 300);
            respawnedHero.setRotation(0);
        } else {
            respawnedHero = moonHeroes[position];
            respawnedHero.setPosition(mapWidth / 6 * (position + 2), mapHeight - 300);
            respawnedHero.setRotation(180);
        }

        respawnedHero.setKilled(false);
        respawnedHero.setTimeAfterDeath(respawnedHero.getRespawnTime());
        respawnedHero.setCurrentHealth(respawnedHero.getMaxHealth());
    }

    public void destroyFortress(String destroyedFortressTeam) {
        isVictory = !team.equals(destroyedFortressTeam);
        if (isHost) {
            this.networkManager.finishBattle();
        }
    }

    public boolean isWinner() {
        return isVictory;
    }

    public PlayerScore[] getSunTeamScores() {
        return sunTeamScores;
    }

    public PlayerScore[] getMoonTeamScores() {
        return moonTeamScores;
    }

    public PlayerScore getPlayerScore() {
        return playerScore;
    }

    public String getTeam() {
        return team;
    }

    public int getPosition() {
        return position;
    }

    private void initScores() {
        sunTeamScores = new PlayerScore[AppConfig.MAX_TEAM_PLAYERS_AMOUNT];
        moonTeamScores = new PlayerScore[AppConfig.MAX_TEAM_PLAYERS_AMOUNT];
        for (int i = 0; i < AppConfig.MAX_TEAM_PLAYERS_AMOUNT; i++) {
            if (sunTeam[i] != null) {
                sunTeamScores[i] = new PlayerScore();
                sunTeamScores[i].setPlayer(sunTeam[i]);
            }

            if (moonTeam[i] != null) {
                moonTeamScores[i] = new PlayerScore();
                moonTeamScores[i].setPlayer(moonTeam[i]);
            }
        }

        if (team.equals("SUN")) {
            playerScore = sunTeamScores[position];
        } else {
            playerScore = moonTeamScores[position];
        }
    }

    private void initUI() {
        stage = new Stage(viewport);
        initHealthBarLabel();
        initShootLoadBarLabel();

        Gdx.input.setInputProcessor(stage);
    }

    private void initHealthBarLabel() {
        healthBarLabel = new Label("Health: ", skin);
        healthBarLabel.setColor(Color.SCARLET);
        stage.addActor(healthBarLabel);
    }

    private void initShootLoadBarLabel() {
        shootLoadBarLabel = new Label("Shoot: ", skin);
        shootLoadBarLabel.setColor(Color.SCARLET);
        stage.addActor(shootLoadBarLabel);
    }

    private void updateUI() {
        float leftBottomCornerX = (camera.position.x - AppConfig.SCREEN_WIDTH / 2);
        float leftBottomCornerY = (camera.position.y - AppConfig.SCREEN_HEIGHT / 2);
        healthBarLabel.setPosition(
                leftBottomCornerX + BattleFieldUIConfig.HEALTH_BAR_LABEL_LEFT_INDENT,
                leftBottomCornerY + BattleFieldUIConfig.HEALTH_BAR_LABEL_BOTTOM_INDENT);
        shootLoadBarLabel.setPosition(
                leftBottomCornerX + BattleFieldUIConfig.SHOOT_LOAD_BAR_LABEL_LEFT_INDENT,
                leftBottomCornerY + BattleFieldUIConfig.SHOOT_LOAD_BAR_LABEL_BOTTOM_INDENT);
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
                sunHeroes[i] = heroFactory.createHero("FIRE", mapWidth / 6 * (i + 2), 300, 0);
            }

            if (moonTeam[i] != null) {
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


        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        renderFortressHealthBar(sunFortress);
        renderFortressHealthBar(moonFortress);


        // renderer.draw(hero);
        renderer.draw(sunFortress);
        renderer.draw(moonFortress);

        for (int i = 0; i < AppConfig.MAX_TEAM_PLAYERS_AMOUNT; i++) {
            if (sunHeroes[i] != null) {
                for (Bullet bullet : sunHeroes[i].getBullets()) {
                    renderer.draw(bullet);
                }
                if (sunHeroes[i].isAlive()) {
                    renderer.draw(sunHeroes[i]);
                    renderHeroHealthBar(sunHeroes[i]);
                }
            }

            if (moonHeroes[i] != null) {
                for (Bullet bullet : moonHeroes[i].getBullets()) {
                    renderer.draw(bullet);
                }
                if (moonHeroes[i].isAlive()) {
                    renderer.draw(moonHeroes[i]);
                    renderHeroHealthBar(moonHeroes[i]);
                }
            }
        }


        renderPlayerHealthBar(hero);
        renderShootLoadBar(hero);

        batch.end();

        shapeRenderer.end();
    }

    private void renderPlayerHealthBar(Hero hero) {
        shapeRenderer.setColor(Color.BLACK);

        shapeRenderer.rect(
                healthBarLabel.getX() + 68,
                healthBarLabel.getY() + 3,
                BattleFieldUIConfig.HERO_HEALTH_BAR_WIDTH * 5 + 4,
                BattleFieldUIConfig.HERO_HEALTH_BAR_HEIGHT * 3 + 4);


        if (hero.isAlive()) {
            float ratio = ((float) hero.getCurrentHealth()
                    / hero.getMaxHealth());

            shapeRenderer.setColor(getHealthColor(ratio * 100));

            shapeRenderer.rect(
                    healthBarLabel.getX() + 70,
                    healthBarLabel.getY() + 5,
                    BattleFieldUIConfig.HERO_HEALTH_BAR_WIDTH * 5 * ratio,
                    BattleFieldUIConfig.HERO_HEALTH_BAR_HEIGHT * 3);
        } else {
            float ratio = hero.getTimeAfterDeath()
                    / hero.getRespawnTime();

            shapeRenderer.setColor(Color.WHITE);

            shapeRenderer.rect(
                    healthBarLabel.getX() + 70,
                    healthBarLabel.getY() + 5,
                    BattleFieldUIConfig.HERO_HEALTH_BAR_WIDTH * 5 * ratio,
                    BattleFieldUIConfig.HERO_HEALTH_BAR_HEIGHT * 3);
        }
    }

    private void renderShootLoadBar(Hero hero) {
        shapeRenderer.setColor(Color.BLACK);

        shapeRenderer.rect(
                shootLoadBarLabel.getX() + 68,
                shootLoadBarLabel.getY() + 3,
                BattleFieldUIConfig.HERO_HEALTH_BAR_WIDTH * 5 + 4,
                BattleFieldUIConfig.HERO_HEALTH_BAR_HEIGHT * 3 + 4);


        float ratio;

        if (!hero.isLoaded()) {
            ratio =  hero.getLoadTime() / hero.getReloadTime();
        } else {
            ratio = 1;
        }

        shapeRenderer.setColor(getHealthColor(ratio * 100));

        shapeRenderer.rect(
                shootLoadBarLabel.getX() + 70,
                shootLoadBarLabel.getY() + 5,
                BattleFieldUIConfig.HERO_HEALTH_BAR_WIDTH * 5 * ratio,
                BattleFieldUIConfig.HERO_HEALTH_BAR_HEIGHT * 3);
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
        if (hero.isAlive()) {
            final int MOUSE_COLLISION_RADIUS = 30;

            boolean moved = false;

            float oldX = hero.getX();
            float oldY = hero.getY();

            boolean aKeyPressed = Gdx.input.isKeyPressed(Input.Keys.A);
            boolean wKeyPressed = Gdx.input.isKeyPressed(Input.Keys.W);
            boolean sKeyPressed = Gdx.input.isKeyPressed(Input.Keys.S);
            boolean dKeyPressed = Gdx.input.isKeyPressed(Input.Keys.D);

            boolean escapePressed = Gdx.input.isKeyPressed(Input.Keys.ESCAPE);

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

            if (escapePressed) {
                isEnd = true;

                networkManager.leaveGame();
            }
        }

    }

    private void initInputProcessor() {
        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    if (hero.isAlive() && !hero.isKilled() && hero.isLoaded()) {
                        networkManager.shootHeroRequest(hero.getX(), hero.getY(), hero.getRotation());
                        return true;
                    }
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
        for (int i = 0; i < AppConfig.MAX_TEAM_PLAYERS_AMOUNT; i++) {
            if (sunHeroes[i] != null) {
                for (Bullet bullet : sunHeroes[i].getBullets()) {
                    bullet.move();
                }
            }
            if (moonHeroes[i] != null) {
                for (Bullet bullet : moonHeroes[i].getBullets()) {
                    bullet.move();
                }
            }
        }
    }

    private void checkCollisions() {
        for (int i = 0; i < AppConfig.MAX_TEAM_PLAYERS_AMOUNT; i++) {

            if (sunHeroes[i] != null) {
                Iterator<Bullet> bulletIterator = sunHeroes[i].getBullets().iterator();
                while (bulletIterator.hasNext()) {
                    Bullet bullet = bulletIterator.next();
                    bullet.move();

                    if (bullet.isFinished()
                            || collisionDetector.collidesWithLayer(indestructibleTiledMapLayer, bullet.getCollisionModel())) {

                        bulletIterator.remove();
                    } else if (collisionDetector.isCollision(bullet, moonFortress)) {

                        if (isHost) {
                            networkManager.hitFortressEventRequest("SUN", i);
                        }

                        bulletIterator.remove();
                    } else {
                        for (int j = 0; j < AppConfig.MAX_TEAM_PLAYERS_AMOUNT; j++) {
                            if ((moonHeroes[j] != null) && moonHeroes[j].isAlive()) {
                                if (collisionDetector.isCollision(bullet, moonHeroes[j])) {

                                    if (isHost) {
                                        networkManager.hitHeroEventRequest("SUN", i, j);
                                        boolean isAlive = (moonHeroes[j].getCurrentHealth() - sunHeroes[i].getDamage()) <= 0;
                                        if (isAlive) {
                                            networkManager.killHero("SUN", i, "MOON", j);
                                        }
                                    }

                                    bulletIterator.remove();

                                    break;
                                }
                            }
                        }
                    }
                }
            }

            if (moonHeroes[i] != null) {
                Iterator<Bullet> bulletIterator = moonHeroes[i].getBullets().iterator();
                while (bulletIterator.hasNext()) {
                    Bullet bullet = bulletIterator.next();
                    bullet.move();

                    if (bullet.isFinished()
                            || collisionDetector.collidesWithLayer(indestructibleTiledMapLayer, bullet.getCollisionModel())) {

                        bulletIterator.remove();
                    } else if (collisionDetector.isCollision(bullet, sunFortress)) {

                        if (isHost) {
                            networkManager.hitFortressEventRequest("MOON", i);
                        }

                        bulletIterator.remove();
                    } else {
                        for (int j = 0; j < AppConfig.MAX_TEAM_PLAYERS_AMOUNT; j++) {
                            if ((sunHeroes[j] != null) && sunHeroes[j].isAlive()) {
                                if (collisionDetector.isCollision(bullet, sunHeroes[j])) {

                                    if (isHost) {
                                        networkManager.hitHeroEventRequest("MOON", i, j);
                                        boolean isAlive = (sunHeroes[j].getCurrentHealth() - moonHeroes[i].getDamage()) <= 0;
                                        if (isAlive) {
                                            networkManager.killHero("MOON", i, "SUN", j);
                                        }
                                    }

                                    bulletIterator.remove();

                                    break;
                                }
                            }
                        }
                    }
                }
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
