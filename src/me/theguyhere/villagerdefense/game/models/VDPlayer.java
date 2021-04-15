package me.theguyhere.villagerdefense.game.models;

import org.bukkit.entity.Player;

public class VDPlayer {
    private final Player player; // Player in a game
    private boolean spectating; // Whether they are spectating
    private int gems; // Gem count
    private int kills; // Kill count

    public VDPlayer(Player player, boolean spectating) {
        this.player = player;
        this.spectating = spectating;
        gems = 0;
        kills = 0;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isSpectating() {
        return spectating;
    }

    public int getGems() {
        return gems;
    }

    public int getKills() {
        return kills;
    }

    public void flipSpectating() {
        spectating = !spectating;
    }

    public void addGems(int change) {
        gems += change;
    }

    public boolean canAfford(int cost) {
        return cost <= gems;
    }

    public void incrementKills() {
        kills++;
    }
}
