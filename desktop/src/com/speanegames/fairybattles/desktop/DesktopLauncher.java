package com.speanegames.fairybattles.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.speanegames.fairybattles.FairyBattlesGame;
import com.speanegames.fairybattles.util.Config;

public class DesktopLauncher {

	private final static String WINDOW_TITLE = "Fairy Battles";

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = WINDOW_TITLE;
		config.width = Config.SCREEN_WIDTH;
		config.height = Config.SCREEN_HEIGHT;
		new LwjglApplication(new FairyBattlesGame(), config);
	}
}
