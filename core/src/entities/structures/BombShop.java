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
import entities.Entity;
import entities.Player;
import helpers.WhiteShader;
import scenes.game.*;

import java.util.ArrayList;

public class BombShop implements Entity, Collidable {
    private final Player player;
    private final CoinManager coinManager;
    private final EntityManager entityManager;
    private final CollisionManager collisionManager;
    private final WaveManager waveManager;
    private float x;
    private float y;
    private int COST = 4;
    private boolean showText = false;
    private boolean canBuyAgain = true;
    private Texture bombShop;
    BitmapFont font = new BitmapFont();

    public BombShop(float x, float y, Player player, CoinManager coinManager, EntityManager entityManager, CollisionManager collisionManager, WaveManager waveManager) {
        this.x = x;
        this.y = y;
        this.waveManager = waveManager;
        this.entityManager = entityManager;
        this.coinManager = coinManager;
        this.collisionManager = collisionManager;
        this.player = player;
        bombShop = new Texture("potionshop.png");
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
        return new Rectangle(x, y, bombShop.getWidth(), bombShop.getHeight());
    }

    @Override
    public Vector2 getCenter() {
        return Geom.getCenter(this);
    }

    @Override
    public void update(float delta) {
        if (!waveManager.isOnIntermission()) return;

        showText = false;

        float dist = Geom.distanceBetween(player, this);
        if (dist < 100) {
            showText = true;

            boolean use = Gdx.input.isKeyPressed(Input.Keys.E);
            if (use && canBuy()) {
                canBuyAgain = false;
                coinManager.removeCoins(COST);
                player.addBomb();
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
    public void draw(Batch batch, ShapeRenderer shapeRenderer) {
        if (!waveManager.isOnIntermission()) {
            batch.setShader(WhiteShader.shaderProgram);
        }
        batch.begin();
        batch.draw(bombShop, x, y);
        batch.end();
        batch.setShader(null);

        if (showText && waveManager.isOnIntermission()) {
            batch.begin();
            if (canBuy()) {
                font.setColor(new Color(0, 1, 0, 1));
            } else {
                font.setColor(new Color(1, 0, 0, 1));
            }
            font.draw(batch, "Buy Bomb " + COST + "Gp (E)", x, y + bombShop.getHeight() + 20);
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
