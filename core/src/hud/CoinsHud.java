package hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import helpers.GameInfo;
import scenes.game.CoinManager;

public class CoinsHud {
    private final CoinManager coinManager;
    BitmapFont font = new BitmapFont();

    public CoinsHud(CoinManager coinManager) {
        this.coinManager = coinManager;
    }

    public void draw(Batch batch) {
        batch.begin();
        font.setColor(new Color(1, 0, 0, 1));
        font.draw(batch, "Coins: " + coinManager.getTotalCoins(), 10, GameInfo.HEIGHT - 30);
        batch.end();
    }
}
