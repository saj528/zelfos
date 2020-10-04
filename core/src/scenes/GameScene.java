package scenes;

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
import helpers.EnemySet;
import helpers.GameInfo;
import helpers.Wave;
import helpers.WaveManager;
import hud.Leaks;
import hud.WavesHud;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class GameScene implements Screen, ContactListener, BombManager, EnemyManager, LeakManager, FlashRedManager, ArrowManager, WaveManager {

    private final GameMain game;
    private final Player player;
    private final OrthographicCamera camera;
    private final OrthographicCamera hudCamera;
    private final ArrayList<EnemyInterface> enemies = new ArrayList<>();
    private final ArrayList<Bomb> bombs = new ArrayList<>();
    private final ArrayList<Arrow> arrows = new ArrayList<>();
    private final Leaks leaksHud;
    private boolean shouldFlashRed = false;
    private final Texture fullScreenRedFlashTexture;
    private final HealthBar healthBar;
    private int leaks = 10;
    private int currentWaveIndex = 0;
    private float SPAWN_DELAY = 0.5f;
    private WavesHud wavesHud;
    TiledMap tiledMap;
    TiledMapRenderer tiledMapRenderer;

    private Wave[] waves;

    private final HashSet<Integer> collidableTiles;

    public GameScene(GameMain game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(
                false,
                GameInfo.WIDTH,
                GameInfo.HEIGHT
        );

        waves = new Wave[2];
        EnemySet[] wave0EnemySets = new EnemySet[2];
        wave0EnemySets[0] = new EnemySet(EnemySet.EnemyType.SOLDIER, 1);
        wave0EnemySets[1] = new EnemySet(EnemySet.EnemyType.ARCHER, 1);
        waves[0] = new Wave(wave0EnemySets);

        EnemySet[] wave1EnemySets = new EnemySet[2];
        wave1EnemySets[0] = new EnemySet(EnemySet.EnemyType.SOLDIER, 5);
        wave1EnemySets[1] = new EnemySet(EnemySet.EnemyType.ARCHER, 2);
        waves[1] = new Wave(wave1EnemySets);

        wavesHud = new WavesHud(this);

        tiledMap = new TmxMapLoader().load("map.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 2);

        MapLayer waypoint = tiledMap.getLayers().get("Waypoint");
        MapObjects objects = waypoint.getObjects();

        RectangleMapObject start = (RectangleMapObject) objects.get("Start");
        Vector2 startPoint = new Vector2((int)start.getRectangle().x * 2, (int)start.getRectangle().y * 2);
        RectangleMapObject end = (RectangleMapObject) objects.get("End");
        Vector2 endPoint = new Vector2((int)end.getRectangle().x * 2, (int)end.getRectangle().y * 2);

        player = new Player(endPoint.x, endPoint.y, this);

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

        spawnNextWave();


        //int objectLayerId = 5;
        //TiledMapTileLayer collisionObjectLayer = (TiledMapTileLayer)map.getLayers().get(objectLayerId);
       // MapObjects objects = collisionObjectLayer.getObjects();

        //(TiledMapTileLayer)map.getLayers().get(objectLayerId);
        // MapObjects objects = collisionObjectLayer.getObjects();



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

        healthBar = new HealthBar(player);

        leaksHud = new Leaks(this, GameInfo.WIDTH / 2f, GameInfo.HEIGHT - 35);
    }



    public void spawnNextWave() {
        final LeakManager leakManager = this;
        final ArrowManager arrowManager = this;

        MapLayer waypoint = tiledMap.getLayers().get("Waypoint");
        MapObjects objects = waypoint.getObjects();

        RectangleMapObject start = (RectangleMapObject) objects.get("Start");
        Vector2 startPoint = new Vector2((int)start.getRectangle().x * 2, (int)start.getRectangle().y * 2);
        RectangleMapObject end = (RectangleMapObject) objects.get("End");
        Vector2 endPoint = new Vector2((int)end.getRectangle().x * 2, (int)end.getRectangle().y * 2);


        Wave currentWave = waves[currentWaveIndex];
        for (EnemySet enemySet : currentWave.getEnemySets()) {
            for (int i = 0; i < enemySet.getCount(); i++) {
                int offsetSize = 200;
                int ox = (int)(Math.random() * offsetSize) - offsetSize / 2;
                int oy = (int)(Math.random() * offsetSize) - offsetSize / 2;

                ArrayList<Vector2> pathwayCoordinates = new ArrayList<>();
                pathwayCoordinates.add(new Vector2(endPoint.x + ox, endPoint.y + oy));

                switch(enemySet.getEnemyType()) {
                    case SOLDIER:
                        enemies.add(new Enemy(startPoint.x + ox, startPoint.y + oy, pathwayCoordinates, player, leakManager));
                        break;
                    case ARCHER:
                        enemies.add(new Archer(startPoint.x + ox, startPoint.y + oy, pathwayCoordinates, player, leakManager, arrowManager));
                        break;
                }
            }
        }
    }

    public ArrayList<EnemyInterface> getEnemies() {
        return enemies;
    }

    public void createBomb(float x, float y) {
        bombs.add(new Bomb(player.getX(), player.getY(), this));
    }

    private boolean isCollidingWithMap() {
        TiledMapTileLayer groundLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Ground");
        for (int j = 0; j < groundLayer.getHeight(); j++) {
            for (int i = 0; i < groundLayer.getWidth(); i++) {
                TiledMapTileLayer.Cell cell = groundLayer.getCell(j, i);
                if (cell != null) {
                    int id = cell.getTile().getId() - 1;
                    if (collidableTiles.contains(id)) {
                        Rectangle tileRectangle = new Rectangle(j * 32, i * 32, 32, 32);
                        Rectangle playerRectangle = player.getBoundingRectangle();

                        if (playerRectangle.overlaps(tileRectangle)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
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
        if (isCollidingWithMap()) {
            player.setX(originalX);
        }

        player.updateY(delta);
        if (isCollidingWithMap()) {
            player.setY(originalY);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            player.attack(enemies);
        }

        for (EnemyInterface enemy : enemies) {
            enemy.update();
        }

        Iterator<EnemyInterface> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            EnemyInterface enemy = iterator.next();
            if (enemy.isDead()) {
                iterator.remove();
            }
        }

        Iterator<Bomb> bombIterator = bombs.iterator();
        while (bombIterator.hasNext()) {
            Bomb bomb = bombIterator.next();
            if (bomb.isDead()) {
                bombIterator.remove();
            }
        }

        for (Arrow arrow : arrows) {
            arrow.update(delta);
        }

        Iterator<Arrow> arrowIterator = arrows.iterator();
        while (arrowIterator.hasNext()) {
            Arrow arrow = arrowIterator.next();
            if (arrow.isDead()) {
                arrowIterator.remove();
            }
        }

        camera.position.x = player.getX();
        camera.position.y = player.getY();
        camera.update();

        if (player.isDead()) {
            game.showMainMenuScene();
        }

        if (enemies.size() <= 0) {
            currentWaveIndex++;
            if (currentWaveIndex >= waves.length) {
                // you won
                game.showWinScreen();
            } else {
                spawnNextWave();
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

        for (EnemyInterface enemy : enemies) {
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

        batch.setProjectionMatrix(hudCamera.combined);

        if (shouldFlashRed) {
            batch.begin();
            batch.draw(fullScreenRedFlashTexture, 0, 0);
            batch.end();
        }

        healthBar.draw(batch);
        leaksHud.draw(batch);
        wavesHud.draw(batch);

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
        for (EnemyInterface enemy : enemies) {
            enemy.getTexture().dispose();
        }
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
        arrows.add(new Arrow(x, y, angle, player));
    }

    @Override
    public int getCurrentWave() {
        return currentWaveIndex + 1;
    }
}
