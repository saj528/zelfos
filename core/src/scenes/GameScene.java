package scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.zelfos.game.GameMain;
import entities.Enemy;
import helpers.GameInfo;
import entities.Crate;
import entities.Player;


import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class GameScene implements Screen, ContactListener {

    private GameMain game;
    private Player player;
    private OrthographicCamera camera;
    private Crate crate;
    private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    TiledMap tiledMap;
    TiledMapRenderer tiledMapRenderer;

    private HashSet<Integer> collidableTiles;


    public GameScene(GameMain game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(
                false,
                GameInfo.WIDTH,
                GameInfo.HEIGHT
        );
        player = new Player(
                1500,
                1300
        );
        crate = new Crate(100, 100);

        ArrayList<Vector2> pathwayCoordinates = new ArrayList<>();
        pathwayCoordinates.add(new Vector2(1500, 900));
        pathwayCoordinates.add(new Vector2(1500, 1100));
        pathwayCoordinates.add(new Vector2(-100, -100));

        enemies.add(new Enemy(1500, 1200, pathwayCoordinates,player));
        //enemies.add(new Enemy(1500, 600, pathwayCoordinates,player));
        //enemies.add(new Enemy(1500, 500, pathwayCoordinates,player));

        tiledMap = new TmxMapLoader().load("map.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 2);

        collidableTiles = new HashSet<Integer>();
        collidableTiles.add(156);
        collidableTiles.add(157);
        collidableTiles.add(158);
        collidableTiles.add(168);
        collidableTiles.add(169);
        collidableTiles.add(170);
        collidableTiles.add(180);
        collidableTiles.add(181);
        collidableTiles.add(182);
    }

    public boolean isOverlappingCrate() {
        Rectangle playerRect = player.getBoundingRectangle();
        Rectangle createRect = crate.getBoundingRectangle();
        return playerRect.overlaps(createRect);
    }

    private boolean isCollidingWithMap() {
        TiledMapTileLayer groundLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Ground");
        for (int j = 0; j < groundLayer.getHeight(); j++) {
            System.out.println();
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
        boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);

        player.update(delta);

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

        for (Enemy enemy : enemies) {
            enemy.update();
        }

        Iterator<Enemy> iter = enemies.iterator();
        while (iter.hasNext()) {
            Enemy enemy = iter.next();
            if (enemy.isDead()) {
                iter.remove();
            }
        }

        camera.position.x = player.getX();
        camera.position.y = player.getY();
        camera.update();
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

        batch.begin();
        crate.draw(batch);
        batch.end();

        Iterator<Enemy> iter = enemies.iterator();
        while (iter.hasNext()) {
            Enemy enemy = iter.next();
            enemy.draw(batch);
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
}
