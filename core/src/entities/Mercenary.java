package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import entities.enemies.Enemy;
import entities.enemies.Footman;
import helpers.Debug;
import helpers.RedShader;
import scenes.game.*;

import java.util.ArrayList;

public class Mercenary implements Knockable, Entity, Damageable, Killable, Collidable, Friendly {

    private final CollisionManager collisionManager;
    private final Vector2 guardPost;
    private final Vector2 basePost;
    private final EntityManager entityManager;
    private int MAX_HEALTH = 5;
    private int health = MAX_HEALTH;
    private boolean isDead = false;
    private boolean isRed = false;
    public final float SPEED = 1.4f;
    private int DAMAGE = 1;
    private State state = State.WALK_TO_BASE;
    private Texture mercenaryTexture;
    private float distanceToPursue = 250;
    private Vector2 nextPointToWalkTowards;
    private ArrayList<Vector2> pathwayCoordinates;
    private float ATTACK_DELAY = 1.0f;
    private Enemy target;
    private int pathCounter = 1;
    private boolean canAttack = true;
    private int ATTACK_RANGE = 20;
    private float x;
    private float y;
    private WaveManager waveManager;

    public Mercenary(Vector2 guardPost, Vector2 basePost, CollisionManager collisionManager, WaveManager waveManager, EntityManager entityManager) {
        mercenaryTexture = new Texture("footman.png");
        this.x = basePost.x;
        this.y = basePost.y;
        this.guardPost = guardPost;
        this.basePost = basePost;
        this.collisionManager = collisionManager;
        this.entityManager = entityManager;
        this.waveManager = waveManager;
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
    public Rectangle getBoundingRectangle() {
        return new Rectangle(x, y, mercenaryTexture.getWidth(), mercenaryTexture.getHeight());
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
    public Rectangle getHitbox() {
        return getBoundingRectangle();
    }

    @Override
    public ArrayList<Class> getIgnoreClassList() {
        return new ArrayList<>();
    }

    @Override
    public boolean isTargetable() {
        return true;
    }

    private enum State {
        WALK_TO_GUARD,
        PURSUE,
        ATTACK,
        WALK_TO_BASE,
        DEAD,
        IDLE,
    }


    private void walkTo(Entity entity) {
        walkTo(entity.getCenter());
    }

    private void walkTo(Vector2 point) {
        float angleToWalk = (float) Math.atan2(point.y - getY(), point.x - getX());
        setX((float) (getX() + Math.cos(angleToWalk) * SPEED));
        setY((float) (getY() + Math.sin(angleToWalk) * SPEED));
    }

    private void attack() {

        if (canAttack) {
            canAttack = false;

            final Mercenary mercenary = this;

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    if (target != null && target.isDead()) {
                        target = null;
                        return;
                    }
                    if (mercenary.isDead()) {
                        return;
                    }
                    float distance = Geom.distanceBetween(mercenary, (Entity)target);
                    float range = ATTACK_RANGE + target.getBoundingRectangle().getWidth() / 2f + mercenary.getBoundingRectangle().getWidth() / 2f;
                    if (distance <= range) {
                        ((Damageable) target).damage(DAMAGE);
                        Physics.knockback((Entity)mercenary, (Knockable) target, 20, collisionManager);
                    }
                }
            }, 0.3f);

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    canAttack = true;
                    state = State.IDLE;
                    target = null;
                }
            }, ATTACK_DELAY);
        }
    }

    @Override
    public void update(float delta) {
        switch (state) {
            case WALK_TO_GUARD:
                walkToGuard();
                break;
            case IDLE:
                idle();
                break;
            case PURSUE:
                pursue();
                break;
            case ATTACK:
                attack();
                break;
            case WALK_TO_BASE:
                walkToBase();
                break;
        }
    }

    private void idle() {
        if (waveManager.isOnIntermission()) {
            state = State.WALK_TO_BASE;
        } else {
            state = State.WALK_TO_GUARD;
        }
    }

    private void walkToGuard() {
        if (waveManager.isOnIntermission()) {
            state = State.WALK_TO_BASE;
            return;
        }

        if (target == null) {
            ArrayList<Enemy> enemies = (ArrayList<Enemy>)(ArrayList<?>)entityManager.getEntitiesByType(Enemy.class);
            for (Enemy enemy : enemies) {
                float distToEnemy = Geom.distanceBetween(this, (Entity)enemy);
                if (distToEnemy <= distanceToPursue) {
                    state = State.PURSUE;
                    target = enemy;
                    return;
                }
            }
        }

        walkTo(guardPost);
    }


    private void walkToBase() {
        if (!waveManager.isOnIntermission()) {
            state = State.WALK_TO_GUARD;
            return;
        }

        walkTo(basePost);
    }


    private void pursue() {
        if (Geom.distanceBetween(this, (Entity)target) > distanceToPursue) {
            state = State.IDLE;
            target = null;
            return;
        }

        float distance = Geom.distanceBetween(this, (Entity)target);
        float range = ATTACK_RANGE + target.getBoundingRectangle().getWidth() / 2f + this.getBoundingRectangle().getWidth() / 2f;
        if (distance <= range) {
            state = State.ATTACK;
            return;
        }

        float angleToWalk = (float) Math.atan2(target.getY() - getY(), target.getX() - getX());
        setX((float) (getX() + Math.cos(angleToWalk) * SPEED));
        setY((float) (getY() + Math.sin(angleToWalk) * SPEED));
    }

    public void damage(int amount) {
        health -= amount;
        isRed = true;

        if (health <= 0) {
            isDead = true;
        }

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                isRed = false;
            }
        }, 0.2f);
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public int getMaxHealth() {
        return MAX_HEALTH;
    }

    @Override
    public void draw(Batch batch, ShapeRenderer shapeRenderer) {
        if (isRed) {
            batch.setShader(RedShader.shaderProgram);
        } else {
            batch.setShader(null);
        }
        batch.begin();
        batch.draw(mercenaryTexture, getX(), getY());
        batch.end();
        batch.setShader(null);

        Debug.drawHitbox(batch, getBoundingRectangle());
    }

    public boolean isDead() {
        return isDead;
    }

    public void dispose() {
    }


    @Override
    public Vector2 getCenter() {
        return Geom.getCenter(this);
    }


}
