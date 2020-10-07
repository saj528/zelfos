package entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
    private static final float ATTACK_RANGE = 10f;
    private static final int DAMAGE = 1;
    private final CoinManager coinManager;
    private final Player player;
    private int health = 8;
    private boolean isDead = false;
    private boolean isRed = false;
    public final float SPEED = 1.4f;
    private Melee updateLogic;

    @Override
    public Rectangle getHitbox() {
        return getBoundingRectangle();
    }


    @Override
    public ArrayList<Class> getIgnoreClassList() {
        ArrayList<Class> ignore = new ArrayList<>();
        ignore.add(Enemy.class);
        ignore.add(DeadZone.class);
        return ignore;
    }

    public Footman(float x, float y, ArrayList<Vector2> pathwayCoordinates, Player player,  CoinManager coinManager, CollisionManager collisionManager, EntityManager entityManager) {
        super(new Texture("footman.png"));
        setPosition(x, y);
        this.coinManager = coinManager;
        this.player = player;
        updateLogic = new Melee(this, entityManager, player, pathwayCoordinates, collisionManager);
    }


    @Override
    public void update(float delta) {
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

    public void dispose() {
    }

    @Override
    public Vector2 getCenter() {
        return Geom.getCenter(this);
    }


}
