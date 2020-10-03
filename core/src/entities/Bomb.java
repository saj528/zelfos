package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import helpers.RedShader;

public class Bomb extends Sprite {
    private float BOMB_TIME = 4.0f;
    private boolean isDead = false;
    private boolean isRed = false;
    private float flashDelay = 0.7f;
    private int BLAST_RADIUS = 300;
    private int BLAST_DAMAGE = 10;

    public Bomb(float x, float y, final EnemyManager enemyManager) {
        super(new Texture("bomb.png"));
        setPosition(x, y);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                isDead = true;
                Vector2 bombPosition = new Vector2(getX(), getY());
                for (Enemy enemy : enemyManager.getEnemies()) {
                    Vector2 enemyPosition = new Vector2(enemy.getX(), enemy.getY());
                    float distance = enemyPosition.dst(bombPosition);
                    if (distance < BLAST_RADIUS) {
                        enemy.damage(BLAST_DAMAGE);
                    }
                }
            }
        }, BOMB_TIME);

        createFlashTimer();
    }

    public void createFlashTimer() {
        if (isDead) return;
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                flashDelay = Math.max(flashDelay - 0.1f, 0.2f);
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
