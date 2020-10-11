package entities;

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

    public Musket(Player player, EntityManager entityManager, CollisionManager collisionManager) {
        this.player = player;
        this.entityManager = entityManager;
        this.collisionManager = collisionManager;
    }

    public void attack() {
        if (canAttack) {
            canAttack = false;
            entityManager.addEntity(new Bullet(player.getX(), player.getY(), player.getFacingAngle() ,entityManager, collisionManager));
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    canAttack = true;
                }
            }, attackDelay);
        }

    }
}
