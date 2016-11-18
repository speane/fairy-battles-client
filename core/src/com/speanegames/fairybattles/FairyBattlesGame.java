package com.speanegames.fairybattles;

import com.badlogic.gdx.Game;
import com.speanegames.fairybattles.screens.BattleFieldScreen;

public class FairyBattlesGame extends Game {

	@Override
	public void create () {
		setScreen(new BattleFieldScreen(this));
	}
}
