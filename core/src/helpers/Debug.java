package helpers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;


public class Debug {

    public static void drawHitbox(Batch batch, Rectangle hitbox) {
        Pixmap pixmap = new Pixmap((int)hitbox.getWidth(), (int)hitbox.getHeight(), Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(1, 1, 0, 0.3f));
        pixmap.fillRectangle(0, 0, (int)hitbox.getWidth(), (int)hitbox.getHeight());
        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        batch.begin();
        batch.draw(texture, (float)hitbox.getX(), (float)hitbox.getY());
        batch.end();
    }
}
