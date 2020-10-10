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
import entities.enemies.Enemy;
import scenes.game.*;
import helpers.RedShader;

import java.util.ArrayList;
import java.util.List;

public class Bomb extends Sprite implements Knockable, Killable, Entity {
    private final float BOMB_TIME = 2.0f;
    private final int BLAST_RADIUS = 80;
    private final int BLAST_DAMAGE = 3;
    private final CollisionManager collisionManager;
    private boolean isDead = false;
    private boolean isRed = false;
    private float flashDelay = 0.5f;

    public Bomb(float x, float y, final EntityManager entityManager, final CollisionManager collisionManager) {
        super(new Texture("bomb.png"));
        setPosition(x, y);
        this.collisionManager = collisionManager;
        final Entity bomb = this;
        final ArrayList<Entity> enemyEntities = (ArrayList<Entity>)(List<?>)entityManager.getEntitiesByType(Enemy.class);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                isDead = true;
                ArrayList<Entity> entitiesInRange = Geom.getEntitiesInRange(enemyEntities, bomb, BLAST_RADIUS);
                for (Entity entity : entitiesInRange) {
                    if (entity instanceof Knockable) {
                        Physics.knockback(bomb, (Knockable)entity, 50, collisionManager);
                    }
                    if (entity instanceof Damageable) {
                        ((Damageable)entity).damage(BLAST_DAMAGE);
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

    @Override
    public Vector2 getCenter() {
        return Geom.getCenter(this);
    }

    @Override
    public void update(float delta) {

    }
}
