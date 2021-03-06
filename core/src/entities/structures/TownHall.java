package entities.structures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import entities.*;
import helpers.EntityHealthBar;
import helpers.WhiteShader;
import scenes.game.Collidable;
import scenes.game.Geom;
import scenes.game.WaveManager;

import java.util.ArrayList;

public class TownHall implements Entity, Damageable, Collidable, Killable, Friendly {

    private final Sprite townHallSprite;
    private final WaveManager waveManager;
    private int MAX_HEALTH = 10;
    private int health = MAX_HEALTH;
    private boolean canBuyAgain = true;
    private boolean showText = false;
    private EntityHealthBar healthBar;
    private float x;
    private float y;
    private Player player;
    BitmapFont font = new BitmapFont();

    public TownHall(float x, float y, WaveManager waveManager, Player player) {
        Texture townHallTexture = new Texture("townhall.png");
        townHallSprite = new Sprite(townHallTexture);
        setX(x - townHallSprite.getWidth() / 2f);
        setY(y - townHallSprite.getHeight() / 2f);
        this.waveManager = waveManager;
        this.player = player;
        this.healthBar = new EntityHealthBar(this);
    }

    @Override
    public void damage(int amount) {
        health -= amount;
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

    public float getWidth() {
        return townHallSprite.getWidth();
    }

    public float getHeight() {
        return townHallSprite.getHeight();
    }

    @Override
    public Rectangle getBoundingRectangle() {
        return new Rectangle(x, y, getWidth(), getHeight());
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
        if (dist < 150) {
            showText = true;

            boolean qDown = Gdx.input.isKeyPressed(Input.Keys.Q);
            if (qDown && canBuy()) {
                canBuyAgain = false;
                player.removeSkillPoint();
                player.unlockWhirlwind();

                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        canBuyAgain = true;
                    }
                }, 0.5f);
            }

            boolean shiftDown = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);
            if (shiftDown && canBuy()) {
                canBuyAgain = false;
                player.removeSkillPoint();
                player.unlockDodge();

                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        canBuyAgain = true;
                    }
                }, 0.5f);
            }

            boolean fDown = Gdx.input.isKeyPressed(Input.Keys.F);
            if (fDown && canBuy()) {
                canBuyAgain = false;
                player.removeSkillPoint();
                player.unlockMusket();

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
        return canBuyAgain && player.getSkillPoints() > 0;
    }


    @Override
    public void draw(Batch batch, ShapeRenderer shapeRenderer) {
        if (!waveManager.isOnIntermission()) {
            batch.setShader(WhiteShader.shaderProgram);
        }

        batch.begin();
        batch.draw(townHallSprite, x, y);
        batch.end();

        healthBar.draw(batch, shapeRenderer);

        if (showText && waveManager.isOnIntermission()) {
            batch.begin();

            if (canBuy()) {
                font.setColor(new Color(0, 1, 0, 1));
            } else {
                font.setColor(new Color(1, 0, 0, 1));
            }
            if (!player.isDodgeUnlocked()){
                font.draw(batch, "Unlock Dodge (SHIFT to buy & use)", x, y + getHeight() + 60);
            }
            if (!player.isWhirlwindUnlocked()) {
                font.draw(batch, "Unlock Whirlwind (Q to buy & use)", x, y + getHeight() + 80);
            }
            if (!player.isMusketUnlocked()) {
                font.draw(batch, "Unlock Musket (F to buy & use)", x, y + getHeight() + 100);
            }
            batch.end();
        }

        batch.setShader(null);
    }

    @Override
    public Rectangle getHitbox() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public ArrayList<Class> getIgnoreClassList() {
        return new ArrayList<>();
    }

    @Override
    public boolean isDead() {
        return health <= 0;
    }

    @Override
    public boolean isTargetable() {
        return true;
    }
}
