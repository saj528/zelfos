package scenes.game;

import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

public interface Collidable {
    Rectangle getHitbox();
    ArrayList<Class> getIgnoreClassList();
}
