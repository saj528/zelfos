package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import helpers.Debug;
import helpers.RedShader;

import java.util.ArrayList;

import static java.lang.Math.sqrt;

public class Enemy extends Sprite implements EnemyInterface {

    private LeakManager leakManager;
    private int health;
    private boolean isDead = false;
    private boolean isRed = false;
    public final int SPEED = 1;
    private int DAMAGE = 1;
    private float current_point_y;
    boolean startOfGame = true;
    private State state;
    private float distanceToPursue = 100;
    private Vector2 nextPointToWalkTowards;
    private ArrayList<Vector2> pathwayCoordinates;
    private float ATTACK_DELAY = 1.0f;
    private int pathCounter = 1;
    private Player player;
    private boolean canAttack = true;
    private int ATTACK_RANGE = 30;

    private enum State {
        WALK,
        PURSUE,
        ATTACK,
        DEAD,
    }

    public Enemy(float x, float y,ArrayList<Vector2> pathwayCoordinates,Player player, LeakManager leakManager) {
        super(new Texture("enemy.png"));
        health = 5;
        setPosition(x, y);
        this.nextPointToWalkTowards = pathwayCoordinates.get(0);
        this.pathwayCoordinates = pathwayCoordinates;
        this.player = player;
        this.leakManager = leakManager;
    }

    public void stateMachine(State state){
        this.state = state;
        switch(state){
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
                attackPlayer();
                break;
        }
    }

    private void attackPlayer() {
        if (canAttack) {
            player.damage(DAMAGE);
            canAttack = false;

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    canAttack = true;
                }
            }, ATTACK_DELAY);
        }

        if(getDistanceToPlayer() > ATTACK_RANGE){
            stateMachine(State.PURSUE);
        }
    }

    public void update(){
        if(startOfGame) {
            startOfGame = false;
            stateMachine(State.WALK);
        }else{
            stateMachine(state);
        }
    }


    private void walkToEnd() {

        if(getDistanceToPlayer() <= distanceToPursue){
            stateMachine(State.PURSUE);
        }
        double distanceFromCurrentPathGoal = sqrt((getX() - nextPointToWalkTowards.x) * (getX()-nextPointToWalkTowards.x) + (getY()-nextPointToWalkTowards.y) * (getY()-nextPointToWalkTowards.y));

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
        if(getDistanceToPlayer() > distanceToPursue){
            stateMachine(State.WALK);
        }

        if (getDistanceToPlayer()  <= ATTACK_RANGE) {
            stateMachine(State.ATTACK);
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
        }

        Physics.knockback(player, this, 30);

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

    public void dispose(){
    }

    public float getDistanceToPlayer() {
        Vector2 enemyCenter = new Vector2(0, 0);
        Vector2 playerCenter = new Vector2(0, 0);

        getBoundingRectangle().getCenter(enemyCenter);
        player.getBoundingRectangle().getCenter(playerCenter);

        return enemyCenter.dst(playerCenter);
    }
}
