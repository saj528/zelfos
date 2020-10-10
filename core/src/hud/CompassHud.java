package hud;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import entities.Entity;
import entities.Player;
import entities.enemies.Enemy;
import helpers.GameInfo;
import scenes.game.EnemyManager;
import scenes.game.EntityManager;

public class CompassHud {
    private final Player player;
    private final Sprite northArrow;
    private final Sprite southArrow;
    private final Sprite eastArrow;
    private final Sprite westArrow;
    private final EntityManager entityManager;

    public CompassHud(EntityManager entityManager, Player player) {
        this.entityManager = entityManager;
        this.player = player;

        Texture compass = new Texture("compass.png");
        northArrow = new Sprite(compass);
//        northArrow.setRotation(90);
        northArrow.setPosition(GameInfo.WIDTH / 2f, GameInfo.HEIGHT - 100);

        eastArrow = new Sprite(compass);
//        eastArrow.setRotation(0);
        eastArrow.setPosition(GameInfo.WIDTH - 60, GameInfo.HEIGHT / 2f);

        westArrow = new Sprite(compass);
//        westArrow.setRotation(180);
        westArrow.setPosition( 10, GameInfo.HEIGHT / 2f);

        southArrow = new Sprite(compass);
//        southArrow.setRotation(-90);
        southArrow.setPosition( GameInfo.WIDTH / 2f, 10);

    }

    public boolean hasEnemiesNorth() {
        for (Entity enemy : entityManager.getEntitiesByType(Enemy.class)) {
            if (enemy.getY() > player.getY() + GameInfo.HEIGHT / 2f) {
                return true;
            }
        }
        return false;
    }

    public boolean hasEnemiesSouth() {
        for (Entity enemy : entityManager.getEntitiesByType(Enemy.class)) {
            if (enemy.getY() < player.getY() - GameInfo.HEIGHT / 2f) {
                return true;
            }
        }
        return false;
    }

    public boolean hasEnemiesWest() {
        for (Entity enemy : entityManager.getEntitiesByType(Enemy.class)) {
            if (enemy.getX() < player.getX() - GameInfo.WIDTH / 2f) {
                return true;
            }
        }
        return false;
    }

    public boolean hasEnemiesEast() {
        for (Entity enemy : entityManager.getEntitiesByType(Enemy.class)) {
            if (enemy.getX() > player.getX() + GameInfo.WIDTH / 2f) {
                return true;
            }
        }
        return false;
    }

    public void draw(Batch batch) {
        batch.begin();
        if (hasEnemiesNorth()) {
            northArrow.draw(batch);
        }

        if (hasEnemiesEast()) {
            eastArrow.draw(batch);
        }

        if (hasEnemiesWest()) {
            westArrow.draw(batch);
        }

        if (hasEnemiesSouth()) {
            southArrow.draw(batch);
        }
        batch.end();
    }
}
