package hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import entities.Player;
import helpers.GameInfo;
import scenes.game.CoinManager;

public class BombsHud {
    private final Player player;
    BitmapFont font = new BitmapFont();

    public BombsHud(Player player) {
        this.player = player;
    }

    public void draw(Batch batch) {
        batch.begin();
        font.setColor(new Color(1, 0, 0, 1));
        font.draw(batch, "Bombs: " + player.getTotalBombs(), 10, GameInfo.HEIGHT - 60);
        batch.end();
    }
}
