package scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.zelfos.game.GameMain;
import helpers.GameInfo;
import player.Player;

public class MainMenu implements Screen, ContactListener {

    private GameMain game;
    private Player player;
    private World world;
    private OrthographicCamera box2DCamera;

    public MainMenu(GameMain game) {
        this.game = game;
        box2DCamera = new OrthographicCamera();
        box2DCamera.setToOrtho(false, GameInfo.WIDTH / GameInfo.PPM,GameInfo.HEIGHT/ GameInfo.PPM);
        box2DCamera.position.set(GameInfo.WIDTH / 2f,GameInfo.HEIGHT / 2f, 0);
        world = new World(new Vector2(0,-9.8f),true);
        world.setContactListener(this);
        player = new Player(world, GameInfo.WIDTH / 2, GameInfo.HEIGHT / 2 + 250);
    }

    void update(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            player.walkLeft();
        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            player.walkRight();
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(1,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.getBatch().begin();
        game.getBatch().draw(player,player.getX(),player.getY() - player.getHeight() / 2f);
        game.getBatch().end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        player.getTexture().dispose();
    }

    @Override
    public void beginContact(Contact contact) {

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
