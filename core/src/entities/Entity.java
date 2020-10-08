package entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public interface Entity {
    float getX();
    float getY();
    void setX(float x);
    void setY(float y);
    Rectangle getBoundingRectangle();
    Vector2 getCenter();
    void update(float delta);
    void draw(Batch batch, ShapeRenderer shapeRenderer);
}
