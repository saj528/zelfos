package entities.enemies.logic;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import entities.*;
import entities.enemies.Enemy;
import entities.enemies.Ranged;
import entities.structures.TownHall;
import scenes.game.*;

import java.util.ArrayList;

public class Range implements UpdateLogic {

    private final Entity enemy;
    private final EntityManager entityManager;
    private final Player player;
    private final CollisionManager collisionManager;
    private float PURSUE_DISTANCE = 150;
    private ArrayList<Vector2> pathwayCoordinates;
    private int pathCounter = 1;
    private boolean canAttack = true;
    private State state = State.WALK;
    private Entity target = null;
    private Vector2 nextPointToWalkTowards;

    public enum State {
        WALK,
        PURSUE,
        ATTACK,
    }

    public Range(Entity enemy, EntityManager entityManager, Player player, ArrayList<Vector2> pathwayCoordinates, CollisionManager collisionManager) {
        this.enemy = enemy;
        this.collisionManager = collisionManager;
        this.entityManager = entityManager;
        this.player = player;
        this.pathwayCoordinates = pathwayCoordinates;
        this.nextPointToWalkTowards = pathwayCoordinates.get(0);
    }

    public State getState() {
        return state;
    }

    @Override
    public void update(float delta) {
        switch (state) {
            case WALK:
                walk();
                break;
            case PURSUE:
                pursue();
                break;
            case ATTACK:
                attack();
                break;
        }
    }

    private void walk() {

        ArrayList<Entity> targets = (ArrayList<Entity>) (ArrayList<?>) entityManager.getEntitiesByType(Friendly.class);
        for (Entity target : targets) {
            if (!((Friendly)target).isTargetable()) continue;
            float distToMerc = Geom.distanceBetween(enemy, target);
            if (distToMerc <= PURSUE_DISTANCE) {
                state = State.PURSUE;
                this.target = target;
                return;
            }
        }

        double distanceFromCurrentPathGoal = nextPointToWalkTowards.dst(new Vector2(enemy.getX(), enemy.getY()));

        if (distanceFromCurrentPathGoal <= 10) {
            if (pathwayCoordinates.size() > pathCounter) {
                nextPointToWalkTowards.y = pathwayCoordinates.get(pathCounter).y;
                nextPointToWalkTowards.x = pathwayCoordinates.get(pathCounter).x;
                pathCounter++;
            }
        }

        walkTo(nextPointToWalkTowards);
    }


    // TODO: make into helper function
    private void walkTo(Vector2 point) {
        Vector2 enemyCenter = enemy.getCenter();

        float angleToWalk = (float) Math.atan2(point.y - enemyCenter.y, point.x - enemyCenter.x);
        float originalX = enemy.getX();
        enemy.setX((float) (enemy.getX() + Math.cos(angleToWalk) * ((Enemy) enemy).getSpeed()));
        if (collisionManager.isCollidingWithMap((Collidable) enemy) || collisionManager.isCollidingWithOtherCollidables((Collidable) enemy)) {
            enemy.setX(originalX);
        }

        float originalY = enemy.getY();
        enemy.setY((float) (enemy.getY() + Math.sin(angleToWalk) * ((Enemy) enemy).getSpeed()));
        if (collisionManager.isCollidingWithMap((Collidable) enemy) || collisionManager.isCollidingWithOtherCollidables((Collidable) enemy)) {
            enemy.setY(originalY);
        }
    }

    private void pursue() {
        if (Geom.distanceBetween(enemy, target) > PURSUE_DISTANCE) {
            state = State.WALK;
            target = null;
            return;
        }

        float distance = Geom.distanceBetween(enemy, target);
        if (distance <= ((Enemy) enemy).getAttackRange()) {
            state = State.ATTACK;
            ((Enemy) enemy).onAttackStart();
            return;
        }

        walkTo(target.getCenter());
    }


    private void attack() {
        if (canAttack) {
            canAttack = false;

            final Entity entity = enemy;

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    if (((Killable) entity).isDead()) return;
                    if (target == null) return;
                    float distance = Geom.distanceBetween(entity, target);
                    if (distance <= ((Enemy) enemy).getAttackRange()) {
                        ((Ranged)enemy).fireProjectile(target);
                    }
                }
            }, 0.3f);

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    canAttack = true;
                    target = null;
                    state = State.WALK;
                }
            }, ((Enemy) enemy).getAttackDelay());
        }
    }
}

