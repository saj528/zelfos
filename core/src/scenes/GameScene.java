package scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.*;
import com.zelfos.game.GameMain;
import helpers.GameInfo;
import entities.Crate;
import entities.Player;

public class GameScene implements Screen, ContactListener {

    private GameMain game;
    private Player player;
    private OrthographicCamera camera;
    private Crate crate;

    public GameScene(GameMain game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(
            false,
            GameInfo.WIDTH,
            GameInfo.HEIGHT
        );
        player = new Player(
            GameInfo.WIDTH / 2,
            GameInfo.HEIGHT / 2 + 250
        );
        crate = new Crate(100, 100);
    }

    void update(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            player.walkLeft();
        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            player.walkRight();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            player.walkUp();
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            player.walkDown();
        }

        camera.position.x = player.getX();
        camera.position.y = player.getY();
        camera.update();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(1,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.getBatch().setProjectionMatrix(camera.combined);
        game.getBatch().begin();
        game.getBatch().draw(
            player,
            player.getX(),
            player.getY()
        );
        game.getBatch().draw(
            crate,
            crate.getX(),
            crate.getY()
        );
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
