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
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.zelfos.game.GameMain;
import entities.*;
import entities.enemies.*;
import entities.structures.*;
import hud.*;
import helpers.GameInfo;


import java.util.ArrayList;
import java.util.Iterator;

public class GameScene implements Screen, ContactListener, FlashRedManager, CoinManager, EntityManager, CollisionManager {

    private final GameMain game;
    private final Player player;
    private final OrthographicCamera camera;
    private final OrthographicCamera hudCamera;
    private WaveManager waveManager;
    private final ArrayList<Entity> entities = new ArrayList<>();
    private final CoinsHud coinsHud;
    private final CompassHud compassHud;
    private boolean shouldFlashRed = false;
    private final Texture fullScreenRedFlashTexture;
    private final CountdownHud countdownHud;
    private final int INTERMISSION_TIME = 10;
    private int secondsUntilNextWave = INTERMISSION_TIME;
    private final HealthBar healthBar;
    private Vector2 forceCameraTo;
    private final Inventory inventory;
    private int totalCoins = 0;
    private int currentWaveIndex = 0;
    private final WavesHud wavesHud;
    private MapManager mapManager;
    private BombsHud bombsHud;
    TiledMapRenderer tiledMapRenderer;


    public GameScene(GameMain game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(
                false,
                GameInfo.WIDTH,
                GameInfo.HEIGHT
        );

        coinsHud = new CoinsHud(this);


        player = new Player(0, 0, this, this, this);
        waveManager = new WaveManager(this, this, player);
        mapManager = new MapManager(this, waveManager, player, this, this);
        waveManager.setMapManager(mapManager);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(mapManager.getTiledMap(), 2);

        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(
                false,
                GameInfo.WIDTH,
                GameInfo.HEIGHT
        );
        wavesHud = new WavesHud(waveManager);

        Vector2 playerStart = mapManager.getMapPoint("PlayerSpawn");
        player.setX(playerStart.x);
        player.setY(playerStart.y);

        Pixmap pixmap = new Pixmap(GameInfo.WIDTH, GameInfo.HEIGHT, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(1, 0, 0, 0.4f));
        pixmap.fillRectangle(0, 0, GameInfo.WIDTH, GameInfo.HEIGHT);
        fullScreenRedFlashTexture = new Texture(pixmap);
        pixmap.dispose();

        healthBar = new HealthBar(player);
        inventory = new Inventory(player);
        countdownHud = new CountdownHud(waveManager);
        bombsHud = new BombsHud(player);

        compassHud = new CompassHud(this, player);

        waveManager.startIntermission();
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public ArrayList<Entity> getEnemies() {
        return getEntitiesByType(Enemy.class);
    }

    public boolean isCollidingWithMap(Collidable collidable) {
        TiledMapTileLayer groundLayer = (TiledMapTileLayer) mapManager.getTiledMap().getLayers().get("Ground");
        for (int j = 0; j < groundLayer.getHeight(); j++) {
            for (int i = 0; i < groundLayer.getWidth(); i++) {
                TiledMapTileLayer.Cell cell = groundLayer.getCell(j, i);
                if (cell != null) {
                    int id = cell.getTile().getId() - 1;
                    if (mapManager.getCollidableTiles().contains(id)) {
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
            ArrayList<Class> ignoreList = entity.getIgnoreClassList();
            boolean skip = false;
            for (Class clazz : ignoreList) {
                if (clazz.isInstance(collidable)) {
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

    public void setForceCameraTo(Vector2 forceCameraTo) {
        this.forceCameraTo = forceCameraTo;
    }

    public void update(float delta) {
        float originalX = player.getX();
        float originalY = player.getY();

        //player movement
        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);
        boolean up = Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W);
        boolean down = Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S);
        boolean enter = Gdx.input.isKeyPressed(Input.Keys.ENTER);
        boolean bombInput = Gdx.input.isKeyPressed(Input.Keys.B);
        boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);

        player.update(delta);

        if (enter && waveManager.isOnIntermission()) {
            waveManager.spawnNextWave();
        }

        if (bombInput && player.hasBombs()) {
            player.dropBomb();
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
            player.attack(getEntitiesByType(Enemy.class));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            player.dodge();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.NUM_1) && player.getHasPotion() && player.getLives() != player.getMaxLives()) {
            player.usePotion();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            player.special();
        }

        int size = entities.size();
        for (int i = 0; i < size; i++) {
            entities.get(i).update(delta);
        }

        removeDeadEntities(entities);

        if (forceCameraTo != null) {
            camera.position.x = forceCameraTo.x;
            camera.position.y = forceCameraTo.y;
        } else {
            camera.position.x = player.getX();
            camera.position.y = player.getY();
        }
        camera.update();

        if (player.isDead()) {
            game.showMainMenuScene();
        }

        if (((TownHall) getEntityByType(TownHall.class)).isDead()) {
            game.showMainMenuScene();
        }

        if (!waveManager.isOnIntermission() && getEntitiesByType(Enemy.class).size() <= 0) {
            if (waveManager.isOnFinalWave()) {
                game.showWinScreen();
            } else {
                waveManager.startIntermission();
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

        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);

        player.draw(batch, shapeRenderer);

        for (Entity entity : entities) {
            entity.draw(batch, shapeRenderer);
        }

        batch.setProjectionMatrix(hudCamera.combined);

        if (shouldFlashRed) {
            batch.begin();
            batch.draw(fullScreenRedFlashTexture, 0, 0);
            batch.end();
        }

        healthBar.draw(batch);
        inventory.draw(batch);
        wavesHud.draw(batch);
        coinsHud.draw(batch);
        bombsHud.draw(batch);
        compassHud.draw(batch);

        if (waveManager.isOnIntermission()) {
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
    public void createCoin(float x, float y) {
        entities.add(new Coin(x, y, player, this));
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
    public Entity getEntityByType(Class clazz) {
        for (Entity entity : entities) {
            if (clazz.isInstance(entity)) {
                return entity;
            }
        }
        return null;
    }

    @Override
    public ArrayList<Entity> getEntitiesByType(Class clazz) {
        ArrayList<Entity> entitiesByType = new ArrayList<>();
        for (Entity entity : entities) {
            if (clazz.isInstance(entity)) {
                entitiesByType.add(entity);
            }
        }
        return entitiesByType;
    }

}
