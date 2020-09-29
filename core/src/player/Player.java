package player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.*;
import helpers.GameInfo;

public class Player extends Sprite {
    private World world;
    private float SPEED = 5.0f;

    public Player(World world, float x, float y){
        super(new Texture("Player.png"));
        this.world = world;
        setPosition(x, y);
    }

    public void walkLeft() {
        this.setX(this.getX() - this.SPEED);
    }

    public void walkRight() {
        this.setX(this.getX() + this.SPEED);
    }
}

