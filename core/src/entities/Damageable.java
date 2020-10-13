package entities;

public interface Damageable {
    void damage(int amount);
    int getHealth();
    int getMaxHealth();
}
