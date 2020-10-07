package entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    private float WALK_ANIMATION_SPEED = 0.13f;
    private int ATTACK_RANGE = 10;
    private float ATTACK_ANIMATION_SPEED = 0.025f;
    private float ATTACK_COOLDOWN = ATTACK_ANIMATION_SPEED * 13;
    private float ATTACK_ANIMATION_DURATION = 0.2f;
    private Animation<TextureRegion> attackAnimation;
    private Animation<TextureRegion> walkAnimation;
    private float attackTime = 0f;
    private float walkTime = 0f;


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
        super(new Texture("soldier_walking_6.png"),0,0,32,32);
        setPosition(x, y);
        attackTime=0;
        walkTime=0;
        this.coinManager = coinManager;
        this.collisionManager = collisionManager;
        this.entityManager = entityManager;
        this.nextPointToWalkTowards = pathwayCoordinates.get(0);
        this.pathwayCoordinates = pathwayCoordinates;
        this.player = player;
        this.leakManager = leakManager;
        initTextures();
    }

    private void initTextures() {
        Texture attackAnimationSheet = new Texture("soldier_attacking_4.png");
        TextureRegion[][] attackAnimationSheetRegion = TextureRegion.split(attackAnimationSheet,attackAnimationSheet.getWidth()/4,attackAnimationSheet.getHeight());

        TextureRegion[] attackAnimationFrames = new TextureRegion[4];
        attackAnimationFrames[0] = attackAnimationSheetRegion[0][0];
        attackAnimationFrames[1] = attackAnimationSheetRegion[0][1];
        attackAnimationFrames[2] = attackAnimationSheetRegion[0][2];
        attackAnimationFrames[3] = attackAnimationSheetRegion[0][3];
        attackAnimation = new Animation<TextureRegion>(ATTACK_ANIMATION_SPEED, attackAnimationFrames);

        Texture walkAnimationSheet = new Texture("soldier_walking_6.png");
        TextureRegion[][] walkAnimationSheetRegion = TextureRegion.split(walkAnimationSheet,walkAnimationSheet.getWidth()/6,walkAnimationSheet.getHeight());

        TextureRegion[] walkAnimationFrames = new TextureRegion[6];
        walkAnimationFrames[0] = walkAnimationSheetRegion[0][0];
        walkAnimationFrames[1] = walkAnimationSheetRegion[0][1];
        walkAnimationFrames[2] = walkAnimationSheetRegion[0][2];
        walkAnimationFrames[3] = walkAnimationSheetRegion[0][3];
        walkAnimationFrames[4] = walkAnimationSheetRegion[0][4];
        walkAnimationFrames[5] = walkAnimationSheetRegion[0][5];
        walkAnimation = new Animation<TextureRegion>(WALK_ANIMATION_SPEED, walkAnimationFrames);
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
        attackTime += delta;
        walkTime += delta;
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

    @Override
    public Rectangle getBoundingRectangle() {
        return new Rectangle(getX() + 2, getY(), getWidth() - 14, getHeight() - 5);
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
        if(state == State.WALK || state == State.PURSUE){
            batch.draw(walkAnimation.getKeyFrame(walkTime, true), getX(), getY());
        }else if(state == State.ATTACK){
            batch.draw(attackAnimation.getKeyFrame(attackTime, false), getX(), getY());
        }

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
