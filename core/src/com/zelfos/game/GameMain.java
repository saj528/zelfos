package com.zelfos.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import scenes.game.GameScene;
import scenes.GameWinScene;
import scenes.MainMenuScene;

public class GameMain extends Game {
	SpriteBatch batch;

	@Override
	public void create () {
		batch = new SpriteBatch();
		showMainMenuScene();
	}

	@Override
	public void render () {
		super.render();
	}

	public SpriteBatch getBatch() {
		return batch;
	}

	public void showMainMenuScene() {
		setScreen(new MainMenuScene(this));
	}

	public void showGameScene() {
		setScreen(new GameScene(this));
	}

	public void showWinScreen() {
		setScreen(new GameWinScene(this));
	}
}
