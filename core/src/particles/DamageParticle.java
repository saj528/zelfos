package particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Timer;
import entities.Killable;
import helpers.GameInfo;
import scenes.game.WaveManager;

public class DamageParticle implements Particle, Killable {

    private final int amount;
    private float x;
    private float y;
    private float vy;
    private float vx;
    private boolean isDead;
    BitmapFont font = new BitmapFont();

    public DamageParticle(float x, float y, int amount) {
        this.amount = amount;
        this.x = x;
        this.y = y;
        this.vy = 7f;
        this.vx = 3f;
        this.isDead = false;

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                isDead = true;
            }
        }, 0.7f);
    }

    public void draw(Batch batch) {
        batch.begin();
        font.setColor(new Color(1, 1, 1, 1));
        font.getData().setScale(2);
        font.draw(batch, amount + "", x, y);
        batch.end();
    }

    public void update(float delta) {
        this.vy -= 0.3f;
        this.y += this.vy;
        this.x += this.vx;
    }

    @Override
    public boolean isDead() {
        return isDead;
    }
}
