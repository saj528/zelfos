package scenes.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import entities.Entity;
import entities.Knockable;

public class Physics {

    private static float friction = 9.0f;

    public static void knockback(final Entity source, final Knockable target, final float amount, CollisionManager collisionManager) {
        if (amount <= 0) return;

        Vector2 targetCenter = new Vector2(0, 0);
        Vector2 sourceCenter = source.getCenter();
        target.getBoundingRectangle().getCenter(targetCenter);

        final float angle = (float)Math.atan2(targetCenter.y - sourceCenter.y, targetCenter.x - sourceCenter.x);

        knockback(target, angle, amount, collisionManager);
    }

    public static void knockback(final Knockable target, final float angle, final float amount, final CollisionManager collisionManager) {
        if (amount <= 0) return;

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                float originalX = target.getX();
                float originalY = target.getY();
                target.setX(target.getX() + amount * (float)Math.cos(angle));

                if (collisionManager.isCollidingWithMap((Collidable)target) || collisionManager.isCollidingWithOtherCollidables((Collidable)target)) {
                    target.setX(originalX);
                }

                target.setY(target.getY() + amount * (float)Math.sin(angle));

                if (collisionManager.isCollidingWithMap((Collidable)target) || collisionManager.isCollidingWithOtherCollidables((Collidable)target)) {
                    target.setY(originalY);
                }

                knockback(target, angle, amount - friction, collisionManager);
            }
        }, 0.03f);
    }

}
