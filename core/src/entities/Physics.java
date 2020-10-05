package entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

public class Physics {

    public static void knockback(final Knockable source, final Knockable target, final float amount) {
        if (amount <= 0) return;

        Vector2 targetCenter = new Vector2(0, 0);
        Vector2 sourceCenter = new Vector2(0, 0);
        target.getBoundingRectangle().getCenter(targetCenter);
        source.getBoundingRectangle().getCenter(sourceCenter);

        final float angle = (float)Math.atan2(targetCenter.y - sourceCenter.y, targetCenter.x - sourceCenter.x);

        final float friction = 9.0f;

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                target.setX(target.getX() + amount * (float)Math.cos(angle));
                target.setY(target.getY() + amount * (float)Math.sin(angle));
                knockback(source, target, amount - friction);
            }
        }, 0.03f);
    }

    public static void knockback(final Knockable target, final float angle, final float amount) {
        if (amount <= 0) return;

        final float friction = 9.0f;

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                target.setX(target.getX() + amount * (float)Math.cos(angle));
                target.setY(target.getY() + amount * (float)Math.sin(angle));
                knockback(target, angle, amount - friction);
            }
        }, 0.03f);
    }

}
