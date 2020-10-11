package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Timer;
import entities.projectile.Bullet;
import scenes.game.CollisionManager;
import scenes.game.EntityManager;
import scenes.game.Geom;

public class Musket {

    private final EntityManager entityManager;
    private final CollisionManager collisionManager;
    private final Player player;
    private boolean canAttack = true;
    private float attackDelay = 3.0f;
    private float attackOffset = 0.15f;
    private Animation animation;
    private float musketAnimationSpeed = .03f;
    private Animation<TextureRegion> upAnimation;
    private Animation<TextureRegion> downAnimation;
    private float animationTime = 0;
    private boolean isFiring = false;
    private Animation<TextureRegion> rightAnimation;
    private Animation<TextureRegion> leftAnimation;

    public Musket(Player player, EntityManager entityManager, CollisionManager collisionManager) {
        this.player = player;
        this.entityManager = entityManager;
        this.collisionManager = collisionManager;

        Texture downAndUpSheet = new Texture("playersprites/player_musket_down&up_5.png");
        TextureRegion[][] downUpRegions = TextureRegion.split(downAndUpSheet,
                downAndUpSheet.getWidth() / 5,
                downAndUpSheet.getHeight() / 2);

        TextureRegion[] downFrames = new TextureRegion[5];
        downFrames[0] = downUpRegions[0][0];
        downFrames[1] = downUpRegions[0][1];
        downFrames[2] = downUpRegions[0][2];
        downFrames[3] = downUpRegions[0][3];
        downFrames[4] = downUpRegions[0][4];
        downAnimation = new Animation<TextureRegion>(musketAnimationSpeed, downFrames);

        TextureRegion[] upFrames = new TextureRegion[5];
        upFrames[0] = downUpRegions[1][0];
        upFrames[1] = downUpRegions[1][1];
        upFrames[2] = downUpRegions[1][2];
        upFrames[3] = downUpRegions[1][3];
        upFrames[4] = downUpRegions[1][4];
        upAnimation = new Animation<TextureRegion>(musketAnimationSpeed, upFrames);

        Texture leftAndRightSheet = new Texture("playersprites/player_musket_right&left_5.png");
        TextureRegion[][] leftRightRegions = TextureRegion.split(leftAndRightSheet,
                leftAndRightSheet.getWidth() / 5,
                leftAndRightSheet.getHeight() / 2);

        TextureRegion[] leftFrames = new TextureRegion[5];
        leftFrames[0] = leftRightRegions[1][0];
        leftFrames[1] = leftRightRegions[1][1];
        leftFrames[2] = leftRightRegions[1][2];
        leftFrames[3] = leftRightRegions[1][3];
        leftFrames[4] = leftRightRegions[1][4];
        leftAnimation = new Animation<TextureRegion>(musketAnimationSpeed, leftFrames);

        TextureRegion[] rightFrames = new TextureRegion[5];
        rightFrames[0] = leftRightRegions[0][0];
        rightFrames[1] = leftRightRegions[0][1];
        rightFrames[2] = leftRightRegions[0][2];
        rightFrames[3] = leftRightRegions[0][3];
        rightFrames[4] = leftRightRegions[0][4];
        rightAnimation = new Animation<TextureRegion>(musketAnimationSpeed, rightFrames);
    }

    public void attack() {
        if (canAttack) {
            animationTime = 0f;
            canAttack = false;
            isFiring = true;
            entityManager.addEntity(new Bullet(player.getX(), player.getY(), player.getFacingAngle(), entityManager, collisionManager));

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    canAttack = true;
                }
            }, attackDelay);

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    isFiring = false;
                }
            }, musketAnimationSpeed * 5);

        }

    }

    public void draw(float x, float y, Batch batch) {
        if (player.getFacingAngle() == (float) Math.PI) {
            batch.draw(leftAnimation.getKeyFrame(animationTime, false), x - 30, y - 5);
        } else if (player.getFacingAngle() == (float) -Math.PI / 2) {
            batch.draw(downAnimation.getKeyFrame(animationTime, false), x, y - 25);
        } else if (player.getFacingAngle() == (float) Math.PI / 2) {
            batch.draw(upAnimation.getKeyFrame(animationTime, false), x - 5, y - 5);
        } else {
            batch.draw(rightAnimation.getKeyFrame(animationTime, false), x, y - 5);
        }
    }

    public void update(float delta) {
        animationTime += delta;
    }

    public boolean isFiring() {
        return isFiring;
    }
}
