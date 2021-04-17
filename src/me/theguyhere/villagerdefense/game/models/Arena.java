package me.theguyhere.villagerdefense.game.models;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.game.Tasks;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;
import java.util.stream.Collectors;

public class Arena {
    private final Main plugin;
    private final Utils utils;

    // Persistent data
    private final int arena; // Arena number
    private String name; // Name of the arena
    private int maxPlayers; // Maximum players in an arena
    private int minPlayers; // Minimum players in an arena
    private int maxWaves; // Maximum waves in an arena
    private int waveTimeLimit; // Base wave time limit
    private int difficultyMultiplier;
    private Location playerSpawn; // Location of player spawn
    private Location waitingRoom; // Location of waiting room
    private List<Location> monsterSpawns = new ArrayList<>(); // List of monster spawn locations
    private List<Location> villagerSpawns = new ArrayList<>(); // List of villager spawn locations
    private String spawnTableFile; // File name of the spawn table
    private boolean dynamicCount; // Toggle for dynamic mob count
    private boolean dynamicDifficulty; // Toggle for dynamic difficulty
    private boolean dynamicPrices; // Toggle for dynamic prices
    private boolean dynamicLimit; // Toggle for dynamic wave time limit
    private boolean closed; // Indicates whether the arena is closed
    private final List<ArenaRecord> arenaRecords = new ArrayList<>(); // List of top arena records

    // Temporary data
    private final Tasks task; // The tasks object for the arena
    private boolean caps; // Indicates whether the naming inventory has caps lock on
    private boolean active; // Indicates whether the arena has a game ongoing
    private boolean ending; // Indicates whether the arena is about to end
    private int currentWave; // Current game wave
    private int villagers; // Villager count
    private int enemies; // Enemy count
    private final List<VDPlayer> players = new ArrayList<>(); // Tracks players playing and their other related stats
    private Inventory weaponShop; // Weapon shop inventory
    private Inventory armorShop; // Armor shop inventory
    private Inventory consumeShop; // Consumables shop inventory
    private Inventory customShop; // Custom shop inventory
    private BossBar timeLimitBar; // Time limit bar

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

    public int getMaxWaves() {
        return maxWaves;
    }

    public int getWaveTimeLimit() {
        return waveTimeLimit;
    }

    public int getDifficultyMultiplier() {
        return difficultyMultiplier;
    }

    public Location getPlayerSpawn() {
        return playerSpawn;
    }

    public Location getWaitingRoom() {
        return waitingRoom;
    }

    public List<Location> getMonsterSpawns() {
        return monsterSpawns;
    }

    public List<Location> getVillagerSpawns() {
        return villagerSpawns;
    }

    public String getSpawnTableFile() {
        return spawnTableFile;
    }

    public boolean isDynamicCount() {
        return dynamicCount;
    }

    public boolean isDynamicDifficulty() {
        return dynamicDifficulty;
    }

    public boolean isDynamicPrices() {
        return dynamicPrices;
    }

    public boolean isDynamicLimit() {
        return dynamicLimit;
    }

    public boolean isClosed() {
        return closed;
    }

    public boolean checkNewRecord(ArenaRecord record) {
        // Automatic record
        if (arenaRecords.size() < 4) {
            arenaRecords.add(record);
        }

        // New record
        else if (arenaRecords.stream().anyMatch(arenaRecord -> arenaRecord.getWave() < record.getWave())) {
            arenaRecords.sort(Comparator.comparingInt(ArenaRecord::getWave));
            arenaRecords.set(0, record);
        }

        // No record
        else return false;

        // Save data
        for (int i = 0; i < arenaRecords.size(); i++) {
            plugin.getArenaData().set("a" + arena + ".records." + i + ".wave", arenaRecords.get(i).getWave());
            plugin.getArenaData().set("a" + arena + ".records." + i + ".players", arenaRecords.get(i).getPlayers());
        }
        plugin.saveArenaData();
        return true;
    }

