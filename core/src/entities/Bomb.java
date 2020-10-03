package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Timer;
import helpers.RedShader;

public class Bomb extends Sprite {
    private float BOMB_TIME = 4.0f;
    private boolean isDead = false;
    private boolean isRed = false;
    private float flashDelay = 0.7f;

    public Bomb(float x, float y) {
        super(new Texture("bomb.png"));
        setPosition(x, y);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                isDead = true;
            }
        }, BOMB_TIME);

        createFlashTimer();
    }

    public void createFlashTimer() {
        if (isDead) return;
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                flashDelay -= 0.1f;
                isRed = !isRed;
                createFlashTimer();
            }
        }, flashDelay);
    }

    public void draw(Batch batch) {
        if (isRed) {
            batch.setShader(RedShader.shaderProgram);
        } else {
            batch.setShader(null);
        }
        batch.begin();
        super.draw(batch);
        batch.end();
    }

    public boolean isDead() {
        return isDead;
    }
}
