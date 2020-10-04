package helpers;

public class Wave {
    private EnemySet[] enemySets;

    public Wave(EnemySet[] enemySets) {
        this.enemySets = enemySets;
    }

    public EnemySet[] getEnemySets() {
        return enemySets;
    }
}
