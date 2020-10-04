package hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import helpers.GameInfo;
import helpers.WaveManager;

public class WavesHud {
    private final WaveManager waveManager;
    BitmapFont font = new BitmapFont();

    public WavesHud(WaveManager waveManager) {
        this.waveManager = waveManager;
    }

    public void draw(Batch batch) {
        batch.begin();
        font.setColor(new Color(1, 0, 0, 1));
        font.draw(batch, "Current Wave: " + waveManager.getCurrentWave(), GameInfo.WIDTH - 130, GameInfo.HEIGHT - 20);
        batch.end();

    }
}
