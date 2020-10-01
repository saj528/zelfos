package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

    private enum DIRECTIONS {
        IDLE,
        UP,
        DOWN,
        RIGHT,
        LEFT
    }

    private Texture playerUp = new Texture("player-up.png");
    private Texture playerDown = new Texture("player-down.png");
    private Texture playerLeft = new Texture("player-left.png");
    private Texture playerRight = new Texture("player-right.png");
    private boolean canAttack = true;
    private boolean isRunningLeft = false;
    private boolean isRunningRight = false;
    private boolean isRunningUp = false;
    private boolean isRunningDown = false;
    private float ATTACK_OFFSET = 0.05f;
    private int SWORD_DAMAGE = 1;
    private Animation<Texture> attackAnimation;
    private Animation<TextureRegion> downAnimation;
    private Animation<TextureRegion> leftAnimation;
    private Animation<TextureRegion> upAnimation;
    private Animation<TextureRegion> rightAnimation;
    private float attackTime = 0f;
    private float walkTime = 0f;

    private float ATTACK_ANIMATION_SPEED = 0.025f;
    private float ATTACK_COOLDOWN = ATTACK_ANIMATION_SPEED * 13;

    public Player(float x, float y) {
        super(new Texture("player-down.png"));
        setPosition(x, y);
        setTexture(playerDown);
        Texture[] attackFrames = new Texture[13];
        attackFrames[0] = new Texture("attack/a1.png");
        attackFrames[1] = new Texture("attack/a2.png");
        attackFrames[2] = new Texture("attack/a3.png");
        attackFrames[3] = new Texture("attack/a4.png");
        attackFrames[4] = new Texture("attack/a5.png");
        attackFrames[5] = new Texture("attack/a6.png");
        attackFrames[6] = new Texture("attack/a7.png");
        attackFrames[7] = new Texture("attack/a8.png");
        attackFrames[8] = new Texture("attack/a9.png");
        attackFrames[9] = new Texture("attack/a10.png");
        attackFrames[10] = new Texture("attack/a11.png");
        attackFrames[11] = new Texture("attack/a12.png");
        attackFrames[12] = new Texture("attack/a13.png");
        attackAnimation = new Animation<Texture>(ATTACK_ANIMATION_SPEED, attackFrames);


        Texture playerSheet = new Texture("player-sheet.png");

        TextureRegion[][] playerSheetRegions = TextureRegion.split(playerSheet,
                playerSheet.getWidth() / 4,
                playerSheet.getHeight() / 3);
        TextureRegion[][] playerSheetRegions2 = TextureRegion.split(playerSheet,
                playerSheet.getWidth() / 4,
                playerSheet.getHeight() / 3);

        TextureRegion[] downFrames = new TextureRegion[3];
        downFrames[0] = playerSheetRegions[0][0];
        downFrames[1] = playerSheetRegions[0][1];
        downFrames[2] = playerSheetRegions[0][2];
        downAnimation = new Animation<TextureRegion>(0.15f, downFrames);

        TextureRegion[] leftFrames = new TextureRegion[3];
        leftFrames[0] = playerSheetRegions[2][0];
        leftFrames[1] = playerSheetRegions[2][1];
        leftFrames[2] = playerSheetRegions[2][2];
        leftAnimation = new Animation<TextureRegion>(0.15f, leftFrames);

        TextureRegion[] upFrames = new TextureRegion[3];
        upFrames[0] = playerSheetRegions[1][0];
        upFrames[1] = playerSheetRegions[1][1];
        upFrames[2] = playerSheetRegions[1][2];
        upAnimation = new Animation<TextureRegion>(0.15f, upFrames);

        TextureRegion[] rightFrames = new TextureRegion[3];
        rightFrames[0] = playerSheetRegions2[2][0];
        rightFrames[1] = playerSheetRegions2[2][1];
        rightFrames[2] = playerSheetRegions2[2][2];
        rightFrames[0].flip(true, false);
        rightFrames[1].flip(true, false);
        rightFrames[2].flip(true, false);
        rightAnimation = new Animation<TextureRegion>(0.15f, rightFrames);
    }

    public void updatePlayerMovement(boolean left, boolean right, boolean up, boolean down, float delta) {
        input_vector.x = (right ? 1 : 0) - (left ? 1 : 0);
        input_vector.y = (up ? 1 : 0) - (down ? 1 : 0);

        if (down) {
            isRunningDown = true;
        }

        if (left) {
            isRunningLeft = true;
        }

        if (right) {
            isRunningRight = true;
        }

        if (up) {
            isRunningUp = true;
        }

        setX(getX() + input_vector.x * ACCELERATION * delta);
        setY(getY() + input_vector.y * ACCELERATION * delta);
    }

    public void attack(final ArrayList<Enemy> enemies) {
        if (!canAttack) return;
        canAttack = false;
        attackTime = 0;

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

    public void update(float delta) {
        attackTime += delta;
        walkTime += delta;
        isRunningDown = false;
        isRunningUp = false;
        isRunningLeft = false;
        isRunningRight = false;
    }

    @Override
    public void draw(Batch batch) {
        batch.begin();

        if (!canAttack) {
            batch.draw(attackAnimation.getKeyFrame(attackTime), getX(), getY(), 64, 64);
        } else {
            if (isRunningLeft) {
                batch.draw(leftAnimation.getKeyFrame(walkTime, true), getX(), getY());
            } else if (isRunningRight) {
                batch.draw(rightAnimation.getKeyFrame(walkTime, true), getX(), getY());
            } else if (isRunningUp) {
                batch.draw(upAnimation.getKeyFrame(walkTime, true), getX(), getY());
            } else if (isRunningDown) {
                batch.draw(downAnimation.getKeyFrame(walkTime, true), getX(), getY());
            } else {
                super.draw(batch);
            }
        }
        Texture box = createHitboxTexture(20, 20);
        AttackHitbox hitbox = new AttackHitbox(this);
//        batch.draw(box, hitbox.top.x, hitbox.top.y);
//        batch.draw(box, hitbox.bottom.x, hitbox.bottom.y);
//        batch.draw(box, hitbox.left.x, hitbox.left.y);
//        batch.draw(box, hitbox.right.x, hitbox.right.y);
        batch.end();
    }
}

