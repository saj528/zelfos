package scenes.game;

import java.util.ArrayList;

public class Wave {
    private ArrayList<EnemySet> enemySets;

    public Wave(ArrayList<EnemySet> enemySets) {
        this.enemySets = enemySets;
    }

    public ArrayList<EnemySet> getEnemySets() {
        return enemySets;
    }
}
