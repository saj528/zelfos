package entities;

import com.badlogic.gdx.math.Rectangle;

public interface Knockable {
    float getX();
    float getY();
    Rectangle getBoundingRectangle();
    void setX(float x);
    void setY(float y);
}
