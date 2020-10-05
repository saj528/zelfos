package entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import entities.Killable;

public interface EnemyInterface extends Killable {

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
