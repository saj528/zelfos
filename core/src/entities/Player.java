package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Player extends Sprite {
    private float SPEED = 5.0f;

    public Player(float x, float y){
        super(new Texture("Player.png"));
        setPosition(x, y);
    }

    public void walkLeft() {
        setX(getX() - SPEED);
    }

    public void walkRight() {
        setX(getX() + SPEED);
    }

    public void walkDown() {
        setY(getY() - SPEED);
    }

    public void walkUp() {
        setY(getY() + SPEED);
    }
}

