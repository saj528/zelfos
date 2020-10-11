package scenes.game;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import entities.Entity;

import java.util.ArrayList;

public class Geom {
    public static ArrayList<Entity> getEntitiesInRange(ArrayList<Entity> entities, Entity target, float range) {
        ArrayList<Entity> entitiesInRange = new ArrayList<>();
        Vector2 targetCenter = target.getCenter();
        for (Entity entity : entities) {
            Vector2 entityCenter = entity.getCenter();
            float distance = targetCenter.dst(entityCenter);
            if (distance < range) {
                entitiesInRange.add(entity);
            }
        }
        return entitiesInRange;
    }

    public static Vector2 getCenter(Entity entity) {
        Vector2 center = new Vector2(0, 0);
        Rectangle rect = entity.getBoundingRectangle();
        rect.getCenter(center);
        return center;
    }

    public static float distanceBetween(Entity source, Entity target) {
        Vector2 sourceCenter = getCenter(source);
        Vector2 targetCenter = getCenter(target);
        return sourceCenter.dst(targetCenter);
    }

    public static float angleBetween(Entity source, Entity target) {
        Vector2 targetCenter = target.getCenter();
        Vector2 sourceCenter = source.getCenter();
        float dy = targetCenter.y - sourceCenter.y;
        float dx = targetCenter.x - sourceCenter.x;
        final float angle = (float)Math.atan2(dy, dx);
        return angle;

    }
}
