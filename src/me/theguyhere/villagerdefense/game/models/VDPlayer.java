package me.theguyhere.villagerdefense.game.models;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class VDPlayer {
    private final Player player; // Player in a game
    private boolean spectating; // Whether they are spectating
    private boolean ghost; // Whether they are a ghost
    private boolean left; // Whether a player has left the arena or not
    private int gems; // Gem count
    private int kills; // Kill count
    private int wolves; // Wolf count
    private int joinedWave; // The wave at which the player joined at
    private String kit; // Selected kit
    private ItemStack helmet; // Helmet to hold for ninja ability
    private ItemStack chestplate; // Chestplate to hold for ninja ability
    private ItemStack leggings; // Leggings to hold for ninja ability
    private ItemStack boots; // Boots to hold for ninja ability

    public VDPlayer(Player player, boolean spectating) {
        this.player = player;
        this.spectating = spectating;
        left = false;
        gems = 0;
        kills = 0;
        wolves = 0;
        joinedWave = 0;
        kit = "None";
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isSpectating() {
        return spectating;
    }

    public boolean isGhost() {
        return ghost;
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

    public void flipGhost() {
        ghost = !ghost;
    }

    public boolean hasLeft() {
        return left;
    }

    public void leave() {
        left = true;
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

    public String getKit() {
        return kit;
    }

    public int getWolves() {
        return wolves;
    }

    public void incrementWolves() {
        wolves++;
    }

    public void decrementWolves() {
        wolves--;
    }

    public int getJoinedWave() {
        return joinedWave;
    }

    public void setJoinedWave(int joinedWave) {
        this.joinedWave = joinedWave;
    }

    public void setKit(String kit) {
        this.kit = kit;
    }

    public void hideArmor() {
        helmet = player.getInventory().getHelmet();
        player.getInventory().setHelmet(null);
        chestplate = player.getInventory().getChestplate();
        player.getInventory().setChestplate(null);
        leggings = player.getInventory().getLeggings();
        player.getInventory().setLeggings(null);
        boots = player.getInventory().getBoots();
        player.getInventory().setBoots(null);
    }

    public void exposeArmor() {
        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
    }
}
