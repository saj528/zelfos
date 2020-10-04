package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import helpers.Debug;
import helpers.RedShader;

import java.util.ArrayList;

class AttackHitbox {
    public Rectangle up;
    public Rectangle left;
    public Rectangle right;
    public Rectangle down;

    public AttackHitbox(Player player) {
        int offset = 50;
        int size = 40;

        up = new Rectangle(player.getX() - 8, player.getY() + 25, size, size);
        left = new Rectangle(player.getX() - 40, player.getY() - 5, size, size);
        right = new Rectangle(player.getX() + 20, player.getY() - 5, size, size);
        down = new Rectangle(player.getX() - 10, player.getY() - offset + 10, size, size);
    }

}

public class Player extends Sprite {

    private final FlashRedManager flashRedManager;
    private Vector2 input_vector = Vector2.Zero;
    private Vector2 motion = Vector2.Zero;
    public final float ACCELERATION = 150.0f;
    public final float MAX_SPEED = 3;

    private enum DIRECTIONS {
        IDLE,
        UP,
        DOWN,
        RIGHT,
        LEFT
    }

    private Texture playerUp = new Texture("player-up.png");
    private Texture playerDown = new Texture("playersprites/Run Down/run_down_1.png");
    private Texture playerLeft = new Texture("player-left.png");
    private Texture playerRight = new Texture("player-right.png");
    private boolean canAttack = true;
    private boolean isRunningLeft = false;
    private boolean isRunningRight = false;
    private boolean isRunningUp = false;
    private boolean isRunningDown = false;
    private boolean isFacingUp = false;
    private boolean isFacingDown = false;
    private boolean isFacingLeft = false;
    private boolean isFacingRight = false;
    private boolean isRunning = false;
    private boolean canDropBomb = true;
    private float ATTACK_OFFSET = 0.05f;
    private int SWORD_DAMAGE = 1;
    private Animation<Texture> attackAnimation;
    private Animation<Texture> downAnimation;
    private Animation<Texture> leftAnimation;
    private Animation<Texture> upAnimation;
    private Animation<Texture> rightAnimation;
    private Animation<TextureRegion> attackUp;
    private Animation<TextureRegion> attackDown;
    private Animation<TextureRegion> attackLeft;
    private Animation<TextureRegion> attackRight;
    private float animationSpeed = 0.13f;
    private float attackTime = 0f;
    private float walkTime = 0f;
    private int maxLives = 5;
    private int lives = maxLives;
    private boolean showAttackAnimation = false;
    private float ATTACK_ANIMATION_SPEED = 0.025f;
    private float ATTACK_COOLDOWN = ATTACK_ANIMATION_SPEED * 13;
    private float ATTACK_ANIMATION_DURATION = 0.2f;
    private int bombs = 100;
    private boolean shouldFlashRed;
    private int attackOffsetX = 11;
    private int attackOffsetY = 13;

    private enum Direction {
        None,
        Left,
        Right,
        Up,
        Down
    }
    private Direction strifeDirection = Direction.None;

    public int getLives() {
        return lives;
    }

    public int getMaxLives() {
        return maxLives;
    }

