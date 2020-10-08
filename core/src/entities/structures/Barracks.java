package entities.structures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import entities.Entity;
import entities.Mercenary;
import entities.Player;
import helpers.GameInfo;
import scenes.game.*;

import java.util.ArrayList;

public class Barracks implements Entity, Collidable {

    private final Player player;
    private final CoinManager coinManager;
    private final EntityManager entityManager;
    private final Vector2 northGuardPost;
    private final Vector2 northBasePost;
    private final CollisionManager collisionManager;
    private float x;
    private float y;
    private int COST = 5;
    private boolean showText = false;
    private boolean canBuyAgain = true;
    private WaveManager waveManager;
    private Texture barracks;
    BitmapFont font = new BitmapFont();

    public Barracks(float x, float y, Player player, CoinManager coinManager, EntityManager entityManager, Vector2 northGuardPost, Vector2 northBasePost, CollisionManager collisionManager, WaveManager waveManager) {
        this.x = x;
        this.y = y;
        this.waveManager = waveManager;
        this.entityManager = entityManager;
        this.coinManager = coinManager;
        this.northGuardPost = northGuardPost;
        this.northBasePost = northBasePost;
        this.collisionManager = collisionManager;
        this.player = player;
        barracks = new Texture("barracks.png");
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
        return new Rectangle(x, y, barracks.getWidth(), barracks.getHeight());
    }

    @Override
    public Vector2 getCenter() {
        return Geom.getCenter(this);
    }

    @Override
    public void update(float delta) {
        showText = false;

        float dist = Geom.distanceBetween(player, this);
        if (dist < 100) {
            showText = true;

            boolean use = Gdx.input.isKeyPressed(Input.Keys.E);
            if (use && canBuy()) {
                canBuyAgain = false;
                coinManager.removeCoins(COST);
                entityManager.addEntity(new Mercenary(northGuardPost, northBasePost, collisionManager, waveManager, entityManager));

                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        canBuyAgain = true;
                    }
                }, 0.5f);
            }
        }
    }

    private boolean canBuy() {
        return canBuyAgain && coinManager.getTotalCoins() >= COST;
    }

    @Override
    public void draw(Batch batch) {
        batch.begin();
        batch.draw(barracks, x, y);
        batch.end();

        if (showText) {
            batch.begin();
            if (canBuy()) {
                font.setColor(new Color(0, 1, 0, 1));
            } else {
                font.setColor(new Color(1, 0, 0, 1));
            }
            font.draw(batch, "Hire Mercenary " + COST + "GP (E)", x, y + barracks.getHeight() + 20);
            batch.end();
        }

    }

    @Override
    public Rectangle getHitbox() {
        return getBoundingRectangle();
    }

    @Override
    public ArrayList<Class> getIgnoreClassList() {
        return new ArrayList<>();
    }
}
