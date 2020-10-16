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
import entities.enemies.logic.Range;
import entities.projectile.Arrow;
import helpers.Debug;
import helpers.RedShader;
import scenes.game.*;

import java.util.ArrayList;

public class Archer extends Sprite implements Enemy, Knockable, Entity, Damageable, Collidable, Ranged {

    private final CoinManager coinManager;
    private final CollisionManager collisionManager;
    private final EntityManager entityManager;
    private int MAX_HEALTH = 3;
    private int health = MAX_HEALTH;
    private boolean isDead = false;
    private boolean isRed = false;
    public final int SPEED = 1;
    public final float ATTACK_DELAY = 3.0f;
    public final float ATTACK_RANGE = 150;
    public final int DAMAGE = 1;
    private Player player;
    private final float WALK_ANIMATION_SPEED = 0.13f;
    private final float ATTACK_ANIMATION_SPEED = 0.15f;
    private Animation<TextureRegion> attackAnimation;
    private Animation<TextureRegion> walkAnimation;
    private Range updateLogic;
    private float attackTime = 0f;
    private float walkTime = 0f;

    @Override
    public void fireProjectile(Entity target) {
        Vector2 targetCenter = target.getCenter();
        float dy = targetCenter.y - getCenter().y;
        float dx = targetCenter.x - getCenter().x;
        final float angle = (float)Math.atan2(dy, dx);
        entityManager.addEntity(new Arrow(getCenter().x, getCenter().y, angle, player, collisionManager));
    }

    public Archer(float x, float y,ArrayList<Vector2> pathwayCoordinates, Player player, CoinManager coinManager, CollisionManager collisionManager, EntityManager entityManager) {
        super(new Texture("archer_walking_6.png"), 0, 0, 32, 32);
        setPosition(x, y);
        attackTime = 0;
        walkTime = 0;
        this.entityManager = entityManager;
        this.collisionManager = collisionManager;
        this.coinManager = coinManager;
        this.player = player;
        initTextures();
        updateLogic = new Range(this, entityManager, player, pathwayCoordinates, collisionManager);
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

    @Override
    public void update(float delta){
        attackTime += delta;
        walkTime += delta;
        updateLogic.update(delta);
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
    public int getHealth() {
        return health;
    }

    @Override
    public int getMaxHealth() {
        return MAX_HEALTH;
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
        if(updateLogic.getState() == Range.State.WALK || updateLogic.getState() == Range.State.PURSUE){
            batch.draw(walkAnimation.getKeyFrame(walkTime, true), getX(), getY());
        }else if(updateLogic.getState() == Range.State.ATTACK){
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
