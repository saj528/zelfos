package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

public interface EnemyInterface {

    boolean isDead();

    void update();

    void damage(int amount);

    Rectangle getBoundingRectangle();

    void draw(Batch batch);

    Texture getTexture();

    float getX();

    float getY();

    float getWidth();

    float getHeight();
}
