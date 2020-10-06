package particles;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface Particle {
    void draw(Batch batch);
    void update(float delta);
}
