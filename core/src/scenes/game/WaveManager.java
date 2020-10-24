package scenes.game;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import entities.Player;
import entities.Rocks;
import entities.enemies.*;

import java.util.ArrayList;
import java.util.List;

public class WaveManager {

    private final EntityManager entityManager;
    private final Player player;
    private MapManager mapManager;
    private ArrayList<Wave> waves;
    private int currentWaveIndex = 0;
    private boolean isOnIntermission = false;
    private GameScene gameScene;

    public WaveManager(EntityManager entityManager, GameScene gameScene, Player player) {
        this.entityManager = entityManager;
        this.gameScene = gameScene;
        this.player = player;

        waves = new ArrayList<>();
        ArrayList<EnemySet> enemySets;

        // wave 1
        enemySets = new ArrayList<>();
        enemySets.add(new EnemySet(EnemySet.EnemyType.PORCUPINE, 1, EnemySet.Lane.NORTH));
        waves.add(new Wave(enemySets));

        // wave 2
        enemySets = new ArrayList<>();
        enemySets.add(new EnemySet(EnemySet.EnemyType.PORCUPINE, 2, EnemySet.Lane.NORTH));
        waves.add(new Wave(enemySets));

        // wave 3
        enemySets = new ArrayList<>();
        enemySets.add(new EnemySet(EnemySet.EnemyType.HORNET, 2, EnemySet.Lane.NORTH));
        enemySets.add(new EnemySet(EnemySet.EnemyType.PORCUPINE, 1, EnemySet.Lane.NORTH));
        waves.add(new Wave(enemySets));

        // wave 4
        enemySets = new ArrayList<>();
        enemySets.add(new EnemySet(EnemySet.EnemyType.HORNET, 2, EnemySet.Lane.NORTH));
        enemySets.add(new EnemySet(EnemySet.EnemyType.PORCUPINE, 2, EnemySet.Lane.NORTH));
        waves.add(new Wave(enemySets));

        // wave 5
        enemySets = new ArrayList<>();
        enemySets.add(new EnemySet(EnemySet.EnemyType.HORNET, 3, EnemySet.Lane.NORTH));
        enemySets.add(new EnemySet(EnemySet.EnemyType.SOLDIER, 1, EnemySet.Lane.NORTH));
        waves.add(new Wave(enemySets));

        // wave 6
        enemySets = new ArrayList<>();
        enemySets.add(new EnemySet(EnemySet.EnemyType.SOLDIER, 2, EnemySet.Lane.NORTH));
        enemySets.add(new EnemySet(EnemySet.EnemyType.ARCHER, 1, EnemySet.Lane.NORTH));
        waves.add(new Wave(enemySets));

        // wave 7
        enemySets = new ArrayList<>();
        enemySets.add(new EnemySet(EnemySet.EnemyType.SOLDIER, 2, EnemySet.Lane.NORTH));
        enemySets.add(new EnemySet(EnemySet.EnemyType.HORNET, 1, EnemySet.Lane.NORTH));
        enemySets.add(new EnemySet(EnemySet.EnemyType.PORCUPINE, 1, EnemySet.Lane.NORTH));
        enemySets.add(new EnemySet(EnemySet.EnemyType.ARCHER, 1, EnemySet.Lane.NORTH));
        waves.add(new Wave(enemySets));

        // wave 8
        enemySets = new ArrayList<>();
        enemySets.add(new EnemySet(EnemySet.EnemyType.HORNET, 5, EnemySet.Lane.NORTH));
        enemySets.add(new EnemySet(EnemySet.EnemyType.ARCHER, 2, EnemySet.Lane.NORTH));
        waves.add(new Wave(enemySets));

        // wave 9
        enemySets = new ArrayList<>();
        enemySets.add(new EnemySet(EnemySet.EnemyType.SOLDIER, 4, EnemySet.Lane.NORTH));
        waves.add(new Wave(enemySets));

        // wave 10
        enemySets = new ArrayList<>();
        enemySets.add(new EnemySet(EnemySet.EnemyType.SOLDIER, 4, EnemySet.Lane.NORTH));
        enemySets.add(new EnemySet(EnemySet.EnemyType.ARCHER, 3, EnemySet.Lane.NORTH));
        enemySets.add(new EnemySet(EnemySet.EnemyType.HORNET, 2, EnemySet.Lane.NORTH));
        enemySets.add(new EnemySet(EnemySet.EnemyType.PORCUPINE, 2, EnemySet.Lane.NORTH));
        waves.add(new Wave(enemySets));

        // wave 11
        enemySets = new ArrayList<>();
        enemySets.add(new EnemySet(EnemySet.EnemyType.PORCUPINE, 4, EnemySet.Lane.EAST));
        enemySets.add(new EnemySet(EnemySet.EnemyType.ARCHER, 2, EnemySet.Lane.EAST));
        waves.add(new Wave(enemySets));

        // wave 12
        enemySets = new ArrayList<>();
        enemySets.add(new EnemySet(EnemySet.EnemyType.SOLDIER, 1, EnemySet.Lane.EAST));
        enemySets.add(new EnemySet(EnemySet.EnemyType.ARCHER, 1, EnemySet.Lane.EAST));
        enemySets.add(new EnemySet(EnemySet.EnemyType.SOLDIER, 1, EnemySet.Lane.NORTH));
        enemySets.add(new EnemySet(EnemySet.EnemyType.ARCHER, 1, EnemySet.Lane.NORTH));
        waves.add(new Wave(enemySets));

        // wave 13
        enemySets = new ArrayList<>();
        enemySets.add(new EnemySet(EnemySet.EnemyType.BOMB_THROWER, 1, EnemySet.Lane.EAST));
        enemySets.add(new EnemySet(EnemySet.EnemyType.BOMB_THROWER, 1, EnemySet.Lane.NORTH));
        enemySets.add(new EnemySet(EnemySet.EnemyType.ARCHER, 2, EnemySet.Lane.EAST));
        enemySets.add(new EnemySet(EnemySet.EnemyType.ARCHER, 2, EnemySet.Lane.NORTH));
        enemySets.add(new EnemySet(EnemySet.EnemyType.SOLDIER, 1, EnemySet.Lane.EAST));
        enemySets.add(new EnemySet(EnemySet.EnemyType.SOLDIER, 1, EnemySet.Lane.NORTH));
        waves.add(new Wave(enemySets));
    }

