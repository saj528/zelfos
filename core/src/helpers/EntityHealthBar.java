package helpers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import entities.Damageable;
import entities.Entity;

public class EntityHealthBar {

    private final Entity entity;

    public EntityHealthBar(Entity entity) {
        this.entity = entity;
    }

    public void draw(Batch batch, ShapeRenderer shapeRenderer) {
        float width = entity.getBoundingRectangle().getWidth();
        float height = entity.getBoundingRectangle().getHeight();
        Damageable damageable = (Damageable)entity;

        Pixmap pixmap = new Pixmap((int) width, 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0, 0, 0, 1f));
        pixmap.fillRectangle(0, 0, (int)  width, 10);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        batch.begin();
        batch.draw(texture, (float) entity.getX(), (float) entity.getY() + height + 10);
        batch.end();

        pixmap = new Pixmap((int) width, 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(1, 0, 0, 1f));
        pixmap.fillRectangle(0, 0, (int) width * damageable.getHealth() / damageable.getMaxHealth(), 10);
        texture = new Texture(pixmap);
        pixmap.dispose();

        batch.begin();
        batch.draw(texture, (float) entity.getX(), (float) entity.getY() + height + 10);
        batch.end();
    }
}
