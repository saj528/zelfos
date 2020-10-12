package entities.items;

import com.badlogic.gdx.graphics.Texture;

public interface Item {
    Texture getTexture();
    Boolean currentlyOwn();
    void useItem();
    void buyItem();
}
