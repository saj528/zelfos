package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import helpers.Debug;
import helpers.RedShader;
import scenes.game.*;

import java.util.ArrayList;

public class Mercenary implements Knockable, Entity, Damageable, Collidable {

    private final CollisionManager collisionManager;
    private final Vector2 guardPost;
    private final Vector2 basePost;
    private LeakManager leakManager;
    private int health = 3;
    private boolean isDead = false;
    private boolean isRed = false;
    public final int SPEED = 1;
    private int DAMAGE = 1;
    private State state = State.WALK_TO_GUARD;
    private Texture mercenaryTexture;
    private float distanceToPursue = 250;
    private Vector2 nextPointToWalkTowards;
    private ArrayList<Vector2> pathwayCoordinates;
    private float ATTACK_DELAY = 1.0f;
    private Entity target;
    private int pathCounter = 1;
    private Player player;
    private boolean canAttack = true;
    private int ATTACK_RANGE = 10;
    private float x;
    private float y;

    public Mercenary(Vector2 guardPost, Vector2 basePost, Player player, CollisionManager collisionManager) {
        mercenaryTexture = new Texture("footman.png");
        this.x = basePost.x;
        this.y = basePost.y;
        this.guardPost = guardPost;
        this.basePost = basePost;
        this.collisionManager = collisionManager;
        this.player = player;
    }

    @Override
    public Rectangle getHitbox() {
        return getBoundingRectangle();
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

    private enum State {
        WALK_TO_GUARD,
        PURSUE,
        ATTACK,
        DEAD,
        IDLE,
    }


    @Override
    public ArrayList<Class> getIgnoreClassList() {
        ArrayList<Class> list = new ArrayList<>();
        list.add(Mercenary.class);
        return list;
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

            final Mercenary footman = this;

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
            case WALK_TO_GUARD:
                walkToGuard();
                break;
        }
    }


    private void walkToGuard() {
//        if (Geom.distanceBetween(this, player) <= distanceToPursue) {
//            state = State.PURSUE;
//            target = player;
//            return;
//        }

        double distanceToGuardPost = guardPost.dst(getCenter());

        if (distanceToGuardPost <= 10) {
            state = State.IDLE;
            return;
        }

        walkTo(guardPost);
    }

    private void pursue() {
        if (Geom.distanceBetween(this, target) > distanceToPursue) {
            state = State.WALK_TO_GUARD;
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
