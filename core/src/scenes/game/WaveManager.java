package scenes.game;

public interface WaveManager {
    int getCurrentWave();
    int getSecondsUntilNextWave();
    void startIntermission();
}
