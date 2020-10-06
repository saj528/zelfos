package scenes.game;


public interface CollisionManager {
    boolean isCollidingWithMap(Collidable collidable);
    boolean isCollidingWithOtherCollidables(Collidable collidable);
}
