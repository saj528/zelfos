package scenes.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.zelfos.game.GameMain;
import entities.*;
import entities.enemies.Archer;
import entities.enemies.Footman;
import entities.enemies.Hornet;
import entities.enemies.Porcupine;
import entities.structures.Barracks;
import entities.structures.PotionShop;
import entities.structures.TownHall;
import hud.*;
import helpers.GameInfo;
import particles.Particle;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;

public class GameScene implements Screen, ContactListener, BombManager, EnemyManager, LeakManager, FlashRedManager, ArrowManager, WaveManager, CoinManager, EntityManager, CollisionManager, ParticleManager {

    private final GameMain game;
    private final Player player;
    private final OrthographicCamera camera;
    private final OrthographicCamera hudCamera;
    private final ArrayList<Entity> enemies = new ArrayList<>();
    private final ArrayList<Bomb> bombs = new ArrayList<>();
    private final ArrayList<Arrow> arrows = new ArrayList<>();
    private final ArrayList<Coin> coins = new ArrayList<>();
    private final ArrayList<Entity> entities = new ArrayList<>();
    private final ArrayList<Particle> particles = new ArrayList<>();
    private TownHall townHall;
    private final CoinsHud coinsHud;
    private final Leaks leaksHud;
    private final CompassHud compassHud;
    private boolean shouldFlashRed = false;
    private final Texture fullScreenRedFlashTexture;
    private boolean isOnIntermission = false;
    private final CountdownHud countdownHud;
    private final int INTERMISSION_TIME = 1;
    private int secondsUntilNextWave = INTERMISSION_TIME;
    private final HealthBar healthBar;
    private final Inventory inventory;
    private int totalCoins = 5;
    private int leaks = 10;
    private int currentWaveIndex = 0;
    private final WavesHud wavesHud;
    TiledMap tiledMap;
    TiledMapRenderer tiledMapRenderer;

    private ArrayList<Wave> waves;

    private final HashSet<Integer> collidableTiles;

    private Vector2 getMapObjectLocation(String name) {
        MapLayer waypoint = tiledMap.getLayers().get("Waypoint");
        MapObjects objects = waypoint.getObjects();
        RectangleMapObject obj = (RectangleMapObject) objects.get(name);
        Vector2 point = new Vector2(obj.getRectangle().x * 2, obj.getRectangle().y * 2);
        return point;
    }

    private Rectangle getMapObjectRectangle(String name) {
        MapLayer waypoint = tiledMap.getLayers().get("Waypoint");
        MapObjects objects = waypoint.getObjects();
        RectangleMapObject obj = (RectangleMapObject) objects.get(name);
        return new Rectangle(obj.getRectangle().x * 2, obj.getRectangle().y * 2, obj.getRectangle().width * 2, obj.getRectangle().height * 2);
    }

    private void setupWaves() {
        waves = new ArrayList<>();
        ArrayList<EnemySet> enemySets;

        enemySets = new ArrayList<>();
        enemySets.add(new EnemySet(EnemySet.EnemyType.HORNET, 1, EnemySet.Lane.NORTH));
        waves.add(new Wave(enemySets));

        enemySets = new ArrayList<>();
        enemySets.add(new EnemySet(EnemySet.EnemyType.SOLDIER, 2, EnemySet.Lane.NORTH));
        enemySets.add(new EnemySet(EnemySet.EnemyType.ARCHER, 1, EnemySet.Lane.NORTH));
        waves.add(new Wave(enemySets));

        enemySets = new ArrayList<>();
        enemySets.add(new EnemySet(EnemySet.EnemyType.SOLDIER, 4, EnemySet.Lane.NORTH));
        enemySets.add(new EnemySet(EnemySet.EnemyType.ARCHER, 3, EnemySet.Lane.NORTH));
        waves.add(new Wave(enemySets));

        enemySets = new ArrayList<>();
        enemySets.add(new EnemySet(EnemySet.EnemyType.HORNET, 5, EnemySet.Lane.NORTH));
        enemySets.add(new EnemySet(EnemySet.EnemyType.SOLDIER, 2, EnemySet.Lane.NORTH));
        waves.add(new Wave(enemySets));
    }

