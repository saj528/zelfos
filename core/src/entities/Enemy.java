package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Enemy extends Sprite {

    private int health;
    private boolean isDead = false;

    public Enemy(float x, float y) {
        super(new Texture("enemy.png"));
        health = 5;
        setPosition(x, y);
    }

    public void damage(int amount) {
        health -= amount;

        if (health <= 0) {
            isDead = true;
        }
    }

    public boolean isDead() {
        return isDead;
    }
}
