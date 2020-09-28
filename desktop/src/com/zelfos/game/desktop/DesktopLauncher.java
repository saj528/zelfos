package com.zelfos.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.zelfos.game.GameMain;
import helpers.GameInfo;

public class DesktopLauncher {
	public static void main (String[] arg) {
		//game config
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = GameInfo.WIDTH;
		config.height = GameInfo.HEIGHT;
		//game creation
		new LwjglApplication(new GameMain(), config);
	}
}
