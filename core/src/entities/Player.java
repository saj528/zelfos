package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.util.ArrayList;

class AttackHitbox {
    public Rectangle up_attack;
    public Rectangle left_attack;
    public Rectangle right_attack;
    public Rectangle down_attack;

    public AttackHitbox(Player player) {
        int offset = 50;
        int size = 20;

        up_attack = new Rectangle(player.getX() + 10, player.getY() + offset + 10, size, size);
        left_attack = new Rectangle(player.getX() + 10 - offset, player.getY() + 10, size, size);
        right_attack = new Rectangle(player.getX() + 10 + offset, player.getY() + 10, size, size);
        down_attack = new Rectangle(player.getX() + 10, player.getY() - offset + 10, size, size);
    }

}

public class Player extends Sprite {

    private Vector2 input_vector = Vector2.Zero;
    private Vector2 motion = Vector2.Zero;
    public final float ACCELERATION = 300.0f;
    public final float MAX_SPEED = 10;

    private Texture playerUp = new Texture("player-up.png");
    private Texture playerDown = new Texture("player-down.png");
    private Texture playerLeft = new Texture("player-left.png");
    private Texture playerRight = new Texture("player-right.png");
    private boolean canAttack = true;
    private float ATTACK_COOLDOWN = 1;
    private float ATTACK_OFFSET = 0.3f;
    private int SWORD_DAMAGE = 1;

    public Player(float x, float y) {
        super(new Texture("player-down.png"));
        setPosition(x, y);
        setTexture(playerDown);
    }

    public void updatePlayerMovement(boolean left, boolean right, boolean up,boolean down, float delta){

        input_vector.x = (right ? 1 : 0) - (left ? 1 : 0);
        input_vector.y = (up ? 1 : 0) - (down ? 1 : 0);

        setX(getX() + input_vector.x * ACCELERATION * delta);
        setY(getY() + input_vector.y * ACCELERATION * delta);

    }
/*
    public void walkLeft() {
        setTexture(playerLeft);
        setX(getX() - SPEED);
    }

    public void walkRight() {
        setTexture(playerRight);
        setX(getX() + SPEED);
    }

    public void walkDown() {
        setTexture(playerDown);
        setY(getY() - SPEED);
    }

    public void walkUp() {
        setTexture(playerUp);
        setY(getY() + SPEED);
    }
*/
    public void attack(final ArrayList<Enemy> enemies) {
        if (!canAttack) return;
        canAttack = false;

        final Player player = this;

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                AttackHitbox hitbox = new AttackHitbox(player);

                for (Enemy enemy : enemies) {
                    if (hitbox.up_attack.overlaps(enemy.getBoundingRectangle()) ||
                            hitbox.left_attack.overlaps(enemy.getBoundingRectangle()) ||
                            hitbox.right_attack.overlaps(enemy.getBoundingRectangle()) ||
                            hitbox.down_attack.overlaps(enemy.getBoundingRectangle())) {
                        enemy.damage(SWORD_DAMAGE);
                        return;
                    }
                }
            }
        }, ATTACK_OFFSET);


        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                System.out.println("cooldown");
                canAttack = true;
            }
        }, ATTACK_COOLDOWN);
    }

    private Texture createHitboxTexture(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0, 1, 0, 1));
        pixmap.fillRectangle(0, 0, width, height);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
        Texture box = createHitboxTexture(20, 20);
        AttackHitbox hitbox = new AttackHitbox(this);
//        batch.draw(box, hitbox.top.x, hitbox.top.y);
//        batch.draw(box, hitbox.bottom.x, hitbox.bottom.y);
//        batch.draw(box, hitbox.left.x, hitbox.left.y);
//        batch.draw(box, hitbox.right.x, hitbox.right.y);
    }
}

