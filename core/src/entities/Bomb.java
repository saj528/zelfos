package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import helpers.RedShader;

public class Bomb extends Sprite implements Knockable {
    private float BOMB_TIME = 3.0f;
    private boolean isDead = false;
    private boolean isRed = false;
    private float flashDelay = 0.5f;
    private int BLAST_RADIUS = 100;
    private int BLAST_DAMAGE = 3;

    public Bomb(float x, float y, final EnemyManager enemyManager) {
        super(new Texture("bomb.png"));
        setPosition(x, y);

        final Sprite bomb = this;

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                isDead = true;
                Vector2 bombPosition = new Vector2(getX() + getWidth() / 2f, getY() + getHeight() / 2f);
                for (EnemyInterface enemy : enemyManager.getEnemies()) {
                    Vector2 enemyPosition = new Vector2(enemy.getX() + enemy.getWidth() / 2f, enemy.getY() + enemy.getHeight() / 2f);
                    float distance = enemyPosition.dst(bombPosition);
                    if (distance < BLAST_RADIUS) {
                        enemy.damage(BLAST_DAMAGE);
                        Physics.knockback((Knockable)bomb, (Knockable)enemy, 50);
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

    public void draw(Batch batch, ShapeRenderer shapeRenderer) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(1, 0, 0, 0.3f));
        shapeRenderer.circle(getX() + getWidth() / 2f, getY() + getHeight() / 2f, BLAST_RADIUS);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        if (isRed) {
            batch.setShader(RedShader.shaderProgram);
        } else {
            batch.setShader(null);
        }

        batch.begin();
        super.draw(batch);
        batch.end();

        batch.setShader(null);

    }

    public boolean isDead() {
        return isDead;
    }
}
