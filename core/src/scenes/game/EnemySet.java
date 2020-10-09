package scenes.game;

public class EnemySet {

    private final Lane lane;
    private final int count;
    private final EnemyType enemyType;

    public enum EnemyType {
        ARCHER,
        SOLDIER,
        HORNET,
        PORCUPINE,
        WIZARD,
    }

    public enum Lane {
        NORTH,
        SOUTH,
        EAST,
        WEST,
    }

    public EnemySet(EnemyType enemyType, int count, Lane lane) {
        this.count = count;
        this.enemyType = enemyType;
        this.lane = lane;
    }

    public EnemyType getEnemyType() {
        return enemyType;
    }

    public Lane getLane() {
        return lane;
    }

    public int getCount() {
        return count;
    }
}
