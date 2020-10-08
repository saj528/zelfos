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
import helpers.Debug;
import helpers.RedShader;
import scenes.game.*;

import java.util.ArrayList;

import static java.lang.Math.sqrt;

public class Archer extends Sprite implements Enemy, Knockable, Entity, Damageable, Collidable {

    private final CoinManager coinManager;
    private ArrowManager arrowManager;
    private int health = 5;
    private boolean isDead = false;
    private boolean isRed = false;
    public final int SPEED = 1;
    private int DAMAGE = 1;
    private float current_point_y;
    boolean startOfGame = true;
    private State state;
    private float distanceToPursue = 300;
    private Vector2 nextPointToWalkTowards;
    private ArrayList<Vector2> pathwayCoordinates;
    private float ATTACK_DELAY = 0.9f;
    private int pathCounter = 1;
    private Player player;
    private boolean canAttack = true;
    private int ATTACK_RANGE = 250;
    private final float WALK_ANIMATION_SPEED = 0.13f;
    private final float ATTACK_ANIMATION_SPEED = 0.15f;
    private final float ATTACK_COOLDOWN = 3f;
    private Animation<TextureRegion> attackAnimation;
    private Animation<TextureRegion> walkAnimation;
    private float attackTime = 0f;
    private float walkTime = 0f;

    private enum State {
        WALK,
        PURSUE,
        ATTACK,
        DEAD,
    }

    public Archer(float x, float y,ArrayList<Vector2> pathwayCoordinates, Player player, ArrowManager arrowManager, CoinManager coinManager) {
        super(new Texture("archer_walking_6.png"), 0, 0, 32, 32);
        setPosition(x, y);
        attackTime = 0;
        walkTime = 0;
        this.coinManager = coinManager;
        this.nextPointToWalkTowards = pathwayCoordinates.get(0);
        this.pathwayCoordinates = pathwayCoordinates;
        this.player = player;
        this.arrowManager = arrowManager;
        this.state = State.WALK;
        initTextures();
    }

    private void initTextures() {
        Texture attackAnimationSheet = new Texture("archer_attack_6.png");
        TextureRegion[][] attackAnimationSheetRegion = TextureRegion.split(attackAnimationSheet, attackAnimationSheet.getWidth() / 6, attackAnimationSheet.getHeight());

        TextureRegion[] attackAnimationFrames = new TextureRegion[6];
        attackAnimationFrames[0] = attackAnimationSheetRegion[0][0];
        attackAnimationFrames[1] = attackAnimationSheetRegion[0][1];
        attackAnimationFrames[2] = attackAnimationSheetRegion[0][2];
        attackAnimationFrames[3] = attackAnimationSheetRegion[0][3];
        attackAnimationFrames[4] = attackAnimationSheetRegion[0][4];
        attackAnimationFrames[5] = attackAnimationSheetRegion[0][5];
        attackAnimation = new Animation<TextureRegion>(ATTACK_ANIMATION_SPEED, attackAnimationFrames);

        Texture walkAnimationSheet = new Texture("archer_walking_6.png");
        TextureRegion[][] walkAnimationSheetRegion = TextureRegion.split(walkAnimationSheet, walkAnimationSheet.getWidth() / 6, walkAnimationSheet.getHeight());

        TextureRegion[] walkAnimationFrames = new TextureRegion[6];
        walkAnimationFrames[0] = walkAnimationSheetRegion[0][0];
        walkAnimationFrames[1] = walkAnimationSheetRegion[0][1];
        walkAnimationFrames[2] = walkAnimationSheetRegion[0][2];
        walkAnimationFrames[3] = walkAnimationSheetRegion[0][3];
        walkAnimationFrames[4] = walkAnimationSheetRegion[0][4];
        walkAnimationFrames[5] = walkAnimationSheetRegion[0][5];
        walkAnimation = new Animation<TextureRegion>(WALK_ANIMATION_SPEED, walkAnimationFrames);
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


    private void fireArrow() {
        if (canAttack) {
            attackTime = 0f;
            canAttack = false;

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    Vector2 playerCenter = player.getCenter();
                    float dy = playerCenter.y - getCenter().y;
                    float dx = playerCenter.x - getCenter().x;
                    final float angle = (float)Math.atan2(dy, dx);
                    arrowManager.createArrow(getCenter().x, getCenter().y, angle);
                }
            }, ATTACK_DELAY);


            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    canAttack = true;
                }
            }, ATTACK_COOLDOWN);
        }

        double distanceEnemyPlayer = Geom.distanceBetween(this, player);
        if(distanceEnemyPlayer > ATTACK_RANGE){
            state = State.PURSUE;
        }
    }

    @Override
    public void update(float delta){
        attackTime += delta;
        walkTime += delta;
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
                fireArrow();
                break;
        }
    }


    private void walkToEnd() {

        double distanceEnemyPlayer = sqrt((getX() - player.getX()) * (getX()-player.getX()) + (getY()-player.getY()) * (getY()-player.getY()));
        if(distanceEnemyPlayer <= distanceToPursue){
            state = State.PURSUE;
            return;
        }
        double distanceFromCurrentPathGoal = sqrt((getX() - nextPointToWalkTowards.x) * (getX()-nextPointToWalkTowards.x) + (getY()-nextPointToWalkTowards.y) * (getY()-nextPointToWalkTowards.y));

        if(distanceFromCurrentPathGoal <= 10){
            if(pathwayCoordinates.size() > pathCounter){
                nextPointToWalkTowards.y = pathwayCoordinates.get(pathCounter).y;
                nextPointToWalkTowards.x = pathwayCoordinates.get(pathCounter).x;
                pathCounter++;
            }else{
                state = State.DEAD;
                return;
            }
        }
        if(getY() < nextPointToWalkTowards.y) {
            setY(getY() + SPEED);
        }else if(getY() > nextPointToWalkTowards.y){
            setY(getY() - SPEED);
        }
        if(getX() < nextPointToWalkTowards.x){
            setX(getX() + SPEED);
        }else if(getX() > nextPointToWalkTowards.x){
            setX(getX() - SPEED);
        }
    }

    private void pursuePlayer(){
        double distanceEnemyPlayer = sqrt((getX() - player.getX()) * (getX()-player.getX()) + (getY()-player.getY()) * (getY()-player.getY()));
        if(distanceEnemyPlayer > distanceToPursue){
            state = State.WALK;
            return;
        }

        if (distanceEnemyPlayer <= ATTACK_RANGE - 30) {
            state = State.ATTACK;
            attackTime = 0f;
            return;
        }

        if(getY() < player.getY()) {
            setY(getY() + SPEED);
        }else if(getY() > player.getY()){
            setY(getY() - SPEED);
        }
        if(getX() < player.getX()){
            setX(getX() + SPEED);
        }else if(getX() > player.getX()){
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
    public void onAttackStart() {

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

    public void dispose(){
    }
}