    public void damage(int amount) {
        lives -= amount;

        // flashes the screen red
        flashRedManager.flashRed(0.2f);

        // flashes the player red
        shouldFlashRed = true;
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                shouldFlashRed = false;
            }
        }, 0.5f);

    }

    public Player(float x, float y, FlashRedManager flashRedManager) {
        super(new Texture("playersprites/Run Down/run_down_1.png"));
        setPosition(x, y);
        setTexture(playerDown);
        isFacingDown = true;
        this.flashRedManager = flashRedManager;
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


        Texture playerSheet = new Texture("player-sheet-all.png");
        TextureRegion[][] playerSheetRegions = TextureRegion.split(playerSheet,
                playerSheet.getWidth() / 4,
                playerSheet.getHeight() / 4);


        Texture playerAttackSheet = new Texture("playersprites/player_attack_frames.png");
        TextureRegion[][] playerAttackSheetRegions = TextureRegion.split(playerAttackSheet,
                playerAttackSheet.getWidth() / 4,
                playerAttackSheet.getHeight() / 4);


        Texture[] downFrames = new Texture[6];
        downFrames[0] = new Texture("playersprites/Run Down/run_down_1.png");
        downFrames[1] = new Texture("playersprites/Run Down/run_down_2.png");
        downFrames[2] = new Texture("playersprites/Run Down/run_down_3.png");
        downFrames[3] = new Texture("playersprites/Run Down/run_down_4.png");
        downFrames[4] = new Texture("playersprites/Run Down/run_down_5.png");
        downFrames[5] = new Texture("playersprites/Run Down/run_down_6.png");
        downAnimation = new Animation<Texture>(animationSpeed, downFrames);

        TextureRegion[] attackDownFrames = new TextureRegion[4];
        attackDownFrames[0] = playerAttackSheetRegions[0][0];
        attackDownFrames[1] = playerAttackSheetRegions[0][1];
        attackDownFrames[2] = playerAttackSheetRegions[0][2];
        attackDownFrames[3] = playerAttackSheetRegions[0][3];
        attackDown = new Animation<TextureRegion>(ATTACK_ANIMATION_SPEED,attackDownFrames);

        Texture[] leftFrames = new Texture[6];
        leftFrames[0] = new Texture("playersprites/Run Left/run_left_1.png");
        leftFrames[1] = new Texture("playersprites/Run Left/run_left_2.png");
        leftFrames[2] = new Texture("playersprites/Run Left/run_left_3.png");
        leftFrames[3] = new Texture("playersprites/Run Left/run_left_4.png");
        leftFrames[4] = new Texture("playersprites/Run Left/run_left_5.png");
        leftFrames[5] = new Texture("playersprites/Run Left/run_left_6.png");
        leftAnimation = new Animation<Texture>(animationSpeed, leftFrames);

        TextureRegion[] attackLeftFrames = new TextureRegion[4];
        attackLeftFrames[0] = playerAttackSheetRegions[3][0];
        attackLeftFrames[1] = playerAttackSheetRegions[3][1];
        attackLeftFrames[2] = playerAttackSheetRegions[3][2];
        attackLeftFrames[3] = playerAttackSheetRegions[3][3];
        attackLeft = new Animation<TextureRegion>(ATTACK_ANIMATION_SPEED,attackLeftFrames);


        Texture[] upFrames = new Texture[6];
        upFrames[0] = new Texture("playersprites/Run Up/run_up_1.png");
        upFrames[1] = new Texture("playersprites/Run Up/run_up_2.png");
        upFrames[2] = new Texture("playersprites/Run Up/run_up_3.png");
        upFrames[3] = new Texture("playersprites/Run Up/run_up_4.png");
        upFrames[4] = new Texture("playersprites/Run Up/run_up_5.png");
        upFrames[5] = new Texture("playersprites/Run Up/run_up_6.png");
        upAnimation = new Animation<Texture>(animationSpeed, upFrames);

        TextureRegion[] attackUpFrames = new TextureRegion[4];
        attackUpFrames[0] = playerAttackSheetRegions[2][0];
        attackUpFrames[1] = playerAttackSheetRegions[2][1];
        attackUpFrames[2] = playerAttackSheetRegions[2][2];
        attackUpFrames[3] = playerAttackSheetRegions[2][3];
        attackUp = new Animation<TextureRegion>(ATTACK_ANIMATION_SPEED,attackUpFrames);

        Texture[] rightFrames = new Texture[6];
        rightFrames[0] = new Texture("playersprites/Run Right/run_right_1.png");
        rightFrames[1] = new Texture("playersprites/Run Right/run_right_2.png");
        rightFrames[2] = new Texture("playersprites/Run Right/run_right_3.png");
        rightFrames[3] = new Texture("playersprites/Run Right/run_right_4.png");
        rightFrames[4] = new Texture("playersprites/Run Right/run_right_5.png");
        rightFrames[5] = new Texture("playersprites/Run Right/run_right_6.png");
        rightAnimation = new Animation<Texture>(animationSpeed, rightFrames);

        TextureRegion[] attackRightFrames = new TextureRegion[4];
        attackRightFrames[0] = playerAttackSheetRegions[1][0];
        attackRightFrames[1] = playerAttackSheetRegions[1][1];
        attackRightFrames[2] = playerAttackSheetRegions[1][2];
        attackRightFrames[3] = playerAttackSheetRegions[1][3];
        attackRight = new Animation<TextureRegion>(ATTACK_ANIMATION_SPEED,attackRightFrames);

        shouldFlashRed = false;
    }

    public void updatePlayerMovement(boolean left, boolean right, boolean up, boolean down, boolean strafe, float delta) {
        input_vector.x = (right ? 1 : 0) - (left ? 1 : 0);
        input_vector.y = (up ? 1 : 0) - (down ? 1 : 0);

        isRunningDown = false;
        isRunningUp = false;
        isRunningLeft = false;
        isRunningRight = false;
        isRunning = false;


        if (!strafe) {
            strifeDirection = Direction.None;
        }

        if (up) {
            isRunningUp = true;
            isRunning = true;
            if (!strafe) {
                isFacingUp = true;
                isFacingDown = false;
                isFacingLeft = false;
                isFacingRight = false;
            }
        } else if (left) {
            isRunningLeft = true;
            isRunning = true;
            if (!strafe) {
                isFacingLeft = true;
                isFacingDown = false;
                isFacingUp = false;
                isFacingRight = false;
            }
        } else if (right) {
            isRunningRight = true;
            isRunning = true;
            if (!strafe) {
                isFacingRight = true;
                isFacingDown = false;
                isFacingLeft = false;
                isFacingUp = false;
            }
        } else if (down) {
            isRunningDown = true;
            isRunning = true;
            if (!strafe) {
                isFacingDown = true;
                isFacingUp = false;
                isFacingLeft = false;
                isFacingRight = false;
            }
        }

        if (strafe && strifeDirection == Direction.None) {
            if (isFacingUp) {
                strifeDirection = Direction.Up;
            } else if (isFacingLeft) {
                strifeDirection = Direction.Left;
            } else if (isFacingRight) {
                strifeDirection = Direction.Right;
            } else if (isFacingDown) {
                strifeDirection = Direction.Down;
            }
        }
    }

    public void updateX(float delta) {
        setX(getX() + input_vector.x * ACCELERATION * delta);
    }

    public void updateY(float delta) {
        setY(getY() + input_vector.y * ACCELERATION * delta);
    }

    public void dropBomb(BombManager bombManager) {
        if (canDropBomb && bombs > 0) {
            bombs--;
            canDropBomb = false;
            bombManager.createBomb(getX(), getY());

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    canDropBomb = true;
                }
            }, 0.5f);

        }
    }

    public boolean hasBombs() {
        return bombs > 0;
    }

    public void attack(final ArrayList<EnemyInterface> enemies) {
        if (!canAttack) return;
        canAttack = false;
        attackTime = 0;
        showAttackAnimation = true;

        final Player player = this;

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                showAttackAnimation = false;
            }
        }, ATTACK_ANIMATION_DURATION);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                AttackHitbox hitbox = new AttackHitbox(player);

                for (EnemyInterface enemy : enemies) {
                    if (isFacingLeft) {
                        if (hitbox.left.overlaps(enemy.getBoundingRectangle())) {
                            enemy.damage(SWORD_DAMAGE);
                        }
                    } else if (isFacingRight) {
                        if (hitbox.right.overlaps(enemy.getBoundingRectangle())) {
                            enemy.damage(SWORD_DAMAGE);
                        }
                    } else if (isFacingUp) {
                        if (hitbox.up.overlaps(enemy.getBoundingRectangle())) {
                            enemy.damage(SWORD_DAMAGE);
                        }
                    } else if (isFacingDown) {
                        if (hitbox.down.overlaps(enemy.getBoundingRectangle())) {
                            enemy.damage(SWORD_DAMAGE);
                        }
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

    public void update(float delta) {
        attackTime += delta;
        walkTime += delta;
    }

    @Override
    public void draw(Batch batch) {
        batch.begin();

        if (shouldFlashRed) {
            batch.setShader(RedShader.shaderProgram);
        }

        if (showAttackAnimation) {
            if (isFacingLeft) {
                batch.draw(attackLeft.getKeyFrame(attackTime, false), getX()-attackOffsetX, getY()-attackOffsetY);
            } else if (isFacingRight) {
                batch.draw(attackRight.getKeyFrame(attackTime, false), getX()-attackOffsetX, getY()-attackOffsetY);
            } else if (isFacingUp) {
                batch.draw(attackUp.getKeyFrame(attackTime, false), getX()-attackOffsetX, getY()-attackOffsetY);
            } else if (isFacingDown) {
                batch.draw(attackDown.getKeyFrame(attackTime, false), getX()-attackOffsetX, getY()-attackOffsetY);
            }
        } else {
            if (isRunning) {
                if (strifeDirection != Direction.None) {
                    if (strifeDirection == Direction.Up) {
                        batch.draw(upAnimation.getKeyFrame(walkTime, true), getX(), getY());
                    } else if (strifeDirection == Direction.Right) {
                        batch.draw(rightAnimation.getKeyFrame(walkTime, true), getX(), getY());
                    } else if (strifeDirection == Direction.Left) {
                        batch.draw(leftAnimation.getKeyFrame(walkTime, true), getX(), getY());
                    } else if (strifeDirection == Direction.Down) {
                        batch.draw(downAnimation.getKeyFrame(walkTime, true), getX(), getY());
                    } else {
                        super.draw(batch);
                    }
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
            } else {
                if (isFacingLeft) {
                    batch.draw(leftAnimation.getKeyFrame(0, true), getX(), getY());
                } else if (isFacingRight) {
                    batch.draw(rightAnimation.getKeyFrame(0, true), getX(), getY());
                } else if (isFacingUp) {
                    batch.draw(upAnimation.getKeyFrame(0, true), getX(), getY());
                } else if (isFacingDown) {
                    batch.draw(downAnimation.getKeyFrame(0, true), getX(), getY());
                } else {
                    super.draw(batch);
                }
            }
        }

        batch.end();

        batch.setShader(null);

        AttackHitbox hitbox = new AttackHitbox(this);
        if (isFacingLeft) {
            Debug.drawHitbox(batch, hitbox.left);
        } else if (isFacingRight) {
            Debug.drawHitbox(batch, hitbox.right);
        } else if (isFacingUp) {
            Debug.drawHitbox(batch, hitbox.up);
        } else if (isFacingDown) {
            Debug.drawHitbox(batch, hitbox.down);
        }
    }

    public boolean isDead() {
        return lives <= 0;
    }
}

