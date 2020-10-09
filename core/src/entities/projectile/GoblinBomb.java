package entities.projectile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import entities.Entity;
import entities.Killable;
import entities.Player;
import scenes.game.CollisionManager;
import scenes.game.EntityManager;
import scenes.game.Geom;
import scenes.game.Physics;

public class GoblinBomb extends Sprite implements Killable, Entity {

    private final EntityManager entityManager;
    private final CollisionManager collisionManager;
    float SPEED = 3.0f;
    final int DAMAGE = 3;
    private int BLAST_RADIUS = 50;
    Player player;
    Vector2 destination;
    boolean isDead;

    public GoblinBomb(float x, float y, Vector2 destination, EntityManager entityManager, Player player, CollisionManager collisionManager) {
        super(new Texture("bomb.png"));
        this.collisionManager = collisionManager;
        setX(x);
        this.destination = destination;
        this.entityManager = entityManager;
        this.player = player;
        setY(y);
        isDead = false;

    }

    public void draw(Batch batch, ShapeRenderer shapeRenderer) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(1, 0, 0, 0.3f));
        shapeRenderer.circle(getX() + getWidth() / 2f, getY() + getHeight() / 2f, BLAST_RADIUS);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();
        super.draw(batch);
        batch.end();
    }

    @Override
    public Vector2 getCenter() {
        return Geom.getCenter(this);
    }

    public void update(float delta) {
        float dy = destination.y - getCenter().y;
        float dx = destination.x - getCenter().x;
        final float angle = (float)Math.atan2(dy, dx);

        setX(getX() + SPEED * (float)Math.cos(angle));
        setY(getY() + SPEED * (float)Math.sin(angle));

        if (getCenter().dst(destination) < 10) {
            isDead = true;
            if (Geom.distanceBetween(this, player) < BLAST_RADIUS) {
                player.damage(DAMAGE);
                Physics.knockback(this, player, 30, collisionManager);
            }
        }
    }

    public boolean isDead() {
        return isDead;
    }
}
