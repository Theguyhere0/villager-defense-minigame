package me.theguyhere.villagerdefense.game.models;

import java.util.List;

public class ArenaRecord {
    private final int wave; // Record wave
    private final List<String> players; // Name of players that reached this record

    public ArenaRecord(int wave, List<String> players) {
        this.wave = wave;
        this.players = players;
    }

    public int getWave() {
        return wave;
    }

    public List<String> getPlayers() {
        return players;
    }
}
