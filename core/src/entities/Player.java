package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import entities.enemies.Enemy;
import entities.structures.Cleric;
import helpers.Debug;
import helpers.GameInfo;
import helpers.WhiteShader;
import scenes.game.*;
import helpers.RedShader;

import java.util.ArrayList;
import java.util.List;

class AttackHitbox {
    public Rectangle up;
    public Rectangle left;
    public Rectangle right;
    public Rectangle down;

    public AttackHitbox(Player player) {
        int size = 20;
        Vector2 playerCenter = player.getCenter();

        up = new Rectangle(playerCenter.x - size / 2, playerCenter.y + player.getHeight() / 2, size, size);
        left = new Rectangle(playerCenter.x - size - player.getWidth() / 2, playerCenter.y - size / 2, size, size);
        right = new Rectangle(playerCenter.x + player.getWidth() / 2, playerCenter.y - size / 2, size, size);
        down = new Rectangle(playerCenter.x - size / 2, playerCenter.y - player.getHeight() / 2 - size, size, size);
    }

}

public class Player extends Sprite implements Knockable, Damageable, Collidable, Entity {

    private final FlashRedManager flashRedManager;
    private final CollisionManager collisionManager;
    private final EntityManager entityManager;
    private Vector2 input_vector = Vector2.Zero;
    private Vector2 motion = Vector2.Zero;
    public final float ACCELERATION = 150.0f;
    public final float MAX_SPEED = 3;
    private boolean canDodge = true;
    private playerState state;
    private Texture playerDown = new Texture("playersprites/RunDown/run_down_1.png");
    private boolean canAttack = true;
    private boolean isDodgeUnlocked = false;
    private boolean isWhirlwindUnlocked = false;
    private boolean isRunningLeft = false;
    private boolean isRunningRight = false;
    private boolean isRunningUp = false;
    private boolean isRunningDown = false;
    private boolean isFacingUp = false;
    private boolean isFacingDown = false;
    private boolean isFacingLeft = false;
    private boolean isFacingRight = false;
    private boolean isRolling = false;
    private int level = 1;
    private int experience = 0;
    private boolean isRunning = false;
    private boolean canDropBomb = true;
    private float ATTACK_OFFSET = 0.05f;
    private float SPECIAL_KNOCKBACK = 40f;
    private int SWORD_DAMAGE = 1;
    private int SPECIAL_DAMAGE = 1;
    private Animation<Texture> downAnimation;
    private Animation<Texture> leftAnimation;
    private Animation<Texture> upAnimation;
    private Animation<Texture> rightAnimation;
    private Animation<TextureRegion> attackUp;
    private Animation<TextureRegion> attackDown;
    private Animation<TextureRegion> attackLeft;
    private Animation<TextureRegion> attackRight;
    private Animation<TextureRegion> dodgeUp;
    private Animation<TextureRegion> dodgeDown;
    private Animation<TextureRegion> dodgeLeft;
    private Animation<TextureRegion> dodgeRight;
    private Animation<TextureRegion> whirlwind;
    private float WHIRLWIND_ANIMATION_SPEED = 0.05f;
    private float animationSpeed = 0.13f;
    private float attackTime = 0f;
    private float whirlwindTime = 0f;
    private boolean isUsingSpecial = false;
    private float dodgeTime = 0;
    private float walkTime = 0f;
    private int maxLives = 5;
    private int lives = maxLives;
    private boolean showAttackAnimation = false;
    private boolean showDodgeAnimation = false;
    private float facingAngle = 0;
    private int skillPoints = 0;
    private float ATTACK_ANIMATION_SPEED = 0.025f;
    private float ATTACK_COOLDOWN = ATTACK_ANIMATION_SPEED * 13;
    private float ATTACK_ANIMATION_DURATION = 0.2f;
    private float SPECIAL_DISTANCE = 60f;
    private float SPECIAL_COOLDOWN = 5.0f;
    private float DODGE_ANIMATION_DURATION = 0.5f;
    private float DODGE_ANIMATION_SPEED = 0.1f;
    private float DODGE_COOLDOWN = DODGE_ANIMATION_DURATION * 2;
    private float DODGE_TO_ATTACK_COOLDOWN = DODGE_ANIMATION_DURATION;
    private int DODGE_DISTANCE = 40;
    private int bombs = 0;
    private boolean shouldFlashRed;
    private boolean isMusketUnlocked = false;
    private int attackOffsetX = 11;
    private int attackOffsetY = 13;
    private boolean hasPotion = false;
    private boolean canSpecial = true;
    private boolean isGunUnlocked = false;
    private Musket musket;

