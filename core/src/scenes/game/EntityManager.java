package scenes.game;

import entities.Entity;
import entities.Killable;

import java.util.ArrayList;

public interface EntityManager {
    ArrayList<Collidable> getCollidables();
    Entity getEntityByType(String className);
    void addEntity(Entity entity);
}
