package me.theguyhere.villagerdefense.plugin.game.models.arenas;

import lombok.Getter;

import java.util.List;

@Getter
public class ArenaRecord {
    private final int wave; // Record wave
    private final List<String> players; // Name of players that reached this record

    public ArenaRecord(int wave, List<String> players) {
        this.wave = wave;
        this.players = players;
    }
}
