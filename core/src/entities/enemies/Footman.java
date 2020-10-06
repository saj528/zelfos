package entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import entities.*;
import entities.structures.TownHall;
import helpers.Debug;
import helpers.RedShader;
import scenes.game.*;

import java.util.ArrayList;

public class Footman extends Sprite implements EnemyInterface, Knockable, Entity, Damageable, Collidable {

    private final CoinManager coinManager;
    private final CollisionManager collisionManager;
    private final EntityManager entityManager;
    private LeakManager leakManager;
    private int health = 3;
    private boolean isDead = false;
    private boolean isRed = false;
    public final int SPEED = 1;
    private int DAMAGE = 1;
    private State state = State.WALK;
    private float distanceToPursue = 250;
    private Vector2 nextPointToWalkTowards;
    private ArrayList<Vector2> pathwayCoordinates;
    private float ATTACK_DELAY = 1.0f;
    private Entity target;
    private int pathCounter = 1;
    private Player player;
    private boolean canAttack = true;
    private int ATTACK_RANGE = 10;


    @Override
    public Rectangle getHitbox() {
        return getBoundingRectangle();
    }

    private enum State {
        WALK,
        PURSUE,
        ATTACK,
        DEAD,
    }


    @Override
    public ArrayList<Class> getIgnoreClassList() {
        return new ArrayList<>();
    }

    public Footman(float x, float y, ArrayList<Vector2> pathwayCoordinates, Player player, LeakManager leakManager, CoinManager coinManager, CollisionManager collisionManager, EntityManager entityManager) {
        super(new Texture("footman.png"));
        setPosition(x, y);
        this.coinManager = coinManager;
        this.collisionManager = collisionManager;
        this.entityManager = entityManager;
        this.nextPointToWalkTowards = pathwayCoordinates.get(0);
        this.pathwayCoordinates = pathwayCoordinates;
        this.player = player;
        this.leakManager = leakManager;
    }

    private void walkTo(Entity entity) {
        walkTo(entity.getCenter());
    }

    private void walkTo(Vector2 point) {
        float angleToWalk = (float) Math.atan2(point.y - getY(), point.x - getX());
        float originalX = getX();
        setX((float) (getX() + Math.cos(angleToWalk) * SPEED));
        if (collisionManager.isCollidingWithMap(this) || collisionManager.isCollidingWithOtherCollidables(this)) {
            setX(originalX);
        }

        float originalY = getY();
        setY((float) (getY() + Math.sin(angleToWalk) * SPEED));
        if (collisionManager.isCollidingWithMap(this) || collisionManager.isCollidingWithOtherCollidables(this)) {
            setY(originalY);
        }
    }

    private void attack() {
        if (canAttack) {
            canAttack = false;

            final Footman footman = this;

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    if (footman.isDead()) return;
                    float distance = Geom.distanceBetween(footman, target);
                    float range = ATTACK_RANGE + target.getBoundingRectangle().getWidth() / 2f + footman.getBoundingRectangle().getWidth() / 2f;
                    if (distance <= range) {
                        ((Damageable) target).damage(DAMAGE);
                        if (target == player) {
                            Physics.knockback(footman, player, 20, collisionManager);
                        }
                    }
                }
            }, 0.3f);

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    canAttack = true;
                    state = State.PURSUE;
                }
            }, ATTACK_DELAY);
        }
    }

    @Override
    public void update(float delta) {
        switch (state) {
            case WALK:
                walk();
                break;
            case DEAD:
                isDead = true;
                break;
            case PURSUE:
                pursue();
                break;
            case ATTACK:
                attack();
                break;
        }
    }


    private void walk() {
        if (Geom.distanceBetween(this, player) <= distanceToPursue) {
            state = State.PURSUE;
            target = player;
            return;
        }

        TownHall townHall = (TownHall) entityManager.getEntityByType(TownHall.class.getName());
        float dist = Geom.distanceBetween(this, townHall);
        if (dist <= distanceToPursue) {
            state = State.PURSUE;
            target = townHall;
            return;
        }

        double distanceFromCurrentPathGoal = nextPointToWalkTowards.dst(new Vector2(getX(), getY()));

        if (distanceFromCurrentPathGoal <= 10) {
            if (pathwayCoordinates.size() > pathCounter) {
                nextPointToWalkTowards.y = pathwayCoordinates.get(pathCounter).y;
                nextPointToWalkTowards.x = pathwayCoordinates.get(pathCounter).x;
                pathCounter++;
            } else {
                state = State.DEAD;
                leakManager.removeLeak();
                return;
            }
        }

        walkTo(nextPointToWalkTowards);
    }

    private void pursue() {
        if (Geom.distanceBetween(this, target) > distanceToPursue) {
            state = State.WALK;
            return;
        }

        float distance = Geom.distanceBetween(this, target);
        float range = ATTACK_RANGE + target.getBoundingRectangle().getWidth() / 2f + this.getBoundingRectangle().getWidth() / 2f;
        if (distance <= range) {
            state = State.ATTACK;
            return;
        }

        float angleToWalk = (float) Math.atan2(player.getY() - getY(), player.getX() - getX());
        float originalX = getX();
        setX((float) (getX() + Math.cos(angleToWalk) * SPEED));
        if (collisionManager.isCollidingWithMap(this) || collisionManager.isCollidingWithOtherCollidables(this)) {
            setX(originalX);
        }

        float originalY = getY();
        setY((float) (getY() + Math.sin(angleToWalk) * SPEED));
        if (collisionManager.isCollidingWithMap(this) || collisionManager.isCollidingWithOtherCollidables(this)) {
            setY(originalY);
        }

    }

    public void damage(int amount) {
        health -= amount;
        isRed = true;

        if (health <= 0) {
            isDead = true;
            coinManager.createCoin(getX(), getY());
        }

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                isRed = false;
            }
        }, 0.2f);
    }

    @Override
    public void draw(Batch batch) {
        if (isRed) {
            batch.setShader(RedShader.shaderProgram);
        } else {
            batch.setShader(null);
        }
        batch.begin();
        batch.draw(this.getTexture(), getX(), getY());
        batch.end();
        batch.setShader(null);

        Debug.drawHitbox(batch, getBoundingRectangle());
    }

    public boolean isDead() {
        return isDead;
    }

    public void dispose() {
    }

    public float getDistanceToPlayer() {
        Vector2 enemyCenter = new Vector2(0, 0);
        Vector2 playerCenter = new Vector2(0, 0);

        getBoundingRectangle().getCenter(enemyCenter);
        player.getBoundingRectangle().getCenter(playerCenter);

        return enemyCenter.dst(playerCenter);
    }

    @Override
    public Vector2 getCenter() {
        return Geom.getCenter(this);
    }


}
