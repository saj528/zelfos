package scenes.game;

public interface CoinManager {
    void createCoin(float x, float y);
    void incrementCoins(int amount);
    int getTotalCoins();
    void removeCoins(int amount);
}
