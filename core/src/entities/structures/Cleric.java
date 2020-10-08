package entities.structures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import entities.Entity;
import entities.Player;
import scenes.game.CoinManager;
import scenes.game.Collidable;
import scenes.game.Geom;

import java.util.ArrayList;

public class Cleric implements Entity, Collidable {

    private static final float ANIMATION_SPEED = 0.5f;
    private final Player player;
    private final CoinManager coinManager;
    private Animation<TextureRegion> clericHealingAnime;
    private Animation<TextureRegion> clericIdleAnime;
    private float x;
    private float y;
    private int COST = 1;
    private boolean showText = false;
    private boolean canBuyAgain = true;
    public static int POTION_HEAL = 2;
    private Texture cleric;
    private boolean currentlyHealing = false;
    private float healingTime = 0f;
    BitmapFont font = new BitmapFont();


    public Cleric(float x, float y, Player player, CoinManager coinManager) {
        this.x = x;
        this.y = y;
        this.coinManager = coinManager;
        this.player = player;
        cleric = new Texture("clericIdle.png");
        initTextures();
    }

    private void initTextures() {

        Texture clericSheet = new Texture("cleric.png");
        TextureRegion[][] clericSheetRegion = TextureRegion.split(clericSheet, clericSheet.getWidth() / 4, clericSheet.getHeight());


        TextureRegion[] clericHealing = new TextureRegion[3];
        clericHealing[0] = clericSheetRegion[0][1];
        clericHealing[1] = clericSheetRegion[0][2];
        clericHealing[2] = clericSheetRegion[0][3];
        clericHealingAnime = new Animation<TextureRegion>(ANIMATION_SPEED, clericHealing);

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
        return new Rectangle(x, y, cleric.getWidth(), cleric.getHeight());
    }

    @Override
    public Vector2 getCenter() {
        return Geom.getCenter(this);
    }

    @Override
    public void update(float delta) {
        showText = false;
        healingTime += delta;
        float dist = Geom.distanceBetween(player, this);
        if (dist < 100) {
            showText = true;

            boolean use = Gdx.input.isKeyPressed(Input.Keys.E);
            if (use && canBuy() ) {
                canBuyAgain = false;
                coinManager.removeCoins(COST);
                currentlyHealing = true;
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        player.setLives(player.getLives() + 1);
                    }
                }, 0.3f);


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
        return canBuyAgain && coinManager.getTotalCoins() >= COST && player.getLives() != player.getMaxLives();
    }

    @Override
    public void draw(Batch batch) {
        batch.begin();
        if (currentlyHealing) {
            batch.draw(clericHealingAnime.getKeyFrame(healingTime, true), getX(), getY());
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    currentlyHealing = false;
                }
            }, 0.5f);
        } else {
            batch.draw(cleric, x, y);
        }
        batch.end();

        if (showText) {
            batch.begin();
            if (canBuy()) {
                font.setColor(new Color(0, 1, 0, 1));
            } else {
                font.setColor(new Color(1, 0, 0, 1));
            }
            font.draw(batch, "Heal " + COST + "Gp (E)", x, y + cleric.getHeight() + 20);
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
