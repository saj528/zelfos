package scenes.game;

import entities.Entity;
import entities.Killable;
import entities.Player;

import java.util.ArrayList;

public interface EntityManager {
    ArrayList<Collidable> getCollidables();
    Entity getEntityByType(Class clazz);
    void addEntity(Entity entity);
    ArrayList<Entity> getEntitiesByType(Class clazz);
    Player getPlayer();
}
