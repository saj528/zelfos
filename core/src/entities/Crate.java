package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Crate extends Sprite {
    public Crate(float x, float y){
        super(new Texture("crate.png"));
        setPosition(x, y);
    }
}

