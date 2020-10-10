package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import scenes.game.Collidable;
import scenes.game.Geom;

import java.util.ArrayList;

public class Rocks implements Entity, Collidable, Killable {

    private final Direction direction;
    private Sprite rocksSprite;
    private float x;
    private float y;
    private boolean isDead;

    @Override
    public boolean isDead() {
        return isDead;
    }

    public void remove() {
        isDead = true;
    }

    public enum Direction {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }

    public Rocks(float x, float y, Direction direction) {
        rocksSprite = new Sprite(new Texture("rocks.png"));
        this.x = x - rocksSprite.getWidth() / 2f;
        this.y = y - rocksSprite.getHeight() / 2f;
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
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
    public void draw(Batch batch, ShapeRenderer shapeRenderer) {
        batch.begin();
        batch.draw(rocksSprite, x, y);
        batch.end();
    }

    @Override
    public Rectangle getHitbox() {
        return new Rectangle(getX(), getY(), rocksSprite.getWidth(), rocksSprite.getHeight());
    }
}
