package entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import entities.*;
import entities.enemies.logic.Melee;
import helpers.Debug;
import helpers.RedShader;
import scenes.game.*;

import java.util.ArrayList;

public class Porcupine implements Enemy, Knockable, Entity, Damageable, Collidable {

    private final CoinManager coinManager;
    private int health = 2;
    private boolean isDead = false;
    private boolean isRed = false;
    public final float SPEED = 1.5f;
    private int DAMAGE = 1;
    private float ATTACK_DELAY = 0.3f;
    private int pathCounter = 1;
    private float x;
    private float y;
    private int ATTACK_RANGE = 20;
    private Animation<TextureRegion> porcupineAttack;
    private Animation<TextureRegion> porcupineWalk;
    private float WALK_ANIMATION_SPEED = 0.13f;
    private float ATTACK_ANIMATION_SPEED = 0.1f;
    private float attackTime = 0f;
    private float walkTime = 0f;
    private final Melee updateLogic;

    @Override
    public Rectangle getHitbox() {
        return getBoundingRectangle();
    }

    @Override
    public ArrayList<Class> getIgnoreClassList() {
        ArrayList<Class> ignore = new ArrayList<>();
        ignore.add(Enemy.class);
        ignore.add(Mercenary.class);
        ignore.add(DeadZone.class);
        return ignore;
    }

    public Porcupine(float x, float y, ArrayList<Vector2> pathwayCoordinates, Player player, CoinManager coinManager, CollisionManager collisionManager, EntityManager entityManager) {
        this.x = x;
        this.y = y;
        this.coinManager = coinManager;
        initTextures();
        updateLogic = new Melee(this, entityManager, player, pathwayCoordinates, collisionManager);
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
        return new Rectangle(x, y, getWidth(), getHeight());
    }

    @Override
    public Texture getTexture() {
        return null;
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
        return 30;
    }

    @Override
    public float getHeight() {
        return 30;
    }

    @Override
    public int getDamage() {
        return DAMAGE;
    }

    @Override
    public float getAttackDelay() {
        return ATTACK_DELAY;
    }

    @Override
    public float getAttackRange() {
        return ATTACK_RANGE;
    }

    @Override
    public float getSpeed() {
        return SPEED;
    }

    @Override
    public void onAttackStart() {
        attackTime = 0f;
    }

    @Override
    public void draw(Batch batch, ShapeRenderer shapeRenderer) {
        if (isRed) {
            batch.setShader(RedShader.shaderProgram);
        } else {
            batch.setShader(null);
        }
        batch.begin();
        if (updateLogic.getState() == Melee.State.WALK || updateLogic.getState() == Melee.State.PURSUE) {
            batch.draw(porcupineWalk.getKeyFrame(walkTime, true), getX(), getY());
        } else if (updateLogic.getState() == Melee.State.ATTACK) {
            batch.draw(porcupineAttack.getKeyFrame(attackTime, false), getX(), getY());
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

    @Override
    public Vector2 getCenter() {
        return Geom.getCenter(this);
    }

    @Override
    public void update(float delta) {
        updateLogic.update(delta);
        attackTime += delta;
        walkTime += delta;

    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public void setY(float y) {
this.y = y;
    }
}
