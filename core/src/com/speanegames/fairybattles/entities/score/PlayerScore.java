package com.speanegames.fairybattles.entities.score;

import com.speanegames.fairybattles.entities.player.Player;

public class PlayerScore {

    private Player player;
    private int score;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
