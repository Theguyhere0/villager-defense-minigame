package me.theguyhere.villagerdefense.game;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Arena {
    private final Main plugin;
    private final Utils utils;

    // Persistent data
    private final int arena; // Arena number
    private String name; // Name of the arena
    private int maxPlayers; // Maximum players in an arena
    private int minPlayers; // Minimum players in an arena
    private Location playerSpawn; // Location of player spawn
    private List<Location> monsterSpawns = new ArrayList<>(); // List of monster spawn locations
    private List<Location> villagerSpawns = new ArrayList<>(); // List of villager spawn locations
    private boolean closed; // Indicates whether the arena is closed

    // Temporary data
    private final Tasks task; // The tasks object for the arena
    private boolean caps; // Indicates whether the naming inventory has caps lock on
    private boolean active; // Indicates whether the arena has a game ongoing
    private boolean ending; // Indicates whether the arena is about to end
    private int currentWave; // Current game wave
    private int villagers; // Villager count
    private int enemies; // Enemy count
    private final List<VDPlayer> players = new ArrayList<>(); // Tracks players playing and their other related stats
    private Inventory shop; // Shop inventory

    public Arena(Main plugin, int arena, Tasks task) {
        this.plugin = plugin;
        utils = new Utils(plugin);
        this.arena = arena;
        this.task = task;
        currentWave = 0;
        villagers = 0;
        enemies = 0;
        updateArena();
    }

    public int getArena() {
        return arena;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public Location getPlayerSpawn() {
        return playerSpawn;
    }

    public List<Location> getMonsterSpawns() {
        return monsterSpawns;
    }

    public List<Location> getVillagerSpawns() {
        return villagerSpawns;
    }

    public boolean isClosed() {
        return closed;
    }

    public Tasks getTask() {
        return task;
    }

    public boolean isCaps() {
        return caps;
    }

    public void flipCaps() {
        caps = !caps;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isEnding() {
        return ending;
    }

    public void flipEnding() {
        ending = !ending;
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public void incrementCurrentWave() {
        currentWave++;
    }

    public void resetCurrentWave() {
        currentWave = 0;
    }

    public int getVillagers() {
        return villagers;
    }

    public void incrementVillagers() {
        villagers++;
    }

    public void decrementVillagers() {
        villagers--;
    }

    public void resetVillagers() {
        villagers = 0;
    }

    public int getEnemies() {
        return enemies;
    }

    public void incrementEnemies() {
        enemies++;
    }

    public void decrementEnemies() {
        enemies--;
    }

    public void resetEnemies() {
        enemies = 0;
    }

    public List<VDPlayer> getPlayers() {
        return players;
    }

    public List<VDPlayer> getActives() {
        return players.stream().filter(p -> !p.isSpectating()).collect(Collectors.toList());
    }

    public List<VDPlayer> getGhosts() {
        return getActives().stream().filter(p -> p.getPlayer().getGameMode() == GameMode.SPECTATOR)
                .collect(Collectors.toList());
    }

    public List<VDPlayer> getSpectators() {
        return players.stream().filter(VDPlayer::isSpectating).collect(Collectors.toList());
    }

    public VDPlayer getPlayer(Player player) {
        return players.stream().filter(p -> p.getPlayer().equals(player)).collect(Collectors.toList()).get(0);
    }

    public boolean hasPlayer(Player player) {
        return players.stream().anyMatch(p -> p.getPlayer().equals(player));
    }

    public boolean hasPlayer(VDPlayer player) {
        return players.stream().anyMatch(p -> p.equals(player));
    }

    public int getActiveCount() {
        return getActives().size();
    }

    public int getAlive() {
        return getActiveCount() - getGhostCount();
    }

    public int getGhostCount() {
        return getGhosts().size();
    }

    public int getSpectatorCount() {
        return getSpectators().size();
    }

    public Inventory getShop() {
        return shop;
    }

    public void setShop(Inventory shop) {
        this.shop = shop;
    }

    public void updateArena() {
        name = plugin.getData().getString("a" + arena + ".name");
        maxPlayers = plugin.getData().getInt("a" + arena + ".max");
        minPlayers = plugin.getData().getInt("a" + arena + ".min");
        playerSpawn = utils.getConfigLocation("a" + arena + ".spawn");
        monsterSpawns = utils.getConfigLocationList("a" + arena + ".monster").stream()
                .filter(Objects::nonNull).collect(Collectors.toList());
        villagerSpawns = utils.getConfigLocationList("a" + arena + ".villager").stream()
                .filter(Objects::nonNull).collect(Collectors.toList());
        closed = plugin.getData().getBoolean("a" + arena + ".closed");
    }
}
