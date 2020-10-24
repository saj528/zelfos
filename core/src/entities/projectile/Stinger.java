package entities.projectile;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import entities.*;
import entities.enemies.logic.Melee;
import scenes.game.CollisionManager;
import scenes.game.EntityManager;
import scenes.game.Geom;
import scenes.game.Physics;

import java.util.ArrayList;

public class Stinger extends Sprite implements Killable, Entity {

    private final CollisionManager collisionManager;
    private final EntityManager entityManager;
    float SPEED = 3.5f;
    final int DAMAGE = 1;
    float angle;
    Player player;
    boolean isDead;

    public Stinger(float x, float y, float angle, Player player, CollisionManager collisionManager, EntityManager entityManager) {
        super(new Texture("stinger.png"));
        this.angle = angle;
        setX(x);
        this.collisionManager = collisionManager;
        this.entityManager = entityManager;
        setY(y);
        this.player = player;
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

        ArrayList<Entity> targets = (ArrayList<Entity>) (ArrayList<?>) entityManager.getEntitiesByType(Friendly.class);
        for (Entity target : targets) {
            if (target instanceof Player) {
                System.out.println("player");
            }

            if (!((Friendly)target).isTargetable()) continue;

            if (getBoundingRectangle().overlaps(target.getBoundingRectangle())) {
                ((Damageable)target).damage(DAMAGE);
                if (target instanceof Knockable) {
                    Physics.knockback(this, (Knockable)target, 10, collisionManager);
                }
                isDead = true;
                break;
            }
        }
    }

    public boolean isDead() {
        return isDead;
    }
}
