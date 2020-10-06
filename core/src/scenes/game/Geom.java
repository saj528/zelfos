package scenes.game;

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
        entity.getBoundingRectangle().getCenter(center);
        return center;
    }
}
