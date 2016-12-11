package com.speanegames.fairybattles.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.speanegames.fairybattles.FairyBattlesGame;
import com.speanegames.fairybattles.config.AppConfig;

public class DesktopLauncher {

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.resizable = false;
        config.samples = AppConfig.SAMPLES;
        config.title = AppConfig.WINDOW_TITLE;
		config.width = AppConfig.SCREEN_WIDTH;
		config.height = AppConfig.SCREEN_HEIGHT;

        // TexturePacker.process("../assets/textures", "../assets/textures/archive", "fairy_battles_assets");

		new LwjglApplication(new FairyBattlesGame(), config);
	}
}
