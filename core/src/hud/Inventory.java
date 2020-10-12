package hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import entities.Player;
import entities.items.Item;
import helpers.GameInfo;

import java.util.ArrayList;

public class Inventory {
    private Player player;
    private ArrayList<Item> itemCollection;
    private int x;
    private int y;
    public Inventory(Player player) {
        this.player = player;
        x = GameInfo.WIDTH - 220;
        y = 10;
    }

    public void addToItemCollection(Item item){
        itemCollection.add(item);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }


/*
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

     */
}
