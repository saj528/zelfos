package scenes.game;

import entities.Killable;

import java.util.ArrayList;

public interface EntityManager {
    ArrayList<Collidable> getCollidables();
}