    public List<ArenaRecord> getSortedDescendingRecords() {
        return arenaRecords.stream().sorted(Comparator.comparingInt(ArenaRecord::getWave).reversed())
                .collect(Collectors.toList());
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

    public double getCurrentDifficulty() {
        double difficulty = Math.pow(Math.E, Math.pow(currentWave - 1, .6) / (5 - difficultyMultiplier / 2d));
        if (dynamicDifficulty)
            difficulty *= .1 * getActiveCount() + .6;
        return difficulty;
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

    public Inventory getWeaponShop() {
        return weaponShop;
    }

    public void setWeaponShop(Inventory weaponShop) {
        this.weaponShop = weaponShop;
    }

    public Inventory getArmorShop() {
        return armorShop;
    }

    public void setArmorShop(Inventory armorShop) {
        this.armorShop = armorShop;
    }

    public Inventory getConsumeShop() {
        return consumeShop;
    }

    public void setConsumeShop(Inventory consumeShop) {
        this.consumeShop = consumeShop;
    }

    public Inventory getCustomShop() {
        return customShop;
    }

    public void setCustomShop(Inventory customShop) {
        this.customShop = customShop;
    }

    public BossBar getTimeLimitBar() {
        return timeLimitBar;
    }

    public void startTimeLimitBar() {
        timeLimitBar = Bukkit.createBossBar(Utils.format("&eWave " + getCurrentWave() + " Time Limit"),
                BarColor.YELLOW, BarStyle.SOLID);
    }

    public void updateTimeLimitBar(double progress) {
        timeLimitBar.setProgress(progress);
    }

    public void updateTimeLimitBar(BarColor color, double progress) {
        timeLimitBar.setColor(color);
        timeLimitBar.setProgress(progress);
    }

    public void removeTimeLimitBar() {
        players.forEach(vdPlayer -> timeLimitBar.removePlayer(vdPlayer.getPlayer()));
        timeLimitBar = null;
    }

    public void addPlayerToTimeLimitBar(Player player) {
        timeLimitBar.addPlayer(player);
    }

    public void removePlayerFromTimeLimitBar(Player player) {
        timeLimitBar.removePlayer(player);
    }

    public void updateArena() {
        name = plugin.getArenaData().getString("a" + arena + ".name");
        maxPlayers = plugin.getArenaData().getInt("a" + arena + ".max");
        minPlayers = plugin.getArenaData().getInt("a" + arena + ".min");
        maxWaves = plugin.getArenaData().getInt("a" + arena + ".maxWaves");
        waveTimeLimit = plugin.getArenaData().getInt("a" + arena + ".waveTimeLimit");
        difficultyMultiplier = plugin.getArenaData().getInt("a" + arena + ".difficulty");
        playerSpawn = utils.getConfigLocationNoRotation("a" + arena + ".spawn");
        waitingRoom = utils.getConfigLocationNoRotation("a" + arena + ".waiting");
        monsterSpawns = utils.getConfigLocationList("a" + arena + ".monster").stream()
                .filter(Objects::nonNull).collect(Collectors.toList());
        villagerSpawns = utils.getConfigLocationList("a" + arena + ".villager").stream()
                .filter(Objects::nonNull).collect(Collectors.toList());
        spawnTableFile = plugin.getArenaData().getString("a" + arena + ".spawnTable");
        dynamicCount = plugin.getArenaData().getBoolean("a" + arena + ".dynamicCount");
        dynamicDifficulty = plugin.getArenaData().getBoolean("a" + arena + ".dynamicDifficulty");
        dynamicPrices = plugin.getArenaData().getBoolean("a" + arena + ".dynamicPrices");
        dynamicLimit = plugin.getArenaData().getBoolean("a" + arena + ".dynamicLimit");
        closed = plugin.getArenaData().getBoolean("a" + arena + ".closed");
        if (plugin.getArenaData().contains("a" + arena + ".records"))
            plugin.getArenaData().getConfigurationSection("a" + arena + ".records").getKeys(false)
                    .forEach(index -> arenaRecords.add(new ArenaRecord(
                            plugin.getArenaData().getInt("a" + arena + ".records." + index + ".wave"),
                            plugin.getArenaData().getStringList("a" + arena + ".records." + index + ".players")
                    )));
    }
}
