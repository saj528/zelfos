package entities.structures;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import entities.Damageable;
import entities.Entity;
import entities.Killable;
import scenes.game.Collidable;
import scenes.game.Geom;

public class TownHall implements Entity, Damageable, Collidable {

    private final Sprite townHallSprite;
    private int health = 10;
    private float x;
    private float y;

    public TownHall(float x, float y) {
        Texture townHallTexture = new Texture("townhall.png");
        townHallSprite = new Sprite(townHallTexture);
        setX(x - townHallSprite.getWidth() / 2f);
        setY(y - townHallSprite.getHeight() / 2f);
    }

    @Override
    public void damage(int amount) {
        health -= amount;
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
        return townHallSprite.getBoundingRectangle();
    }

    @Override
    public Vector2 getCenter() {
        return Geom.getCenter(this);
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void draw(Batch batch) {
        batch.begin();
        batch.draw(townHallSprite, x, y);
        batch.end();
    }

    @Override
    public Rectangle getHitbox() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }
}
