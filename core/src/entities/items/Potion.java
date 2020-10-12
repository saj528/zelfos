package entities.items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import entities.Entity;
import entities.Player;
import scenes.game.CoinManager;

public class Potion implements Item, Entity {



    private Player player;
    private CoinManager coinManager;
    private Texture potionFull;
    private Boolean currentlyOwn = false;
    private int x;
    private int y;
    private final int HEAL_AMOUNT = 3;

    public Potion(Player player, CoinManager coinManager) {
        this.player = player;
        this.coinManager = coinManager;
        potionFull = new Texture("healthBottleFull.png");

    }

    @Override
    public Texture getTexture() {
        return potionFull;
    }

    @Override
    public Boolean currentlyOwn() {
        return currentlyOwn;
    }

    @Override
    public void useItem() {
        player.setLives(player.getLives() + HEAL_AMOUNT);
        currentlyOwn = false;
    }

    @Override
    public void buyItem() {
        currentlyOwn = true;
    }

    @Override
    public float getX() {
        return 0;
    }

    @Override
    public float getY() {
        return 0;
    }

    @Override
    public void setX(float x) {

    }

    @Override
    public void setY(float y) {

    }

    @Override
    public Rectangle getBoundingRectangle() {
        return null;
    }

    @Override
    public Vector2 getCenter() {
        return null;
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void draw(Batch batch, ShapeRenderer shapeRenderer) {

    }
}
