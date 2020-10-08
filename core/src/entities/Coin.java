package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import scenes.game.CoinManager;

import java.awt.*;

public class Coin implements Killable{
    private final Animation<TextureRegion> coinAnimation;
    private final float x;
    private final float y;
    private Animation<TextureRegion> coinTexture;
    private float coinAnimationTime = 0;
    private float coinAnimationSpeed = 0.25f;
    private Player player;
    private int coinWidth;
    private boolean isDead = false;
    private CoinManager coinManager;

    public Coin(float x, float y, Player player, CoinManager coinManager) {
        this.x = x;
        this.y = y;
        this.player = player;
        this.coinManager = coinManager;
        Texture coinSheet = new Texture("coin.png");
        TextureRegion[][] coinSheetRegions = TextureRegion.split(coinSheet,
                coinSheet.getWidth() / 6,
                coinSheet.getHeight() / 1);
        coinWidth = coinSheet.getWidth() / 6;
        TextureRegion[] coinFrames = new TextureRegion[6];
        coinFrames[0] = coinSheetRegions[0][0];
        coinFrames[1] = coinSheetRegions[0][1];
        coinFrames[2] = coinSheetRegions[0][2];
        coinFrames[3] = coinSheetRegions[0][3];
        coinFrames[4] = coinSheetRegions[0][4];
        coinFrames[5] = coinSheetRegions[0][5];
        coinAnimation = new Animation<>(coinAnimationSpeed, coinFrames);
    }

    public void update(float delta) {
        coinAnimationTime += delta;

        if (player.getBoundingRectangle().overlaps(new Rectangle(x, y, coinWidth, coinWidth))) {
            coinManager.incrementCoins(1);
            isDead = true;
        }

    }

    public void draw(Batch batch, ShapeRenderer shapeRenderer) {
        batch.begin();
        batch.draw(coinAnimation.getKeyFrame(coinAnimationTime, true), x, y);
        batch.end();
    }

    public boolean isDead() {
        return isDead;
    }
}