    private enum DIRECTIONS {
        IDLE,
        UP,
        DOWN,
        RIGHT,
        LEFT
    }

    private enum Direction {
        None,
        Left,
        Right,
        Up,
        Down
    }

    private enum playerState {
        WALKING,
        ATTACKING,
        DODGING,
        BOMBING,
        TAKINGDAMAGE,
        DEAD
    }

    private Direction strifeDirection = Direction.None;


    public Player(float x, float y, FlashRedManager flashRedManager, EntityManager entityManager, CollisionManager collisionManager) {
        super(new Texture("playersprites/RunDown/run_down_1.png"));
        setPosition(x, y);
        setTexture(playerDown);
        this.collisionManager = collisionManager;
        isFacingDown = true;
        initPlayerTextures();
        this.entityManager = entityManager;
        this.flashRedManager = flashRedManager;
        shouldFlashRed = false;
        this.musket = new Musket(this, entityManager, collisionManager);
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public int getExperienceUntilNextLevel() {
        return (int) (Math.pow(7, level) / 10) * 10 + 150;
    }

    public void addExperience(int amount) {
        experience += amount;
        if (experience >= getExperienceUntilNextLevel()) {
            experience = 0;
            level++;
            skillPoints++;
            entityManager.addEntity(new LevelUpParticle(getX(), getY()));
        }
    }

    public void unlockDodge() {
        isDodgeUnlocked = true;
    }

    public void unlockMusket() {
        isMusketUnlocked = true;
    }

    public void unlockWhirlwind() {
        isWhirlwindUnlocked = true;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getMaxLives() {
        return maxLives;
    }

    public void damage(int amount) {
        if (isRolling) return;

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

    public void addBomb() {
        bombs++;
    }

    public int getTotalBombs() {
        return bombs;
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    public void removeSkillPoint() {
        skillPoints--;
    }

    public float getFacingAngle() {
        return facingAngle;
    }

    public void special() {
        if (!isWhirlwindUnlocked) return;
        if (canSpecial) {
            canSpecial = false;
            whirlwindTime = 0f;
            isUsingSpecial = true;

            ArrayList<Enemy> enemies = (ArrayList<Enemy>) (List<?>) entityManager.getEntitiesByType(Enemy.class);

            for (Enemy enemy : enemies) {
                if (Geom.distanceBetween((Entity) enemy, this) < SPECIAL_DISTANCE) {
                    enemy.damage(SPECIAL_DAMAGE);
                    Physics.knockback(this, (Knockable) enemy, SPECIAL_KNOCKBACK, collisionManager);
                }
            }

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    canSpecial = true;
                }
            }, SPECIAL_COOLDOWN);

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    isUsingSpecial = false;
                }
            }, WHIRLWIND_ANIMATION_SPEED * 8);
        }
    }

    private void playerWalk() {
    }

    public void fire() {
        if (!isMusketUnlocked()) return;
        musket.attack();
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

        if (left) {
            isRunningLeft = true;
            isRunning = true;
            if (!strafe) {
                isFacingLeft = true;
                isFacingDown = false;
                isFacingUp = false;
                isFacingRight = false;
                facingAngle = (float) Math.PI;
            }
        } else if (right) {
            isRunningRight = true;
            isRunning = true;
            if (!strafe) {
                isFacingRight = true;
                isFacingDown = false;
                isFacingLeft = false;
                isFacingUp = false;
                facingAngle = (float) 0;
            }
        } else if (down) {
            isRunningDown = true;
            isRunning = true;
            if (!strafe) {
                isFacingDown = true;
                isFacingUp = false;
                isFacingLeft = false;
                isFacingRight = false;
                facingAngle = (float) -Math.PI / 2;
            }
        } else if (up) {
            isRunningUp = true;
            isRunning = true;
            if (!strafe) {
                isFacingUp = true;
                isFacingDown = false;
                isFacingLeft = false;
                isFacingRight = false;
                facingAngle = (float) Math.PI / 2;
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

    public void dropBomb() {
        if (canDropBomb && bombs > 0) {
            bombs--;
            canDropBomb = false;
            entityManager.addEntity(new Bomb(getX(), getY(), entityManager, collisionManager));
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    canDropBomb = true;
                }
            }, 0.5f);
        }
    }

    public void dodge() {
        if (!isDodgeUnlocked) return;
        if (!canAttack || !canDodge) return;
        canAttack = false;
        canDodge = false;
        dodgeTime = 0;
        isRolling = true;

        showDodgeAnimation = true;

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                showDodgeAnimation = false;
            }
        }, DODGE_ANIMATION_DURATION);

        if (isFacingLeft) {
            Physics.knockback(this, (float) Math.PI, DODGE_DISTANCE, collisionManager);
        } else if (isFacingRight) {
            Physics.knockback(this, 0, DODGE_DISTANCE, collisionManager);
        } else if (isFacingUp) {
            Physics.knockback(this, (float) Math.PI / 2, DODGE_DISTANCE, collisionManager);
        } else if (isFacingDown) {
            Physics.knockback(this, (float) -Math.PI / 2, DODGE_DISTANCE, collisionManager);
        }

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {

            }
        }, ATTACK_COOLDOWN);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                canDodge = true;
            }
        }, DODGE_COOLDOWN);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                canAttack = true;
                isRolling = false;
            }
        }, DODGE_TO_ATTACK_COOLDOWN);

    }

    public boolean hasBombs() {
        return bombs > 0;
    }

    public void attack(final ArrayList<Entity> enemies) {
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
                canAttack = true;
            }
        }, ATTACK_COOLDOWN);

        final boolean isFacingLeft = this.isFacingLeft;
        final boolean isFacingRight = this.isFacingRight;
        final boolean isFacingDown = this.isFacingDown;
        final boolean isFacingUp = this.isFacingUp;

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                final AttackHitbox hitbox = new AttackHitbox(player);

                for (Entity enemy : enemies) {
                    Damageable damageable = (Damageable) enemy;
                    if (isFacingLeft) {
                        if (hitbox.left.overlaps(enemy.getBoundingRectangle())) {
                            damageable.damage(SWORD_DAMAGE);
                            entityManager.addEntity(new DamageParticle(enemy.getX(), enemy.getY(), SWORD_DAMAGE));
                            Physics.knockback(player, (Knockable) enemy, 10, collisionManager);
                        }
                    } else if (isFacingRight) {
                        if (hitbox.right.overlaps(enemy.getBoundingRectangle())) {
                            damageable.damage(SWORD_DAMAGE);
                            entityManager.addEntity(new DamageParticle(enemy.getX(), enemy.getY(), SWORD_DAMAGE));
                            Physics.knockback(player, (Knockable) enemy, 10, collisionManager);
                        }
                    } else if (isFacingUp) {
                        if (hitbox.up.overlaps(enemy.getBoundingRectangle())) {
                            damageable.damage(SWORD_DAMAGE);
                            entityManager.addEntity(new DamageParticle(enemy.getX(), enemy.getY(), SWORD_DAMAGE));
                            Physics.knockback(player, (Knockable) enemy, 10, collisionManager);
                        }
                    } else if (isFacingDown) {
                        if (hitbox.down.overlaps(enemy.getBoundingRectangle())) {
                            damageable.damage(SWORD_DAMAGE);
                            entityManager.addEntity(new DamageParticle(enemy.getX(), enemy.getY(), SWORD_DAMAGE));
                            Physics.knockback(player, (Knockable) enemy, 10, collisionManager);
                        }
                    }
                }
            }
        }, ATTACK_OFFSET);
    }

    @Override
    public Vector2 getCenter() {
        return Geom.getCenter(this);
    }

    public void update(float delta) {
        attackTime += delta;
        walkTime += delta;
        dodgeTime += delta;
        whirlwindTime += delta;
        musket.update(delta);
    }

    @Override
    public Rectangle getHitbox() {
        return getBoundingRectangle();
    }

    @Override
    public ArrayList<Class> getIgnoreClassList() {
        ArrayList<Class> ignore = new ArrayList<>();
        ignore.add(Enemy.class);
        ignore.add(Mercenary.class);
        return ignore;
    }


    @Override
    public void draw(Batch batch, ShapeRenderer shapeRenderer) {
        batch.begin();

        if (shouldFlashRed) {
            batch.setShader(RedShader.shaderProgram);
        }

        if (isRolling) {
            batch.setShader(WhiteShader.shaderProgram);
        }

        if (musket.isFiring()) {
            musket.draw(getX(), getY(), batch);
        } else if (isUsingSpecial) {
            batch.draw(whirlwind.getKeyFrame(whirlwindTime, false), getX() - attackOffsetX, getY() - attackOffsetY);
        } else if (showAttackAnimation) {
            if (isFacingLeft) {
                batch.draw(attackLeft.getKeyFrame(attackTime, false), getX() - attackOffsetX, getY() - attackOffsetY);
            } else if (isFacingRight) {
                batch.draw(attackRight.getKeyFrame(attackTime, false), getX() - attackOffsetX, getY() - attackOffsetY);
            } else if (isFacingUp) {
                batch.draw(attackUp.getKeyFrame(attackTime, false), getX() - attackOffsetX, getY() - attackOffsetY);
            } else if (isFacingDown) {
                batch.draw(attackDown.getKeyFrame(attackTime, false), getX() - attackOffsetX, getY() - attackOffsetY);
            }
        } else if (showDodgeAnimation) {
            if (isFacingLeft) {
                batch.draw(dodgeLeft.getKeyFrame(dodgeTime, false), getX(), getY());
            } else if (isFacingRight) {
                batch.draw(dodgeRight.getKeyFrame(dodgeTime, false), getX(), getY());
            } else if (isFacingUp) {
                batch.draw(dodgeUp.getKeyFrame(dodgeTime, false), getX(), getY());
            } else if (isFacingDown) {
                batch.draw(dodgeDown.getKeyFrame(dodgeTime, false), getX(), getY());
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

        if (GameInfo.DEBUG) {
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

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(new Color(1, 0, 0, 0.2f));
            shapeRenderer.circle(getCenter().x, getCenter().y, SPECIAL_DISTANCE);
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    public void initPlayerTextures() {
        Texture playerAttackSheet = new Texture("playersprites/player_attack_frames.png");
        TextureRegion[][] playerAttackSheetRegions = TextureRegion.split(playerAttackSheet,
                playerAttackSheet.getWidth() / 4,
                playerAttackSheet.getHeight() / 4);

        Texture playerDodgeSheet = new Texture("playersprites/player_dodge_frames.png");
        TextureRegion[][] playerDodgeSheetRegions = TextureRegion.split(playerDodgeSheet,
                playerDodgeSheet.getWidth() / 5,
                playerDodgeSheet.getHeight() / 4);

        Texture dodgeResetDown = new Texture("playersprites/RunDown/run_down_1.png");
        TextureRegion[][] dodgeResetDownRegion = TextureRegion.split(dodgeResetDown, dodgeResetDown.getWidth(), dodgeResetDown.getHeight());

        Texture dodgeResetUp = new Texture("playersprites/RunUp/run_up_1.png");
        TextureRegion[][] dodgeResetUpRegion = TextureRegion.split(dodgeResetUp, dodgeResetUp.getWidth(), dodgeResetUp.getHeight());

        Texture dodgeResetRight = new Texture("playersprites/RunRight/run_right_1.png");
        TextureRegion[][] dodgeResetRightRegion = TextureRegion.split(dodgeResetRight, dodgeResetRight.getWidth(), dodgeResetRight.getHeight());

        Texture dodgeResetLeft = new Texture("playersprites/RunLeft/run_left_1.png");
        TextureRegion[][] dodgeResetLeftRegion = TextureRegion.split(dodgeResetLeft, dodgeResetLeft.getWidth(), dodgeResetLeft.getHeight());

        Texture whirlwindTexture = new Texture("playersprites/player_attack_whirlwind_8.png");
        TextureRegion[][] whirlwindRegion = TextureRegion.split(whirlwindTexture, whirlwindTexture.getWidth() / 4, whirlwindTexture.getHeight() / 2);

        Texture[] downFrames = new Texture[6];
        downFrames[0] = new Texture("playersprites/RunDown/run_down_1.png");
        downFrames[1] = new Texture("playersprites/RunDown/run_down_2.png");
        downFrames[2] = new Texture("playersprites/RunDown/run_down_3.png");
        downFrames[3] = new Texture("playersprites/RunDown/run_down_4.png");
        downFrames[4] = new Texture("playersprites/RunDown/run_down_5.png");
        downFrames[5] = new Texture("playersprites/RunDown/run_down_6.png");
        downAnimation = new Animation<Texture>(animationSpeed, downFrames);

        TextureRegion[] attackDownFrames = new TextureRegion[4];
        attackDownFrames[0] = playerAttackSheetRegions[0][0];
        attackDownFrames[1] = playerAttackSheetRegions[0][1];
        attackDownFrames[2] = playerAttackSheetRegions[0][2];
        attackDownFrames[3] = playerAttackSheetRegions[0][3];
        attackDown = new Animation<TextureRegion>(ATTACK_ANIMATION_SPEED, attackDownFrames);


        TextureRegion[] dodgeDownFrames = new TextureRegion[5];
        dodgeDownFrames[0] = playerDodgeSheetRegions[0][0];
        dodgeDownFrames[1] = playerDodgeSheetRegions[0][1];
        dodgeDownFrames[2] = playerDodgeSheetRegions[0][2];
        dodgeDownFrames[3] = playerDodgeSheetRegions[0][3];
        dodgeDownFrames[4] = dodgeResetDownRegion[0][0];
        dodgeDown = new Animation<TextureRegion>(DODGE_ANIMATION_SPEED, dodgeDownFrames);

        Texture[] leftFrames = new Texture[6];
        leftFrames[0] = new Texture("playersprites/RunLeft/run_left_1.png");
        leftFrames[1] = new Texture("playersprites/RunLeft/run_left_2.png");
        leftFrames[2] = new Texture("playersprites/RunLeft/run_left_3.png");
        leftFrames[3] = new Texture("playersprites/RunLeft/run_left_4.png");
        leftFrames[4] = new Texture("playersprites/RunLeft/run_left_5.png");
        leftFrames[5] = new Texture("playersprites/RunLeft/run_left_6.png");
        leftAnimation = new Animation<Texture>(animationSpeed, leftFrames);


        TextureRegion[] attackLeftFrames = new TextureRegion[4];
        attackLeftFrames[0] = playerAttackSheetRegions[3][0];
        attackLeftFrames[1] = playerAttackSheetRegions[3][1];
        attackLeftFrames[2] = playerAttackSheetRegions[3][2];
        attackLeftFrames[3] = playerAttackSheetRegions[3][3];
        attackLeft = new Animation<TextureRegion>(ATTACK_ANIMATION_SPEED, attackLeftFrames);

        TextureRegion[] dodgeLeftFrames = new TextureRegion[5];
        dodgeLeftFrames[0] = playerDodgeSheetRegions[3][3];
        dodgeLeftFrames[1] = playerDodgeSheetRegions[3][2];
        dodgeLeftFrames[2] = playerDodgeSheetRegions[3][1];
        dodgeLeftFrames[3] = playerDodgeSheetRegions[3][0];
        dodgeLeftFrames[4] = dodgeResetLeftRegion[0][0];
        dodgeLeft = new Animation<TextureRegion>(DODGE_ANIMATION_SPEED, dodgeLeftFrames);


        TextureRegion[] whirlwindFrames = new TextureRegion[8];
        whirlwindFrames[0] = whirlwindRegion[0][0];
        whirlwindFrames[1] = whirlwindRegion[0][1];
        whirlwindFrames[2] = whirlwindRegion[0][2];
        whirlwindFrames[3] = whirlwindRegion[0][3];
        whirlwindFrames[4] = whirlwindRegion[1][0];
        whirlwindFrames[5] = whirlwindRegion[1][1];
        whirlwindFrames[6] = whirlwindRegion[1][2];
        whirlwindFrames[7] = whirlwindRegion[1][3];
        whirlwind = new Animation<TextureRegion>(WHIRLWIND_ANIMATION_SPEED, whirlwindFrames);

        Texture[] upFrames = new Texture[6];
        upFrames[0] = new Texture("playersprites/RunUp/run_up_1.png");
        upFrames[1] = new Texture("playersprites/RunUp/run_up_2.png");
        upFrames[2] = new Texture("playersprites/RunUp/run_up_3.png");
        upFrames[3] = new Texture("playersprites/RunUp/run_up_4.png");
        upFrames[4] = new Texture("playersprites/RunUp/run_up_5.png");
        upFrames[5] = new Texture("playersprites/RunUp/run_up_6.png");
        upAnimation = new Animation<Texture>(animationSpeed, upFrames);

        TextureRegion[] attackUpFrames = new TextureRegion[4];
        attackUpFrames[0] = playerAttackSheetRegions[2][0];
        attackUpFrames[1] = playerAttackSheetRegions[2][1];
        attackUpFrames[2] = playerAttackSheetRegions[2][2];
        attackUpFrames[3] = playerAttackSheetRegions[2][3];
        attackUp = new Animation<TextureRegion>(ATTACK_ANIMATION_SPEED, attackUpFrames);

        TextureRegion[] dodgeUpFrames = new TextureRegion[5];
        dodgeUpFrames[0] = playerDodgeSheetRegions[2][0];
        dodgeUpFrames[1] = playerDodgeSheetRegions[2][1];
        dodgeUpFrames[2] = playerDodgeSheetRegions[2][2];
        dodgeUpFrames[3] = playerDodgeSheetRegions[2][3];
        dodgeUpFrames[4] = dodgeResetUpRegion[0][0];
        dodgeUp = new Animation<TextureRegion>(DODGE_ANIMATION_SPEED, dodgeUpFrames);

        Texture[] rightFrames = new Texture[6];
        rightFrames[0] = new Texture("playersprites/RunRight/run_right_1.png");
        rightFrames[1] = new Texture("playersprites/RunRight/run_right_2.png");
        rightFrames[2] = new Texture("playersprites/RunRight/run_right_3.png");
        rightFrames[3] = new Texture("playersprites/RunRight/run_right_4.png");
        rightFrames[4] = new Texture("playersprites/RunRight/run_right_5.png");
        rightFrames[5] = new Texture("playersprites/RunRight/run_right_6.png");
        rightAnimation = new Animation<Texture>(animationSpeed, rightFrames);

        TextureRegion[] attackRightFrames = new TextureRegion[4];
        attackRightFrames[0] = playerAttackSheetRegions[1][0];
        attackRightFrames[1] = playerAttackSheetRegions[1][1];
        attackRightFrames[2] = playerAttackSheetRegions[1][2];
        attackRightFrames[3] = playerAttackSheetRegions[1][3];
        attackRight = new Animation<TextureRegion>(ATTACK_ANIMATION_SPEED, attackRightFrames);

        TextureRegion[] dodgeRightFrames = new TextureRegion[5];
        dodgeRightFrames[0] = playerDodgeSheetRegions[1][0];
        dodgeRightFrames[1] = playerDodgeSheetRegions[1][1];
        dodgeRightFrames[2] = playerDodgeSheetRegions[1][2];
        dodgeRightFrames[3] = playerDodgeSheetRegions[1][3];
        dodgeRightFrames[4] = dodgeResetRightRegion[0][0];
        dodgeRight = new Animation<TextureRegion>(DODGE_ANIMATION_SPEED, dodgeRightFrames);
    }

    public boolean getHasPotion() {
        return hasPotion;
    }

    public void setHasPotion(boolean hasPotion) {
        this.hasPotion = hasPotion;
    }


    public boolean isDodgeUnlocked() {
        return isDodgeUnlocked;
    }

    public boolean isMusketUnlocked() {
        return isMusketUnlocked;
    }

    public boolean isWhirlwindUnlocked() {
        return isWhirlwindUnlocked;
    }

    public void usePotion() {
        lives += Cleric.POTION_HEAL;
        hasPotion = false;
    }


    public boolean isDead() {
        return lives <= 0;
    }
}

