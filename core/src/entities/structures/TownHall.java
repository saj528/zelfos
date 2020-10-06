package entities.structures;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
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

import java.util.ArrayList;

public class TownHall implements Entity, Damageable, Collidable, Killable {

    private final Sprite townHallSprite;
    private int health = 10;
    private int maxHealth = 10;
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
        return new Rectangle(x, y, getWidth(), getHeight());
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

        Pixmap pixmap = new Pixmap((int)getWidth(), 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0, 0, 0, 1f));
        pixmap.fillRectangle(0, 0, (int)getWidth(), 10);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        batch.begin();
        batch.draw(texture, (float)getX(), (float)getY() + getHeight() + 10);
        batch.end();


        pixmap = new Pixmap((int)getWidth(), 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(1, 0, 0, 1f));
        pixmap.fillRectangle(0, 0, (int)getWidth() * health / maxHealth, 10);
        texture = new Texture(pixmap);
        pixmap.dispose();

        batch.begin();
        batch.draw(texture, (float)getX(), (float)getY() + getHeight() + 10);
        batch.end();
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
}
