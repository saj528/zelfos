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
import entities.enemies.logic.Melee;
import helpers.Debug;
import helpers.RedShader;
import scenes.game.*;

import java.util.ArrayList;

public class Footman extends Sprite implements Enemy, Knockable, Entity, Damageable, Collidable {

    private static final float ATTACK_DELAY = 0.7f;
    private static final float ATTACK_RANGE = 25f;
    private static final int DAMAGE = 1;
    private final CoinManager coinManager;
    private int health = 7;
    private boolean isDead = false;
    private boolean isRed = false;
    public final float SPEED = 1.4f;
    private final Melee updateLogic;
    private final float WALK_ANIMATION_SPEED = 0.13f;
    private final float ATTACK_ANIMATION_SPEED = 0.3f;
    private Animation<TextureRegion> attackAnimation;
    private Animation<TextureRegion> walkAnimation;
    private float attackTime = 0f;
    private float walkTime = 0f;

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

    public Footman( float x, float y, ArrayList<Vector2 > pathwayCoordinates, Player player, CoinManager coinManager, CollisionManager collisionManager, EntityManager entityManager){
            super(new Texture("soldier_walking_6.png"), 0, 0, 32, 32);
            setPosition(x, y);
            attackTime = 0;
            walkTime = 0;
            this.coinManager = coinManager;
            updateLogic = new Melee(this, entityManager, player, pathwayCoordinates, collisionManager);
            initTextures();
        }

        private void initTextures () {
            Texture attackAnimationSheet = new Texture("soldier_attacking_4.png");
            TextureRegion[][] attackAnimationSheetRegion = TextureRegion.split(attackAnimationSheet, attackAnimationSheet.getWidth() / 4, attackAnimationSheet.getHeight());

            TextureRegion[] attackAnimationFrames = new TextureRegion[4];
            attackAnimationFrames[0] = attackAnimationSheetRegion[0][0];
            attackAnimationFrames[1] = attackAnimationSheetRegion[0][1];
            attackAnimationFrames[2] = attackAnimationSheetRegion[0][2];
            attackAnimationFrames[3] = attackAnimationSheetRegion[0][3];
            attackAnimation = new Animation<TextureRegion>(ATTACK_ANIMATION_SPEED, attackAnimationFrames);

            Texture walkAnimationSheet = new Texture("soldier_walking_6.png");
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
        public void update ( float delta){
            updateLogic.update(delta);
            attackTime += delta;
            walkTime += delta;
        }

        public void damage ( int amount){
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
        public int getDamage () {
            return DAMAGE;
        }

        @Override
        public float getAttackDelay () {
            return ATTACK_DELAY;
        }

        @Override
        public float getAttackRange () {
            return ATTACK_RANGE;
        }

        @Override
        public float getSpeed () {
            return SPEED;
        }

        @Override
        public void draw (Batch batch){
            if (isRed) {
                batch.setShader(RedShader.shaderProgram);
            } else {
                batch.setShader(null);
            }

            batch.begin();
            if (updateLogic.getState() == Melee.State.WALK || updateLogic.getState() == Melee.State.PURSUE) {
                batch.draw(walkAnimation.getKeyFrame(walkTime, true), getX(), getY());
            } else if (updateLogic.getState() == Melee.State.ATTACK) {
                batch.draw(attackAnimation.getKeyFrame(attackTime, false), getX()-15, getY()-20);
            }
            batch.end();
            batch.setShader(null);
            Debug.drawHitbox(batch, getBoundingRectangle());
        }


    @Override
    public Rectangle getBoundingRectangle() {
        return new Rectangle(getX() + 2, getY(), getWidth() - 14, getHeight() - 5);
    }
    @Override
        public void onAttackStart() {
            attackTime = 0f;
        }

        public boolean isDead () {
            return isDead;
        }

        public void dispose () {
        }

        @Override
        public Vector2 getCenter () {
            return Geom.getCenter(this);
        }


    }
