package me.theguyhere.villagerdefense.game.models;

import me.theguyhere.villagerdefense.GUI.InventoryItems;
import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class Arena {
    private final Main plugin;
    private final Utils utils;

    // Persistent data
    private final int arena; // Arena number
    private String name; // Name of the arena
    private String spawnTableFile; // File name of the spawn table
    private String difficultyLabel; // Labeled difficulty of the arena
    private int maxPlayers; // Maximum players in an arena
    private int minPlayers; // Minimum players in an arena
    private int maxWaves; // Maximum waves in an arena
    private int waveTimeLimit; // Base wave time limit
    private int difficultyMultiplier;
    private int waitingSound; // Selected waiting music
    private Location playerSpawn; // Location of player spawn
    private Location waitingRoom; // Location of waiting room
    private List<Location> monsterSpawns = new ArrayList<>(); // List of monster spawn locations
    private List<Location> villagerSpawns = new ArrayList<>(); // List of villager spawn locations
    private List<String> bannedKits = new ArrayList<>(); // LIst of kits that aren't allowed in the arena
    private boolean normal; // Toggle for normal shop
    private boolean custom; // Toggle for custom shop
    private boolean winSound; // Toggle for win sound
    private boolean loseSound; // Toggle for lose sound
    private boolean waveStartSound; // Toggle for wave start sound
    private boolean waveFinishSound; // Toggle for wave finish sound
    private boolean gemSound; // Toggle for gem pickup sound
    private boolean playerDeathSound; // Toggle for player death sound
    private boolean dynamicCount; // Toggle for dynamic mob count
    private boolean dynamicDifficulty; // Toggle for dynamic difficulty
    private boolean dynamicPrices; // Toggle for dynamic prices
    private boolean dynamicLimit; // Toggle for dynamic wave time limit
    private boolean closed; // Indicates whether the arena is closed
    private List<ArenaRecord> arenaRecords = new ArrayList<>(); // List of top arena records

    // Temporary data
    private final Tasks task; // The tasks object for the arena
    private boolean caps; // Indicates whether the naming inventory has caps lock on
    private boolean active; // Indicates whether the arena has a game ongoing
    private boolean spawning; // Indicates whether the arena is in the process of spawning mobs
    private boolean ending; // Indicates whether the arena is about to end
    private int currentWave; // Current game wave
    private int villagers; // Villager count
    private int enemies; // Enemy count
    private int golems; // Iron golem count
    private int spawnID = 0; // Spawn particles ID
    private int monsterID = 0; // Monster particles ID
    private int villagerID = 0; // Villager particles ID
    private final List<VDPlayer> players = new ArrayList<>(); // Tracks players playing and their other related stats
    private Inventory weaponShop; // Weapon shop inventory
    private Inventory armorShop; // Armor shop inventory
    private Inventory consumeShop; // Consumables shop inventory
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

    public String getDifficultyLabel() {
        return difficultyLabel;
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

    public Sound getWaitingSound() {
        switch (waitingSound) {
            case 0:
                return Sound.MUSIC_DISC_CAT;
            case 1:
                return Sound.MUSIC_DISC_BLOCKS;
            case 2:
                return Sound.MUSIC_DISC_FAR;
            case 3:
                return Sound.MUSIC_DISC_STRAD;
            case 4:
                return Sound.MUSIC_DISC_MELLOHI;
            case 5:
                return Sound.MUSIC_DISC_WARD;
            case 9:
                return Sound.MUSIC_DISC_CHIRP;
            case 10:
                return Sound.MUSIC_DISC_STAL;
            case 11:
                return Sound.MUSIC_DISC_MALL;
            case 12:
                return Sound.MUSIC_DISC_WAIT;
            case 13:
                return Sound.MUSIC_DISC_PIGSTEP;
            default:
                return null;
        }
    }

    public String getWaitingSoundName() {
        switch (waitingSound) {
            case 0:
                return "Cat";
            case 1:
                return "Blocks";
            case 2:
                return "Far";
            case 3:
                return "Strad";
            case 4:
                return "Mellohi";
            case 5:
                return "Ward";
            case 9:
                return "Chirp";
            case 10:
                return "Stal";
            case 11:
                return "Mall";
            case 12:
                return "Wait";
            case 13:
                return "Pigstep";
            default:
                return "None";
        }
    }

    public void setWaitingSound(int sound) {
        plugin.getArenaData().set("a" + arena + ".sounds.waiting", sound);
        plugin.saveArenaData();
        waitingSound = sound;
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

    public List<String> getBannedKits() {
        return bannedKits;
    }

    public String getSpawnTableFile() {
        return spawnTableFile;
    }

    public boolean isSpawnParticles() {
        return plugin.getArenaData().getBoolean("a" + arena + ".particles.spawn");
    }

    public void flipSpawnParticles() {
        plugin.getArenaData().set("a" + arena + ".particles.spawn", !isSpawnParticles());
        plugin.saveArenaData();
    }

    public void startSpawnParticles() {
        if (spawnID == 0)
            spawnID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                double var = 0;
                double var2 = 0;
                Location first, second;

                @Override
                public void run() {
                    // Update particle locations
                    var += Math.PI / 12;
                    var2 -= Math.PI / 12;
                    first = playerSpawn.clone().add(Math.cos(var), Math.sin(var) + 1, Math.sin(var));
                    second = playerSpawn.clone().add(Math.cos(var2 + Math.PI), Math.sin(var2) + 1,
                            Math.sin(var2 + Math.PI));

                    // Spawn particles
                    playerSpawn.getWorld().spawnParticle(Particle.FLAME, first, 0);
                    playerSpawn.getWorld().spawnParticle(Particle.FLAME, second, 0);
                }
            }, 0 , 2);
    }

    public void cancelSpawnParticles() {
        if (spawnID != 0)
            Bukkit.getScheduler().cancelTask(spawnID);
        spawnID = 0;
    }

    public boolean isMonsterParticles() {
        return plugin.getArenaData().getBoolean("a" + arena + ".particles.monster");
    }

    public void flipMonsterParticles() {
        plugin.getArenaData().set("a" + arena + ".particles.monster", !isMonsterParticles());
        plugin.saveArenaData();
    }

    public void startMonsterParticles() {
        if (monsterID == 0)
            monsterID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                double var = 0;
                Location first, second;

                @Override
                public void run() {
                    var -= Math.PI / 12;
                    monsterSpawns.forEach(location -> {
                        // Update particle locations
                        first = location.clone().add(Math.cos(var), Math.sin(var) + 1, Math.sin(var));
                        second = location.clone().add(Math.cos(var + Math.PI), Math.sin(var) + 1,
                                Math.sin(var + Math.PI));

                        // Spawn particles
                        location.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, first, 0);
                        location.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, second, 0);
                    });
                }
            }, 0 , 2);
    }

    public void cancelMonsterParticles() {
        if (monsterID != 0)
            Bukkit.getScheduler().cancelTask(monsterID);
        monsterID = 0;
    }

    public boolean isVillagerParticles() {
        return plugin.getArenaData().getBoolean("a" + arena + ".particles.villager");
    }

    public void flipVillagerParticles() {
        plugin.getArenaData().set("a" + arena + ".particles.villager", !isVillagerParticles());
        plugin.saveArenaData();
    }

    public void startVillagerParticles() {
        if (villagerID == 0)
            villagerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                double var = 0;
                Location first, second;

                @Override
                public void run() {
                    var += Math.PI / 12;
                    villagerSpawns.forEach(location -> {
                        // Update particle locations
                        first = location.clone().add(Math.cos(var), Math.sin(var) + 1, Math.sin(var));
                        second = location.clone().add(Math.cos(var + Math.PI), Math.sin(var) + 1,
                                Math.sin(var + Math.PI));

                        // Spawn particles
                        location.getWorld().spawnParticle(Particle.COMPOSTER, first, 0);
                        location.getWorld().spawnParticle(Particle.COMPOSTER, second, 0);
                    });
                }
            }, 0 , 2);
    }

    public void cancelVillagerParticles() {
        if (villagerID != 0)
            Bukkit.getScheduler().cancelTask(villagerID);
        villagerID = 0;
    }

    public boolean isNormal() {
        return normal;
    }

    public void flipNormal() {
        plugin.getArenaData().set("a" + arena + ".normal", !normal);
        plugin.saveArenaData();
        normal = !normal;
    }

    public boolean isCustom() {
        return custom;
    }

    public void flipCustom() {
        plugin.getArenaData().set("a" + arena + ".custom", !custom);
        plugin.saveArenaData();
        custom = !custom;
    }

    public boolean isWinSound() {
        return winSound;
    }

    public void flipWinSound() {
        plugin.getArenaData().set("a" + arena + ".sounds.win", !winSound);
        plugin.saveArenaData();
        winSound = !winSound;
    }

    public boolean isLoseSound() {
        return loseSound;
    }

    public void flipLoseSound() {
        plugin.getArenaData().set("a" + arena + ".sounds.lose", !loseSound);
        plugin.saveArenaData();
        loseSound = !loseSound;
    }

    public boolean isWaveStartSound() {
        return waveStartSound;
    }

    public void flipWaveStartSound() {
        plugin.getArenaData().set("a" + arena + ".sounds.start", !waveStartSound);
        plugin.saveArenaData();
        waveStartSound = !waveStartSound;
    }

    public boolean isWaveFinishSound() {
        return waveFinishSound;
    }

    public void flipWaveFinishSound() {
        plugin.getArenaData().set("a" + arena + ".sounds.end", !waveFinishSound);
        plugin.saveArenaData();
        waveFinishSound = !waveFinishSound;
    }

    public boolean isGemSound() {
        return gemSound;
    }

    public void flipGemSound() {
        plugin.getArenaData().set("a" + arena + ".sounds.gem", !gemSound);
        plugin.saveArenaData();
        gemSound = !gemSound;
    }

    public boolean isPlayerDeathSound() {
        return playerDeathSound;
    }

    public void flipPlayerDeathSound() {
        plugin.getArenaData().set("a" + arena + ".sounds.death", !playerDeathSound);
        plugin.saveArenaData();
        playerDeathSound = !playerDeathSound;
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

    public boolean isSpawning() {
        return spawning;
    }

    public void setSpawning(boolean spawning) {
        this.spawning = spawning;
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

    public int getGolems() {
        return golems;
    }

    public void incrementGolems() {
        golems++;
    }

    public void decrementGolems() {
        golems--;
    }

    public void resetGolems() {
        golems = 0;
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

    public Inventory getCustomShopEditor() {
        // Create inventory
        Inventory inv = Bukkit.createInventory(null, 54, Utils.format("&k") +
                Utils.format("&6&lCustom Shop Editor: " + name));

        // Set exit option
        inv.setItem(53, InventoryItems.exit());

        // Check for a stored inventory
        if (!plugin.getArenaData().contains("a" + arena + ".customShop"))
            return inv;

        // Get items from stored inventory
        plugin.getArenaData().getConfigurationSection("a" + arena + ".customShop").getKeys(false)
            .forEach(index -> inv.setItem(Integer.parseInt(index),
                    plugin.getArenaData().getItemStack("a" + arena + ".customShop." + index)));

        return inv;
    }

    public Inventory getCustomShop() {
        // Create inventory
        Inventory inv = Bukkit.createInventory(null, 54, Utils.format("&k") +
                Utils.format("&6&lCustom Shop"));

        // Set exit option
        inv.setItem(53, InventoryItems.exit());

        // Check for a stored inventory
        if (!plugin.getArenaData().contains("a" + arena + ".customShop"))
            return inv;

        // Get items from stored inventory
        plugin.getArenaData().getConfigurationSection("a" + arena + ".customShop").getKeys(false)
                .forEach(index -> {
                    // Get raw item and data
                    ItemStack item = plugin.getArenaData().getItemStack("a" + arena + ".customShop." + index).clone();
                    ItemMeta meta = item.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    String name = meta.getDisplayName().substring(0, meta.getDisplayName().length() - 5);
                    int price = Integer.parseInt(meta.getDisplayName().substring(meta.getDisplayName().length() - 5));

                    // Transform to proper shop item
                    meta.setDisplayName(Utils.format("&f" + name));
                    if (meta.hasLore()) {
                        lore = meta.getLore();
                        lore.add(Utils.format("&2Gems: &a" + price));
                    } else lore.add(Utils.format("&2Gems: &a" + price));
                    meta.setLore(lore);
                    item.setItemMeta(meta);

                    // Set item into inventory
                    inv.setItem(Integer.parseInt(index), item);
                });

        return inv;
    }

    public Inventory getMockCustomShop() {
        // Create inventory
        Inventory inv = Bukkit.createInventory(null, 54, Utils.format("&k") +
                Utils.format("&6&lCustom Shop: " + name));

        // Set exit option
        inv.setItem(53, InventoryItems.exit());

        // Check for a stored inventory
        if (!plugin.getArenaData().contains("a" + arena + ".customShop"))
            return inv;

        // Get items from stored inventory
        plugin.getArenaData().getConfigurationSection("a" + arena + ".customShop").getKeys(false)
                .forEach(index -> {
                    // Get raw item and data
                    ItemStack item = plugin.getArenaData().getItemStack("a" + arena + ".customShop." + index).clone();
                    ItemMeta meta = item.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    String name = meta.getDisplayName().substring(0, meta.getDisplayName().length() - 5);
                    int price = Integer.parseInt(meta.getDisplayName().substring(meta.getDisplayName().length() - 5));

                    // Transform to proper shop item
                    meta.setDisplayName(Utils.format("&f" + name));
                    if (meta.hasLore()) {
                        lore = meta.getLore();
                        lore.add(Utils.format("&2Gems: &a" + price));
                    } else lore.add(Utils.format("&2Gems: &a" + price));
                    meta.setLore(lore);
                    item.setItemMeta(meta);

                    // Set item into inventory
                    inv.setItem(Integer.parseInt(index), item);
                });

        return inv;
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
        FileConfiguration config = plugin.getArenaData();
        String path = "a" + arena;

        name = config.getString(path + ".name");
        if (config.contains(path + ".difficultyLabel"))
            difficultyLabel = config.getString(path + ".difficultyLabel");
        else difficultyLabel = null;
        maxPlayers = config.getInt(path + ".max");
        minPlayers = config.getInt(path + ".min");
        maxWaves = config.getInt(path + ".maxWaves");
        waveTimeLimit = config.getInt(path + ".waveTimeLimit");
        difficultyMultiplier = config.getInt(path + ".difficulty");
        waitingSound = config.getInt(path + ".sounds.waiting");
        playerSpawn = utils.getConfigLocationNoRotation(path + ".spawn");
        waitingRoom = utils.getConfigLocationNoRotation(path + ".waiting");
        monsterSpawns = utils.getConfigLocationList(path + ".monster").stream()
                .filter(Objects::nonNull).collect(Collectors.toList());
        villagerSpawns = utils.getConfigLocationList(path + ".villager").stream()
                .filter(Objects::nonNull).collect(Collectors.toList());
        bannedKits = config.getStringList(path + ".bannedKits");
        spawnTableFile = config.getString(path + ".spawnTable");
        normal = config.getBoolean(path + ".normal");
        custom = config.getBoolean(path + ".custom");
        winSound = config.getBoolean(path + ".sounds.win");
        loseSound = config.getBoolean(path + ".sounds.lose");
        waveStartSound = config.getBoolean(path + ".sounds.start");
        waveFinishSound = config.getBoolean(path + ".sounds.end");
        gemSound = config.getBoolean(path + ".sounds.gem");
        playerDeathSound = config.getBoolean(path + ".sounds.death");
        dynamicCount = config.getBoolean(path + ".dynamicCount");
        dynamicDifficulty = config.getBoolean(path + ".dynamicDifficulty");
        dynamicPrices = config.getBoolean(path + ".dynamicPrices");
        dynamicLimit = config.getBoolean(path + ".dynamicLimit");
        closed = config.getBoolean(path + ".closed");
        arenaRecords = new ArrayList<>();
        if (config.contains(path + ".records"))
            config.getConfigurationSection(path + ".records").getKeys(false)
                    .forEach(index -> arenaRecords.add(new ArenaRecord(
                            config.getInt(path + ".records." + index + ".wave"),
                            config.getStringList(path + ".records." + index + ".players")
                    )));
    }
}
