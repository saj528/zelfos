package hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import helpers.GameInfo;
import scenes.game.WaveManager;

public class CountdownHud {
    private final WaveManager waveManager;
    BitmapFont font = new BitmapFont();

    public CountdownHud(WaveManager waveManager) {
        this.waveManager = waveManager;
    }

    public void draw(Batch batch) {
        String text = "Press ENTER to start the next wave!";
        if (waveManager.isOnIntermission()) {
            GlyphLayout glyphLayout = new GlyphLayout();
            glyphLayout.setText(font, text);

            batch.begin();
            font.setColor(new Color(1, 0, 0, 1));
            font.draw(batch, text, GameInfo.WIDTH / 2 - glyphLayout.width / 2, GameInfo.HEIGHT - glyphLayout.height - 5);
            batch.end();
        }
    }
}
