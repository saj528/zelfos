package scenes.game;

public class EnemySet {
    public int getCount() {
        return count;
    }

    private final int count;
    private final EnemyType enemyType;

    public enum EnemyType {
        ARCHER,
        SOLDIER,
    }

    public EnemySet(EnemyType enemyType, int count) {
        this.count = count;
        this.enemyType = enemyType;
    }

    public EnemyType getEnemyType() {
        return enemyType;
    }
}
