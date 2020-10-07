package entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import entities.enemies.Enemy;
import scenes.game.Collidable;
import scenes.game.Geom;

import java.util.ArrayList;

public class DeadZone implements Collidable, Entity {
    private final Rectangle hitbox;

    public DeadZone(Rectangle hitbox) {
        this.hitbox = hitbox;
    }

    @Override
    public Rectangle getHitbox() {
        return hitbox;
    }

    @Override
    public ArrayList<Class> getIgnoreClassList() {
        ArrayList<Class> ignoreClassList = new ArrayList<>();
        return ignoreClassList;
    }

    @Override
    public float getX() {
        return hitbox.getX();
    }

    @Override
    public float getY() {
        return hitbox.getY();
    }

    @Override
    public void setX(float x) {
        hitbox.setX(x);
    }

    @Override
    public void setY(float y) {
        hitbox.setY(y);
    }

    @Override
    public Rectangle getBoundingRectangle() {
        return hitbox;
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

    }
}
