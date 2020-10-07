package hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import entities.Player;
import helpers.GameInfo;

public class Inventory {
    private Player player;
    private Texture inventoryBox;
    private Texture potionFull;
    private Texture potionEmpty;
    private int inventorySize = 4;
    private int x;
    private int y;
    BitmapFont font = new BitmapFont();

    public Inventory(Player player) {
        this.player = player;
        inventoryBox = new Texture("itemAndUiSprites/s_inventory_box.png");
        potionFull = new Texture("healthBottleFull.png");
        potionEmpty = new Texture("healthBottleEmpty.png");

        x = GameInfo.WIDTH - 220;
        y = 10;
    }

    public void draw(Batch batch) {
        batch.begin();
        for (int i = 0; i <= inventorySize; i++) {
            batch.draw(inventoryBox, x + i * (inventoryBox.getWidth() + 10), y);
            font.setColor(new Color(255,255,0, 1));
            font.getData().setScale(0.7f,0.7f);
            font.draw(batch, String.valueOf(i + 1), (x + 5) + i * (inventoryBox.getWidth() + 10), y + 27);
            if(player.getHasPotion() && i == 0){
                batch.draw(potionFull, x + i * (inventoryBox.getWidth() + 10), y);
            }else if(!player.getHasPotion() && i == 0){
                batch.draw(potionEmpty, x + i * (inventoryBox.getWidth() + 10), y);
            }
        }
        batch.end();
    }
}
