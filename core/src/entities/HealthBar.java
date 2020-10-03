package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class HealthBar {
    private Texture heartRedTexture;
    private Texture heartGrayTexture;
    private Player player;
    private int x;
    private int y;

    public HealthBar(Player player) {
        this.player = player;
        heartRedTexture = new Texture("heart-red.png");
        heartGrayTexture = new Texture("heart-gray.png");
        x = 10;
        y = 10;
    }

    public void draw(Batch batch) {
        batch.begin();
        for (int life = 0; life < player.getMaxLives(); life++) {
            if (life < player.getLives() ) {
                batch.draw(heartRedTexture, x + life * (heartRedTexture.getWidth() + 10), y);
            } else {
                batch.draw(heartGrayTexture, x + life * (heartRedTexture.getWidth() + 10), y);
            }
        }
        batch.end();
    }
}
