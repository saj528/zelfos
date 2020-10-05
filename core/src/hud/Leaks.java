package hud;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import scenes.game.LeakManager;
import entities.Player;

public class Leaks {
    private Texture leakTexture;
    private LeakManager leakManager;
    private Player player;
    private float x;
    private float y;

    public Leaks(LeakManager leakManager, float x, float y) {
        this.leakManager = leakManager;
        leakTexture = new Texture("skull.png");
        this.x = x;
        this.y = y;
    }

    public void draw(Batch batch) {
        batch.begin();
        int totalLeaksLeft = leakManager.getLeaks();
        float leaksWidth = totalLeaksLeft * (leakTexture.getWidth() + 10);
        for (int leaks = 0; leaks < totalLeaksLeft; leaks++) {
            batch.draw(leakTexture, -leaksWidth / 2 + x + leaks * (leakTexture.getWidth() + 10), y);
        }
        batch.end();
    }
}
