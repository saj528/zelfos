package scenes.game;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import entities.DeadZone;
import entities.Player;
import entities.Rocks;
import entities.structures.*;

import java.util.HashSet;

public class MapManager {
    private final EntityManager entityManager;
    private final WaveManager waveManager;
    private final Player player;
    private TiledMap tiledMap;
    private final HashSet<Integer> collidableTiles;

    public MapManager(EntityManager entityManager, WaveManager waveManager, Player player, CoinManager coinManager, CollisionManager collisionManager) {
        this.entityManager = entityManager;
        this.waveManager = waveManager;
        this.player = player;

        tiledMap = new TmxMapLoader().load("map.tmx");

        setupRocks("EastRocks0", Rocks.Direction.EAST);
        setupRocks("EastRocks1", Rocks.Direction.EAST);
        setupRocks("EastRocks2", Rocks.Direction.EAST);

        setupRocks("WestRocks0", Rocks.Direction.WEST);
        setupRocks("WestRocks1", Rocks.Direction.WEST);
        setupRocks("WestRocks2", Rocks.Direction.WEST);

        setupRocks("SouthRocks0", Rocks.Direction.SOUTH);
        setupRocks("SouthRocks1", Rocks.Direction.SOUTH);
        setupRocks("SouthRocks2", Rocks.Direction.SOUTH);

        DeadZone deadZone = new DeadZone(getMapObjectRectangle("NorthDeadZone"));
        entityManager.addEntity(deadZone);

        Vector2 northGuardPost = getMapObjectLocation("NorthGuardPost");
        Vector2 northBasePost = getMapObjectLocation("NorthBasePost");
        Vector2 barracksPoint = getMapObjectLocation("Barracks");
        entityManager.addEntity(new Barracks(barracksPoint.x, barracksPoint.y, player, coinManager, entityManager, northGuardPost, northBasePost, collisionManager, waveManager));

        Vector2 clericsPoint = getMapObjectLocation("Cleric");
        entityManager.addEntity(new Cleric(clericsPoint.x, clericsPoint.y, player, coinManager, waveManager));

        Vector2 potionShopPoint = getMapObjectLocation("Potion");
        entityManager.addEntity(new PotionShop(potionShopPoint.x, potionShopPoint.y, player, coinManager, entityManager, collisionManager, waveManager));

        Vector2 bombShopPoint = getMapObjectLocation("BombShop");
        entityManager.addEntity(new BombShop(bombShopPoint.x, bombShopPoint.y, player, coinManager, entityManager, collisionManager, waveManager));

        TownHall townHall = new TownHall(getMapObjectLocation("End").x, getMapObjectLocation("End").y, waveManager, player);
        entityManager.addEntity(townHall);

        Tower northTower0 = new Tower(getMapObjectLocation("NorthTower0").x, getMapObjectLocation("NorthTower0").y, entityManager, collisionManager);
        entityManager.addEntity(northTower0);

        Tower northTower1 = new Tower(getMapObjectLocation("NorthTower1").x, getMapObjectLocation("NorthTower1").y, entityManager, collisionManager);
        entityManager.addEntity(northTower1);

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
    }

    public HashSet<Integer> getCollidableTiles() {
        return collidableTiles;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

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

    public void setupRocks(String name, Rocks.Direction direction) {
        Vector2 point = getMapPoint(name);
        entityManager.addEntity(new Rocks(point.x, point.y, direction));
    }

    public Vector2 getMapPoint(String name) {
        MapLayer waypoint = tiledMap.getLayers().get("Waypoint");
        MapObjects objects = waypoint.getObjects();
        RectangleMapObject end = (RectangleMapObject) objects.get(name);
        return new Vector2(end.getRectangle().x * 2, end.getRectangle().y * 2);
    }


}
