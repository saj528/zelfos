package entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import entities.*;
import helpers.Debug;
import helpers.RedShader;
import scenes.game.*;

import java.util.ArrayList;

import static java.lang.Math.sqrt;

public class Hornet implements Enemy, Knockable, Entity, Damageable, Collidable {

    private final CoinManager coinManager;
    private final EntityManager entityManager;
    private final CollisionManager collisionManager;
    private ArrowManager arrowManager;
    private int health = 1;
    private boolean isDead = false;
    private boolean isRed = false;
    public final float SPEED = 1.5f;
    private int DAMAGE = 1;
    boolean startOfGame = true;
    private State state;
    private float x;
    private float y;
    private float distanceToPursue = 150;
    private float walkTime;
    private Vector2 nextPointToWalkTowards;
    private ArrayList<Vector2> pathwayCoordinates;
    private Animation<TextureRegion> hornetAnimation;
    private float ATTACK_DELAY = 2.0f;
    private int pathCounter = 1;
    private Player player;
    private boolean canAttack = true;
    private int ATTACK_RANGE = 100;


    private enum State {
        WALK,
        PURSUE,
        ATTACK,
        DEAD,
    }

    public Hornet(float x, float y, ArrayList<Vector2> pathwayCoordinates, Player player, ArrowManager arrowManager, CoinManager coinManager, EntityManager entityManager, CollisionManager collisionManager) {
        Texture hornetSheet = new Texture("hornet.png");
        TextureRegion[][] hornetRegions = TextureRegion.split(hornetSheet,
                hornetSheet.getWidth() / 2,
                hornetSheet.getHeight() / 1);

        TextureRegion[] hornetFrames = new TextureRegion[2];
        hornetFrames[0] = hornetRegions[0][0];
        hornetFrames[1] = hornetRegions[0][1];
        hornetAnimation = new Animation<TextureRegion>(0.3f, hornetFrames);
        walkTime = 0;
        setX(x);
        setY(y);
        this.coinManager = coinManager;
        this.collisionManager = collisionManager;
        this.nextPointToWalkTowards = pathwayCoordinates.get(0);
        this.pathwayCoordinates = pathwayCoordinates;
        this.player = player;
        this.entityManager = entityManager;
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
    public Vector2 getCenter() {
        return Geom.getCenter(this);
    }

    @Override
    public ArrayList<Class> getIgnoreClassList() {
        ArrayList<Class> ignore = new ArrayList<>();
        ignore.add(Enemy.class);
        ignore.add(DeadZone.class);
        return ignore;
    }


    @Override
    public Rectangle getHitbox() {
        return getBoundingRectangle();
    }

    public void stateMachine(State state) {
        this.state = state;
        switch (state) {
            case WALK: // walk to end point
                walkToEnd();
                break;
            case DEAD:
                isDead = true;
                break;
            case PURSUE:
                pursuePlayer();
                break;
            case ATTACK:
                fireArrow();
                break;
        }
    }


    private void fireArrow() {
        if (canAttack) {
            Vector2 playerCenter = new Vector2(0, 0);
            player.getBoundingRectangle().getCenter(playerCenter);
            float dy = playerCenter.y - getY();
            float dx = playerCenter.x - getX();
            float angle = (float) Math.atan2(dy, dx);
            entityManager.addEntity(new Stinger(getX(), getY(), angle, player, collisionManager));
            canAttack = false;

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    canAttack = true;
                }
            }, ATTACK_DELAY);
        }

        double distanceEnemyPlayer = sqrt((getX() - player.getX()) * (getX() - player.getX()) + (getY() - player.getY()) * (getY() - player.getY()));
        if (distanceEnemyPlayer > ATTACK_RANGE) {
            stateMachine(State.PURSUE);
        }
    }

    @Override
    public void update(float delta) {
        walkTime += delta;
        if (startOfGame) {
            startOfGame = false;
            stateMachine(State.WALK);
        } else {
            stateMachine(state);
        }
    }

    private void walkToEnd() {

        double distanceEnemyPlayer = sqrt((getX() - player.getX()) * (getX() - player.getX()) + (getY() - player.getY()) * (getY() - player.getY()));
        if (distanceEnemyPlayer <= distanceToPursue) {
            stateMachine(State.PURSUE);
        }
        double distanceFromCurrentPathGoal = sqrt((getX() - nextPointToWalkTowards.x) * (getX() - nextPointToWalkTowards.x) + (getY() - nextPointToWalkTowards.y) * (getY() - nextPointToWalkTowards.y));

        if (distanceFromCurrentPathGoal <= 10) {
            if (pathwayCoordinates.size() > pathCounter) {
                nextPointToWalkTowards.y = pathwayCoordinates.get(pathCounter).y;
                nextPointToWalkTowards.x = pathwayCoordinates.get(pathCounter).x;
                pathCounter++;
            } else {
                stateMachine(State.DEAD);
                return;
            }
        }
        if (getY() < nextPointToWalkTowards.y) {
            setY(getY() + SPEED);
        } else if (getY() > nextPointToWalkTowards.y) {
            setY(getY() - SPEED);
        }
        if (getX() < nextPointToWalkTowards.x) {
            setX(getX() + SPEED);
        } else if (getX() > nextPointToWalkTowards.x) {
            setX(getX() - SPEED);
        }
    }

    private void pursuePlayer() {
        double distanceEnemyPlayer = sqrt((getX() - player.getX()) * (getX() - player.getX()) + (getY() - player.getY()) * (getY() - player.getY()));
        if (distanceEnemyPlayer > distanceToPursue) {
            stateMachine(State.WALK);
        }

        if (distanceEnemyPlayer <= ATTACK_RANGE) {
            stateMachine(State.ATTACK);
        }

        if (getY() < player.getY()) {
            setY(getY() + SPEED);
        } else if (getY() > player.getY()) {
            setY(getY() - SPEED);
        }
        if (getX() < player.getX()) {
            setX(getX() + SPEED);
        } else if (getX() > player.getX()) {
            setX(getX() - SPEED);
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
    public Rectangle getBoundingRectangle() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public Texture getTexture() {
        return hornetAnimation.getKeyFrame(walkTime).getTexture();
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
    public float getWidth() {
        return hornetAnimation.getKeyFrame(walkTime).getRegionWidth();
    }

    @Override
    public float getHeight() {
        return hornetAnimation.getKeyFrame(walkTime).getRegionHeight();
    }

    @Override
    public int getDamage() {
        return 0;
    }

    @Override
    public float getAttackDelay() {
        return 0;
    }

    @Override
    public float getAttackRange() {
        return 0;
    }

    @Override
    public float getSpeed() {
        return 0;
    }

    @Override
    public void draw(Batch batch) {
        if (isRed) {
            batch.setShader(RedShader.shaderProgram);
        } else {
            batch.setShader(null);
        }
        batch.begin();
        batch.draw(hornetAnimation.getKeyFrame(walkTime, true), getX(), getY());
        batch.end();
        batch.setShader(null);
        Debug.drawHitbox(batch, getBoundingRectangle());
    }

    public boolean isDead() {
        return isDead;
    }


    public void dispose() {
    }
}
