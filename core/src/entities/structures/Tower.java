package entities.structures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import entities.Damageable;
import entities.Entity;
import entities.Player;
import entities.enemies.Enemy;
import entities.projectile.Bullet;
import helpers.EntityHealthBar;
import helpers.WhiteShader;
import scenes.game.*;

import java.util.ArrayList;
import java.util.List;

public class Tower implements Entity, Collidable, Damageable {
    private final EntityManager entityManager;
    private final CoinManager coinManager;
    private final WaveManager waveManager;
    private final Player player;
    private final EntityHealthBar healthBar;
    private float x;
    private int COST = 10;
    private float y;
    private boolean canFire = true;
    private Texture texture;
    private float ATTACK_COOLDOWN = 3.0f;
    private CollisionManager collisionManager;
    private float TOWER_RANGE = 200f;
    private boolean isActive = false;
    BitmapFont font = new BitmapFont();
    private int MAX_HEALTH = 20;
    private int health = MAX_HEALTH;
    private boolean showText = false;

    public Tower(float x, float y, EntityManager entityManager, CollisionManager collisionManager, CoinManager coinManager, WaveManager waveManager, Player player) {
        this.x = x;
        this.y = y;
        this.texture = new Texture("tower.png");
        this.entityManager = entityManager;
        this.collisionManager = collisionManager;
        this.waveManager = waveManager;
        this.player = player;
        this.coinManager = coinManager;
        this.healthBar = new EntityHealthBar(this);

    }

    public void activate() {
        isActive = true;
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
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    @Override
    public Rectangle getBoundingRectangle() {
        return new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }

    @Override
    public Vector2 getCenter() {
        return Geom.getCenter(this);
    }

    @Override
    public void update(float delta) {
        showText = false;

        if (isActive) {
            if (canFire) {
                ArrayList<Entity> enemies = entityManager.getEntitiesByType(Enemy.class);
                ArrayList<Entity> enemiesInRange = Geom.getEntitiesInRange(enemies, this, TOWER_RANGE);
                if (enemiesInRange.size() > 0) {
                    Entity enemy = enemiesInRange.get(0);
                    entityManager.addEntity(new Bullet(getCenter().x, getCenter().y, Geom.angleBetween(this, enemy), entityManager, collisionManager));
                    canFire = false;

                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            canFire = true;
                        }
                    }, ATTACK_COOLDOWN);
                }
            }
        } else {
            if (canBuy()) {
                if (Gdx.input.isKeyPressed(Input.Keys.E)) {
                    isActive = true;
                    coinManager.removeCoins(COST);
                }
            }

        }
    }

    public boolean canBuy() {
        return !isActive && waveManager.isOnIntermission() && coinManager.getTotalCoins() >= COST && Geom.distanceBetween(player, this) < 100;
    }

    @Override
    public void draw(Batch batch, ShapeRenderer shapeRenderer) {
        if (!isActive) {
            batch.setShader(WhiteShader.shaderProgram);
        }

        batch.begin();
        batch.draw(texture, x, y);
        batch.end();
        batch.setShader(null);

        if (isActive) {
            healthBar.draw(batch, shapeRenderer);
        }

        if (canBuy()) {
            batch.begin();
            font.setColor(new Color(0, 1, 0, 1));
            font.draw(batch, "Buy Tower " + COST + "Gp (E)", x, y + texture.getHeight() + 20);
            batch.end();
        }
    }

    @Override
    public Rectangle getHitbox() {
        return getBoundingRectangle();
    }

    @Override
    public ArrayList<Class> getIgnoreClassList() {
        return null;
    }

    @Override
    public void damage(int amount) {
        this.health -= amount;
        if (amount <= 0) {
            isActive = false;
        }
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public int getMaxHealth() {
        return MAX_HEALTH;
    }
}
