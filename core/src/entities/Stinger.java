package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import scenes.game.CollisionManager;
import scenes.game.Geom;
import scenes.game.Physics;

public class Stinger extends Sprite implements Killable, Entity {

    private final CollisionManager collisionManager;
    float SPEED = 3.5f;
    final int DAMAGE = 1;
    float angle;
    Player player;
    boolean isDead;

    public Stinger(float x, float y, float angle, Player player, CollisionManager collisionManager) {
        super(new Texture("stinger.png"));
        this.angle = angle;
        setX(x);
        this.collisionManager = collisionManager;
        setY(y);
        this.player = player;
        isDead = false;
    }

    public void draw(Batch batch, ShapeRenderer shapeRenderer) {
        setOrigin(getWidth() / 2, getHeight() / 2);
        setRotation(angle / (float)Math.PI * 180);
        batch.begin();
        super.draw(batch);
        batch.end();
    }

    @Override
    public Vector2 getCenter() {
        return Geom.getCenter(this);
    }

    public void update(float delta) {
        setX(getX() + SPEED * (float)Math.cos(angle));
        setY(getY() + SPEED * (float)Math.sin(angle));

        if (getBoundingRectangle().overlaps(player.getBoundingRectangle())) {
            player.damage(DAMAGE);
            Physics.knockback(this, player, 10, collisionManager);
            isDead = true;
        }
    }

    public boolean isDead() {
        return isDead;
    }
}
