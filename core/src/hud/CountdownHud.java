package hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import helpers.GameInfo;
import scenes.game.WaveManager;

public class CountdownHud {
    private final WaveManager waveManager;
    BitmapFont font = new BitmapFont();

    public CountdownHud(WaveManager waveManager) {
        this.waveManager = waveManager;
    }

    public void draw(Batch batch) {
        batch.begin();
        font.setColor(new Color(0, 0, 1, 1));
        font.draw(batch, "Next wave in " + waveManager.getSecondsUntilNextWave() + " seconds!", GameInfo.WIDTH / 2, GameInfo.HEIGHT - 60);
        batch.end();
    }
}