    public GameScene(GameMain game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(
                false,
                GameInfo.WIDTH,
                GameInfo.HEIGHT
        );

        setupWaves();

        coinsHud = new CoinsHud(this);

        wavesHud = new WavesHud(this);

        tiledMap = new TmxMapLoader().load("map.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 2);


        MapLayer waypoint = tiledMap.getLayers().get("Waypoint");
        MapObjects objects = waypoint.getObjects();

        RectangleMapObject end = (RectangleMapObject) objects.get("End");
        Vector2 endPoint = new Vector2(end.getRectangle().x * 2, end.getRectangle().y * 2);

        RectangleMapObject playerSpawn = (RectangleMapObject) objects.get("PlayerSpawn");
        Vector2 playerSpawnPoint = new Vector2(playerSpawn.getRectangle().x * 2, playerSpawn.getRectangle().y * 2);

        Vector2 eastRocksPoint0 = getMapObjectLocation("EastRocks0");
        entities.add(new Rocks(eastRocksPoint0.x, eastRocksPoint0.y));
        Vector2 eastRocksPoint1 = getMapObjectLocation("EastRocks1");
        entities.add(new Rocks(eastRocksPoint1.x, eastRocksPoint1.y));
        Vector2 eastRocksPoint2 = getMapObjectLocation("EastRocks2");
        entities.add(new Rocks(eastRocksPoint2.x, eastRocksPoint2.y));


        DeadZone deadZone = new DeadZone(getMapObjectRectangle("NorthDeadZone"));
        System.out.println(deadZone.getHitbox());
        entities.add(deadZone);

        player = new Player(playerSpawnPoint.x, playerSpawnPoint.y, this, this, this, this);

        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(
                false,
                GameInfo.WIDTH,
                GameInfo.HEIGHT
        );

        //pathwayCoordinates.add(new Vector2(1500, 1100));
        //pathwayCoordinates.add(new Vector2(-100, -100));

        Pixmap pixmap = new Pixmap(GameInfo.WIDTH, GameInfo.HEIGHT, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(1, 0, 0, 0.4f));
        pixmap.fillRectangle(0, 0, GameInfo.WIDTH, GameInfo.HEIGHT);
        fullScreenRedFlashTexture = new Texture(pixmap);
        pixmap.dispose();

        //enemies.add(new Enemy(start.getRectangle().x + 700, start.getRectangle().y + 1000, pathwayCoordinates,player));
        //enemies.add(new Enemy(1500, 500, pathwayCoordinates,player));


        //int objectLayerId = 5;
        //TiledMapTileLayer collisionObjectLayer = (TiledMapTileLayer)map.getLayers().get(objectLayerId);
        // MapObjects objects = collisionObjectLayer.getObjects();

        //(TiledMapTileLayer)map.getLayers().get(objectLayerId);
        // MapObjects objects = collisionObjectLayer.getObjects();
        Vector2 northGuardPost = getMapObjectLocation("NorthGuardPost");
        Vector2 northBasePost = getMapObjectLocation("NorthBasePost");
        Vector2 barracksPoint = getMapObjectLocation("Barracks");
        entities.add(new Barracks(barracksPoint.x, barracksPoint.y, player, this, this, northGuardPost, northBasePost, this));

        Vector2 clericsPoint = getMapObjectLocation("Cleric");
        entities.add(new Cleric(clericsPoint.x, clericsPoint.y, player, this));

        Vector2 potionShopPoint = getMapObjectLocation("Potion");
        entities.add(new PotionShop(potionShopPoint.x, potionShopPoint.y, player, this,this,this));

        collidableTiles = new HashSet<>();
        collidableTiles.add(156);
        collidableTiles.add(157);
        collidableTiles.add(158);
        collidableTiles.add(168);
        collidableTiles.add(169);
        collidableTiles.add(170);
        collidableTiles.add(180);
        collidableTiles.add(181);
        collidableTiles.add(182);


        townHall = new TownHall(endPoint.x, endPoint.y);
        entities.add(townHall);

        healthBar = new HealthBar(player);
        inventory = new Inventory(player);

        leaksHud = new Leaks(this, GameInfo.WIDTH / 2f, GameInfo.HEIGHT - 35);

        countdownHud = new CountdownHud(this);

        compassHud = new CompassHud(this, player);

        startIntermission();
    }

    @Override
    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void spawnNextWave() {
        final LeakManager leakManager = this;
        final ArrowManager arrowManager = this;

        MapLayer waypoint = tiledMap.getLayers().get("Waypoint");
        MapObjects objects = waypoint.getObjects();

        RectangleMapObject north = (RectangleMapObject) objects.get("North");
        Vector2 northPoint = new Vector2((int) north.getRectangle().x * 2, (int) north.getRectangle().y * 2);

        RectangleMapObject south = (RectangleMapObject) objects.get("South");
        Vector2 southPoint = new Vector2((int) south.getRectangle().x * 2, (int) south.getRectangle().y * 2);

        RectangleMapObject east = (RectangleMapObject) objects.get("East");
        Vector2 eastPoint = new Vector2((int) east.getRectangle().x * 2, (int) east.getRectangle().y * 2);

        RectangleMapObject west = (RectangleMapObject) objects.get("West");
        Vector2 westPoint = new Vector2((int) west.getRectangle().x * 2, (int) west.getRectangle().y * 2);

        RectangleMapObject end = (RectangleMapObject) objects.get("End");
        Vector2 endPoint = new Vector2((int) end.getRectangle().x * 2, (int) end.getRectangle().y * 2);

        Vector2[] points = new Vector2[4];
        points[0] = northPoint;
        points[1] = southPoint;
        points[2] = eastPoint;
        points[3] = westPoint;

        Wave currentWave = waves.get(currentWaveIndex);
        for (EnemySet enemySet : currentWave.getEnemySets()) {
            for (int i = 0; i < enemySet.getCount(); i++) {
                int offsetSize = 200;
                int ox = (int) (Math.random() * offsetSize) - offsetSize / 2;
                int oy = (int) (Math.random() * offsetSize) - offsetSize / 2;

                ArrayList<Vector2> pathwayCoordinates = new ArrayList<>();
                pathwayCoordinates.add(new Vector2(endPoint.x + ox, endPoint.y + oy));

                Vector2 spawnPoint;
                switch (enemySet.getLane()) {
                    case NORTH:
                        spawnPoint = points[0];
                        break;
                    case SOUTH:
                        spawnPoint = points[1];
                        break;
                    case EAST:
                        spawnPoint = points[2];
                        break;
                    case WEST:
                        spawnPoint = points[3];
                        break;
                    default:
                        spawnPoint = points[0];
                        break;
                }

                switch (enemySet.getEnemyType()) {
                    case SOLDIER:
                        enemies.add(new Footman(spawnPoint.x + ox, spawnPoint.y + oy, pathwayCoordinates, player, leakManager, this, this, this));
                        break;
                    case ARCHER:
                        enemies.add(new Archer(spawnPoint.x + ox, spawnPoint.y + oy, pathwayCoordinates, player, leakManager, arrowManager, this));
                        break;
                    case HORNET:
                        enemies.add(new Hornet(spawnPoint.x + ox, spawnPoint.y + oy, pathwayCoordinates, player, leakManager, this, this, this, this));
                        break;
                    case PORCUPINE:
                        enemies.add(new Porcupine(spawnPoint.x + ox, spawnPoint.y + oy, pathwayCoordinates, player, leakManager, this, this));
                        break;
                }
            }
        }
    }

    public ArrayList<Entity> getEnemies() {
        return enemies;
    }

    public void createBomb(float x, float y) {
        bombs.add(new Bomb(player.getX(), player.getY(), this, this));
    }

    public boolean isCollidingWithMap(Collidable collidable) {
        TiledMapTileLayer groundLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Ground");
        for (int j = 0; j < groundLayer.getHeight(); j++) {
            for (int i = 0; i < groundLayer.getWidth(); i++) {
                TiledMapTileLayer.Cell cell = groundLayer.getCell(j, i);
                if (cell != null) {
                    int id = cell.getTile().getId() - 1;
                    if (collidableTiles.contains(id)) {
                        Rectangle tileRectangle = new Rectangle(j * 32, i * 32, 32, 32);
                        Rectangle playerRectangle = collidable.getHitbox();

                        if (playerRectangle.overlaps(tileRectangle)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isCollidingWithOtherCollidables(Collidable entity) {
        for (Collidable collidable : getCollidables()) {
            if (collidable == entity) continue;
            ArrayList<Class> ignoreList = collidable.getIgnoreClassList();
            boolean skip = false;
            for (Class clazz : ignoreList) {
                if (clazz.isInstance(entity)) {
                    skip = true;
                    break;
                }
            }
            if (skip) continue;
            Rectangle entityHitbox = entity.getHitbox();
            Rectangle collidableHitbox = collidable.getHitbox();
            if (entityHitbox.overlaps(collidableHitbox)) {
                return true;
            }
        }
        return false;
    }

    public void removeDeadEntities(ArrayList killables) {
        Iterator iterator = killables.iterator();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof Killable) {
                if (((Killable) next).isDead()) {
                    iterator.remove();
                }
            }
        }
    }

    public void update(float delta) {
        float originalX = player.getX();
        float originalY = player.getY();

        //player movement
        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);
        boolean up = Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W);
        boolean down = Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S);
        boolean bombInput = Gdx.input.isKeyPressed(Input.Keys.B);
        boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);

        player.update(delta);

        if (bombInput && player.hasBombs()) {
            player.dropBomb(this);
        }

        player.updatePlayerMovement(left, right, up, down, shift, delta);

        player.updateX(delta);
        if (isCollidingWithMap(player) || isCollidingWithOtherCollidables(player)) {
            player.setX(originalX);
        }

        player.updateY(delta);
        if (isCollidingWithMap(player) || isCollidingWithOtherCollidables(player)) {
            player.setY(originalY);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            player.attack(enemies);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            player.dodge();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.NUM_1) && player.getHasPotion() && player.getLives() != player.getMaxLives()) {
            player.usePotion();
        }

        for (Entity enemy : enemies) {
            enemy.update(delta);
        }

        for (Arrow arrow : arrows) {
            arrow.update(delta);
        }

        for (Coin coin : coins) {
            coin.update(delta);
        }

        for (Particle particle : particles) {
            particle.update(delta);
        }

        int size = entities.size();
        for (int i = 0; i < size; i++) {
            entities.get(i).update(delta);
        }

        removeDeadEntities(enemies);
        removeDeadEntities(bombs);
        removeDeadEntities(arrows);
        removeDeadEntities(coins);
        removeDeadEntities(particles);
        removeDeadEntities(entities);

        camera.position.x = player.getX();
        camera.position.y = player.getY();
        camera.update();

        if (player.isDead()) {
            game.showMainMenuScene();
        }

        if (townHall.isDead()) {
            game.showMainMenuScene();
        }

        if (!isOnIntermission && enemies.size() <= 0) {
            currentWaveIndex++;
            if (currentWaveIndex >= waves.size()) {
                // you won
                game.showWinScreen();
            } else {
                startIntermission();
            }
        }

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update(delta);

        Batch batch = game.getBatch();
        Gdx.gl.glClearColor(0, 1, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.setShader(null);


        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        player.draw(batch);

        for (Entity enemy : enemies) {
            enemy.draw(batch);
        }

        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);

        for (Bomb bomb : bombs) {
            bomb.draw(batch, shapeRenderer);
        }

        for (Arrow arrow : arrows) {
            arrow.draw(batch);
        }

        for (Coin coin : coins) {
            coin.draw(batch);
        }

        for (Particle particle : particles) {
            particle.draw(batch);
        }

        for (Entity entity : entities) {
            entity.draw(batch);
        }

//        townHall.draw(batch);

        batch.setProjectionMatrix(hudCamera.combined);

        if (shouldFlashRed) {
            batch.begin();
            batch.draw(fullScreenRedFlashTexture, 0, 0);
            batch.end();
        }

        healthBar.draw(batch);
        inventory.draw(batch);
//        leaksHud.draw(batch);
        wavesHud.draw(batch);
        coinsHud.draw(batch);
        compassHud.draw(batch);

        if (isOnIntermission) {
            countdownHud.draw(batch);
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        player.getTexture().dispose();
        tiledMap.dispose();
    }

    @Override
    public void beginContact(Contact contact) {

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    @Override
    public void removeLeak() {
        leaks--;
        flashRed(0.5f);
        if (leaks < 0) {
            game.showMainMenuScene();
        }
    }

    @Override
    public int getLeaks() {
        return leaks;
    }

    @Override
    public void flashRed(float time) {
        shouldFlashRed = true;
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                shouldFlashRed = false;
            }
        }, time);
    }

    @Override
    public void createArrow(float x, float y, float angle) {
        arrows.add(new Arrow(x, y, angle, player, this));
    }

    @Override
    public int getCurrentWave() {
        return currentWaveIndex + 1;
    }

    @Override
    public int getSecondsUntilNextWave() {
        return secondsUntilNextWave;
    }

    @Override
    public void startIntermission() {
        isOnIntermission = true;
        secondsUntilNextWave = INTERMISSION_TIME;

        final Timer.Task countdown = Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                secondsUntilNextWave--;
            }
        }, 0f, 1.0f);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                isOnIntermission = false;
                countdown.cancel();
                spawnNextWave();
            }
        }, INTERMISSION_TIME);
    }

    @Override
    public void createCoin(float x, float y) {
        coins.add(new Coin(x, y, player, this));
    }

    @Override
    public void incrementCoins(int amount) {
        totalCoins += amount;
    }

    @Override
    public int getTotalCoins() {
        return totalCoins;
    }

    @Override
    public void removeCoins(int amount) {
        totalCoins -= amount;
    }

    @Override
    public ArrayList<Collidable> getCollidables() {
        ArrayList<Collidable> collidables = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity instanceof Collidable) {
                collidables.add((Collidable) entity);
            }
        }
        return collidables;
    }

    @Override
    public Entity getEntityByType(String className) {
        for (Entity entity : entities) {
            if (entity.getClass().getName().equals(className)) {
                return entity;
            }
        }
        return null;
    }

    @Override
    public void addParticle(Particle particle) {
        particles.add(particle);
    }
}