    public void startIntermission() {
        isOnIntermission = true;
    }

    public boolean isOnIntermission() {
        return isOnIntermission;
    }

    public int getCurrentWave() {
        return currentWaveIndex;
    }


    public boolean isOnFinalWave() {
        return currentWaveIndex + 1 >= waves.size();
    }

    public void spawnNextWave() {
        isOnIntermission = false;

        if (currentWaveIndex == 10) {
            ArrayList<Rocks> eastRocks = new ArrayList<Rocks>();
            ArrayList<Rocks> rocks = (ArrayList<Rocks>)(List<?>)entityManager.getEntitiesByType(Rocks.class);
            for (Rocks rock : rocks) {
                if (rock.getDirection() == Rocks.Direction.EAST) {
                    eastRocks.add(rock);
                }
            }
            gameScene.setForceCameraTo(eastRocks.get(1).getCenter());

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    ArrayList<Rocks> rocks = (ArrayList<Rocks>)(List<?>)entityManager.getEntitiesByType(Rocks.class);
                    for (Rocks rock : rocks) {
                        if (rock.getDirection() == Rocks.Direction.EAST) {
                            rock.remove();
                        }
                    }
                }
            }, 1.5f);

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    gameScene.setForceCameraTo(null);
                }
            }, 3.0f);
        }

        MapLayer waypoint = mapManager.getTiledMap().getLayers().get("Waypoint");
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
                        entityManager.addEntity(new Footman(spawnPoint.x + ox, spawnPoint.y + oy, pathwayCoordinates, player, gameScene, gameScene, gameScene));
                        break;
                    case ARCHER:
                        entityManager.addEntity(new Archer(spawnPoint.x + ox, spawnPoint.y + oy, pathwayCoordinates, player, gameScene, gameScene, gameScene));
                        break;
                    case HORNET:
                        entityManager.addEntity(new Hornet(spawnPoint.x + ox, spawnPoint.y + oy, pathwayCoordinates, player, gameScene, gameScene, gameScene));
                        break;
                    case PORCUPINE:
                        entityManager.addEntity(new Porcupine(spawnPoint.x + ox, spawnPoint.y + oy, pathwayCoordinates, player, gameScene, gameScene, gameScene));
                        break;
                    case BOMB_THROWER:
                        entityManager.addEntity(new BombThrower(spawnPoint.x + ox, spawnPoint.y + oy, pathwayCoordinates, player, gameScene, gameScene, gameScene));
                        break;
                }
            }
        }
        currentWaveIndex++;
    }

    public void setMapManager(MapManager mapManager) {
        this.mapManager = mapManager;
    }
}
