package entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;

class AttackHitbox {
    public Rectangle top;
    public Rectangle left;
    public Rectangle right;
    public Rectangle bottom;

    public AttackHitbox(Player player) {
        int offset = 50;
        int size = 20;

        top = new Rectangle(player.getX() + 10, player.getY() + offset + 10, size, size);
        left = new Rectangle(player.getX() + 10 - offset, player.getY() + 10, size, size);
        right = new Rectangle(player.getX() + 10 + offset, player.getY() + 10, size, size);
        bottom = new Rectangle(player.getX() + 10, player.getY() - offset + 10, size, size);
    }
}

public class Player extends Sprite {
    public float SPEED = 3.0f;
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

    public void attack(final ArrayList<Enemy> enemies) {
        if (!canAttack) return;
        canAttack = false;

        final Player player = this;

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                AttackHitbox hitbox = new AttackHitbox(player);

                for (Enemy enemy : enemies) {
                    if (hitbox.top.overlaps(enemy.getBoundingRectangle()) ||
                            hitbox.left.overlaps(enemy.getBoundingRectangle()) ||
                            hitbox.right.overlaps(enemy.getBoundingRectangle()) ||
                            hitbox.bottom.overlaps(enemy.getBoundingRectangle())) {
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

