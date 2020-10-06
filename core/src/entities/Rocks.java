package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import scenes.game.Collidable;
import scenes.game.Geom;

import java.util.ArrayList;

public class Rocks implements Entity, Collidable {

    private Sprite rocksSprite;
    private float x;
    private float y;

    public Rocks(float x, float y) {
        rocksSprite = new Sprite(new Texture("rocks.png"));
        this.x = x - rocksSprite.getWidth() / 2f;
        this.y = y - rocksSprite.getHeight() / 2f;
    }


    @Override
    public ArrayList<Class> getIgnoreClassList() {
        return new ArrayList<>();
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
        return new Rectangle(getX(), getY(), rocksSprite.getWidth(), rocksSprite.getHeight());
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
        batch.draw(rocksSprite, x, y);
        batch.end();
    }

    @Override
    public Rectangle getHitbox() {
        return new Rectangle(getX(), getY(), rocksSprite.getWidth(), rocksSprite.getHeight());
    }
}
