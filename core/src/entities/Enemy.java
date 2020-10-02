package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;

import static java.lang.Math.sqrt;

public class Enemy extends Sprite {

    private int health;
    private boolean isDead = false;
    private boolean isRed = false;
    public final float ACCELERATION = 300.0f;
    private float current_point_y;
    boolean startOfGame = true;
    private int state;
    private float distanceToPursue = 100;
    private Vector2 nextPointToWalkTowards;
    private ArrayList<Vector2> pathwayCoordinates;
    private int pathCounter = 1;
    private Player player;



    private final String flashRedVertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
            + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
            + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
            + "uniform mat4 u_projTrans;\n" //
            + "varying vec4 v_color;\n" //
            + "varying vec2 v_texCoords;\n" //
            + "\n" //
            + "void main()\n" //
            + "{\n" //
            + "   v_color = vec4(1.0, 0.0, 0.0, 1.0);\n" //
            + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
            + "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
            + "}\n";

    private final String flashRedFragmentShader = "#ifdef GL_ES\n" //
            + "#define LOWP lowp\n" //
            + "precision mediump float;\n" //
            + "#else\n" //
            + "#define LOWP \n" //
            + "#endif\n" //
            + "varying LOWP vec4 v_color;\n" //
            + "varying vec2 v_texCoords;\n" //
            + "uniform sampler2D u_texture;\n" //
            + "void main()\n"//
            + "{\n" //
            + "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords).a;\n" //
            + "}";

    private final ShaderProgram flashRedShader = new ShaderProgram(flashRedVertexShader, flashRedFragmentShader);



    public Enemy(float x, float y,ArrayList<Vector2> pathwayCoordinates,Player player) {
        super(new Texture("enemy.png"));
        health = 5;
        setPosition(x, y);
        this.nextPointToWalkTowards = pathwayCoordinates.get(0);
        this.pathwayCoordinates = pathwayCoordinates;
        this.player = player;

    }

    public void stateMachine(int state){
        this.state = state;
        System.out.println("Current state: " + state);
        switch(state){
            case 0: // walk to end point
                walkToEnd();
                break;
            case 1:
                isDead = true;
                break;
            case 2:
                pursuePlayer();
                break;
        }
    }

    public void update(){
        if(startOfGame) {
            startOfGame = false;
            stateMachine(0);
        }else{
            stateMachine(state);
        }
    }


    private void walkToEnd() {

        double distanceEnemyPlayer = sqrt((getX() - player.getX()) * (getX()-player.getX()) + (getY()-player.getY()) * (getY()-player.getY()));
        if(distanceEnemyPlayer <= distanceToPursue){
            stateMachine(2);
        }

        double distanceFromCurrentPathGoal = sqrt((getX() - nextPointToWalkTowards.x) * (getX()-nextPointToWalkTowards.x) + (getY()-nextPointToWalkTowards.y) * (getY()-nextPointToWalkTowards.y));
        if(distanceFromCurrentPathGoal <= 0){
            if(pathwayCoordinates.size() > pathCounter){
                nextPointToWalkTowards.y = pathwayCoordinates.get(pathCounter).y;
                nextPointToWalkTowards.x = pathwayCoordinates.get(pathCounter).x;
                pathCounter++;
            }else{
                stateMachine(1);
                return;
            }
        }
        if(getY() < nextPointToWalkTowards.y) {
            setY(getY() + 1);
        }else if(getY() > nextPointToWalkTowards.y){
            setY(getY() - 1);
        }
        if(getX() < nextPointToWalkTowards.x){
            setX(getX() + 1);
        }else if(getX() > nextPointToWalkTowards.x){
            setX(getX() - 1);
        }
    }

    private void pursuePlayer(){
        double distanceEnemyPlayer = sqrt((getX() - player.getX()) * (getX()-player.getX()) + (getY()-player.getY()) * (getY()-player.getY()));
        System.out.println("distance to player" + distanceEnemyPlayer);
        System.out.println("distance to pursue" + distanceToPursue);
        if(distanceEnemyPlayer > distanceToPursue){
            System.out.println("The distance from the enemy to the player is less than the distance to pursue");
            stateMachine(0);
        }

        if(getY() < player.getY()) {
            setY(getY() + 1);
        }else if(getY() > player.getY()){
            setY(getY() - 1);
        }
        if(getX() < player.getX()){
            setX(getX() + 1);
        }else if(getX() > player.getX()){
            setX(getX() - 1);
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
            batch.setShader(flashRedShader);
        } else {
            batch.setShader(null);
        }
        batch.begin();
        batch.draw(this.getTexture(), getX(), getY());
        batch.end();
    }

    public boolean isDead() {
        return isDead;
    }
}
