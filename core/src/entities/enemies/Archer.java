package entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import entities.*;
import helpers.Debug;
import helpers.RedShader;
import scenes.game.ArrowManager;
import scenes.game.CoinManager;
import scenes.game.LeakManager;

import java.util.ArrayList;

import static java.lang.Math.sqrt;

public class Archer extends Sprite implements EnemyInterface, Knockable {

    private final CoinManager coinManager;
    private ArrowManager arrowManager;
    private LeakManager leakManager;
    private int health = 2;
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
    private float ATTACK_DELAY = 3.0f;
    private int pathCounter = 1;
    private Player player;
    private boolean canAttack = true;
    private int ATTACK_RANGE = 250;

    private enum State {
        WALK,
        PURSUE,
        ATTACK,
        DEAD,
    }

    public Archer(float x, float y,ArrayList<Vector2> pathwayCoordinates,Player player, LeakManager leakManager, ArrowManager arrowManager, CoinManager coinManager) {
        super(new Texture("archer.png"));
        setPosition(x, y);
        this.coinManager = coinManager;
        this.nextPointToWalkTowards = pathwayCoordinates.get(0);
        this.pathwayCoordinates = pathwayCoordinates;
        this.player = player;
        this.leakManager = leakManager;
        this.arrowManager = arrowManager;
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
            float angle = (float)Math.atan2(dy, dx);
            arrowManager.createArrow(getX(), getY(), angle);
            canAttack = false;

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    canAttack = true;
                }
            }, ATTACK_DELAY);
        }

        double distanceEnemyPlayer = sqrt((getX() - player.getX()) * (getX()-player.getX()) + (getY()-player.getY()) * (getY()-player.getY()));
        if(distanceEnemyPlayer > ATTACK_RANGE){
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

        double distanceEnemyPlayer = sqrt((getX() - player.getX()) * (getX()-player.getX()) + (getY()-player.getY()) * (getY()-player.getY()));
        if(distanceEnemyPlayer <= distanceToPursue){
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
        double distanceEnemyPlayer = sqrt((getX() - player.getX()) * (getX()-player.getX()) + (getY()-player.getY()) * (getY()-player.getY()));
        if(distanceEnemyPlayer > distanceToPursue){
            stateMachine(State.WALK);
        }

        if (distanceEnemyPlayer <= ATTACK_RANGE) {
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

    public void dispose(){
    }
}
