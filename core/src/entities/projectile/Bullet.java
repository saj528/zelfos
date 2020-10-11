package entities.projectile;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import entities.Entity;
import entities.Killable;
import entities.Knockable;
import entities.Player;
import entities.enemies.Enemy;
import scenes.game.CollisionManager;
import scenes.game.EntityManager;
import scenes.game.Geom;
import scenes.game.Physics;

import java.util.ArrayList;
import java.util.List;

public class Bullet extends Sprite implements Killable, Entity {

    private final CollisionManager collisionManager;
    private final EntityManager entityManager;
    float SPEED = 10.0f;
    final int DAMAGE = 2;
    float angle;
    boolean isDead;

    public Bullet(float x, float y, float angle, EntityManager entityManager, CollisionManager collisionManager) {
        super(new Texture("playersprites/s_arrow.png"));
        this.angle = angle;
        setX(x);
        this.collisionManager = collisionManager;
        this.entityManager = entityManager;
        setY(y);
        isDead = false;
    }

    public void draw(Batch batch, ShapeRenderer shapeRenderer) {
        setOrigin(getWidth() / 2, getHeight() / 2);
        setRotation(angle / (float)Math.PI * 180);
        batch.begin();
        super.draw(batch);
        batch.end();
    }

    @Override
    public Vector2 getCenter() {
        return Geom.getCenter(this);
    }

    public void update(float delta) {
        setX(getX() + SPEED * (float)Math.cos(angle));
        setY(getY() + SPEED * (float)Math.sin(angle));

        final ArrayList<Enemy> enemyEntities = (ArrayList<Enemy>)(List<?>)entityManager.getEntitiesByType(Enemy.class);

        for (Enemy enemy : enemyEntities) {
            if (getBoundingRectangle().overlaps(enemy.getBoundingRectangle())) {
                enemy.damage(DAMAGE);
                Physics.knockback(this, (Knockable)enemy, 10, collisionManager);
                isDead = true;
                return;
            }
        }
    }

    public boolean isDead() {
        return isDead;
    }
}
