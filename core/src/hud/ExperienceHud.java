package hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import entities.Player;
import helpers.GameInfo;
import scenes.game.CoinManager;

public class ExperienceHud {
    private final Player player;
    BitmapFont font = new BitmapFont();

    public ExperienceHud(Player player) {
        this.player = player;
    }

    public void draw(Batch batch) {
        batch.begin();
        font.setColor(new Color(1, 0, 0, 1));
        font.draw(batch, "Level " + player.getLevel() + " Exp: " + player.getExperience() + " / " + player.getExperienceUntilNextLevel(), 10, GameInfo.HEIGHT - 90);
        batch.end();
    }
}
