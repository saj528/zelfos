package entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

public class LevelUpParticle implements Entity, Killable {

    private float x;
    private float y;
    private float vy;
    private float vx;
    private boolean isDead;
    BitmapFont font = new BitmapFont();

    public LevelUpParticle(float x, float y) {
        this.x = x;
        this.y = y;
        this.vy = 9f;
        this.vx = (float) (Math.random() * 6f - 3f);
        this.isDead = false;

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                isDead = true;
            }
        }, 0.7f);
    }

    public void draw(Batch batch, ShapeRenderer shapeRenderer) {
        batch.begin();
        font.setColor(new Color(0, 1, 0, 1));
        font.getData().setScale(2.0f);
        font.draw(batch, "LEVEL UP!", x, y);
        batch.end();
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
        return null;
    }

    @Override
    public Vector2 getCenter() {
        return null;
    }

    public void update(float delta) {
        this.vy -= 0.4f;
        this.y += this.vy;
        this.x += this.vx;
    }

    @Override
    public boolean isDead() {
        return isDead;
    }
}
