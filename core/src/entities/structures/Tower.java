package entities.structures;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import entities.Entity;
import entities.enemies.Enemy;
import entities.projectile.Bullet;
import scenes.game.Collidable;
import scenes.game.CollisionManager;
import scenes.game.EntityManager;
import scenes.game.Geom;

import java.util.ArrayList;
import java.util.List;

public class Tower implements Entity, Collidable {
    private final EntityManager entityManager;
    private float x;
    private float y;
    private boolean canFire = true;
    private Texture texture;
    private float ATTACK_COOLDOWN = 3.0f;
    private CollisionManager collisionManager;
    private float TOWER_RANGE = 200f;

    public Tower(float x, float y, EntityManager entityManager, CollisionManager collisionManager) {
        this.x = x;
        this.y = y;
        this.texture = new Texture("tower.png");
        this.entityManager = entityManager;
        this.collisionManager = collisionManager;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    @Override
    public Rectangle getBoundingRectangle() {
        return new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }

    @Override
    public Vector2 getCenter() {
        return Geom.getCenter(this);
    }

    @Override
    public void update(float delta) {
        if (canFire) {
            ArrayList<Entity> enemies = entityManager.getEntitiesByType(Enemy.class);
            ArrayList<Entity> enemiesInRange = Geom.getEntitiesInRange(enemies, this, TOWER_RANGE);
            if (enemiesInRange.size() > 0) {
                Entity enemy = enemiesInRange.get(0);
                entityManager.addEntity(new Bullet(getX(), getY(), Geom.angleBetween(this, enemy), entityManager, collisionManager));
                canFire = false;

                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        canFire = true;
                    }
                }, ATTACK_COOLDOWN);
            }
        }
    }

    @Override
    public void draw(Batch batch, ShapeRenderer shapeRenderer) {
        batch.begin();
        batch.draw(texture, x, y);
        batch.end();
    }

    @Override
    public Rectangle getHitbox() {
        return getBoundingRectangle();
    }

    @Override
    public ArrayList<Class> getIgnoreClassList() {
        return null;
    }
}
