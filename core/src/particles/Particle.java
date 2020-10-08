package particles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface Particle {
    void draw(Batch batch, ShapeRenderer shapeRenderer);
    void update(float delta);
}
