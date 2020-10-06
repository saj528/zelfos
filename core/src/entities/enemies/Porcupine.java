package entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import entities.Damageable;
import entities.Entity;
import entities.Knockable;
import entities.Player;
import helpers.Debug;
import helpers.RedShader;
import scenes.game.*;

import java.util.ArrayList;

public class Porcupine extends Sprite implements EnemyInterface, Knockable, Entity, Damageable, Collidable {

    private final CoinManager coinManager;
    private final CollisionManager collisionManager;
    private LeakManager leakManager;
    private int health = 3;
    private boolean isDead = false;
    private boolean isRed = false;
    public final int SPEED = 1;
    private int DAMAGE = 1;
    private State state = State.WALK;
    private float distanceToPursue = 100;
    private Vector2 nextPointToWalkTowards;
    private ArrayList<Vector2> pathwayCoordinates;
    private float ATTACK_DELAY = 1.0f;
    private int pathCounter = 1;
    private Player player;
    private boolean canAttack = true;
    private int ATTACK_RANGE = 30;
    private Animation<TextureRegion> porcupineAttack;
    private Animation<TextureRegion> porcupineWalk;
    private float WALK_ANIMATION_SPEED = 0.13f;
    private float ATTACK_ANIMATION_SPEED = 0.025f;
    private float ATTACK_COOLDOWN = ATTACK_ANIMATION_SPEED * 13;
    private float ATTACK_ANIMATION_DURATION = 0.2f;
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

    public Porcupine(float x, float y, ArrayList<Vector2> pathwayCoordinates, Player player, LeakManager leakManager, CoinManager coinManager, CollisionManager collisionManager) {
        setPosition(x, y);
        this.coinManager = coinManager;
        this.collisionManager = collisionManager;
        this.nextPointToWalkTowards = pathwayCoordinates.get(0);
        this.pathwayCoordinates = pathwayCoordinates;
        this.player = player;
        this.leakManager = leakManager;
        initTextures();
    }

    private void initTextures() {

        Texture porcupineAttackSheet = new Texture("enemysprites/s_porcupine_attack_strip5.png");
        TextureRegion[][] porcupineAttackSheetRegions = TextureRegion.split(porcupineAttackSheet,
                porcupineAttackSheet.getWidth() / 5,
                porcupineAttackSheet.getHeight());

        Texture porcupineWalkSheet = new Texture("enemysprites/s_porcupine_run_strip3.png");
        TextureRegion[][] porcupineWalkSheetRegions = TextureRegion.split(porcupineWalkSheet,
                porcupineWalkSheet.getWidth() / 3,
                porcupineWalkSheet.getHeight());


        TextureRegion[] attackFrames = new TextureRegion[5];
        attackFrames[0] = porcupineAttackSheetRegions[0][0];
        attackFrames[1] = porcupineAttackSheetRegions[0][1];
        attackFrames[2] = porcupineAttackSheetRegions[0][2];
        attackFrames[3] = porcupineAttackSheetRegions[0][3];
        attackFrames[4] = porcupineAttackSheetRegions[0][4];
        porcupineAttack = new Animation<TextureRegion>(ATTACK_ANIMATION_SPEED, attackFrames);

        TextureRegion[] walkFrames = new TextureRegion[3];
        walkFrames[0] = porcupineWalkSheetRegions[0][0];
        walkFrames[1] = porcupineWalkSheetRegions[0][1];
        walkFrames[2] = porcupineWalkSheetRegions[0][2];
        porcupineWalk = new Animation<TextureRegion>(WALK_ANIMATION_SPEED, walkFrames);

    }

    public void stateMachine(State state){
        this.state = state;
        switch(state){
            case WALK:
                walkToEnd();
                break;
            case DEAD:
                isDead = true;
                break;
            case PURSUE:
                pursuePlayer();
                break;
            case ATTACK:
                attackPlayer();
                break;
        }
    }

    private void attackPlayer() {
        if (canAttack) {
            canAttack = false;

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    if (getDistanceToPlayer() <= ATTACK_RANGE) {
                        player.damage(DAMAGE);
                    }
                }
            }, 0.5f);

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    canAttack = true;
                    stateMachine(State.PURSUE);
                }
            }, ATTACK_DELAY);
        }


    }


    private void walkToEnd() {

        if(getDistanceToPlayer() <= distanceToPursue){
            stateMachine(State.PURSUE);
        }
        double distanceFromCurrentPathGoal = nextPointToWalkTowards.dst(new Vector2(getX(), getY()));

        if(distanceFromCurrentPathGoal <= 10){
            if(pathwayCoordinates.size() > pathCounter){
                nextPointToWalkTowards.y = pathwayCoordinates.get(pathCounter).y;
                nextPointToWalkTowards.x = pathwayCoordinates.get(pathCounter).x;
                pathCounter++;
            }else{
                stateMachine(State.DEAD);
                leakManager.removeLeak();
                return;
            }
        }

        float angleToWalk = (float)Math.atan2(nextPointToWalkTowards.y - getY(), nextPointToWalkTowards.x - getX());
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

    private void pursuePlayer(){
        if(getDistanceToPlayer() > distanceToPursue){
            stateMachine(State.WALK);
        }

        if (getDistanceToPlayer() <= ATTACK_RANGE) {
            stateMachine(State.ATTACK);
        }

        float angleToWalk = (float)Math.atan2(player.getY() - getY(), player.getX() - getX());
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
        if(state == State.WALK || state == State.PURSUE) {
            batch.draw(porcupineWalk.getKeyFrame(walkTime, true), getX(), getY());
        }else if(state == State.ATTACK){
            batch.draw(porcupineAttack.getKeyFrame(attackTime, true), getX(), getY());
        }
        batch.end();
        batch.setShader(null);

        Debug.drawHitbox(batch, getBoundingRectangle());
    }

    public boolean isDead() {
        return isDead;
    }

    public void dispose(){
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

    @Override
    public void update(float delta) {
        stateMachine(state);
        attackTime += delta;
        walkTime += delta;

    }
}
