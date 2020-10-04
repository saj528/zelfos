package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Align;

public class Arrow extends Sprite {

    float SPEED = 4.0f;
    final int DAMAGE = 1;
    float angle;
    Player player;
    boolean isDead;

    public Arrow(float x, float y, float angle, Player player) {
        super(new Texture("arrow.png"));
        this.angle = angle;
        setX(x);
        setY(y);
        this.player = player;
        isDead = false;
    }

    public void draw(Batch batch) {
        setOrigin(getWidth() / 2, getHeight() / 2);
        setRotation(angle / (float)Math.PI * 180);
        batch.begin();
        super.draw(batch);
        batch.end();
    }

    public void update(float delta) {
        setX(getX() + SPEED * (float)Math.cos(angle));
        setY(getY() + SPEED * (float)Math.sin(angle));

        if (getBoundingRectangle().overlaps(player.getBoundingRectangle())) {
            player.damage(DAMAGE);
            isDead = true;
        }
    }

    public boolean isDead() {
        return isDead;
    }
}
